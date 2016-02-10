package com.micen.suppliers.activity;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.home.HomeActivity_;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.user.User;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class LoadingActivity extends BaseActivity
{
	@ViewById(R.id.loading_copyright)
	protected TextView copyright;

	@Extra("messageLink")
	protected String messageLink;
	@Extra("messageParam")
	protected String messageParam;
	@Extra("messageId")
	protected String messageId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		// 友盟统计设置
		AnalyticsConfig.setAppkey(Constants.UMENG_APPKEY);
		AnalyticsConfig.setChannel(Constants.UMENG_CHANNEL);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		AnalyticsConfig.enableEncrypt(true);
		// 代表应用 正常启动
		SupplierApplication.setStartFromLoading();
	}

	@AfterViews
	protected void initView()
	{
		copyright.setText(getString(R.string.copyright_left) + Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR)
				+ getString(R.string.copyright_right));

		initVersionParams();
		if (SharedPreferenceManager.getInstance().getBoolean("isAutoLogon", false) && !isAccountOverdue())
		{
			RequestCenter.login(SharedPreferenceManager.getInstance().getString("lastLoginName", ""),
					SharedPreferenceManager.getInstance().getString("lastLoginPassword", ""), loginListener);
			handler.sendEmptyMessageDelayed(1, 5000);
		}
		else
		{
			handler.sendEmptyMessageDelayed(0, 2000);
		}
	}

	/**
	 * 判断登录账号是否过期
	 * @return
	 */
	private boolean isAccountOverdue()
	{
		long lastLoginTime = SharedPreferenceManager.getInstance().getLong("lastLoginTime",
				Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
		long currentTime = Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis();
		// 上次登录时间距离现在时间大于3天(259200000毫秒)
		if (currentTime - lastLoginTime > 259200000)
		{
			SysManager.logout();
			return true;
		}
		return false;
	}

	private void initVersionParams()
	{
		String latestVersion = SharedPreferenceManager.getInstance().getString("latestVersion", "");
		if ("".equals(latestVersion) || latestVersion.equals(Utils.getAppVersionName(this)))
		{
			SharedPreferenceManager.getInstance().remove("isClickSetting");
			SharedPreferenceManager.getInstance().remove("isHaveNewVersion");
		}
	}

	private DisposeDataListener loginListener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object result)
		{
			if (!isFinishing())
			{
				SharedPreferenceManager.getInstance().putLong("lastLoginTime",
						Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
				SupplierApplication.getInstance().setUser((User) result);
				RequestCenter.boundAccount(new SimpleDisposeDataListener(), true);
				handler.sendEmptyMessage(2);
			}
		}

		public void onFailure(Object failedMsg)
		{
			if (!isFinishing())
			{
				ToastUtil.toast(LoadingActivity.this, failedMsg);
				handler.sendEmptyMessage(0);
			}
		};

	};

	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				SharedPreferences sp = getSharedPreferences(Constants.sharedPreDBName, Context.MODE_PRIVATE);
				if (sp.getBoolean("isFirst", true))
				{
					startActivity(new Intent(LoadingActivity.this, GuideActivity_.class));
				}
				else
				{
					startActivity(new Intent(LoadingActivity.this, LoginActivity_.class));
				}
				finish();
				break;
			case 1:
				if (isFinishing())
					return;
				sendEmptyMessage(0);
				break;
			case 2:
				Intent intent = new Intent(LoadingActivity.this, HomeActivity_.class);
				if (messageLink != null && messageParam != null && messageId != null)
				{
					intent.putExtra("messageLink", messageLink);
					intent.putExtra("messageParam", messageParam);
					intent.putExtra("messageId", messageId);
				}
				startActivity(intent);
				finish();
				break;
			}
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			SysManager.exitSystem(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
