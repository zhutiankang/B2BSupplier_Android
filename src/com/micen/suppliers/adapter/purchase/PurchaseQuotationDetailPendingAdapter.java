package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.QuotationDetail;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;


public class PurchaseQuotationDetailPendingAdapter extends PurchaseQuotationDetailAdapter
{
	private ViewHolder holder;

	public PurchaseQuotationDetailPendingAdapter(Activity c, List<QuotationDetail> l)
	{
		super(c, l);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub

		if (null == convertView)
		{
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_purchase_quotation_pending, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.quotation_pending_list_item_nameTextView);

			holder.tvRecommend = (TextView) convertView
					.findViewById(R.id.quotation_pending_list_item_recommendedTextView);
			holder.llCompanyName = (LinearLayout) convertView
					.findViewById(R.id.purchase_pending_list_item_company_LinearLayout);
			holder.tvCompanyName = (TextView) convertView
					.findViewById(R.id.quotation_pending_list_item_companynameValueTextView);
			holder.tvBuyerName = (TextView) convertView
					.findViewById(R.id.quotation_pending_list_item_userValueTextView);
			holder.tvExpiredDate = (TextView) convertView
					.findViewById(R.id.quotation_pending_list_item_expireddateValueTextView);
			holder.tvSubmitName = (TextView) convertView
					.findViewById(R.id.quotation_pending_list_item_submitterValueTextView);

			holder.ivCountry = (ImageView) convertView.findViewById(R.id.quotation_pending_list_item_countryImageView);

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

			if ("0".equals(tmp.isRecommended))
			{
				holder.tvRecommend.setVisibility(View.GONE);
				holder.tvCompanyName.setText(tmp.buyerComName);
			}
			else
			{
				holder.tvRecommend.setVisibility(View.VISIBLE);
				if (Utils.isEmpty(tmp.buyerComName))
				{
					holder.llCompanyName.setVisibility(View.GONE);
				}
				else
				{
					holder.tvCompanyName.setText(tmp.buyerComName);
				}
			}

			holder.tvBuyerName.setText(tmp.buyerName);
			holder.tvExpiredDate.setText(Util.formatDate(tmp.validateTimeEnd, "yyyy-MM-dd"));

			holder.tvSubmitName.setText(tmp.quoteName);

			ImageUtil.getImageLoader().displayImage(tmp.buyerCountryImageUrl, holder.ivCountry,
					ImageUtil.getCommonImageOptions());

		}

		return convertView;
	}

	static class ViewHolder
	{
		ImageView ivCountry;
		TextView tvName;
		TextView tvRecommend;
		TextView tvCompanyName;
		TextView tvBuyerName;
		TextView tvExpiredDate;
		TextView tvSubmitName;

		LinearLayout llCompanyName;

	}

}
