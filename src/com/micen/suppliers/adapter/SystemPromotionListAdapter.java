package com.micen.suppliers.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.promotion.SystemPromotionDiscovery;
import com.micen.suppliers.util.ImageUtil;


public class SystemPromotionListAdapter extends BaseAdapter
{
	private Activity mActivity;
	private ArrayList<SystemPromotionDiscovery> dataList;
	private ViewHolder holder;
	private SystemPromotionDiscovery module;

	public SystemPromotionListAdapter(Activity activity, ArrayList<SystemPromotionDiscovery> dataList)
	{
		this.mActivity = activity;
		this.dataList = dataList;
	}

	@Override
	public int getCount()
	{
		return dataList.size();
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
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.system_promotion_item, parent, false);
			holder = new ViewHolder();
			holder.iconImage = (ImageView) convertView.findViewById(R.id.iv_promotion_image);
			holder.titleText = (TextView) convertView.findViewById(R.id.tv_promotion_title);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		module = (SystemPromotionDiscovery) getItem(position);
		ImageUtil.getImageLoader().displayImage(module.imageSrc, holder.iconImage, ImageUtil.getImageOptions());
		holder.titleText.setText(module.title);
		return convertView;
	}

	static class ViewHolder
	{
		ImageView iconImage;
		TextView titleText;
	}

}
