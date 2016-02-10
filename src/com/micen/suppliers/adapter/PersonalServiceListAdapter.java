package com.micen.suppliers.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.user.ServiceInfo;
import com.micen.suppliers.util.Util;


public class PersonalServiceListAdapter extends BaseAdapter
{
	private Activity mActivity;
	private ArrayList<ServiceInfo> dataList;
	private ViewHolder holder;
	private ServiceInfo module;
	private boolean isInit = true;

	public PersonalServiceListAdapter(Activity activity, ArrayList<ServiceInfo> dataList)
	{
		this.mActivity = activity;
		this.dataList = dataList;
	}

	@Override
	public int getCount()
	{
		return dataList.size() < 3 ? dataList.size() : (isInit ? 2 : dataList.size());
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
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.personal_service_item, parent, false);
			holder = new ViewHolder();
			holder.serviceName = (TextView) convertView.findViewById(R.id.tv_service_name);
			holder.serviceType = (TextView) convertView.findViewById(R.id.tv_service_type);
			holder.serviceDate = (TextView) convertView.findViewById(R.id.tv_service_date);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		module = (ServiceInfo) getItem(position);
		holder.serviceName.setText(module.serviceNameCn);
		holder.serviceType.setText(module.description);
		holder.serviceDate.setText(Util.formatDate(module.serviceStartTime) + " ~ "
				+ Util.formatDate(module.serviceEndTime));
		return convertView;
	}

	static class ViewHolder
	{
		TextView serviceName;
		TextView serviceType;
		TextView serviceDate;
	}

	public void setInit(boolean isInit)
	{
		this.isInit = isInit;
	}

}
