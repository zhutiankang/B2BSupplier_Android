package com.micen.suppliers.http;


public class UrlConstants
{
	/**
	 * 请求头地址
	 * 正式版Url	:http://mic.s.wfeature.com
	 * P版Url		:http://mic.sp.wfeature.com
	 * 测试版Url	:http://192.168.42.154:82
	 */
	public static final String BASE_HOST = "http://mic.s.wfeature.com";
	/**
	 * 请求协议名称
	 */
	public static final String HOST_NAME = "/supplier/";
	/**
	 * 登录请求地址
	 */
	public static final String USER_LOGIN = BASE_HOST + HOST_NAME + "loginManager/login";
	/**
	 * 获取登录用户的最新信息请求地址
	 */
	public static final String GET_STAFF_PROFILE = BASE_HOST + HOST_NAME + "loginManager/profile";
	/**
	 * 获取系统推广信息
	 */
	public static final String GET_SYSTEM_PROMOTION = BASE_HOST + HOST_NAME + "system/getSystemPromotion";
	// begin 询盘
	/**
	 * 获取邮件列表
	 */
	public static final String GET_MESSAGE_LIST = BASE_HOST + HOST_NAME + "inquiryManager/getInquiryList";
	/**
	 * 获取询盘详情
	 */
	public static final String GET_MESSAGE_DETAIL = BASE_HOST + HOST_NAME + "inquiryManager/getInquiryDetail";
	/**
	 * 删除询盘
	 */
	public static final String DELETE_MESSAGE = BASE_HOST + HOST_NAME + "inquiryManager/deleteInquiry";

	/**
	 * 获取业务员列表
	 */
	public static final String GET_SALESMAN = BASE_HOST + HOST_NAME + "accountManager/getSalesman";
	/**
	 * 分配业务员
	 */
	public static final String ASSIGN_SALESMAN = BASE_HOST + HOST_NAME + "accountManager/assignSalesman";
	/**
	 * 发送/回复询盘
	 */
	public static final String SEND_INQUIRY = BASE_HOST + HOST_NAME + "inquiryManager/sendInquiry";
	/**
	 * 文件上传
	 */
	public static final String UPLOAD_FILE = BASE_HOST + HOST_NAME + "system/uploadFile";
	/**
	 * 获取买家行为记录
	 */
	public static final String GET_BUYERBEHAVIOUR = BASE_HOST + HOST_NAME + "accountManager/getBuyerBehaviour";
	/**
	 * 获取联系人
	 */
	public static final String GET_CONTACTS = BASE_HOST + HOST_NAME + "accountManager/getContacts";
	/**
	 * 增加联系人
	 */
	public static final String ADD_CONTACTS = BASE_HOST + HOST_NAME + "accountManager/addContactPerson";
	/**
	 * 删除联系人
	 */
	public static final String DELETE_CONTACTS = BASE_HOST + HOST_NAME + "accountManager/deleteContactPerson";
	// begin 产品详情
	/***
	 * 获得产品详情数据
	 */
	public static final String GET_PRODUCTDETAIL = BASE_HOST + HOST_NAME + "productManager/getProductDetail";
	/**
	 * RFQ列表请求地址
	 */
	public static final String GET_RFQ_LIST = BASE_HOST + HOST_NAME + "rfqManager/getRfqList";
	/***
	 * RFQ详情
	 */
	public static final String GET_RFQ_DETAIL = BASE_HOST + HOST_NAME + "rfqManager/getRfqDetail";
	/***
	 * 获得目录数据
	 */
	public static final String GET_CATEGORYLIST = BASE_HOST + HOST_NAME + "system/getCategoryList";

	// begin 采购相关
	public static final String SEARCH_RFQ = BASE_HOST + HOST_NAME + "rfqManager/search";

	public static final String GET_RELATED_QUOTATION = BASE_HOST + HOST_NAME + "quotationManager/getRelatedQuotations";
	/**
	 * 获取公司产品列表`
	 */
	public static final String GET_PRODUCT_LIST = BASE_HOST + HOST_NAME + "productManager/getProductList";
	/**
	 * 等待报价
	 */
	public static final String GET_RFQ_NEED_QUOTE = BASE_HOST + HOST_NAME + "rfqManager/getRfqNeedQuote";
	/**
	 * 管理报价
	 */
	public static final String GET_QUOTATIONS = BASE_HOST + HOST_NAME + "quotationManager/getQuotations";
	/**
	 * 获取所有报价
	 */
	public static final String GET_ALL_QUOTATIONS = BASE_HOST + HOST_NAME + "quotationManager/getAllQuotations";

	public static final String SEND_QUOTATION = BASE_HOST + HOST_NAME + "quotationManager/sendQuotation";

	public static final String SEND_RECOMMEND_QUOTATION = BASE_HOST + HOST_NAME
			+ "quotationManager/sendRecommendQuotation";

	public static final String GET_QUOTATIONDETAIL = BASE_HOST + HOST_NAME + "quotationManager/getQuotationDetail";

	public static final String GET_RECOMMEND_QUOTATIONDETAIL = BASE_HOST + HOST_NAME
			+ "quotationManager/getRecommendQuotationDetail";

	public static final String MODIFY_QUOTATION = BASE_HOST + HOST_NAME + "quotationManager/modifyQuotation";

	// end 采购相关

	public static final String ABOUT_URL = "file:///android_asset/html/about.html";

	public static final String APPLY_URL = "file:///android_asset/html/apply.html";

	public static final String QRCODE_SIGN_IN = BASE_HOST + "/scanCode/sign";
}
