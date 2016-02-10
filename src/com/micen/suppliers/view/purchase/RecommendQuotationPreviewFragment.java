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
import com.micen.suppliers.http.purchase.EntrustQuotation;
import com.micen.suppliers.module.BaseResponse;
import com.micen.suppliers.module.message.MessageDetailUpLoadFile;
import com.micen.suppliers.util.ImageUtil;


public class RecommendQuotationPreviewFragment extends Fragment
{
	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvSubmit;
	private TextView tvTitle;
	private RelativeLayout rlTitle;

	private String rfqid;
	private TextView button;

	private ScrollView svScroll;

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

	// 存储发送的信息
	private EntrustQuotation sendData;

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
				RequestCenter.sendRecommendQuotation(sendData, sendListener);
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
				// error
				LogUtil.e("errpr", "erro");
				showError(data.err);
			}
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			showError(anomalyMsg.toString());
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
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
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_quotation_recommend_preview, container, false);
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

		tvTitle.setText(R.string.quotationdetailpreview);
		tvTitle.setTextColor(getResources().getColor(R.color.color_0088f0));

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
		tvProductName = (TextView) view.findViewById(R.id.quotation_recommend_nameTextView);

		ivRemark = (TextView) view.findViewById(R.id.quotation_recommend_remarkTextView);

		// 附件图片
		ivImage = (ImageView) view.findViewById(R.id.quotation_recommend_imgImageView);

		// 报价详情
		tvUnitPrice = (TextView) view.findViewById(R.id.quotation_recommend_uintpriceTextView);
		tvMinOrder = (TextView) view.findViewById(R.id.quotation_recommend_quanityTextView);
		tvPayment = (TextView) view.findViewById(R.id.quotation_recommend_paymentTextView);

		// 附加条件
		tvDeliveryTime = (TextView) view.findViewById(R.id.quotation_recommend_dayTextView);
		tvModeOfTransport = (TextView) view.findViewById(R.id.quotation_recommend_mode_of_transportationTextView);

		llRemark = (LinearLayout) view.findViewById(R.id.quotation_recommend_remarkLinearLayout);
		llPayment = (LinearLayout) view.findViewById(R.id.quotation_recommend_paymentLinearLayout);
		llModeOfTransport = (LinearLayout) view
				.findViewById(R.id.quotation_recommend_mode_of_transportationLinearLayout);

		llAdditionalContent = (LinearLayout) view.findViewById(R.id.quotation_recommend_additionalcontentLinearLayout);

	}

	public static RecommendQuotationPreviewFragment newInstance(EntrustQuotation data)
	{
		RecommendQuotationPreviewFragment fragment = new RecommendQuotationPreviewFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("value", data);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void setData()
	{
		if (null == sendData)
			return;

		// 产品资料
		tvProductName.setText(sendData.prodName);

		// ivImage

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
		tvUnitPrice.setText(sendData.prodPrice + " " + sendData.prodPriceUnit_pro + "/"
				+ sendData.prodpricePacking_pro_zh);
		tvMinOrder.setText(sendData.prodMinnumOrder + " " + sendData.prodMinnumOrderType_pro_zh);
		tvDeliveryTime.setText(sendData.leadTime + " " + getString(R.string.unit_day));

		// 是否显示
		// 附加条件
		if ("1".equals(sendData.mAddtional))
		{
			if (Utils.isEmpty(sendData.remark) && Utils.isEmpty(sendData.paymentTerm_pro_zh)
					&& Utils.isEmpty(sendData.shipmentType) && Utils.isEmpty(sendData.shipmentPort))
			{
				llAdditionalContent.setVisibility(View.GONE);
			}
			else
			{

				llAdditionalContent.setVisibility(View.VISIBLE);

				if (Utils.isEmpty(sendData.remark))
				{
					llRemark.setVisibility(View.GONE);
				}
				else
				{
					llRemark.setVisibility(View.VISIBLE);
					ivRemark.setText(sendData.remark);
				}

				if (Utils.isEmpty(sendData.paymentTerm_pro_zh))
				{
					llPayment.setVisibility(View.GONE);
				}
				else
				{
					llPayment.setVisibility(View.VISIBLE);
					tvPayment.setText(sendData.paymentTerm_pro_zh);
				}

				if (Utils.isEmpty(sendData.shipmentType))
				{
					if (Utils.isEmpty(sendData.shipmentPort))
					{
						llModeOfTransport.setVisibility(View.GONE);
					}
					else
					{
						llModeOfTransport.setVisibility(View.VISIBLE);
						tvModeOfTransport.setText(sendData.shipmentPort);
					}
				}
				else
				{
					llModeOfTransport.setVisibility(View.VISIBLE);
					if (Utils.isEmpty(sendData.shipmentPort))
					{
						tvModeOfTransport.setText(sendData.shipmentType_zh);
					}
					else
					{
						tvModeOfTransport.setText(sendData.shipmentType_zh + "/" + sendData.shipmentPort);
					}
				}
			}

		}
		else
		{
			llAdditionalContent.setVisibility(View.GONE);
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
			RequestCenter.sendRecommendQuotation(sendData, sendListener);
		}
	}

	private void showSuccuss()
	{
		CommonProgressDialog.getInstance().dismissProgressDialog();
		Toast.makeText(getActivity(), R.string.sendquotationsuccess, Toast.LENGTH_LONG).show();
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
