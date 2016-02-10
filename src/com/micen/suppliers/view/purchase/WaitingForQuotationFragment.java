package com.micen.suppliers.view.purchase;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.micen.suppliers.adapter.purchase.PurchaseRfqNeedQuoteAdapter;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.RfqNeedQuoted;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


public class WaitingForQuotationFragment extends Fragment
{
	private int curPage = 1;
	private int pageSize = 20;
	private boolean isLoadMore;

	private boolean isBroadcast;
	private String messageID;
	private String from;

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;

	private PullToRefreshListView listView;
	private PurchaseRfqNeedQuoteAdapter adapter;

	private RfqNeedQuoted list;

	private ImageView ivTop;
	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			list = (RfqNeedQuoted) obj;
			if ("0".equals(list.code) && null != list.content)
			{
				if (isLoadMore)
				{
					isLoadMore = false;
					if (list.content.unQuoteRfq != null && list.content.unQuoteRfq.size() > 0)
					{
						curPage++;
						adapter.addData(list.content.unQuoteRfq);
					}
					else
					{
						ToastUtil.toast(getActivity(), R.string.nomoredata);
					}
				}
				else
				{
					if (list.content.unQuoteRfq != null && list.content.unQuoteRfq.size() > 0)
					{
						if (null == adapter)
						{
							adapter = new PurchaseRfqNeedQuoteAdapter(getActivity(), list.content.unQuoteRfq);
							listView.setAdapter(adapter);
						}
						else
						{
							adapter.setData(list.content.unQuoteRfq);
						}
					}
				}

			}

			if (isLoadMore)
			{
				isLoadMore = false;
			}

			if (null != adapter)
			{
				adapter.notifyDataSetChanged();
				if (adapter.getCount() > 0)
				{
					showRelatedData();
				}
				else
				{
					showNoRelatedData();
				}
			}
			else
			{
				showNoRelatedData();
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

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.common_ll_title_back:
				MobclickAgent.onEvent(getActivity(), "98");
				SysManager.analysis(R.string.c_type_click, R.string.c098);
				getActivity().finish();
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
			intent.putExtra("quotationid", adapter.getItem(position).quotationId);
			intent.putExtra("quoteSource", "0");
			intent.putExtra("isrecommend", "0");
			intent.putExtra("reqFrom", "2");

			// intent.putExtra("recommend", adapter.getItem(position).isRecommended);
			startActivity(intent);
		}
	};

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub

			if (PageStatus.PageEmptyLink == status)
			{
				if ("1".equals(from))
				{
					getActivity().finish();
				}
				else
				{
					// 跳转到采购频道首页
					Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
					startActivity(intent);
					getActivity().finish();
				}
			}
			else
			{
				showwait();

				if (isLoadMore)
				{
					initData(curPage + 1);
				}
				else
				{
					curPage = 1;
					pageSize = 20;
					initData(curPage);
				}

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

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isLoadMore = false;
		Bundle bundle = getArguments();
		if (bundle != null)
		{
			isBroadcast = bundle.getBoolean("isbroadcast");
			messageID = bundle.getString("messageid");
			from = bundle.getString("from");
		}

		if (isBroadcast && !Utils.isEmpty(messageID))
		{
			RequestCenter.updateMesssageStatus(messageID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_waiting_for_quotation, container, false);

		initView(view);
		showwait();
		initData(curPage);
		return view;
	}

	public static WaitingForQuotationFragment newInstance(boolean isBroadcast, String messageID, String from)
	{
		WaitingForQuotationFragment fragment = new WaitingForQuotationFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isbroadcast", isBroadcast);
		bundle.putString("messageid", messageID);
		bundle.putString("from", from);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void initView(View view)
	{
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		tvTitle = (TextView) view.findViewById(R.id.common_title_name);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_btn);

		tvTitle.setText(R.string.unoffer_purchase);

		ivTop = (ImageView) view.findViewById(R.id.iv_scroll_top);
		ivTop.setOnClickListener(clickListener);

		listView = (PullToRefreshListView) view.findViewById(R.id.purchase_waiting_for_quotation_ListView);
		listView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(initListRefreshListener());
		listView.setOnItemClickListener(itemClickListener);
		listView.setOnScrollListener(OnScrollListener);
		listView.setOnPullEventListener(pullEventListener);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);
	}

	private void initData(int indexPage)
	{
		RequestCenter.getRfqNeedQuote(indexPage, pageSize, dataListener);
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
		initData(curPage);
		// 执行动作
	}

	/**
	 * 上拉加载更多
	 * @param refreshView
	 */
	protected void onPullUpToRefreshView(PullToRefreshBase<?> refreshView)
	{
		isLoadMore = true;
		initData(curPage + 1);
		// 执行动作
	}

	private String getFragmentTag()
	{
		return WaitingForQuotationFragment.class.getName();
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
		listView.setVisibility(View.GONE);

		// pageStateView.
		pageStateView.setMode(PageStatus.PageEmptyLink);
		pageStateView.setVisibility(View.VISIBLE);

		progressBar.setVisibility(View.GONE);
	}

	private void showRelatedData()
	{
		pageStateView.setMode(PageStatus.PageEmpty);
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
