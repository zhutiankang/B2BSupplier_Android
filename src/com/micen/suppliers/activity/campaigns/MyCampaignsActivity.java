package com.micen.suppliers.activity.campaigns;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshListView;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.module.activities.Activities;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;

public class MyCampaignsActivity extends BaseActivity
{
	private RelativeLayout commonTitle;
	private SearchListProgressBar ProgressBar;
	private PullToRefreshListView pullToListView;
	private ListView ptrlv;
	private PageStatusView statusView;
	private ImageView btBack;
	private TextView tvTitleName;
	private int page = 1;
	private String pageSize = "20";
	private boolean isLoadMore = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_activities);
		initNavigationBarStyle(false);
		initView();
		initPtrlv();
		getMyActivities();
	}

	private void initPtrlv()
	{
		pullToListView.setMode(Mode.BOTH);
		pullToListView.setOnRefreshListener(initListRefreshListener());
		pullToListView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getActivityTag(), ""));
		pullToListView.setOnPullEventListener(pullEventListener);
		ptrlv = pullToListView.getRefreshableView();
		ptrlv.setOnItemClickListener(onItemClickListener);
	}

	private void initView()
	{
		commonTitle = (RelativeLayout) findViewById(R.id.rl_common_title);
		ProgressBar = (SearchListProgressBar) findViewById(R.id.progress_bar);
		pullToListView = (PullToRefreshListView) findViewById(R.id.my_activities_lv);
		statusView = (PageStatusView) findViewById(R.id.my_activities_page_status);
		btBack = (ImageView) findViewById(R.id.common_title_back);
		tvTitleName = (TextView) findViewById(R.id.common_title_name);
		commonTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));
		btBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(this);
		llBack.setBackgroundResource(R.drawable.btn_white_common_btn);
		tvTitleName.setText(R.string.my_activities_title_name);
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
								SharedPreferenceManager.getInstance().getString(getActivityTag(), ""));
						break;
					default:
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
						break;
					}
				}
			};

	private void getMyActivities()
	{
		if (isLoadMore)
		{
			page++;
		}
		else
		{
			page = 1;
		}
		RequestCenter.getMyActivities(page + "", pageSize, dataListen);

	}

	DisposeDataListener dataListen = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			refreshComplete();

			Activities activities = (Activities) obj;
			if (null != activities.activity)
			{
				if (activities.activity.size() != 0)
				{
					showRelatedDate();
					// TODO 进行数据绑定操作:针对下拉刷新和上拉加载两种情况
					if (isLoadMore)
					{
						// 调用adapt中add();

					}
					else
					{
						// 调用adapt中set();
					}

				}
				else
				{
					showNoData();
				}
			}
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			refreshComplete();
			showNoNetWork();
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			refreshComplete();
			showNoData();
		}
	};

	OnItemClickListener onItemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// 通过adapt取对应的数据信息
		}
	};

	// PullToRefresh事件回调
	private OnRefreshListener2<ListView> initListRefreshListener()
	{
		OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>()
		{
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
			{
				isLoadMore = false;
				Util.saveChildLastRefreshTime(MyCampaignsActivity.this, getActivityTag());
				getMyActivities();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
			{
				isLoadMore = true;
				getMyActivities();
			}
		};
		return refreshListener;
	}

	private String getActivityTag()
	{
		return MyCampaignsActivity.class.getName();
	}

	/**
	 * 刷新完成
	 */
	protected void refreshComplete()
	{
		if (pullToListView != null && pullToListView.isRefreshing())
		{
			pullToListView.onRefreshComplete();
		}
	}

	private void showWait()
	{
		ProgressBar.setVisibility(View.VISIBLE);
	}

	private void showRelatedDate()
	{
		ProgressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.GONE);
		pullToListView.setVisibility(View.VISIBLE);
	}

	private void showNoNetWork()
	{
		ProgressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
		statusView.setMode(PageStatus.PageNetwork);
		pullToListView.setVisibility(View.GONE);
	}

	private void showNoData()
	{
		ProgressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
		statusView.setMode(PageStatus.PageEmpty);
		pullToListView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			finish();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
}