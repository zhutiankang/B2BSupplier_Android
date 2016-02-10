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
import com.micen.suppliers.module.purchase.QuotationItem;
import com.micen.suppliers.util.Util;


public class PurchaseQuotationAdapter extends BaseAdapter
{
	private Context mContext;
	private List<QuotationItem> mList;
	private QuotationItem temp;
	private Holder holder;

	public PurchaseQuotationAdapter(Context c, List<QuotationItem> l)
	{
		mContext = c;
		mList = l;
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
	public QuotationItem getItem(int position)
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_quotation, parent, false);
			holder = new Holder();
			holder.nameTextView = (TextView) convertView
					.findViewById(R.id.purchase_quotation_list_item_namevalueTextView);
			holder.locatioinTextView = (TextView) convertView
					.findViewById(R.id.purchase_quotation_list_item_locationvalueTextView);
			holder.dateTextView = (TextView) convertView
					.findViewById(R.id.purchase_search_list_item_quoteleftValueTextView);

			holder.ivAS = (ImageView) convertView.findViewById(R.id.purchase_quotation_list_item_asImageview);

			holder.ivGold = (ImageView) convertView.findViewById(R.id.purchase_quotation_list_item_goldImageview);

			convertView.setTag(holder);
		}
		else
		{
			holder = (Holder) convertView.getTag();
		}

		temp = getItem(position);

		if (null != temp)
		{
			if (null != temp.companyName && temp.companyName.length() > 2)
			{
				holder.nameTextView.setText(temp.companyName.substring(0, 2) + "******");
			}
			else
			{
				holder.nameTextView.setText(temp.companyName);
			}

			holder.locatioinTextView.setText(temp.location);
			/*
			if (Utils.isEmpty(temp.comProvince))
			{
				holder.locatioinTextView.setText(temp.comCity);
			}
			else
			{
				holder.locatioinTextView.setText(temp.comCity + "," + temp.comProvince);

			}
			 */
			holder.dateTextView.setText(Util.formatDate(temp.quoteTime, "yyyy-MM-dd"));

			if ("true".equals(temp.isGoldMember))
			{
				holder.ivGold.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.ivGold.setVisibility(View.GONE);
			}

			if ("30".equals(temp.csLevel))
			{
				holder.ivGold.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.ivGold.setVisibility(View.GONE);
			}

			// 认证类型:
			// 0：无认证
			// 1：AuditSupplier认证
			// 2：OnsiteCheck认证
			// 3: LicenseVerify 认证

			if ("1".equals(temp.auditType))
			{
				holder.ivAS.setImageResource(R.drawable.ic_supplier_as);
				holder.ivAS.setVisibility(View.VISIBLE);
			}
			else if ("2".equals(temp.auditType))
			{
				holder.ivAS.setImageResource(R.drawable.ic_supplier_oc);
				holder.ivAS.setVisibility(View.VISIBLE);
			}
			else if ("3".equals(temp.auditType))
			{
				holder.ivAS.setImageResource(R.drawable.ic_supplier_lv);
				holder.ivAS.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.ivAS.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	static class Holder
	{
		TextView nameTextView;
		TextView locatioinTextView;
		TextView dateTextView;

		ImageView ivGold;
		ImageView ivAS;
	}

}
