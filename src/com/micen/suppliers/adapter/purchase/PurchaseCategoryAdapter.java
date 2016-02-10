package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.CategoryItem;


public class PurchaseCategoryAdapter extends BaseAdapter
{
	private Context mContext;
	private List<CategoryItem> list;

	private ViewHolder holder;

	private int choosePosition;

	public PurchaseCategoryAdapter(Context c, List<CategoryItem> l)
	{
		mContext = c;
		list = l;
		choosePosition = -1;
	}

	public void setChoosePostion(int position)
	{
		if (choosePosition == position)
		{
			choosePosition = -1;
		}
		else
		{
			choosePosition = position;
		}

		notifyDataSetChanged();

	}

	public String getCategory()
	{
		if (choosePosition != -1)
			return getItem(choosePosition).catNameEn;

		return "";
	}
	
	public String getCategoryCode()
	{
		if (choosePosition != -1)
			return getItem(choosePosition).catCode;

		return "";
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
	public CategoryItem getItem(int position)
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

		holder.tvName.setText(getItem(position).catNameEn);
		// todo是否选中
		holder.cbChoose.setVisibility(View.GONE);
		if (choosePosition == position)
		{
			holder.cbChoose.setChecked(true);
		}
		else
		{
			holder.cbChoose.setChecked(false);
		}
		return convertView;
	}

	static class ViewHolder
	{
		TextView tvName;
		CheckBox cbChoose;
	}

}
