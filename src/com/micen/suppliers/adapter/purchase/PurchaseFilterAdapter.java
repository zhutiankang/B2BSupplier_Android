package com.micen.suppliers.adapter.purchase;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.SearchResultKeyValue;


public class PurchaseFilterAdapter extends BaseAdapter
{

	private List<SearchResultKeyValue> list;
	private Context mContext;
	private ViewHolder holder;

	private int choosePosition;
	private List<String> chooseList;
	private boolean isMulti;

	// 单选已经选中的值
	private String chooseValue;

	public PurchaseFilterAdapter(Context c, List<SearchResultKeyValue> l)
	{
		mContext = c;
		list = l;
	}

	public PurchaseFilterAdapter(Context c, List<SearchResultKeyValue> l, boolean m)
	{
		mContext = c;
		list = l;
		isMulti = m;
		if (isMulti)
		{
			chooseList = new ArrayList<String>();
		}
	}

	public void setChoosePostion(int position)
	{
		if (isMulti)
		{
			if (-1 == position)
			{
				chooseList.clear();
			}
			else
			{
				if (chooseList.contains(getItem(position).value))
				{
					chooseList.remove(getItem(position).value);
				}
				else
				{
					chooseList.add(getItem(position).value);
				}
			}
		}
		else
		{
			if (choosePosition != position)
			{
				choosePosition = position;
			}
			else
			{
				choosePosition = -1;
			}
			if (choosePosition == -1)
			{
				chooseValue = "";
			}
			else
			{
				chooseValue = getItem(position).value;
			}
		}

		notifyDataSetChanged();

	}

	public void setChoose(String value)
	{
		if (isMulti)
		{
			if (!Utils.isEmpty(value))
			{
				String[] temp = value.split(",");
				for (String s : temp)
				{
					chooseList.add(s);
				}
			}
		}
		else
		{
			chooseValue = value;
		}

		notifyDataSetChanged();
	}

	/**
	 * @return 当前选中的位置
	 */
	public int getChoosePostion()
	{
		// TODO Auto-generated method stub
		return choosePosition;
	}

	public String getChooseValue()
	{
		String result = "";

		for (int i = 0; i < chooseList.size(); i++)
		{
			if (0 == i)
			{
				result = chooseList.get(i);
			}
			else
			{
				result = result + "," + chooseList.get(i);
			}
		}

		return result;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (null != list)
			return list.size();
		return 0;
	}

	@Override
	public SearchResultKeyValue getItem(int position)
	{
		// TODO Auto-generated method stub
		if (null != list)
			return list.get(position);
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_category, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.list_item_purchase_category_nameTextView);
			holder.cbChoose = (CheckBox) convertView.findViewById(R.id.list_item_purchase_category_chooseCheckBox);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(getItem(position).name);

		if (isMulti)
		{
			if (chooseList.contains(getItem(position).value))
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
			// todo是否选中
			if (getItem(position).value.equals(chooseValue))
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
