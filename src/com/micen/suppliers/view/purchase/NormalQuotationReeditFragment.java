package com.micen.suppliers.view.purchase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.focustech.common.capturepicture.CameraTakePhoto;
import com.focustech.common.capturepicture.GalleryTakePhoto;
import com.focustech.common.capturepicture.TakePhoto;
import com.focustech.common.capturepicture.TakePhoto.TakePhotoFailReason;
import com.focustech.common.capturepicture.TakePhoto.TakePhotoListener;
import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.LogUtil;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.http.purchase.NormalQuotation;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.BaseResponse;
import com.micen.suppliers.module.message.MessageDetailUpLoadFile;
import com.micen.suppliers.module.purchase.NormalQuotationContent;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.micen.suppliers.widget.dialog.ChooseCameraOrGalleryDialog;
import com.micen.suppliers.widget.dialog.DatePickerFragment;
import com.micen.suppliers.widget.dialog.QuotationDialog;
import com.micen.suppliers.widget.product.ResizeLinearLayout;
import com.micen.suppliers.widget.product.ResizeLinearLayout.OnResizeListener;
import com.umeng.analytics.MobclickAgent;


public class NormalQuotationReeditFragment extends Fragment
{
	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;
	// 软件盘状态更新显示控件部分
	private ResizeLinearLayout llReeditQuotation;
	private LinearLayout llQuotation;
	private TextView tvPreview;
	private TextView tvSend;

	private LinearLayout svLlQuotation;
	private TextView svTvPreview;
	private TextView svTvSend;

	private String rfqid;
	private TextView button;

	private ScrollView svScroll;

	// 附加区域
	private LinearLayout llAdditional;
	private RelativeLayout rlAdditionalContent;
	private LinearLayout llSamplingIndicator;
	private LinearLayout llSampling;
	private LinearLayout llSamplingContent;
	private LinearLayout llSamplingFreeContent;

	// 产品资料
	private EditText etProductName;
	private EditText etProductType;
	private EditText etProductDescription;

	// 附件图片
	private ImageView ivAddImage;
	private ImageView ivDelImage;

	// 报价详情
	private EditText etTradeType;
	private EditText etPortName;
	private EditText etUnitPrice;
	private EditText etMinOrder;
	private EditText etPayment;

	private TextView tvCurrency;
	private TextView tvUnit;
	private TextView tvMinOrderUnit;

	// 附加条件
	private CheckBox cbAdditional;

	private EditText etValidDate;
	private EditText etDeliveryTime;
	private EditText etModeOfTransport;
	private EditText etModeOfPacking;
	private EditText etQualityInspection;
	private EditText etFile;

	// 提供样品
	private CheckBox cbSampling;
	private CheckBox cbisSampling;
	private CheckBox cbisFreeSample;

	// 错误提示
	private TextView tvTip;
	private LinearLayout llTip;

	// 存储发送的信息
	private NormalQuotation sendData;

	// 采购ID
	private String quotationid;
	// 返回的数据
	private NormalQuotationContent value;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	// 拍照 图库
	protected TakePhoto takePhoto;

	private final int REQUEST_TRADETYPE = 0x1001;
	private final int REQUEST_CURRENCY = 0x1002;
	private final int REQUEST_UINT = 0x1003;
	private final int REQUEST_PAYMENT = 0x1004;
	private final int REQUEST_VALIDDATE = 0x1005;
	private final int REQUEST_MODEOFTRANSPORTATION = 0x1006;
	private final int REQUEST_MODEOFPACKING = 0x1007;
	private final int REQUEST_QUALITYINSPECTION = 0x1008;
	private final int REQUEST_FILE = 0x1010;
	private final int REQUEST_CHOOSEPIC = 0x1011;
	private final int REQUEST_MINORDER_UINT = 0x1012;
	private final int REQUEST_PREVIEW = 0x1013;
	private final int REQUEST_CHOOSEPRODUCT = 0x1014;

	// 公共库拍照或者选择图片返回
	private final int REQUEST_PICRESULT = 3;
	// 软件盘监控部分
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int MSG_RESIZE = 1;

	private Handler handler = new Handler();
	private InputHandler mHandler = new InputHandler();

	private OnResizeListener mResizeListener = new OnResizeListener()
	{

		public void OnResize(int w, int h, int oldw, int oldh)
		{
			int change = BIGGER;
			if (h < oldh)
			{
				change = SMALLER;
			}
			Message msg = new Message();
			msg.what = MSG_RESIZE;
			msg.arg1 = change;
			mHandler.sendMessage(msg);
		}
	};

