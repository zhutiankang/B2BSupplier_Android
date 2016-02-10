package com.micen.suppliers.view.purchase;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.RfqContent;
import com.micen.suppliers.module.purchase.RfqDetail;
import com.micen.suppliers.module.purchase.RfqFile;
import com.micen.suppliers.module.purchase.RfqNeedInfo;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


public class RFqInfoFragment extends Fragment
{
	private String rfqID;
	private String isRecommend;
	private String quoteSource;
	private String reqFrom;
	private String quotationid;

	private ImageView ivBack;
	private LinearLayout llBack;
	private RelativeLayout rlTitle;

	private TextView tvUrgent;
	private TextView tvRecommend;

	// 各个控件
	private ImageView imageImageView;

	private TextView nameTextView;

	private TextView purchasequantityTextView;

	private TextView datepostedTextView;
	private TextView quoteleftTextView;
	private TextView expireddateTextView;
	private TextView sourcingrequestfromTextView;

	private TextView annualpurchasevolumeTextView;
	private TextView businesstypeTextView;
	private TextView locationTextView;
	private TextView numberofemployeesTextView;
	private TextView companycertificationTextView;
	private TextView exportmarketTextView;

	private TextView shipmenttermsTextView;
	private TextView targetpriceTextView;
	private TextView destinatationportTextView;
	private TextView paymenttermsTextView;

	private TextView productdescriptionTextView;
	private TextView specificationTextView;

	private TextView quotationinfoTextView;

	private ImageView ivCountry;

	// LinearLayout

	private LinearLayout purchasequantityLinearLayout;
	private LinearLayout datepostedLinearLayout;
	private LinearLayout quoteleftLinearLayout;
	private LinearLayout expireddateLinearLayout;
	private LinearLayout sourcingrequestfromLinearLayout;

	private LinearLayout requirementsofrsupplierLineayLayout;

	private LinearLayout annualpurchaseLinearLayout;
	private LinearLayout businesstypeLinearLayout;
	private LinearLayout locationLinearLayout;
	private LinearLayout numberofemployeesLinearLayout;
	private LinearLayout companycertificationLinearLayout;
	private LinearLayout exportmarketLinearLayout;

	private LinearLayout requirementsfortradingLinearLayout;
	private LinearLayout shipmenttermsLinearLayout;
	private LinearLayout targetpriceLinearLayout;
	private LinearLayout destinatationportLinearLayout;
	private LinearLayout paymenttermsLinearLayout;

	private LinearLayout productdescriptionLinearLayout;
	private LinearLayout specificationLinearLayout;

	private Button quoteButton;

	private RelativeLayout quotationinfoRelativeLayout;

	private LinearLayout rfqfileLinearLayout;

	// RFQ附件 以下是显示多个文件，目前不显示了，只显示图片
	// private PurchaseRfqFileAdapter adapter;
	// private ListView listView;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	// 返回的详情
	private RfqContent content;

	// 带入报价的信息
	private RfqNeedInfo rfqneedinfo;

	private boolean hasQuoted;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		rfqneedinfo = new RfqNeedInfo();
		Bundle bundle = getArguments();

