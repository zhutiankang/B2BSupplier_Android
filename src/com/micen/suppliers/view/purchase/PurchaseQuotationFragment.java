package com.micen.suppliers.view.purchase;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshListView;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationDetailAdapter;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationDetailExpiredAdapter;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationDetailLockedAdapter;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationDetailNeedModifyAdapter;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationDetailPendingAdapter;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationDetailQuotedAdapter;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.QuotationDetail;
import com.micen.suppliers.module.purchase.QuotationDetailContent;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


public class PurchaseQuotationFragment extends Fragment
{
	private ImageView ivTop;
	private int curPage;
	private boolean isLoadMore;

	private String status;
	private int type;
	private List<QuotationDetail> quotation;

	private PullToRefreshListView listView;

	private PurchaseQuotationDetailAdapter adapter;

	private QuotationDetailContent value;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub

			value = (QuotationDetailContent) obj;

			if ("0".equals(value.code) && null != value.content)
			{
				if (isLoadMore)
				{
					isLoadMore = false;
					if (value.content.size() > 0)
					{
						curPage++;
						adapter.addData(value.content);
					}
					else
					{
						ToastUtil.toast(getActivity(), R.string.nomoredata);
					}
				}
				else
				{
					adapter.setData(value.content);
				}

				adapter.notifyDataSetChanged();
			}

			if (isLoadMore)
			{
				isLoadMore = false;
			}

			if (adapter.getCount() > 0)
			{
				showRelatedData();
			}
			else
			{
				showRelatedData();
			}

			refreshComplete();
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
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			refreshComplete();
			showNetworkError();
		}
	};

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub

			if (4 == type)
			{
				// 已经过期，跳转到 采购详情
				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);

				intent.putExtra("fragment", "rfqinfo");
				// intent.putExtra("fragment", "rfqneedquote");
				intent.putExtra("rfqid", adapter.getItem(position).rfqId);

				intent.putExtra("quoteSource", "0");
				intent.putExtra("reqFrom", "1");

				startActivity(intent);
			}
			else
			{
				// 跳转到 报价详情
				QuotationDetail tmp = adapter.getItem(position);

				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
				// quotationid
				if ("1".equals(tmp.isRecommended))
				{
					intent.putExtra("fragment", "recommenddetail");
				}
				else
				{
					intent.putExtra("fragment", "quotationdetail");
				}

				intent.putExtra("quotationid", tmp.quotationId);

				startActivity(intent);
			}
			MobclickAgent.onEvent(getActivity(), "108");
			SysManager.analysis(R.string.c_type_click, R.string.c108);
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
			case R.id.iv_scroll_top:
				// listView.scrollTo(0, 0);
				listView.getRefreshableView().smoothScrollToPosition(0);
				break;
			default:
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
				getData(curPage + 1, 20);
			}
			else
			{
				curPage = 1;
				getData(1, 20);
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

	public static PurchaseQuotationFragment newInstance(String type, ArrayList<QuotationDetail> l)
	{
		PurchaseQuotationFragment fragment = new PurchaseQuotationFragment();

		Bundle bundle = new Bundle();

		bundle.putString("status", type);
		bundle.putParcelableArrayList("quotation", l);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		curPage = 1;
		Bundle bundle = getArguments();

		if (null != bundle)
		{
			status = bundle.getString("status");
			quotation = bundle.getParcelableArrayList("quotation");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_quotation_page, container, false);
		initView(view);

		if (null != status && null != quotation)
		{
			initAdapter();
		}

		if (null != adapter)
		{
			listView.setAdapter(adapter);
		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
		{
			if (type == 2)
			{
				showwait();
				isLoadMore = false;
				curPage = 1;
				getData(1, 20);
			}
		}
	}

	private void initView(View view)
	{
		ivTop = (ImageView) view.findViewById(R.id.iv_scroll_top);
		ivTop.setOnClickListener(listener);

		listView = (PullToRefreshListView) view.findViewById(R.id.purchase_quotation_page_listListView);
		listView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getFragmentTag() + status, ""));
		// 只启动下拉加载更多
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(initListRefreshListener());
		listView.setOnScrollListener(OnScrollListener);
		listView.setOnItemClickListener(itemClickListener);
		listView.setOnPullEventListener(pullEventListener);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);
		progressBar.setVisibility(View.VISIBLE);
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
						SharedPreferenceManager.getInstance().getString(getFragmentTag() + status, ""));
				break;
			default:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
				break;
			}
		}
	};

	private void initAdapter()
	{
		try
		{
			type = Integer.parseInt(status);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			type = 0;
		}
		// 1：待审核 2：需要修改 3.已报价 4：已过期，5：已冻结
		switch (type)
		{
		case 1:
			adapter = new PurchaseQuotationDetailPendingAdapter(getActivity(), quotation);
			break;
		case 2:
			adapter = new PurchaseQuotationDetailNeedModifyAdapter(getActivity(), quotation);
			break;
		case 3:
			adapter = new PurchaseQuotationDetailQuotedAdapter(getActivity(), quotation);
			break;
		case 4:
			adapter = new PurchaseQuotationDetailExpiredAdapter(getActivity(), quotation);
			break;
		case 5:
			adapter = new PurchaseQuotationDetailLockedAdapter(getActivity(), quotation);
			break;

		default:
			break;
		}
	}

	private void getData(int page, int pagesize)
	{
		RequestCenter.getQuotations(status, page, pagesize, dataListener);
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
		Util.saveChildLastRefreshTime(getActivity(), getFragmentTag() + status);
		getData(1, 20);

	}

	/**
	 * 上拉加载更多
	 * @param refreshView
	 */
	protected void onPullUpToRefreshView(PullToRefreshBase<?> refreshView)
	{
		isLoadMore = true;
		getData(curPage + 1, 20);

	}

	private String getFragmentTag()
	{
		return PurchaseQuotationFragment.class.getName();
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

	private void showwait()
	{
		listView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void showNoRelatedData()
	{
		ivTop.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
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
		ivTop.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}
}
