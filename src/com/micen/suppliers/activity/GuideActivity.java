package com.micen.suppliers.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.micen.suppliers.R;
import com.micen.suppliers.adapter.GuidePagerAdapter;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class GuideActivity extends BaseActivity
{
	@ViewById(R.id.guide_viewpager)
	protected ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		GuidePagerAdapter adapter = new GuidePagerAdapter(this);
		adapter.setBtStartListener(this);
		viewPager.setAdapter(adapter);
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.btn_skip_guide:
		case R.id.btn_guide_start:
			SharedPreferenceManager.getInstance().putBoolean("isFirst", false);
			startActivity(new Intent(this, LoginActivity_.class));
			finish();
			break;
		}
	}
}
