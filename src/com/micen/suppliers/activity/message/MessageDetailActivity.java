package com.micen.suppliers.activity.message;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.baidutranslate.openapi.TranslateClient;
import com.baidu.baidutranslate.openapi.callback.IDictResultCallback;
import com.baidu.baidutranslate.openapi.entity.DictResult;
import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.universalimageloader.core.ImageLoader;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.activity.product.ProductDetailActivity_;
import com.micen.suppliers.adapter.message.MessageSalesmanListAdapter;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.message.MessageContent;
import com.micen.suppliers.module.message.MessageDetails;
import com.micen.suppliers.module.message.MessageSalesman;
import com.micen.suppliers.module.message.MessageSalesmans;
import com.micen.suppliers.module.message.MessageSendTarget;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.umeng.analytics.MobclickAgent;

@EActivity
public class MessageDetailActivity extends BaseActivity
{
	@ViewById(R.id.rl_common_title)
	protected RelativeLayout messageTitleRl;
	// User
	@ViewById(R.id.message_detail_user_tv_behavior_record)
	protected TextView userTvBehaviorRecord;
	@ViewById(R.id.message_detail_user_tv_other_people)
	protected TextView userTvOtherPeople;
	@ViewById(R.id.message_detail_user_tv_other_people_name)
	protected TextView userTvOtherPeopleName;
	@ViewById(R.id.message_detail_user_tv_other_people_location_content)
	protected TextView userTvotherPeopleLocationContent;
	@ViewById(R.id.message_detail_user_ll_other_people_location)
	protected LinearLayout userLLOtherPeopleLocation;
	@ViewById(R.id.message_detail_user_iv_other_people_country_image)
	protected ImageView userIvOtherPeopleCountryImage;
	@ViewById(R.id.message_detail_user_rl_other_people_country_image)
	protected RelativeLayout userrlOtherPeopleCountryImage;
	@ViewById(R.id.message_detail_user_tv_our)
	protected TextView userTvOur;
	@ViewById(R.id.message_detail_user_tv_our_name)
	protected TextView userTvOurName;
	// Subject
	@ViewById(R.id.message_detail_subject_tv_title)
	protected TextView subjectTvTitle;
	@ViewById(R.id.message_detail_subject_tv_date)
	protected TextView subjectTvDate;
	// Product
	@ViewById(R.id.message_detail_product_iv_thumb)
	protected ImageView productIvThumb;
	@ViewById(R.id.message_detail_product_tv_product_name)
	protected TextView productTvProductName;
	@ViewById(R.id.message_detail_product_ll_interest)
	protected LinearLayout productLlInterest;
	@ViewById(R.id.message_detail_product_ll)
	protected LinearLayout productLl;

	@ViewById(R.id.message_detail_attachment_ll)
	protected LinearLayout attachmentLl;

	// 邮件主体内容
	@ViewById(R.id.message_detail_tv_content)
	protected TextView tvContent;

	@ViewById(R.id.message_details_progressbar)
	protected RelativeLayout progressBar;
	@ViewById(R.id.message_detail_scroll)
	protected ScrollView messageDetailScroll;
	@ViewById(R.id.message_detail_ll)
	protected LinearLayout messageDetailLl;
	@ViewById(R.id.broadcast_page_status)
	protected PageStatusView statusView;
	@ViewById(R.id.message_attachment_hl)
	protected LinearLayout attachmentHl;

	// 获取邮件详情的请求参数
	private List<MessageContent> mContentList;
	protected CommonDialog distributeDialog;
	protected ArrayList<MessageSalesman> dataList;
	protected MessageSalesmanListAdapter salesmanAdapter;
	private String operatorId = "";
	protected ArrayList<MessageSalesman> salesmanList;
	private MessageSalesmans salesmans;
	private MessageDetails details;
	/**
	 * 标识收件箱/发件箱
	 * 0:收件箱,1：发件箱
	 */
	private String action = "0";
	/**
	 * 当前邮件的位置，以便来查看上一封或下一封邮件
	 */
	private int currentPosition;

	private String mailId = "";

	// startActivityForResult标识

	private TranslateClient client;
	private String fromLang = "en";// 源语言
	private String toLang = "zh";// 目标语言
	private int clickoffset;

