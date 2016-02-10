package com.micen.suppliers.module.purchase;

import android.os.Parcel;
import android.os.Parcelable;


public class RfqFile implements Parcelable
{
	public RfqFile()
	{

	}

	public String attachmentId;
	public String attachmentName;
	public String attachmentType;
	public String fileUrl;

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
		dest.writeString(attachmentId);
		dest.writeString(attachmentName);
		dest.writeString(attachmentType);
		dest.writeString(fileUrl);

	}

	public static final Parcelable.Creator<RfqFile> CREATOR = new Parcelable.Creator<RfqFile>()
	{

		@Override
		public RfqFile createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new RfqFile(source);
		}

		@Override
		public RfqFile[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new RfqFile[size];
		}
	};

	public RfqFile(Parcel in)
	{
		attachmentId = in.readString();
		attachmentName = in.readString();
		attachmentType = in.readString();
		fileUrl = in.readString();
	}
}