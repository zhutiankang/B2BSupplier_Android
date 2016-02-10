package com.micen.suppliers.activity.setting;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.NotifySetStatus;
import com.micen.suppliers.module.NotifyType;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class NotifyMessageActivity extends BaseActivity
{
	@ViewById(R.id.rl_setting_notify)
	protected RelativeLayout btnNotify;
	@ViewById(R.id.cb_setting_notify)
	protected CheckBox cbNotify;

	@ViewById(R.id.rl_setting_notify_inquiry)
	protected RelativeLayout btnNotifyInquiry;
	@ViewById(R.id.cb_setting_notify_inquiry)
	protected CheckBox cbNotifyInquiry;

	@ViewById(R.id.rl_setting_notify_purchase)
	protected RelativeLayout btnNotifyPurchase;
	@ViewById(R.id.cb_setting_notify_purchase)
	protected CheckBox cbNotifyPurchase;

	@ViewById(R.id.rl_setting_notify_service)
	protected RelativeLayout btnNotifyService;
	@ViewById(R.id.cb_setting_notify_service)
	protected CheckBox cbNotifyService;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notify_message);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		tvTitle.setText(R.string.setting_notify);
		llBack.setOnClickListener(this);

		btnNotify.setOnClickListener(this);
		btnNotifyInquiry.setOnClickListener(this);
		btnNotifyPurchase.setOnClickListener(this);
		btnNotifyService.setOnClickListener(this);

		cbNotify.setTag(NotifySetStatus.getValue(NotifySetStatus.TotalSwitch));
		cbNotifyInquiry.setTag(NotifySetStatus.getValue(NotifySetStatus.InquirySwitch));
		cbNotifyPurchase.setTag(NotifySetStatus.getValue(NotifySetStatus.PurchaseSwitch));
		cbNotifyService.setTag(NotifySetStatus.getValue(NotifySetStatus.ServiceSwitch));

		cbNotify.setChecked(SharedPreferenceManager.getInstance().getBoolean(cbNotify.getTag().toString(), true));
		cbNotifyInquiry.setChecked(SharedPreferenceManager.getInstance().getBoolean(
				cbNotifyInquiry.getTag().toString(), true));
		cbNotifyPurchase.setChecked(SharedPreferenceManager.getInstance().getBoolean(
				cbNotifyPurchase.getTag().toString(), true));
		cbNotifyService.setChecked(SharedPreferenceManager.getInstance().getBoolean(
				cbNotifyService.getTag().toString(), true));

		cbNotify.setOnCheckedChangeListener(notifyChange);
		if (!cbNotify.isChecked())
		{
			btnNotifyInquiry.setClickable(false);
			btnNotifyPurchase.setClickable(false);
			btnNotifyService.setClickable(false);
		}
	}

	private OnCheckedChangeListener notifyChange = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			btnNotifyInquiry.setClickable(isChecked);
			btnNotifyPurchase.setClickable(isChecked);
			btnNotifyService.setClickable(isChecked);

			if (!isChecked)
				closeAllNotify(cbNotifyInquiry, cbNotifyPurchase, cbNotifyService);
		}
	};

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			MobclickAgent.onEvent(NotifyMessageActivity.this, "134");
			SysManager.analysis(R.string.c_type_click, R.string.c134);
			finish();
			break;
		case R.id.rl_setting_notify:
			cbNotify.setChecked(!cbNotify.isChecked());
			SharedPreferenceManager.getInstance().putBoolean(cbNotify.getTag().toString(), cbNotify.isChecked());
			RequestCenter.updatePushSetting("0", cbNotify.isChecked() ? "1" : "0");
			MobclickAgent.onEvent(NotifyMessageActivity.this, "135");
			SysManager.analysis(R.string.c_type_click, R.string.c135);
			break;
		case R.id.rl_setting_notify_inquiry:
			switchOnClick(cbNotifyInquiry);
			RequestCenter.updatePushSetting(NotifyType.getValue(NotifyType.Inquiry), cbNotifyInquiry.isChecked() ? "1"
					: "0");
			MobclickAgent.onEvent(NotifyMessageActivity.this, "136");
			SysManager.analysis(R.string.c_type_click, R.string.c136);
			break;
		case R.id.rl_setting_notify_purchase:
			switchOnClick(cbNotifyPurchase);
			RequestCenter.updatePushSetting(NotifyType.getValue(NotifyType.Purchase),
					cbNotifyPurchase.isChecked() ? "1" : "0");
			MobclickAgent.onEvent(NotifyMessageActivity.this, "137");
			SysManager.analysis(R.string.c_type_click, R.string.c137);
			break;
		case R.id.rl_setting_notify_service:
			switchOnClick(cbNotifyService);
			RequestCenter.updatePushSetting(NotifyType.getValue(NotifyType.Service), cbNotifyService.isChecked() ? "1"
					: "0");
			MobclickAgent.onEvent(NotifyMessageActivity.this, "138");
			SysManager.analysis(R.string.c_type_click, R.string.c138);
			break;
		}
	}

	private void switchOnClick(CheckBox cb)
	{
		cb.setChecked(!cb.isChecked());
		SharedPreferenceManager.getInstance().putBoolean(cb.getTag().toString(), cb.isChecked());
		isAllNotifyClosed(cbNotifyInquiry, cbNotifyPurchase, cbNotifyService);
	}

	private void isAllNotifyClosed(CheckBox... cbs)
	{
		boolean result = true;
		for (CheckBox cb : cbs)
		{
			if (cb.isChecked())
			{
				result = false;
				break;
			}
		}
		if (result)
		{
			cbNotify.setChecked(false);
		}
	}

	private void closeAllNotify(CheckBox... cbs)
	{
		for (CheckBox cb : cbs)
		{
			cb.setChecked(false);
			SharedPreferenceManager.getInstance().putBoolean(cb.getTag().toString(), false);
		}
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10030));
		SysManager.analysis(R.string.p_type_page, R.string.p10030);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10030));
		MobclickAgent.onPause(this);
	}
}
