package com.micen.suppliers.http;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.focustech.common.CommonConfigurationHelper;
import com.focustech.common.http.CommonJsonHttpResponseHandler;
import com.focustech.common.http.FocusClient;
import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.mob.MobRequestCenter;
import com.focustech.common.util.MobileUtil;
import com.focustech.common.util.Utils;
import com.loopj.android.http.RequestParams;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.http.purchase.EntrustQuotation;
import com.micen.suppliers.http.purchase.NormalQuotation;
import com.micen.suppliers.module.BaseResponse;
import com.micen.suppliers.module.message.MessageBehaviorRecords;
import com.micen.suppliers.module.message.MessageContacts;
import com.micen.suppliers.module.message.MessageDetailUpLoadFile;
import com.micen.suppliers.module.message.MessageDetails;
import com.micen.suppliers.module.message.MessageSalesmans;
import com.micen.suppliers.module.message.Messages;
import com.micen.suppliers.module.product.ProductDetails;
import com.micen.suppliers.module.promotion.SystemPromotion;
import com.micen.suppliers.module.purchase.CategoryContent;
import com.micen.suppliers.module.purchase.NormalQuotationContent;
import com.micen.suppliers.module.purchase.ProductContent;
import com.micen.suppliers.module.purchase.PurchaseContent;
import com.micen.suppliers.module.purchase.QuotationAllContent;
import com.micen.suppliers.module.purchase.QuotationDetailContent;
import com.micen.suppliers.module.purchase.QuotationList;
import com.micen.suppliers.module.purchase.RecommendQuotationContent;
import com.micen.suppliers.module.purchase.RfqContent;
import com.micen.suppliers.module.purchase.RfqNeedQuoted;
import com.micen.suppliers.module.purchase.SearchResultContent;
import com.micen.suppliers.module.user.User;
import com.tencent.android.tpush.XGPushConfig;


