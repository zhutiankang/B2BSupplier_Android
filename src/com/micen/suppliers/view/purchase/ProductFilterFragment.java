package com.micen.suppliers.view.purchase;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.adapter.purchase.PurchaseProductFilterAdapter;
import com.micen.suppliers.module.purchase.ProductFilter;


public class ProductFilterFragment extends Fragment
{

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;

	private Button btnOK;
	private Button btnReset;

	private ExpandableListView listView;

	private PurchaseProductFilterAdapter adapter;

	private List<ProductFilter> list;

	private ProductFilter tmp;

	private String[] chooseMap;
	private String[] chooseValueMap;

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.common_ll_title_back:
				getActivity().finish();
				break;
			case R.id.filter_okButton:
				Intent data = new Intent();
				data.putExtra("choose", chooseValueMap);
				data.putExtra("choosename", chooseMap);
				getActivity().setResult(1, data);
				getActivity().finish();
				break;
			case R.id.filter_resetButton:
				for (int i = 0; i < chooseMap.length; i++)
				{
					chooseMap[i] = null;
					chooseValueMap[i] = null;
				}
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	private OnChildClickListener childClickListener = new OnChildClickListener()
	{

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
		{
			// TODO Auto-generated method stub
			tmp = list.get(groupPosition);

			if (Utils.isEmpty(chooseMap[groupPosition]))
			{
				chooseMap[groupPosition] = tmp.getList().get(childPosition).getName();
				chooseValueMap[groupPosition] = tmp.getList().get(childPosition).getValue();
			}
			else
			{

				if (chooseMap[groupPosition].equals(tmp.getList().get(childPosition).getName()))
				{
					chooseMap[groupPosition] = null;
					chooseValueMap[groupPosition] = null;
				}
				else
				{
					chooseMap[groupPosition] = tmp.getList().get(childPosition).getName();
					chooseValueMap[groupPosition] = tmp.getList().get(childPosition).getValue();
				}
			}

			adapter.notifyDataSetChanged();

			return false;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();

		if (null != bundle)
		{
			list = bundle.getParcelableArrayList("data");
			chooseMap = bundle.getStringArray("name");
			chooseValueMap = bundle.getStringArray("value");

			if (null != list)
			{
				if (null == chooseMap)
				{
					chooseMap = new String[list.size()];
				}
				if (null == chooseValueMap)
				{
					chooseValueMap = new String[list.size()];
				}
				adapter = new PurchaseProductFilterAdapter(getActivity(), list, chooseMap);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_product_search_filter, container, false);

		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		tvTitle = (TextView) view.findViewById(R.id.common_title_name);

		listView = (ExpandableListView) view.findViewById(R.id.purchase_product_search_filter_listview);
		listView.setOnChildClickListener(childClickListener);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_btn);

		tvTitle.setText(R.string.filter);

		btnReset = (Button) view.findViewById(R.id.filter_resetButton);
		btnReset.setOnClickListener(clickListener);

		btnOK = (Button) view.findViewById(R.id.filter_okButton);
		btnOK.setOnClickListener(clickListener);

		if (null != adapter)
		{
			listView.setAdapter(adapter);
		}
		return view;
	}

	public static ProductFilterFragment newInstance(ArrayList<ProductFilter> list, String[] name, String[] value)
	{
		ProductFilterFragment fragment = new ProductFilterFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("data", list);
		bundle.putStringArray("name", name);
		bundle.putStringArray("value", value);

		fragment.setArguments(bundle);
		return fragment;
	}
}
