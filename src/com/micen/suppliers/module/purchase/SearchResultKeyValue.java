package com.micen.suppliers.module.purchase;

import android.os.Parcel;
import android.os.Parcelable;


public class SearchResultKeyValue implements Parcelable
{
	public String name;
	public String value;

	public SearchResultKeyValue()
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
		dest.writeString(name);
		dest.writeString(value);

	}

	public static final Parcelable.Creator<SearchResultKeyValue> CREATOR = new Parcelable.Creator<SearchResultKeyValue>()
	{

		@Override
		public SearchResultKeyValue createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new SearchResultKeyValue(source);
		}

		@Override
		public SearchResultKeyValue[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new SearchResultKeyValue[size];
		}
	};
	
	public SearchResultKeyValue(Parcel in)
	{
		name = in.readString();
		value = in.readString();
	}
}
