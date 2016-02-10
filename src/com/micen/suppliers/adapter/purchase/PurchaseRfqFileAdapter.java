package com.micen.suppliers.adapter.purchase;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.purchase.RfqFile;


public class PurchaseRfqFileAdapter extends BaseAdapter
{
	private Context mContext;
	private List<RfqFile> mList;
	private ViewHolder holder;

	public PurchaseRfqFileAdapter(Context c, List<RfqFile> l)
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
	public String getItem(int position)
	{
		// TODO Auto-generated method stub
		if (null != mList)
			return mList.get(position).attachmentName;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_purchase_rfq_file, null);

			holder = new ViewHolder();

			holder.tvFileName = (TextView) convertView.findViewById(R.id.list_item_purchase_rfq_file_nameTextView);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		if (!Utils.isEmpty(getItem(position)))
		{
			holder.tvFileName.setText(getItem(position));
		}

		return convertView;
	}

	static class ViewHolder
	{
		TextView tvFileName;
	}

}
