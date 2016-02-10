package com.micen.suppliers.view.home;

import java.util.ArrayList;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.module.response.PersonalMessage;
import com.focustech.common.module.response.PersonalMessageStatus;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.broadcast.BroadCastActivity_;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.NotifyType;
import com.umeng.analytics.MobclickAgent;


@EFragment(R.layout.broadcast_fragment_layout)
public class BroadcastFragment extends HomeBaseFragment
{
	@ViewById(R.id.broadcast_title_name)
	protected TextView tvTitle;

	@ViewById(R.id.rl_broadcast_inquiry)
	protected RelativeLayout inquiryBroadcast;
	@ViewById(R.id.tv_inquiry_unread)
	protected TextView inquiryBroadcastUnread;

	@ViewById(R.id.rl_broadcast_purchase)
	protected RelativeLayout purchaseBroadcast;
	@ViewById(R.id.tv_purchase_unread)
	protected TextView purchaseBroadcastUnread;

	@ViewById(R.id.rl_broadcast_service)
	protected RelativeLayout serviceBroadcast;
	@ViewById(R.id.tv_service_unread)
	protected TextView serviceBroadcastUnread;

	public BroadcastFragment()
	{

	}

	@Override
	protected void initView()
	{
		tvTitle.setText(R.string.home_bottom_title3);

		inquiryBroadcastUnread.setVisibility(View.GONE);
		purchaseBroadcastUnread.setVisibility(View.GONE);
		serviceBroadcastUnread.setVisibility(View.GONE);

		inquiryBroadcast.setOnClickListener(this);
		purchaseBroadcast.setOnClickListener(this);
		serviceBroadcast.setOnClickListener(this);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		RequestCenter.syncPersonalMessage(listener);
	}

	private DisposeDataListener listener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object obj)
		{
			PersonalMessage message = (PersonalMessage) obj;
			if (message.content.message != null && message.content.message.size() > 0)
			{
				refreshBroadcastNum(message.content.message);
			}
		}
	};

	/**
	 * 刷新消息中心未读消息数量
	 */
	public void refreshBroadcastNum(ArrayList<PersonalMessageStatus> messageList)
	{
		for (PersonalMessageStatus module : messageList)
		{
			switch (NotifyType.getValueByTag(module.pushType))
			{
			case Inquiry:
				if (!Utils.isEmpty(module.unreadNum) && !"0".equals(module.unreadNum))
				{
					inquiryBroadcastUnread.setVisibility(View.VISIBLE);
					inquiryBroadcastUnread.setText(module.unreadNum);
				}
				else
				{
					inquiryBroadcastUnread.setVisibility(View.GONE);
				}
				break;
			case Purchase:
				if (!Utils.isEmpty(module.unreadNum) && !"0".equals(module.unreadNum))
				{
					purchaseBroadcastUnread.setVisibility(View.VISIBLE);
					purchaseBroadcastUnread.setText(module.unreadNum);
				}
				else
				{
					purchaseBroadcastUnread.setVisibility(View.GONE);
				}
				break;
			case Service:
				if (!Utils.isEmpty(module.unreadNum) && !"0".equals(module.unreadNum))
				{
					serviceBroadcastUnread.setVisibility(View.VISIBLE);
					serviceBroadcastUnread.setText(module.unreadNum);
				}
				else
				{
					serviceBroadcastUnread.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		Intent intent = new Intent(getActivity(), BroadCastActivity_.class);
		switch (v.getId())
		{
		case R.id.rl_broadcast_inquiry:
			intent.putExtra("broadCastType", NotifyType.getValue(NotifyType.Inquiry));
			MobclickAgent.onEvent(getActivity(), "109");
			SysManager.analysis(R.string.c_type_click, R.string.c109);
			break;
		case R.id.rl_broadcast_purchase:
			intent.putExtra("broadCastType", NotifyType.getValue(NotifyType.Purchase));
			MobclickAgent.onEvent(getActivity(), "110");
			SysManager.analysis(R.string.c_type_click, R.string.c110);
			break;
		case R.id.rl_broadcast_service:
			intent.putExtra("broadCastType", NotifyType.getValue(NotifyType.Service));
			MobclickAgent.onEvent(getActivity(), "111");
			SysManager.analysis(R.string.c_type_click, R.string.c111);
			break;
		}
		startActivity(intent);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10020));
		SysManager.analysis(R.string.p_type_page, R.string.p10020);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10020));
	}

	@Override
	protected String getFragmentTag()
	{
		return BroadcastFragment.class.getName();
	}

}
