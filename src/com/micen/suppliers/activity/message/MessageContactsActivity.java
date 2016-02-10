package com.micen.suppliers.activity.message;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshListView;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.adapter.message.MessageContactsListAdapter;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.message.MessageContact;
import com.micen.suppliers.module.message.MessageContacts;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.umeng.analytics.MobclickAgent;



@EActivity
public class MessageContactsActivity extends BaseActivity implements OnClickListener, OnItemClickListener,
		OnItemLongClickListener
{
	@ViewById(R.id.rl_common_title)
	protected RelativeLayout rlCommonTitle;
	@ViewById(R.id.message_contacts_lv)
	protected PullToRefreshListView pullToListView;
	@ViewById(R.id.progress_bar)
	protected RelativeLayout progressBar;
	@ViewById(R.id.broadcast_page_status)
	protected PageStatusView statusView;

	private MessageContactsListAdapter contactsListAdapter;

	private MessageContacts contacts;

	private ArrayList<MessageContact> contactList;

	private int onLongClickPos;

	private CommonDialog dialog;

	protected ListView contactsLv;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_contacts);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		initTitleBar();
		initLv();
		initOnClick();
		getContacts();
		initBroadcast();
	}

	protected void initBroadcast()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
				+ MessageConstantDefine.deleteContacts.toString());
		registerReceiver(mRefreshBroadcastReceiver, intentFilter);
	}

	private void initOnClick()
	{
		statusView.setLinkOrRefreshOnClickListener(linkClickListener);
	}

	private void initLv()
	{
		pullToListView.setMode(Mode.PULL_FROM_START);
		contactsLv = pullToListView.getRefreshableView();
		pullToListView.setOnRefreshListener(initListRefreshListener());
		pullToListView.setOnPullEventListener(pullEventListener);
		pullToListView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getActvityTag(), ""));
		contactsLv.setOnItemClickListener(this);
		contactsLv.setOnItemLongClickListener(this);
		// 添加header,只为显示顶部的分隔线
		View line = new View(MessageContactsActivity.this);
		LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.toDip(MessageContactsActivity.this,
				0));
		line.setBackgroundResource(R.color.color_f2f2f2);
		line.setLayoutParams(lineParams);
		contactsLv.addHeaderView(line);
	}

	private void getContacts()
	{
		/**
		 * 1.获取网络数据
		 * 2.数据绑定--ListView
		 * 3.使用MessageContactsListAdapter绑定数据
		 */
		RequestCenter.getContacts(listen, 1, 1);
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
						SharedPreferenceManager.getInstance().getString(getActvityTag(), ""));
				break;
			default:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
				break;
			}
		}
	};

	private void initTitleBar()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		llBack.setBackgroundResource(R.drawable.btn_blue_common_btn);
		tvTitle.setText(R.string.message_contacts_name);
		tvTitle.setTextColor(getResources().getColor(R.color.color_ffffff));
		llBack.setOnClickListener(this);
	}

	private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String broadcastAction = intent.getAction();
			if (!Utils.isEmpty(broadcastAction)
					&& broadcastAction.equals(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
							+ MessageConstantDefine.deleteContacts.toString()))
			{
				// 删除操作
				String deletePos = intent.getStringExtra(MessageConstantDefine.deleteContactsPosition.toString());
				contactList.remove(Integer.parseInt(deletePos));
				contactsListAdapter.notifyDataSetChanged();
			}
		}
	};

	private LinkClickListener linkClickListener = new LinkClickListener()
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
				statusView.setVisibility(View.INVISIBLE);
				getContacts();
				break;
			default:
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
				Util.saveChildLastRefreshTime(MessageContactsActivity.this, getActvityTag());
				getContacts();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
			{
			}
		};
		return refreshListener;
	}

	private String getActvityTag()
	{
		return MessageContactsActivity.class.getName();
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

	DisposeDataListener listen = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			refreshComplete();
			progressBar.setVisibility(View.GONE);
			contacts = (MessageContacts) obj;
			if (contacts.content != null && contacts.content.size() != 0)
			{
				statusView.setVisibility(View.GONE);
				pullToListView.setVisibility(View.VISIBLE);
				contactList = contacts.content;
				contactsListAdapter = new MessageContactsListAdapter(contactList, MessageContactsActivity.this);
				pullToListView.setAdapter(contactsListAdapter);
			}
			else
			{
				pullToListView.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				statusView.setMode(PageStatus.PageEmpty);
			}
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			activityFinishProtect();
			refreshComplete();
			ToastUtil.toast(MessageContactsActivity.this, failedReason);
			pullToListView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageEmpty);
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			refreshComplete();
			ToastUtil.toast(MessageContactsActivity.this, anomalyMsg);
			pullToListView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageNetwork);
		}
	};

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long id)
	{
		Intent intent = new Intent(MessageContactsActivity.this, MessageBehaviorRecordActivity_.class);
		intent.putExtra(MessageConstantDefine.allowAdditions.toString(), false);
		intent.putExtra(MessageConstantDefine.sourceType.toString(), "2");
		intent.putExtra(MessageConstantDefine.businessId.toString(), contacts.content.get(pos - 1).contactId);
		intent.putExtra(MessageConstantDefine.contactsPersion.toString(), pos - 1 + "");
		startActivity(intent);
		MobclickAgent.onEvent(MessageContactsActivity.this, "62");
		SysManager.analysis(R.string.c_type_click, R.string.c062);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mRefreshBroadcastReceiver);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
	{
		MobclickAgent.onEvent(MessageContactsActivity.this, "63");
		SysManager.analysis(R.string.c_type_click, R.string.c063);
		onLongClickPos = pos;
		// 弹框确认是否确定删除
		if (dialog != null)
		{
			dialog.show();
		}
		else
		{
			dialog = new CommonDialog(MessageContactsActivity.this);
			dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							// 进行删除操作,发送删除请求
							CommonProgressDialog.getInstance().showCancelableProgressDialog(
									MessageContactsActivity.this, getString(R.string.message_deleting));
							RequestCenter.deleteContactPerson(deleteLisener,
									contacts.content.get(onLongClickPos - 1).contactId);
						}
					}).buildSimpleDialog(getString(R.string.message_contacts_make_sure_to_delect));
		}
		return true;
	}

	DisposeDataListener deleteLisener = new DisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			contactList.remove(onLongClickPos - 1);
			contactsListAdapter.notifyDataSetChanged();
			if (contactList.size() == 0)
			{
				pullToListView.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				statusView.setMode(PageStatus.PageEmpty);
			}
			ToastUtil.toast(MessageContactsActivity.this, getString(R.string.message_delect_success));
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(MessageContactsActivity.this, anomalyMsg);
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(MessageContactsActivity.this, anomalyMsg);
		}
	};

	private void activityFinishProtect()
	{
		if (MessageContactsActivity.this.isFinishing())
			return;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10009));
		SysManager.analysis(R.string.p_type_page, R.string.p10009);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10009));
		MobclickAgent.onPause(this);
	}

}
