package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.ProductFilteItem;
import com.micen.suppliers.module.purchase.ProductFilter;


public class PurchaseProductFilterAdapter extends BaseExpandableListAdapter
{
	private ViewHolder viewHolderParent;
	private ViewHolder viewHolderChild;
	private Context mContext;
	private List<ProductFilter> mList;

	private String[] selectList;

	private ProductFilter tmp;
	private ProductFilteItem tmpChild;

	public PurchaseProductFilterAdapter(Context c, List<ProductFilter> l, String[] s)
	{
		mContext = c;
		mList = l;
		selectList = s;
	}

	@Override
	public int getGroupCount()
	{
		// TODO Auto-generated method stub
		if (mList != null)
			return mList.size();
		return 0;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		// TODO Auto-generated method stub
		if (mList != null)
			return mList.get(groupPosition).getList().size();
		return 0;
	}

	@Override
	public ProductFilter getGroup(int groupPosition)
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.get(groupPosition);
		return null;
	}

	@Override
	public ProductFilteItem getChild(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.get(groupPosition).getList().get(childPosition);
		return null;
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		if (null == convertView)
		{
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.list_item_purchase_product_filter_parent, null);
			viewHolderParent = new ViewHolder();
			viewHolderParent.tvName = (TextView) convertView
					.findViewById(R.id.list_item_purchase_product_filter_parent_nameTextView);
			viewHolderParent.cbCheck = (CheckBox) convertView
					.findViewById(R.id.list_item_purchase_product_filter_parent_CheckBox);

			viewHolderParent.tvSelect = (TextView) convertView
					.findViewById(R.id.list_item_purchase_product_filter_parent_selectTextView);

			convertView.setTag(viewHolderParent);
		}
		else
		{
			viewHolderParent = (ViewHolder) convertView.getTag();
		}

		tmp = getGroup(groupPosition);

		if (null != tmp)
		{
			viewHolderParent.tvName.setText(tmp.getName());
			viewHolderParent.tvSelect.setText(selectList[groupPosition]);
		}

		viewHolderParent.cbCheck.setChecked(isExpanded);

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent)
	{
		// TODO Auto-generated method stub
		if (null == convertView)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_product_filter_child, null);

			viewHolderChild = new ViewHolder();

			viewHolderChild.tvName = (TextView) convertView
					.findViewById(R.id.list_item_purchase_product_filter_child_nameTextView);
			viewHolderChild.cbCheck = (CheckBox) convertView
					.findViewById(R.id.list_item_purchase_product_filter_child_CheckBox);

			convertView.setTag(viewHolderChild);
		}
		else
		{
			viewHolderChild = (ViewHolder) convertView.getTag();
		}

		tmp = getGroup(groupPosition);

		tmpChild = getChild(groupPosition, childPosition);

		if (null != tmpChild)
		{
			viewHolderChild.tvName.setText(tmpChild.getName());
			
			if (tmpChild.getName().equals(selectList[groupPosition]))
			{
				viewHolderChild.cbCheck.setChecked(true);
			}
			else
			{
				viewHolderChild.cbCheck.setChecked(false);
			}
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return true;
	}

	static class ViewHolder
	{
		public TextView tvName;
		public TextView tvSelect;
		public CheckBox cbCheck;
	}

}
