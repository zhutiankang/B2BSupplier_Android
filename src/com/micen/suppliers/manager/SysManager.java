package com.micen.suppliers.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.inputmethod.InputMethodManager;

import com.focustech.common.http.FocusClient;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.mob.analysis.FocusAnalyticsTracker;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.util.ImageUtil;
import com.umeng.analytics.MobclickAgent;


public class SysManager
{
	private static SysManager instance;
	private static Context mContext;

	private SysManager()
	{
	}

	public static SysManager getInstance()
	{
		if (instance == null)
		{
			instance = new SysManager();
		}
		return instance;
	}

	/**
	 * 退出系统
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static void exitSystem(Context context)
	{
		ImageUtil.getImageLoader().clearMemoryCache();
		MobclickAgent.onKillProcess(context);
		Intent intent = new Intent();
		intent.setAction(Constants.APP_FINISH_ACTION); // 说明动作
		if (VERSION.SDK_INT >= 12)
		{
			intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		}
		if (context != null)
		{
			context.sendBroadcast(intent);// 该函数用于发送广播
		}
		else
		{
			SupplierApplication.getInstance().sendBroadcast(intent);
		}
		FocusAnalyticsTracker.getInstances().stopAnalyticsService();
	}

	/**
	 * 用户登出，清除用户数据
	 */
	public static void logout()
	{
		SharedPreferenceManager.getInstance().putBoolean("isAutoLogon", false);
		SharedPreferenceManager.getInstance().remove("lastLoginPassword");
		FocusClient.removeCookieStore();
		SupplierApplication.getInstance().setUser(null);
		RequestCenter.boundAccount(new SimpleDisposeDataListener(), false);
	}

	/***
	 * 让软键盘消失
	 */
	public void dismissInputKey(Activity context)
	{
		try
		{
			((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void showInputKey(Activity context)
	{
		try
		{
			((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(
					context.getCurrentFocus(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 收集行为
	 * @param actionType 行为类别
	 * @param actionDescribe 行为描述
	 */
	public static void analysis(String actionType, String actionDescribe)
	{
		// 新增加mobservice的事件统计
		FocusAnalyticsTracker mbtracker = FocusAnalyticsTracker.getInstances();
		mbtracker.trackEvent(actionDescribe);
	}

	/**
	 * 收集行为
	 * @param actionTypeID String's resource id
	 * @param actionDescribeID String's resouce id
	 */
	public static void analysis(int actionTypeID, int actionDescribeID)
	{
		if (mContext == null)
		{
			mContext = SupplierApplication.getInstance();
		}
		// String actionType = mContext.getString(actionTypeID);
		String actionDescribe = mContext.getString(actionDescribeID);

		// 新增加mobservice的事件统计
		FocusAnalyticsTracker mbtracker = FocusAnalyticsTracker.getInstances();
		mbtracker.trackEvent(actionDescribe);
	}
}
