package com.micen.suppliers.view.purchase;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshListView;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.adapter.purchase.PurchaseProductAdapter;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.module.purchase.ProductContent;
import com.micen.suppliers.module.purchase.ProductFacet;
import com.micen.suppliers.module.purchase.ProductFilteItem;
import com.micen.suppliers.module.purchase.ProductFilter;
import com.micen.suppliers.module.purchase.ProductItem;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;


public class ChooseProductFromRoomFragment extends Fragment
{
	private int pageSize = 20;
	private int curPage;
	private boolean isLoadMore;

	private String keyword;

	private String group;
	private String master;
	private String productType;
	private String productStar;

	private String[] chooseName;
	private String[] choose;

	private View vLine;

	private ImageView ivBack;

	private ImageView ivFilter;
	private ImageView ivSearch;
	private ImageView ivClear;

	private ImageView ivTop;

	private EditText etText;

	private PullToRefreshListView listView;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	private PurchaseProductAdapter adapter;

	private ProductContent value;

	private List<ProductItem> initialList;
	// 记录是否是第一次加载数据
	private boolean isLoadFirst = true;
	// 记录是否允许用户退出当前界面
	private boolean isAllowBack = true;
	// 记录防止存在无相关产品/无网络情况下提供返回刷新数据
	private boolean isNeedRefresh = false;
	// 用户点击重新加载所触发的事件
	private boolean isLoadFirstNoNet = false;
	private Toast toast;

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@SuppressLint("NewApi")
		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			value = (ProductContent) obj;
			if ("0".equals(value.code) && null != value.content && null != value.content.product)
			{
				if (isLoadMore)
				{
					if (value.content.product.size() > 0)
					{
						curPage++;
						adapter.addData(value.content.product);
						adapter.notifyDataSetChanged();

						View c = listView.getRefreshableView().getChildAt(0);
						listView.getRefreshableView().scrollListBy(c.getHeight());
					}
					else
					{
						ToastUtil.toast(getActivity(), R.string.nomoredata);
					}
				}
				else
				{
					if (value.content.product.size() == 0)
					{
						showNoRelatedData();
						if (isLoadFirst)
						{
							isNeedRefresh = true;
							isAllowBack = true;
						}
						else
						{
							isAllowBack = false;
						}
						return;
					}
					else
					{
						// TODO 保存初始数据
						if (isLoadFirst)
						{
							initialList = new ArrayList<ProductItem>();
							initialList.addAll(value.content.product);
							isAllowBack = true;
						}
						else
						{
							isAllowBack = false;
						}

						if (null == adapter)
						{
							adapter = new PurchaseProductAdapter(getActivity(), value.content.product);
							listView.setAdapter(adapter);
						}
						else
						{
							adapter.setData(value.content.product);
							adapter.notifyDataSetChanged();
						}
					}
					showTip(value.content.productTotalNum);
				}
				showRelatedData();
			}
			else
			{
				showNoRelatedData();

			}
			if (isLoadMore)
			{
				isLoadMore = false;
			}
			refreshComplete();
			isLoadFirst = false;
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			// TODO Auto-generated method stub
			refreshComplete();
			if (!isLoadMore)
			{
				showNoRelatedData();
			}
			if (isLoadFirst)
			{
				isNeedRefresh = true;
				isAllowBack = true;
				isLoadFirst = false;
			}
			else
			{
				isAllowBack = false;
			}
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			refreshComplete();
			showNetworkError();
			if (isLoadFirst)
			{
				isNeedRefresh = true;
				isAllowBack = true;
				isLoadFirst = false;
				isLoadFirstNoNet = true;
			}
			else
			{
				isAllowBack = false;
			}
		}
	};

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub

			switch (v.getId())
			{
			case R.id.common_title_back:
				back();
				break;
			case R.id.purchase_search_searchImageView:
				startSearch();
				break;
			case R.id.purchase_search_clearImageView:
				keyword = "";
				etText.setText("");
				break;
			case R.id.purchase_chooseproduct_filterImageView:
				// 打开筛选页面
				startFilter();
				break;

			case R.id.iv_scroll_top:
				// listView.scrollTo(0, 0);
				listView.getRefreshableView().smoothScrollToPosition(0);
				break;
			default:
				break;
			}

		}

	};

	public void back()
	{
		Util.hideSoftKeyboard(getActivity());
		if (null != toast)
			toast.cancel();
		if (!isAllowBack)
		{
			etText.setText("");
			if (null != initialList && null != adapter)
			{
				showRelatedData();
				adapter.setData(initialList);
				adapter.notifyDataSetChanged();
			}
			else if (isNeedRefresh)
			{
				isLoadMore = false;
				isLoadFirst = true;
				curPage = 1;
				keyword = "";
				getData(curPage);
			}
			isAllowBack = true;
		}
		else
		{
			getActivity().finish();
		}
	}

	private OnEditorActionListener actionListenr = new OnEditorActionListener()
	{

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
		{
			// TODO Auto-generated method stub
			boolean handled = false;
			if (actionId == EditorInfo.IME_ACTION_SEARCH)
			{
				startSearch();
				handled = true;
			}
			else if (null != event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
				{
					startSearch();
					handled = true;
				}
			}
			return handled;

		}

	};

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub
			Intent data = new Intent();

			data.putExtra("id", adapter.getItem(position).productId);
			data.putExtra("name", adapter.getItem(position).productName);
			data.putExtra("img", adapter.getItem(position).image);
			data.putExtra("mode", adapter.getItem(position).prodModel);

			getActivity().setResult(1, data);
			getActivity().finish();
		}
	};

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub
			showwait();
			if (isLoadFirstNoNet && TextUtils.isEmpty(keyword))
			{
				isLoadFirst = true;
				isAllowBack = true;
			}
			if (isLoadMore)
			{
				getData(curPage + 1);
			}
			else
			{
				getData(curPage);
			}

		}

	};

	private OnScrollListener OnScrollListener = new OnScrollListener()
	{

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			// TODO Auto-generated method stub
			if (firstVisibleItem > 0)
			{
				ivTop.setVisibility(View.VISIBLE);
			}
			else
			{
				ivTop.setVisibility(View.GONE);
			}

		}
	};

	private TextWatcher textWatch = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
			if (Utils.isEmpty(etText.getText().toString()))
			{
				ivClear.setVisibility(View.INVISIBLE);
			}
			else
			{
				ivClear.setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		curPage = 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_choose_product, container, false);

		initView(view);
		showwait();
		getData(curPage);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (null != data)
		{
			chooseName = data.getStringArrayExtra("choosename");
			choose = data.getStringArrayExtra("choose");

			if (null != choose && choose.length == 4)
			{
				group = choose[0];
				productStar = choose[1];
				master = choose[2];
				productType = choose[3];

				curPage = 1;
				// 开始搜索
				getData(curPage);
			}
		}
	}

	public static ChooseProductFromRoomFragment newInstance()
	{
		ChooseProductFromRoomFragment fragment = new ChooseProductFromRoomFragment();

		return fragment;
	}

	private void initView(View view)
	{
		etText = (EditText) view.findViewById(R.id.purchase_chooseproduct_contentEditText);
		etText.setOnEditorActionListener(actionListenr);
		etText.addTextChangedListener(textWatch);

		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		ivBack.setOnClickListener(clickListener);

		ivSearch = (ImageView) view.findViewById(R.id.purchase_search_searchImageView);
		// ivSearch.setOnClickListener(clickListener);

		ivClear = (ImageView) view.findViewById(R.id.purchase_search_clearImageView);
		ivClear.setOnClickListener(clickListener);

		listView = (PullToRefreshListView) view.findViewById(R.id.purchase_chooseproduct_productListView);
		listView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
		listView.setOnItemClickListener(itemClickListener);
		listView.setOnScrollListener(OnScrollListener);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(initListRefreshListener());
		listView.setOnPullEventListener(pullEventListener);

		ivFilter = (ImageView) view.findViewById(R.id.purchase_chooseproduct_filterImageView);
		ivFilter.setOnClickListener(clickListener);

		ivTop = (ImageView) view.findViewById(R.id.iv_scroll_top);
		ivTop.setOnClickListener(clickListener);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);
	}

	private OnPullEventListener<ListView> pullEventListener = new OnPullEventListener<ListView>()
	{
		@Override
		public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction)
		{
			switch (direction)
			{
			case PULL_FROM_START:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
				break;
			default:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
				break;
			}
		}
	};

	private boolean isExistFacet(ProductContent tmp)
	{
		return (tmp != null && tmp.content != null && tmp.content.facet != null);
	}

	private List<ProductFilteItem> getFilterList(List<? extends ProductFacet> l)
	{
		List<ProductFilteItem> list = new ArrayList<ProductFilteItem>();
		for (ProductFacet item : l)
		{
			list.add(new ProductFilteItem(item.getName(), item.getValue()));
		}
		return list;
	}

	private void startSearch()
	{

		keyword = etText.getText().toString().trim();

		if (Utils.isEmpty(keyword))
		{
			Toast.makeText(getActivity(), "关键词为空，请重新输入", Toast.LENGTH_SHORT).show();
			etText.setText("");
		}
		else
		{
			Util.hideSoftKeyboard(getActivity());
			curPage = 1;
			getData(curPage);
		}

	}

	private void getData(int targetPage)
	{
		if (!isLoadMore)
		{
			showwait();
		}
		RequestCenter.getProductList(keyword, group, master, productType, productStar, targetPage, pageSize,
				dataListener);
	}

	private void startFilter()
	{
		if (isExistFacet(value))
		{

			ArrayList<ProductFilter> list = new ArrayList<ProductFilter>();
			ProductFilter tmp = new ProductFilter();
			tmp.setName(getString(R.string.productgroup));
			tmp.setList(getFilterList(value.content.facet.group));
			list.add(tmp);

			tmp = new ProductFilter();
			tmp.setName(getString(R.string.productstars));
			tmp.setList(getFilterList(value.content.facet.productStar));
			list.add(tmp);

			tmp = new ProductFilter();
			tmp.setName(getString(R.string.productmaster));
			tmp.setList(getFilterList(value.content.facet.master));
			list.add(tmp);

			tmp = new ProductFilter();
			tmp.setName(getString(R.string.producttype));
			tmp.setList(getFilterList(value.content.facet.productType));
			list.add(tmp);

			Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
			intent.putExtra("fragment", "chooseproductfilter");
			intent.putParcelableArrayListExtra("data", list);
			intent.putExtra("name", chooseName);
			intent.putExtra("value", choose);

			startActivityForResult(intent, 1);
		}
	}

	// 事件回调
	private OnRefreshListener2<ListView> initListRefreshListener()
	{
		OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>()
		{
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
			{
				onPullDownToRefreshView(refreshView);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
			{
				onPullUpToRefreshView(refreshView);
			}
		};
		return refreshListener;
	}

	private void showTip(String count)
	{
		String num = "<font color=\"#ff3f26\">" + count + "</font>";
		String value = getString(R.string.totalitem, num);

		if (1 > Constants.density)
		{
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			Constants.density = dm.density;
		}

		TextView tvTip = null;
		View toastView = null;
		toastView = LayoutInflater.from(getActivity()).inflate(R.layout.toast_msg_count, null);
		tvTip = (TextView) toastView.findViewById(R.id.toast_msg_count_tipTextView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.getWindowWidthPix(getActivity()),
				Util.dip2px(40));
		// toastView.setLayoutParams(params);
		tvTip.setLayoutParams(params);

		vLine = (View) toastView.findViewById(R.id.toast_msg_line_View);
		LinearLayout.LayoutParams lineparams = new LinearLayout.LayoutParams(Utils.getWindowWidthPix(getActivity()),
				Util.dip2px(0.5f));
		vLine.setLayoutParams(lineparams);

		tvTip.setText(Html.fromHtml(value));

		toast = new Toast(getActivity());

		toast.setView(toastView);
		toast.setGravity(Gravity.LEFT | Gravity.TOP, 0, Util.dip2px(58f));
		toast.show();
	}

	/**
	 * 刷新完成
	 */
	protected void refreshComplete()
	{
		if (listView != null && listView.isRefreshing())
		{
			listView.onRefreshComplete();
		}
	}

	/**
	 * 下拉刷新
	 * @param refreshView
	 */
	protected void onPullDownToRefreshView(PullToRefreshBase<?> refreshView)
	{
		isLoadMore = false;
		curPage = 1;
		Util.saveChildLastRefreshTime(getActivity(), getFragmentTag());
		getData(curPage);
		// 执行动作
	}

	/**
	 * 上拉加载更多
	 * @param refreshView
	 */
	protected void onPullUpToRefreshView(PullToRefreshBase<?> refreshView)
	{
		isLoadMore = true;
		getData(curPage + 1);
		// 执行动作
	}

	private String getFragmentTag()
	{
		return ChooseProductFromRoomFragment.class.getName();
	}

	private void showwait()
	{
		listView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void showNoRelatedData()
	{
		listView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageEmpty);
		progressBar.setVisibility(View.GONE);
	}

	private void showRelatedData()
	{
		listView.setVisibility(View.VISIBLE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
	}

	private void showNetworkError()
	{
		listView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}
}
