package com.micen.suppliers.adapter.purchase;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.micen.suppliers.R;


public class PurchaseQuotationChooseAdapter extends BaseAdapter
{

	private String[] mArray;

	private ArrayList<String> mSelect;
	private ArrayList<String> mSelectValue;
	private Context mContext;

	private ViewHolder holder;

	private boolean isSingle;

	private int mPostion;

	public PurchaseQuotationChooseAdapter(Context c, String[] l, boolean single)
	{
		mPostion = -1;
		mContext = c;
		mArray = l;
		isSingle = single;
		mSelect = new ArrayList<String>();
		mSelectValue = new ArrayList<String>();
	}

	public void setPosition(int position)
	{
		if (mPostion != position)
		{
			mPostion = position;
		}
		else
		{
			mPostion = -1;
		}
		notifyDataSetChanged();
	}

	public int getCurPosition()
	{
		return mPostion;
	}

	public void addString(String value)
	{
		if (mSelect.contains(value))
		{
			mSelect.remove(value);
		}
		else
		{
			mSelect.add(value);
		}
		notifyDataSetChanged();
	}

	public void addStringValue(String value)
	{
		if (mSelectValue.contains(value))
		{
			mSelectValue.remove(value);
		}
		else
		{
			mSelectValue.add(value);
		}

	}

	public ArrayList<String> getSelect()
	{
		return mSelect;
	}

	public ArrayList<String> getSelectValue()
	{
		return mSelectValue;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (null != mArray)
			return mArray.length;
		return 0;
	}

	@Override
	public String getItem(int position)
	{
		// TODO Auto-generated method stub
		if (null != mArray)
			return mArray[position];
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
		if (null == convertView)
		{
			if (isSingle)
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_quotation_choose, null);
			}
			else
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_quotation_choose_multi,
						null);
			}

			holder = new ViewHolder();

			holder.tvName = (TextView) convertView.findViewById(R.id.list_item_purchase_quotation_choose_nameTextView);
			holder.cbChoose = (CheckBox) convertView
					.findViewById(R.id.list_item_purchase_quotation_choose_chooseCheckBox);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(getItem(position));

		if (isSingle)
		{
			if (position == mPostion)
			{
				holder.cbChoose.setChecked(true);
			}
			else
			{
				holder.cbChoose.setChecked(false);
			}
		}
		else
		{
			if (mSelect.contains(getItem(position)))
			{
				holder.cbChoose.setChecked(true);
			}
			else
			{
				holder.cbChoose.setChecked(false);
			}

		}

		return convertView;
	}

	static class ViewHolder
	{
		TextView tvName;
		CheckBox cbChoose;
	}

}
