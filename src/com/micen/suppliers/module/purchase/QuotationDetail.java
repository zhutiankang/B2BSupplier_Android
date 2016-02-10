package com.micen.suppliers.module.purchase;

import android.os.Parcel;
import android.os.Parcelable;


public class QuotationDetail implements Parcelable
{
	public String quotationId;
	public String rfqId;
	public String rfqSubject;
	public String isRecommended;
	public String buyerName;
	public String buyerComName;
	public String buyerComCountry;
	public String buyerCountryImageUrl;
	public String validateTimeEnd;
	public String quoteName;
	public String quoteTime;
	public String buyerReadFlag;
	public String messageId;
	public String showReplyMessage;
	public String ossRemark;
	public String showContactBuyer;
	

	public QuotationDetail()
	{

	}

	public QuotationDetail(Parcel in)
	{

		quotationId = in.readString();
		rfqId = in.readString();
		rfqSubject = in.readString();
		isRecommended = in.readString();
		buyerName = in.readString();
		buyerComName = in.readString();
		buyerComCountry = in.readString();
		buyerCountryImageUrl = in.readString();
		validateTimeEnd = in.readString();
		quoteName = in.readString();
		quoteTime = in.readString();
		buyerReadFlag = in.readString();
		messageId = in.readString();
		showReplyMessage = in.readString();
		ossRemark = in.readString();
		showContactBuyer = in.readString();
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO Auto-generated method stub
		dest.writeString(quotationId);
		dest.writeString(rfqId);
		dest.writeString(rfqSubject);
		dest.writeString(isRecommended);
		dest.writeString(buyerName);
		dest.writeString(buyerComName);
		dest.writeString(buyerComCountry);
		dest.writeString(buyerCountryImageUrl);
		dest.writeString(validateTimeEnd);
		dest.writeString(quoteName);
		dest.writeString(quoteTime);
		dest.writeString(buyerReadFlag);
		dest.writeString(messageId);
		dest.writeString(showReplyMessage);
		dest.writeString(ossRemark);
		dest.writeString(showContactBuyer);

	}

	public static final Parcelable.Creator<QuotationDetail> CREATOR = new Parcelable.Creator<QuotationDetail>()
	{

		@Override
		public QuotationDetail createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new QuotationDetail(source);
		}

		@Override
		public QuotationDetail[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new QuotationDetail[size];
		}
	};

}
