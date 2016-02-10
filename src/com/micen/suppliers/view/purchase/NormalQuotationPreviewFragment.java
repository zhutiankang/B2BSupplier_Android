package com.micen.suppliers.view.purchase;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.LogUtil;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.http.purchase.NormalQuotation;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.BaseResponse;
import com.micen.suppliers.module.message.MessageDetailUpLoadFile;
import com.micen.suppliers.util.ImageUtil;
import com.umeng.analytics.MobclickAgent;


public class NormalQuotationPreviewFragment extends Fragment
{

	private boolean isEdit;
	private String quoteSource;
	private String quotationid;

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvSubmit;
	private TextView tvTitle;
	private RelativeLayout rlTitle;

	private TextView button;

	private ScrollView svScroll;

	// 附加区域
	private LinearLayout llAdditionalContent;
	private LinearLayout llValidDate;
	private LinearLayout llDeliveryTime;
	private LinearLayout llModeOfTransport;
	private LinearLayout llModeOfPacking;
	private LinearLayout llQualityInspection;
	private LinearLayout llFile;

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

	// 梓品
	private TextView tvSample;

	// 存储发送的信息
	private NormalQuotation sendData;

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
			case R.id.common_title_text_function:
				// 提交报价
				sendQuotation();
				MobclickAgent.onEvent(getActivity(), "97");
				SysManager.analysis(R.string.c_type_click, R.string.c097);
				break;
			default:
				break;
			}

		}
	};

	private DisposeDataListener uploadListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			MessageDetailUpLoadFile data = (MessageDetailUpLoadFile) obj;

			if ("0".equals(data.code))
			{
				sendData.prodPhoto = data.content;
				LogUtil.e("error", "content:" + sendData.prodPhoto);
				// 进行下面的数据上传
				if (isEdit)
				{
					RequestCenter.modifyQuotation(sendData, sendListener);
				}
				else
				{
					RequestCenter.sendQuotation(sendData, quoteSource, quotationid, sendListener);
				}
			}
			else
			{
				showError(data.err);
			}

		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			LogUtil.e("errpr", anomalyMsg.toString());
			// showError(R.string.senderror);
			showError(anomalyMsg.toString());
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			LogUtil.e("errpr", anomalyMsg.toString());
			showError(R.string.senderror);

		}
	};

	private DisposeDataListener sendListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			BaseResponse data = (BaseResponse) obj;

			if ("0".equals(data.code))
			{
				// success
				LogUtil.e("errpr", "success");
				showSuccuss();
			}
			else
			{
				showError(data.err);
				// error
				LogUtil.e("errpr", "erro");
			}
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			showError(anomalyMsg.toString());
			// showError(R.string.senderror);
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			// showError(anomalyMsg.toString());
			showError(anomalyMsg.toString());
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (null != bundle)
		{
			sendData = bundle.getParcelable("value");
			isEdit = bundle.getBoolean("edit");
			quoteSource = bundle.getString("quoteSource");
			quotationid = bundle.getString("quotationid");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_quotation_normal_preview, container, false);
		initView(view);
		setData();
		return view;
	}

	private void initView(View view)
	{

		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		tvSubmit = (TextView) view.findViewById(R.id.common_title_text_function);
		tvTitle = (TextView) view.findViewById(R.id.common_title_name);
		rlTitle = (RelativeLayout) view.findViewById(R.id.rl_common_title);

		tvTitle.setTextColor(getResources().getColor(R.color.color_0088f0));
		tvTitle.setText(R.string.quotationdetailpreview);

		tvSubmit.setText(R.string.submit);
		tvSubmit.setTextColor(getResources().getColorStateList(R.color.bg_submit_textview));
		tvSubmit.setOnClickListener(clickListener);
		tvSubmit.setVisibility(View.VISIBLE);
		tvSubmit.setBackgroundResource(R.drawable.bg_common_white_btn);

		ivBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_white_btn);

		rlTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));

		// 产品资料
		tvProductName = (TextView) view.findViewById(R.id.quotation_normal_nameTextView);
		tvProductType = (TextView) view.findViewById(R.id.quotation_normal_typeTextView);
		tvProductDescription = (TextView) view.findViewById(R.id.quotation_normal_detailTextView);

		// 附件图片
		ivImage = (ImageView) view.findViewById(R.id.quotation_normal_imgImageView);

		// 报价详情
		tvTradeType = (TextView) view.findViewById(R.id.quotation_normal_tradetype_TextView);
		tvPortName = (TextView) view.findViewById(R.id.quotation_normal_portnameTextView);
		tvUnitPrice = (TextView) view.findViewById(R.id.quotation_normal_uintpriceTextView);
		tvMinOrder = (TextView) view.findViewById(R.id.quotation_normal_quanityTextView);
		tvPayment = (TextView) view.findViewById(R.id.quotation_normal_paymentTextView);

		// 附加条件
		tvValidDate = (TextView) view.findViewById(R.id.quotation_normal_validdateTextView);
		tvDeliveryTime = (TextView) view.findViewById(R.id.quotation_normal_dayTextView);
		tvModeOfTransport = (TextView) view.findViewById(R.id.quotation_normal_mode_of_transportationTextView);
		tvModeOfPacking = (TextView) view.findViewById(R.id.quotation_normal_mode_of_packingTextView);
		tvQualityInspection = (TextView) view.findViewById(R.id.quotation_normal_quality_inspectionTextView);
		tvFile = (TextView) view.findViewById(R.id.quotation_normal_fileTextView);

		llValidDate = (LinearLayout) view.findViewById(R.id.quotation_normal_validdateLinearLayout);
		llDeliveryTime = (LinearLayout) view.findViewById(R.id.quotation_normal_dayLinearLayout);
		llModeOfTransport = (LinearLayout) view.findViewById(R.id.quotation_normal_mode_of_transportationLinearLayout);
		llModeOfPacking = (LinearLayout) view.findViewById(R.id.quotation_normal_mode_of_packingLinearLayout);
		llQualityInspection = (LinearLayout) view.findViewById(R.id.quotation_normal_quality_inspectionLinearLayout);
		llFile = (LinearLayout) view.findViewById(R.id.quotation_normal_fileLinearLayout);

		// 梓品
		tvSample = (TextView) view.findViewById(R.id.quotation_normal_sampleTextView);

		llAdditionalContent = (LinearLayout) view.findViewById(R.id.quotation_normal_additionalcontentLinearLayout);
		llSamplingContent = (LinearLayout) view.findViewById(R.id.quotation_normal_sampleLinearLayout);

	}

	public static NormalQuotationPreviewFragment newInstance(Boolean edit, NormalQuotation data, String quoteSource,
			String quotationid)
	{
		NormalQuotationPreviewFragment fragment = new NormalQuotationPreviewFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("value", data);
		bundle.putBoolean("edit", edit);
		bundle.putString("quoteSource", quoteSource);
		bundle.putString("quotationid", quotationid);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void setData()
	{
		if (null == sendData)
			return;

		// 产品资料
		tvProductName.setText(sendData.prodName);

		tvProductType.setText(sendData.prodModel);

		tvProductDescription.setText(sendData.remark);

		// 附件图片
		if (!Utils.isEmpty(sendData.mFilePath))
		{
			ivImage.setImageBitmap(BitmapFactory.decodeFile(sendData.mFilePath));
		}
		else
		{
			if (!Utils.isEmpty(sendData.prodImg))
			{
				ImageUtil.getImageLoader()
						.displayImage(sendData.prodImg, ivImage, ImageUtil.getRecommendImageOptions());
			}
			else
			{
				ivImage.setVisibility(View.GONE);
			}
		}

		// 报价详情
		tvTradeType.setText(sendData.shipmentType);

		tvPortName.setText(sendData.shipmentPort);

		tvUnitPrice.setText(sendData.prodPrice + " " + sendData.prodPriceUnit_pro + "/"
				+ sendData.prodpricePacking_pro_zh);

		tvMinOrder.setText(sendData.prodMinnumOrder + " " + sendData.prodMinnumOrderType_pro_zh);

		tvPayment.setText(sendData.paymentTerm_pro);

		// 是否显示

		if ("1".equals(sendData.mAddtional))
		{

			if (Utils.isEmpty(sendData.quoteExpiredDate_zh) && Utils.isEmpty(sendData.leadTime)
					&& Utils.isEmpty(sendData.deliveryMethod_pro_zh) && Utils.isEmpty(sendData.packaging_zh)
					&& Utils.isEmpty(sendData.qualityInspection_zh) && Utils.isEmpty(sendData.documents_zh))
			{
				llAdditionalContent.setVisibility(View.GONE);
			}
			else
			{

				llAdditionalContent.setVisibility(View.VISIBLE);
				// 附加条件
				if (Utils.isEmpty(sendData.quoteExpiredDate_zh))
				{
					llValidDate.setVisibility(View.GONE);
				}
				else
				{
					llValidDate.setVisibility(View.VISIBLE);
					tvValidDate.setText(sendData.quoteExpiredDate_zh);
				}

				if (Utils.isEmpty(sendData.leadTime))
				{
					llDeliveryTime.setVisibility(View.GONE);
				}
				else
				{
					llDeliveryTime.setVisibility(View.VISIBLE);
					tvDeliveryTime.setText(sendData.leadTime + " " + getString(R.string.unit_day));
				}

				if (Utils.isEmpty(sendData.deliveryMethod_pro_zh))
				{
					llModeOfTransport.setVisibility(View.GONE);
				}
				else
				{
					llModeOfTransport.setVisibility(View.VISIBLE);
					tvModeOfTransport.setText(sendData.deliveryMethod_pro_zh);
				}

				if (Utils.isEmpty(sendData.packaging_zh))
				{
					llModeOfPacking.setVisibility(View.GONE);
				}
				else
				{
					llModeOfPacking.setVisibility(View.VISIBLE);
					tvModeOfPacking.setText(sendData.packaging_zh);
				}

				if (Utils.isEmpty(sendData.qualityInspection_zh))
				{
					llQualityInspection.setVisibility(View.GONE);
				}
				else
				{
					llQualityInspection.setVisibility(View.VISIBLE);
					tvQualityInspection.setText(sendData.qualityInspection_zh);
				}

				if (Utils.isEmpty(sendData.documents_zh))
				{
					llFile.setVisibility(View.GONE);
				}
				else
				{
					llFile.setVisibility(View.VISIBLE);
					tvFile.setText(sendData.documents_zh);
				}
			}
		}
		else
		{
			llAdditionalContent.setVisibility(View.GONE);
		}

		// 梓品

		if ("1".equals(sendData.sampleProvide))
		{
			llSamplingContent.setVisibility(View.VISIBLE);
			if ("1".equals(sendData.sampleFre))
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

	}

	private void sendQuotation()
	{
		CommonProgressDialog.getInstance().showCancelableProgressDialog(getActivity(),
				getString(R.string.message_sent_loading));
		// 是滞存在附件
		if (!Utils.isEmpty(sendData.mFilePath))
		{

			RequestCenter.uploadFile(uploadListener, sendData.mFilePath, "quotationAttachment");
		}
		else
		{
			if (isEdit)
			{
				RequestCenter.modifyQuotation(sendData, sendListener);
			}
			else
			{
				RequestCenter.sendQuotation(sendData, quoteSource, quotationid, sendListener);
			}

		}
	}

	private void showSuccuss()
	{
		CommonProgressDialog.getInstance().dismissProgressDialog();

		if (isEdit)
		{
			Toast.makeText(getActivity(), R.string.sendquotationeditsuccess, Toast.LENGTH_LONG).show();
			// ToastUtil.toast(getActivity(), R.string.sendquotationeditsuccess);
		}
		else
		{
			Toast.makeText(getActivity(), R.string.sendquotationsuccess, Toast.LENGTH_LONG).show();
			// ToastUtil.toast(getActivity(), R.string.sendquotationsuccess);
		}

		Intent intent = new Intent();
		intent.putExtra("success", true);
		getActivity().setResult(1, intent);
		getActivity().finish();
	}

	private void showError(String str)
	{
		CommonProgressDialog.getInstance().dismissProgressDialog();
		ToastUtil.toast(getActivity(), str);
	}

	private void showError(int str)
	{
		CommonProgressDialog.getInstance().dismissProgressDialog();
		ToastUtil.toast(getActivity(), str);
	}

}
