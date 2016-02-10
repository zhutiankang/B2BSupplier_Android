package com.micen.suppliers.adapter.broadcast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.module.response.BroadCastContent;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;


public abstract class BroadCastBaseAdapter extends BaseAdapter
{
	protected Activity activity;
	protected ArrayList<BroadCastContent> dataList;
	protected ViewHolder holder;
	protected BroadCastContent module;

	public BroadCastBaseAdapter(Activity activity, ArrayList<BroadCastContent> dataList)
	{
		this.activity = activity;
		this.dataList = dataList;
	}

	@Override
	public int getCount()
	{
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	@Override
	public abstract View getView(int arg0, View arg1, ViewGroup arg2);

	public void addData(ArrayList<BroadCastContent> dataList)
	{
		this.dataList.addAll(dataList);
		notifyDataSetChanged();
	}

	protected static class ViewHolder
	{
		ImageView ivUnreadFlag;
		TextView tvBroadcastDate;
		TextView tvInquiryFrom;
		TextView tvContent;
		LinearLayout inquiryContentLayout;
		LinearLayout purchaseContentLayout;
		LinearLayout detailLayout;
	}

	protected String formatDate(String time)
	{
		if (Utils.isEmpty(time))
		{
			return "";
		}
		Calendar today = Calendar.getInstance(TimeZone.getDefault());
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		Calendar yesterday = Calendar.getInstance(TimeZone.getDefault());
		yesterday.set(Calendar.DATE, today.get(Calendar.DATE) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);

		long messageTime = Long.parseLong(time);
		SimpleDateFormat sdf = null;
		if (messageTime >= today.getTimeInMillis())
		{
			sdf = new SimpleDateFormat(activity.getString(R.string.today) + " HH:mm", Locale.getDefault());
		}
		else if (messageTime >= yesterday.getTimeInMillis() && messageTime < today.getTimeInMillis())
		{
			sdf = new SimpleDateFormat(activity.getString(R.string.yesterday) + " HH:mm", Locale.getDefault());
		}
		else
		{
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		}
		Date dt = new Date();
		try
		{
			dt = new Date(messageTime);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		return sdf.format(dt);
	}

	/**
	 * 消息是否已读
	 * @return		1:已读，0:未读
	 */
	protected boolean isMessageRead()
	{
		return module != null && "1".equals(module.readState) ? true : false;
	}

	/**
	 * 更新适配器数据
	 * @param module
	 */
	public void updateModuleData(BroadCastContent module)
	{
		dataList.set(dataList.indexOf(module), module);
		notifyDataSetChanged();
	}
}
