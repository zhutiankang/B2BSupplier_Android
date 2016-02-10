package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.QuotationDetail;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;


public class PurchaseQuotationDetailExpiredAdapter extends PurchaseQuotationDetailAdapter
{
	private ViewHolder holder;

	public PurchaseQuotationDetailExpiredAdapter(Activity c, List<QuotationDetail> l)
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
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_purchase_quotation_expired, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.quotation_expired_list_item_nameTextView);

			holder.ivCountry = (ImageView) convertView.findViewById(R.id.quotation_expired_list_item_countryImageView);

			holder.tvRecommend = (TextView) convertView
					.findViewById(R.id.quotation_expired_list_item_recommendedTextView);

			holder.tvCompanyName = (TextView) convertView
					.findViewById(R.id.quotation_expired_list_item_companynameValueTextView);
			holder.tvBuyerName = (TextView) convertView
					.findViewById(R.id.quotation_expired_list_item_userValueTextView);
			holder.tvExpiredDate = (TextView) convertView
					.findViewById(R.id.quotation_expired_list_item_expireddateValueTextView);
			holder.tvSubmit = (TextView) convertView.findViewById(R.id.quotation_expired_list_item_submitter);
			holder.tvSubmitName = (TextView) (TextView) convertView
					.findViewById(R.id.quotation_expired_list_item_submitterValueTextView);

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
			}
			else
			{
				holder.tvRecommend.setVisibility(View.VISIBLE);
			}
			holder.tvBuyerName.setText(tmp.buyerName);
			holder.tvExpiredDate.setText(Util.formatDate(tmp.validateTimeEnd, "yyyy-MM-dd"));
			holder.tvCompanyName.setText(tmp.buyerComName);

			if (Utils.isEmpty(tmp.quoteName))
			{
				holder.tvSubmit.setVisibility(View.GONE);
				holder.tvSubmitName.setVisibility(View.GONE);
			}
			else
			{
				holder.tvSubmit.setVisibility(View.VISIBLE);
				holder.tvSubmitName.setVisibility(View.VISIBLE);
				holder.tvSubmitName.setText(tmp.quoteName);
			}

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

		TextView tvSubmit;
		TextView tvSubmitName;
	}

}
