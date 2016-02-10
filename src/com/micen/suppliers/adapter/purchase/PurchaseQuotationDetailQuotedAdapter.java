package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.message.MessageDetailActivity_;
import com.micen.suppliers.activity.message.MessageSendActivity_;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.message.MessageSendTarget;
import com.micen.suppliers.module.purchase.QuotationDetail;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.umeng.analytics.MobclickAgent;


public class PurchaseQuotationDetailQuotedAdapter extends PurchaseQuotationDetailAdapter
{
	private ViewHolder holder;

	public PurchaseQuotationDetailQuotedAdapter(Activity c, List<QuotationDetail> l)
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
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_item_purchase_quotation_quoted, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.quotation_quoted_list_item_nameTextView);

			holder.tvRecommend = (TextView) convertView
					.findViewById(R.id.quotation_quoted_list_item_recommendedTextView);
			holder.llCompanyName = (LinearLayout) convertView
					.findViewById(R.id.purchase_quoted_list_item_company_LinearLayout);
			holder.tvCompanyName = (TextView) convertView
					.findViewById(R.id.quotation_quoted_list_item_companynameValueTextView);
			holder.tvBuyerName = (TextView) convertView.findViewById(R.id.quotation_quoted_list_item_userValueTextView);
			holder.tvExpiredDate = (TextView) convertView
					.findViewById(R.id.quotation_quoted_list_item_expireddateValueTextView);
			holder.tvStatus = (TextView) convertView
					.findViewById(R.id.quotation_quoted_list_item_buyerresponseValueTextView);

			holder.tvSubmitName = (TextView) (TextView) convertView
					.findViewById(R.id.quotation_quoted_list_item_submitterValueTextView);

			holder.rlButton = (RelativeLayout) convertView.findViewById(R.id.quotation_page_list_item_addional);
			holder.tvContactBuyer = (TextView) convertView
					.findViewById(R.id.quotation_quoted_list_item_contactbuyerTextView);
			holder.tvBuyInquiry = (TextView) convertView
					.findViewById(R.id.quotation_quoted_list_item_buyerinquryTextView);

			holder.ivCountry = (ImageView) convertView.findViewById(R.id.quotation_quoted_list_item_countryImageView);

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

			if ("1".equals(tmp.buyerReadFlag))
			{

				holder.tvStatus.setText(mActivity.getString(R.string.readed));
			}
			else
			{
				holder.tvStatus.setText(mActivity.getString(R.string.unreaded));
			}

			holder.tvSubmitName.setText(tmp.quoteName);

			if ("0".equals(tmp.showContactBuyer))
			{
				// 显示 或者不显示
				holder.tvContactBuyer.setVisibility(View.GONE);
			}
			else
			{
				holder.tvContactBuyer.setVisibility(View.VISIBLE);
				holder.tvContactBuyer.setTag(position);
				holder.tvContactBuyer.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						// 进入给买家发送询盘页面
						int position = (Integer) v.getTag();
						QuotationDetail temp = getItem(position);
						// MessageSendActivity
						Intent intent = new Intent(mActivity, MessageSendActivity_.class);
						intent.putExtra(MessageConstantDefine.action.toString(), "0");
						intent.putExtra(MessageConstantDefine.mailId.toString(), temp.messageId);
						intent.putExtra(MessageConstantDefine.messageSendTarget.toString(),
								MessageSendTarget.getValue(MessageSendTarget.Send));
						// 新增加
						intent.putExtra("replySubject", temp.rfqSubject);
						intent.putExtra("replyReceive", temp.buyerName);

						mActivity.startActivity(intent);
						MobclickAgent.onEvent(mActivity, "105");
						SysManager.analysis(R.string.c_type_click, R.string.c105);
					}
				});
			}

			if ("0".equals(tmp.showReplyMessage))
			{
				holder.tvBuyInquiry.setVisibility(View.GONE);
			}
			else
			{
				holder.tvBuyInquiry.setVisibility(View.VISIBLE);
				holder.tvBuyInquiry.setTag(position);
				holder.tvBuyInquiry.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						int position = (Integer) v.getTag();
						QuotationDetail temp = getItem(position);
						// 买家回复给供应商的询盘详情页
						Intent intent = new Intent(mActivity, MessageDetailActivity_.class);
						intent.putExtra(MessageConstantDefine.action.toString(), "0");
						intent.putExtra(MessageConstantDefine.mailId.toString(), temp.messageId);
						intent.putExtra(MessageConstantDefine.messageSendTarget.toString(), true);
						intent.putExtra(MessageConstantDefine.isPurchase.toString(), true);
						mActivity.startActivity(intent);
						MobclickAgent.onEvent(mActivity, "106");
						SysManager.analysis(R.string.c_type_click, R.string.c106);
					}
				});

			}

			if ("0".equals(tmp.showContactBuyer) && "0".equals(tmp.showReplyMessage))
			{
				holder.rlButton.setVisibility(View.GONE);
			}
			else
			{
				holder.rlButton.setVisibility(View.VISIBLE);
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
		TextView tvCompanyName;
		TextView tvBuyerName;
		TextView tvExpiredDate;
		TextView tvSubmitName;
		// 已阅读/未阅读
		TextView tvStatus;

		RelativeLayout rlButton;
		// 联系买家
		TextView tvContactBuyer;
		// 买家询盘
		TextView tvBuyInquiry;
		// isrecommend
		TextView tvRecommend;

		LinearLayout llCompanyName;
	}

}
