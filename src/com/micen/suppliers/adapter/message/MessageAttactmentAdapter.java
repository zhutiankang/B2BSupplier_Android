package com.micen.suppliers.adapter.message;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.message.MessageDetailsAttachmentList;
import com.micen.suppliers.util.ImageUtil;


public class MessageAttactmentAdapter extends BaseAdapter
{
	private Activity mActivity;
	private ArrayList<MessageDetailsAttachmentList> dataList;
	private ViewHolder holder;

	public MessageAttactmentAdapter(Activity activity, ArrayList<MessageDetailsAttachmentList> dataList)
	{
		this.mActivity = activity;
		this.dataList = dataList;
	}

	@Override
	public int getCount()
	{
		return null == dataList ? 0 : dataList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_message_attachment, parent, false);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.iv_recommend_item);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if ("1".equals(dataList.get(position).isImg))
		{
			ImageUtil.getImageLoader().displayImage(dataList.get(position).imgUrl, holder.iv,
					ImageUtil.getRecommendImageOptions());
		}
		else
		{
			holder.iv.setImageResource(R.drawable.ic_launcher);
		}
		return convertView;
	}

	static class ViewHolder
	{
		ImageView iv;
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

}
