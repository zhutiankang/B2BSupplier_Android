package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.micen.suppliers.module.purchase.QuotationDetail;


public class PurchaseQuotationDetailAdapter extends BaseAdapter
{
	protected Activity mActivity;
	protected List<QuotationDetail> mList;
	protected QuotationDetail tmp;

	public PurchaseQuotationDetailAdapter(Activity c, List<QuotationDetail> l)
	{
		mActivity = c;
		mList = l;
	}

	
	public void setData(List<QuotationDetail> l)
	{
		mList.clear();
		mList.addAll(l);
	}
	
	public void addData(List<QuotationDetail> l)
	{
		mList.addAll(l);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.size();
		return 0;
	}

	@Override
	public QuotationDetail getItem(int position)
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
