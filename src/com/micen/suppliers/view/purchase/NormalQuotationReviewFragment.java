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
import com.micen.suppliers.module.purchase.NormalQuotationContent;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;


public class NormalQuotationReviewFragment extends Fragment
{

	private String quotationid;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;
	private RelativeLayout rlTitle;

	// 附加区域
	private LinearLayout llAdditionalContent;
	private LinearLayout llSamplingContent;

	// 产品资料
	private TextView tvProductName;
	private TextView tvProductType;
	private TextView tvProductDescription;

	// 附件图片
	private ImageView ivImage;

	// 报价详情
	private TextView tvTradeType;
	private TextView tvPortName;
	private TextView tvUnitPrice;
	private TextView tvMinOrder;
	private TextView tvPayment;

	// 附加条件
	private TextView tvValidDate;
	private TextView tvDeliveryTime;
	private TextView tvModeOfTransport;
	private TextView tvModeOfPacking;
	private TextView tvQualityInspection;
	private TextView tvFile;
	private LinearLayout llProductType;
	private LinearLayout llProductDescription;

	private LinearLayout llValidDate;
	private LinearLayout llDeliveryTime;
	private LinearLayout llModeOfTransport;
	private LinearLayout llModeOfPacking;
	private LinearLayout llQualityInspection;
	private LinearLayout llFile;
	// 梓品
	private TextView tvSample;

	// 返回的数据
	private NormalQuotationContent value;

