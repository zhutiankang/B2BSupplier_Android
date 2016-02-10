package com.micen.suppliers.activity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.widget.SystemBarTintManager;
import com.micen.suppliers.R;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.user.User;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class BaseActivity extends Activity implements OnClickListener
{
	@ViewById(R.id.common_ll_title_back)
	protected LinearLayout llBack;
	@ViewById(R.id.common_title_back)
	protected ImageView btnBack;
	@ViewById(R.id.common_title_name)
	protected TextView tvTitle;
	@ViewById(R.id.common_title_function1)
	protected ImageView btnFunction1;
	@ViewById(R.id.common_title_function2)
	protected ImageView btnFunction2;
	@ViewById(R.id.common_title_function3)
	protected ImageView btnFunction3;
	@ViewById(R.id.common_title_function4)
	protected ImageView btnFunction4;
	@ViewById(R.id.common_title_function5)
	protected ImageView btnFunction5;
	@ViewById(R.id.common_title_text_function)
	protected TextView btnTextFunction;

	protected ActivityFinishReceiver finishReceiver = new ActivityFinishReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (null != savedInstanceState && null != savedInstanceState.getBundle("savebundle"))
		{
			getIntent().putExtras(savedInstanceState.getBundle("savebundle"));
			getIntent().putExtra("savedInstanceState", true);
		}
		registerFinishReceiver();
		registerReLoginReceiver();
	}

	/**
	 * 初始化通知栏样式
	 * @param isNavigationBarGray	是否灰色样式
	 */
	protected void initNavigationBarStyle(boolean isNavigationBarGray)
	{
		// Only set the tint if the device is running KitKat or above
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(getResources().getColor(
					isNavigationBarGray ? R.color.color_333333 : R.color.color_005799));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBundle("savebundle", getIntent().getExtras());
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(finishReceiver);
		unregisterReceiver(receiver);
	}

	protected void registerFinishReceiver()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.APP_FINISH_ACTION);
		registerReceiver(finishReceiver, filter); // 注册
	}

	protected void registerReLoginReceiver()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.APP_RELOGIN_ACTION);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onClick(View v)
	{

	}

	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			int code = intent.getIntExtra("code", 0);
			if (99999 == code)
			{
				ToastUtil.toast(BaseActivity.this, intent.getStringExtra("reLoginReason"));
				// 重新登录
				RequestCenter.login(SharedPreferenceManager.getInstance().getString("lastLoginName", ""),
						SharedPreferenceManager.getInstance().getString("lastLoginPassword", ""), loginListener);
			}
			else if (30001 == code)
			{
				ToastUtil.toast(BaseActivity.this, intent.getStringExtra("reLoginReason"));
				// 登录失败
				SysManager.logout();
				SysManager.exitSystem(BaseActivity.this);
				startActivity(new Intent(BaseActivity.this, LoginActivity_.class));
			}
		}
	};

	private DisposeDataListener loginListener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object result)
		{
			SupplierApplication.getInstance().setUser((User) result);
			RequestCenter.boundAccount(new SimpleDisposeDataListener(), true);
		}

		@Override
		public void onFailure(Object failedReason)
		{
			// 登录失败
			SysManager.logout();
			SysManager.exitSystem(BaseActivity.this);
			startActivity(new Intent(BaseActivity.this, LoginActivity_.class));
		}
	};
}
