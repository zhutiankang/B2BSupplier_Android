package com.micen.suppliers.view.purchase;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.micen.suppliers.activity.purchase.PurchaseSearchFilterActivity;
import com.micen.suppliers.adapter.purchase.PurchaseSearchResultAdapter;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.SearchResultContent;
import com.micen.suppliers.module.purchase.SearchResultProperty;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


public class PurchaseSearchResultFragment extends Fragment
{
	private ImageView ivTop;

	private TextView keyworkTextView;
	private ImageView ivBack;
	private ImageView btnFilter;

	private View vLine;

	private PullToRefreshListView listview;
	private PurchaseSearchResultAdapter adapter;

	private String keywords;
	private String country;
	private String category;
	private String postDate;

	private String categoryname;

	private int curPage;
	private int pageSize;
	private boolean isLoadMore;

	// 平台返回的搜索结果
	SearchResultContent value;
	// 第一次搜索返回的筛选结果
	List<SearchResultProperty> facet;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		keywords = "";
		country = "";
		category = "";
		postDate = "";

		curPage = 1;
		pageSize = 20;
		Bundle bundle = getArguments();
		if (bundle != null)
		{
			keywords = bundle.getString("keyword", "");
			category = bundle.getString("category", "");
			categoryname = bundle.getString("categoryname", "");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_search_result, container, false);
		initView(view);
		isLoadMore = false;
		showwait();
		curPage = 1;
		getData(curPage);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && null != data)
		{
			category = data.getStringExtra("category");
			country = data.getStringExtra("location");
			postDate = data.getStringExtra("postdate");
			curPage = 1;
			isLoadMore = false;
			getData(curPage);
		}
	}

	public static PurchaseSearchResultFragment newInstance(String category, String keyword, String categoryname)
	{
		PurchaseSearchResultFragment fragment = new PurchaseSearchResultFragment();
		Bundle bundle = new Bundle();
		bundle.putString("keyword", keyword);
		bundle.putString("category", category);
		bundle.putString("categoryname", categoryname);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void getData(int indexPage)
	{
		RequestCenter.searchRfq(keywords, country, category, postDate, indexPage, pageSize, dataListener);
	}

	private void initView(View view)
	{

		ivTop = (ImageView) view.findViewById(R.id.iv_scroll_top);
		ivTop.setOnClickListener(listener);

		listview = (PullToRefreshListView) view.findViewById(R.id.purchase_search_result_listview);
		listview.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
		listview.setMode(Mode.BOTH);
		listview.setOnScrollListener(OnScrollListener);
		listview.setOnRefreshListener(initListRefreshListener());
		listview.setOnItemClickListener(itemClickListener);
		listview.setOnPullEventListener(pullEventListener);

		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		ivBack.setOnClickListener(listener);

		btnFilter = (ImageView) view.findViewById(R.id.fragment_search_result_filterButton);
		btnFilter.setOnClickListener(listener);

		keyworkTextView = (TextView) view.findViewById(R.id.purchase_search_result_contentTextView);
		keyworkTextView.setOnClickListener(listener);

		if (!Utils.isEmpty(categoryname))
		{
			keyworkTextView.setText(categoryname);
		}
		else
		{
			keyworkTextView.setText(keywords);
		}

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);
		progressBar.setVisibility(View.VISIBLE);

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

		Toast toast = new Toast(getActivity());

		toast.setView(toastView);
		toast.setGravity(Gravity.LEFT | Gravity.TOP, 0, Util.dip2px(58f));
		toast.show();
	}

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			value = (SearchResultContent) obj;
			if ("0".equals(value.code) && null != value.content && null != value.content.rfqs)
			{
				if (isLoadMore)
				{
					if (value.content.rfqs.size() > 0)
					{
						curPage++;
						adapter.addData(value.content.rfqs);
					}
					else
					{
						ToastUtil.toast(getActivity(), R.string.nomoredata);
					}
					showRelatedData();
				}
				else
				{
					if (value.content.rfqs.size() > 0)
					{
						if (null == adapter)
						{
							adapter = new PurchaseSearchResultAdapter(getActivity(), value.content.rfqs);
							listview.setAdapter(adapter);
						}
						else
						{
							adapter.setData(value.content.rfqs);
						}

						if (null == facet)
						{
							facet = value.content.facet;
						}
						showRelatedData();
					}
					else
					{
						showNoRelatedData();
					}
					showTip(value.content.totalCount);
				}

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
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			// TODO Auto-generated method stub
			if (!isLoadMore)
			{
				showNoRelatedData();
			}
			refreshComplete();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			showNetworkError();
			refreshComplete();
		}
	};

	private OnClickListener listener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub

			switch (v.getId())
			{
			case R.id.common_title_back:
				MobclickAgent.onEvent(getActivity(), "77");
				SysManager.analysis(R.string.c_type_click, R.string.c077);
				getActivity().finish();
				break;
			case R.id.fragment_search_result_filterButton:
				// 筛选
				startFilter();
				MobclickAgent.onEvent(getActivity(), "80");
				SysManager.analysis(R.string.c_type_click, R.string.c080);
				break;
			case R.id.purchase_search_result_contentTextView:
				Intent intent = new Intent();
				intent.putExtra("isearch", true);
				getActivity().setResult(1, intent);
				getActivity().finish();
				break;
			case R.id.iv_scroll_top:
				// listView.scrollTo(0, 0);
				listview.getRefreshableView().smoothScrollToPosition(0);
				break;
			default:
				break;
			}

		}
	};

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

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub
			showwait();

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

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub
			// adapter.getItem(position).rfqId
			Intent intent = new Intent(getActivity(), PurchaseActivity_.class);

			intent.putExtra("fragment", "rfqinfo");
			// intent.putExtra("fragment", "rfqneedquote");
			intent.putExtra("rfqid", adapter.getItem(position).rfqId);
			intent.putExtra("isrecommend", adapter.getItem(position).isRecommended);
			intent.putExtra("quoteSource", "1");
			// intent.putExtra("recommend", adapter.getItem(position).isRecommended);
			startActivity(intent);
		}
	};

	private void startFilter()
	{
		if (null != value && null != facet)
		{
			Intent intent = new Intent(getActivity(), PurchaseSearchFilterActivity.class);

			intent.putExtra("type", 1);

			for (int i = 0; i < facet.size(); i++)
			{
				if (facet.get(i).name.startsWith("catalog"))
				{
					intent.putParcelableArrayListExtra("categorylist", facet.get(i).options);
					// intent.putStringArrayListExtra("categorylist", value.content.facet.get(i).options);
				}

				if (facet.get(i).name.startsWith("country"))
				{
					intent.putParcelableArrayListExtra("locationlist", facet.get(i).options);
				}

				if (facet.get(i).name.startsWith("post_date"))
				{
					intent.putParcelableArrayListExtra("postdatelist", facet.get(i).options);
				}
			}
			// keywords, country, category, postDate,
			intent.putExtra("keyword", keywords);
			intent.putExtra("country", country);
			intent.putExtra("category", category);
			intent.putExtra("postDate", postDate);
			intent.putExtra("categoryname", categoryname);
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

	/**
	 * 下拉刷新
	 * @param refreshView
	 */
	protected void onPullDownToRefreshView(PullToRefreshBase<?> refreshView)
	{
		isLoadMore = false;
		curPage = 1;
		Util.saveChildLastRefreshTime(getActivity(), getFragmentTag());
		// 执行动作
		getData(curPage);

	}

	/**
	 * 上拉加载更多
	 * @param refreshView
	 */
	protected void onPullUpToRefreshView(PullToRefreshBase<?> refreshView)
	{
		isLoadMore = true;
		// 执行动作
		getData(curPage + 1);

	}

	private String getFragmentTag()
	{
		return PurchaseSearchResultFragment.class.getName();
	}

	/**
	 * 刷新完成
	 */
	protected void refreshComplete()
	{
		if (listview != null && listview.isRefreshing())
		{
			listview.onRefreshComplete();
		}
	}

	private void showwait()
	{
		listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void showNoRelatedData()
	{
		ivTop.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}

	private void showRelatedData()
	{
		listview.setVisibility(View.VISIBLE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
	}

	private void showNetworkError()
	{
		ivTop.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10013));
		SysManager.analysis(R.string.p_type_page, R.string.p10013);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10013));
	}

}
