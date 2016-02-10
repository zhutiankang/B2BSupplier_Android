package com.micen.suppliers.activity.purchase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.util.LogUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.viewpagerindictor.UnderlinePageIndicator;
import com.focustech.common.widget.viewpagerindictor.UnderlinePageIndicator.ChangeTitle;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.SearchResultKeyValue;
import com.micen.suppliers.view.purchase.PurchaseSearchFilterFragment;
import com.micen.suppliers.view.purchase.PurchaseSearchFilterFragment.OnChooseChangedListener;
import com.umeng.analytics.MobclickAgent;

public class PurchaseSearchFilterActivity extends BaseFragmentActivity implements OnChooseChangedListener
{
	// Fragment mFragment;

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;

	private UnderlinePageIndicator pageIndicator;

	private ViewPager mViewPager;

	private List<PurchaseSearchFilterFragment> mList;

	private TextView btn1TextView;
	private TextView btn2TextView;
	private TextView btn3TextView;

	private Button btnReset;
	private Button btnOK;

	private int type;

	// private String keywords;
	// private String country;
	// private String category;
	// private String postDate;

	private String[] property =
	{ "", "", "", "", "" };

	private Map<String, String> filterMap = new HashMap<String, String>();

	private FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
	{

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			if (mList != null)
				return mList.size();
			return 0;
		}

