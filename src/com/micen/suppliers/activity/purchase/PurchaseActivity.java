package com.micen.suppliers.activity.purchase;

import java.util.ArrayList;

import org.androidannotations.annotations.EActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.http.purchase.EntrustQuotation;
import com.micen.suppliers.http.purchase.NormalQuotation;
import com.micen.suppliers.module.purchase.ProductFilter;
import com.micen.suppliers.module.purchase.RfqNeedInfo;
import com.micen.suppliers.view.purchase.ChooseProductFromRoomFragment;
import com.micen.suppliers.view.purchase.ImageBrowserFragment;
import com.micen.suppliers.view.purchase.NormalQuotationFragment;
import com.micen.suppliers.view.purchase.NormalQuotationPreviewFragment;
import com.micen.suppliers.view.purchase.NormalQuotationReeditFragment;
import com.micen.suppliers.view.purchase.NormalQuotationReviewFragment;
import com.micen.suppliers.view.purchase.ProductFilterFragment;
import com.micen.suppliers.view.purchase.PurchaseDashBoardFragment;
import com.micen.suppliers.view.purchase.PurchaseSearchCategoryFragment;
import com.micen.suppliers.view.purchase.PurchaseSearchFragment;
import com.micen.suppliers.view.purchase.PurchaseSearchResultFragment;
import com.micen.suppliers.view.purchase.QuotationListFragment;
import com.micen.suppliers.view.purchase.RFqInfoFragment;
import com.micen.suppliers.view.purchase.RecommendQuotationFragment;
import com.micen.suppliers.view.purchase.RecommendQuotationPreviewFragment;
import com.micen.suppliers.view.purchase.RecommendQuotationReviewFragment;
import com.micen.suppliers.view.purchase.WaitingForQuotationFragment;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class PurchaseActivity extends BaseFragmentActivity
{

	Fragment mFragment;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_purchase);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);

		FragmentManager fm = this.getSupportFragmentManager();

		mFragment = fm.findFragmentById(R.id.id_fragment_container);

		if (null == mFragment)
		{
			name = this.getIntent().getStringExtra("fragment");
			mFragment = getFragment(name);
			fm.beginTransaction().add(R.id.id_fragment_container, mFragment).commitAllowingStateLoss();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.e("Activity", "PurchaseActivity onSaveInstanceState");
	}

	private Fragment getFragment(String name)
	{
		if (Utils.isEmpty(name))
		{
			return PurchaseDashBoardFragment.newInstance();
			// return RecommendQuotationFragment.newInstance(this.getIntent().getStringExtra("rfqid"), null);
			// return NormalQuotationFragment.newInstance(this.getIntent().getStringExtra("rfqid"));
		}

		if ("rfqinfo".equals(name))
		{
			initNavigationBarStyle(true);
			return RFqInfoFragment.newInstance(this.getIntent().getStringExtra("rfqid"), this.getIntent()
					.getStringExtra("quoteSource"), this.getIntent().getStringExtra("isrecommend"), this.getIntent()
					.getStringExtra("reqFrom"), this.getIntent().getStringExtra("quotationid"));
		}
		else if ("search".equals(name))
		{
			return PurchaseSearchFragment.newInstance();
		}
		else if ("choosecategory".equals(name))
		{
			return PurchaseSearchCategoryFragment.newInstance();
		}
		else if ("searchresult".equals(name))
		{
			return PurchaseSearchResultFragment.newInstance(this.getIntent().getStringExtra("category"), this
					.getIntent().getStringExtra("keyword"), this.getIntent().getStringExtra("categoryname"));
		}
		else if ("quotationlist".equals(name))
		{
			initNavigationBarStyle(true);
			return QuotationListFragment.newInstance(this.getIntent().getStringExtra("rfqid"), this.getIntent()
					.getStringExtra("isrecommend"));
		}
		else if ("quotationnormal".equals(name))
		{
			RfqNeedInfo info = this.getIntent().getParcelableExtra("data");
			return NormalQuotationFragment.newInstance(this.getIntent().getStringExtra("rfqid"), info, this.getIntent()
					.getStringExtra("quoteSource"), this.getIntent().getStringExtra("quotationid"));
		}
		else if ("quotationnormalreedit".equals(name))
		{
			return NormalQuotationReeditFragment.newInstance(this.getIntent().getStringExtra("rfqid"), this.getIntent()
					.getStringExtra("quotationid"));
		}
		else if ("quotationentrust".equals(name))
		{
			// EntrustQuotationFragment
			RfqNeedInfo info = this.getIntent().getParcelableExtra("data");
			return RecommendQuotationFragment.newInstance(this.getIntent().getStringExtra("rfqid"), info);
		}
		else if ("chooseproduct".equals(name))
		{
			return ChooseProductFromRoomFragment.newInstance();
		}
		else if ("chooseproductfilter".equals(name))
		{
			ArrayList<ProductFilter> list = this.getIntent().getParcelableArrayListExtra("data");
			return ProductFilterFragment.newInstance(list, this.getIntent().getStringArrayExtra("name"), this
					.getIntent().getStringArrayExtra("value"));
		}
		else if ("rfqneedquote".equals(name))
		{
			return WaitingForQuotationFragment.newInstance(this.getIntent().getBooleanExtra("isFromBroadcast", false),
					this.getIntent().getStringExtra("messageId"), this.getIntent().getStringExtra("from"));
		}
		else if ("normalquotationpreview".equals(name))
		{
			NormalQuotation data = this.getIntent().getParcelableExtra("value");
			return NormalQuotationPreviewFragment.newInstance(this.getIntent().getBooleanExtra("edit", false), data,
					this.getIntent().getStringExtra("quoteSource"), this.getIntent().getStringExtra("quotationid"));
		}
		else if ("entrustquotationpreview".equals(name))
		{
			// this.getIntent().getParcelableExtra("value")
			EntrustQuotation data = this.getIntent().getParcelableExtra("value");
			return RecommendQuotationPreviewFragment.newInstance(data);
		}
		else if ("quotationdetail".equals(name))
		{
			return NormalQuotationReviewFragment.newInstance(this.getIntent().getStringExtra("quotationid"));
		}
		else if ("recommenddetail".equals(name))
		{
			return RecommendQuotationReviewFragment.newInstance(this.getIntent().getStringExtra("quotationid"));
		}
		else if ("imagebrowser".equals(name))
		{
			return ImageBrowserFragment.newInstance(this.getIntent().getStringExtra("imUri"));
		}

		return PurchaseDashBoardFragment.newInstance();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ("chooseproduct".equals(name))
		{
			((ChooseProductFromRoomFragment) getSupportFragmentManager().findFragmentById(R.id.id_fragment_container))
					.back();
			return true;
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
}