	public static NormalQuotationReviewFragment newInstance(String quotationid)
	{
		NormalQuotationReviewFragment fragment = new NormalQuotationReviewFragment();

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
		View view = inflater.inflate(R.layout.fragment_purchase_quotation_normal_review, container, false);
		initView(view);
		getData();
		return view;
	}

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
			case R.id.quotation_normal_imgImageView:
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

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			value = (NormalQuotationContent) obj;
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

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub
			getData();
		}

	};

	private void getData()
	{
		showwait();
		RequestCenter.getQuotationDetail(quotationid, dataListener);
	}

	private void initView(View view)
	{
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		tvTitle = (TextView) view.findViewById(R.id.common_title_name);
		rlTitle = (RelativeLayout) view.findViewById(R.id.rl_common_title);

		tvTitle.setText(R.string.quotationdetail);
		tvTitle.setTextColor(getResources().getColor(R.color.color_0088f0));

		ivBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_white_btn);

		rlTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));

		// 产品资料
		tvProductName = (TextView) view.findViewById(R.id.quotation_normal_nameTextView);
		tvProductType = (TextView) view.findViewById(R.id.quotation_normal_typeTextView);
		tvProductDescription = (TextView) view.findViewById(R.id.quotation_normal_detailTextView);
		llProductType = (LinearLayout) view.findViewById(R.id.quotation_normal_title_typeLinearLayout);
		llProductDescription = (LinearLayout) view.findViewById(R.id.quotation_normal_detailLinearLayout);
		// 附件图片
		ivImage = (ImageView) view.findViewById(R.id.quotation_normal_imgImageView);
		ivImage.setOnClickListener(clickListener);
		// 报价详情
		tvTradeType = (TextView) view.findViewById(R.id.quotation_normal_tradetype_TextView);
		tvPortName = (TextView) view.findViewById(R.id.quotation_normal_portnameTextView);
		tvUnitPrice = (TextView) view.findViewById(R.id.quotation_normal_uintpriceTextView);
		tvMinOrder = (TextView) view.findViewById(R.id.quotation_normal_quanityTextView);
		tvPayment = (TextView) view.findViewById(R.id.quotation_normal_paymentTextView);

		// 附加条件
		llValidDate = (LinearLayout) view.findViewById(R.id.quotation_normal_validdateLinearLayout);
		tvValidDate = (TextView) view.findViewById(R.id.quotation_normal_validdateTextView);
		llDeliveryTime = (LinearLayout) view.findViewById(R.id.quotation_normal_dayLinearLayout);
		tvDeliveryTime = (TextView) view.findViewById(R.id.quotation_normal_dayTextView);

		llModeOfTransport = (LinearLayout) view.findViewById(R.id.quotation_normal_mode_of_transportationLinearLayout);
		tvModeOfTransport = (TextView) view.findViewById(R.id.quotation_normal_mode_of_transportationTextView);

		llModeOfPacking = (LinearLayout) view.findViewById(R.id.quotation_normal_mode_of_packingLinearLayout);
		tvModeOfPacking = (TextView) view.findViewById(R.id.quotation_normal_mode_of_packingTextView);

		llQualityInspection = (LinearLayout) view.findViewById(R.id.quotation_normal_quality_inspectionLinearLayout);
		tvQualityInspection = (TextView) view.findViewById(R.id.quotation_normal_quality_inspectionTextView);

		llFile = (LinearLayout) view.findViewById(R.id.quotation_normal_fileLinearLayout);
		tvFile = (TextView) view.findViewById(R.id.quotation_normal_fileTextView);

		// 梓品
		tvSample = (TextView) view.findViewById(R.id.quotation_normal_sampleTextView);

		llAdditionalContent = (LinearLayout) view.findViewById(R.id.quotation_normal_additionalcontentLinearLayout);
		llSamplingContent = (LinearLayout) view.findViewById(R.id.quotation_normal_sampleLinearLayout);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);
	}

	private void setData()
	{
		if (null == value.content)
			return;

		// 产品资料
		tvProductName.setText(value.content.prodName);
		if (Utils.isEmpty(value.content.prodModel))
		{
			llProductType.setVisibility(View.GONE);
		}
		else
		{
			tvProductType.setText(value.content.prodModel);
		}
		if (Utils.isEmpty(value.content.remark))
		{
			llProductDescription.setVisibility(View.GONE);
		}
		else
		{
			tvProductDescription.setText(value.content.remark);
		}

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
		tvTradeType.setText(value.content.shipmentType);
		tvPortName.setText(value.content.shipmentPort);

		String[] unit = getResources().getStringArray(R.array.priceunit_zh);
		String[] unit_value = getResources().getStringArray(R.array.priceunit_value);
		String[] minorder_value = getResources().getStringArray(R.array.minorder_value);

		for (int i = 0; i < unit_value.length; i++)
		{
			if (unit_value[i].equals(value.content.prodPricePacking_pro))
			{
				value.content.prodPricePacking_pro = unit[i];
				break;
			}
		}

		tvUnitPrice.setText(value.content.prodPrice + " " + value.content.prodPriceUnit_pro + "/"
				+ value.content.prodPricePacking_pro);

		for (int i = 0; i < minorder_value.length; i++)
		{
			if (minorder_value[i].equals(value.content.prodMinnumOrderType_pro))
			{
				value.content.prodMinnumOrderType_pro = unit[i];
				break;
			}
		}

		tvMinOrder.setText(value.content.prodMinnumOrder + " " + value.content.prodMinnumOrderType_pro);
		tvPayment.setText(value.content.paymentTerm_pro);

		// 是否显示

		if (Utils.isEmpty(value.content.quoteExpiredDate) && Utils.isEmpty(value.content.leadTime)
				&& Utils.isEmpty(value.content.deliveryMethod_pro) && Utils.isEmpty(value.content.packaging)
				&& Utils.isEmpty(value.content.qualityInspection) && Utils.isEmpty(value.content.documents))
		{
			llAdditionalContent.setVisibility(View.GONE);

		}
		else
		{
			llAdditionalContent.setVisibility(View.VISIBLE);
			// 附加条件
			if (Utils.isEmpty(value.content.quoteExpiredDate))
			{
				llValidDate.setVisibility(View.GONE);
			}
			else
			{
				tvValidDate.setText(Util.formatDate(value.content.quoteExpiredDate, "yyyy-MM-dd"));
			}
			if (Utils.isEmpty(value.content.leadTime))
			{
				llDeliveryTime.setVisibility(View.GONE);
			}
			else
			{
				tvDeliveryTime.setText(value.content.leadTime + " " + getString(R.string.unit_day));
			}

			if (!Utils.isEmpty(value.content.deliveryMethod_pro))
			{
				String[] modeoftransport = getResources().getStringArray(R.array.modeoftransport_zh);
				String[] modeoftransport_value = getResources().getStringArray(R.array.modeoftransport_value);

				for (int i = 0; i < modeoftransport_value.length; i++)
				{
					if (modeoftransport_value[i].equals(value.content.deliveryMethod_pro))
					{
						value.content.deliveryMethod_pro = modeoftransport[i];
						break;
					}
				}

				tvModeOfTransport.setText(value.content.deliveryMethod_pro);
			}
			else
			{
				llModeOfTransport.setVisibility(View.GONE);
			}

			if (!Utils.isEmpty(value.content.packaging))
			{
				if (Utils.isEmpty(value.content.packagingOther))
				{
					tvModeOfPacking.setText(splitPackaging(value.content.packaging));
				}
				else
				{
					tvModeOfPacking.setText(splitPackaging(value.content.packaging) + ","
							+ value.content.packagingOther);
				}
			}
			else if (!Utils.isEmpty(value.content.packagingOther))
			{
				tvModeOfPacking.setText(value.content.packagingOther);
			}
			else
			{
				llModeOfPacking.setVisibility(View.GONE);
			}

			if (!Utils.isEmpty(value.content.qualityInspection))
			{
				String[] qualityinspection = getResources().getStringArray(R.array.qualityinspection_zh);
				String[] qualityinspection_value = getResources().getStringArray(R.array.qualityinspection_value);
				for (int i = 0; i < qualityinspection_value.length; i++)
				{
					if (qualityinspection_value[i].equals(value.content.qualityInspection))
					{
						value.content.qualityInspection = qualityinspection[i];
						break;
					}
				}

				tvQualityInspection.setText(value.content.qualityInspection);
			}
			else
			{
				llQualityInspection.setVisibility(View.GONE);
			}

			if (!Utils.isEmpty(value.content.documents))
			{

				if (Utils.isEmpty(value.content.documentsOther))
				{
					tvFile.setText(splitDocument(value.content.documents));
				}
				else
				{
					tvFile.setText(splitDocument(value.content.documents) + "," + value.content.documentsOther);
				}
			}
			else if (!Utils.isEmpty(value.content.documentsOther))
			{
				tvFile.setText(value.content.documentsOther);
			}
			else
			{
				llFile.setVisibility(View.GONE);
			}
		}

		// 梓品
		if ("1".equals(value.content.sampleProvide))
		{
			llSamplingContent.setVisibility(View.VISIBLE);
			if ("1".equals(value.content.sampleFre))
			{
				tvSample.setText(R.string.sampling_free);
			}
			else
			{
				tvSample.setText(R.string.sampling_unfree);
			}
		}
		else
		{
			llSamplingContent.setVisibility(View.GONE);
		}

		// llSamplingContent
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

	private String splitDocument(String document)
	{
		String[] filetype = getResources().getStringArray(R.array.filetype_zh);
		String[] filetype_value = getResources().getStringArray(R.array.filetype_value);
		String[] choose = document.split(",");

		for (int i = 0; i < choose.length; i++)
		{
			for (int j = 0; j < filetype_value.length; j++)
			{
				if (filetype_value[j].equals(choose[i].trim()))
				{
					if (i == 0)
					{
						document = filetype[j];
					}
					else
					{
						document += "," + filetype[j];
					}
					break;
				}
			}
		}
		return document;
	}

	private String splitPackaging(String packaging)
	{

		String[] modesofpacking = getResources().getStringArray(R.array.modesofpacking_zh);
		String[] modesofpacking_value = getResources().getStringArray(R.array.modesofpacking_value);
		String[] choose = packaging.split(",");

		for (int i = 0; i < choose.length; i++)
		{
			for (int j = 0; j < modesofpacking_value.length; j++)
			{
				if (modesofpacking_value[j].equals(choose[i].trim()))
				{
					if (i == 0)
					{
						packaging = modesofpacking[j];
					}
					else
					{
						packaging += "," + modesofpacking[j];
					}
					break;
				}
			}
		}

		return packaging;
	}
}
