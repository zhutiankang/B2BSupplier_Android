package com.micen.suppliers.module.message;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageSender implements Parcelable
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

	public static final Parcelable.Creator<MessageSender> CREATOR = new Parcelable.Creator<MessageSender>()
	{
		public MessageSender createFromParcel(Parcel in)
		{
			MessageSender messageSender = new MessageSender();
			messageSender.companyId = in.readString();
			messageSender.companyName = in.readString();
			messageSender.country = in.readString();
			messageSender.countryImageUrl = in.readString();
			messageSender.fullName = in.readString();
			messageSender.gender = in.readString();
			messageSender.operatorId = in.readString();
			return messageSender;
		}

		public MessageSender[] newArray(int size)
		{
			return new MessageSender[size];
		}
	};

}
