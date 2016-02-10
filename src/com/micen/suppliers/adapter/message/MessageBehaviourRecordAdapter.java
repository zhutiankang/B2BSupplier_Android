package com.micen.suppliers.adapter.message;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.message.MessageBehaviorRecordInquiryBetweenUs;
import com.micen.suppliers.util.Util;


public class MessageBehaviourRecordAdapter extends BaseAdapter
{
	private Context context;
	private ViewHolder holder;
	public ArrayList<MessageBehaviorRecordInquiryBetweenUs> dataList;

	public MessageBehaviourRecordAdapter(Context context, ArrayList<MessageBehaviorRecordInquiryBetweenUs> dataList)
	{
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount()
	{
		return null == dataList ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null || convertView.getTag() == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message_buyer_inquiry_between_us,
					parent, false);
			holder.identify = (TextView) convertView.findViewById(R.id.mbr_identify);
			holder.date = (TextView) convertView.findViewById(R.id.mbr_date);
			holder.subject = (TextView) convertView.findViewById(R.id.mbr_subject);
			holder.receive = (TextView) convertView.findViewById(R.id.mbr_receive);
			holder.sender = (TextView) convertView.findViewById(R.id.mbr_sender);
			holder.attachment = (ImageView) convertView.findViewById(R.id.mbr_attachment);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if ("1".equals(dataList.get(position).isInbox))
		{
			holder.identify.setText(R.string.message);
			holder.identify.setBackgroundResource(R.drawable.bg_behaviour_receive);
		}
		else
		{
			holder.identify.setText(R.string.message_reply);
			holder.identify.setBackgroundResource(R.drawable.bg_behaviour_reply);
		}

		if ("1".equals(dataList.get(position).isAttached))
		{
			holder.attachment.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.attachment.setVisibility(View.INVISIBLE);
		}
		if (!Utils.isEmpty(dataList.get(position).time))
		{
			holder.date.setVisibility(View.VISIBLE);
			holder.date.setText(Util.formatDateForMessageList(dataList.get(position).time));
		}
		else
		{
			holder.date.setVisibility(View.INVISIBLE);
		}
		holder.subject.setText(dataList.get(position).subject);
		// TODO 记录标识需要修改：询盘/回复 -->背景设置
		// 收发件人自行拼接

		holder.receive.setText(context.getString(R.string.message_detail_receiver) + dataList.get(position).receiver);
		holder.sender.setText(context.getString(R.string.message_detail_sender) + dataList.get(position).sender);
		return convertView;
	}

	static class ViewHolder
	{
		TextView identify;
		TextView date;
		TextView subject;
		TextView receive;
		TextView sender;
		ImageView attachment;

	}

}
