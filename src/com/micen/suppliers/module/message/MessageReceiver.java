package com.micen.suppliers.module.message;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageReceiver implements Parcelable
{
	public String companyId;

	public String companyName;

	public String country;

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
		dest.writeString(companyId);
		dest.writeString(companyName);
		dest.writeString(country);
		dest.writeString(countryImageUrl);
		dest.writeString(fullName);
		dest.writeString(gender);
		dest.writeString(operatorId);
	}

	public static final Parcelable.Creator<MessageReceiver> CREATOR = new Parcelable.Creator<MessageReceiver>()
	{
		public MessageReceiver createFromParcel(Parcel in)
		{
			MessageReceiver messageReceiver = new MessageReceiver();
			messageReceiver.companyId = in.readString();
			messageReceiver.companyName = in.readString();
			messageReceiver.country = in.readString();
			messageReceiver.countryImageUrl = in.readString();
			messageReceiver.fullName = in.readString();
			messageReceiver.gender = in.readString();
			messageReceiver.operatorId = in.readString();
			return messageReceiver;
		}

		public MessageReceiver[] newArray(int size)
		{
			return new MessageReceiver[size];
		}
	};
}
