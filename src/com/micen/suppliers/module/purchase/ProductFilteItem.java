package com.micen.suppliers.module.purchase;

import android.os.Parcel;
import android.os.Parcelable;


public class ProductFilteItem implements Parcelable
{
	private String name;
	private String value;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public ProductFilteItem(Parcel in)
	{
		name = in.readString();
		value = in.readString();
	}

	public ProductFilteItem(String n, String v)
	{
		name = n;
		value = v;
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

	public static final Parcelable.Creator<ProductFilteItem> CREATOR = new Parcelable.Creator<ProductFilteItem>()
	{

		@Override
		public ProductFilteItem createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new ProductFilteItem(source);
		}

		@Override
		public ProductFilteItem[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new ProductFilteItem[size];
		}
	};

}
