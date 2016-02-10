package com.micen.suppliers.adapter.message;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.db.MessageShortCut;


public class MessageShortCutListAdapter extends BaseAdapter
{
	private ArrayList<MessageShortCut> dataList;
	private Context context;
	private ViewHolder holder;

	public MessageShortCutListAdapter(ArrayList<MessageShortCut> dataList, Context context)
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message_shortcut, null);
			holder = new ViewHolder();
			holder.mailShortCutTv = (TextView) convertView.findViewById(R.id.mail_short_cut_tv);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mailShortCutTv.setText(dataList.get(position).messageShortCutContent);
		return convertView;
	}

	static class ViewHolder
	{
		TextView mailShortCutTv;
	}
}
