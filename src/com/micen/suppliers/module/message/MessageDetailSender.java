package com.micen.suppliers.module.message;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageDetailSender implements Parcelable
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

	public static final Parcelable.Creator<MessageDetailSender> CREATOR = new Parcelable.Creator<MessageDetailSender>()
	{
		public MessageDetailSender createFromParcel(Parcel in)
		{
			MessageDetailSender mdContentSender = new MessageDetailSender();
			mdContentSender.companyName = in.readString();
			mdContentSender.companyId = in.readString();
			mdContentSender.country = in.readString();
			mdContentSender.countryNameCn = in.readString();
			mdContentSender.countryImageUrl = in.readString();
			mdContentSender.fullName = in.readString();
			mdContentSender.gender = in.readString();
			mdContentSender.operatorId = in.readString();
			return mdContentSender;
		}

		public MessageDetailSender[] newArray(int size)
		{
			return new MessageDetailSender[size];
		}
	};

}
