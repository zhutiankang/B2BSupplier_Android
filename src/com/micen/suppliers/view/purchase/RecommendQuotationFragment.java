package com.micen.suppliers.view.purchase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.micen.suppliers.http.purchase.EntrustQuotation;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.BaseResponse;
import com.micen.suppliers.module.message.MessageDetailUpLoadFile;
import com.micen.suppliers.module.purchase.RfqNeedInfo;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.widget.dialog.ChooseCameraOrGalleryDialog;
import com.micen.suppliers.widget.dialog.QuotationDialog;
import com.micen.suppliers.widget.product.ResizeLinearLayout;
import com.micen.suppliers.widget.product.ResizeLinearLayout.OnResizeListener;
import com.umeng.analytics.MobclickAgent;


public class RecommendQuotationFragment extends Fragment
{
	private String rfqid;

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;

	// 软件盘状态更新显示控件部分
	private ResizeLinearLayout llRecommendQuotation;
	private LinearLayout llQuotation;
	private TextView tvPreview;
	private TextView tvSend;

	private LinearLayout svLlQuotation;
	private TextView svTvPreview;
	private TextView svTvSend;

	private ScrollView svScroll;
	private TextView tvChooseFromRoom;

	private EditText etProductName;
	private EditText etUnitPrice;
	private TextView tvCurrency;
	private TextView tvUnit;

	private EditText etMinOrder;
	private TextView tvMinOrderUnit;

	private EditText etDeliveryTime;

	private LinearLayout llAddtional;
	private RelativeLayout rlAddtioinalContent;
	private CheckBox cbAddtional;

	private EditText etRemark;

	// 附件图片
	private ImageView ivAddImage;
	private ImageView ivDelImage;

	private EditText etPayment;
	private EditText etshipmentType;

	private EditText etshipmentPort;
	private TextView tvTip;
	private LinearLayout llTip;

	// 拍照 图库
	protected TakePhoto takePhoto;

	private EntrustQuotation sendData;
	private RfqNeedInfo rfqneedinfo;

	private final int REQUEST_CURRENCY = 0x1002;
	private final int REQUEST_UINT = 0x1003;
	private final int REQUEST_PAYMENT = 0x1004;
	private final int REQUEST_MINORDER_UINT = 0x1005;
	private final int REQUEST_SHIPMENTTYPE = 0x1006;
	private final int REQUEST_CHOOSEPIC = 0x1011;
	private final int REQUEST_PREVIEW = 0x1012;
	private final int REQUEST_CHOOSEPRODUCT = 0x1013;
	// 公共库拍照或者选择图片返回
	private final int REQUEST_PICRESULT = 3;

	private String mImgPath;

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
			case R.id.quotation_recommend_currencyTextView:

				/*
				 * if (null == rfqneedinfo || Utils.isEmpty(rfqneedinfo.priceType)) { showCurrencyDialog(); }
				 */
				break;
			case R.id.quotation_recommend_unitTextView:
				showUnitDialog();
				break;
			case R.id.quotation_recommend_quanityTextView:
				showMinOrderUnitDialog();
				break;
			case R.id.quotation_recommend_paymentEditText:
				showpaymentDialog();
				break;
			case R.id.quotation_recommend_shipmenttypeEditTex:
				showshipmenttypeDialog();
				break;
			case R.id.quotation_recommend_imgImageView:
				ChooseCameraOrGalleryDialog dialog = ChooseCameraOrGalleryDialog.newInstance();
				dialog.setTargetFragment(RecommendQuotationFragment.this, REQUEST_CHOOSEPIC);
				dialog.show(getFragmentManager(), null);
				break;
			case R.id.quotation_recommend_imgdelImageView:
				// 删除图片
				ivAddImage.setImageResource(R.drawable.ic_purchase_quotation_capture_add);
				ivDelImage.setVisibility(View.GONE);
				mImgPath = "";
				break;
			case R.id.quotation_recommend_chooseTextView:
				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
				intent.putExtra("fragment", "chooseproduct");
				startActivityForResult(intent, REQUEST_CHOOSEPRODUCT);
				break;
			case R.id.common_ll_title_back:
				Util.hideSoftKeyboard(getActivity());
				getActivity().finish();
				break;

