package com.micen.suppliers.http.purchase;

import android.os.Parcel;
import android.os.Parcelable;


public class NormalQuotation implements Parcelable
{
	public String rfqId;

	public String prodName;
	public String prodModel;
	public String prodPhoto;
	public String remark;
	// 贸易条款
	public String shipmentType;
	public String shipmentPort;
	public String prodPrice;
	public String prodPriceUnit_pro;
	public String prodpricePacking_pro;
	public String prodMinnumOrder;
	public String prodMinnumOrderType_pro;
	public String paymentTerm_pro;
	public String quoteExpiredDate;
	public String quoteExpiredDate_zh;
	public String leadTime;
	public String deliveryMethod_pro;
	public String packaging;
	public String qualityInspection;
	public String documents;
	public String sampleProvide;
	public String sampleFre;

	public String prodpricePacking_pro_zh;
	public String prodMinnumOrderType_pro_zh;
	public String deliveryMethod_pro_zh;
	public String packaging_zh;
	public String qualityInspection_zh;
	public String documents_zh;

	// m开头的变量为后添加的
	public String mFilePath;

	public String mAddtional;

	public String quotationid;
	
	public String prodId;
	public String prodImg;
	

	public NormalQuotation()
	{

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
		dest.writeString(rfqId);
		dest.writeString(prodName);
		dest.writeString(prodModel);
		dest.writeString(prodPhoto);
		dest.writeString(remark);
		dest.writeString(shipmentType);
		dest.writeString(shipmentPort);
		dest.writeString(prodPrice);
		dest.writeString(prodPriceUnit_pro);
		dest.writeString(prodpricePacking_pro);
		dest.writeString(prodMinnumOrder);
		dest.writeString(prodMinnumOrderType_pro);
		dest.writeString(paymentTerm_pro);
		dest.writeString(quoteExpiredDate);
		dest.writeString(quoteExpiredDate_zh);
		dest.writeString(leadTime);
		dest.writeString(deliveryMethod_pro);
		dest.writeString(packaging);
		dest.writeString(qualityInspection);
		dest.writeString(documents);
		dest.writeString(sampleProvide);
		dest.writeString(sampleFre);

		// 额外增加，显示中文
		dest.writeString(prodpricePacking_pro_zh);
		dest.writeString(prodMinnumOrderType_pro_zh);
		dest.writeString(deliveryMethod_pro_zh);
		dest.writeString(packaging_zh);
		dest.writeString(qualityInspection_zh);
		dest.writeString(documents_zh);

		dest.writeString(mFilePath);

		dest.writeString(mAddtional);
		
		dest.writeString(quotationid);
		
		dest.writeString(prodId);
		dest.writeString(prodImg);
	}

	public static final Parcelable.Creator<NormalQuotation> CREATOR = new Parcelable.Creator<NormalQuotation>()
	{

		@Override
		public NormalQuotation createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new NormalQuotation(source);
		}

		@Override
		public NormalQuotation[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new NormalQuotation[size];
		}
	};

	private NormalQuotation(Parcel in)
	{
		rfqId = in.readString();
		prodName = in.readString();
		prodModel = in.readString();
		prodPhoto = in.readString();
		remark = in.readString();
		shipmentType = in.readString();
		shipmentPort = in.readString();
		prodPrice = in.readString();
		prodPriceUnit_pro = in.readString();
		prodpricePacking_pro = in.readString();
		prodMinnumOrder = in.readString();
		prodMinnumOrderType_pro = in.readString();
		paymentTerm_pro = in.readString();
		quoteExpiredDate = in.readString();
		quoteExpiredDate_zh = in.readString();
		leadTime = in.readString();
		deliveryMethod_pro = in.readString();
		packaging = in.readString();
		qualityInspection = in.readString();
		documents = in.readString();
		sampleProvide = in.readString();
		sampleFre = in.readString();

		// 额外增加，显示中文
		prodpricePacking_pro_zh = in.readString();
		prodMinnumOrderType_pro_zh = in.readString();
		deliveryMethod_pro_zh = in.readString();
		packaging_zh = in.readString();
		qualityInspection_zh = in.readString();
		documents_zh = in.readString();

		mFilePath = in.readString();

		mAddtional = in.readString();
		
		quotationid = in.readString();
		prodId = in.readString();
		prodImg = in.readString();
	}

}
