package com.micen.suppliers.adapter.message;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.message.MessageBehaviorRecordRFQ;


public class MessageBehaviourSourcingListAdapter extends BaseAdapter
{
	private Context context;
	private ViewHolder holder;
	private ArrayList<MessageBehaviorRecordRFQ> rfqList;

	public MessageBehaviourSourcingListAdapter(Context context, ArrayList<MessageBehaviorRecordRFQ> rfqList)
	{
		this.context = context;
		this.rfqList = rfqList;
	}

	@Override
	public int getCount()
	{
		return null == rfqList ? 0 : rfqList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return rfqList.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message_behaviour_rfq, parent, false);
			holder.name = (TextView) convertView.findViewById(R.id.mb_rfq_list_item_tv_name);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(rfqList.get(position).rfqFullTitle);
		return convertView;
	}

	static class ViewHolder
	{
		TextView name;
	}

}