	/**
	 * false:显示所有内容
	 * true :屏蔽相关不需要显示的功能
	 * 屏蔽上一封、下一封、分配、回复、删除、买家行为记录
	 */
	private boolean isNeedToShieldFunction = false;
	/**
	 * false:不屏蔽上一封，下一封
	 * true:屏蔽上一封，下一封显示
	 */
	private boolean isPurchase = false;
	/**
	 *是否允许删除
	 */
	private boolean isAllowDelete = false;

	/**
	 * 用户所分配的业务员
	 */
	private int lastIndex = -1;

	@Extra("isFromBroadcast")
	protected boolean isFromBroadcast = false;
	@Extra("messageId")
	protected String messageId;

	private boolean isGoldMember = false;
	private boolean isSaleman = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);
		initNavigationBarStyle(true);
	}

	@AfterViews
	protected void initView()
	{
		MobclickAgent.openActivityDurationTrack(false);
		initData();
		initTitleBar();
		getMessageData();
		initViewClick();
		client = new TranslateClient(this, Constants.API_KEY);

		if (isFromBroadcast && !Utils.isEmpty(messageId))
		{
			RequestCenter.updateMesssageStatus(messageId);
		}

	}

	@SuppressLint("ClickableViewAccessibility")
	private void initViewClick()
	{
		productLlInterest.setOnClickListener(this);
		userTvBehaviorRecord.setOnClickListener(this);
		attachmentLl.setOnClickListener(this);
		tvContent.setOnClickListener(this);
		tvContent.setOnTouchListener(onTouchListener);
		statusView.setLinkOrRefreshOnClickListener(linkClickListener);
	}

	/**
	 * 获取邮件详细信息数据,获取成功后，将message_detail_scroll部分显示,否则给出错误信息
	 */
	private void getMessageData()
	{
		RequestCenter.getMessageDetail(getMessageDetailListener, mailId, action);
	}

	private LinkClickListener linkClickListener = new LinkClickListener()
	{
		@Override
		public void onClick(PageStatus status)
		{
			switch (status)
			{
			case PageEmpty:
				break;
			case PageNetwork:
				statusView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				getMessageData();
				break;
			default:
				break;
			}
		}
	};

	private OnTouchListener onTouchListener = new OnTouchListener()
	{
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			if (event.getAction() == 0)
			{
				Layout layout = ((TextView) v).getLayout();
				int x = (int) event.getX();
				int y = (int) event.getY();
				int m = v.getPaddingTop();
				int j = v.getPaddingLeft();
				if (layout != null)
				{
					int line = layout.getLineForVertical(y - m);
					int offset = layout.getOffsetForHorizontal(line, x - j);
					clickoffset = offset;
					Log.e("index", "" + offset);
					// Toast.makeText(MainActivity.this, "" + offset, Toast.LENGTH_LONG).show();
				}
			}

			return false;
		}
	};

	/**
	 *1.共有部分：subjectTvTitle，subjectTvDate
	 *显示时间时，需要将字符串类型的时间进行转换
	 *Util.formatDate("1437549763646", "yyyy-MM-dd HH:mm:ss")
	 * 
	 *2.获取数据成功后，根据当前action的值判断需要给哪些控件赋值
	 *action = 0:收件箱 
	 *userTvOtherPeopleName,userTvOtherPeopleLocationContent,userIvOtherPeopleCountryImage
	 *action = 1:发件箱 
	 *userTvOtherPeopleName
	 *
	 *3.需要判断是否有产品信息和缩略图	------针对于收件箱 action = 0
	 *productLlInterest是否显示：productIvThumb,productTvProductName
	 *
	 *4.判断是否有附件(有附件的情景下，给予温馨提示) ------针对发件箱 action = 1
	 *AttachmentLl是否显示
	 *
	 *5.邮件主体：contentTv
	 */
	DisposeDataListener getMessageDetailListener = new SimpleDisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			progressBar.setVisibility(View.GONE);
			messageDetailScroll.setVisibility(View.VISIBLE);
			initTitleBarFunction();
			initUserView();
			details = null;
			details = (MessageDetails) obj;
			if (details.content != null)
			{
				subjectTvTitle.setText(details.content.subject);
				if (!Utils.isEmpty(details.content.date))
				{
					subjectTvDate.setText(Util.formatDate(details.content.date, "yyyy-MM-dd HH:mm:ss") + "("
							+ Util.getWeekStr(details.content.date) + ")");
					subjectTvDate.setVisibility(View.VISIBLE);
				}
				else
				{
					subjectTvDate.setVisibility(View.GONE);
				}
				operatorId = details.content.allocateOperatorId;
				if ("0".equals(action))
				{
					startSendBroadCast(MessageConstantDefine.read.toString());
					userTvOtherPeopleName.setText(details.content.sender.fullName);
					userTvotherPeopleLocationContent.setText(details.content.sender.countryNameCn);

					if ("00".equals(details.content.receiver.operatorId)
							|| !SupplierApplication.getInstance().getUser().content.userInfo.isManager())
					{
						userTvOurName.setText("我");
						userTvOurName.setTextColor(getResources().getColor(R.color.color_333333));
					}
					else
					{
						userTvOurName.setText(details.content.receiver.fullName);
						userTvOurName.setTextColor(getResources().getColor(R.color.color_ccb895));
					}

					if (!Utils.isEmpty(details.content.sender.countryImageUrl))
					{
						userrlOtherPeopleCountryImage.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(details.content.sender.countryImageUrl,
								userIvOtherPeopleCountryImage, ImageUtil.getCommonImageOptions());
					}
					else
					{
						userrlOtherPeopleCountryImage.setVisibility(View.INVISIBLE);
					}
					if (Utils.isEmpty(details.content.productId))
					{
						productLl.setVisibility(View.GONE);
					}
					else
					{
						productLl.setVisibility(View.VISIBLE);
						if (Utils.isEmpty(details.content.productImageUrl))
						{
							productIvThumb.setImageResource(R.drawable.bg_message_product_default);
						}
						else
						{
							ImageLoader.getInstance().displayImage(details.content.productImageUrl, productIvThumb,
									ImageUtil.getMessageProductImageOptions());
						}
						productTvProductName.setText(details.content.productName);
					}
				}
				else
				{
					userTvOtherPeopleName.setText(details.content.receiver.fullName);

					if ("00".equals(details.content.sender.operatorId)
							|| !SupplierApplication.getInstance().getUser().content.userInfo.isManager())
					{
						userTvOurName.setText("我");
						userTvOurName.setTextColor(getResources().getColor(R.color.color_333333));
					}
					else
					{
						userTvOurName.setText(details.content.sender.fullName);
						userTvOurName.setTextColor(getResources().getColor(R.color.color_ccb895));
					}
				}
				// 先隐藏，如果出现非图片附件，再显示
				attachmentHl.removeAllViews();
				attachmentHl.setVisibility(View.GONE);
				attachmentLl.setVisibility(View.GONE);
				if (details.content.attachments.size() != 0)
				{
					for (int i = 0; i < details.content.attachments.size(); i++)
					{
						if ("1".equals(details.content.attachments.get(i).isImg))
						{
							// TODO 设置加载附件图片
							attachmentHl.setVisibility(View.VISIBLE);
							ImageView imageView = new ImageView(MessageDetailActivity.this);
							RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
							imageView.setPadding(0, Utils.toDip(MessageDetailActivity.this, 20), 0, 0);
							imageView.setLayoutParams(layoutParams);
							imageView.setAdjustViewBounds(true);
							ImageLoader.getInstance().displayImage(details.content.attachments.get(i).imgUrl,
									imageView, ImageUtil.getInquiryImageLargerStubOptions(MessageDetailActivity.this));
							attachmentHl.addView(imageView);
						}
						else
						{
							attachmentLl.setVisibility(View.VISIBLE);
						}
					}
				}
				tvContent.setText(details.content.mailContent);
			}
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
			finish();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
			progressBar.setVisibility(View.GONE);
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageNetwork);
		}
	};

	/**
	 * 检验邮件的上一封和下一封按钮状态
	 */
	private void initMessageState()
	{
		if (mContentList.size() > 1)
		{
			if (currentPosition == mContentList.size() - 1)
			{
				btnFunction2.setEnabled(false);
				btnFunction2.setImageResource(R.drawable.ic_title_next_gray);
				btnFunction1.setEnabled(true);
				btnFunction1.setImageResource(R.drawable.ic_title_previous_blue);
			}
			else if (currentPosition == 0)
			{
				btnFunction2.setEnabled(true);
				btnFunction2.setImageResource(R.drawable.ic_title_next_blue);
				btnFunction1.setEnabled(false);
				btnFunction1.setImageResource(R.drawable.ic_title_previous_gray);
			}
			else
			{
				btnFunction2.setEnabled(true);
				btnFunction2.setImageResource(R.drawable.ic_title_next_blue);
				btnFunction1.setEnabled(true);
				btnFunction1.setImageResource(R.drawable.ic_title_previous_blue);
			}
		}
		else
		{
			btnFunction2.setEnabled(false);
			btnFunction2.setImageResource(R.drawable.ic_title_next_gray);
			btnFunction1.setEnabled(false);
			btnFunction1.setImageResource(R.drawable.ic_title_previous_gray);
		}
	}

	/**
	 * 获取上一个界面传递的数据
	 * 获取邮件详细信息需要mailId和action两个参数
	 */
	private void initData()
	{
		isGoldMember = "30".equals(SupplierApplication.getInstance().getUser().content.companyInfo.csLevel) ? true
				: false;
		isSaleman = SupplierApplication.getInstance().getUser().content.userInfo.isManager() ? false : true;
		userTvOtherPeopleName.setMaxWidth(Utils.toDip(MessageDetailActivity.this, 130));
		userTvotherPeopleLocationContent.setMaxWidth(Utils.toDip(MessageDetailActivity.this, 175));
		Intent intent = getIntent();
		action = intent.getStringExtra(MessageConstantDefine.action.toString());
		// 获取到message对象
		isPurchase = intent.getBooleanExtra(MessageConstantDefine.isPurchase.toString(), false);
		isAllowDelete = intent.getBooleanExtra(MessageConstantDefine.isAllowDelete.toString(), false);
		if (!isPurchase)
		{
			isNeedToShieldFunction = intent.getBooleanExtra(MessageConstantDefine.isNeedToShieldFunction.toString(),
					false);
			// 通过当前的position获取到messageContentList 中的mailId
			if (!isNeedToShieldFunction)
			{
				currentPosition = Integer.parseInt(intent.getStringExtra("position"));
				mContentList = intent.getParcelableArrayListExtra("mailDataList");
				mailId = mContentList.get(currentPosition).mailId;
				operatorId = mContentList.get(currentPosition).allocateOperatorId;
			}
			else
			{
				mailId = intent.getStringExtra(MessageConstantDefine.mailId.toString());
			}
		}
		else
		{
			mailId = intent.getStringExtra(MessageConstantDefine.mailId.toString());
		}
	}

	/**
	 * 初始化界面内容：收件人/发件人
	 */
	private void initUserView()
	{

		if ("0".equals(action))
		{
			// 显示收件箱用户信息
			userTvOtherPeople.setText(R.string.message_detail_sender);
			userTvOur.setText(R.string.message_detail_receiver);
			userLLOtherPeopleLocation.setVisibility(View.VISIBLE);
		}
		else
		{
			// 显示发件箱用户信息
			userTvOtherPeople.setText(R.string.message_detail_receiver);
			userLLOtherPeopleLocation.setVisibility(View.GONE);
			userTvOur.setText(R.string.message_detail_sender);
		}

		if (isNeedToShieldFunction)
		{
			userTvBehaviorRecord.setVisibility(View.GONE);
		}
		else
		{
			userTvBehaviorRecord.setVisibility(View.VISIBLE);

		}

	}

	/**
	 * 初始化当前界面Title
	 */
	private void initTitleBar()
	{
		userTvBehaviorRecord.setBackgroundResource(R.drawable.btn_white_common_btn);
		messageTitleRl.setBackgroundColor(getResources().getColor(R.color.color_ffffff));
		btnBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(this);
		llBack.setBackgroundResource(R.drawable.btn_white_common_btn);
		tvTitle.setVisibility(View.GONE);
	}

	private void initTitleBarFunction()
	{
		if (isPurchase)
		{
			if (isGoldMember && !isSaleman && "0".equals(action))
			{
				btnFunction3.setVisibility(View.VISIBLE);
				btnFunction3.setOnClickListener(this);
				btnFunction3.setBackgroundResource(R.drawable.btn_white_common_btn);
				btnFunction3.setImageResource(R.drawable.ic_title_allocation_blue);
			}
			btnFunction4.setVisibility(View.VISIBLE);
			btnFunction4.setOnClickListener(this);
			btnFunction4.setBackgroundResource(R.drawable.btn_white_common_btn);
			btnFunction4.setImageResource(R.drawable.ic_title_reply_blue);
			if (isAllowDelete)
			{
				btnFunction5.setVisibility(View.VISIBLE);
				btnFunction5.setOnClickListener(this);
				btnFunction5.setBackgroundResource(R.drawable.btn_white_common_btn);
				btnFunction5.setImageResource(R.drawable.ic_title_blue_delete);
			}

		}
		else
		{

			if (!isNeedToShieldFunction)
			{
				if ("0".equals(action))
				{
					if (isGoldMember && !isSaleman)
					{
						btnFunction3.setVisibility(View.VISIBLE);
						btnFunction3.setOnClickListener(this);
						btnFunction3.setBackgroundResource(R.drawable.btn_white_common_btn);
						btnFunction3.setImageResource(R.drawable.ic_title_allocation_blue);
					}
					btnFunction4.setVisibility(View.VISIBLE);
					btnFunction4.setOnClickListener(this);
					btnFunction4.setBackgroundResource(R.drawable.btn_white_common_btn);
					btnFunction4.setImageResource(R.drawable.ic_title_reply_blue);
				}
				btnFunction1.setVisibility(View.VISIBLE);
				btnFunction1.setOnClickListener(this);
				btnFunction1.setBackgroundResource(R.drawable.btn_white_common_btn);
				btnFunction1.setImageResource(R.drawable.ic_title_previous_blue);

				btnFunction2.setVisibility(View.VISIBLE);
				btnFunction2.setOnClickListener(this);
				btnFunction2.setBackgroundResource(R.drawable.btn_white_common_btn);
				btnFunction2.setImageResource(R.drawable.ic_title_next_blue);
				btnFunction5.setVisibility(View.VISIBLE);
				btnFunction5.setOnClickListener(this);
				btnFunction5.setBackgroundResource(R.drawable.btn_white_common_btn);
				btnFunction5.setImageResource(R.drawable.ic_title_blue_delete);
				// 初始化邮件的上一封，下一封...
				initMessageState();
			}
		}
	}

	/**
	 * 删除收件箱或者发件箱中的邮件
	 * @param mailId	邮件ID，多个以","隔开
	 */
	public void deleteCurrentMessage()
	{
		// 需要传递参数：companyId，operatorId，mailId，action，versionCode，
		CommonProgressDialog.getInstance().showProgressDialog(this, getString(R.string.mic_loading));
		RequestCenter.deleteMessage(simpleDisposeDataListener, mailId, action);
	}

	private void startSendBroadCast(String tag)
	{
		Intent intent = new Intent();
		intent.putExtra(tag + MessageConstantDefine.mailId.toString(), mailId);
		intent.setAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag) + tag);
		sendBroadcast(intent);
	}

	private void startSendDistributedBroadCast()
	{
		Intent intent = new Intent();
		intent.putExtra(MessageConstantDefine.distributed.toString() + MessageConstantDefine.mailId.toString(), mailId);
		intent.putExtra(MessageConstantDefine.distributedOperatorId.toString(), salesmanList.get(lastIndex).operatorId);
		intent.setAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag)
				+ MessageConstantDefine.distributed.toString());
		sendBroadcast(intent);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			back();
			break;
		// 查看上一份邮件
		case R.id.common_title_function1:
			if (currentPosition > -1)
			{
				currentPosition = currentPosition - 1;
				mailId = mContentList.get(currentPosition).mailId;
				initSeleteMessageDetail();
			}
			MobclickAgent.onEvent(MessageDetailActivity.this, "20");
			SysManager.analysis(R.string.c_type_click, R.string.c020);
			break;
		// 查看下一份邮件
		case R.id.common_title_function2:
			if (currentPosition < mContentList.size())
			{
				currentPosition = currentPosition + 1;
				mailId = mContentList.get(currentPosition).mailId;
				initSeleteMessageDetail();
			}
			MobclickAgent.onEvent(MessageDetailActivity.this, "21");
			SysManager.analysis(R.string.c_type_click, R.string.c021);
			break;

		case R.id.common_title_function3:
			getDistributeSalesman();
			MobclickAgent.onEvent(MessageDetailActivity.this, "27");
			SysManager.analysis(R.string.c_type_click, R.string.c027);
			break;
		case R.id.common_title_function4:
			// 回复跳转
			Intent intent = new Intent(this, MessageSendActivity_.class);
			intent.putExtra("replySubject", subjectTvTitle.getText().toString());
			intent.putExtra("replyReceive", userTvOtherPeopleName.getText().toString());
			intent.putExtra("replyMailId", mailId);
			intent.putExtra(MessageConstantDefine.messageSendTarget.toString(),
					MessageSendTarget.getValue(MessageSendTarget.Reply));
			startActivity(intent);
			MobclickAgent.onEvent(MessageDetailActivity.this, "26");
			SysManager.analysis(R.string.c_type_click, R.string.c026);
			break;
		case R.id.common_title_function5:
			CommonDialog dialog = new CommonDialog(this);
			dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							// 事件统计47 删除询盘（收件箱询盘详情页） 点击事件
							// SysManager.analysis(R.string.a_type_click, R.string.c47);
							deleteCurrentMessage();
						}
					}).buildSimpleDialog(getString(R.string.message_make_sure_to_delect));
			if ("0".equals(action))
			{
				MobclickAgent.onEvent(MessageDetailActivity.this, "25");
				SysManager.analysis(R.string.c_type_click, R.string.c025);
			}
			else
			{
				MobclickAgent.onEvent(MessageDetailActivity.this, "50");
				SysManager.analysis(R.string.c_type_click, R.string.c050);
			}
			break;

		case R.id.message_detail_user_tv_behavior_record:
			// 根据当前是收件箱还是发件箱获取相关的联系人ID
			startActivity(new Intent(MessageDetailActivity.this, MessageBehaviorRecordActivity_.class).putExtra(
					MessageConstantDefine.businessId.toString(), mailId).putExtra(
					MessageConstantDefine.sourceType.toString(), action));
			if ("0".equals(action))
			{
				MobclickAgent.onEvent(MessageDetailActivity.this, "23");
				SysManager.analysis(R.string.c_type_click, R.string.c023);
			}
			else
			{
				MobclickAgent.onEvent(MessageDetailActivity.this, "52");
				SysManager.analysis(R.string.c_type_click, R.string.c052);
			}
			break;
		// 产品名+产品缩略图部分
		case R.id.message_detail_product_ll_interest:
			if ("1".equals(details.content.viewFlag))
			{
				// 跳转到产品详情页,需要传值productId
				Intent productIntent = new Intent(MessageDetailActivity.this, ProductDetailActivity_.class);
				productIntent.putExtra("productId", details.content.productId);
				startActivity(productIntent);
			}
			else
			{
				ToastUtil.toast(MessageDetailActivity.this, details.content.productStatus);
			}
			MobclickAgent.onEvent(MessageDetailActivity.this, "28");
			SysManager.analysis(R.string.c_type_click, R.string.c028);
			break;

		case R.id.message_detail_tv_content:

			final int[] postion = getWholeWord(tvContent, clickoffset);
			if (postion != null)
				if (postion[1] > postion[0])
				{
					translate((String) tvContent.getText().subSequence(postion[0], postion[1]));
					// oast.makeText(MainActivity.this, tv_content.getText().subSequence(postion[0], postion[1]),
					// Toast.LENGTH_SHORT).show();
				}
			break;
		case R.id.message_detail_attachment_ll:
			MobclickAgent.onEvent(MessageDetailActivity.this, "24");
			SysManager.analysis(R.string.c_type_click, R.string.c024);
			break;
		default:
			break;
		}
	}

	/**
	 * 点击上一封下一封时初始化界面状态
	 */
	private void initSeleteMessageDetail()
	{
		messageDetailScroll.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		btnFunction1.setVisibility(View.GONE);
		btnFunction2.setVisibility(View.GONE);
		btnFunction3.setVisibility(View.GONE);
		btnFunction4.setVisibility(View.GONE);
		btnFunction5.setVisibility(View.GONE);
		getMessageData();
	}

	/**
	 * 分配操作
	 */
	private void getDistributeSalesman()
	{
		// 获取网络数据,指定operatorId
		CommonProgressDialog.getInstance().showCancelableProgressDialog(this, this.getString(R.string.mic_loading));
		RequestCenter.getSalesman(getSalesmanListener);
	}

	SimpleDisposeDataListener simpleDisposeDataListener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			startSendBroadCast(MessageConstantDefine.delete.toString());
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(MessageDetailActivity.this, getString(R.string.message_make_sure_to_delect_success));
			finish();
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
			CommonProgressDialog.getInstance().dismissProgressDialog();

		}
	};

	DisposeDataListener getSalesmanListener = new SimpleDisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			activityFinishProtect();
			if (null != distributeDialog && distributeDialog.isShowing())
				distributeDialog.dismiss();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			salesmans = (MessageSalesmans) obj;
			if (salesmans.content != null && salesmans.content.size() != 0)
			{
				salesmanList = new ArrayList<MessageSalesman>();
				salesmanList.clear();
				for (int i = 0; i < salesmans.content.size(); i++)
				{
					salesmanList.add(salesmans.content.get(i));
				}
				salesmanAdapter = new MessageSalesmanListAdapter(salesmanList, MessageDetailActivity.this, operatorId);
				LayoutInflater inflater = LayoutInflater.from(MessageDetailActivity.this);
				View view = inflater.inflate(R.layout.dialog_message_allocation, null);
				ListView distributedListView = (ListView) view.findViewById(R.id.message_allocation_pop_lv);
				LayoutParams params = distributedListView.getLayoutParams();
				params.width = LayoutParams.MATCH_PARENT;

				if (salesmanList.size() < 8)
				{
					params.height = LayoutParams.WRAP_CONTENT;
				}
				else
				{
					params.height = Utils.toDip(MessageDetailActivity.this, 365);
				}
				distributedListView.setAdapter(salesmanAdapter);
				distributedListView.setOnItemClickListener(onItemClickListener);
				distributeDialog = new CommonDialog(MessageDetailActivity.this);
				distributeDialog.setCancelBtnText(getString(R.string.cancel)).setDialogConfirmBtnCamcel(false)
						.setConfirmBtnText(getString(R.string.confirm)).setDialogContentView(view)
						.setCancelDialogListener(dialogClickListener).setConfirmDialogListener(confirmClickListener)
						.build();
				distributeDialog.setOnDismissListener(dismissListener);
			}
			else
			{
				ToastUtil.toast(MessageDetailActivity.this, "无有效业务员");
			}
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
			CommonProgressDialog.getInstance().dismissProgressDialog();
		}
	};

	OnDismissListener dismissListener = new OnDismissListener()
	{

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			lastIndex = -1;
		}
	};

	DialogClickListener confirmClickListener = new DialogClickListener()
	{

		@Override
		public void onDialogClick()
		{
			// 进行业务员分配请求
			if (lastIndex == -1)
			{
				distributeDialog.dismiss();
			}
			else
			{
				CommonProgressDialog.getInstance().showProgressDialog(MessageDetailActivity.this,
						getString(R.string.message_sent_distributing));
				final String distributeOperatorId = salesmanList.get(lastIndex).operatorId;
				RequestCenter.assignSalesman(dataListener, distributeOperatorId, mailId);
				MobclickAgent.onEvent(MessageDetailActivity.this, "31");
				SysManager.analysis(R.string.c_type_click, R.string.c031);
			}
		}
	};

	DialogClickListener dialogClickListener = new DialogClickListener()
	{

		@Override
		public void onDialogClick()
		{
			distributeDialog.dismiss();
			MobclickAgent.onEvent(MessageDetailActivity.this, "30");
			SysManager.analysis(R.string.c_type_click, R.string.c030);
		}
	};

	OnItemClickListener onItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos, long id)
		{
			if (!Utils.isEmpty(operatorId))
			{

				if (!operatorId.equals(salesmanList.get(pos).operatorId))
				{
					lastIndex = pos;
					for (int i = 0; i < salesmanList.size(); i++)
					{
						if (i == pos)
						{
							salesmanList.get(i).isAllocationed = "1";
						}
						else
						{
							salesmanList.get(i).isAllocationed = "0";
						}
					}
				}
				else
				{
					lastIndex = -1;
					for (int i = 0; i < salesmanList.size(); i++)
					{
						salesmanList.get(i).isAllocationed = "0";
					}
				}
				salesmanAdapter.refreshData(salesmanList);
			}
			MobclickAgent.onEvent(MessageDetailActivity.this, "29");
			SysManager.analysis(R.string.c_type_click, R.string.c029);
		}
	};

	DisposeDataListener dataListener = new SimpleDisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			startSendDistributedBroadCast();
			activityFinishProtect();
			ToastUtil.toast(MessageDetailActivity.this, getString(R.string.message_allocation_success));
			if (-1 != lastIndex)
				operatorId = salesmanList.get(lastIndex).operatorId;
			// 成功后，将邮件ID和operatorId 添加到广播中
			CommonProgressDialog.getInstance().dismissProgressDialog();
			distributeDialog.dismiss();
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			activityFinishProtect();
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(MessageDetailActivity.this, anomalyMsg);
		}
	};

	private void activityFinishProtect()
	{
		if (MessageDetailActivity.this.isFinishing())
			return;
	}

	private int[] getWholeWord(TextView tv, int index)
	{
		if (tv.getText().length() > index)
		{

			int i = index;
			index = i;
			while (true)
			{
				if (!tv.getText().subSequence(index, index + 1).toString().matches("[a-zA-Z-']"))
					break;
				index -= 1;
				if (index < 0)
					break;
			}
			while ((i < tv.getText().length()) && (tv.getText().subSequence(i, i + 1).toString().matches("[a-zA-Z-']")))
				i += 1;
			return new int[]
			{ index + 1, i };
		}
		else
		{
			return null;
		}
	}

	private void translate(String content)
	{
		MobclickAgent.onEvent(MessageDetailActivity.this, "32");
		SysManager.analysis(R.string.c_type_click, R.string.c032);
		if (Utils.isEmpty(content) && !Util.isEnglish(content))

			return;
		client.dict(content, fromLang, toLang, new IDictResultCallback()
		{

			@Override
			public void onResult(DictResult result)
			{
				if (result == null)
				{
					Log.d("TransOpenApiDemo", "Trans Result is null");

				}
				else
				{
					Log.d("TransOpenApiDemo", "MainActivity->" + result.toJSONString());

					// translateDebugText.setText(result.toJSONString());
					if (result.error_code == 0)
					{// 没错
						if (!MessageDetailActivity.this.isFinishing())

						{
							String toastContent = result.word_name + "\r\n"
									+ result.symbols.get(0).dict_means.get(0).part
									+ result.symbols.get(0).dict_means.get(0).means.toString();
							toastContent = toastContent.replace("[", " ");
							toastContent = toastContent.replace("]", " ");
							ToastUtil.toast(MessageDetailActivity.this, toastContent);
						}
						// translateResutlText.setText(result.trans_result);
					}
					else
					{
						// translateResutlText.setText(result.error_msg);
					}
				}
			}

		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (distributeDialog != null && distributeDialog.isShowing())
			{
				distributeDialog.dismiss();
			}
			else
			{
				back();
			}
		}
		return true;
	}

	private void back()
	{
		setResult(RESULT_CANCELED);
		MessageDetailActivity.this.finish();

	}

	@Override
	public void onResume()
	{
		super.onResume();
		if ("0".equals(action))
		{

			MobclickAgent.onPageStart(getString(R.string.p10003));
			SysManager.analysis(R.string.p_type_page, R.string.p10003);
		}
		else
		{
			MobclickAgent.onPageStart(getString(R.string.p10007));
			SysManager.analysis(R.string.p_type_page, R.string.p10007);
		}
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if ("0".equals(action))
		{
			MobclickAgent.onPageEnd(getString(R.string.p10003));
		}
		else
		{
			MobclickAgent.onPageEnd(getString(R.string.p10007));

		}
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (client != null)
		{
			client.onDestroy();
		}
	}
}
