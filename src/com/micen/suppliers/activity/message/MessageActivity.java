package com.micen.suppliers.activity.message;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.focustech.common.widget.viewpagerindictor.UnderlinePageIndicator;
import com.focustech.common.widget.viewpagerindictor.UnderlinePageIndicator.ChangeTitle;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.adapter.message.CommonFragmentAdapter;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.manager.PopupManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.view.message.AllMessageFragment_;
import com.micen.suppliers.view.message.DistributedMessageFragment_;
import com.micen.suppliers.view.message.MessageBaseFragment;
import com.micen.suppliers.view.message.MessageBaseFragment.OnMessageChangedListener;
import com.micen.suppliers.view.message.UnDistributedMessageFragment_;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class MessageActivity extends BaseFragmentActivity implements ChangeTitle, OnMessageChangedListener
{
	@ViewById(R.id.rl_common_title)
	protected RelativeLayout messageTitleRl;
	@ViewById(R.id.message_rl_undistributed)
	protected RelativeLayout messageRlUndistributed;
	@ViewById(R.id.message_tv_undistributed)
	protected TextView messageTvUndistributed;
	@ViewById(R.id.message_rl_distributed)
	protected RelativeLayout messageRlDistributed;
	@ViewById(R.id.message_tv_distributed)
	protected TextView messageTvDistributed;
	@ViewById(R.id.message_tv_all)
	protected TextView messageTvAll;
	@ViewById(R.id.message_ulpi_title_line)
	protected UnderlinePageIndicator messageUlpiTitleLine;
	@ViewById(R.id.message_vp)
	protected ViewPager messageVp;
	@ViewById(R.id.message_cb_unread)
	protected CheckBox messageCbUnread;
	@ViewById(R.id.message_rl_unread)
	protected RelativeLayout messageRlUnread;
	@ViewById(R.id.message_rl_unread_checkbox)
	protected RelativeLayout messageRlUnreadCheckbox;
	// 邮件分类：为了针对业务员和会员
	@ViewById(R.id.message_ll_sort)
	protected LinearLayout messageLlSort;
	// 未读邮件数
	@ViewById(R.id.message_tv_unread_num)
	protected TextView messageTvUnreadNum;
	// 未分配邮件数
	@ViewById(R.id.message_tv_undistributed_num)
	protected TextView messageTvUnDistributedNum;
	protected PopupWindow popupWindow;
	protected LinearLayout messageTypeInbox;
	protected LinearLayout messageTypeSent;
	private CommonFragmentAdapter adapter;
	private Boolean isGoldMember = true;
	private boolean isSaleman = false;
	private ArrayList<Fragment> fragmentList;
	/**
	 * 收发件箱标识
	 * 0:收件箱
	 * 1：发件箱
	 */
	private String action = "0";
	/**
	 * 读取状态
	 */
	private String readStatus = "0";

	private boolean checkUnReadMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		initNavigationBarStyle(false);
	}

	/**
	 * ------思路--------------
	 * 1.分别加载三个Fragment：未分配，已分配，全部
	 * 2.未分配：action=1 读取状态：readStatus = getReadStatus(),
	 * 3.已分配：action=1 ,readStatus = getReadStatus(),
	 * 4.全部：action =getAction(), readStatus = getReadStatus();
	 * 5.界面切换时调用loadData(pos);
	 * 
	 * 读取状态  getReadStatus();
	 * 邮箱类型：收件箱/发件箱 getAction();
	 */

	@AfterViews
	protected void initView()
	{
		MobclickAgent.openActivityDurationTrack(false);
		initData();
		// 获取当前用户是否是高级供应商 会员类型：0：免费供应商 5：合作会员 30：高级供应商
		initTitleBar();
		initFragmentList();
	}

	/**
	 * 初始化相关数据
	 */
	private void initData()
	{
		isGoldMember = "30".equals(SupplierApplication.getInstance().getUser().content.companyInfo.csLevel) ? true
				: false;
		isSaleman = SupplierApplication.getInstance().getUser().content.userInfo.isManager() ? false : true;
		checkUnReadMessage = getIntent().getBooleanExtra(MessageConstantDefine.checkUnReadMessage.toString(), false);
	}

	private void initCheckUnReadMessage()
	{
		if (checkUnReadMessage)
		{
			SharedPreferenceManager.getInstance().putBoolean(
					((MessageBaseFragment) adapter.getItem(fragmentList.size() - 1)).getChildTag() + "unRead", true);
		}
	}

	private void initTitleBar()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		btnBack.setOnClickListener(this);
		btnBack.setBackgroundResource(R.drawable.btn_blue_common_btn);
		tvTitle.setText(R.string.message_inbox);
		tvTitle.setTextColor(getResources().getColor(R.color.color_ffffff));
		// 设置收/发件箱名后的图片
		setTitleNameCompoundDrawables(tvTitle, R.drawable.ic_title_arrows_down);
		tvTitle.setBackgroundResource(R.drawable.btn_blue_common_btn);
		tvTitle.setOnClickListener(this);

		btnFunction5.setVisibility(View.VISIBLE);
		btnFunction5.setImageResource(R.drawable.ic_title_contacts);
		btnFunction5.setBackgroundResource(R.drawable.btn_blue_common_btn);
		LinearLayout.LayoutParams imgvwDimens = new LinearLayout.LayoutParams(Utils.toDip(MessageActivity.this, 54),
				LayoutParams.MATCH_PARENT);
		btnFunction5.setLayoutParams(imgvwDimens);
		btnFunction5.setOnClickListener(this);
		messageRlUndistributed.setOnClickListener(this);
		messageRlDistributed.setOnClickListener(this);
		messageCbUnread.setOnCheckedChangeListener(onCheckedChangeListener);
		messageTvAll.setOnClickListener(this);
		messageRlUnreadCheckbox.setOnClickListener(this);
	}

	/**
	 * 初始化加载的Fragment
	 */
	private void initFragmentList()
	{
		fragmentList = new ArrayList<Fragment>();
		// 设置ViewPage默认加载页数量
		messageVp.setOffscreenPageLimit(5);
		messageRlUnread.setVisibility(View.VISIBLE);
		fragmentList.clear();
		// 判断是否是金牌会员，业务员，普通会员，合作会员只显示全部邮件
		if (isGoldMemberAdmin())
		{
			messageLlSort.setVisibility(View.VISIBLE);
			fragmentList.add(new UnDistributedMessageFragment_());
			// 该部分需要判断用户是否是高级会员：高级会员显示已分配部分,并且将布局中的部分显示出来
			fragmentList.add(new DistributedMessageFragment_());
			fragmentList.add(new AllMessageFragment_());
			messageRlDistributed.setVisibility(View.VISIBLE);
		}
		else
		{
			// 只显示全部邮件，并且隐藏相应头部
			fragmentList.add(new AllMessageFragment_());
			messageLlSort.setVisibility(View.GONE);
		}
		adapter = new CommonFragmentAdapter(this, getSupportFragmentManager(), fragmentList);
		// 设置ViewPage默认加载页
		messageVp.setAdapter(adapter);
		messageUlpiTitleLine.setViewPager(messageVp);
		messageUlpiTitleLine.setChangeTitleListener(this);
		messageUlpiTitleLine.setFades(false);
		messageUlpiTitleLine.setSelectedColor(getResources().getColor(R.color.page_indicator_title_selected));
		initCheckUnReadMessage();
	}

	// 判断是否是金牌管理员
	private boolean isGoldMemberAdmin()
	{
		return isGoldMember && !isSaleman;
	}

	// 判断当前页是否是需要展示的页面
	private boolean isNeedChangeCurrentItem(int pos)
	{
		return messageVp.getCurrentItem() != pos;
	}

	private boolean isInbox()
	{
		return "0".equals(action);
	}

	/**
	 * 设置Title部分的名称右侧图片
	 * @param id 图片资源：R.drawable.xxx
	 */
	private void setTitleNameCompoundDrawables(TextView v, int id)
	{
		final Drawable drawable = getResources().getDrawable(id);
		// 必须设置图片大小，否则不显示
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		v.setCompoundDrawables(null, null, drawable, null);
		v.setCompoundDrawablePadding(Utils.toDip(MessageActivity.this, 13));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		// 未分配
		case R.id.message_rl_undistributed:
			if (isNeedChangeCurrentItem(0))
			{
				messageVp.setCurrentItem(0);
			}
			MobclickAgent.onEvent(MessageActivity.this, "14");
			SysManager.analysis(R.string.c_type_click, R.string.c014);
			break;
		// 已分配
		case R.id.message_rl_distributed:
			if (isNeedChangeCurrentItem(1))
			{
				messageVp.setCurrentItem(1);
			}
			MobclickAgent.onEvent(MessageActivity.this, "15");
			SysManager.analysis(R.string.c_type_click, R.string.c015);
			break;
		// 全部
		// 需要进行判断：当前界面可能只有两个栏目：非高级会员不显示未分配界面
		case R.id.message_tv_all:
			if (!isGoldMember)
			{
				if (isNeedChangeCurrentItem(1))
				{
					messageVp.setCurrentItem(1);
				}
			}
			else
			{
				if (isNeedChangeCurrentItem(2))
				{
					messageVp.setCurrentItem(2);
				}
			}
			MobclickAgent.onEvent(MessageActivity.this, "16");
			SysManager.analysis(R.string.c_type_click, R.string.c016);
			break;
		// 通讯录
		case R.id.common_title_function5:
			startActivity(new Intent(MessageActivity.this, MessageContactsActivity_.class));
			MobclickAgent.onEvent(MessageActivity.this, "13");
			SysManager.analysis(R.string.c_type_click, R.string.c013);
			break;
		// 切换收件箱和发件箱
		case R.id.common_title_name:
			showPopupWindow(v);
			setTitleNameCompoundDrawables(tvTitle, R.drawable.ic_title_arrows_up);
			break;
		case R.id.message_type_inbox:
			if (!getString(R.string.message_inbox).equals(tvTitle.getText().toString()))
			{
				tvTitle.setText(R.string.message_inbox);
				action = "0";
				readStatus = "0";
				initFragmentList();
			}
			if (popupWindow != null)
				popupWindow.dismiss();
			MobclickAgent.onEvent(MessageActivity.this, "47");
			SysManager.analysis(R.string.c_type_click, R.string.c047);
			break;

		case R.id.message_type_sent:
			if (!getString(R.string.message_send).equals(tvTitle.getText().toString()))
			{
				tvTitle.setText(R.string.message_send);
				action = "1";
				readStatus = "0";
				initUnRead();
				initSent();
			}
			if (popupWindow != null)
				popupWindow.dismiss();
			MobclickAgent.onEvent(MessageActivity.this, "12");
			SysManager.analysis(R.string.c_type_click, R.string.c012);
			break;
		case R.id.common_title_back:
			// 结束当前界面
			finish();
			break;
		case R.id.message_rl_unread_checkbox:
			final boolean checked = messageCbUnread.isChecked();
			messageCbUnread.setChecked(!checked);
			initReadStatus(!checked);
			loadData(messageVp.getCurrentItem());
			MobclickAgent.onEvent(MessageActivity.this, "140");
			SysManager.analysis(R.string.c_type_click, R.string.c140);
			break;
		}
	}

	/**
	 * 初始化收件箱
	 */
	private void initSent()
	{
		fragmentList.clear();
		fragmentList.add(new AllMessageFragment_());
		messageLlSort.setVisibility(View.GONE);
		messageRlUnread.setVisibility(View.GONE);
		adapter = new CommonFragmentAdapter(this, getSupportFragmentManager(), fragmentList);
		// 设置ViewPage默认加载页
		messageVp.setAdapter(adapter);
		messageCbUnread.setChecked(false);
	}

	@Override
	public void changeTitleStatus(int position)
	{
		if (isInbox())
		{
			final boolean isCheck = SharedPreferenceManager.getInstance().getBoolean(
					((MessageBaseFragment) adapter.getItem(position)).getChildTag() + "unRead", false);
			messageCbUnread.setChecked(isCheck);
			initReadStatus(isCheck);
		}
		// 根据界面的切换，获取是否只显示未读状态，并且进行设置
		// 进行数据加载
		loadData(position);
	}

	/**
	 * 根据当前界面保存的未读状态进行改变ReadStatus
	 * @param checked
	 */
	private void initReadStatus(boolean checked)
	{
		if (checked)
		{
			// 设置未读标识
			readStatus = "1";
		}
		else
		{
			// 隐藏未读邮件的数据
			messageTvUnreadNum.setVisibility(View.INVISIBLE);
			readStatus = "0";
		}
	}

	/**
	 * 只显示未读部分的CheckBox监听
	 */
	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			final int currentItem = messageVp.getCurrentItem();
			if (isInbox())
				// 根据当前显示的界面，保存对应界面的只读邮件选择的状态
				SharedPreferenceManager.getInstance().putBoolean(
						((MessageBaseFragment) adapter.getItem(currentItem)).getChildTag() + "unRead", isChecked);
		}
	};

	/**
	 * 该方法用来修改未读邮件数(共XX封)
	 */
	@Override
	public void setUnReadMessageNum(String unReadMessageNum)
	{
		messageTvUnreadNum.setVisibility(View.VISIBLE);
		messageTvUnreadNum.setText("(" + unReadMessageNum + "封)");
	}

	/**
	 * 该方法供Fragment调用，修改未分配邮件数目
	 * @param messageNum
	 */
	@Override
	public void setMessageNum(boolean isShow, String messageNum)
	{
		if (isShow)
		{
			messageTvUnDistributedNum.setVisibility(View.VISIBLE);
			messageTvUnDistributedNum.setText(messageNum);
		}
		else
		{
			messageTvUnDistributedNum.setVisibility(View.GONE);
		}
	}

	/**
	 * 该方法控制未读按钮是否可点击
	 * @param messageNum
	 */
	@Override
	public void setUnreadClickable(boolean isClickable)
	{
		messageRlUnreadCheckbox.setClickable(isClickable);
	}

	/**
	 * 返回当前选择的是 收件箱还是发件箱
	 * @return 0:收件箱 ,1：发件箱
	 */
	@Override
	public String getAction()
	{
		return action;
	}

	/**
	 * 读取状态
	 * @return 0：全部 ,1：未读 ,2：已读
	 */
	@Override
	public String getReadStatus()
	{
		return readStatus;
	}

	/**
	 * 返回当前用户是否是高级会员管理员
	 * @return
	 */
	@Override
	public boolean getDistractPermission()
	{
		return isGoldMember && !isSaleman && "0".equals(getAction());
	}

	/**
	 * 返回当前用户是否是高级会员管理员
	 * @return
	 */
	@Override
	public boolean getCheckUnReadMessageTAG()
	{
		return checkUnReadMessage;
	}

	/**
	 * 获取ViewPage中的第一页TAG
	 * @return
	 */
	@Override
	public String getFragmentTag(int pos)
	{
		return ((MessageBaseFragment) adapter.getItem(pos)).getChildTag();
	}

	/**
	 * 设置显示第三页(全部)--未读
	 * @return
	 */
	@Override
	public void setUnReadMessage()
	{
		if (isGoldMember)
		{
			messageVp.setCurrentItem(2);
		}
		else
		{
			messageVp.setCurrentItem(0);
		}
	}

	/**
	 * ViewPage加载数据
	 * @param pos 界面位置
	 */
	public void loadData(int pos)
	{
		((MessageBaseFragment) adapter.getItem(pos)).onLoadData(true);
	}

	/**
	* 弹出PopWindows
	* @param v 显示时依托的View
	*/
	public void showPopupWindow(View v)
	{
		popupWindow = PopupManager.getInstance().showSelectTypePopupDown(this,
				this.getLayoutInflater().inflate(R.layout.pop_message_type, null), v, Utils.toDip(this, 151),
				LayoutParams.WRAP_CONTENT, Utils.toDip(MessageActivity.this, -22),
				Utils.toDip(MessageActivity.this, -7));
		messageTypeInbox = (LinearLayout) popupWindow.getContentView().findViewById(R.id.message_type_inbox);
		messageTypeSent = (LinearLayout) popupWindow.getContentView().findViewById(R.id.message_type_sent);
		messageTypeInbox.setOnClickListener(this);
		messageTypeSent.setOnClickListener(this);
		popupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{

				setTitleNameCompoundDrawables(tvTitle, R.drawable.ic_title_arrows_down);

			}
		});
	}

	/**
	 * 初始化 列表页展示未读状态
	 */
	private void initUnRead()
	{
		for (int i = 0; i < fragmentList.size(); i++)
		{
			SharedPreferenceManager.getInstance().putBoolean(
					((MessageBaseFragment) adapter.getItem(i)).getChildTag() + "unRead", false);
		}
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// 清空保存的是否为未读状态
		initUnRead();
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		adapter.getItem(messageVp.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (adapter.getItem(messageVp.getCurrentItem()) != null)
			{
				finish();
				return true;
			}
		}
		return false;
	}
}
