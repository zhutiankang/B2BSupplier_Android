package com.micen.suppliers.view.message;

import java.util.ArrayList;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.micen.suppliers.R;
import com.micen.suppliers.activity.message.MessageDetailActivity_;
import com.micen.suppliers.adapter.message.MessageBehaviourRecordAdapter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageBehaviorRecordInquiryBetweenUs;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.umeng.analytics.MobclickAgent;


@EFragment(R.layout.fragment_message_buyer_inquiry_between_us)
public class BuyerInquiryBetweenUsMessageFragment extends Fragment implements OnClickListener, OnItemClickListener
{
	@ViewById(R.id.mbr_lv)
	protected ListView mbrLv;

	private boolean isLoaded = false;

	private ArrayList<MessageBehaviorRecordInquiryBetweenUs> dataList;

	public BuyerInquiryBetweenUsMessageFragment()
	{

	}

	@Override
	public void onClick(View v)
	{

	}

	public void setData(ArrayList<MessageBehaviorRecordInquiryBetweenUs> bean)
	{
		if (!isLoaded)
		{

			if (bean != null && bean.size() != 0)
			{
				// 填充数据
				// ListView绑定数据MessageBehaviourRecordAdapter
				dataList = bean;
				MessageBehaviourRecordAdapter adapter = new MessageBehaviourRecordAdapter(getActivity(), dataList);
				mbrLv.setAdapter(adapter);
				mbrLv.setOnItemClickListener(this);
				isLoaded = true;
			}
			else
			{
				mbrLv.setVisibility(View.GONE);
				isLoaded = true;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id)
	{
		final String actionId = ("1".equals(dataList.get(pos).isInbox) ? 0 : 1) + "";
		// 显示邮件详情界面
		Intent intent = new Intent(getActivity(), MessageDetailActivity_.class);
		intent.putExtra(MessageConstantDefine.action.toString(), actionId);
		intent.putExtra(MessageConstantDefine.mailId.toString(), dataList.get(pos).mailId);
		intent.putExtra(MessageConstantDefine.isNeedToShieldFunction.toString(), true);
		startActivity(intent);
		MobclickAgent.onEvent(getActivity(), "61");
		SysManager.analysis(R.string.c_type_click, R.string.c061);
	}
}
