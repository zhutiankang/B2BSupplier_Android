package com.micen.suppliers.activity.message;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.message.MessageSendTarget;
import com.micen.suppliers.view.message.MessageSentBaseFragment;
import com.micen.suppliers.view.message.ReplyFragment_;
import com.micen.suppliers.view.message.SendFragment_;
import com.umeng.analytics.MobclickAgent;



@EActivity
public class MessageSendActivity extends BaseFragmentActivity
{
	private MessageSentBaseFragment sendBaseFragment;
	private MessageSendTarget target;

	private String mailTarget;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null)
		{
			mailTarget = savedInstanceState.getString(MessageConstantDefine.messageSendTarget.toString());
		}
		initNavigationBarStyle(false);
		setContentView(R.layout.activity_message_send);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (!Utils.isEmpty(mailTarget))
		{
			outState.putString(MessageConstantDefine.messageSendTarget.toString(),
					getIntent().getStringExtra(MessageConstantDefine.messageSendTarget.toString()));
		}
	}

	@AfterViews
	protected void initView()
	{
		if (Utils.isEmpty(mailTarget))
		{
			// 取值，判断当前展示的是回复询盘页还是发送询盘页
			if (getIntent() != null && getIntent().hasExtra(MessageConstantDefine.messageSendTarget.toString()))
			{
				mailTarget = getIntent().getStringExtra(MessageConstantDefine.messageSendTarget.toString());
			}
		}
		target = MessageSendTarget.getValueByTag(mailTarget);

		if (findViewById(R.id.mail_send_fragment_container) != null && target != null)
		{
			switch (target)
			{
			case Reply:
				sendBaseFragment = new ReplyFragment_();
				break;
			case Send:
				sendBaseFragment = new SendFragment_();
				break;
			default:
				break;
			}
			getSupportFragmentManager().beginTransaction().add(R.id.mail_send_fragment_container, sendBaseFragment)
					.commit();
		}

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
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			sendBaseFragment.back();
		}
		return true;
	}
}