			case R.id.sv_recommend_quotation_preview:
			case R.id.recommend_quotation_preview:
				if (getAllData())
				{
					// preview
					Intent preview = new Intent(getActivity(), PurchaseActivity_.class);
					preview.putExtra("value", sendData);
					preview.putExtra("fragment", "entrustquotationpreview");
					startActivityForResult(preview, REQUEST_PREVIEW);
				}
				break;
			case R.id.sv_recommend_quotation_send:
			case R.id.recommend_quotation_send:
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
						RequestCenter.sendRecommendQuotation(sendData, sendListener);
					}
				}

				break;

			case R.id.quotation_recommend_additionalLinearLayout:
				if (cbAddtional.isChecked())
				{
					cbAddtional.setChecked(false);
					rlAddtioinalContent.setVisibility(View.GONE);
				}
				else
				{
					cbAddtional.setChecked(true);
					rlAddtioinalContent.setVisibility(View.VISIBLE);
					scrollToDown();
				}
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
			mImgPath = imagePath;

		}
	};

	public static RecommendQuotationFragment newInstance(String rfqid, RfqNeedInfo info)
	{
		RecommendQuotationFragment fragment = new RecommendQuotationFragment();
		Bundle bundle = new Bundle();
		bundle.putString("rfqid", rfqid);
		bundle.putParcelable("data", info);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		sendData = new EntrustQuotation();

		Bundle bundle = getArguments();

		if (null != bundle)
		{
			rfqid = bundle.getString("rfqid");
			rfqneedinfo = bundle.getParcelable("data");
			sendData.rfqId = rfqid;
		}

		if (null != rfqneedinfo)
		{

			if (!Utils.isEmpty(rfqneedinfo.priceType))
			{
				sendData.prodPriceUnit_pro = rfqneedinfo.priceType;
			}

			if (!Utils.isEmpty(rfqneedinfo.purchaseQuantityType))
			{
				// sendData.prodpricePacking_pro = rfqneedinfo.purchaseQuantityType;
				sendData.prodMinnumOrderType_pro = rfqneedinfo.purchaseQuantityType;

				String[] unit = getResources().getStringArray(R.array.priceunit_zh);
				String[] unit_name = getResources().getStringArray(R.array.priceunit_recommend_name);
				String[] unit_value = getResources().getStringArray(R.array.priceunit_recommend_value);

				// 匹配复数
				for (int i = 0; i < unit_name.length; i++)
				{
					if (sendData.prodMinnumOrderType_pro.startsWith(unit_name[i]))
					{
						sendData.prodMinnumOrderType_pro_zh = unit[i];
						sendData.prodMinnumOrderType_pro = unit_value[i];
						break;
					}
				}

				// 根据中文匹配
				for (int i = 0; i < unit_name.length; i++)
				{
					if (unit_name[i].equals(sendData.prodMinnumOrderType_pro_zh))
					{
						sendData.prodpricePacking_pro_zh = unit[i];
						sendData.prodpricePacking_pro = unit_value[i];
						break;
					}
				}

			}

		}

		// 币种文本固定为USD
		sendData.prodPriceUnit_pro = "USD";

		if (Utils.isEmpty(sendData.prodpricePacking_pro))
		{
			sendData.prodpricePacking_pro = "0";
			sendData.prodMinnumOrderType_pro = "0";

			sendData.prodpricePacking_pro_zh = "个";
			sendData.prodMinnumOrderType_pro_zh = "个";
		}
		// sendData.rfqId = "44004";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_quotation_recommend, container, false);
		initView(view);
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
		case REQUEST_MINORDER_UINT:
			sendData.prodMinnumOrderType_pro = data.getStringExtra("value");
			sendData.prodMinnumOrderType_pro_zh = data.getStringExtra("name");
			tvMinOrderUnit.setText(sendData.prodMinnumOrderType_pro_zh);
			break;
		case REQUEST_CURRENCY:
			sendData.prodPriceUnit_pro = data.getStringExtra("value");

			tvCurrency.setText(data.getStringExtra("name"));
			break;
		case REQUEST_UINT:
			sendData.prodpricePacking_pro = data.getStringExtra("value");
			sendData.prodpricePacking_pro_zh = data.getStringExtra("name");
			tvUnit.setText(sendData.prodpricePacking_pro_zh);
			// sendData.prodMinnumOrderType_pro = sendData.prodpricePacking_pro;
			// sendData.prodMinnumOrderType_pro_zh = sendData.prodpricePacking_pro_zh;
			// tvMinOrderUnit.setText(sendData.prodpricePacking_pro_zh);
			break;
		case REQUEST_PAYMENT:
			sendData.paymentTerm_pro = data.getStringExtra("value");
			sendData.paymentTerm_pro_zh = data.getStringExtra("name");

			etPayment.setText(sendData.paymentTerm_pro_zh);

			break;
		case REQUEST_SHIPMENTTYPE:
			sendData.shipmentType = data.getStringExtra("value");
			sendData.shipmentType_zh = data.getStringExtra("name");

			etshipmentType.setText(sendData.shipmentType_zh);

			if ("FOB".equals(sendData.shipmentType))
			{
				etshipmentPort.setText("");
				etshipmentPort.setHint(R.string.portname_fob);
			}
			else
			{
				etshipmentPort.setText("");
				etshipmentPort.setHint(R.string.portname_cfr);
			}

			break;
		case REQUEST_CHOOSEPIC:
			int type = data.getIntExtra("choose", 1);
			if (1 == type)
			{
				// gallery
				takePhoto = new GalleryTakePhoto(RecommendQuotationFragment.this, null, takePhotoListener);
			}
			else
			{
				// camera
				takePhoto = new CameraTakePhoto(RecommendQuotationFragment.this, null, takePhotoListener);
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
			getActivity().setResult(1, data);
			getActivity().finish();
		case REQUEST_CHOOSEPRODUCT:
			sendData.prodId = data.getStringExtra("id");
			sendData.prodImg = data.getStringExtra("img");
			etProductName.setText(data.getStringExtra("name"));
			// 加载产品图片
			if (!Utils.isEmpty(sendData.prodImg))
			{
				ivDelImage.setVisibility(View.VISIBLE);
				ImageUtil.getImageLoader().displayImage(sendData.prodImg, ivAddImage,
						ImageUtil.getRecommendImageOptions());
			}
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
		llRecommendQuotation = (ResizeLinearLayout) view.findViewById(R.id.quotation_recommend_ll);
		llQuotation = (LinearLayout) view.findViewById(R.id.recommend_quotation_ll);
		tvPreview = (TextView) view.findViewById(R.id.recommend_quotation_preview);
		tvSend = (TextView) view.findViewById(R.id.recommend_quotation_send);
		svLlQuotation = (LinearLayout) view.findViewById(R.id.sv_recommend_quotation_ll);
		svTvPreview = (TextView) view.findViewById(R.id.sv_recommend_quotation_preview);
		svTvSend = (TextView) view.findViewById(R.id.sv_recommend_quotation_send);
		// tvPreview.setText(R.string.preview);
		// tvPreview.setTextColor(getResources().getColorStateList(R.color.bg_preview_textview));
		llRecommendQuotation.setOnResizeListener(mResizeListener);
		tvPreview.setOnClickListener(clickListener);
		tvSend.setOnClickListener(clickListener);
		svTvPreview.setOnClickListener(clickListener);
		svTvSend.setOnClickListener(clickListener);

		// tvPreview.setEnabled(false);
		// tvPreview.setVisibility(View.VISIBLE);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_btn);

		tvTitle.setText(R.string.inputentrustquotationinfo);

		svScroll = (ScrollView) view.findViewById(R.id.quotation_recommend_scrollScrollView);

		tvChooseFromRoom = (TextView) view.findViewById(R.id.quotation_recommend_chooseTextView);
		tvChooseFromRoom.setOnClickListener(clickListener);

		etProductName = (EditText) view.findViewById(R.id.quotation_recommend_nameEditText);
		etUnitPrice = (EditText) view.findViewById(R.id.quotation_recommend_uintpriceEditText);
		tvCurrency = (TextView) view.findViewById(R.id.quotation_recommend_currencyTextView);
		tvUnit = (TextView) view.findViewById(R.id.quotation_recommend_unitTextView);

		etMinOrder = (EditText) view.findViewById(R.id.quotation_recommend_quanityEditText);
		tvMinOrderUnit = (TextView) view.findViewById(R.id.quotation_recommend_quanityTextView);

		etDeliveryTime = (EditText) view.findViewById(R.id.quotation_recommend_dayEditText);

		llAddtional = (LinearLayout) view.findViewById(R.id.quotation_recommend_additionalLinearLayout);
		llAddtional.setOnClickListener(clickListener);

		rlAddtioinalContent = (RelativeLayout) view
				.findViewById(R.id.quotation_recommend_additionalcontentRelativeLayout);

		cbAddtional = (CheckBox) view.findViewById(R.id.quotation_recommend_additionalCheckBox);

		etRemark = (EditText) view.findViewById(R.id.quotation_recommend_remarkEditText);

		etRemark.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (v.getId() == R.id.quotation_recommend_remarkEditText)
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

		// 提供样品
		ivAddImage = (ImageView) view.findViewById(R.id.quotation_recommend_imgImageView);
		ivDelImage = (ImageView) view.findViewById(R.id.quotation_recommend_imgdelImageView);

		etPayment = (EditText) view.findViewById(R.id.quotation_recommend_paymentEditText);

		etshipmentType = (EditText) view.findViewById(R.id.quotation_recommend_shipmenttypeEditTex);
		etshipmentPort = (EditText) view.findViewById(R.id.quotation_recommend_portnameEditText);

		tvTip = (TextView) view.findViewById(R.id.quotation_recommend_tipTextView);
		llTip = (LinearLayout) view.findViewById(R.id.quotation_recommend_tipLinearLayout);

		// 设置事件
		ivAddImage.setOnClickListener(clickListener);
		ivDelImage.setOnClickListener(clickListener);

		tvCurrency.setOnClickListener(clickListener);
		tvUnit.setOnClickListener(clickListener);
		tvMinOrderUnit.setOnClickListener(clickListener);

		etPayment.setKeyListener(null);
		etshipmentType.setKeyListener(null);

		etPayment.setOnClickListener(clickListener);
		etshipmentType.setOnClickListener(clickListener);

		etProductName.addTextChangedListener(nameWatch);
		etUnitPrice.addTextChangedListener(priceUnitWatch);
		etMinOrder.addTextChangedListener(minOrderWatch);
		etDeliveryTime.addTextChangedListener(deliveryTimeWatch);
		etshipmentPort.addTextChangedListener(portNameWatch);

		// 初始化数据

		if (!Utils.isEmpty(sendData.prodPriceUnit_pro))
		{
			tvCurrency.setText(sendData.prodPriceUnit_pro);
		}

		if (!Utils.isEmpty(sendData.prodpricePacking_pro))
		{

			tvUnit.setText(sendData.prodpricePacking_pro_zh);
			tvMinOrderUnit.setText(sendData.prodMinnumOrderType_pro_zh);

		}
	}

	private boolean getAllData()
	{
		if (checkAllNeed())
		{
			sendData.prodName = etProductName.getText().toString();
			sendData.prodPrice = etUnitPrice.getText().toString();
			sendData.prodMinnumOrder = etMinOrder.getText().toString();
			sendData.leadTime = etDeliveryTime.getText().toString();

			if (cbAddtional.isChecked())
			{
				sendData.mAddtional = "1";
				sendData.remark = etRemark.getText().toString();
				sendData.mFilePath = mImgPath;
				sendData.shipmentPort = etshipmentPort.getText().toString();
				// sendData.paymentTerm_pro = etPayment.getText().toString();
			}
			else
			{
				sendData.mAddtional = "0";
				sendData.remark = "";
				sendData.paymentTerm_pro = "";
				sendData.shipmentType = "";
				sendData.shipmentPort = "";
				sendData.mFilePath = "";
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

		if (!checkDeliveryTime())
		{
			showEditTip(R.string.need_delivery_time);
			return false;
		}

		if (!checkPortName())
		{
			showEditTip(R.string.need_a_en_portname);
			return false;
		}

		return true;
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
		String[] unit = getResources().getStringArray(R.array.priceunit_recommend_zh);
		String[] unit_value = getResources().getStringArray(R.array.priceunit_recommend_value);
		QuotationDialog unitdialog = QuotationDialog.newInstance(title, true, unit, unit_value,
				sendData.prodpricePacking_pro);
		unitdialog.setTargetFragment(this, REQUEST_UINT);
		unitdialog.show(getFragmentManager(), null);
	}

	// REQUEST_MINORDER_UINT
	private void showMinOrderUnitDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// 0String title = getString(R.string.dialogtitle, getString(R.string.unitofmeasurement),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.unitofmeasurement));
		String[] unit = getResources().getStringArray(R.array.priceunit_recommend_zh);
		String[] unit_value = getResources().getStringArray(R.array.priceunit_recommend_value);
		QuotationDialog unitdialog = QuotationDialog.newInstance(title, true, unit, unit_value,
				sendData.prodMinnumOrderType_pro);
		unitdialog.setTargetFragment(this, REQUEST_MINORDER_UINT);
		unitdialog.show(getFragmentManager(), null);
	}

	private void showshipmenttypeDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.mode_of_transportation),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.mode_of_transportation));
		String[] shipmentType = getResources().getStringArray(R.array.tradetype_recommend_name);
		String[] shipmentType_value = getResources().getStringArray(R.array.tradetype_recommend_value);
		QuotationDialog shipmentTypedialog = QuotationDialog.newInstance(title, true, shipmentType, shipmentType_value,
				sendData.shipmentType);
		shipmentTypedialog.setTargetFragment(this, REQUEST_SHIPMENTTYPE);
		shipmentTypedialog.show(getFragmentManager(), null);
	}

	private void showpaymentDialog()
	{
		Util.hideSoftKeyboard(getActivity());
		// String title = getString(R.string.dialogtitle, getString(R.string.payment),
		// getString(R.string.singlechoose));
		String title = getString(R.string.dialogtitlenomark, getString(R.string.payment));
		String[] payment = getResources().getStringArray(R.array.payment_recommend_name);
		String[] payment_value = getResources().getStringArray(R.array.payment_recommend_value);
		QuotationDialog paymentdialog = QuotationDialog.newInstance(title, true, payment, payment_value,
				sendData.paymentTerm_pro);
		paymentdialog.setTargetFragment(this, REQUEST_PAYMENT);
		paymentdialog.show(getFragmentManager(), null);
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

	private TextWatcher deliveryTimeWatch = new TextWatcher()
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

			if (!checkPriceUnit())
			{
				showEditTip(R.string.need_a_unit);
				return;
			}

			if (!checkMinOrder())
			{
				showEditTip(R.string.need_a_quantity);
				return;
			}

			if (checkDeliveryTime())
			{
				hideEditTip();
			}
			else
			{
				showEditTip(R.string.need_delivery_time);
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

			if (!checkPriceUnit())
			{
				showEditTip(R.string.need_a_unit);
				return;
			}

			if (!checkMinOrder())
			{
				showEditTip(R.string.need_a_quantity);
				return;
			}

			if (!checkDeliveryTime())
			{
				showEditTip(R.string.need_delivery_time);
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

	private boolean checkProductName()
	{
		String str = etProductName.getText().toString().trim();

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

	private boolean checkDeliveryTime()
	{
		String str = etDeliveryTime.getText().toString().trim();

		String regEx = "[0-9]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		return m.matches();
	}

	private boolean checkPortName()
	{
		String strShipment = etshipmentType.getText().toString().trim();
		if (Utils.isEmpty(strShipment))
		{
			return true;
		}
		else
		{
			String str = etshipmentPort.getText().toString().trim();

			String regEx = "[a-zA-Z0-9 -.]+";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);

			return m.matches();
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

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10017));
		SysManager.analysis(R.string.p_type_page, R.string.p10017);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10017));
	}

	private void scrollToDown()
	{
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				// svScroll.fullScroll(ScrollView.FOCUS_DOWN);
				svScroll.scrollTo(0, svScroll.getHeight());
			}
		}, 0);
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
