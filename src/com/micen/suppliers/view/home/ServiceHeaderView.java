package com.micen.suppliers.view.home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.focustech.common.widget.circulatebanner.CBViewHolderCreator;
import com.focustech.common.widget.circulatebanner.CirculateBanner;
import com.focustech.common.widget.circulatebanner.CirculateBanner.Transformer;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.message.MessageActivity_;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.promotion.SystemPromotionBanner;
import com.micen.suppliers.module.user.User;
import com.umeng.analytics.MobclickAgent;


public class ServiceHeaderView extends LinearLayout implements OnClickListener
{
	private RelativeLayout inquiryLayout;
	private RelativeLayout purchaseLayout;
	@SuppressWarnings("rawtypes")
	private CirculateBanner bannerViewPager;
	private Activity mActivity;
	private ArrayList<SystemPromotionBanner> dataList;
	private LinearLayout inquiryNumLayout;
	private LinearLayout purchaseNumLayout;
	private TextView inquiryNum;
	private TextView purchaseStatus;
	private TextView purchaseNum;
	private LinearLayout bannerLayout;

	private CBViewHolderCreator<BannerImageHolderView> bannerHolder = new CBViewHolderCreator<BannerImageHolderView>()
	{
		@Override
		public BannerImageHolderView createHolder()
		{
			return new BannerImageHolderView();
		}
	};

	public ServiceHeaderView(Activity activity, ArrayList<SystemPromotionBanner> dataList)
	{
		super(activity);
		this.mActivity = activity;
		this.dataList = dataList;
		initView();
		initViewData();
	}

	@SuppressWarnings("unchecked")
	protected void initView()
	{
		LayoutInflater.from(getContext()).inflate(R.layout.service_header_layout, this);
		inquiryLayout = (RelativeLayout) findViewById(R.id.rl_service_inquiry);
		purchaseLayout = (RelativeLayout) findViewById(R.id.rl_service_purchase);

		inquiryNumLayout = (LinearLayout) findViewById(R.id.ll_service_inquiry_num);
		inquiryNum = (TextView) findViewById(R.id.tv_inquiry_unread);
		purchaseNumLayout = (LinearLayout) findViewById(R.id.ll_service_purchase_num);
		purchaseStatus = (TextView) findViewById(R.id.tv_purchase_status);
		purchaseNum = (TextView) findViewById(R.id.tv_purchase_unread);

		bannerViewPager = (CirculateBanner<BannerImageHolderView>) findViewById(R.id.promotion_banner_viewpager);
		bannerLayout = (LinearLayout) findViewById(R.id.ll_service_banner);

		inquiryNumLayout.setOnClickListener(this);
		purchaseNumLayout.setOnClickListener(this);
		inquiryLayout.setOnClickListener(this);
		purchaseLayout.setOnClickListener(this);

		bannerLayout.setVisibility(View.VISIBLE);
		bannerViewPager.setPageAdapter(bannerHolder, dataList)
				// 设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
				.setPageIndicator(new int[]
				{ R.drawable.ic_banner_indicator, R.drawable.ic_banner_indicator_selected })
				// 设置指示器的方向
				.setPageIndicatorAlign(CirculateBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
				// 设置翻页的效果，不需要翻页效果可用不设
				.setPageTransformer(Transformer.DefaultTransformer)
				.setPointViewVisible(dataList != null && dataList.size() > 0);
		startBannerTurning();
	}

	public void stopBannerTurning()
	{
		if (bannerViewPager != null)
		{
			bannerViewPager.stopTurning();
		}
	}

	public void startBannerTurning()
	{
		if (bannerViewPager != null)
		{
			bannerViewPager.startTurning(3000);
		}
	}

	private void initViewData()
	{
		User user = SupplierApplication.getInstance().getUser();
		if (user != null)
		{
			// 显示未读询盘数量
			int unreadInquiryNum = 0;
			if (!Utils.isEmpty(user.content.userInfo.unreadMail))
			{
				unreadInquiryNum += Integer.parseInt(user.content.userInfo.unreadMail);
			}
			if (unreadInquiryNum != 0
					&& (user.content.userInfo.isManager() || user.content.userInfo.isReceiveInquiry()))
			{
				inquiryNum.setText(String.valueOf(unreadInquiryNum));
				inquiryNumLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				inquiryNumLayout.setVisibility(View.GONE);
			}
			// 显示未报价采购数量
			int unreadPurchaseNum = 0;
			if (!Utils.isEmpty(user.content.userInfo.unreadRFQ))
			{
				unreadPurchaseNum += Integer.parseInt(user.content.userInfo.unreadRFQ);
			}
			if (unreadPurchaseNum != 0
					&& (user.content.userInfo.isManager() || user.content.userInfo.isReceiveInquiry()))
			{
				purchaseNumLayout.setClickable(true);
				purchaseNum.setText(String.valueOf(unreadPurchaseNum));
				purchaseStatus.setText(R.string.unoffer_purchase);
				purchaseStatus.setTextColor(getResources().getColor(R.color.color_333333));
			}
			else
			{
				purchaseNumLayout.setClickable(false);
				purchaseNum.setVisibility(View.GONE);
				purchaseStatus.setText(R.string.see_purchase);
				purchaseStatus.setTextColor(getResources().getColor(R.color.color_999999));
			}

		}
		else
		{
			purchaseNumLayout.setClickable(false);
			inquiryNumLayout.setVisibility(View.GONE);
			purchaseStatus.setText(R.string.see_purchase);
			purchaseStatus.setTextColor(getResources().getColor(R.color.color_999999));
			purchaseNum.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.ll_service_inquiry_num:
			Intent intent = new Intent(mActivity, MessageActivity_.class);
			intent.putExtra(MessageConstantDefine.checkUnReadMessage.toString(), true);
			mActivity.startActivity(intent);
			MobclickAgent.onEvent(mActivity, "4");
			SysManager.analysis(R.string.c_type_click, R.string.c004);
			break;
		case R.id.rl_service_inquiry:
			mActivity.startActivity(new Intent(mActivity, MessageActivity_.class));
			MobclickAgent.onEvent(mActivity, "3");
			SysManager.analysis(R.string.c_type_click, R.string.c003);
			break;
		case R.id.rl_service_purchase:
			mActivity.startActivity(new Intent(mActivity, PurchaseActivity_.class));
			MobclickAgent.onEvent(mActivity, "5");
			SysManager.analysis(R.string.c_type_click, R.string.c005);
			break;
		case R.id.ll_service_purchase_num:
			Intent purchaseIntent = new Intent(mActivity, PurchaseActivity_.class);
			purchaseIntent.putExtra("fragment", "rfqneedquote");
			mActivity.startActivity(purchaseIntent);
			MobclickAgent.onEvent(mActivity, "6");
			SysManager.analysis(R.string.c_type_click, R.string.c006);
			break;
		}
	}

}
