package com.micen.suppliers.adapter.broadcast;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.module.response.BroadCastContent;
import com.micen.suppliers.R;
import com.micen.suppliers.module.NotifyLink;


public class ServiceBroadCastAdapter extends BroadCastBaseAdapter
{
	public ServiceBroadCastAdapter(Activity activity, ArrayList<BroadCastContent> dataList)
	{
		super(activity, dataList);
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2)
	{
		if (arg1 == null)
		{
			arg1 = LayoutInflater.from(activity).inflate(R.layout.broadcast_list_item, null);
			holder = new ViewHolder();
			holder.ivUnreadFlag = (ImageView) arg1.findViewById(R.id.iv_broadcast_status);
			holder.tvBroadcastDate = (TextView) arg1.findViewById(R.id.tv_broadcast_date);
			holder.inquiryContentLayout = (LinearLayout) arg1.findViewById(R.id.ll_broadcast_title_inquiry);
			holder.purchaseContentLayout = (LinearLayout) arg1.findViewById(R.id.ll_broadcast_title_purchase);
			holder.detailLayout = (LinearLayout) arg1.findViewById(R.id.ll_broadcast_detail);
			holder.tvContent = (TextView) arg1.findViewById(R.id.tv_broadcast_content);
			arg1.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) arg1.getTag();
		}
		module = (BroadCastContent) getItem(arg0);
		holder.ivUnreadFlag.setVisibility(isMessageRead() ? View.GONE : View.VISIBLE);
		holder.inquiryContentLayout.setVisibility(View.GONE);
		holder.purchaseContentLayout.setVisibility(View.GONE);
		holder.tvBroadcastDate.setText(formatDate(module.sendTime));
		holder.tvContent.setText(module.subject);
		if (NotifyLink.getValueByTag(module.messageLink) == NotifyLink.WebAddress)
		{
			holder.detailLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.detailLayout.setVisibility(View.GONE);
		}
		return arg1;
	}

}
