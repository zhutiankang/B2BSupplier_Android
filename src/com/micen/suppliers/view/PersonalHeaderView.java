package com.micen.suppliers.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.http.UrlConstants;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.SupplierAuthentication;
import com.micen.suppliers.module.SupplierLevel;
import com.micen.suppliers.module.WebViewType;
import com.micen.suppliers.module.user.ServiceInfo;
import com.micen.suppliers.module.user.UserContent;
import com.umeng.analytics.MobclickAgent;


public class PersonalHeaderView extends LinearLayout implements OnClickListener
{
	private TextView personalName;
	private TextView personalMember;
	private TextView personalStatus;
	private ImageView personalGB;
	private ImageView personalAudit;
	private TextView personalLevel;
	private TextView personalApplyAudit;
	private TextView personalService;

	private Activity mActivity;
	private UserContent user;

	public PersonalHeaderView(Activity activity)
	{
		super(activity);
		this.mActivity = activity;
		user = SupplierApplication.getInstance().getUser().content;
		initView();
		initViewData();
	}

	private void initView()
	{
		LayoutInflater.from(getContext()).inflate(R.layout.personal_header_layout, this);
		personalName = (TextView) findViewById(R.id.tv_personal_name);
		personalMember = (TextView) findViewById(R.id.tv_personal_member);
		personalStatus = (TextView) findViewById(R.id.tv_personal_status);
		personalGB = (ImageView) findViewById(R.id.iv_personal_tag_gb);
		personalAudit = (ImageView) findViewById(R.id.iv_personal_tag_audit);
		personalLevel = (TextView) findViewById(R.id.tv_personal_level);
		personalApplyAudit = (TextView) findViewById(R.id.tv_personal_apply_audit);
		personalService = (TextView) findViewById(R.id.tv_personal_service);

		personalApplyAudit.setOnClickListener(this);
	}

	private void initViewData()
	{
		if (user == null)
			return;
		personalName.setText(user.userInfo.fullName);
		personalMember.setText(user.userInfo.email);
		personalStatus.setText(formatOperatorNumToValue());
		// 是否显示金牌标示
		switch (SupplierLevel.getValueByTag(user.companyInfo.csLevel))
		{
		case Cooperate:
			personalGB.setBackgroundResource(R.drawable.ic_supplier_gold_member);
			personalApplyAudit.setVisibility(View.GONE);
			personalLevel.setText(R.string.cooperate_supplier);
			break;
		case Higher:
			personalGB.setBackgroundResource(R.drawable.ic_supplier_gold_member);
			personalApplyAudit.setVisibility(View.GONE);
			personalLevel.setText(R.string.gold_supplier);
			break;
		case Free:
			personalLevel.setText(R.string.free_supplier);
			personalGB.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		// 根据状态显示认证标示
		switch (SupplierAuthentication.getValueByTag(user.companyInfo.auditType))
		{
		case AuditSupplier:
			personalAudit.setBackgroundResource(R.drawable.ic_supplier_as);
			personalLevel.setText(R.string.higher_supplier);
			break;
		case OnsiteCheck:
			personalAudit.setBackgroundResource(R.drawable.ic_supplier_oc);
			personalLevel.setText(R.string.higher_supplier);
			break;
		case LicenseVerify:
			personalAudit.setBackgroundResource(R.drawable.ic_supplier_lv);
			personalLevel.setText(R.string.higher_supplier);
			break;
		default:
			personalAudit.setVisibility(View.GONE);
			break;
		}

		ArrayList<ServiceInfo> dataList = SupplierApplication.getInstance().getUser().content.advancedService;
		if (dataList != null && dataList.size() == 0 || dataList == null)
		{
			personalService.setVisibility(View.GONE);
		}
	}

	private String formatOperatorNumToValue()
	{
		if (SupplierApplication.getInstance().getUser().content.userInfo.isManager())
		{
			return mActivity.getString(R.string.operation_manager);
		}
		else
		{
			return mActivity.getString(R.string.operation_general);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tv_personal_apply_audit:
			Intent applyIntent = new Intent(mActivity, WebViewActivity_.class);
			applyIntent.putExtra("targetUri", UrlConstants.APPLY_URL);
			applyIntent.putExtra("targetType", WebViewType.getValue(WebViewType.Apply));
			mActivity.startActivity(applyIntent);
			MobclickAgent.onEvent(mActivity, "130");
			SysManager.analysis(R.string.c_type_click, R.string.c130);
			break;
		}
	}

}
