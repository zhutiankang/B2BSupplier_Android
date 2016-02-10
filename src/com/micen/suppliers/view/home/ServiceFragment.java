package com.micen.suppliers.view.home;

import java.util.ArrayList;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.universalimageloader.core.ImageLoader;
import com.focustech.common.universalimageloader.core.assist.FailReason;
import com.focustech.common.universalimageloader.core.listener.ImageLoadingListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshListView;
import com.google.zxing.demo.CaptureActivity;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.activity.setting.PersonalActivity_;
import com.micen.suppliers.activity.setting.SettingActivity_;
import com.micen.suppliers.adapter.SystemPromotionListAdapter;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.WebViewType;
import com.micen.suppliers.module.promotion.SystemPromotion;
import com.micen.suppliers.module.promotion.SystemPromotionBanner;
import com.micen.suppliers.module.promotion.SystemPromotionDiscovery;
import com.micen.suppliers.module.user.User;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.umeng.analytics.MobclickAgent;


@EFragment(R.layout.service_fragment_layout)
public class ServiceFragment extends HomeBaseFragment implements OnRefreshListener2<ListView>
{
	@ViewById(R.id.iv_user_logo)
	protected ImageView ivUserLogo;
	@ViewById(R.id.tv_user_name)
	protected TextView tvUserName;
	@ViewById(R.id.rl_setting)
	protected RelativeLayout btnSetting;
	@ViewById(R.id.iv_newversion_flag)
	protected ImageView ivVersionFlag;
	@ViewById(R.id.rf_service_list)
	protected PullToRefreshListView plServiceList;
	@ViewById(R.id.iv_scan)
	protected ImageView ivScan;

	private ListView listView;
	private ServiceHeaderView serviceHeaderView;
	private SystemPromotionListAdapter adapter;

	public ServiceFragment()
	{

	}

	@Override
	protected void initView()
	{
		ivUserLogo.setImageResource(R.drawable.ic_head_default);
		ivUserLogo.setOnClickListener(this);
		btnSetting.setOnClickListener(this);
		ivScan.setOnClickListener(this);
		// 不启动任何刷新
		plServiceList.setMode(Mode.PULL_FROM_START);
		// 不显示指示器
		plServiceList.setShowIndicator(false);
		plServiceList.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
		plServiceList.setOnPullEventListener(pullEventListener);
		plServiceList.setOnRefreshListener(this);
		listView = plServiceList.getRefreshableView();
		listView.setOnItemClickListener(itemClick);
		initUserMsg();

		if (serviceHeaderView != null)
			listView.removeHeaderView(serviceHeaderView);
		if (listView.getHeaderViewsCount() == 0)
		{
			serviceHeaderView = new ServiceHeaderView(getActivity(), new ArrayList<SystemPromotionBanner>());
			listView.addHeaderView(serviceHeaderView);
		}
		
		adapter = new SystemPromotionListAdapter(getActivity(), new ArrayList<SystemPromotionDiscovery>());
		listView.setAdapter(adapter);

		requestSystemMessage();
		if (!SharedPreferenceManager.getInstance().getBoolean("isClickSetting", false)
				&& SharedPreferenceManager.getInstance().getBoolean("isHaveNewVersion", false))
		{
			ivVersionFlag.setVisibility(View.VISIBLE);
		}
		else
		{
			ivVersionFlag.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();
		if (serviceHeaderView != null)
		{
			serviceHeaderView.stopBannerTurning();
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (serviceHeaderView != null)
		{
			serviceHeaderView.startBannerTurning();
		}
	}

	private void initUserMsg()
	{
		if (SupplierApplication.getInstance().getUser() != null)
		{
			tvUserName.setText(getString(R.string.hello)
					+ SupplierApplication.getInstance().getUser().content.userInfo.fullName);

			initHeadLogo();
		}
	}

	private void initHeadLogo()
	{
		ImageUtil.getImageLoader().displayImage(SupplierApplication.getInstance().getUser().content.companyInfo.logo,
				ivUserLogo, new ImageLoadingListener()
				{
					@Override
					public void onLoadingStarted(String imageUri, View view)
					{

					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason)
					{
						ivUserLogo.setImageResource(R.drawable.ic_head_default);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
					{
						if (loadedImage != null)
						{
							ivUserLogo.setImageBitmap(ImageUtil.toRoundBitmap(loadedImage));
						}
						else
						{
							ivUserLogo.setImageResource(R.drawable.ic_head_default);
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view)
					{

					}
				});
	}

	private OnItemClickListener itemClick = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			MobclickAgent.onEvent(getActivity(), "8");
			SysManager.analysis(R.string.c_type_click, R.string.c008);
			SystemPromotionDiscovery module = (SystemPromotionDiscovery) arg0.getAdapter().getItem(arg2);
			Intent intent = new Intent(getActivity(), WebViewActivity_.class);
			intent.putExtra("targetUri", module.detailUrl);
			intent.putExtra("targetType", WebViewType.getValue(WebViewType.Discovery));
			startActivity(intent);
		}
	};

	private void requestSystemMessage()
	{
		RequestCenter.getSystemPromotion(sysPromotionListener);
	}

	private DisposeDataListener sysPromotionListener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			refreshComplete();
			SystemPromotion module = (SystemPromotion) obj;
			if (serviceHeaderView != null)
			{
				serviceHeaderView.stopBannerTurning();
				listView.removeHeaderView(serviceHeaderView);
			}
			if (listView.getHeaderViewsCount() == 0)
			{
				serviceHeaderView = new ServiceHeaderView(getActivity(), module.content.banner);
				listView.addHeaderView(serviceHeaderView);
			}
			adapter = new SystemPromotionListAdapter(getActivity(), module.content.recommend);
			listView.setAdapter(adapter);
		}

		public void onFailure(Object failedMsg)
		{
			ToastUtil.toast(getActivity(), R.string.request_no_internet);
			refreshComplete();
		}
	};

