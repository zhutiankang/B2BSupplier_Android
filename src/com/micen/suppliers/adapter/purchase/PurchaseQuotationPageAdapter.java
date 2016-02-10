package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.QuotationDetailListWithStatus;
import com.micen.suppliers.view.purchase.PurchaseQuotationFragment;


public class PurchaseQuotationPageAdapter extends FragmentPagerAdapter
{
	private Context mContext;
	private List<QuotationDetailListWithStatus> mList;

	public PurchaseQuotationPageAdapter(Context c, FragmentManager fm, List<QuotationDetailListWithStatus> l)
	{
		super(fm);
		// TODO Auto-generated constructor stub
		mContext = c;
		mList = l;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		// TODO Auto-generated method stub
		return getTitle(mList.get(position).quotationStatus);
	}

	@Override
	public Fragment getItem(int position)
	{
		// TODO Auto-generated method stub

		return PurchaseQuotationFragment.newInstance(mList.get(position).quotationStatus,
				mList.get(position).quotations);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.size();
		return 0;
	}

	private String getTitle(String status)
	{
		String title = "";
		int type = 0;
		try
		{
			type = Integer.parseInt(status);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			type = 0;
		}
		// 1：待审核 2：需要修改 3.已报价 4：已过期，5：已冻结
		switch (type)
		{
		case 1:
			title = mContext.getString(R.string.quotationtitlepending);
			break;
		case 2:
			title = mContext.getString(R.string.quotationtitleneedmodify);
			break;
		case 3:
			title = mContext.getString(R.string.quotationtitlequoted);
			break;
		case 4:
			title = mContext.getString(R.string.quotationtitleexpired);
			break;
		case 5:
			title = mContext.getString(R.string.quotationtitlelocked);
			break;

		default:
			break;
		}

		return title;
	}

}
