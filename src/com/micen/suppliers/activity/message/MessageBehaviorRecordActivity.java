package com.micen.suppliers.activity.message;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.focustech.common.widget.viewpagerindictor.UnderlinePageIndicator;
import com.focustech.common.widget.viewpagerindictor.UnderlinePageIndicator.ChangeTitle;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.adapter.message.CommonFragmentAdapter;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageBehaviorRecords;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.message.BuyerComInfoMessageFragment;
import com.micen.suppliers.view.message.BuyerComInfoMessageFragment_;
import com.micen.suppliers.view.message.BuyerInquiryBetweenUsMessageFragment;
import com.micen.suppliers.view.message.BuyerInquiryBetweenUsMessageFragment_;
import com.micen.suppliers.view.message.BuyerRecentBehaviourMessageFragment;
import com.micen.suppliers.view.message.BuyerRecentBehaviourMessageFragment_;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class MessageBehaviorRecordActivity extends BaseFragmentActivity implements ChangeTitle, DisposeDataListener
{
	@ViewById(R.id.md_behavior_record_ulpi_line)
	protected UnderlinePageIndicator brUlpiLine;
	@ViewById(R.id.md_behavior_record_vp)
	protected ViewPager brVp;
	@ViewById(R.id.md_behavior_record_sort_tv_buy_info)
	protected TextView brSortTvBuyInfo;
	@ViewById(R.id.md_behavior_record_sort_tv_buy_behavior)
	protected TextView brSortTvBuyBehavior;
	@ViewById(R.id.md_behavior_record_sort_tv_buy_correspondence)
	protected TextView brSortTvBuyCorrespondence;
	@ViewById(R.id.broadcast_page_status)
	protected PageStatusView statusView;
	@ViewById(R.id.progress_bar)
	protected RelativeLayout progressBar;
	@ViewById(R.id.mbr_behavior_record_ll_sort)
	protected LinearLayout mbrBehaviorRecordLlSort;
	@ViewById(R.id.md_behavior_record_tv_contacts_name)
	protected TextView mbrTvContactsName;
	@ViewById(R.id.md_behavior_record_tv_contacts_city)
	protected TextView mbrTvContactsCity;
	@ViewById(R.id.md_behavior_record_iv_contacts_city_img)
	protected ImageView mbrIvContactsCityImg;

	/**
	 * 是否已经添加联系人
	 * true : 显示添加图标
	 * false: 显示删除图标
	 */
	private boolean isNeedToAdd = true;
	/**
	 * 是否是金牌会员
	 */
	private boolean isGoldMember = true;
	/**
	 * 联系人ID
	 */
	private String businessId;
	/**
	 * 甄别进入买家行为的入口
	 */
	private String sourceType;
	/**
	 * 买家行为记录入口甄别
	 * false：只显示删除按钮
	 */
	private boolean AllowAdditions;

	/**
	 * 可能会删除的pos
	 */
	private String deletePos;

	private ArrayList<Fragment> fragmentList;
	private CommonFragmentAdapter adapter;
	private CommonDialog dialog;
	private MessageBehaviorRecords messageBehaviorRecords;

	/**
	 * 界面加载说明：
	 * 1.先进性界面的绘制...数据添加前，界面隐藏
	 * 2.数据加载成功后，获取数据中的isContacPerson，判断titlebar中的图标显示
	 * 3.调用对应Fragment的setData方法...
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_behavior_record);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		fragmentList = new ArrayList<Fragment>();
		brVp.setOffscreenPageLimit(5);
		// 获取当前用户是否是高级会员
		isGoldMember = "30".equals(SupplierApplication.getInstance().getUser().content.companyInfo.csLevel) ? true
				: false;
		initIntent();
		initTitleBar();
		initFragmentList();
		initOnclickListen();
	}

	/**
	 * 初始化监听事件
	 */
	private void initOnclickListen()
	{
		brSortTvBuyInfo.setOnClickListener(this);
		brSortTvBuyBehavior.setOnClickListener(this);
		brSortTvBuyCorrespondence.setOnClickListener(this);
		llBack.setOnClickListener(this);
		btnFunction5.setOnClickListener(this);
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
					getContactsInfo();
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * 请求网络，获取买家信息
	 */
	public void getContactsInfo()
	{
		// 发送网络请求
		/**
		 * 参数：businessId，sourceType
		 */
		RequestCenter.getBuyerBehaviour(this, businessId, sourceType);
	}

	/**
	 * 绘制界面
	 */
	private void initFragmentList()
	{
		// Auto-generated method stub
		fragmentList.clear();
		fragmentList.add(new BuyerComInfoMessageFragment_());
		// 该部分需要判断用户是否是高级会员：高级会员显示已分配部分,并且将布局中的部分显示出来
		if (isGoldMember)
		{
			fragmentList.add(new BuyerRecentBehaviourMessageFragment_());
			brSortTvBuyBehavior.setVisibility(View.VISIBLE);
		}
		// 只显示全部邮件，并且隐藏相应头部
		fragmentList.add(new BuyerInquiryBetweenUsMessageFragment_());
		adapter = new CommonFragmentAdapter(this, getSupportFragmentManager(), fragmentList);
		// 设置ViewPage默认加载页
		brVp.setAdapter(adapter);
		brUlpiLine.setViewPager(brVp);
		brUlpiLine.setChangeTitleListener(this);
		brUlpiLine.setFades(false);
		brUlpiLine.setSelectedColor(getResources().getColor(R.color.page_indicator_title_selected));
	}

	/**
	 * 处理上一个界面传递过来的值
	 */
	private void initIntent()
	{

		businessId = getIntent().getStringExtra(MessageConstantDefine.businessId.toString());
		// 标识当前用户--详情页中的mailId
		sourceType = getIntent().getStringExtra(MessageConstantDefine.sourceType.toString());
		AllowAdditions = getIntent().getBooleanExtra(MessageConstantDefine.allowAdditions.toString(), true);

		if ("2".equals(sourceType))
		{
			deletePos = getIntent().getStringExtra(MessageConstantDefine.contactsPersion.toString());
		}
	}

	/**
	 * 初始化TitleBar部分
	 */
	private void initTitleBar()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		tvTitle.setVisibility(View.GONE);
	}

	private void setFunctionImage()
	{
		btnFunction5.setVisibility(View.VISIBLE);
		// 判断当前买家是否未该供应商用户的好友,根据数据返回的isContacPerson进行判断
		isNeedToAdd = "0".equals(messageBehaviorRecords.content.buyerComInfo.isContactPerson) ? true : false;
		if (AllowAdditions)
		{
			if (isNeedToAdd)
			{
				// 显示添加联系人图标
				btnFunction5.setImageResource(R.drawable.ic_behavior_contacts_add);
			}
			else
			{
				// 显示删除联系人图标,一期去除了询盘详情进入后提供删除功能
				// btnFunction5.setImageResource(R.drawable.ic_behavior_contacts_delete);
				btnFunction5.setVisibility(View.GONE);
			}
		}
		else
		{
			// 显示删除联系人图标
			btnFunction5.setImageResource(R.drawable.ic_behavior_contacts_delete);
		}
	}

	/**
	 * 界面切换时需要调用来改变标题部分的状态
	 * @param position
	 */
	@Override
	public void changeTitleStatus(int position)
	{
		switch (position)
		{
		case 0:
			((BuyerComInfoMessageFragment) adapter.getItem(position))
					.setData(messageBehaviorRecords.content.buyerComInfo);
			break;
		case 1:
			if (isGoldMember)
			{
				((BuyerRecentBehaviourMessageFragment) adapter.getItem(position))
						.setData(messageBehaviorRecords.content.buyerRecentBehaviour);
			}
			else
			{
				((BuyerInquiryBetweenUsMessageFragment) adapter.getItem(position))
						.setData(messageBehaviorRecords.content.inquiryBeetweenUs);
			}
			break;
		case 2:
			((BuyerInquiryBetweenUsMessageFragment) adapter.getItem(position))
					.setData(messageBehaviorRecords.content.inquiryBeetweenUs);
			break;
		}
	}

	@Override
	public void onClick(View v)
	{
		// Auto-generated method stub
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.md_behavior_record_sort_tv_buy_info:
			if (isNeedChangeCurrentItem(0))
			{
				brVp.setCurrentItem(0);
			}
			MobclickAgent.onEvent(MessageBehaviorRecordActivity.this, "54");
			SysManager.analysis(R.string.c_type_click, R.string.c054);
			break;
		case R.id.md_behavior_record_sort_tv_buy_behavior:
			if (isNeedChangeCurrentItem(1))
			{
				brVp.setCurrentItem(1);
			}
			MobclickAgent.onEvent(MessageBehaviorRecordActivity.this, "55");
			SysManager.analysis(R.string.c_type_click, R.string.c055);
			break;
		case R.id.md_behavior_record_sort_tv_buy_correspondence:
			if (!isGoldMember)
			{
				if (isNeedChangeCurrentItem(1))
				{
					brVp.setCurrentItem(1);
				}
			}
			else
			{
				if (isNeedChangeCurrentItem(2))
				{
					brVp.setCurrentItem(2);
				}
			}
			MobclickAgent.onEvent(MessageBehaviorRecordActivity.this, "56");
			SysManager.analysis(R.string.c_type_click, R.string.c056);
			break;
		case R.id.common_title_function5:
			if (AllowAdditions)
			{
				if (!isNeedToAdd)
				{
					// 删除操作
					delectTomakeSure();
				}
				else
				{
					// 添加联系人操作
					addContact();
				}

			}
			else
			{
				delectTomakeSure();
			}
			break;
		case R.id.common_ll_title_back:
			finish();
			break;
		default:
			break;
		}
	}

	// 判断当前页是否是需要展示的页面
	private boolean isNeedChangeCurrentItem(int pos)
	{
		return brVp.getCurrentItem() != pos;
	}

	/**
	 * 添加联系人操作
	 */
	private void addContact()
	{
		MobclickAgent.onEvent(MessageBehaviorRecordActivity.this, "53");
		SysManager.analysis(R.string.c_type_click, R.string.c053);
		// 进行添加请求
		RequestCenter.addContactPerson(addLisener, businessId);
		btnFunction5.setVisibility(View.GONE);
	}

	DisposeDataListener addLisener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			ToastUtil.toast(MessageBehaviorRecordActivity.this, getString(R.string.message_add_success));
			// 请求成功后，替换图标,
			// isNeedToAdd置为false
			isNeedToAdd = false;
			// 显示删除联系人图标,一期去除了询盘详情进入后提供删除功能
			// btnFunction5.setImageResource(R.drawable.ic_behavior_contacts_delete);
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			btnFunction5.setVisibility(View.VISIBLE);
			ToastUtil.toast(MessageBehaviorRecordActivity.this, anomalyMsg);
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			btnFunction5.setVisibility(View.VISIBLE);
			ToastUtil.toast(MessageBehaviorRecordActivity.this, anomalyMsg);
		}
	};

	/**
	 * 弹出删除确定框
	 */
	private void delectTomakeSure()
	{
		MobclickAgent.onEvent(MessageBehaviorRecordActivity.this, "64");
		SysManager.analysis(R.string.c_type_click, R.string.c064);
		// 弹框确认是否确定删除
		if (dialog != null)
		{
			dialog.show();
		}
		else
		{
			dialog = new CommonDialog(this);
			dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							// 删除成功后，执行退出操作...
							RequestCenter.deleteContactPerson(deleteLisener, businessId);
							btnFunction5.setVisibility(View.GONE);
						}
					}).buildSimpleDialog(getString(R.string.message_make_sure_to_delect_contacts));
		}
	}

	DisposeDataListener deleteLisener = new DisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			if ("2".equals(sourceType))
			{
				// 删除列表页数据
				Intent intent = new Intent();
				intent.putExtra(MessageConstantDefine.deleteContactsPosition.toString(), deletePos);
				intent.setAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
						+ MessageConstantDefine.deleteContacts.toString());
				sendBroadcast(intent);
			}
			activityFinishProtect();
			ToastUtil.toast(MessageBehaviorRecordActivity.this, getString(R.string.message_delect_success));
			finish();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			btnFunction5.setVisibility(View.VISIBLE);
			ToastUtil.toast(MessageBehaviorRecordActivity.this, anomalyMsg);
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			btnFunction5.setVisibility(View.VISIBLE);
			ToastUtil.toast(MessageBehaviorRecordActivity.this, anomalyMsg);
		}
	};

	@Override
	public void onSuccess(Object obj)
	{
		activityFinishProtect();
		messageBehaviorRecords = (MessageBehaviorRecords) obj;
		progressBar.setVisibility(View.GONE);
		if (messageBehaviorRecords.content != null)
		{
			mbrBehaviorRecordLlSort.setVisibility(View.VISIBLE);
			if (messageBehaviorRecords.content.buyerComInfo != null)
			{
				mbrTvContactsName.setText(messageBehaviorRecords.content.buyerComInfo.fullName);
				mbrTvContactsCity.setText(messageBehaviorRecords.content.buyerComInfo.country);
				if (!Utils.isEmpty(messageBehaviorRecords.content.buyerComInfo.countryImageUrl))
				{
					mbrIvContactsCityImg.setVisibility(View.VISIBLE);
					ImageUtil.getImageLoader().displayImage(
							messageBehaviorRecords.content.buyerComInfo.countryImageUrl, mbrIvContactsCityImg,
							ImageUtil.getCommonImageOptions());
				}
				else
				{
					mbrIvContactsCityImg.setVisibility(View.GONE);
				}
				changeTitleStatus(0);
				// 获取成功后，显示右上角图标
				setFunctionImage();
			}
		}
		else
		{
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageEmpty);
		}

	}

	@Override
	public void onDataAnomaly(Object failedReason)
	{
		activityFinishProtect();
		ToastUtil.toast(MessageBehaviorRecordActivity.this, failedReason);
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
		statusView.setMode(PageStatus.PageEmpty);
	}

	@Override
	public void onNetworkAnomaly(Object anomalyMsg)
	{
		activityFinishProtect();
		ToastUtil.toast(MessageBehaviorRecordActivity.this, anomalyMsg);
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
		statusView.setMode(PageStatus.PageNetwork);
	}

	private void activityFinishProtect()
	{
		if (MessageBehaviorRecordActivity.this.isFinishing())
			return;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10008));
		SysManager.analysis(R.string.p_type_page, R.string.p10008);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10008));
		MobclickAgent.onPause(this);
	}

}
