package com.micen.suppliers.activity.purchase;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.widget.PagerSlidingTabStrip;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationPageAdapter;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.QuotationAllContent;
import com.micen.suppliers.module.purchase.QuotationDetailListWithStatus;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


public class PurchaseQuotationManagementActivity extends BaseFragmentActivity
{
	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;

	private PagerSlidingTabStrip mTabStrip;
	private ViewPager mViewPager;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	private QuotationAllContent data;

	private PurchaseQuotationPageAdapter adapter;

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.common_ll_title_back:
				MobclickAgent.onEvent(PurchaseQuotationManagementActivity.this, "99");
				SysManager.analysis(R.string.c_type_click, R.string.c099);
				finish();
				break;

			default:
				break;
			}
		}
	};

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			data = (QuotationAllContent) obj;

			if ("0".equals(data.code) && data.content.size() > 0)
			{
				// data.content.
				List<QuotationDetailListWithStatus> list = new ArrayList<QuotationDetailListWithStatus>();

				// 1：待审核 2：需要修改 3.已报价 4：已过期，5：已冻结
				// 3已报价》1待审核》2需要修改》4已过期》5已冻结

				for (int i = 0; i < data.content.size(); i++)
				{
					if ("3".equals(data.content.get(i).quotationStatus))
					{
						list.add(data.content.get(i));
						break;
					}
				}
				for (int i = 0; i < data.content.size(); i++)
				{
					if ("1".equals(data.content.get(i).quotationStatus))
					{
						list.add(data.content.get(i));
						break;
					}
				}
				for (int i = 0; i < data.content.size(); i++)
				{
					if ("2".equals(data.content.get(i).quotationStatus))
					{
						list.add(data.content.get(i));
						break;
					}
				}
				for (int i = 0; i < data.content.size(); i++)
				{
					if ("4".equals(data.content.get(i).quotationStatus))
					{
						list.add(data.content.get(i));
						break;
					}
				}
				for (int i = 0; i < data.content.size(); i++)
				{
					if ("5".equals(data.content.get(i).quotationStatus))
					{
						list.add(data.content.get(i));
						break;
					}

				}

				adapter = new PurchaseQuotationPageAdapter(PurchaseQuotationManagementActivity.this,
						getSupportFragmentManager(), list);
				mViewPager.setAdapter(adapter);
				mTabStrip.setViewPager(mViewPager);
				showRelatedData();
			}
			else
			{
				showNoRelatedData();
			}
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			// TODO Auto-generated method stub
			showNoRelatedData();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			showNetworkError();
		}
	};

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub

			if (PageStatus.PageEmptyLink == status)
			{
				finish();
			}
			else
			{
				showwait();
				getData();
			}

		}

	};

	private OnPageChangeListener pageListener = new OnPageChangeListener()
	{

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position)
		{
			if (data.content != null && data.content.size() != 0)
			{
				int type = 0;
				try
				{
					type = Integer.parseInt(data.content.get(position).quotationStatus);
				}
				catch (Exception e)
				{
					// TODO: handle exception
					type = 0;
				}
				// 1：待审核 2：需要修改 3.已报价 4：已过期，5：已冻结
				switch (type)
				{
				case 1:
					MobclickAgent.onEvent(PurchaseQuotationManagementActivity.this, "101");
					SysManager.analysis(R.string.c_type_click, R.string.c101);
					break;
				case 2:
					MobclickAgent.onEvent(PurchaseQuotationManagementActivity.this, "102");
					SysManager.analysis(R.string.c_type_click, R.string.c102);
					break;
				case 3:
					MobclickAgent.onEvent(PurchaseQuotationManagementActivity.this, "100");
					SysManager.analysis(R.string.c_type_click, R.string.c100);
					break;
				case 4:
					MobclickAgent.onEvent(PurchaseQuotationManagementActivity.this, "103");
					SysManager.analysis(R.string.c_type_click, R.string.c103);
					break;
				case 5:
					MobclickAgent.onEvent(PurchaseQuotationManagementActivity.this, "104");
					SysManager.analysis(R.string.c_type_click, R.string.c104);
					break;
				default:
					break;
				}
			}
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
			// TODO Auto-generated method stub

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quotation_management);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);

		ivBack = (ImageView) findViewById(R.id.common_title_back);
		llBack = (LinearLayout) findViewById(R.id.common_ll_title_back);
		tvTitle = (TextView) findViewById(R.id.common_title_name);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_btn);

		tvTitle.setText(R.string.manager_quotation);

		mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.quotationi_management_tabPagerSlidingTabStrip);

		if (1 > Constants.density)
		{
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			Constants.density = dm.density;
		}

		mTabStrip.setUnderlineHeight(Util.dip2px(4));
		mTabStrip.setUnderlineColorResource(R.color.transparent);

		mTabStrip.setIndicatorHeight(Util.dip2px(4));
		mTabStrip.setIndicatorColorResource(R.color.color_ffa73f);

		mTabStrip.setAllCaps(false);
		mTabStrip.setDividerColorResource(R.color.transparent);

		mTabStrip.setOnPageChangeListener(pageListener);

		mViewPager = (ViewPager) findViewById(R.id.quotationi_management_containViewPager);
		mViewPager.setOffscreenPageLimit(4);

		pageStateView = (PageStatusView) findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);

		showwait();

		getData();
	}

	private void getData()
	{
		RequestCenter.getAllQuotations(1, 20, dataListener);
	}

	private void showwait()
	{
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void showNoRelatedData()
	{
		// listview.setVisibility(View.GONE);
		pageStateView.setMode(PageStatus.PageEmptyLink);
		pageStateView.setVisibility(View.VISIBLE);

		progressBar.setVisibility(View.GONE);
	}

	private void showRelatedData()
	{
		// listview.setVisibility(View.VISIBLE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
	}

	private void showNetworkError()
	{
		// listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10019));
		SysManager.analysis(R.string.p_type_page, R.string.p10019);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10019));
		MobclickAgent.onPause(this);
	}

}
