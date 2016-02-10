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
import com.micen.suppliers.module.message.MessageSalesman;


public class MessageSalesmanListAdapter extends BaseAdapter
{
	private ArrayList<MessageSalesman> dataList;
	private Context context;
	private ViewHolder holder;
	private String operatorId;

	public MessageSalesmanListAdapter(ArrayList<MessageSalesman> dataList, Context context, String operatorId)
	{
		this.dataList = dataList;
		this.context = context;
		this.operatorId = operatorId;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_message_salesman, null);
			holder = new ViewHolder();
			holder.salesman = (TextView) convertView.findViewById(R.id.ms_tv_salesman);
			holder.distributed = (TextView) convertView.findViewById(R.id.ms_tv_distributed);
			holder.checked = (ImageView) convertView.findViewById(R.id.ms_iv_checked);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		if ("1".equals(dataList.get(position).isAllocationed))
		{
			holder.checked.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.checked.setVisibility(View.INVISIBLE);

		}
		if (!Utils.isEmpty(operatorId) && operatorId.equals(dataList.get(position).operatorId))
		{
			holder.distributed.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.distributed.setVisibility(View.INVISIBLE);
		}
		holder.salesman.setMaxWidth(Utils.toDip(context, 100));
		holder.salesman.setText(dataList.get(position).userName);
		return convertView;
	}

	static class ViewHolder
	{
		TextView salesman;
		TextView distributed;
		ImageView checked;
	}

	public void refreshData(ArrayList<MessageSalesman> data)
	{
		this.dataList = data;
		notifyDataSetChanged();
	}
}
