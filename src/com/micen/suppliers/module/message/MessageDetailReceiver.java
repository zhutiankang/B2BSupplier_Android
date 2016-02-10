package com.micen.suppliers.module.message;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageDetailReceiver implements Parcelable
{
	public String companyName;

	public String companyId;

	public String country;
	
	public String countryNameCn;

	public String countryImageUrl;

	public String fullName;

	public String gender;

	public String operatorId;

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
		dest.writeString(companyName);
		dest.writeString(companyId);
		dest.writeString(country);
		dest.writeString(countryNameCn);
		dest.writeString(countryImageUrl);
		dest.writeString(fullName);
		dest.writeString(gender);
		dest.writeString(operatorId);
	}

	public static final Parcelable.Creator<MessageDetailReceiver> CREATOR = new Parcelable.Creator<MessageDetailReceiver>()
	{
		public MessageDetailReceiver createFromParcel(Parcel in)
		{
			MessageDetailReceiver mdContentReceiver = new MessageDetailReceiver();
			mdContentReceiver.companyName = in.readString();
			mdContentReceiver.companyId = in.readString();
			mdContentReceiver.country = in.readString();
			mdContentReceiver.countryNameCn = in.readString();
			mdContentReceiver.countryImageUrl = in.readString();
			mdContentReceiver.fullName = in.readString();
			mdContentReceiver.gender = in.readString();
			mdContentReceiver.operatorId = in.readString();
			return mdContentReceiver;
		}

		public MessageDetailReceiver[] newArray(int size)
		{
			return new MessageDetailReceiver[size];
		}
	};

}
