package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.QuotationDetail;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.umeng.analytics.MobclickAgent;


public class PurchaseQuotationDetailNeedModifyAdapter extends PurchaseQuotationDetailAdapter
{
	private ViewHolder holder;

	public PurchaseQuotationDetailNeedModifyAdapter(Activity c, List<QuotationDetail> l)
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
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_purchase_quotation_need_modify,
					null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.quotation_need_modify_list_item_nameTextView);

			holder.ivCountry = (ImageView) convertView
					.findViewById(R.id.quotation_need_modify_list_item_countryImageView);

			holder.tvRecommend = (TextView) convertView
					.findViewById(R.id.quotation_need_modify_list_item_recommendedTextView);

			holder.tvCompanyName = (TextView) convertView
					.findViewById(R.id.quotation_need_modify_list_item_companynameValueTextView);
			holder.tvBuyerName = (TextView) convertView
					.findViewById(R.id.quotation_need_modify_list_item_userValueTextView);
			holder.tvExpiredDate = (TextView) convertView
					.findViewById(R.id.quotation_need_modify_list_item_expireddateValueTextView);

			holder.tvSubmitName = (TextView) (TextView) convertView
					.findViewById(R.id.quotation_need_modify_list_item_submitterValueTextView);

			holder.tvNeedEdit = (TextView) convertView
					.findViewById(R.id.quotation_need_modify_list_item_neededitTextView);

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
			holder.tvSubmitName.setText(tmp.quoteName);
			holder.tvNeedEdit.setTag(position);
			holder.tvNeedEdit.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					// 入采购报价表单页面，带入用户的数据，重新提交后，进入待审核队列；
					// 委托 及 普通报价
					int position = (Integer) v.getTag();
					QuotationDetail temp = getItem(position);
					Intent intent = new Intent(mActivity, PurchaseActivity_.class);
					// 普通报价
					intent.putExtra("fragment", "quotationnormalreedit");
					intent.putExtra("rfqid", temp.rfqId);
					intent.putExtra("quotationid", temp.quotationId);
					mActivity.startActivityForResult(intent, 1);
					MobclickAgent.onEvent(mActivity, "107");
					SysManager.analysis(R.string.c_type_click, R.string.c107);
				}
			});
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

		// 联系买家
		TextView tvNeedEdit;
	}

}