	/**
	 * 刷新完成
	 */
	private void refreshComplete()
	{
		if (plServiceList != null)
		{
			plServiceList.onRefreshComplete();
		}
	}

	private OnPullEventListener<ListView> pullEventListener = new OnPullEventListener<ListView>()
	{
		@Override
		public void onPullEvent(PullToRefreshBase<ListView> refreshView, State state, Mode direction)
		{
			switch (direction)
			{
			case PULL_FROM_START:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
				break;
			default:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
				break;
			}
		}
	};

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
	{
		Util.saveChildLastRefreshTime(getActivity(), getFragmentTag());
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
		RequestCenter.profile(profileListener);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
	{

	}

	private DisposeDataListener profileListener = new SimpleDisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			requestSystemMessage();
			SupplierApplication.getInstance().setUser((User) obj);
			RequestCenter.boundAccount(new SimpleDisposeDataListener(), true);
			initUserMsg();
		}

		public void onFailure(Object failedMsg)
		{
			ToastUtil.toast(getActivity(), R.string.request_no_internet);
			requestSystemMessage();
		}
	};

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.iv_user_logo:
			startActivity(new Intent(getActivity(), PersonalActivity_.class));
			MobclickAgent.onEvent(getActivity(), "1");
			SysManager.analysis(R.string.c_type_click, R.string.c001);
			break;
		case R.id.rl_setting:
			MobclickAgent.onEvent(getActivity(), "2");
			SysManager.analysis(R.string.c_type_click, R.string.c002);
			startActivity(new Intent(getActivity(), SettingActivity_.class));
			SharedPreferenceManager.getInstance().putBoolean("isClickSetting", true);
			ivVersionFlag.setVisibility(View.GONE);
			break;
		case R.id.iv_scan:
			getActivity().startActivityForResult((new Intent(getActivity(), CaptureActivity.class)), 1);
			break;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10001));
		SysManager.analysis(R.string.p_type_page, R.string.p10001);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10001));
	}

	@Override
	protected String getFragmentTag()
	{
		return ServiceFragment.class.getName();
	}

}
