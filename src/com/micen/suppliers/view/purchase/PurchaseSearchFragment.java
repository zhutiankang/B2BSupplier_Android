package com.micen.suppliers.view.purchase;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.adapter.purchase.PurchaseSearchHistoryAdapter;
import com.micen.suppliers.manager.DataManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.db.SearchRecord;
import com.micen.suppliers.util.Util;
import com.umeng.analytics.MobclickAgent;


public class PurchaseSearchFragment extends Fragment
{
	private ImageView ivBack;
	private ImageView btnCategory;
	private ImageView ivSearch;
	private ImageView ivClear;

	private EditText editText;

	private String category;
	private String keyword;

	private ListView listView;
	private PurchaseSearchHistoryAdapter adapter;

	private View activityRootView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_search, container, false);

		initView(view);

		return view;
	}

	public static PurchaseSearchFragment newInstance()
	{
		PurchaseSearchFragment fragment = new PurchaseSearchFragment();
		return fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (null != data)
		{
			category = data.getStringExtra("category");
			// btnCategory.setText(categoryName);
		}
	}

	private void initView(View view)
	{
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		ivBack.setOnClickListener(listener);

		btnCategory = (ImageView) view.findViewById(R.id.fragment_search_categoryButton);
		btnCategory.setOnClickListener(listener);

		ivSearch = (ImageView) view.findViewById(R.id.purchase_search_searchImageView);
		// 不进行搜索
		// ivSearch.setOnClickListener(listener);

		ivClear = (ImageView) view.findViewById(R.id.purchase_search_clearImageView);
		ivClear.setOnClickListener(listener);

		editText = (EditText) view.findViewById(R.id.purchase_search_contentEditText);
		editText.setOnEditorActionListener(actionListenr);
		editText.addTextChangedListener(textWatch);

		listView = (ListView) view.findViewById(R.id.purchase_search_historyListView);
		listView.setOnItemClickListener(itemClickListener);

		activityRootView = view.findViewById(R.id.activityRoot);

	}

	private OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener()
	{

		@Override
		public void onGlobalLayout()
		{
			int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
			if (heightDiff < 100)
			{   // 如果高度差不超过100像素，就很有可能没有软键盘，edittext失去焦点
				ivBack.setFocusable(true);
				ivBack.setFocusableInTouchMode(true);
				ivBack.requestFocus();
			}
		}

	};

	private OnClickListener listener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.common_title_back:
				Util.hideSoftKeyboard(getActivity());
				getActivity().finish();
				break;
			case R.id.fragment_search_categoryButton:
				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
				intent.putExtra("fragment", "choosecategory");
				startActivityForResult(intent, 1);
				MobclickAgent.onEvent(getActivity(), "73");
				SysManager.analysis(R.string.c_type_click, R.string.c073);
				break;
			case R.id.purchase_search_searchImageView:
				doSearch();
				MobclickAgent.onEvent(getActivity(), "72");
				SysManager.analysis(R.string.c_type_click, R.string.c072);
				break;
			case R.id.purchase_search_clearImageView:
				editText.setText("");
				break;
			default:
				break;
			}
			// TODO Auto-generated method stub

		}
	};

	private OnEditorActionListener actionListenr = new OnEditorActionListener()
	{

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
		{
			// TODO Auto-generated method stub
			boolean handled = false;
			if (actionId == EditorInfo.IME_ACTION_SEARCH)
			{
				doSearch();
				handled = true;
			}
			else if (null != event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
				{
					doSearch();
					handled = true;
				}
			}
			return handled;

		}

	};

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub
			editText.setText(adapter.getItem(position).recentKeywords);
			doSearch();
			MobclickAgent.onEvent(getActivity(), "74");
			SysManager.analysis(R.string.c_type_click, R.string.c074);
		}

	};

	private TextWatcher textWatch = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub
			if (Utils.isEmpty(editText.getText().toString()))
			{
				ivClear.setVisibility(View.INVISIBLE);
			}
			else
			{
				ivClear.setVisibility(View.VISIBLE);
			}
		}
	};

	private void getData()
	{
		ArrayList<SearchRecord> dataList = DataManager.getInstance().refreshRecentKeywordsList("rfq");

		adapter = new PurchaseSearchHistoryAdapter(getActivity(), dataList);
		listView.setAdapter(adapter);
	}

	private void doSearch()
	{
		keyword = editText.getText().toString().trim();

		if (Utils.isEmpty(keyword))
		{
			Toast.makeText(getActivity(), "关键词为空，请重新输入", Toast.LENGTH_SHORT).show();
			editText.setText("");
		}
		else
		{
			// 关键词存入数据库
			DataManager.getInstance().insertKeyWords(keyword, "rfq");
			// 执行搜索
			searchRFQ();
		}

	}

	private void searchRFQ()
	{
		// 搜索结果页面
		Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
		intent.putExtra("fragment", "searchresult");
		intent.putExtra("keyword", keyword);
		intent.putExtra("category", category);
		startActivity(intent);
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		getData();
		MobclickAgent.onPageStart(getString(R.string.p10011));
		SysManager.analysis(R.string.p_type_page, R.string.p10011);

		editText.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				// 监听软键盘状态
				activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
			}
		}, 300);

	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10011));
	}

}
