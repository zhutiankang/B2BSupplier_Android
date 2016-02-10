package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.SearchResultItem;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;


public class PurchaseSearchResultAdapter extends BaseAdapter
{

	private List<SearchResultItem> list;

	private ViewHolder holder;
	private SearchResultItem tmp;

	private Context mContext;

	public PurchaseSearchResultAdapter(Context c, List<SearchResultItem> l)
	{
		mContext = c;
		list = l;
	}

	public void addData(List<SearchResultItem> l)
	{
		if (null != list)
		{
			list.addAll(l);
			notifyDataSetChanged();
		}
	}

	public void setData(List<SearchResultItem> l)
	{
		if (null != list)
		{
			list.clear();
			list.addAll(l);
			notifyDataSetChanged();
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
	public SearchResultItem getItem(int position)
	{
		// TODO Auto-generated method stub
		if (list != null)
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
		if (convertView == null)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_search_result, null);
			holder = new ViewHolder();
			// holder.ivVip = (ImageView) convertView.findViewById(R.id.purchase_search_list_item_vipImageView);
			holder.ivCountry = (ImageView) convertView.findViewById(R.id.purchase_search_list_item_countryImageView);
			holder.tvName = (TextView) convertView.findViewById(R.id.purchase_search_list_item_nameTextView);

			holder.tvRecommend = (TextView) convertView.findViewById(R.id.purchase_search_list_item_recommendTextView);

			holder.tvPurchaseQuantity = (TextView) convertView
					.findViewById(R.id.purchase_search_list_item_purchasequantityValueTextView);
			holder.tvDatePosted = (TextView) convertView
					.findViewById(R.id.purchase_search_list_item_datepostedValueTextView);
			holder.tvExpiredDate = (TextView) convertView
					.findViewById(R.id.purchase_search_list_item_expireddateValueTextView);
			holder.tvQuoteLeft = (TextView) convertView
					.findViewById(R.id.purchase_search_list_item_quoteleftValueTextView);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		tmp = getItem(position);

		if (tmp != null)
		{
			holder.tvName.setText(tmp.subject);

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

			if (Utils.isEmpty(tmp.purchaseQuantity) || "0".equals(tmp.purchaseQuantity))
			{
				holder.tvPurchaseQuantity.setText(R.string.contact_buyer);
			}
			else
			{
				holder.tvPurchaseQuantity.setText(tmp.purchaseQuantity + " " + tmp.purchaseUnit);
			}

			holder.tvDatePosted.setText(Util.formatDate(tmp.postDate, "yyyy-MM-dd"));
			holder.tvExpiredDate.setText(Util.formatDate(tmp.expiredDate, "yyyy-MM-dd"));

			// 加载国家图片
			ImageUtil.getImageLoader().displayImage(tmp.countryImageUrl, holder.ivCountry,
					ImageUtil.getCommonImageOptions());
		}
		return convertView;
	}

	static class ViewHolder
	{
		// ImageView ivVip;
		ImageView ivCountry;
		TextView tvName;
		TextView tvRecommend;
		TextView tvPurchaseQuantity;
		TextView tvDatePosted;
		TextView tvExpiredDate;
		TextView tvQuoteLeft;
	}

}
