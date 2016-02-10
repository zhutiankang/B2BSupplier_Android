package com.micen.suppliers.module.purchase;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;


public class ProductFilter implements Parcelable
{
	private String name;
	private List<ProductFilteItem> list;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<ProductFilteItem> getList()
	{
		return list;
	}

	public void setList(List<ProductFilteItem> list)
	{
		this.list = list;
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
		//dest.writeList(list);
		dest.writeTypedList(list);

	}

	public static final Parcelable.Creator<ProductFilter> CREATOR = new Parcelable.Creator<ProductFilter>()
	{

		@Override
		public ProductFilter createFromParcel(Parcel source)
		{
			// TODO Auto-generated method stub
			return new ProductFilter(source);
		}

		@Override
		public ProductFilter[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new ProductFilter[size];
		}

	};

	public ProductFilter(Parcel in)
	{
		name = in.readString();
		list = new ArrayList<ProductFilteItem>();
		// list = in.readList(list, loader);
		// in.readList(list, ProductFilteItem.class.getClassLoader());
		in.readTypedList(list, ProductFilteItem.CREATOR);
	}

	public ProductFilter()
	{

	}
}
