package com.micen.suppliers.activity.home;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.broadcast.xmpp.XmppConstants;
import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.module.response.PersonalMessage;
import com.focustech.common.module.response.PersonalMessageStatus;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.google.zxing.core.client.result.ParsedResultType;
import com.google.zxing.demo.CaptureActivity;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.activity.message.MessageDetailActivity_;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.activity.qrcode.QRCodeResultActivity;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.ActivityManager;
import com.micen.suppliers.manager.CheckUpdateManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.NotifyLink;
import com.micen.suppliers.module.NotifySetStatus;
import com.micen.suppliers.module.NotifyType;
import com.micen.suppliers.module.WebViewType;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.qrcode.QRCodeTypeDefine;
import com.micen.suppliers.view.home.BroadcastFragment;
import com.micen.suppliers.view.home.BroadcastFragment_;
import com.micen.suppliers.view.home.DiscoveryFragment;
import com.micen.suppliers.view.home.DiscoveryFragment_;
import com.micen.suppliers.view.home.HomeBaseFragment;
import com.micen.suppliers.view.home.ServiceFragment;
import com.micen.suppliers.view.home.ServiceFragment_;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class HomeActivity extends BaseFragmentActivity
{
	@ViewById(R.id.ll_common_toolbar_layout)
	protected LinearLayout toolBarLayout;
	@ViewById(R.id.common_toolbar_service)
	protected RelativeLayout serviceToolBar;
	@ViewById(R.id.common_toolbar_discovery)
	protected RelativeLayout discoveryToolBar;
	@ViewById(R.id.common_toolbar_broadcast)
	protected RelativeLayout broadcastToolBar;
	@ViewById(R.id.iv_broadcast_flag)
	protected ImageView broadcastFlag;

	@Extra("messageLink")
	protected String messageLink;
	@Extra("messageParam")
	protected String messageParam;
	@Extra("messageId")
	protected String messageId;

	protected ServiceFragment serviceFragment;
	protected DiscoveryFragment discoveryFragment;
	protected BroadcastFragment broadcastFragment;

	private ReceiveBroadCast receiveBroadCast;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ActivityManager.getInstance().put(this);
		initNavigationBarStyle(false);

		if (findViewById(R.id.home_fragment_container) != null)
		{
			if (savedInstanceState != null)
			{
				return;
			}
			serviceFragment = new ServiceFragment_();
			addFragment(serviceFragment);
		}

		CheckUpdateManager.checkUpdate(this, false);

		// XGPushConfig.enableDebug(this, true);
		XGPushManager.registerPush(this);
	}

	@AfterViews
	protected void initView()
	{
		setChildSelected(R.id.common_toolbar_service);
		serviceToolBar.setOnClickListener(this);
		discoveryToolBar.setOnClickListener(this);
		broadcastToolBar.setOnClickListener(this);
		MobclickAgent.openActivityDurationTrack(false);
		autoIntent();
	}

	private void autoIntent()
	{
		if (messageLink == null || messageParam == null || messageId == null)
		{
			return;
		}
		switch (NotifyLink.getValueByTag(messageLink))
		{
		case InquiryDetail:
			Intent intentInquiry = new Intent();
			intentInquiry.setClass(this, MessageDetailActivity_.class);
			intentInquiry.putExtra("isFromBroadcast", true);
			intentInquiry.putExtra("messageId", messageId);
			intentInquiry.putExtra(MessageConstantDefine.isPurchase.toString(), true);
			intentInquiry.putExtra(MessageConstantDefine.mailId.toString(), messageParam);
			intentInquiry.putExtra(MessageConstantDefine.action.toString(), "0");
			startActivity(intentInquiry);
			break;
		case Purchase:
			Intent intentPurchase = new Intent();
			intentPurchase.setClass(this, PurchaseActivity_.class);
			intentPurchase.putExtra("isFromBroadcast", true);
			intentPurchase.putExtra("messageId", messageId);
			intentPurchase.putExtra("fragment", "rfqneedquote");
			startActivity(intentPurchase);
			break;
		case WebAddress:
			Intent intentWeb = new Intent();
			intentWeb.setClass(this, WebViewActivity_.class);
			intentWeb.putExtra("targetUri", messageParam);
			intentWeb.putExtra("targetType", WebViewType.getValue(WebViewType.Service));
			intentWeb.putExtra("isFromBroadcast", true);
			intentWeb.putExtra("messageId", messageId);
			startActivity(intentWeb);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		/** 注册广播 */
		receiveBroadCast = new ReceiveBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(XmppConstants.ACTION_SHOW_NOTIFICATION); // 只有持有相同的action的接受者才能接收此广播
		registerReceiver(receiveBroadCast, filter);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		unregisterReceiver(receiveBroadCast);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ActivityManager.getInstance().remove(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		RequestCenter.syncPersonalMessage(listener);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private DisposeDataListener listener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			PersonalMessage message = (PersonalMessage) obj;
			if (message.content.message != null && message.content.message.size() > 0)
			{
				if (broadcastFragment != null)
				{
					broadcastFragment.refreshBroadcastNum(message.content.message);
				}
				int unreadNum = 0;
				for (PersonalMessageStatus module : message.content.message)
				{
					if (!Utils.isEmpty(module.unreadNum) && !"0".equals(module.unreadNum))
					{
						unreadNum += Integer.parseInt(module.unreadNum);
					}
				}
				if (unreadNum > 0)
				{
					broadcastFlag.setVisibility(View.VISIBLE);
				}
				else
				{
					broadcastFlag.setVisibility(View.GONE);
				}
				// 通过服务器返回的参数初始化本地通知设置项
				SharedPreferenceManager.getInstance().putBoolean(NotifySetStatus.getValue(NotifySetStatus.TotalSwitch),
						"1".equals(message.content.isReceive) ? true : false);
				for (PersonalMessageStatus module : message.content.message)
				{
					switch (NotifyType.getValueByTag(module.pushType))
					{
					case Inquiry:
						SharedPreferenceManager.getInstance().putBoolean(
								NotifySetStatus.getValue(NotifySetStatus.InquirySwitch),
								"1".equals(module.pushState) ? true : false);
						break;
					case Purchase:
						SharedPreferenceManager.getInstance().putBoolean(
								NotifySetStatus.getValue(NotifySetStatus.PurchaseSwitch),
								"1".equals(module.pushState) ? true : false);
						break;
					case Service:
						SharedPreferenceManager.getInstance().putBoolean(
								NotifySetStatus.getValue(NotifySetStatus.ServiceSwitch),
								"1".equals(module.pushState) ? true : false);
						break;
					default:
						break;
					}
				}
			}
			else
			{
				broadcastFlag.setVisibility(View.GONE);
			}
		}
	};

	/**
	 * 设置首页底部工具栏状态
	 * @param selectViewId
	 */
	private void setChildSelected(int selectViewId)
	{
		RelativeLayout itemLayout;
		for (int i = 0; i < toolBarLayout.getChildCount(); i++)
		{
			if (toolBarLayout.getChildAt(i) instanceof RelativeLayout)
			{
				itemLayout = (RelativeLayout) toolBarLayout.getChildAt(i);
				if (itemLayout.getId() == selectViewId)
				{
					switch (selectViewId)
					{
					case R.id.common_toolbar_service:
						setTextViewCompoundDrawable(R.drawable.ic_bottom_bar_service_selected,
								(TextView) itemLayout.getChildAt(0));
						break;
					case R.id.common_toolbar_discovery:
						setTextViewCompoundDrawable(R.drawable.ic_bottom_bar_discovery_selected,
								(TextView) itemLayout.getChildAt(0));
						break;
					case R.id.common_toolbar_broadcast:
						setTextViewCompoundDrawable(R.drawable.ic_bottom_bar_broadcast_selected,
								(TextView) itemLayout.getChildAt(0));
						break;
					}
					((TextView) itemLayout.getChildAt(0)).setTextColor(getResources().getColor(R.color.color_0088F0));
				}
				else
				{
					switch (toolBarLayout.getChildAt(i).getId())
					{
					case R.id.common_toolbar_service:
						setTextViewCompoundDrawable(R.drawable.ic_bottom_bar_service,
								(TextView) itemLayout.getChildAt(0));
						break;
					case R.id.common_toolbar_discovery:
						setTextViewCompoundDrawable(R.drawable.ic_bottom_bar_discovery,
								(TextView) itemLayout.getChildAt(0));
						break;
					case R.id.common_toolbar_broadcast:
						setTextViewCompoundDrawable(R.drawable.ic_bottom_bar_broadcast,
								(TextView) itemLayout.getChildAt(0));
						break;
					}
					((TextView) itemLayout.getChildAt(0)).setTextColor(getResources().getColor(R.color.color_505559));
				}
			}
		}
	}

	private void setTextViewCompoundDrawable(int drawableResId, TextView tv)
	{
		Drawable drawable = getResources().getDrawable(drawableResId);
		drawable.setBounds(0, 0, Utils.toDip(this, 34), Utils.toDip(this, 34));
		tv.setCompoundDrawables(null, drawable, null, null);
	}

	private void switchFragment(Fragment targetFragment, Fragment... otherFragments)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (!targetFragment.isAdded())
		{
			transaction.add(R.id.home_fragment_container, targetFragment);
		}
		else
		{
			transaction.show(targetFragment);
		}
		for (Fragment fragment : otherFragments)
		{
			if (fragment != null)
			{
				transaction.hide(fragment);
			}
		}
		transaction.commit();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.common_toolbar_service:
			if (serviceFragment == null)
			{
				serviceFragment = new ServiceFragment_();
				addFragment(serviceFragment);
			}
			else
			{
				switchFragment(serviceFragment, discoveryFragment, broadcastFragment);
			}
			setChildSelected(v.getId());
			break;
		case R.id.common_toolbar_discovery:
			if (discoveryFragment == null)
			{
				discoveryFragment = new DiscoveryFragment_();
				addFragment(discoveryFragment);
			}
			else
			{
				switchFragment(discoveryFragment, serviceFragment, broadcastFragment);
			}
			setChildSelected(v.getId());
			MobclickAgent.onEvent(HomeActivity.this, "9");
			SysManager.analysis(R.string.c_type_click, R.string.c009);
			break;
		case R.id.common_toolbar_broadcast:
			if (broadcastFragment == null)
			{
				broadcastFragment = new BroadcastFragment_();
				addFragment(broadcastFragment);
			}
			else
			{
				switchFragment(broadcastFragment, serviceFragment, discoveryFragment);
			}
			setChildSelected(v.getId());
			MobclickAgent.onEvent(HomeActivity.this, "10");
			SysManager.analysis(R.string.c_type_click, R.string.c010);
			break;
		}
	}

	private void addFragment(HomeBaseFragment targetFragment)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.home_fragment_container, targetFragment);

		if (serviceFragment != null && serviceFragment != targetFragment)
		{
			transaction.hide(serviceFragment);
		}

		if (discoveryFragment != null && discoveryFragment != targetFragment)
		{
			transaction.hide(discoveryFragment);
		}

		if (broadcastFragment != null && broadcastFragment != targetFragment)
		{
			transaction.hide(broadcastFragment);
		}

		transaction.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			CommonDialog dialog = new CommonDialog(this);
			dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							SysManager.exitSystem(HomeActivity.this);
						}
					}).buildSimpleDialog(getString(R.string.exit_dialog_msg));
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class ReceiveBroadCast extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			RequestCenter.syncPersonalMessage(listener);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (Activity.RESULT_OK == resultCode)
		{
			if (1 == requestCode)
			{
				Bundle bundle = data.getExtras();
				final String resultType = bundle.getString("resultType");
				if (ParsedResultType.MIC.toString().equals(resultType))
				{
					final String action = bundle.getString("action");
					String param = bundle.getString("param");
					switch (QRCodeTypeDefine.getValueByTag(action))
					{
					case Sign:
						try
						{
							JSONObject jsonObject = new JSONObject(param);
							if (jsonObject.has("activityId") && jsonObject.has("topicName"))
							{
								startActivityForResult(
										new Intent(HomeActivity.this, QRCodeResultActivity.class)
												.putExtra("fragment", "SignIn")
												.putExtra("activityId", jsonObject.getString("activityId"))
												.putExtra("topicName", jsonObject.getString("topicName")), 2);
								return;
							}
						}
						catch (JSONException e)
						{
							e.printStackTrace();
						}

						break;
					default:
						break;
					}

				}
				else
				{
					final String resultContent = bundle.getString("resultContent");
					startActivityForResult((new Intent(HomeActivity.this, QRCodeResultActivity.class).putExtra(
							"qrCodeContent", resultContent)), 2);
					return;

				}

			}
			else if (2 == requestCode)
			{
				startActivityForResult((new Intent(HomeActivity.this, CaptureActivity.class)), 1);

			}
		}
	}

}
