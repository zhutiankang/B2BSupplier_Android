package com.micen.suppliers.adapter.message;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.module.message.MessageContent;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.widget.CountryImageView;


public class MessageListAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<MessageContent> dataList;
	private String action;
	private ViewHolder holder;
	public ArrayList<String> selectMailList = new ArrayList<String>();
	private MessageContent messageContent;
	private String fullName = null;
	private String otherName = null;
	private String otherNameTag = null;
	private int otherNameColor;

	public MessageListAdapter(Context context, ArrayList<MessageContent> dataList, String action)
	{
		this.context = context;
		this.dataList = dataList;
		this.action = action;
	}

	@Override
	public int getCount()
	{
		return null == dataList ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return null == dataList ? null : dataList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (null == convertView || null == convertView.getTag())
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent, false);
			holder.name = (TextView) convertView.findViewById(R.id.message_contacts_lv_item_name);
			holder.otherPeopleName = (TextView) convertView.findViewById(R.id.message_contacts_lv_item_other_name);
			holder.content = (TextView) convertView.findViewById(R.id.message_contacts_lv_item_content);
			holder.data = (TextView) convertView.findViewById(R.id.message_contacts_lv_item_data);
			holder.tag = (ImageView) convertView.findViewById(R.id.message_contacts_lv_item_tag);
			holder.city = (CountryImageView) convertView.findViewById(R.id.message_contacts_lv_item_city);
			holder.rlItemCity = (RelativeLayout) convertView.findViewById(R.id.message_contacts_rl_item_city);
			holder.attachment = (ImageView) convertView.findViewById(R.id.message_contacts_lv_item_attachment);
			holder.isRead = (ImageView) convertView.findViewById(R.id.message_contacts_lv_item_read);
			holder.rlTag = (RelativeLayout) convertView.findViewById(R.id.message_contacts_lv_item_rl_tag);
			holder.rlTagPadding = (View) convertView.findViewById(R.id.message_contacts_lv_item_left_padding);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		messageContent = (MessageContent) getItem(position);
		if ("0".equals(action))
		{
			holder.isRead.setVisibility(View.GONE);
			fullName = messageContent.sender.fullName;
			otherNameTag = "收件人：";
			if ("00".equals(messageContent.receiver.operatorId)
					|| !SupplierApplication.getInstance().getUser().content.userInfo.isManager())
			{
				otherName = "我";
				otherNameColor = context.getResources().getColor(R.color.color_b3b3b3);
			}
			else
			{
				otherName = messageContent.receiver.fullName;
				otherNameColor = context.getResources().getColor(R.color.color_ccb895);
			}
			if (!Utils.isEmpty(messageContent.sender.countryImageUrl))
			{
				holder.rlItemCity.setVisibility(View.VISIBLE);
				ImageUtil.getImageLoader().displayImage(messageContent.sender.countryImageUrl, holder.city,
						ImageUtil.getCommonImageOptions());
			}
			else
			{
				holder.rlItemCity.setVisibility(View.INVISIBLE);
			}
			if ("0".equals(messageContent.isRead))// true：未读
			{
				holder.name.setTextColor(context.getResources().getColor(R.color.color_333333));
				holder.tag.setImageResource(R.drawable.bg_message_blue_dot);
				holder.tag.setVisibility(View.VISIBLE);
			}
			else
			{
				// 判断是否已经回复
				if ("1".equals(messageContent.isReplied))
				{
					holder.tag.setImageResource(R.drawable.ic_message_reply);
					holder.tag.setVisibility(View.VISIBLE);
				}
				else
				{
					holder.tag.setVisibility(View.INVISIBLE);
				}
				holder.name.setTextColor(context.getResources().getColor(R.color.color_666666));
			}
		}
		else
		{
			holder.rlTag.setVisibility(View.GONE);
			holder.rlTagPadding.setVisibility(View.VISIBLE);
			if ("1".equals(messageContent.receiverReadFlag))// true：未读
			{
				holder.name.setTextColor(context.getResources().getColor(R.color.color_666666));
				holder.isRead.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.name.setTextColor(context.getResources().getColor(R.color.color_333333));
				holder.isRead.setVisibility(View.GONE);
			}
			fullName = messageContent.receiver.fullName;
			otherNameTag = "发件人：";
			if ("00".equals(messageContent.sender.operatorId)
					|| !SupplierApplication.getInstance().getUser().content.userInfo.isManager())
			{
				otherName = "我";
				otherNameColor = context.getResources().getColor(R.color.color_b3b3b3);
			}
			else
			{
				otherName = messageContent.sender.fullName;
				otherNameColor = context.getResources().getColor(R.color.color_ccb895);
			}
			holder.rlItemCity.setVisibility(View.GONE);
			holder.tag.setVisibility(View.GONE);
		}
		if ("1".equals(messageContent.isAttached))
		{
			holder.attachment.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.attachment.setVisibility(View.GONE);
		}
		holder.otherPeopleName.setText(otherNameTag + otherName);
		holder.otherPeopleName.setTextColor(otherNameColor);
		holder.name.setText(fullName);
		if (!Utils.isEmpty(messageContent.date))
		{
			holder.data.setText(Util.formatDateForMessageList(messageContent.date));
			holder.data.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.data.setVisibility(View.INVISIBLE);
		}
		holder.content.setText(messageContent.subject);
		return convertView;
	}

	static class ViewHolder
	{
		ImageView tag;
		TextView name;
		TextView otherPeopleName;
		TextView data;
		TextView content;
		CountryImageView city;
		ImageView attachment;
		ImageView isRead;
		RelativeLayout rlItemCity;
		RelativeLayout rlTag;
		View rlTagPadding;

	}

	public void setData(ArrayList<MessageContent> dataList)
	{
		this.dataList = dataList;
		notifyDataSetChanged();
	}

}