public class RequestCenter
{
	/**
	 * 用户登录请求
	 * @param username	用户名
	 * @param password	密码
	 * @param lisener	登录结果回调
	 */
	public static void login(String username, String password, DisposeDataListener lisener)
	{
		RequestParams params = new RequestParams();
		params.put("loginId", username);
		params.put("password", password);
		params.put("region", "MIC for Supplier Android");
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.USER_LOGIN, params, new CommonJsonHttpResponseHandler(lisener, User.class));
	}

	/**
	 * 用户详细信息请求
	 * @param lisener
	 */
	public static void profile(DisposeDataListener lisener)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient
				.post(UrlConstants.GET_STAFF_PROFILE, params, new CommonJsonHttpResponseHandler(lisener, User.class));
	}

	/**
	 * 注册设备到移动服务端
	 */
	public static void registerDevice()
	{
		MobRequestCenter.register(new SimpleDisposeDataListener());
	}

	/**
	 * 推送功能绑定账户
	 * @param lisener
	 * @param isBind
	 */
	public static void boundAccount(DisposeDataListener lisener, boolean isBind)
	{
		MobRequestCenter.boundAccountOrNot(
				isBind ? SupplierApplication.getInstance().getUser().content.userInfo.fullName : null,
				isBind ? SupplierApplication.getInstance().getUser().content.companyInfo.companyId : null,
				isBind ? SupplierApplication.getInstance().getUser().content.companyInfo.operatorId : null, lisener);
	}

	// begin 询盘
	/**
	 * 获取收发件箱列表
	 * @param lisener
	 * @param pageIndex
	 * @param pageSize
	 * @param action 0：收件箱 1：发件箱
	 * @param allocateStatus 分配状态，0 ：全部 1：未分配 2：已分配
	 * @param readStatus 读取状态，0：全部 1：未读 2：已读
	 * @param sendTime 每页最后一个询盘的发送时间（时间戳格式），请求第一页时可不传递
	 */
	public static void getMessageList(DisposeDataListener listener, String pageIndex, String pageSize, String action,
			String allocateStatus, String readStatus, String sendTime)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
		params.put("allocateStatus", allocateStatus);
		params.put("readStatus", readStatus);
		params.put("action", action);
		params.put("sendTime", sendTime);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_MESSAGE_LIST, params, new CommonJsonHttpResponseHandler(listener,
				Messages.class));
	}

	/**
	 * 获取收发件箱详情
	 * @param lisener
	 * @param mailId
	 * @param action
	 */
	public static void getMessageDetail(DisposeDataListener listener, String mailId, String action)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("mailId", mailId);
		params.put("action", action);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_MESSAGE_DETAIL, params, new CommonJsonHttpResponseHandler(listener,
				MessageDetails.class));
	}

	/**
	 * 删除邮件
	 * @param lisener
	 * @param mailId
	 * @param action 选择删除0：收件箱 1：发件箱的邮件
	 */
	public static void deleteMessage(DisposeDataListener listener, String mailId, String action)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("mailId", mailId);
		params.put("action", action);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.DELETE_MESSAGE, params, new CommonJsonHttpResponseHandler(listener,
				BaseResponse.class));
	}

	/**
	 * 获取业务员
	 * @param lisener
	 */
	public static void getSalesman(DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_SALESMAN, params, new CommonJsonHttpResponseHandler(listener,
				MessageSalesmans.class));
	}

	/**
	 * 分配业务员
	 * @param lisener
	 * @param salesmanOperatorId
	 * @param mailId 要分配的询盘 支持批量，多个参数用“,”隔开
	 */
	public static void assignSalesman(DisposeDataListener listener, String salesmanOperatorId, String mailId)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("salesmanOperatorId", salesmanOperatorId);
		params.put("mailId", mailId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.ASSIGN_SALESMAN, params, new CommonJsonHttpResponseHandler(listener,
				BaseResponse.class));

	}

	/**
	 * 回复询盘
	 * @param lisener
	 * @param mailId
	 * @param subject
	 * @param body
	 * @param attachmentID
	 */
	public static void replyMessage(DisposeDataListener listener, String mailId, String subject, String body,
			String attachmentID, String region)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("detailId", mailId);
		params.put("subject", subject);
		params.put("body", body);
		params.put("attachmentID", attachmentID);
		params.put("region", region);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.SEND_INQUIRY, params, new CommonJsonHttpResponseHandler(listener,
				BaseResponse.class));

	}

	/**
	 * 上传附件
	 * @param lisener
	 * @param filePath
	 * @param contentType "quotationAttachment"或者"messageAttachment"
	 */
	public static void uploadFile(DisposeDataListener listener, String filePath, String contentType)
	{

		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		File file = new File(filePath);
		if (!file.exists())
		{
			return;
		}
		params.put("originalFileName", file.getName());
		params.put("contentType", contentType);
		params.put("name", file.getName().substring(0, file.getName().indexOf(".")));
		try
		{
			params.put("uploadPhoto", file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		params.put("picWidth", String.valueOf(options.outWidth));
		params.put("picHeight", String.valueOf(options.outHeight));
		params.put("fileSize", String.valueOf(file.length()));
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.UPLOAD_FILE, params, new CommonJsonHttpResponseHandler(listener,
				MessageDetailUpLoadFile.class));
	}

	/**
	 * 买家行为记录
	 * @param lisener
	 * @param businessId 询盘ID，或者通讯录ID
	 * @param sourceType 请求来源 1：询盘收件箱 2：询盘发件箱 3：通讯录
	 */
	public static void getBuyerBehaviour(DisposeDataListener listener, String businessId, String sourceType)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("businessId", businessId);
		params.put("sourceType", sourceType);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_BUYERBEHAVIOUR, params, new CommonJsonHttpResponseHandler(listener,
				MessageBehaviorRecords.class));
	}

	/**
	 * 获取通讯录
	 * @param lisener
	 * @param pageIndex
	 * @param pageSize 请求条数，默认20条
	 */
	public static void getContacts(DisposeDataListener lisener, int pageIndex, int pageSize)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("pageIndex", "");
		params.put("pageSize", "");
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_CONTACTS, params, new CommonJsonHttpResponseHandler(lisener,
				MessageContacts.class));
	}

	/**
	 * 增加到通讯录
	 * @param lisener
	 * @param mailId
	 */
	public static void addContactPerson(DisposeDataListener lisener, String mailId)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("mailId", mailId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.ADD_CONTACTS, params, new CommonJsonHttpResponseHandler(lisener,
				BaseResponse.class));
	}

	/**
	 * 从通讯录删除
	 * @param lisener
	 * @param contactId 要删除的联系人ID 支持批量，多个参数用“,”隔开
	 */
	public static void deleteContactPerson(DisposeDataListener lisener, String contactId)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("contactId", contactId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.DELETE_CONTACTS, params, new CommonJsonHttpResponseHandler(lisener,
				BaseResponse.class));
	}

	// end 询盘

	// begin 产品

	/**
	 * 获取产品详情
	 * @param context
	 * @param lisener
	 * @param productId
	 */
	public static void getProductDetail(DisposeDataListener lisener, String productId)
	{
		RequestParams params = new RequestParams();
		params.put("productId", productId);
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_PRODUCTDETAIL, params, new CommonJsonHttpResponseHandler(lisener,
				ProductDetails.class));
	}

	// begin我的活动

	/**
	 * 获取我的活动列表数据
	 * @param lisener
	 * @param pageIndex
	 * @param pageSize
	 */
	public static void getMyActivities(String pageIndex, String pageSize, DisposeDataListener lisener)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);
	}

	// begin 采购

	/**
	 * @param type rfq类型，1：最紧急 2：最新
	 * @param pageNum
	 * @param pageSize
	 * @param listener
	 */
	public static void getRfqList(int rfqType, int pageNum, int pageSize, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("rfqType", String.valueOf(rfqType));
		params.put("pageIndex", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_RFQ_LIST, params, new CommonJsonHttpResponseHandler(listener,
				PurchaseContent.class));
	}

	/**
	 * 获取系统推广信息
	 * @param lisener
	 */
	public static void getSystemPromotion(DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();
		params.add("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.add("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_SYSTEM_PROMOTION, params, new CommonJsonHttpResponseHandler(listener,
				SystemPromotion.class));
	}

	public static void getRfqDetail(String rfqID, String isRecommend, String reqFrom, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("rfqId", rfqID);
		params.put("isRecommended", isRecommend);

		params.put("reqFrom", reqFrom);

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_RFQ_DETAIL, params, new CommonJsonHttpResponseHandler(listener,
				RfqContent.class));
	}

	public static void getCategoryList(DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("catCode", "0");
		params.put("catLevel", "1");
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_CATEGORYLIST, params, new CommonJsonHttpResponseHandler(listener,
				CategoryContent.class));
	}

	public static void searchRfq(String keywords, String country, String category, String postDate, int pageNum,
			int pageSize, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("keywords", keywords);
		params.put("country", country);
		params.put("category", category);
		params.put("postDate", postDate);

		params.put("pageNum", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);

		FocusClient.post(UrlConstants.SEARCH_RFQ, params, new CommonJsonHttpResponseHandler(listener,
				SearchResultContent.class));
	}

	public static void getRelatedQuotations(String rfqID, String isrecommend, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.add("rfqId", rfqID);
		params.add("isRecommended", isrecommend);

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_RELATED_QUOTATION, params, new CommonJsonHttpResponseHandler(listener,
				QuotationList.class));
	}

	/**
	 * 获取公司产品列表
	 * @param keywords
	 * @param groupid
	 * @param masterid
	 * @param producttype
	 * @param productstar
	 * @param pageNum
	 * @param pageSize
	 * @param listener
	 */
	public static void getProductList(String keywords, String groupid, String masterid, String producttype,
			String productstar, int pageNum, int pageSize, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		if (!Utils.isEmpty(keywords))
		{
			params.put("keyWords", keywords);
		}

		if (!Utils.isEmpty(groupid))
		{
			params.put("groupId", groupid);
		}

		if (!Utils.isEmpty(masterid))
		{
			params.put("masterOperatorId", masterid);
		}

		if (!Utils.isEmpty(producttype))
		{
			params.put("productType", producttype);
		}

		if (!Utils.isEmpty(productstar))
		{
			params.put("productStar", productstar);
		}

		params.put("pageIndex", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);

		FocusClient.post(UrlConstants.GET_PRODUCT_LIST, params, new CommonJsonHttpResponseHandler(listener,
				ProductContent.class));
	}

	public static void getRfqNeedQuote(int pageNum, int pageSize, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("pageIndex", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_RFQ_NEED_QUOTE, params, new CommonJsonHttpResponseHandler(listener,
				RfqNeedQuoted.class));
	}

	/**
	 * 获取管理报价
	 * @param type 报价状态，0：已报价，2：待审核 3：需要修改 ，4：已过期，5：已冻结
	 * @param pageNum
	 * @param pageSize
	 * @param listener
	 */
	public static void getQuotations(String type, int pageNum, int pageSize, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("quotationStatus", type);

		params.put("pageIndex", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_QUOTATIONS, params, new CommonJsonHttpResponseHandler(listener,
				QuotationDetailContent.class));
	}

	/**
	 * 获取所有报价
	 * @param pageNum
	 * @param pageSize
	 * @param listener
	 */
	public static void getAllQuotations(int pageNum, int pageSize, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("pageIndex", String.valueOf(pageNum));
		params.put("pageSize", String.valueOf(pageSize));

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_ALL_QUOTATIONS, params, new CommonJsonHttpResponseHandler(listener,
				QuotationAllContent.class));
	}

	public static void sendQuotation(NormalQuotation quotation, String quoteSource, String quotationid,
			DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("rfqId", quotation.rfqId);

		// 这边有一个QuotationID
		params.put("quotationId", quotationid);

		// 报价来源 0：来自vo 1：来自采购频道 2：来自供应商专题
		params.put("quoteSource", quoteSource);

		params.put("prodName", quotation.prodName);

		// 下面3个可选
		params.put("prodModel", quotation.prodModel);

		if (Utils.isEmpty(quotation.prodImg))
		{
			params.put("prodPhoto", quotation.prodPhoto);
		}
		else
		{
			// 产品ID
			params.put("prodId", quotation.prodId);
		}

		params.put("remark", quotation.remark);
		params.put("shipmentType", quotation.shipmentType);
		params.put("shipmentPort", quotation.shipmentPort);

		params.put("prodPrice", quotation.prodPrice);
		params.put("prodPriceUnit_pro", quotation.prodPriceUnit_pro);
		params.put("prodpricePacking_pro", quotation.prodpricePacking_pro);

		params.put("prodMinnumOrder", quotation.prodMinnumOrder);
		params.put("prodMinnumOrderType_pro", quotation.prodMinnumOrderType_pro);
		params.put("paymentTerm_pro", quotation.paymentTerm_pro);

		// 下面是可选
		if ("1".equals(quotation.mAddtional))
		{
			params.put("quoteExpiredDate", quotation.quoteExpiredDate);
			params.put("leadTime", quotation.leadTime);
			params.put("deliveryMethod_pro", quotation.deliveryMethod_pro);
			params.put("packaging", quotation.packaging);
			params.put("qualityInspection", quotation.qualityInspection);
			params.put("documents", quotation.documents);
		}

		params.put("sampleProvide", quotation.sampleProvide);
		params.put("sampleFre", quotation.sampleFre);
		params.put("region", "MIC for Supplier Android");
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.SEND_QUOTATION, params, new CommonJsonHttpResponseHandler(listener,
				BaseResponse.class));
	}

	public static void sendRecommendQuotation(EntrustQuotation quotation, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("purchaseId", quotation.rfqId);

		if ("1".equals(quotation.mAddtional))
		{
			if (Utils.isEmpty(quotation.prodImg))
			{
				params.put("prodPhoto", quotation.prodPhoto);
			}
			else
			{
				params.put("prodId", quotation.prodId);
			}
		}
		params.put("prodName", quotation.prodName);
		params.put("prodMinnumOrder", quotation.prodMinnumOrder);
		params.put("prodMinnumOrderType_pro", quotation.prodMinnumOrderType_pro);
		params.put("prodPrice", quotation.prodPrice);
		params.put("prodPriceUnit_pro", quotation.prodPriceUnit_pro);
		params.put("prodpricePacking_pro", quotation.prodpricePacking_pro);
		params.put("remark", quotation.remark);

		params.put("leadTime", quotation.leadTime);
		params.put("paymentTerm_pro", quotation.paymentTerm_pro);
		params.put("shipmentPort", quotation.shipmentPort);
		params.put("shipmentType", quotation.shipmentType);
		params.put("region", "MIC for Supplier Android");
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.SEND_RECOMMEND_QUOTATION, params, new CommonJsonHttpResponseHandler(listener,
				BaseResponse.class));
	}

	public static void getQuotationDetail(String quotationID, DisposeDataListener listener)
	{

		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("quotationId", quotationID);

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_QUOTATIONDETAIL, params, new CommonJsonHttpResponseHandler(listener,
				NormalQuotationContent.class));
	}

	public static void getRecommendQuotationDetail(String quotationID, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("quotationId", quotationID);

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.GET_RECOMMEND_QUOTATIONDETAIL, params, new CommonJsonHttpResponseHandler(
				listener, RecommendQuotationContent.class));
	}

	public static void modifyQuotation(NormalQuotation quotation, DisposeDataListener listener)
	{
		RequestParams params = new RequestParams();

		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);

		params.put("rfqId", quotation.rfqId);

		params.put("quotationId", quotation.quotationid);

		params.put("prodName", quotation.prodName);

		// 下面3个可选
		params.put("prodModel", quotation.prodModel);

		if (Utils.isEmpty(quotation.prodImg))
		{
			params.put("prodPhoto", quotation.prodPhoto);
		}
		else
		{
			// 产品ID
			params.put("prodId", quotation.prodId);
		}

		params.put("remark", quotation.remark);
		params.put("shipmentType", quotation.shipmentType);
		params.put("shipmentPort", quotation.shipmentPort);

		params.put("prodPrice", quotation.prodPrice);
		params.put("prodPriceUnit_pro", quotation.prodPriceUnit_pro);
		params.put("prodpricePacking_pro", quotation.prodpricePacking_pro);

		params.put("prodMinnumOrder", quotation.prodMinnumOrder);
		params.put("prodMinnumOrderType_pro", quotation.prodMinnumOrderType_pro);
		params.put("paymentTerm_pro", quotation.paymentTerm_pro);

		// 下面是可选
		if ("1".equals(quotation.mAddtional))
		{
			params.put("quoteExpiredDate", quotation.quoteExpiredDate);
			params.put("leadTime", quotation.leadTime);
			params.put("deliveryMethod_pro", quotation.deliveryMethod_pro);
			params.put("packaging", quotation.packaging);
			params.put("qualityInspection", quotation.qualityInspection);
			params.put("documents", quotation.documents);
		}
		params.put("sampleProvide", quotation.sampleProvide);
		params.put("sampleFree", quotation.sampleFre);

		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.MODIFY_QUOTATION, params, new CommonJsonHttpResponseHandler(listener,
				BaseResponse.class));
	}

	// end 采购

	/**
	 * 发送反馈的意见
	 * @param content
	 * @param listener
	 */
	public static void feedback(String content, DisposeDataListener listener)
	{
		MobRequestCenter.feedback(content, listener);
	}

	/**
	 * 同步用户信息
	 * @param listener
	 */
	public static void syncPersonalMessage(DisposeDataListener listener)
	{
		MobRequestCenter.getUnReadMessageNum(SupplierApplication.getInstance().getUser().content.companyInfo.companyId,
				SupplierApplication.getInstance().getUser().content.companyInfo.operatorId, listener);
	}

	/**
	 * 获取信息列表
	 * @param listener
	 */
	public static void getMessages(int pageIndex, String type, DisposeDataListener listener)
	{
		MobRequestCenter.getAllMessages(SupplierApplication.getInstance().getUser().content.companyInfo.companyId,
				SupplierApplication.getInstance().getUser().content.companyInfo.operatorId, type, pageIndex, 20,
				listener);
	}

	/**
	 * 更新推送消息开关状态
	 * @param pushType
	 * @param pushState
	 */
	public static void updatePushSetting(String pushType, String pushState)
	{
		MobRequestCenter.updatePushSetting(SupplierApplication.getInstance().getUser().content.companyInfo.companyId,
				SupplierApplication.getInstance().getUser().content.companyInfo.operatorId, pushType, pushState,
				new SimpleDisposeDataListener());
	}

	/**
	 * 免打扰设置开关状态
	 * @param notDisturbStatus
	 */
	public static void updateNotDisturbSetting(String notDisturbStatus)
	{
		MobRequestCenter.updateNotDisturbSetting(
				SupplierApplication.getInstance().getUser().content.companyInfo.companyId, SupplierApplication
						.getInstance().getUser().content.companyInfo.operatorId, notDisturbStatus,
				new SimpleDisposeDataListener());
	}

	/**
	 * 更新消息状态
	 * @param messageId
	 */
	public static void updateMesssageStatus(String messageId)
	{
		MobRequestCenter.updateMessageStatus(SupplierApplication.getInstance().getUser().content.companyInfo.companyId,
				SupplierApplication.getInstance().getUser().content.companyInfo.operatorId, messageId,
				new SimpleDisposeDataListener());
	}

	/**
	 * 签到
	 * @param messageId
	 */
	public static void sendSignIn(Context context, String activityId, DisposeDataListener lisener)
	{
		RequestParams params = new RequestParams();
		params.put("equipId", MobileUtil.getDeviceUniqueid(context));
		params.put("companyId", SupplierApplication.getInstance().getUser().content.companyInfo.companyId);
		params.put("companyName", SupplierApplication.getInstance().getUser().content.companyInfo.companyName);
		params.put("userName", SupplierApplication.getInstance().getUser().content.userInfo.fullName);
		params.put("operatorId", SupplierApplication.getInstance().getUser().content.companyInfo.operatorId);
		params.put("signTime", System.currentTimeMillis() / 1000 + "");
		params.put("activityId", activityId);
		params.put("versionCode", Constants.APP_PORT_VERSION_CODE);
		FocusClient.post(UrlConstants.QRCODE_SIGN_IN, params, new CommonJsonHttpResponseHandler(lisener,
				BaseResponse.class));
	}
}
