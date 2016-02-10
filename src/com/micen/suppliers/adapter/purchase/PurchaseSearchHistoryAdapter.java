package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.micen.suppliers.R;
import com.micen.suppliers.module.db.SearchRecord;


public class PurchaseSearchHistoryAdapter extends BaseAdapter
{
	private Context mContext;
	private List<SearchRecord> mList;

	private ViewHolder holder;
	private SearchRecord tmp;

	public PurchaseSearchHistoryAdapter(Context c, List<SearchRecord> l)
	{
		mContext = c;
		mList = l;
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
	public SearchRecord getItem(int position)
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
		if (null == convertView)
		{
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_search_history, null);

			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.list_item_purchase_search_historyTextView);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		tmp = getItem(position);
		if (null != tmp)
		{
			holder.tvName.setText(tmp.recentKeywords);
		}
		return convertView;
	}

	static class ViewHolder
	{
		TextView tvName;
	}
}
