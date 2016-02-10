package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.ProductItem;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;


public class PurchaseProductAdapter extends BaseAdapter
{
	private ViewHolder holder;
	private Context mContext;
	private ProductItem tmp;
	private List<ProductItem> list;

	public PurchaseProductAdapter(Context c, List<ProductItem> l)
	{
		mContext = c;
		list = l;
	}

	public void setData(List<ProductItem> l)
	{
		list = l;
	}

	public void addData(List<ProductItem> l)
	{
		if (null != list)
		{
			list.addAll(l);
		}
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (null != list)
			return list.size();
		return 0;
	}

	@Override
	public ProductItem getItem(int position)
	{
		// TODO Auto-generated method stub
		if (null != list)
			return list.get(position);
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		if (null == convertView)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_product, null);
			holder = new ViewHolder();

			holder.ivImg = (ImageView) convertView.findViewById(R.id.list_item_purchase_product_imgImageView);
			holder.tvName = (TextView) convertView.findViewById(R.id.list_item_purchase_product_nameTextView);
			holder.rtStar = (RatingBar) convertView.findViewById(R.id.list_item_purchase_product_starRatingBar);
			holder.tvMasterName = (TextView) convertView
					.findViewById(R.id.list_item_purchase_product_masternameTextView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		tmp = getItem(position);
		if (null != tmp)
		{
			holder.tvName.setText(tmp.productName);
			holder.tvMasterName.setText(tmp.userName);
			holder.rtStar.setRating(Util.parseProductStar(tmp.productStarLevel));

			// 加载产品图片
			if (Utils.isEmpty(tmp.image))
			{
				holder.ivImg.setImageResource(R.drawable.bg_message_attachment_loading);
			}
			else
			{
				ImageUtil.getImageLoader().displayImage(tmp.image, holder.ivImg, ImageUtil.getCommonImageOptions());
			}

		}

		return convertView;
	}

	static class ViewHolder
	{
		public TextView tvName;
		public ImageView ivImg;
		public RatingBar rtStar;
		public TextView tvMasterName;

	}

}