		@Override
		public Fragment getItem(int position)
		{
			// TODO Auto-generated method stub
			if (mList != null)
				return mList.get(position);
			return null;
		}
	};

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.filter_header_btn1_TextView:
				mViewPager.setCurrentItem(0);
				MobclickAgent.onEvent(PurchaseSearchFilterActivity.this, "81");
				SysManager.analysis(R.string.c_type_click, R.string.c081);
				break;
			case R.id.filter_header_btn2_TextView:
				mViewPager.setCurrentItem(1);
				MobclickAgent.onEvent(PurchaseSearchFilterActivity.this, "82");
				SysManager.analysis(R.string.c_type_click, R.string.c082);
				break;
			case R.id.filter_header_btn3_TextView:
				mViewPager.setCurrentItem(2);
				MobclickAgent.onEvent(PurchaseSearchFilterActivity.this, "83");
				SysManager.analysis(R.string.c_type_click, R.string.c083);
				break;
			case R.id.filter_resetButton:
				// 重置所有
				filterMap.clear();

				if (!Utils.isEmpty(property[4]))
				{
					filterMap.put("category", property[2]);
				}

				if (null != mList)
				{
					for (PurchaseSearchFilterFragment fragmetn : mList)
					{
						fragmetn.resetChoose();
					}
				}

				MobclickAgent.onEvent(PurchaseSearchFilterActivity.this, "87");
				SysManager.analysis(R.string.c_type_click, R.string.c087);
				break;
			case R.id.filter_okButton:
				Intent data = new Intent();
				Iterator<Entry<String, String>> iter = filterMap.entrySet().iterator();
				while (iter.hasNext())
				{
					Entry<String, String> entry = iter.next();
					data.putExtra(entry.getKey(), entry.getValue());
				}
				setResult(1, data);
				MobclickAgent.onEvent(PurchaseSearchFilterActivity.this, "88");
				SysManager.analysis(R.string.c_type_click, R.string.c088);
				finish();
				break;

			case R.id.common_ll_title_back:
				MobclickAgent.onEvent(PurchaseSearchFilterActivity.this, "141");
				SysManager.analysis(R.string.c_type_click, R.string.c141);
				finish();
				break;
			default:
				break;
			}
		}
	};

	private UnderlinePageIndicator.ChangeTitle indicatorListener = new ChangeTitle()
	{

		@Override
		public void changeTitleStatus(int position)
		{
			// TODO Auto-generated method stub
			mViewPager.setCurrentItem(position);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_filter);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);

		ivBack = (ImageView) findViewById(R.id.common_title_back);
		llBack = (LinearLayout) findViewById(R.id.common_ll_title_back);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_btn);

		tvTitle = (TextView) findViewById(R.id.common_title_name);

		tvTitle.setText(R.string.filter);

		mViewPager = (ViewPager) findViewById(R.id.filter_containViewPager);
		mViewPager.setOffscreenPageLimit(2);

		btnReset = (Button) findViewById(R.id.filter_resetButton);
		btnReset.setOnClickListener(clickListener);

		btnOK = (Button) findViewById(R.id.filter_okButton);
		btnOK.setOnClickListener(clickListener);

		pageIndicator = (UnderlinePageIndicator) findViewById(R.id.filter_indicator);
		/*
		 * FragmentManager fm = this.getSupportFragmentManager(); mFragment = (PurchaseDashBoardFragment)
		 * fm.findFragmentById(R.id.id_fragment_container); if (null == mFragment) { mFragment =
		 * PurchaseSearchFragment.newInstance(); } fm.beginTransaction().add(R.id.id_fragment_container,
		 * mFragment).commit();
		 */

		type = getIntent().getIntExtra("type", 0);

		if (type == 1)
		{
			btn1TextView = (TextView) findViewById(R.id.filter_header_btn1_TextView);
			btn2TextView = (TextView) findViewById(R.id.filter_header_btn2_TextView);
			btn3TextView = (TextView) findViewById(R.id.filter_header_btn3_TextView);

			btn1TextView.setOnClickListener(clickListener);
			btn2TextView.setOnClickListener(clickListener);
			btn3TextView.setOnClickListener(clickListener);

			btn1TextView.setText(getString(R.string.category_zh));
			btn2TextView.setText(getString(R.string.location_zh));
			btn3TextView.setText(getString(R.string.postdate_zh));

			mList = new ArrayList<PurchaseSearchFilterFragment>();
			// 采购搜索结果页的筛选
			// category
			ArrayList<SearchResultKeyValue> categoryList = getIntent().getParcelableArrayListExtra("categorylist");

			mList.add(PurchaseSearchFilterFragment.newInstance("category", categoryList));
			// location
			ArrayList<SearchResultKeyValue> location = getIntent().getParcelableArrayListExtra("locationlist");
			mList.add(PurchaseSearchFilterFragment.newInstance("location", location));
			// postdate
			ArrayList<SearchResultKeyValue> postdate = getIntent().getParcelableArrayListExtra("postdatelist");
			mList.add(PurchaseSearchFilterFragment.newInstance("postdate", postdate));

			// keywords = getIntent().getStringExtra("keyword");
			// country = getIntent().getStringExtra("country");
			// category = getIntent().getStringExtra("category");
			// postDate = getIntent().getStringExtra("postDate");

			property[0] = getIntent().getStringExtra("keyword");
			property[1] = getIntent().getStringExtra("country");
			property[2] = getIntent().getStringExtra("category");
			property[3] = getIntent().getStringExtra("postDate");
			property[4] = getIntent().getStringExtra("categoryname");
		}

		mViewPager.setAdapter(mAdapter);

		pageIndicator.setViewPager(mViewPager);
		pageIndicator.setChangeTitleListener(indicatorListener);
		pageIndicator.setFades(false);
		pageIndicator.setSelectedColor(getResources().getColor(R.color.page_indicator_title_selected));
	}

	@Override
	public void onItemSelected(String key, String value)
	{
		// TODO Auto-generated method stub
		// 记录各个筛选，选中的值
		filterMap.put(key, value);
		LogUtil.e("filterMap", filterMap.toString());
	}

	@Override
	public String[] getHasChoose()
	{
		// TODO Auto-generated method stub

		// property[1] = filterMap.get("country");
		// property[2] = filterMap.get("category");
		// property[3] = filterMap.get("postDate");

		return property;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10014));
		SysManager.analysis(R.string.p_type_page, R.string.p10014);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10014));
		MobclickAgent.onPause(this);
	}
}
