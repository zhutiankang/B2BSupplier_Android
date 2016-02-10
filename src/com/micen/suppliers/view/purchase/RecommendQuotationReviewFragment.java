package com.micen.suppliers.view.purchase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.module.purchase.RecommendQuotationContent;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;


public class RecommendQuotationReviewFragment extends Fragment
{
	private String quotationid;

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;
	private RelativeLayout rlTitle;
	// 附加区域
	private LinearLayout llAdditionalContent;

	// 产品资料
	private TextView tvProductName;
	private TextView ivRemark;

	// 附件图片
	private ImageView ivImage;

	// 报价详情
	private TextView tvUnitPrice;
	private TextView tvMinOrder;
	private TextView tvPayment;

	// 附加条件
	private TextView tvDeliveryTime;
	private TextView tvModeOfTransport;
	private LinearLayout llRemark;
	private LinearLayout llPayment;
	private LinearLayout llModeOfTransport;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	private RecommendQuotationContent value;

	public static RecommendQuotationReviewFragment newInstance(String quotationid)
	{
		RecommendQuotationReviewFragment fragment = new RecommendQuotationReviewFragment();

		Bundle bundle = new Bundle();
		bundle.putString("quotationid", quotationid);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (null != bundle)
		{
			quotationid = bundle.getString("quotationid");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_quotation_recommend_review, container, false);
		initView(view);
		getData();
		return view;
	}

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			value = (RecommendQuotationContent) obj;
			if ("0".equals(value.code))
			{
				setData();
				showRelatedData();
			}
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			showNetworkError();
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			showNoRelatedData();
		}
	};

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.common_ll_title_back:
				getActivity().finish();
				break;
			case R.id.quotation_recommend_imgImageView:
				if (Utils.isEmpty(value.content.attachmentPicUrl))
				{
					if (!Utils.isEmpty(value.content.attachmentName))
					{
						ToastUtil.toast(getActivity(), R.string.viewattachment);
					}
				}
				else
				{
					Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
					intent.putExtra("fragment", "imagebrowser");
					intent.putExtra("imUri", value.content.attachmentPicUrl);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		}

	};

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub

			pageStateView.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			getData();
		}

	};

	private void getData()
	{
		showwait();
		RequestCenter.getRecommendQuotationDetail(quotationid, dataListener);
	}

	private void initView(View view)
	{
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);

		ivBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_white_btn);

		tvTitle = (TextView) view.findViewById(R.id.common_title_name);
		tvTitle.setText(R.string.quotationdetail);
		tvTitle.setTextColor(getResources().getColor(R.color.color_0088f0));

		rlTitle = (RelativeLayout) view.findViewById(R.id.rl_common_title);
		rlTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));

		// 产品资料
		tvProductName = (TextView) view.findViewById(R.id.quotation_recommend_nameTextView);

		ivRemark = (TextView) view.findViewById(R.id.quotation_recommend_remarkTextView);

		// 附件图片
		ivImage = (ImageView) view.findViewById(R.id.quotation_recommend_imgImageView);
		ivImage.setOnClickListener(clickListener);
		// 报价详情
		tvUnitPrice = (TextView) view.findViewById(R.id.quotation_recommend_uintpriceTextView);
		tvMinOrder = (TextView) view.findViewById(R.id.quotation_recommend_quanityTextView);
		tvPayment = (TextView) view.findViewById(R.id.quotation_recommend_paymentTextView);

		// 附加条件
		tvDeliveryTime = (TextView) view.findViewById(R.id.quotation_recommend_dayTextView);
		tvModeOfTransport = (TextView) view.findViewById(R.id.quotation_recommend_mode_of_transportationTextView);
		llAdditionalContent = (LinearLayout) view.findViewById(R.id.quotation_recommend_additionalcontentLinearLayout);

		llRemark = (LinearLayout) view.findViewById(R.id.quotation_recommend_remarkLinearLayout);
		llPayment = (LinearLayout) view.findViewById(R.id.quotation_recommend_paymentLinearLayout);
		llModeOfTransport = (LinearLayout) view
				.findViewById(R.id.quotation_recommend_mode_of_transportationLinearLayout);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);
	}

	private void setData()
	{
		if (null == value || null == value.content)
			return;

		// 产品资料
		tvProductName.setText(value.content.prodName);

		// ivRemark.setText(value.content.remark);

		// 附件图片
		if (Utils.isEmpty(value.content.attachmentPicUrl))
		{
			if (Utils.isEmpty(value.content.attachmentName))
			{
				ivImage.setVisibility(View.GONE);
			}
			else
			{
				ivImage.setImageResource(R.drawable.ic_purchase_rfq_file);
			}
		}
		else
		{
			ImageUtil.getImageLoader().displayImage(value.content.attachmentPicUrl, ivImage,
					ImageUtil.getRecommendImageOptions());
		}

		// 报价详情
		// 先获取对应的英文
		String[] unit = getResources().getStringArray(R.array.priceunit_zh);
		String[] unit_value = getResources().getStringArray(R.array.priceunit_value);

		String[] unit_recommend_name = getResources().getStringArray(R.array.priceunit_recommend_name);

		int index = 0;

		try
		{
			index = Integer.parseInt(value.content.prodPriceUnit_pro);
			if (index < unit_recommend_name.length)
			{
				value.content.prodPriceUnit_pro = unit_recommend_name[index];
			}

			for (int i = 0; i < unit_value.length; i++)
			{
				if (unit_value[i].equals(value.content.prodPriceUnit_pro))
				{
					value.content.prodPriceUnit_pro = unit[i];
					break;
				}
			}

		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		tvUnitPrice.setText(value.content.prodPrice + " USD/" + value.content.prodPriceUnit_pro);

		index = 0;

		try
		{
			index = Integer.parseInt(value.content.prodMinnumOrderType_pro);
			if (index < unit_recommend_name.length)
			{
				value.content.prodMinnumOrderType_pro = unit_recommend_name[index];
			}
			for (int i = 0; i < unit_value.length; i++)
			{
				if (unit_value[i].equals(value.content.prodMinnumOrderType_pro))
				{
					value.content.prodMinnumOrderType_pro = unit[i];
					break;
				}
			}

		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		tvMinOrder.setText(value.content.prodMinnumOrder + " " + value.content.prodMinnumOrderType_pro);

		tvDeliveryTime.setText(value.content.leadTime + " " + getString(R.string.unit_day));
		// tvPayment.setText(value.content.paymentTerm_pro);

		// 是否显示
		// rlAdditionalContent
		// 附加条件

		// tvModeOfTransport.setText(value.content.deliveryMethod_pro);
		// 是否显示

		if (Utils.isEmpty(value.content.remark) && Utils.isEmpty(value.content.shipmentType)
				&& Utils.isEmpty(value.content.shipmentPort) && Utils.isEmpty(value.content.prodPaymentType))
		{
			llAdditionalContent.setVisibility(View.GONE);

		}
		else
		{
			llAdditionalContent.setVisibility(View.VISIBLE);

			// 附加条件
			if (Utils.isEmpty(value.content.remark))
			{
				llRemark.setVisibility(View.GONE);
			}
			else
			{
				ivRemark.setText(value.content.remark);
			}
			if (Utils.isEmpty(value.content.shipmentPort) && Utils.isEmpty(value.content.shipmentType))
			{
				llModeOfTransport.setVisibility(View.GONE);
			}
			else
			{
				index = 0;

				try
				{
					index = Integer.parseInt(value.content.shipmentType);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}

				String[] shipment_name = getResources().getStringArray(R.array.tradetype_recommend_name);

				if (index < shipment_name.length)
				{
					value.content.shipmentType = shipment_name[index];
				}
				else
				{
					value.content.shipmentType = "Other";
				}

				if (Utils.isEmpty(value.content.shipmentPort))
				{
					tvModeOfTransport.setText(value.content.shipmentType);
				}
				else
				{
					tvModeOfTransport.setText(value.content.shipmentType + "/" + value.content.shipmentPort);
				}
			}
			if (Utils.isEmpty(value.content.prodPaymentType))
			{
				llPayment.setVisibility(View.GONE);
			}
			else
			{
				index = 0;

				try
				{
					index = Integer.parseInt(value.content.prodPaymentType);
				}
				catch (Exception e)
				{
					index = 99;
					// TODO: handle exception
				}

				String[] payment_name = getResources().getStringArray(R.array.payment_recommend_name);

				if (index < payment_name.length)
				{
					value.content.prodPaymentType = payment_name[index];
				}
				else
				{
					value.content.prodPaymentType = value.content.prodPaymentType;
				}

				tvPayment.setText(value.content.prodPaymentType);
			}
		}
	}

	private void showwait()
	{
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void showNoRelatedData()
	{
		pageStateView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}

	private void showRelatedData()
	{
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
	}

	private void showNetworkError()
	{
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}
}
