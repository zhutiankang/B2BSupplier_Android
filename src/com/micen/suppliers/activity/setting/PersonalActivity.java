package com.micen.suppliers.activity.setting;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.adapter.PersonalServiceListAdapter;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.user.ServiceInfo;
import com.micen.suppliers.view.PersonalHeaderView;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class PersonalActivity extends BaseActivity
{
	@ViewById(R.id.lv_personal_list)
	protected ListView personalListView;

	private PersonalServiceListAdapter adapter;
	private TextView btnServiceMore;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		tvTitle.setText(R.string.setting_personal);
		llBack.setOnClickListener(this);

		personalListView.addHeaderView(new PersonalHeaderView(this));
		ArrayList<ServiceInfo> dataList = SupplierApplication.getInstance().getUser().content.advancedService;
		if (dataList != null && dataList.size() < 3 || dataList == null)
		{
			if (personalListView.getFooterViewsCount() > 0)
			{
				personalListView.removeFooterView(btnServiceMore);
			}
		}
		else
		{
			if (btnServiceMore == null)
			{
				btnServiceMore = new TextView(this);
				btnServiceMore.setId(R.id.tv_personal_service_more);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.toDip(this, 60));
				btnServiceMore.setLayoutParams(params);
				btnServiceMore.setGravity(Gravity.CENTER);
				btnServiceMore.setText(R.string.see_all_service);
				btnServiceMore.setOnClickListener(this);
				btnServiceMore.setTextColor(getResources().getColor(R.color.color_0088F0));
			}
			personalListView.addFooterView(btnServiceMore);
		}
		if (dataList != null)
		{
			adapter = new PersonalServiceListAdapter(this, dataList);
		}
		else
		{
			adapter = new PersonalServiceListAdapter(this, new ArrayList<ServiceInfo>());
		}
		personalListView.setAdapter(adapter);
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
		case R.id.tv_personal_service_more:
			adapter.setInit(false);
			adapter.notifyDataSetChanged();
			if (personalListView.getFooterViewsCount() > 0)
			{
				personalListView.removeFooterView(btnServiceMore);
			}
			MobclickAgent.onEvent(PersonalActivity.this, "131");
			SysManager.analysis(R.string.c_type_click, R.string.c131);
			break;
		}
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10027));
		SysManager.analysis(R.string.p_type_page, R.string.p10027);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10027));
		MobclickAgent.onPause(this);
	}
}
