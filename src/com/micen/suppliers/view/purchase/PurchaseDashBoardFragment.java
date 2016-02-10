package com.micen.suppliers.view.purchase;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.micen.suppliers.activity.purchase.PurchaseQuotationManagementActivity;
import com.micen.suppliers.adapter.purchase.PurchaseInfoAdapter;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.PurchaseContent;
import com.micen.suppliers.module.purchase.PurchaseInfo;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


public class PurchaseDashBoardFragment extends Fragment
{
	private LinearLayout llBack;
	private ImageView ivBack;
	private TextView tvTitle;

	private ImageView ivTop;

	private ImageView ivSearch;

	private PullToRefreshListView listView;

	private ArrayList<PurchaseInfo> urgentList;
	private ArrayList<PurchaseInfo> latestList;
	private PurchaseInfoAdapter adapter;

	// 原来是两个按钮，现合并成一个
	private TextView tvNewOrUrgent;
	// private TextView tvRecommend;
	private TextView tvunReadNum;

	private View vDivider;

	private ImageView ivNew;
	// private ImageView ivRecommend;

	private RelativeLayout rlWait;
	private RelativeLayout rlManagement;

	private int curStatus;
	private int curLatestPage;
	private int curUrgentPage;
	private boolean isLoadMore;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		curLatestPage = 1;
		curUrgentPage = 1;
		curStatus = 1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_dashboard, container, false);
		View headView = inflater.inflate(R.layout.list_headerview_purchase, null, false);
		initView(view, headView);
		getData(curUrgentPage, 20);
		return view;
	}

	public static PurchaseDashBoardFragment newInstance()
	{
		PurchaseDashBoardFragment fragment = new PurchaseDashBoardFragment();
		return fragment;
	}

	/**
	 * 
	 * @param page
	 * @param pageSize
	 * @param type rfq类型，1：最紧急 2：最新
	 */
	private void getData(int page, int pageSize)
	{
		if (page == 1)
		{
			showwait();
		}

		RequestCenter.getRfqList(curStatus, page, pageSize, dataListener);
	}

	private OnItemClickListener itemClick = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub
			// 因为添加了一个 headerview
			if (position > 0)
			{
				position = position - 1;
			}
			Intent intent = new Intent(getActivity(), PurchaseActivity_.class);

			intent.putExtra("fragment", "rfqinfo");
			// intent.putExtra("fragment", "rfqneedquote");
			intent.putExtra("rfqid", adapter.getItem(position).rfqId);
			intent.putExtra("isrecommend", adapter.getItem(position).isRecommended);
			intent.putExtra("quoteSource", "1");
			startActivity(intent);
			if ("1".equals(adapter.getItem(position).isRecommended))
			{
				MobclickAgent.onEvent(getActivity(), "69");
				SysManager.analysis(R.string.c_type_click, R.string.c069);
			}
			else
			{
				MobclickAgent.onEvent(getActivity(), "70");
				SysManager.analysis(R.string.c_type_click, R.string.c070);
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

	private View.OnClickListener clickListener = new View.OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			// 切换状态

			switch (v.getId())
			{
			case R.id.purchase_dashboard_newTextView:
				if (1 == curStatus)
				{
					isLoadMore = false;
					curStatus = 2;
					curLatestPage = 1;
					// changeTextViewStyle(1);
					tvNewOrUrgent.setText(R.string.latest);
					ivTop.setVisibility(View.GONE);
					// adapter.setData(latestList, 1);

					getData(1, 20);
				}
				else
				{
					MobclickAgent.onEvent(getActivity(), "68");
					SysManager.analysis(R.string.c_type_click, R.string.c068);

					isLoadMore = false;
					curStatus = 1;
					curLatestPage = 1;
					// changeTextViewStyle(0);
					tvNewOrUrgent.setText(R.string.urgent);
					ivTop.setVisibility(View.GONE);
					// adapter.setData(urgentList, 0);

					getData(1, 20);
				}
				break;
			case R.id.purchase_dashboard_managementRelativeLayout:
				MobclickAgent.onEvent(getActivity(), "67");
				SysManager.analysis(R.string.c_type_click, R.string.c067);
				Intent managerIntent = new Intent(getActivity(), PurchaseQuotationManagementActivity.class);
				startActivity(managerIntent);
				break;
			case R.id.purchase_dashboard_waitRelativeLayout:
				MobclickAgent.onEvent(getActivity(), "66");
				SysManager.analysis(R.string.c_type_click, R.string.c066);
				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
				intent.putExtra("fragment", "rfqneedquote");
				intent.putExtra("from", "1");
				startActivity(intent);
				break;
			case R.id.common_ll_title_back:
				getActivity().finish();
				break;
			case R.id.common_title_function5:
				MobclickAgent.onEvent(getActivity(), "65");
				SysManager.analysis(R.string.c_type_click, R.string.c065);
				// 进入搜索
				Intent searchintent = new Intent(getActivity(), PurchaseActivity_.class);
				searchintent.putExtra("fragment", "search");
				startActivity(searchintent);
				break;

			case R.id.iv_scroll_top:
				// listView.scrollTo(0, 0);
				MobclickAgent.onEvent(getActivity(), "71");
				SysManager.analysis(R.string.c_type_click, R.string.c071);
				listView.getRefreshableView().smoothScrollToPosition(0);
				break;
			default:
				break;
			}

			adapter.notifyDataSetChanged();
		}
	};

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{

			// TODO Auto-generated method stub
			PurchaseContent purchaseContent = (PurchaseContent) obj;

			if ("0".equals(purchaseContent.code))
			{
				if (purchaseContent.content != null && purchaseContent.content.rfq != null)
				{
					if (purchaseContent.content.rfq.size() > 0)
					{
						if (1 == curStatus)
						{
							if (isLoadMore)
							{
								curLatestPage++;
								urgentList.addAll(purchaseContent.content.rfq);
							}
							else
							{
								urgentList.clear();
								urgentList.addAll(purchaseContent.content.rfq);

							}
							adapter.setData(urgentList, 0);
							adapter.notifyDataSetChanged();
							if (isLoadMore)
							{
								// listView.getRefreshableView().smoothScrollToPosition(
								// listView.getRefreshableView().getLastVisiblePosition() + 1);
								// listView.getRefreshableView().scrollListBy(200);
							}
						}
						else
						{
							if (isLoadMore)
							{
								curUrgentPage++;
								latestList.addAll(purchaseContent.content.rfq);
							}
							else
							{
								latestList.clear();
								latestList.addAll(purchaseContent.content.rfq);
							}
							adapter.setData(latestList, 1);
							adapter.notifyDataSetChanged();
							if (isLoadMore)
							{
								// listView.getRefreshableView().smoothScrollToPosition(
								// listView.getRefreshableView().getLastVisiblePosition() + 1);
								// listView.getRefreshableView().scrollListBy(200);
							}
						}
						showRelatedData();

						// pageStateView.setVisibility(View.VISIBLE);
						// pageStateView.setMode(PageStatus.PageNetwork);
					}
					else
					{
						if (!isLoadMore)
						{
							showNoRelatedData();
						}
						else
						{
							ToastUtil.toast(getActivity(), R.string.nomoredata);
						}
					}

					if (Utils.isEmpty(purchaseContent.content.unreadRFQ)
							|| "0".equals(purchaseContent.content.hasPower))
					{
						tvunReadNum.setVisibility(View.GONE);
					}
					else
					{
						if ("0".equals(purchaseContent.content.unreadRFQ))
						{
							tvunReadNum.setVisibility(View.GONE);
						}
						else
						{
							tvunReadNum.setText(purchaseContent.content.unreadRFQ);
							tvunReadNum.setVisibility(View.VISIBLE);
						}
					}

					if (!isLoadMore)
					{
						listView.getRefreshableView().setSelection(0);
					}
					else
					{
						isLoadMore = false;
					}
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

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub

			if (isLoadMore)
			{
				if (curStatus == 1)
				{
					getData(curLatestPage + 1, 20);
				}
				else
				{
					getData(curUrgentPage + 1, 20);
				}
			}
			else
			{
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

	private void initView(View view, View headerView)
	{
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		tvTitle = (TextView) view.findViewById(R.id.common_title_name);
		ivSearch = (ImageView) view.findViewById(R.id.common_title_function5);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.btn_blue_common_btn);

		ivSearch.setVisibility(View.VISIBLE);
		ivSearch.setImageResource(R.drawable.ic_purchase_dashboard_search);
		ivSearch.setBackgroundResource(R.drawable.btn_blue_common_btn);
		ivSearch.setOnClickListener(clickListener);

		tvTitle.setText(R.string.purchasechannel);

		ivTop = (ImageView) view.findViewById(R.id.iv_scroll_top);
		ivTop.setOnClickListener(clickListener);

		listView = (PullToRefreshListView) view.findViewById(R.id.lv_purchaseinfoListView);

		if (null != headerView)
		{
			tvNewOrUrgent = (TextView) headerView.findViewById(R.id.purchase_dashboard_newTextView);
			tvNewOrUrgent.setOnClickListener(clickListener);

			// tvRecommend = (TextView) view.findViewById(R.id.purchase_dashboard_recommendTextView);
			// tvRecommend.setOnClickListener(clickListener);

			tvunReadNum = (TextView) headerView.findViewById(R.id.purchase_dashborad_unreadTextView);

			ivNew = (ImageView) headerView.findViewById(R.id.purchase_dashboard_newImageView);
			// ivRecommend = (ImageView) view.findViewById(R.id.purchase_dashboard_recommendImageView);

			rlWait = (RelativeLayout) headerView.findViewById(R.id.purchase_dashboard_waitRelativeLayout);
			rlWait.setOnClickListener(clickListener);

			rlManagement = (RelativeLayout) headerView.findViewById(R.id.purchase_dashboard_managementRelativeLayout);
			rlManagement.setOnClickListener(clickListener);

			tvNewOrUrgent.setText(R.string.urgent);

			vDivider = headerView.findViewById(R.id.purchase_dashboard_divideView);

			if (VERSION.SDK_INT > 19)
			{
				vDivider.setVisibility(View.VISIBLE);
			}
			else
			{
				vDivider.setVisibility(View.GONE);
			}

		}

		listView.getRefreshableView().addHeaderView(headerView, null, false);

		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(initListRefreshListener());
		listView.setOnScrollListener(OnScrollListener);
		listView.setOnPullEventListener(pullEventListener);
		listView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);

		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);

		urgentList = new ArrayList<PurchaseInfo>();
		latestList = new ArrayList<PurchaseInfo>();

		adapter = new PurchaseInfoAdapter(getActivity(), urgentList);

		listView.setOnItemClickListener(itemClick);

		listView.getRefreshableView().setAdapter(adapter);

		listView.setVisibility(View.GONE);

		// changeTextViewStyle(0);

	}

	/*
	 * private void changeTextViewStyle(int status) { if (status == 0) {
	 * tvNewOrUrgent.setTextColor(getResources().getColor(R.color.color_999999));
	 * ivNew.setImageResource(R.drawable.ic_purchase_dashboard_down_off);
	 * tvRecommend.setTextColor(getResources().getColor(R.color.color_0088F0));
	 * ivRecommend.setImageResource(R.drawable.ic_purchase_dashboard_down_light); } else {
	 * tvNewOrUrgent.setTextColor(getResources().getColor(R.color.color_0088F0));
	 * ivNew.setImageResource(R.drawable.ic_purchase_dashboard_down_light);
	 * tvRecommend.setTextColor(getResources().getColor(R.color.color_999999));
	 * ivRecommend.setImageResource(R.drawable.ic_purchase_dashboard_down_off); } }
	 */
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
		Util.saveChildLastRefreshTime(getActivity(), getFragmentTag());
		if (curStatus == 1)
		{
			curLatestPage = 1;
			getData(curLatestPage, 20);
		}
		else
		{
			curUrgentPage = 1;
			getData(curUrgentPage, 20);
		}

		// 执行动作
	}

	/**
	 * 上拉加载更多
	 * @param refreshView
	 */
	protected void onPullUpToRefreshView(PullToRefreshBase<?> refreshView)
	{

		isLoadMore = true;
		if (curStatus == 1)
		{
			getData(curLatestPage + 1, 20);
		}
		else
		{
			getData(curUrgentPage + 1, 20);
		}

		// 执行动作
	}

	private String getFragmentTag()
	{
		return PurchaseDashBoardFragment.class.getName();
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
		listView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10010));
		SysManager.analysis(R.string.p_type_page, R.string.p10010);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10010));
	}

}
