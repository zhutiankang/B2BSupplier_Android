package com.micen.suppliers.adapter.purchase;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.RfqItem;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;


public class PurchaseRfqNeedQuoteAdapter extends BaseAdapter
{
	private Context mContext;
	private List<RfqItem> mList;

	private ViewHolder holder;
	private RfqItem tmp;

	public PurchaseRfqNeedQuoteAdapter(Context c, List<RfqItem> l)
	{
		mContext = c;
		mList = new ArrayList<RfqItem>();
		mList.addAll(l);
	}

	public void addData(List<RfqItem> l)
	{
		mList.addAll(l);

	}

	public void setData(List<RfqItem> l)
	{
		mList.clear();
		mList.addAll(l);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.size();
		return 0;
	}

	@Override
	public RfqItem getItem(int position)
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_rfq_need_quote, null);

			holder = new ViewHolder();

			holder.tvName = (TextView) convertView.findViewById(R.id.rfq_need_quote_list_item_nameTextView);
			holder.tvCompanyName = (TextView) convertView
					.findViewById(R.id.rfq_need_quote_list_item_companynameValueTextView);
			holder.tvUser = (TextView) convertView.findViewById(R.id.rfq_need_quote_list_item_userValueTextView);
			holder.tvExpiredDate = (TextView) convertView
					.findViewById(R.id.rfq_need_quote_list_item_expireddateValueTextView);
			holder.tvReceivedDate = (TextView) convertView
					.findViewById(R.id.rfq_need_quote_list_item_receiveddateValueTextView);

			holder.ivCountry = (ImageView) convertView.findViewById(R.id.rfq_need_quote_list_item_countryImageView);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		tmp = getItem(position);

		if (null != tmp)
		{
			holder.tvName.setText(tmp.rfqSubject);
			holder.tvCompanyName.setText(tmp.buyerComName);
			holder.tvUser.setText(tmp.buyerName);
			holder.tvExpiredDate.setText(Util.formatDate(tmp.validateTimeEnd, "yyyy-MM-dd"));
			holder.tvReceivedDate.setText(Util.formatDate(tmp.rfqReceiveTime, "yyyy-MM-dd"));

			ImageUtil.getImageLoader().displayImage(tmp.buyerCountryImageUrl, holder.ivCountry,
					ImageUtil.getCommonImageOptions());
		}

		return convertView;
	}

	static class ViewHolder
	{
		public TextView tvName;
		public TextView tvCompanyName;
		public TextView tvUser;
		public TextView tvExpiredDate;
		public TextView tvReceivedDate;

		public ImageView ivCountry;
	}

}
