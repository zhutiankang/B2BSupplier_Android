package com.micen.suppliers.module.purchase;

import android.os.Parcel;
import android.os.Parcelable;


public class RfqNeedInfo implements Parcelable
{
	public String shipmentTerms;
	public String destinationPort;
	public String priceType;
	public String purchaseQuantityType;
	public String paymentTerms;

	public RfqNeedInfo()
	{

	}

	public RfqNeedInfo(Parcel in)
	{
		shipmentTerms = in.readString();
		destinationPort = in.readString();
		priceType = in.readString();
		purchaseQuantityType = in.readString();
		paymentTerms = in.readString();
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

		dest.writeString(shipmentTerms);
		dest.writeString(destinationPort);
		dest.writeString(priceType);
		dest.writeString(purchaseQuantityType);
		dest.writeString(paymentTerms);
	}

	public static final Parcelable.Creator<RfqNeedInfo> CREATOR = new Parcelable.Creator<RfqNeedInfo>()
	{

		@Override
		public RfqNeedInfo createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new RfqNeedInfo(source);
		}

		@Override
		public RfqNeedInfo[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new RfqNeedInfo[size];
		}
	};

}
