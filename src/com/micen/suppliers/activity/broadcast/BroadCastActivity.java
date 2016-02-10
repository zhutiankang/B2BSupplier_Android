package com.micen.suppliers.activity.broadcast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.module.response.BroadCast;
import com.focustech.common.module.response.BroadCastContent;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshListView;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.activity.message.MessageDetailActivity_;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.adapter.broadcast.BroadCastBaseAdapter;
import com.micen.suppliers.adapter.broadcast.InquiryBroadCastAdapter;
import com.micen.suppliers.adapter.broadcast.PurchaseBroadCastAdapter;
import com.micen.suppliers.adapter.broadcast.ServiceBroadCastAdapter;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.NotifyLink;
import com.micen.suppliers.module.NotifyType;
import com.micen.suppliers.module.WebViewType;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class BroadCastActivity extends BaseActivity implements OnRefreshListener2<ListView>
{
	@ViewById(R.id.broadcast_listview)
	protected PullToRefreshListView pullToRefreshListView;
	@ViewById(R.id.progress_bar)
	protected SearchListProgressBar progressBar;
	@ViewById(R.id.broadcast_page_status)
	protected PageStatusView statusView;

	@Extra("broadCastType")
	protected String broadCastType;

	private ListView listView;
	private static final String TAG = BroadCastActivity.class.getName();
	private BroadCastBaseAdapter adapter;

	private int pageIndex = 1;
	private boolean isRefresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast);
		initNavigationBarStyle(false);
	}

	@AfterViews
	protected void initView()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(this);
		switch (NotifyType.getValueByTag(broadCastType))
		{
		case Inquiry:
			tvTitle.setText(R.string.setting_notify_inquiry);
			break;
		case Purchase:
			tvTitle.setText(R.string.setting_notify_purchase);
			break;
		case Service:
			tvTitle.setText(R.string.setting_notify_service);
			break;
		default:
			break;
		}

		// 不启动任何刷新
		pullToRefreshListView.setMode(Mode.BOTH);
		// 不显示指示器
		pullToRefreshListView.setShowIndicator(false);
		pullToRefreshListView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(TAG + broadCastType, ""));
		pullToRefreshListView.setOnPullEventListener(pullEventListener);
		pullToRefreshListView.setOnRefreshListener(this);
		listView = pullToRefreshListView.getRefreshableView();
		listView.setOnItemClickListener(itemClick);
		listView.setSelector(R.color.transparent);
		pullToRefreshListView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		statusView.setLinkOrRefreshOnClickListener(new LinkClickListener()
		{
			@Override
			public void onClick(PageStatus status)
			{
				switch (status)
				{
				case PageEmpty:
					break;
				case PageNetwork:
					progressBar.setVisibility(View.VISIBLE);
					statusView.setVisibility(View.GONE);
					RequestCenter.getMessages(pageIndex, broadCastType, dataListener);
					break;
				default:
					break;
				}
			}
		});
		RequestCenter.getMessages(pageIndex, broadCastType, dataListener);
	}

	private OnItemClickListener itemClick = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			BroadCastContent module = (BroadCastContent) arg0.getAdapter().getItem(arg2);
			if (adapter != null)
			{
				module.readState = "1";
				adapter.updateModuleData(module);
			}
			switch (NotifyType.getValueByTag(broadCastType))
			{
			case Inquiry:
				Intent inquiryIntent = new Intent(BroadCastActivity.this, MessageDetailActivity_.class);
				inquiryIntent.putExtra(MessageConstantDefine.isPurchase.toString(), true);
				inquiryIntent.putExtra(MessageConstantDefine.isAllowDelete.toString(), true);
				inquiryIntent.putExtra(MessageConstantDefine.mailId.toString(), module.messageParameter);
				inquiryIntent.putExtra(MessageConstantDefine.action.toString(), "0");
				inquiryIntent.putExtra("isFromBroadcast", true);
				inquiryIntent.putExtra("messageId", module.messageId);
				startActivity(inquiryIntent);
				MobclickAgent.onEvent(BroadCastActivity.this, "112");
				SysManager.analysis(R.string.c_type_click, R.string.c112);
				break;
			case Purchase:
				Intent purchaseIntent = new Intent(BroadCastActivity.this, PurchaseActivity_.class);
				purchaseIntent.putExtra("fragment", "rfqneedquote");
				purchaseIntent.putExtra("isFromBroadcast", true);
				purchaseIntent.putExtra("messageId", module.messageId);
				startActivity(purchaseIntent);
				MobclickAgent.onEvent(BroadCastActivity.this, "113");
				SysManager.analysis(R.string.c_type_click, R.string.c113);
				break;
			case Service:
				if (NotifyLink.getValueByTag(module.messageLink) == NotifyLink.WebAddress)
				{
					Intent intent = new Intent(BroadCastActivity.this, WebViewActivity_.class);
					intent.putExtra("targetUri", module.messageParameter);
					intent.putExtra("targetType", WebViewType.getValue(WebViewType.Service));
					intent.putExtra("isFromBroadcast", true);
					intent.putExtra("messageId", module.messageId);
					startActivity(intent);
					MobclickAgent.onEvent(BroadCastActivity.this, "114");
					SysManager.analysis(R.string.c_type_click, R.string.c114);
				}
				else
				{
					MobclickAgent.onEvent(BroadCastActivity.this, "115");
					SysManager.analysis(R.string.c_type_click, R.string.c115);
				}
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
						SharedPreferenceManager.getInstance().getString(TAG + broadCastType, ""));
				break;
			default:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
				break;
			}
		}
	};

	/**
	 * 刷新完成
	 */
	private void refreshComplete()
	{
		if (pullToRefreshListView != null)
		{
			pullToRefreshListView.onRefreshComplete();
		}
	}

	private DisposeDataListener dataListener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			refreshComplete();
			statusView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			pullToRefreshListView.setVisibility(View.VISIBLE);
			BroadCast broadcast = (BroadCast) obj;
			if (broadcast.content != null && broadcast.content.size() > 0)
			{
				if (adapter == null || isRefresh)
				{
					switch (NotifyType.getValueByTag(broadCastType))
					{
					case Inquiry:
						adapter = new InquiryBroadCastAdapter(BroadCastActivity.this, broadcast.content);
						break;
					case Purchase:
						adapter = new PurchaseBroadCastAdapter(BroadCastActivity.this, broadcast.content);
						break;
					case Service:
						adapter = new ServiceBroadCastAdapter(BroadCastActivity.this, broadcast.content);
						break;
					default:
						break;
					}
					listView.setAdapter(adapter);
				}
				else
				{
					adapter.addData(broadcast.content);
				}
			}
			else
			{
				if (adapter == null)
				{
					pullToRefreshListView.setVisibility(View.GONE);
					statusView.setVisibility(View.VISIBLE);
					statusView.setMode(PageStatus.PageEmpty);
				}
			}
			if (isRefresh)
			{
				isRefresh = false;
			}
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			refreshComplete();
			progressBar.setVisibility(View.GONE);
			if (adapter == null)
			{
				statusView.setVisibility(View.VISIBLE);
				statusView.setMode(PageStatus.PageEmpty);
			}
			else
			{
				statusView.setVisibility(View.GONE);
				if (!isFinishing())
				{
					ToastUtil.toast(BroadCastActivity.this, anomalyMsg);
				}
			}
		};

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			refreshComplete();
			progressBar.setVisibility(View.GONE);
			if (adapter == null)
			{
				statusView.setVisibility(View.VISIBLE);
				statusView.setMode(PageStatus.PageNetwork);
			}
			else
			{
				statusView.setVisibility(View.GONE);
				if (!isFinishing())
				{
					ToastUtil.toast(BroadCastActivity.this, anomalyMsg);
				}
			}
		};
	};

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
	{
		isRefresh = true;
		pageIndex = 1;
		Util.saveChildLastRefreshTime(BroadCastActivity.this, TAG + broadCastType);
		RequestCenter.getMessages(pageIndex, broadCastType, dataListener);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
	{
		pageIndex++;
		RequestCenter.getMessages(pageIndex, broadCastType, dataListener);
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			finish();
			break;
		}
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		switch (NotifyType.getValueByTag(broadCastType))
		{
		case Inquiry:
			MobclickAgent.onPageStart(getString(R.string.p10021));
			SysManager.analysis(R.string.p_type_page, R.string.p10021);
			break;
		case Purchase:
			MobclickAgent.onPageStart(getString(R.string.p10022));
			SysManager.analysis(R.string.p_type_page, R.string.p10022);
			break;
		case Service:
			MobclickAgent.onPageStart(getString(R.string.p10023));
			SysManager.analysis(R.string.p_type_page, R.string.p10023);
			break;
		default:
			break;
		}
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		switch (NotifyType.getValueByTag(broadCastType))
		{
		case Inquiry:
			MobclickAgent.onPageEnd(getString(R.string.p10021));
			break;
		case Purchase:
			MobclickAgent.onPageEnd(getString(R.string.p10022));
			break;
		case Service:
			MobclickAgent.onPageEnd(getString(R.string.p10023));
			break;
		default:
			break;
		}
		MobclickAgent.onPause(this);

	}

}
