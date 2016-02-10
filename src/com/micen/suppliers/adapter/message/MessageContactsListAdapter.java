package com.micen.suppliers.adapter.message;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.message.MessageContact;
import com.micen.suppliers.util.Util;


public class MessageContactsListAdapter extends BaseAdapter
{
	private ArrayList<MessageContact> dataList;
	private Context context;
	private ViewHolder holder;

	public MessageContactsListAdapter(ArrayList<MessageContact> dataList, Context context)
	{
		this.dataList = dataList;
		this.context = context;
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message_contacts, null);
			holder = new ViewHolder();
			holder.itemTvname = (TextView) convertView.findViewById(R.id.message_contacts_item_tv_name);
			holder.itemTvEmail = (TextView) convertView.findViewById(R.id.message_contacts_item_tv_email);
			holder.itemTvCompany = (TextView) convertView.findViewById(R.id.message_contacts_item_tv_company);
			holder.itemTvCity = (TextView) convertView.findViewById(R.id.message_contacts_item_tv_city);
			holder.itemTvLocalTime = (TextView) convertView.findViewById(R.id.message_contacts_item_tv_local_time);
			holder.localTime = (LinearLayout) convertView.findViewById(R.id.message_contacts_item_ll);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		// TODO bean中的字段需要修改
		if (!Utils.isEmpty(dataList.get(position).receiverName))
		{
			holder.itemTvname.setText(dataList.get(position).receiverName);
		}
		else
		{
			holder.itemTvname.setText("N/A");
		}
		if (!Utils.isEmpty(dataList.get(position).receiverMail))
		{
			holder.itemTvEmail.setText(dataList.get(position).receiverMail);
		}
		else
		{
			holder.itemTvEmail.setText("N/A");
		}
		if (!Utils.isEmpty(dataList.get(position).receiverComName))
		{
			holder.itemTvCompany.setText(dataList.get(position).receiverComName);
		}
		else
		{
			holder.itemTvCompany.setText("N/A");
		}
		if (!Utils.isEmpty(dataList.get(position).receiverCountry))
		{
			holder.itemTvCity.setText(dataList.get(position).receiverCountry);
		}
		else
		{
			holder.itemTvCity.setText("N/A");
		}
		if (!Utils.isEmpty(dataList.get(position).localTime))
		{
			holder.itemTvLocalTime.setText(Util.formatDate(dataList.get(position).localTime));
		}
		else
		{
			holder.itemTvLocalTime.setText("N/A");
		}
		return convertView;
	}

	static class ViewHolder
	{
		TextView itemTvname;
		TextView itemTvEmail;
		TextView itemTvCompany;
		TextView itemTvCity;
		TextView itemTvLocalTime;
		LinearLayout localTime;
	}
}
