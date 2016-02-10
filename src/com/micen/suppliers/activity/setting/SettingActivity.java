package com.micen.suppliers.activity.setting;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.http.FocusClient;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.activity.LoginActivity_;
import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.http.UrlConstants;
import com.micen.suppliers.manager.CheckUpdateManager;
import com.micen.suppliers.manager.IntentManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.NotifySetStatus;
import com.micen.suppliers.module.SupplierLevel;
import com.micen.suppliers.module.WebViewType;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class SettingActivity extends BaseActivity
{
	@ViewById(R.id.tv_setting_personal)
	protected TextView btnPersonal;
	@ViewById(R.id.rl_setting_disturb)
	protected RelativeLayout btnNotdisturb;
	@ViewById(R.id.tv_setting_disturb)
	protected TextView tvNotDisturb;
	@ViewById(R.id.cb_setting_disturb)
	protected CheckBox cbDisturb;
	@ViewById(R.id.tv_setting_broadcast_msg)
	protected TextView btnBroadcastMsg;
	@ViewById(R.id.tv_setting_feedback)
	protected TextView btnFeedback;
	@ViewById(R.id.rl_setting_update)
	protected RelativeLayout btnUpdate;
	@ViewById(R.id.tv_setting_update)
	protected TextView tvUpdate;
	@ViewById(R.id.tv_setting_update_status)
	protected TextView updateStatus;
	@ViewById(R.id.tv_setting_aboutus)
	protected TextView btnAboutus;
	@ViewById(R.id.rl_setting_call_service)
	protected RelativeLayout btnCallService;
	@ViewById(R.id.tv_setting_call_service)
	protected TextView tvCallService;
	@ViewById(R.id.rl_setting_apply_service)
	protected RelativeLayout btnApplyService;
	@ViewById(R.id.tv_setting_apply_service)
	protected TextView tvApplyService;
	@ViewById(R.id.tv_setting_signout)
	protected TextView btnSignout;
	private CommonDialog exitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		tvTitle.setText(R.string.setting);
		llBack.setOnClickListener(this);

		btnPersonal.setOnClickListener(this);
		btnNotdisturb.setOnClickListener(this);
		btnBroadcastMsg.setOnClickListener(this);
		btnFeedback.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		btnAboutus.setOnClickListener(this);
		btnCallService.setOnClickListener(this);
		btnApplyService.setOnClickListener(this);
		btnSignout.setOnClickListener(this);

		if (SharedPreferenceManager.getInstance().getBoolean("isHaveNewVersion", false))
		{
			updateStatus.setText(R.string.setting_update_status_new);
			updateStatus.setTextColor(getResources().getColor(R.color.color_FF3F26));
		}
		else
		{
			updateStatus.setText(getString(R.string.setting_update_status) + Utils.getAppVersionName(this));
			updateStatus.setTextColor(getResources().getColor(R.color.color_999999));
		}
		cbDisturb.setTag(NotifySetStatus.getValue(NotifySetStatus.NotDisturbSwitch));
		cbDisturb.setChecked(SharedPreferenceManager.getInstance().getBoolean(cbDisturb.getTag().toString(), false));

		initCompoundDrawable();

		if (SupplierApplication.getInstance().getUser() != null)
		{
			switch (SupplierLevel
					.getValueByTag(SupplierApplication.getInstance().getUser().content.companyInfo.csLevel))
			{
			case Cooperate:
			case Higher:
				btnApplyService.setVisibility(View.GONE);
				break;
			case Free:
				btnCallService.setVisibility(View.GONE);
				break;
			default:
				btnCallService.setVisibility(View.GONE);
				break;
			}
		}
	}

	private void initCompoundDrawable()
	{
		setTextViewCompoundDrawable(R.drawable.ic_setting_personal, btnPersonal);
		setTextViewCompoundDrawable(R.drawable.ic_setting_broadcast_msg, btnBroadcastMsg);
		setTextViewCompoundDrawable(R.drawable.ic_setting_feedback, btnFeedback);
		setTextViewCompoundDrawable(R.drawable.ic_setting_update, tvUpdate);
		setTextViewCompoundDrawable(R.drawable.ic_setting_aboutus, btnAboutus);
		setTextViewCompoundDrawable(R.drawable.ic_setting_call_service, tvCallService);
		setTextViewCompoundDrawable(R.drawable.ic_setting_apply_service, tvApplyService);
		setTextViewCompoundDrawable(R.drawable.ic_setting_signout, btnSignout);
	}

	private void setTextViewCompoundDrawable(int drawableResId, TextView tv)
	{
		Drawable drawable = getResources().getDrawable(drawableResId);
		drawable.setBounds(0, 0, Utils.toDip(this, 30), Utils.toDip(this, 30));
		tv.setCompoundDrawablePadding(Utils.toDip(this, 15));
		tv.setCompoundDrawables(drawable, null, null, null);
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
		case R.id.tv_setting_personal:
			startActivity(new Intent(this, PersonalActivity_.class));
			MobclickAgent.onEvent(SettingActivity.this, "121");
			SysManager.analysis(R.string.c_type_click, R.string.c121);
			break;
		case R.id.rl_setting_disturb:
			cbDisturb.setChecked(!cbDisturb.isChecked());
			SharedPreferenceManager.getInstance().putBoolean(cbDisturb.getTag().toString(), cbDisturb.isChecked());
			RequestCenter.updateNotDisturbSetting(cbDisturb.isChecked() ? "1" : "0");
			MobclickAgent.onEvent(SettingActivity.this, "123");
			SysManager.analysis(R.string.c_type_click, R.string.c123);
			break;
		case R.id.tv_setting_broadcast_msg:
			startActivity(new Intent(this, NotifyMessageActivity_.class));
			MobclickAgent.onEvent(SettingActivity.this, "122");
			SysManager.analysis(R.string.c_type_click, R.string.c122);
			break;
		case R.id.tv_setting_feedback:
			startActivity(new Intent(this, FeedBackActivity_.class));
			MobclickAgent.onEvent(SettingActivity.this, "124");
			SysManager.analysis(R.string.c_type_click, R.string.c124);
			break;
		case R.id.rl_setting_update:
			CheckUpdateManager.checkUpdate(this, true);
			break;
		case R.id.tv_setting_aboutus:
			Intent intent = new Intent(this, WebViewActivity_.class);
			intent.putExtra("targetUri", UrlConstants.ABOUT_URL);
			intent.putExtra("targetType", WebViewType.getValue(WebViewType.AboutUs));
			startActivity(intent);
			MobclickAgent.onEvent(SettingActivity.this, "125");
			SysManager.analysis(R.string.c_type_click, R.string.c125);
			break;
		case R.id.rl_setting_call_service:
			View view = LayoutInflater.from(this).inflate(R.layout.service_dialog_content, null);
			TextView serviceCode = (TextView) view.findViewById(R.id.tv_service_code);
			serviceCode.setText(SupplierApplication.getInstance().getUser().content.serviceCode);
			CommonDialog dialog = new CommonDialog(this);
			dialog.setDialogContentView(view).setCancelBtnText(R.string.cancel).setConfirmBtnText(R.string.call)
					.setConfirmTextColor(getResources().getColor(R.color.color_0088F0))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							IntentManager.makeCall(SettingActivity.this, getString(R.string.setting_call_service_tel));
						}
					}).build();
			MobclickAgent.onEvent(SettingActivity.this, "126");
			SysManager.analysis(R.string.c_type_click, R.string.c126);
			break;
		case R.id.rl_setting_apply_service:
			Intent applyIntent = new Intent(this, WebViewActivity_.class);
			applyIntent.putExtra("targetUri", UrlConstants.APPLY_URL);
			applyIntent.putExtra("targetType", WebViewType.getValue(WebViewType.Apply));
			startActivity(applyIntent);
			break;
		case R.id.tv_setting_signout:
			MobclickAgent.onEvent(SettingActivity.this, "127");
			SysManager.analysis(R.string.c_type_click, R.string.c127);
			exitDialog = new CommonDialog(this);
			exitDialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
					.setCancelDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							exitDialog.dismiss();
							MobclickAgent.onEvent(SettingActivity.this, "128");
							SysManager.analysis(R.string.c_type_click, R.string.c128);
						}
					}).setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							SysManager.logout();
							SysManager.exitSystem(SettingActivity.this);
							startActivity(new Intent(SettingActivity.this, LoginActivity_.class));
							MobclickAgent.onEvent(SettingActivity.this, "129");
							SysManager.analysis(R.string.c_type_click, R.string.c129);
							FocusClient.clearCookie(SettingActivity.this);
						}
					}).buildSimpleDialog(getString(R.string.signout_dialog_msg));
			break;
		}
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10026));
		SysManager.analysis(R.string.p_type_page, R.string.p10026);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10026));
		MobclickAgent.onPause(this);
	}
}