	class InputHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_RESIZE:
			{
				if (msg.arg1 == BIGGER)
				{
					// 软键盘消失
					if (View.VISIBLE != llQuotation.getVisibility())
					{
						llQuotation.setVisibility(View.VISIBLE);
						svLlQuotation.setVisibility(View.GONE);
					}
				}
				else if (msg.arg1 == SMALLER)
				{
					// 软键盘出现
					if (View.VISIBLE != svLlQuotation.getVisibility())
					{
						svLlQuotation.setVisibility(View.VISIBLE);
						llQuotation.setVisibility(View.GONE);
					}
				}
			}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
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
			case R.id.quotation_normal_chooseTextView:
				// ChooseProductFromRoomFragment
				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
				intent.putExtra("fragment", "chooseproduct");
				startActivityForResult(intent, REQUEST_CHOOSEPRODUCT);
				break;
			case R.id.sv_reedit_quotation_preview:
			case R.id.reedit_quotation_preview:
				// preview
				if (getAllData())
				{
					Intent preview = new Intent(getActivity(), PurchaseActivity_.class);
					preview.putExtra("fragment", "normalquotationpreview");
					preview.putExtra("value", sendData);
					preview.putExtra("edit", true);
					startActivityForResult(preview, REQUEST_PREVIEW);
				}
				break;
			case R.id.sv_reedit_quotation_send:
			case R.id.reedit_quotation_send:
				if (getAllData())
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
						RequestCenter.modifyQuotation(sendData, sendListener);

					}
				}
				break;
			case R.id.quotation_normal_imgImageView:
				// 增加图片
				ChooseCameraOrGalleryDialog dialog = ChooseCameraOrGalleryDialog.newInstance();
				dialog.setTargetFragment(NormalQuotationReeditFragment.this, REQUEST_CHOOSEPIC);
				dialog.show(getFragmentManager(), null);
				// PopupManager.getInstance().showRFQPopup(createPopupView(createCapturePopupView(v), popupBgClick));
				break;
			case R.id.quotation_normal_imgdelImageView:
				// 删除图片
				ivAddImage.setImageResource(R.drawable.ic_purchase_quotation_capture_add);
				ivDelImage.setVisibility(View.GONE);
				sendData.mFilePath = null;
				sendData.prodId = null;

				break;

			case R.id.quotation_normal_additionalLinearLayout:

				if (cbAdditional.isChecked())
				{
					sendData.mAddtional = "0";
					cbAdditional.setChecked(false);
					rlAdditionalContent.setVisibility(View.GONE);
				}
				else
				{
					sendData.mAddtional = "1";
					cbAdditional.setChecked(true);
					rlAdditionalContent.setVisibility(View.VISIBLE);
					// svScroll.scrollTo(0, svScroll.getHeight());
					// svScroll.scrollBy(0, llAdditionalContent.getHeight());
					scrollToDown();
				}
				MobclickAgent.onEvent(getActivity(), "95");
				SysManager.analysis(R.string.c_type_click, R.string.c095);
				break;

			case R.id.quotation_normal_sampling_indicator_LinearLayout:
				if (cbSampling.isChecked())
				{
					cbSampling.setChecked(false);
					// cbisFreeSample.setChecked(false);
					llSamplingContent.setVisibility(View.GONE);
				}
				else
				{
					cbSampling.setChecked(true);
					llSamplingContent.setVisibility(View.VISIBLE);
					// svScroll.scrollTo(0, svScroll.getHeight());
					// svScroll.scrollBy(0, llSamplingContent.getHeight());
					scrollToDown();
				}
				break;

			case R.id.quotation_normal_samplingLinearLayout:
				if (cbisSampling.isChecked())
				{
					cbisSampling.setChecked(false);
					cbisFreeSample.setChecked(false);
					llSamplingFreeContent.setVisibility(View.GONE);
				}
				else
				{
					llSamplingFreeContent.setVisibility(View.VISIBLE);
					cbisSampling.setChecked(true);
					scrollToDown();
				}
				break;
			case R.id.quotation_normal_samplingfreeLinearLayout:
				if (cbisFreeSample.isChecked())
				{
					cbisFreeSample.setChecked(false);
				}
				else
				{
					cbisFreeSample.setChecked(true);
				}
				break;
			case R.id.quotation_normal_tradetype_EditText:
				// 贸易条款
				showTradeTypeDialog();
				break;
			case R.id.quotation_normal_currencyTextView:
				// 货币类型

				showCurrencyDialog();

				break;
			case R.id.quotation_normal_unitTextView:
				// 计量单位
				showUnitDialog();
				break;

			case R.id.quotation_normal_quanityTextView:

				showMinOrderUnitDialog();
				break;

			case R.id.quotation_normal_paymentEditText:
				// 支付方式
				showpaymentDialog();

				break;
			case R.id.quotation_normal_validdateEditText:
				// 有效日期
				showDatePickerDialog();

				break;
			case R.id.quotation_normal_mode_of_transportationEditText:
				// 运输类型
				showmodeoftransportationDialog();

				break;
			case R.id.quotation_normal_mode_of_packingEditText:
				// 包装类型
				showmodeofpackingDialog();

				break;
			case R.id.quotation_normal_quality_inspectionEditText:
				// 质量检测
				showqualityinspectionDialog();

				break;
			case R.id.quotation_normal_fileEditText:
				// 包含文件
				showfileDialog();

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
				RequestCenter.modifyQuotation(sendData, sendListener);
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
	protected TakePhotoListener takePhotoListener = new TakePhotoListener()
	{
		@Override
		public void onSuccess(String imagePath, View displayView, Bitmap bitmap)
		{
			// 根据传入的控件ID区分
		}

		@Override
		public void onFail(TakePhotoFailReason failReason)
		{
			if (failReason == TakePhotoFailReason.SDCardNotFound)
			{
				ToastUtil.toast(getActivity(), R.string.nosdcard);
			}
		}

		@Override
		public void onSuccess(String imagePath, View displayView, Bitmap bitmap, Long fileSize)
		{
			// TODO Auto-generated method stub
			// 根据传入的控件ID区分
			ivDelImage.setVisibility(View.VISIBLE);
			ivAddImage.setImageBitmap(bitmap);
			sendData.prodImg = "";
			sendData.mFilePath = imagePath;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sendData = new NormalQuotation();
		Bundle bundle = getArguments();
		if (null != bundle)
		{
			sendData.rfqId = bundle.getString("rfqid");
			quotationid = bundle.getString("quotationid");
			sendData.quotationid = bundle.getString("quotationid");
		}

		sendData.prodPriceUnit_pro = "USD";

		sendData.prodpricePacking_pro = "Piece";
		sendData.prodMinnumOrderType_pro = "Pieces";

		sendData.prodpricePacking_pro_zh = "个";
		sendData.prodMinnumOrderType_pro_zh = "个";

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_quotation_normal_reedit, container, false);
		initView(view);
		getData();
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (null == data)
		{
			if (1 == requestCode)
			{
				if (null != takePhoto)
				{
					takePhoto.onActivityResult(requestCode, resultCode, data);
				}
			}
			return;
		}
		switch (requestCode)
		{
		case REQUEST_CHOOSEPRODUCT:
			// 展示厅返回
			sendData.prodId = data.getStringExtra("id");
			sendData.prodImg = data.getStringExtra("img");
			sendData.prodModel = data.getStringExtra("mode");
			sendData.mFilePath = "";

			etProductName.setText(data.getStringExtra("name"));
			etProductType.setText(sendData.prodModel);
			// 加载产品图片
			if (!Utils.isEmpty(sendData.prodImg))
			{
				ivDelImage.setVisibility(View.VISIBLE);
				ImageUtil.getImageLoader().displayImage(sendData.prodImg, ivAddImage,
						ImageUtil.getRecommendImageOptions());
			}
			// .setImageBitmap(bitmap);
			break;
		case REQUEST_TRADETYPE:
			// 贸易条款
			hideEditTip(R.string.need_trade_type);
			sendData.shipmentType = data.getStringExtra("name");
			etTradeType.setText(sendData.shipmentType);
			break;
		case REQUEST_CURRENCY:
			sendData.prodPriceUnit_pro = data.getStringExtra("name");
			tvCurrency.setText(sendData.prodPriceUnit_pro);
			break;
		case REQUEST_UINT:
			sendData.prodpricePacking_pro = data.getStringExtra("value");
			sendData.prodpricePacking_pro_zh = data.getStringExtra("name");
			tvUnit.setText(sendData.prodpricePacking_pro_zh);

			// sendData.prodMinnumOrderType_pro = sendData.prodpricePacking_pro;
			// sendData.prodMinnumOrderType_pro_zh = sendData.prodpricePacking_pro_zh;
			// tvMinOrderUnit.setText(sendData.prodMinnumOrderType_pro_zh);

			break;

		case REQUEST_MINORDER_UINT:
			sendData.prodMinnumOrderType_pro = data.getStringExtra("value");
			sendData.prodMinnumOrderType_pro_zh = data.getStringExtra("name");

			tvMinOrderUnit.setText(sendData.prodMinnumOrderType_pro_zh);
			break;
		case REQUEST_PAYMENT:
			hideEditTip(R.string.need_payment);
			sendData.paymentTerm_pro = data.getStringExtra("name");
			etPayment.setText(sendData.paymentTerm_pro);
			break;
		case REQUEST_VALIDDATE:
			sendData.quoteExpiredDate_zh = data.getStringExtra("name");
			sendData.quoteExpiredDate = data.getStringExtra("value");
			etValidDate.setText(sendData.quoteExpiredDate_zh);
			break;
		case REQUEST_MODEOFTRANSPORTATION:
			sendData.deliveryMethod_pro = data.getStringExtra("value");
			sendData.deliveryMethod_pro_zh = data.getStringExtra("name");
			etModeOfTransport.setText(sendData.deliveryMethod_pro_zh);
			break;
		case REQUEST_MODEOFPACKING:
			sendData.packaging = data.getStringExtra("value");
			sendData.packaging_zh = data.getStringExtra("name");
			etModeOfPacking.setText(sendData.packaging_zh);
			break;
		case REQUEST_QUALITYINSPECTION:
			sendData.qualityInspection = data.getStringExtra("value");
			sendData.qualityInspection_zh = data.getStringExtra("name");
			etQualityInspection.setText(sendData.qualityInspection_zh);
			break;
		case REQUEST_FILE:
			sendData.documents = data.getStringExtra("value");
			sendData.documents_zh = data.getStringExtra("name");
			etFile.setText(sendData.documents_zh);
			break;
		case REQUEST_CHOOSEPIC:
			int type = data.getIntExtra("choose", 1);
			if (1 == type)
			{
				// gallery
				takePhoto = new GalleryTakePhoto(NormalQuotationReeditFragment.this, null, takePhotoListener);
			}
			else
			{
				// camera
				takePhoto = new CameraTakePhoto(NormalQuotationReeditFragment.this, null, takePhotoListener);
			}
			break;
		case REQUEST_PICRESULT:

			if (null != takePhoto)
			{
				takePhoto.onActivityResult(requestCode, resultCode, data);
			}
			else
			{
				LogUtil.e("takephot", "takephoto is null");
			}
			break;
		case REQUEST_PREVIEW:
			// 报价功能返回
			getActivity().finish();
			break;
		default:
			break;
		}
	}

	private void initView(View view)
	{
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		tvTitle = (TextView) view.findViewById(R.id.common_title_name);

		llReeditQuotation = (ResizeLinearLayout) view.findViewById(R.id.quotation_reedit_ll);
		llQuotation = (LinearLayout) view.findViewById(R.id.reedit_quotation_ll);
		tvPreview = (TextView) view.findViewById(R.id.reedit_quotation_preview);
		tvSend = (TextView) view.findViewById(R.id.reedit_quotation_send);
		svLlQuotation = (LinearLayout) view.findViewById(R.id.sv_reedit_quotation_ll);
		svTvPreview = (TextView) view.findViewById(R.id.sv_reedit_quotation_preview);
		svTvSend = (TextView) view.findViewById(R.id.sv_reedit_quotation_send);

		// tvPreview.setText(R.string.preview);
		// tvPreview.setTextColor(getResources().getColorStateList(R.color.bg_preview_textview));
		llReeditQuotation.setOnResizeListener(mResizeListener);
		tvPreview.setOnClickListener(clickListener);
		tvSend.setOnClickListener(clickListener);
		svTvPreview.setOnClickListener(clickListener);
		svTvSend.setOnClickListener(clickListener);
		// tvPreview.setEnabled(false);
		// tvPreview.setVisibility(View.VISIBLE);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_btn);

		tvTitle.setText(R.string.inputquotationinfo);

		button = (TextView) view.findViewById(R.id.quotation_normal_chooseTextView);
		button.setOnClickListener(clickListener);

		svScroll = (ScrollView) view.findViewById(R.id.quotation_normal_scrollScrollView);

		llSamplingIndicator = (LinearLayout) view.findViewById(R.id.quotation_normal_sampling_indicator_LinearLayout);
		llSampling = (LinearLayout) view.findViewById(R.id.quotation_normal_samplingLinearLayout);
		llSamplingContent = (LinearLayout) view.findViewById(R.id.quotation_normal_samplingcontentLinearLayout);
		llSamplingFreeContent = (LinearLayout) view.findViewById(R.id.quotation_normal_samplingfreeLinearLayout);
		llAdditional = (LinearLayout) view.findViewById(R.id.quotation_normal_additionalLinearLayout);
		rlAdditionalContent = (RelativeLayout) view.findViewById(R.id.quotation_normal_additionalcontentLinearLayout);

		llSamplingIndicator.setOnClickListener(clickListener);
		llSampling.setOnClickListener(clickListener);
		llSamplingFreeContent.setOnClickListener(clickListener);

		llAdditional.setOnClickListener(clickListener);

		// 产品资料
		etProductName = (EditText) view.findViewById(R.id.quotation_normal_nameEditText);
		etProductType = (EditText) view.findViewById(R.id.quotation_normal_typeEditText);
		etProductDescription = (EditText) view.findViewById(R.id.quotation_normal_detailEditText);
		etProductDescription.setMovementMethod(new ScrollingMovementMethod());

		etProductDescription.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (v.getId() == R.id.quotation_normal_detailEditText)
				{
					v.getParent().requestDisallowInterceptTouchEvent(true);
					switch (event.getAction() & MotionEvent.ACTION_MASK)
					{
					case MotionEvent.ACTION_UP:
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
					}
				}
				return false;
			}
		});

		ivAddImage = (ImageView) view.findViewById(R.id.quotation_normal_imgImageView);
		ivDelImage = (ImageView) view.findViewById(R.id.quotation_normal_imgdelImageView);

		ivAddImage.setOnClickListener(clickListener);
		ivDelImage.setOnClickListener(clickListener);
		// 报价详情

		etTradeType = (EditText) view.findViewById(R.id.quotation_normal_tradetype_EditText);
		etPortName = (EditText) view.findViewById(R.id.quotation_normal_portnameEditText);
		etUnitPrice = (EditText) view.findViewById(R.id.quotation_normal_uintpriceEditText);
		etMinOrder = (EditText) view.findViewById(R.id.quotation_normal_quanityEditText);
		etPayment = (EditText) view.findViewById(R.id.quotation_normal_paymentEditText);

		tvCurrency = (TextView) view.findViewById(R.id.quotation_normal_currencyTextView);
		tvUnit = (TextView) view.findViewById(R.id.quotation_normal_unitTextView);
		tvMinOrderUnit = (TextView) view.findViewById(R.id.quotation_normal_quanityTextView);

		// 附加条件
		cbAdditional = (CheckBox) view.findViewById(R.id.quotation_normal_additionalCheckBox);

		etValidDate = (EditText) view.findViewById(R.id.quotation_normal_validdateEditText);
		etDeliveryTime = (EditText) view.findViewById(R.id.quotation_normal_dayEditText);
		etModeOfTransport = (EditText) view.findViewById(R.id.quotation_normal_mode_of_transportationEditText);
		etModeOfPacking = (EditText) view.findViewById(R.id.quotation_normal_mode_of_packingEditText);
		etQualityInspection = (EditText) view.findViewById(R.id.quotation_normal_quality_inspectionEditText);
		etFile = (EditText) view.findViewById(R.id.quotation_normal_fileEditText);
		// 提供样品
		cbSampling = (CheckBox) view.findViewById(R.id.quotation_normal_sampling_indicator_CheckBox);
		cbisSampling = (CheckBox) view.findViewById(R.id.quotation_normal_samplingCheckBox);
		cbisFreeSample = (CheckBox) view.findViewById(R.id.quotation_normal_freeCheckBox);

		tvTip = (TextView) view.findViewById(R.id.quotation_normal_tipTextView);
		llTip = (LinearLayout) view.findViewById(R.id.quotation_normal_tipLinearLayout);
		// 初始化数据

		if (!Utils.isEmpty(sendData.shipmentType))
		{
			etTradeType.setText(sendData.shipmentType);
		}

		if (!Utils.isEmpty(sendData.shipmentPort))
		{
			etPortName.setText(sendData.shipmentPort);
		}

		if (!Utils.isEmpty(sendData.prodPriceUnit_pro))
		{
			tvCurrency.setText(sendData.prodPriceUnit_pro);
		}

		if (!Utils.isEmpty(sendData.paymentTerm_pro))
		{
			etPayment.setText(sendData.paymentTerm_pro);
		}

		if (!Utils.isEmpty(sendData.prodpricePacking_pro))
		{

			tvUnit.setText(sendData.prodpricePacking_pro_zh);
			tvMinOrderUnit.setText(sendData.prodMinnumOrderType_pro_zh);

		}

		// 点击事件

		etTradeType.setOnClickListener(clickListener);
		tvCurrency.setOnClickListener(clickListener);
		tvUnit.setOnClickListener(clickListener);
		tvMinOrderUnit.setOnClickListener(clickListener);
		etPayment.setOnClickListener(clickListener);
		etValidDate.setOnClickListener(clickListener);
		etModeOfTransport.setOnClickListener(clickListener);

		// 包装方式
		etModeOfPacking.setOnClickListener(clickListener);
		etQualityInspection.setOnClickListener(clickListener);
		etFile.setOnClickListener(clickListener);

		etTradeType.setKeyListener(null);
		etPayment.setKeyListener(null);
		etValidDate.setKeyListener(null);
		etModeOfTransport.setKeyListener(null);
		etModeOfPacking.setKeyListener(null);
		etQualityInspection.setKeyListener(null);
		etFile.setKeyListener(null);

		etTradeType.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					showTradeTypeDialog();
				}

			}
		});

		etPayment.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					showpaymentDialog();
				}

			}
		});
		etValidDate.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					showDatePickerDialog();
				}

			}
		});
		etModeOfTransport.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					showmodeoftransportationDialog();
				}

			}
		});
		etModeOfPacking.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					showmodeofpackingDialog();
				}

			}
		});
		etQualityInspection.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					showqualityinspectionDialog();
				}

			}
		});
		etFile.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				// TODO Auto-generated method stub
				if (hasFocus)
				{
					showfileDialog();
				}

			}
		});

		etProductName.addTextChangedListener(nameWatch);
		etProductDescription.addTextChangedListener(detailWatch);

		etPortName.addTextChangedListener(portNameWatch);

		etUnitPrice.addTextChangedListener(priceUnitWatch);
		etMinOrder.addTextChangedListener(minOrderWatch);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);

	}

	public static NormalQuotationReeditFragment newInstance(String rfqid, String quotationId)
	{
		NormalQuotationReeditFragment fragment = new NormalQuotationReeditFragment();
		Bundle bundle = new Bundle();
		bundle.putString("rfqid", rfqid);
		bundle.putString("quotationid", quotationId);
		fragment.setArguments(bundle);
		return fragment;
	}

	private boolean getAllData()
	{
		if (checkAllNeed())
		{
			sendData.prodName = etProductName.getText().toString();
			sendData.prodModel = etProductType.getText().toString();
			sendData.remark = etProductDescription.getText().toString();

			// 报价详情

			sendData.shipmentType = etTradeType.getText().toString();
			sendData.shipmentPort = etPortName.getText().toString();
			sendData.prodPrice = etUnitPrice.getText().toString();
			sendData.prodMinnumOrder = etMinOrder.getText().toString();
			sendData.paymentTerm_pro = etPayment.getText().toString();

			sendData.prodPriceUnit_pro = tvCurrency.getText().toString();

			// 附加条件
			if (cbAdditional.isChecked())
			{
				sendData.mAddtional = "1";
				sendData.leadTime = etDeliveryTime.getText().toString();

			}
			else
			{
				sendData.mAddtional = "0";
				sendData.leadTime = "";
			}

			// 提供样品
			if (cbisSampling.isChecked())
			{
				sendData.sampleProvide = "1";
			}
			else
			{
				sendData.sampleProvide = "0";
				sendData.sampleFre = "0";
			}
			if (cbisFreeSample.isChecked())
			{
				sendData.sampleFre = "1";
			}
			else
			{
				sendData.sampleFre = "0";
			}
			return true;
		}
		else
		{
			return false;
		}

	}

	private boolean checkAllNeed()
	{
		if (!checkProductName())
		{
			showEditTip(R.string.needaname);
			return false;
		}

		if (!checkDetail())
		{
			showEditTip(R.string.needdetail);
			return false;
		}

		if (!checkShipmentType())
		{
			showEditTip(R.string.need_trade_type);
			return false;
		}

		if (!checkPortName())
		{
			showEditTip(R.string.need_a_en_portname);
			return false;
		}

		if (!checkPriceUnit())
		{
			showEditTip(R.string.need_a_unit);
			return false;
		}

		if (!checkMinOrder())
		{
			showEditTip(R.string.need_a_quantity);
			return false;
		}

		if (!checkPayment())
		{
			showEditTip(R.string.need_payment);
			return false;
		}

		return true;
	}

	private void showTradeTypeDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.trade_type),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.trade_type));

		String[] tradetype = getResources().getStringArray(R.array.tradetype_name);
		String[] tradetype_value = getResources().getStringArray(R.array.tradetype_value);
		QuotationDialog tradetypedialog = QuotationDialog.newInstance(title, true, tradetype, tradetype_value,
				sendData.shipmentType);
		tradetypedialog.setTargetFragment(this, REQUEST_TRADETYPE);
		tradetypedialog.show(getFragmentManager(), null);
	}

	private void showCurrencyDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.currency_type),
		// getString(R.string.singlechoose));

		String title = getString(R.string.dialogtitlenomark, getString(R.string.currency_type));
		String[] currency = getResources().getStringArray(R.array.currency_name);
		String[] currency_value = getResources().getStringArray(R.array.currency_value);
		QuotationDialog currencydialog = QuotationDialog.newInstance(title, true, currency, currency_value,
				sendData.prodPriceUnit_pro);
		currencydialog.setTargetFragment(this, REQUEST_CURRENCY);
		currencydialog.show(getFragmentManager(), null);
	}

	private void showUnitDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.unitofmeasurement),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.unitofmeasurement));
		String[] unit = getResources().getStringArray(R.array.priceunit_zh);
		String[] unit_value = getResources().getStringArray(R.array.priceunit_value);
		QuotationDialog unitdialog = QuotationDialog.newInstance(title, true, unit, unit_value,
				sendData.prodpricePacking_pro);
		unitdialog.setTargetFragment(this, REQUEST_UINT);
		unitdialog.show(getFragmentManager(), null);
	}

	private void showMinOrderUnitDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.unitofmeasurement),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.unitofmeasurement));
		String[] unit = getResources().getStringArray(R.array.priceunit_zh);
		String[] unit_value = getResources().getStringArray(R.array.minorder_value);
		QuotationDialog unitdialog = QuotationDialog.newInstance(title, true, unit, unit_value,
				sendData.prodMinnumOrderType_pro);
		unitdialog.setTargetFragment(this, REQUEST_MINORDER_UINT);
		unitdialog.show(getFragmentManager(), null);
	}

	private void showpaymentDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.payment),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.payment));
		String[] payment = getResources().getStringArray(R.array.payment_name);
		String[] payment_value = getResources().getStringArray(R.array.payment_value);
		QuotationDialog paymentdialog = QuotationDialog.newInstance(title, true, payment, payment_value,
				sendData.paymentTerm_pro);
		paymentdialog.setTargetFragment(this, REQUEST_PAYMENT);
		paymentdialog.show(getFragmentManager(), null);
	}

	private void showDatePickerDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle);
		DialogFragment newFragment = DatePickerFragment.newInstance(getString(R.string.offer_valid_to));
		newFragment.setTargetFragment(this, REQUEST_VALIDDATE);
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void showmodeoftransportationDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.mode_of_transportation),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.mode_of_transportation));
		String[] modeoftransport = getResources().getStringArray(R.array.modeoftransport_zh);
		String[] modeoftransport_value = getResources().getStringArray(R.array.modeoftransport_value);
		QuotationDialog odeoftransportdialog = QuotationDialog.newInstance(title, true, modeoftransport,
				modeoftransport_value, sendData.deliveryMethod_pro);
		odeoftransportdialog.setTargetFragment(this, REQUEST_MODEOFTRANSPORTATION);
		odeoftransportdialog.show(getFragmentManager(), null);
	}

	private void showmodeofpackingDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		String title = getString(R.string.dialogtitle, getString(R.string.modes_of_Packing),
				getString(R.string.multichoose));
		String[] modesofpacking = getResources().getStringArray(R.array.modesofpacking_zh);
		String[] modesofpacking_value = getResources().getStringArray(R.array.modesofpacking_value);
		QuotationDialog modesofpackingdialog = QuotationDialog.newInstance(title, false, modesofpacking,
				modesofpacking_value, sendData.packaging);
		modesofpackingdialog.setTargetFragment(this, REQUEST_MODEOFPACKING);
		modesofpackingdialog.show(getFragmentManager(), null);
	}

	private void showqualityinspectionDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.quality_inspection),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.quality_inspection));
		String[] qualityinspection = getResources().getStringArray(R.array.qualityinspection_zh);
		String[] qualityinspection_value = getResources().getStringArray(R.array.qualityinspection_value);
		QuotationDialog qualityinspectiondialog = QuotationDialog.newInstance(title, true, qualityinspection,
				qualityinspection_value, sendData.qualityInspection);
		qualityinspectiondialog.setTargetFragment(this, REQUEST_QUALITYINSPECTION);
		qualityinspectiondialog.show(getFragmentManager(), null);
	}

	private void showfileDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		String title = getString(R.string.dialogtitle, getString(R.string.file), getString(R.string.multichoose));
		String[] filetype = getResources().getStringArray(R.array.filetype_zh);
		String[] filetype_value = getResources().getStringArray(R.array.filetype_value);
		QuotationDialog filetypedialog = QuotationDialog.newInstance(title, false, filetype, filetype_value,
				sendData.documents);
		filetypedialog.setTargetFragment(this, REQUEST_FILE);
		filetypedialog.show(getFragmentManager(), null);
	}

	private TextWatcher nameWatch = new TextWatcher()
	{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub
			if (checkProductName())
			{
				hideEditTip();
			}
			else
			{
				showEditTip(R.string.needaname);
			}
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub

		}

	};

	private TextWatcher detailWatch = new TextWatcher()
	{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

			if (!checkProductName())
			{
				showEditTip(R.string.needaname);
				return;
			}

			if (checkDetail())
			{
				hideEditTip();
			}
			else
			{
				showEditTip(R.string.needdetail);
			}

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub

		}

	};

	private TextWatcher portNameWatch = new TextWatcher()
	{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

			if (!checkProductName())
			{
				showEditTip(R.string.needaname);
				return;
			}

			if (!checkDetail())
			{
				showEditTip(R.string.needdetail);
				return;
			}

			if (checkPortName())
			{
				hideEditTip();
			}
			else
			{
				showEditTip(R.string.need_a_en_portname);
			}

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub

		}

	};

	private TextWatcher priceUnitWatch = new TextWatcher()
	{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

			if (!checkProductName())
			{
				showEditTip(R.string.needaname);
				return;
			}

			if (!checkDetail())
			{
				showEditTip(R.string.needdetail);
				return;
			}

			if (!checkPortName())
			{
				showEditTip(R.string.need_a_en_portname);
				return;
			}

			if (checkPriceUnit())
			{
				hideEditTip();
			}
			else
			{
				showEditTip(R.string.need_a_unit);
			}

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub

		}

	};

	private TextWatcher minOrderWatch = new TextWatcher()
	{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

			if (!checkProductName())
			{
				showEditTip(R.string.needaname);
				return;
			}

			if (!checkDetail())
			{
				showEditTip(R.string.needdetail);
				return;
			}

			if (!checkPortName())
			{
				showEditTip(R.string.need_a_en_portname);
				return;
			}

			if (!checkPriceUnit())
			{
				showEditTip(R.string.need_a_unit);
				return;
			}

			if (checkMinOrder())
			{
				hideEditTip();
			}
			else
			{
				showEditTip(R.string.need_a_quantity);
			}

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub

		}

	};

	private boolean checkProductName()
	{
		String str = etProductName.getText().toString().trim();

		String regEx = "[a-zA-Z0-9 -.]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.matches();

	}

	private boolean checkDetail()
	{
		String str = etProductDescription.getText().toString().trim();

		// String regEx = "[a-zA-Z0-9 ~!@#$%^&*()_+'{}|\":?<>;\\,./=\r\n\\x5b\\x5d]+";
		String regEx = "[\\x0-\\x7f]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.matches();
	}

	private boolean checkShipmentType()
	{
		String str = etTradeType.getText().toString().trim();

		if (Utils.isEmpty(str))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private boolean checkPortName()
	{
		String str = etPortName.getText().toString().trim();

		String regEx = "[a-zA-Z0-9 -.]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.matches();
	}

	private boolean checkPriceUnit()
	{
		String str = etUnitPrice.getText().toString().trim();

		String regEx = "[a-zA-Z0-9 -.]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.matches();
	}

	private boolean checkMinOrder()
	{
		String str = etMinOrder.getText().toString().trim();

		String regEx = "[a-zA-Z0-9 -.]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.matches();
	}

	private boolean checkPayment()
	{
		String str = etPayment.getText().toString().trim();

		if (Utils.isEmpty(str))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void showEditTip(int value)
	{
		tvTip.setText(value);
		llTip.setVisibility(View.VISIBLE);
	}

	private void hideEditTip()
	{
		llTip.setVisibility(View.GONE);
	}

	private void hideEditTip(int value)
	{
		if (tvTip.getText().toString().equals(getString(value)))
		{
			llTip.setVisibility(View.GONE);
		}
	}

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub
			getData();
		}

	};

	private void setData()
	{
		if (null == value.content)
			return;

		// 产品资料
		etProductName.setText(value.content.prodName);
		etProductType.setText(value.content.prodModel);
		etProductDescription.setText(value.content.remark);

		// 附件图片
		// ivImage
		/*
		 * if (Utils.isEmpty(value.content.prodPhoto)) {
		 * ImageUtil.getImageLoader().displayImage(value.content.prodPhoto, ivAddImage,
		 * ImageUtil.getRecommendImageOptions()); ivDelImage.setVisibility(View.VISIBLE); }
		 */

		// 报价详情
		etTradeType.setText(value.content.shipmentType);
		etPortName.setText(value.content.shipmentPort);
		etUnitPrice.setText(value.content.prodPrice);

		sendData.prodPriceUnit_pro = value.content.prodPriceUnit_pro;
		tvCurrency.setText(sendData.prodPriceUnit_pro);

		String[] unit = getResources().getStringArray(R.array.priceunit_zh);
		String[] unit_value = getResources().getStringArray(R.array.priceunit_value);
		String[] minorder_value = getResources().getStringArray(R.array.minorder_value);

		if (!Utils.isEmpty(value.content.prodPricePacking_pro))
		{
			sendData.prodpricePacking_pro = value.content.prodPricePacking_pro;
			for (int i = 0; i < unit_value.length; i++)
			{
				if (unit_value[i].equals(sendData.prodpricePacking_pro))
				{
					sendData.prodpricePacking_pro_zh = unit[i];
					break;
				}
			}
			tvUnit.setText(sendData.prodpricePacking_pro_zh);
		}

		if (!Utils.isEmpty(value.content.prodMinnumOrderType_pro))
		{
			sendData.prodMinnumOrderType_pro = value.content.prodMinnumOrderType_pro;
			for (int i = 0; i < minorder_value.length; i++)
			{
				if (minorder_value[i].equals(sendData.prodMinnumOrderType_pro))
				{
					sendData.prodMinnumOrderType_pro_zh = unit[i];
					break;
				}
			}
			tvMinOrderUnit.setText(sendData.prodMinnumOrderType_pro_zh);
		}

		etMinOrder.setText(value.content.prodMinnumOrder);
		etPayment.setText(value.content.paymentTerm_pro);

		// 是否显示

		if (Utils.isEmpty(value.content.quoteExpiredDate) && Utils.isEmpty(value.content.leadTime)
				&& Utils.isEmpty(value.content.deliveryMethod_pro) && Utils.isEmpty(value.content.packaging)
				&& Utils.isEmpty(value.content.qualityInspection) && Utils.isEmpty(value.content.documents))
		{
			rlAdditionalContent.setVisibility(View.GONE);
			sendData.mAddtional = "0";
			cbAdditional.setChecked(false);

		}
		else
		{
			sendData.mAddtional = "1";
			cbAdditional.setChecked(true);
			rlAdditionalContent.setVisibility(View.VISIBLE);
			// 附加条件
			if (!Utils.isEmpty(value.content.quoteExpiredDate))
			{
				sendData.quoteExpiredDate = value.content.quoteExpiredDate;
				sendData.quoteExpiredDate_zh = Util.formatDate(value.content.quoteExpiredDate, "yyyy-MM-dd");
				etValidDate.setText(sendData.quoteExpiredDate_zh);
			}

			etDeliveryTime.setText(value.content.leadTime);

			if (!Utils.isEmpty(value.content.deliveryMethod_pro))
			{
				sendData.deliveryMethod_pro = value.content.deliveryMethod_pro;

				String[] modeoftransport = getResources().getStringArray(R.array.modeoftransport_zh);
				String[] modeoftransport_value = getResources().getStringArray(R.array.modeoftransport_value);

				for (int i = 0; i < modeoftransport_value.length; i++)
				{
					if (modeoftransport_value[i].equals(sendData.deliveryMethod_pro))
					{
						sendData.deliveryMethod_pro_zh = modeoftransport[i];
						break;
					}
				}

				etModeOfTransport.setText(sendData.deliveryMethod_pro_zh);
			}

			if (!Utils.isEmpty(value.content.packaging))
			{
				sendData.packaging = value.content.packaging;

				String[] modesofpacking = getResources().getStringArray(R.array.modesofpacking_zh);
				String[] modesofpacking_value = getResources().getStringArray(R.array.modesofpacking_value);
				String[] choose = sendData.packaging.split(",");

				for (int i = 0; i < choose.length; i++)
				{
					for (int j = 0; j < modesofpacking_value.length; j++)
					{
						if (modesofpacking_value[j].equals(choose[i].trim()))
						{
							if (i == 0)
							{
								sendData.packaging_zh = modesofpacking[j];
							}
							else
							{
								sendData.packaging_zh += "," + modesofpacking[j];
							}
							break;
						}
					}
				}
				etModeOfPacking.setText(sendData.packaging_zh);
			}
			if (!Utils.isEmpty(value.content.qualityInspection))
			{
				sendData.qualityInspection = value.content.qualityInspection;

				String[] qualityinspection = getResources().getStringArray(R.array.qualityinspection_zh);
				String[] qualityinspection_value = getResources().getStringArray(R.array.qualityinspection_value);
				for (int i = 0; i < qualityinspection_value.length; i++)
				{
					if (qualityinspection_value[i].equals(sendData.qualityInspection))
					{
						sendData.qualityInspection_zh = qualityinspection[i];
						break;
					}
				}

				etQualityInspection.setText(sendData.qualityInspection_zh);
			}

			if (!Utils.isEmpty(value.content.documents))
			{
				sendData.documents = value.content.documents;

				String[] filetype = getResources().getStringArray(R.array.filetype_zh);
				String[] filetype_value = getResources().getStringArray(R.array.filetype_value);
				String[] choose = sendData.documents.split(",");

				for (int i = 0; i < choose.length; i++)
				{
					for (int j = 0; j < filetype_value.length; j++)
					{
						if (filetype_value[j].equals(choose[i].trim()))
						{
							if (i == 0)
							{
								sendData.documents_zh = filetype[j];
							}
							else
							{
								sendData.documents_zh += "," + filetype[j];
							}
							break;
						}
					}
				}

				etFile.setText(sendData.documents_zh);
			}
		}

		// 梓品
		if ("1".equals(value.content.sampleProvide))
		{
			cbSampling.setChecked(true);
			llSamplingContent.setVisibility(View.VISIBLE);
			llSamplingFreeContent.setVisibility(View.VISIBLE);
			cbisSampling.setChecked(true);
			if ("1".equals(value.content.sampleFre))
			{
				cbisFreeSample.setChecked(true);
			}
		}
		else
		{
			llSamplingContent.setVisibility(View.GONE);
		}

		etProductName.requestFocus();
		// llSamplingContent
	}

	private void getData()
	{
		showwait();
		RequestCenter.getQuotationDetail(quotationid, dataListener);
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

	private void showSuccuss()
	{
		CommonProgressDialog.getInstance().dismissProgressDialog();
		Toast.makeText(getActivity(), R.string.sendquotationeditsuccess, Toast.LENGTH_LONG).show();
		// ToastUtil.toast(getActivity(), R.string.sendquotationeditsuccess);
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

	private void scrollToDown()
	{
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				// svScroll.scrollTo(0, svScroll.getHeight());
				svScroll.scrollBy(0, svScroll.getHeight());
			}
		}, 0);

	}
}
