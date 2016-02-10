package com.micen.suppliers.http.purchase;

import android.os.Parcel;
import android.os.Parcelable;


public class EntrustQuotation implements Parcelable
{
	public String rfqId;
	public String prodId;
	public String prodImg;
	public String prodName;
	public String prodMinnumOrder;
	public String prodMinnumOrderType_pro;

	public String prodPrice;
	public String prodPriceUnit_pro;
	public String prodpricePacking_pro;
	public String remark;
	public String prodPhoto;
	public String paymentTerm_pro;
	public String shipmentType;
	public String leadTime;

	public String shipmentPort;
	// m开头的变量为后添加的
	public String mFilePath;

	public String prodMinnumOrderType_pro_zh;
	public String prodPriceUnit_pro_zh;
	public String prodpricePacking_pro_zh;

	public String mAddtional;

	public String paymentTerm_pro_zh;
	public String shipmentType_zh;

	public EntrustQuotation()
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
		dest.writeString(prodId);
		dest.writeString(prodImg);
		dest.writeString(prodName);
		dest.writeString(prodMinnumOrder);
		dest.writeString(prodMinnumOrderType_pro);
		dest.writeString(prodPrice);
		dest.writeString(prodPriceUnit_pro);
		dest.writeString(prodpricePacking_pro);
		dest.writeString(remark);
		dest.writeString(prodPhoto);
		dest.writeString(paymentTerm_pro);
		dest.writeString(shipmentType);
		dest.writeString(leadTime);
		dest.writeString(shipmentPort);
		// m开头的变量为后添加的
		dest.writeString(mFilePath);

		dest.writeString(prodMinnumOrderType_pro_zh);
		dest.writeString(prodPriceUnit_pro_zh);
		dest.writeString(prodpricePacking_pro_zh);

		dest.writeString(mAddtional);

		dest.writeString(paymentTerm_pro_zh);
		dest.writeString(shipmentType_zh);
	}

	public static final Parcelable.Creator<EntrustQuotation> CREATOR = new Parcelable.Creator<EntrustQuotation>()
	{

		@Override
		public EntrustQuotation createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new EntrustQuotation(source);
		}

		@Override
		public EntrustQuotation[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new EntrustQuotation[size];
		}
	};

	private EntrustQuotation(Parcel in)
	{
		rfqId = in.readString();
		prodId = in.readString();
		prodImg = in.readString();
		prodName = in.readString();
		prodMinnumOrder = in.readString();
		prodMinnumOrderType_pro = in.readString();
		prodPrice = in.readString();
		prodPriceUnit_pro = in.readString();
		prodpricePacking_pro = in.readString();
		remark = in.readString();
		prodPhoto = in.readString();
		paymentTerm_pro = in.readString();
		shipmentType = in.readString();
		leadTime = in.readString();
		shipmentPort = in.readString();
		// m开头的变量为后添加的
		mFilePath = in.readString();

		prodMinnumOrderType_pro_zh = in.readString();
		prodPriceUnit_pro_zh = in.readString();
		prodpricePacking_pro_zh = in.readString();

		mAddtional = in.readString();

		paymentTerm_pro_zh = in.readString();
		shipmentType_zh = in.readString();

	}
}
