package com.micen.suppliers.view.purchase;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.LogUtil;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.adapter.purchase.PurchaseFilterAdapter;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.SearchResultContent;
import com.micen.suppliers.module.purchase.SearchResultKeyValue;
import com.umeng.analytics.MobclickAgent;


public class PurchaseSearchFilterFragment extends Fragment
{
	private ListView listView;
	private PurchaseFilterAdapter mAdapter;

	private int curChoose;
	private String key;
	private ArrayList<SearchResultKeyValue> content;

	private String tempValue;
	private String[] property;
	// 平台返回的搜索结果
	private SearchResultContent value;

	private OnChooseChangedListener mListener;

	public interface OnChooseChangedListener
	{
		public void onItemSelected(String key, String value);

		public String[] getHasChoose();
	}

	@Override
	public void onAttach(Activity activity)
	{
		// TODO Auto-generated method stub
		mListener = (OnChooseChangedListener) activity;
		property = mListener.getHasChoose();
		super.onAttach(activity);
		LogUtil.e("PurchaseSearchFilterFragment", "onAttach:" + key);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		if (null != bundle)
		{
			key = bundle.getString("key");
			content = bundle.getParcelableArrayList("value");
		}
		curChoose = -1;
		LogUtil.e("PurchaseSearchFilterFragment", "onCreate:" + key);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_search_filter, null);
		listView = (ListView) view.findViewById(R.id.purchase_search_filter_listview);
		listView.setOnItemClickListener(itemClickListener);
		if (content != null)
		{
			// List<String> list = Arrays.asList(content.split(","));
			if ("location".equals(key))
			{
				// 对于地区要支持多选
				mAdapter = new PurchaseFilterAdapter(getActivity(), content, true);
			}
			else
			{
				mAdapter = new PurchaseFilterAdapter(getActivity(), content);
			}

			mAdapter.setChoosePostion(curChoose);
			if ("category".equals(key))
			{
				mListener.onItemSelected(key, property[2]);
				mAdapter.setChoose(property[2]);
			}
			else if ("location".equals(key))
			{
				mListener.onItemSelected(key, property[1]);
				mAdapter.setChoose(property[1]);
			}
			else if ("postdate".equals(key))
			{
				mListener.onItemSelected(key, property[3]);
				mAdapter.setChoose(property[3]);
			}

			listView.setAdapter(mAdapter);
		}
		LogUtil.e("PurchaseSearchFilterFragment", "onCreateView:" + key);
		return view;
	}

	@Override
	public void onDestroyView()
	{
		// TODO Auto-generated method stub
		super.onDestroyView();
		LogUtil.e("PurchaseSearchFilterFragment", "onDestroyView:" + key);
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.e("PurchaseSearchFilterFragment", "onDestroy:" + key);
	}

	@Override
	public void onDetach()
	{
		// TODO Auto-generated method stub
		super.onDetach();
		LogUtil.e("PurchaseSearchFilterFragment", "onDetach:" + key);
	}

	public static PurchaseSearchFilterFragment newInstance(String key, ArrayList<SearchResultKeyValue> value)
	{
		PurchaseSearchFilterFragment fragment = new PurchaseSearchFilterFragment();

		Bundle bundle = new Bundle();
		bundle.putString("key", key);
		bundle.putParcelableArrayList("value", value);
		fragment.setArguments(bundle);
		return fragment;
	}

	public void resetChoose()
	{
		//对于 目录的筛选，不进行重置
		if ("category".equals(key))
		{
			if (!Utils.isEmpty(property[4]))
			{
				return;
			}
		}
		curChoose = -1;
		// mAdapter
		if (mAdapter != null)
			mAdapter.setChoosePostion(curChoose);
	}

	/**
	 * 搜索联动，目前不使用
	 */
	private void getData()
	{
		if ("category".equals(key))
		{
			RequestCenter.searchRfq(property[0], property[1], tempValue, property[3], 1, 20, dataListener);
		}
		else if ("location".equals(key))
		{
			RequestCenter.searchRfq(property[0], tempValue, property[2], property[3], 1, 20, dataListener);
		}
		else if ("postdate".equals(key))
		{
			RequestCenter.searchRfq(property[0], property[1], property[2], tempValue, 1, 20, dataListener);
		}

	}

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub
			if (mListener != null)
			{
				// tempValue = content.get(position).value;
				// getData();
				curChoose = position;

				if (mAdapter != null)
					mAdapter.setChoosePostion(curChoose);

				if (-1 == mAdapter.getChoosePostion())
				{
					mListener.onItemSelected(key, "");
				}
				else
				{
					if ("location".equals(key))
					{
						mListener.onItemSelected(key, mAdapter.getChooseValue());
					}
					else
					{
						mListener.onItemSelected(key, content.get(position).value);
					}
				}

			}
			if ("country".equals(key))
			{
				MobclickAgent.onEvent(getActivity(), "85");
				SysManager.analysis(R.string.c_type_click, R.string.c085);
			}
			else if ("category".equals(key))
			{
				MobclickAgent.onEvent(getActivity(), "84");
				SysManager.analysis(R.string.c_type_click, R.string.c084);
			}
			else if ("postDate".equals(key))
			{
				MobclickAgent.onEvent(getActivity(), "86");
				SysManager.analysis(R.string.c_type_click, R.string.c086);
			}

		}
	};

	// 下面代码是准备联动使用的，目前不使用
	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			value = (SearchResultContent) obj;
			if ("0".equals(value.code))
			{
				if (mAdapter != null)
					mAdapter.setChoosePostion(curChoose);
				mListener.onItemSelected(key, content.get(curChoose).value);
			}
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub

		}
	};
}
