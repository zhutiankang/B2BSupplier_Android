package com.micen.suppliers.module.purchase;

import java.util.List;


public class RfqDetail
{
	public List<RfqFile> attachments;
	public String rfqId;
	public String rfqType;
	public String status;
	public String subject;

	public List<String> photoList;

	public String isRecommended;
	public String isUrgent;
	public String estimatedQuantity;
	public String estimatedQuantityType;
	public String addTime;
	public String validateTimeEnd;
	public String quoteLeft;
	public String canbeQuoted;
	public String refuseReason;
	public String contactTelephone;
	public String country;
	public String countryImageUrl;

	public BuyerRequest buyerRequest;

	public String detailDescription;
	public String supplyCustomProp;
	public String purchaseCustomProp;

	public String quotationNum;

}
