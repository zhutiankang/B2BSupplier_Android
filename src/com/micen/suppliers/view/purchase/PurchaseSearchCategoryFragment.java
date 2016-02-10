package com.micen.suppliers.view.purchase;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.adapter.purchase.PurchaseCategoryAdapter;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.DataManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.db.CategoryHistory;
import com.micen.suppliers.module.purchase.CategoryContent;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.micen.suppliers.widget.flowlayout.FlowLayout;
import com.micen.suppliers.widget.flowlayout.FlowLayout.LayoutParams;
import com.umeng.analytics.MobclickAgent;


public class PurchaseSearchCategoryFragment extends Fragment
{
	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;

	private ListView listview;

	private PurchaseCategoryAdapter adapter;

	private TextView tvCategoryHistory;
	private FlowLayout flowLayout;

	private Context mContext;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_purchase_choose_category, container, false);

		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		tvTitle = (TextView) view.findViewById(R.id.common_title_name);

		ivBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_btn);

		tvTitle.setText(R.string.category_zh);

		tvCategoryHistory = (TextView) view.findViewById(R.id.purchase_search_categoryhistoryTextView);

		listview = (ListView) view.findViewById(R.id.purchase_choosecategory_categotyListView);
		listview.setOnItemClickListener(itemClickListener);

		flowLayout = (FlowLayout) view.findViewById(R.id.purchase_search_recentcategoryFlowLayout);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);
		progressBar.setVisibility(view.VISIBLE);

		initData();
		return view;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		showHistoryCategory();
		MobclickAgent.onPageStart(getString(R.string.p10012));
		SysManager.analysis(R.string.p_type_page, R.string.p10012);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10012));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (1 == resultCode)
		{
			if (null != data)
			{
				boolean issearch = data.getBooleanExtra("isearch", false);
				if (issearch)
				{
					getActivity().finish();
				}
			}
		}
	}

	public static PurchaseSearchCategoryFragment newInstance()
	{
		PurchaseSearchCategoryFragment fragment = new PurchaseSearchCategoryFragment();
		return fragment;
	}

	private void initData()
	{
		listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		RequestCenter.getCategoryList(dataListener);
	}

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

			default:
				break;
			}

		}
	};

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub
			adapter.setChoosePostion(position);
			// adapter.setChoosePostion(position);
			DataManager.getInstance().insertCategoryHistory(adapter.getItem(position).catNameEn,
					adapter.getItem(position).catCode);

			// Intent intent = new Intent();
			// intent.putExtra("category", adapter.getCategoty());
			// getActivity().setResult(1, intent);

			// getActivity().finish();

			// 搜索结果页面
			Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
			intent.putExtra("fragment", "searchresult");
			intent.putExtra("category", adapter.getItem(position).catCode);
			intent.putExtra("categoryname", adapter.getItem(position).catNameEn);
			startActivityForResult(intent, 1);
			MobclickAgent.onEvent(getActivity(), "76");
			SysManager.analysis(R.string.c_type_click, R.string.c076);
		}
	};

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			CategoryContent value = (CategoryContent) obj;

			if ("0".equals(value.code) && null != value.content)
			{
				if (value.content.size() > 0)
				{
					// CategoryItem item = new CategoryItem();
					// item.catNameEn = "ALL";
					// value.content.add(0, item);
					adapter = new PurchaseCategoryAdapter(getActivity(), value.content);
					listview.setAdapter(adapter);

					// showHistoryCategory();
					showRelatedData();
					// showNetworkError();
				}
				else
				{
					showNoRelatedData();
				}
			}
			else
			{
				showNoRelatedData();
			}
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			// TODO Auto-generated method stub

			showNoRelatedData();

		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			// TODO Auto-generated method stub

			showNetworkError();

		}
	};

	private LinkClickListener reloadListener = new LinkClickListener()
	{

		@Override
		public void onClick(PageStatus status)
		{
			// TODO Auto-generated method stub
			showwait();
			initData();
		}

	};

	private void showHistoryCategory()
	{
		ArrayList<CategoryHistory> recentCategoryList = DataManager.getInstance().initRecentCategory();
		if (recentCategoryList.size() <= 0)
		{
			// recentCategoryLayout.setVisibility(View.GONE);
			tvCategoryHistory.setVisibility(View.GONE);
			flowLayout.setVisibility(View.GONE);

		}
		else
		{
			flowLayout.removeAllViews();
			tvCategoryHistory.setVisibility(View.VISIBLE);
			// recentCategoryLayout.setVisibility(View.VISIBLE);
			flowLayout.setVisibility(View.VISIBLE);
			// flowLayout.removeAllViews();
			for (int i = 0; i < recentCategoryList.size(); i++)
			{
				flowLayout.addView(createButton(recentCategoryList.get(i)));
			}
			// flowLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 创建流式按钮
	 * @param categoryHistory
	 * @return
	 */
	private TextView createButton(final CategoryHistory categoryHistory)
	{

		if (1 > Constants.density)
		{
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			Constants.density = dm.density;
		}

		TextView recentCategoryBtn = new TextView(mContext);
		FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(LayoutParams.WRAP_CONTENT, mContext.getResources()
				.getDimensionPixelSize(R.dimen.flow_btn_height));
		// params.leftMargin = Util.dip2px(9);
		params.rightMargin = Util.dip2px(10);
		params.topMargin = Util.dip2px(13);

		// recentCategoryBtn.setMaxWidth(Util.dip2px(150));
		recentCategoryBtn.setLayoutParams(params);
		// recentCategoryBtn.setLayoutParams(new FlowLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
		recentCategoryBtn.setEllipsize(TruncateAt.END);
		recentCategoryBtn.setSingleLine(true);
		recentCategoryBtn.setGravity(Gravity.CENTER);
		recentCategoryBtn.setBackgroundResource(R.drawable.bg_recent_category_drawable);
		recentCategoryBtn.setTextColor(mContext.getResources().getColor(R.color.color_333333));
		recentCategoryBtn.setTextSize(15);
		recentCategoryBtn.setText(categoryHistory.categoriesHistory);
		recentCategoryBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				/*
				 * Intent intent = new Intent(); intent.putExtra("category",categoryHistory.category);
				 * getActivity().setResult(1, intent); getActivity().finish();
				 */
				Intent intent = new Intent(getActivity(), PurchaseActivity_.class);
				intent.putExtra("fragment", "searchresult");
				intent.putExtra("category", categoryHistory.category);
				intent.putExtra("categoryname", categoryHistory.categoriesHistory);
				startActivityForResult(intent, 1);
				MobclickAgent.onEvent(getActivity(), "75");
				SysManager.analysis(R.string.c_type_click, R.string.c075);
			}
		});

		return recentCategoryBtn;
	}

	private void showwait()
	{
		listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void showNoRelatedData()
	{
		listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}

	private void showRelatedData()
	{
		listview.setVisibility(View.VISIBLE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
	}

	private void showNetworkError()
	{
		listview.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}
}
