package com.micen.suppliers.view.purchase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationAdapter;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.purchase.QuotationList;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.LinkClickListener;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.micen.suppliers.view.SearchListProgressBar;
import com.umeng.analytics.MobclickAgent;


public class QuotationListFragment extends Fragment
{

	private ImageView ivBack;
	private LinearLayout llBack;
	private TextView tvTitle;
	private RelativeLayout rlTitle;

	private String mRfqID;
	private String mIsRecommend;

	// private TextView mTotalTextView;
	private ListView mListView;

	// 等待及无数据
	private PageStatusView pageStateView;
	private SearchListProgressBar progressBar;

	// 平台返回结果
	private QuotationList value;

	private PurchaseQuotationAdapter adapter;

	private DisposeDataListener dataListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			// TODO Auto-generated method stub
			value = (QuotationList) obj;
			if ("0".equals(value.code))
			{
				if (null != value.content)
				{
					// 显示数据
					adapter = new PurchaseQuotationAdapter(getActivity(), value.content);
					mListView.setAdapter(adapter);
					// mTotalTextView.setText(getTotalString(value.content.size()));
					tvTitle.setTextColor(getResources().getColor(R.color.color_0088F0));
					tvTitle.setText(getTotalString(value.content.size()));
					showRelatedData();
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

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null)
		{
			mRfqID = bundle.getString("rfqid");
			mIsRecommend = bundle.getString("isrecommend");
			if (Utils.isEmpty(mIsRecommend))
			{
				mIsRecommend = "0";
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_purchase_quotation_list, container, false);
		ivBack = (ImageView) view.findViewById(R.id.common_title_back);
		llBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		ivBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(clickListener);
		llBack.setBackgroundResource(R.drawable.bg_common_white_btn);

		rlTitle = (RelativeLayout) view.findViewById(R.id.rl_common_title);

		rlTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));

		tvTitle = (TextView) view.findViewById(R.id.common_title_name);

		mListView = (ListView) view.findViewById(R.id.purchase_quotationlist_listListView);
		// mTotalTextView = (TextView) view.findViewById(R.id.purchase_quotationlist__totalTextView);

		pageStateView = (PageStatusView) view.findViewById(R.id.broadcast_page_status);
		progressBar = (SearchListProgressBar) view.findViewById(R.id.progress_bar);
		pageStateView.setLinkOrRefreshOnClickListener(reloadListener);

		showwait();
		initData();
		return view;
	}

	public static QuotationListFragment newInstance(String rfqID, String isrecommend)
	{
		QuotationListFragment fragment = new QuotationListFragment();
		Bundle bundle = new Bundle();
		bundle.putString("rfqid", rfqID);
		bundle.putString("isrecommend", isrecommend);
		fragment.setArguments(bundle);
		return fragment;
	}

	private void initData()
	{
		RequestCenter.getRelatedQuotations(mRfqID, mIsRecommend, dataListener);
	}

	private String getTotalString(int total)
	{
		return getString(R.string.quotationtotal, total);
	}

	private void showwait()
	{
		mListView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
	}

	private void showNoRelatedData()
	{
		mListView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
	}

	private void showRelatedData()
	{
		mListView.setVisibility(View.VISIBLE);
		pageStateView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
	}

	private void showNetworkError()
	{
		mListView.setVisibility(View.GONE);
		pageStateView.setVisibility(View.VISIBLE);
		pageStateView.setMode(PageStatus.PageNetwork);
		progressBar.setVisibility(View.GONE);
	}
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10018));
		SysManager.analysis(R.string.p_type_page, R.string.p10018);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10018));
	}
}
