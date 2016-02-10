package com.micen.suppliers.adapter.purchase;

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
import com.micen.suppliers.module.purchase.PurchaseInfo;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;


public class PurchaseInfoAdapter extends BaseAdapter
{
	private Context mContext;
	private ArrayList<PurchaseInfo> mList;
	private ViewHolder holder;
	private PurchaseInfo tmp;

	/**
	 * 最紧急及最新
	 */
	private int curType;

	public PurchaseInfoAdapter(Context context, ArrayList<PurchaseInfo> list)
	{
		mContext = context;
		mList = list;
		curType = 0;
	}

	public void setData(ArrayList<PurchaseInfo> list, int type)
	{
		mList = list;
		curType = type;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (mList != null)
			return mList.size();
		return 0;
	}

	@Override
	public PurchaseInfo getItem(int index)
	{
		// TODO Auto-generated method stub
		if (mList != null)
			return mList.get(index);

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
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase, null);
			holder = new ViewHolder();
			// holder.ivVip = (ImageView) convertView.findViewById(R.id.purchase_list_item_vipImageView);
			holder.ivCountry = (ImageView) convertView.findViewById(R.id.purchase_list_item_countryImageView);
			holder.tvName = (TextView) convertView.findViewById(R.id.purchase_list_item_nameTextView);
			holder.tvRecommend = (TextView) convertView.findViewById(R.id.purchase_list_item_recommendTextView);
			holder.tvPurchaseQuantity = (TextView) convertView
					.findViewById(R.id.purchase_list_item_purchasequantityValueTextView);

			holder.tvExpiredDAtaName = (TextView) convertView.findViewById(R.id.purchase_list_item_expireddate);
			holder.tvExpiredDate = (TextView) convertView
					.findViewById(R.id.purchase_list_item_expireddateValueTextView);
			holder.tvQuoteLeft = (TextView) convertView.findViewById(R.id.purchase_list_item_quoteleftValueTextView);

			convertView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		tmp = getItem(position);

		if (tmp != null)
		{
			holder.tvName.setText(tmp.productName);
			if (Utils.isEmpty(tmp.purchaseQuantity) || "0".equals(tmp.purchaseQuantity))
			{
				holder.tvPurchaseQuantity.setText(R.string.contact_buyer);
			}
			else
			{
				holder.tvPurchaseQuantity.setText(tmp.purchaseQuantity + " " + tmp.purchaseQuantityType);
			}

			if ("1".equals(tmp.isRecommended))
			{
				holder.tvRecommend.setVisibility(View.VISIBLE);
				holder.tvQuoteLeft.setText(mContext.getString(R.string.recommendtip));
			}
			else
			{
				holder.tvRecommend.setVisibility(View.GONE);
				holder.tvQuoteLeft.setText(tmp.quoteLeft);
			}

			if (0 == curType)
			{
				// time left
				holder.tvExpiredDAtaName.setText(mContext.getString(R.string.expireddate));
				// holder.tvExpiredDate.setText(Util.getLeftDay(tmp.expiredDate));
				holder.tvExpiredDate.setText(Util.formatDate(tmp.expiredDate, "yyyy-MM-dd"));
			}
			else
			{
				// Date Posted
				holder.tvExpiredDAtaName.setText(mContext.getString(R.string.dateposted_));
				holder.tvExpiredDate.setText(Util.formatDate(tmp.postDate, "yyyy-MM-dd"));
			}
			// 加载国家图片
			ImageUtil.getImageLoader().displayImage(tmp.countryImageUrl, holder.ivCountry,
					ImageUtil.getCommonImageOptions());
		}

		return convertView;
	}

	static class ViewHolder
	{
		ImageView ivVip;
		ImageView ivCountry;
		TextView tvName;
		TextView tvRecommend;
		TextView tvPurchaseQuantity;
		TextView tvExpiredDAtaName;
		TextView tvExpiredDate;
		TextView tvQuoteLeft;
	}
}