		if (null != bundle)
		{
			// 获取RFQID
			rfqID = bundle.getString("rfqid");
			isRecommend = bundle.getString("isrecommend");
			quoteSource = bundle.getString("quoteSource");
			quotationid = bundle.getString("quotationid");
			reqFrom = bundle.getString("reqFrom");
			if (Utils.isEmpty(isRecommend))
			{
				isRecommend = "0";
			}

			if (Utils.isEmpty(reqFrom))
			{
				reqFrom = "0";
			}

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_rfq_info, container, false);
		initView(view);
		showwait();
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
			return;
		}
		if (requestCode == 1)
		{
			hasQuoted = data.getBooleanExtra("success", false);
		}

	}

	public static RFqInfoFragment newInstance(String rfqid, String quoteSource, String isrecommend, String reqFrom,
			String quotationid)
	{
		Bundle bundle = new Bundle();
		bundle.putString("rfqid", rfqid);
		bundle.putString("quoteSource", quoteSource);
		bundle.putString("isrecommend", isrecommend);
		bundle.putString("quotationid", quotationid);
		bundle.putString("reqFrom", reqFrom);
		RFqInfoFragment fragment = new RFqInfoFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	private void initView(View view)
	{
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		ivBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_white_btn);

		rlTitle = (RelativeLayout) view.findViewById(R.id.rl_common_title);

		rlTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));

		tvUrgent = (TextView) view.findViewById(R.id.rfq_info_urgentTextView);
		tvRecommend = (TextView) view.findViewById(R.id.rfq_info_recommendTextView);

		nameTextView = (TextView) view.findViewById(R.id.rfq_info_nameTextView);

		purchasequantityTextView = (TextView) view.findViewById(R.id.rfq_info_purchasequantityTextView);
		datepostedTextView = (TextView) view.findViewById(R.id.rfq_info_datepostedTextView);
		quoteleftTextView = (TextView) view.findViewById(R.id.rfq_info_quoteleftTextView);
		expireddateTextView = (TextView) view.findViewById(R.id.rfq_info_expireddateTextView);
		sourcingrequestfromTextView = (TextView) view.findViewById(R.id.rfq_info_sourcingrequestfromTextView);

		annualpurchasevolumeTextView = (TextView) view.findViewById(R.id.rfq_info_annualpurchasevolumeTextView);
		businesstypeTextView = (TextView) view.findViewById(R.id.rfq_info_businesstypeTextView);
		locationTextView = (TextView) view.findViewById(R.id.rfq_info_locationTextView);
		numberofemployeesTextView = (TextView) view.findViewById(R.id.rfq_info_numberofemployeesTextView);
		companycertificationTextView = (TextView) view.findViewById(R.id.rfq_info_companycertificationTextView);
		exportmarketTextView = (TextView) view.findViewById(R.id.rfq_info_exportmarketTextView);

		shipmenttermsTextView = (TextView) view.findViewById(R.id.rfq_info_shipmenttermsTextView);
		targetpriceTextView = (TextView) view.findViewById(R.id.rfq_info_targetpriceTextView);
		destinatationportTextView = (TextView) view.findViewById(R.id.rfq_info_destinatationportTextView);
		paymenttermsTextView = (TextView) view.findViewById(R.id.rfq_info_paymenttermsTextView);

		productdescriptionTextView = (TextView) view.findViewById(R.id.rfq_info_productdescriptionTextView);

		specificationTextView = (TextView) view.findViewById(R.id.rfq_info_specificationTextView);

		quotationinfoTextView = (TextView) view.findViewById(R.id.rfq_info_quotationinfoTextView);

		purchasequantityLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_purchasequantityLinearLayout);
		datepostedLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_datepostedLInearLayout);
		quoteleftLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_quoteleftLinearLayout);
		expireddateLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_expireddateLinearLayout);
		sourcingrequestfromLinearLayout = (LinearLayout) view
				.findViewById(R.id.rfq_info_sourcingrequestfromLinearLayout);

		requirementsofrsupplierLineayLayout = (LinearLayout) view
				.findViewById(R.id.rfq_info_requirementsofrsupplierLineayLayout);
		annualpurchaseLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_annualpurchasevolumeLinearLayout);
		businesstypeLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_businesstypeLinearLayout);
		locationLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_locationLinearLayout);
		numberofemployeesLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_numberofemployeesLinearLayout);
		companycertificationLinearLayout = (LinearLayout) view
				.findViewById(R.id.rfq_info_companycertificationLinearLayout);
		exportmarketLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_exportmarketLinearLayout);

		requirementsfortradingLinearLayout = (LinearLayout) view
				.findViewById(R.id.rfq_info_requirementsfortradingLinearLayout);
		shipmenttermsLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_shipmenttermsLinearLayout);
		targetpriceLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_targetpriceLinearLayout);
		destinatationportLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_destinatationportLinearLayout);
		paymenttermsLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_paymenttermsLinearLayout);

		productdescriptionLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_productdescriptionLinearLayout);
		specificationLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_specificationLinearLayout);

		ivCountry = (ImageView) view.findViewById(R.id.rfq_info_countryImageView);

		quoteButton = (Button) view.findViewById(R.id.rfq_info_quoteButton);

		imageImageView = (ImageView) view.findViewById(R.id.rfq_info_imageImageView);
		imageImageView.setOnClickListener(clickListener);
		quotationinfoRelativeLayout = (RelativeLayout) view.findViewById(R.id.rfq_info_quotationinfoRelativeLayout);

		// listView = (ListView) view.findViewById(R.id.rfq_info_fileListView);

		rfqfileLinearLayout = (LinearLayout) view.findViewById(R.id.rfq_info_attachmentLinearLayout);

		// 点击事件
		quoteButton.setOnClickListener(clickListener);
		quotationinfoRelativeLayout.setOnClickListener(clickListener);
		rfqfileLinearLayout.setOnClickListener(clickListener);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);

	}

	private void getData()
	{
		RequestCenter.getRfqDetail(rfqID, isRecommend, reqFrom, dataListener);
	}

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.rfq_info_quotationinfoRelativeLayout:
				// 显示报价列表
				if (null != content && isExistrfqs(content.content))
				{
					if (!Utils.isEmpty(content.content.quotationNum) && !"0".equals(content.content.quotationNum))
					{
						showQuotationList(content.content.rfqId);
					}
					else
					{
						ToastUtil.toast(getActivity(), R.string.noquotationlist);
					}

					// quoteNow(rfqID);
				}
				else
				{
					ToastUtil.toast(getActivity(), R.string.noquotationlist);
					// showQuotationList(rfqID);
				}
				MobclickAgent.onEvent(getActivity(), "90");
				SysManager.analysis(R.string.c_type_click, R.string.c090);
				break;
			case R.id.rfq_info_quoteButton:
				if (null != content && isExistrfqs(content.content))
				{
					// quoteNow(content.content.rfqId);
					quoteNow(rfqID);
				}
				else
				{
					// quoteNow(rfqID);
				}
				MobclickAgent.onEvent(getActivity(), "91");
				SysManager.analysis(R.string.c_type_click, R.string.c091);

				break;
			case R.id.common_ll_title_back:
				getActivity().finish();
				break;
			case R.id.rfq_info_attachmentLinearLayout:
				MobclickAgent.onEvent(getActivity(), "89");
				SysManager.analysis(R.string.c_type_click, R.string.c089);
				break;
			case R.id.rfq_info_imageImageView:
				String imUri = (String) v.getTag();
				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
				intent.putExtra("fragment", "imagebrowser");
				intent.putExtra("imUri", imUri);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};

	// 请求详情
	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			content = (RfqContent) obj;

			if ("0".equals(content.code) && null != content.content)
			{
				updateUI(content.content);
				showRelatedData();
			}
			else
			{
				showNoRelatedData();
			}
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			// TODO Auto-generated method stub
			showNoRelatedData();
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub
			showNetworkError();

		}
	};

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub
			showwait();
			getData();
		}

	};

	private boolean isExistrfqs(RfqDetail detail)
	{
		return (null != detail);
	}

	private void updateUI(RfqDetail detail)
	{
		// 缺少年度 采购数量
		if (null != detail)
		{
			if (isExistrfqs(detail))
			{

				updateRfqFile(detail.attachments);

				if (!Utils.isEmpty(detail.quotationNum) && !"0".equals(detail.quotationNum))
				{
					quotationinfoTextView.setText(detail.quotationNum);
				}

				if (Utils.isEmpty(detail.estimatedQuantity) || "0".equals(detail.estimatedQuantity))
				{
					// purchasequantityLinearLayout.setVisibility(View.GONE);
					purchasequantityTextView.setText(R.string.contact_buyer);
				}
				else
				{
					purchasequantityTextView.setText(detail.estimatedQuantity + " " + detail.estimatedQuantityType);
				}

				if (!Utils.isEmpty(detail.estimatedQuantityType))
				{
					rfqneedinfo.purchaseQuantityType = detail.estimatedQuantityType;
				}

				// paymenttermsTextView.setText(detail.buyerRequest.paymentTerms);

				if (Utils.isEmpty(detail.addTime))
				{
					datepostedLinearLayout.setVisibility(View.GONE);
				}
				else
				{
					datepostedTextView.setText(Util.formatDate(detail.addTime, "yyyy-MM-dd"));
				}

				if ("1".equals(detail.isRecommended))
				{
					quoteleftTextView.setText(R.string.recommendtip);
				}
				else
				{
					if (Utils.isEmpty(detail.quoteLeft))
					{
						quoteleftLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						quoteleftTextView.setText(detail.quoteLeft);
					}
				}

				if (Utils.isEmpty(detail.validateTimeEnd))
				{
					expireddateLinearLayout.setVisibility(View.GONE);
				}
				else
				{
					expireddateTextView.setText(Util.formatDate(detail.validateTimeEnd, "yyyy-MM-dd"));
				}

				if (Utils.isEmpty(detail.detailDescription))
				{
					productdescriptionLinearLayout.setVisibility(View.GONE);

				}
				else
				{
					productdescriptionTextView.setText(detail.detailDescription);
				}

				if (Utils.isEmpty(detail.purchaseCustomProp))
				{
					specificationLinearLayout.setVisibility(View.GONE);
				}
				else
				{
					specificationTextView.setText(Html.fromHtml(detail.purchaseCustomProp));
				}

				if (Utils.isEmpty(detail.country))
				{
					sourcingrequestfromLinearLayout.setVisibility(View.GONE);
				}
				else
				{
					sourcingrequestfromTextView.setText(detail.country);
					ImageUtil.getImageLoader().displayImage(detail.countryImageUrl, ivCountry,
							ImageUtil.getCommonImageOptions());
				}

				nameTextView.setText(detail.subject);

				if ("1".equals(detail.isUrgent))
				{
					tvUrgent.setVisibility(View.VISIBLE);
				}
				else
				{
					tvUrgent.setVisibility(View.GONE);
				}

				if ("1".equals(detail.isRecommended))
				{
					tvRecommend.setVisibility(View.VISIBLE);
				}
				else
				{
					tvRecommend.setVisibility(View.GONE);
				}

				if (detail.photoList != null && detail.photoList.size() > 0)
				{
					ImageUtil.getImageLoader().displayImage(detail.photoList.get(0), imageImageView,
							ImageUtil.getRecommendImageOptions());
					imageImageView.setTag(detail.photoList.get(0));
				}
				else
				{
					imageImageView.setVisibility(View.GONE);
				}

			}

			if (null != detail.buyerRequest)
			{

				if (Utils.isEmpty(detail.buyerRequest.annualPurchaseVolume)
						&& Utils.isEmpty(detail.buyerRequest.preferredsupplierType)
						&& Utils.isEmpty(detail.buyerRequest.preferredRegion)
						&& Utils.isEmpty(detail.buyerRequest.supplierEmployeesType)
						&& Utils.isEmpty(detail.buyerRequest.supplierCertification)
						&& Utils.isEmpty(detail.buyerRequest.tradeMarket))
				{
					requirementsofrsupplierLineayLayout.setVisibility(View.GONE);

				}
				else
				{

					if (Utils.isEmpty(detail.buyerRequest.annualPurchaseVolume))
					{
						annualpurchaseLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						annualpurchasevolumeTextView.setText(detail.buyerRequest.annualPurchaseVolume);
					}

					if (Utils.isEmpty(detail.buyerRequest.preferredsupplierType))
					{
						businesstypeLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						businesstypeTextView.setText(detail.buyerRequest.preferredsupplierType);
					}

					if (Utils.isEmpty(detail.buyerRequest.preferredRegion))
					{
						locationLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						locationTextView.setText(detail.buyerRequest.preferredRegion);
					}

					if (Utils.isEmpty(detail.buyerRequest.supplierEmployeesType))
					{
						numberofemployeesLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						numberofemployeesTextView.setText(detail.buyerRequest.supplierEmployeesType);
					}

					if (Utils.isEmpty(detail.buyerRequest.supplierCertification))
					{
						companycertificationLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						companycertificationTextView.setText(detail.buyerRequest.supplierCertification);
					}

					if (Utils.isEmpty(detail.buyerRequest.tradeMarket))
					{
						exportmarketLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						exportmarketTextView.setText(detail.buyerRequest.tradeMarket);
					}
				}

				if (Utils.isEmpty(detail.buyerRequest.shipmentTerms) && Utils.isEmpty(detail.buyerRequest.targetPrice)
						&& Utils.isEmpty(detail.buyerRequest.destinationPort)
						&& Utils.isEmpty(detail.buyerRequest.paymentTerms))
				{
					//
					requirementsfortradingLinearLayout.setVisibility(View.GONE);
				}
				else
				{
					if (Utils.isEmpty(detail.buyerRequest.shipmentTerms))
					{
						shipmenttermsLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						// 贸易条款
						rfqneedinfo.shipmentTerms = detail.buyerRequest.shipmentTerms;
						shipmenttermsTextView.setText(detail.buyerRequest.shipmentTerms);
					}

					if (Utils.isEmpty(detail.buyerRequest.targetPrice))
					{
						targetpriceLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						targetpriceTextView.setText(detail.buyerRequest.targetPrice + " "
								+ detail.buyerRequest.priceType);
					}

					if (Utils.isEmpty(detail.buyerRequest.destinationPort))
					{
						destinatationportLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						rfqneedinfo.destinationPort = detail.buyerRequest.destinationPort;
						destinatationportTextView.setText(detail.buyerRequest.destinationPort);
					}

					if (Utils.isEmpty(detail.buyerRequest.paymentTerms))
					{
						paymenttermsLinearLayout.setVisibility(View.GONE);
					}
					else
					{
						rfqneedinfo.paymentTerms = detail.buyerRequest.paymentTerms;
						paymenttermsTextView.setText(detail.buyerRequest.paymentTerms);
					}
				}

				if (!Utils.isEmpty(detail.buyerRequest.priceType))
				{
					rfqneedinfo.priceType = detail.buyerRequest.priceType;
				}

			}

			if ("0".equals(content.content.canbeQuoted))
			{
				if ("采购已过期".equals(content.content.refuseReason))
				{
					// quoteButton.setEnabled(false);
					quoteButton.setVisibility(View.GONE);
				}
			}

		}
	}

	private void updateRfqFile(List<RfqFile> file)
	{
		if (null != file && file.size() > 0)
		{
			// adapter = new PurchaseRfqFileAdapter(getActivity(), file);
			// listView.setAdapter(adapter);
			rfqfileLinearLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			rfqfileLinearLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * @param rfqID RFQID
	 */
	private void showQuotationList(String rfqID)
	{
		//
		Intent intent = new Intent(getActivity(), PurchaseActivity_.class);

		intent.putExtra("fragment", "quotationlist");

		intent.putExtra("rfqid", rfqID);
		intent.putExtra("isrecommend", isRecommend);

		startActivity(intent);
	}

	/**
	 * 立即报价
	 * @param rfqID
	 */
	private void quoteNow(String rfqID)
	{
		if (hasQuoted)
		{
			CommonDialog dialog = new CommonDialog(getActivity());
			dialog.setDialogMode(true);
			dialog.setConfirmBtnText(getString(R.string.confirm)).setConfirmDialogListener(new DialogClickListener()
			{
				@Override
				public void onDialogClick()
				{

				}
			}).buildSimpleDialog(getString(R.string.hasquoted));

			return;
		}

		if ("0".equals(content.content.canbeQuoted))
		{
			CommonDialog dialog = new CommonDialog(getActivity());
			if (Utils.isEmpty(content.content.contactTelephone))
			{
				dialog.setDialogMode(true);
				dialog.setConfirmBtnText(getString(R.string.confirm))
						.setConfirmDialogListener(new DialogClickListener()
						{
							@Override
							public void onDialogClick()
							{

							}
						}).buildSimpleDialog(content.content.refuseReason);
			}
			else
			{
				dialog.setDialogMode(false);
				dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
						.setConfirmDialogListener(new DialogClickListener()
						{
							@Override
							public void onDialogClick()
							{
								String tel = content.content.contactTelephone.replace("-", "");
								Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}
						}).buildSimpleDialog(getString(R.string.freesupplier) + content.content.contactTelephone);
			}
			// ToastUtil.toast(getActivity(), content.content.refuseReason);
			return;
		}

		Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
		if ("1".equals(isRecommend))
		{
			// 委托报价
			intent.putExtra("fragment", "quotationentrust");
		}
		else
		{
			// 普通报价
			intent.putExtra("fragment", "quotationnormal");
			// intent.putExtra("fragment", "quotationentrust");
		}
		intent.putExtra("rfqid", rfqID);
		intent.putExtra("quoteSource", quoteSource);
		intent.putExtra("quotationid", quotationid);
		intent.putExtra("data", rfqneedinfo);
		startActivityForResult(intent, 1);
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

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10015));
		SysManager.analysis(R.string.p_type_page, R.string.p10015);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10015));
	}

}
