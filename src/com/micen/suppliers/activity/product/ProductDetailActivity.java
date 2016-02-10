package com.micen.suppliers.activity.product;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.module.product.ProductDetailContent;
import com.micen.suppliers.module.product.ProductDetails;
import com.micen.suppliers.module.product.ProductKeyValuePair;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.micen.suppliers.view.product.ProductDescriptionFragment;
import com.micen.suppliers.view.product.ProductFragment;
import com.micen.suppliers.widget.product.PullToNextAdapter;
import com.micen.suppliers.widget.product.PullToNextLayout;
import com.micen.suppliers.widget.product.PullToNextLayout.LayoutType;


@EActivity
public class ProductDetailActivity extends BaseFragmentActivity
{
	@ViewById(R.id.root)
	protected View rootLayout;
	@ViewById(R.id.pull_to_next_layout)
	protected PullToNextLayout pullToNextLayout;
	@ViewById(R.id.progressbar_layout)
	protected SearchListProgressBar progressBar;
	@ViewById(R.id.broadcast_page_status)
	protected PageStatusView statusView;
	@ViewById(R.id.rl_common_title)
	protected View productTitleLayout;

	private String productId;
	private ProductDetailContent detail;

	private ArrayList<Fragment> list;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productdetail_layout);
		initNavigationBarStyle(true);
	}

	@AfterViews
	protected void initView()
	{
		// TODO
		productId = getIntent().getStringExtra("productId");
		btnBack.setImageResource(R.drawable.ic_title_back_blue);
		btnBack.setOnClickListener(this);
		btnBack.setBackgroundResource(R.drawable.btn_white_common_btn);
		list = new ArrayList<Fragment>();
		statusView.setLinkOrRefreshOnClickListener(new LinkClickListener()
		{
			@Override
			public void onClick(PageStatus status)
			{
				switch (status)
				{
				case PageEmpty:
					break;
				case PageNetwork:
					progressBar.setVisibility(View.VISIBLE);
					statusView.setVisibility(View.GONE);
					stratGetProductDetail();
					break;
				default:
					break;
				}
			}
		});
		stratGetProductDetail();
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_title_back:
			onBackPressed();
			break;
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * 请求产品详情数据...
	 */
	private void stratGetProductDetail()
	{
		RequestCenter.getProductDetail(productDetailListener, productId);
	}

	/**
	 * 请求产品详情响应事件
	 */
	private DisposeDataListener productDetailListener = new DisposeDataListener()
	{
		@Override
		public void onSuccess(Object arg0)
		{
			if (isFinishing())
			{
				return;
			}
			pullToNextLayout.setVisibility(View.VISIBLE);
			detail = ((ProductDetails) arg0).content;
			try
			{
				if (detail != null)
				{
					JSONObject tradeInfoObj = new JSONObject(detail.tradeInfo);
					/**
					 * 初始化产品详情页面的交易信息
					 */
					detail.tradeInfoList = new ArrayList<ProductKeyValuePair>();
					Util.setJsonValueToPairList(tradeInfoObj, detail.tradeInfoList);
					if (tradeInfoObj.has("Price"))
					{
						detail.pricevalue = tradeInfoObj.getString("Price");
					}
					if (tradeInfoObj.has("orderUnit"))
					{
						detail.orderUnit = tradeInfoObj.getString("orderUnit");
					}

					if (tradeInfoObj.has("Trade Terms"))
					{
						detail.tradeTerms = tradeInfoObj.getString("Trade Terms");
					}
					/**
					 * 初始化产品详情列表页面的basicinfo
					 */
					JSONObject basicInfoObj = new JSONObject(detail.basicInfo);
					detail.basicInfoList = new ArrayList<ProductKeyValuePair>();
					Util.setJsonValueToPairList(basicInfoObj, detail.basicInfoList);
					/**
					 * 初始化产品详情列表页面的addationInfo
					 */
					JSONObject additionalInfoObj = new JSONObject(detail.additionalInfo);
					detail.additionalInfoList = new ArrayList<ProductKeyValuePair>();
					Util.setJsonValueToPairList(additionalInfoObj, detail.additionalInfoList);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			ProductFragment prudctFragment = new ProductFragment(detail, productTitleLayout);
			ProductDescriptionFragment productDescFragment = new ProductDescriptionFragment(detail);
			list.clear();
			list.add(prudctFragment);
			list.add(productDescFragment);
			pullToNextLayout.setLayoutType(LayoutType.PRODUCT_DETAIL);
			pullToNextLayout.setAdapter(new PullToNextAdapter(getSupportFragmentManager(), list));
			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			ToastUtil.toast(ProductDetailActivity.this, failedReason);
			progressBar.setVisibility(View.GONE);
			pullToNextLayout.setVisibility(View.GONE);
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageEmpty);
			finish();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			ToastUtil.toast(ProductDetailActivity.this, anomalyMsg);
			progressBar.setVisibility(View.GONE);
			pullToNextLayout.setVisibility(View.GONE);
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageNetwork);
		}
	};
}
