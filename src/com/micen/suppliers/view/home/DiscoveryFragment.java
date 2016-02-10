package com.micen.suppliers.view.home;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.focustech.common.widget.pulltorefresh.PullToRefreshBase;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.focustech.common.widget.pulltorefresh.PullToRefreshBase.State;
import com.focustech.common.widget.pulltorefresh.PullToRefreshWebView;
import com.micen.suppliers.R;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.util.Util;
import com.umeng.analytics.MobclickAgent;


@EFragment(R.layout.discovery_fragment_layout)
public class DiscoveryFragment extends HomeBaseFragment implements OnRefreshListener2<WebView>
{
	@ViewById(R.id.discovery_title_name)
	protected TextView tvTitle;
	@ViewById(R.id.discovery_webview)
	protected PullToRefreshWebView pullToRefreshWebView;
	@ViewById(R.id.webview_progressbar)
	protected ProgressBar progressBar;

	private WebView webView;

	public DiscoveryFragment()
	{

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initConnectionReceiver();
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(connectionReceiver); // 取消监听
	}

	@SuppressLint(
	{ "SetJavaScriptEnabled" })
	@Override
	protected void initView()
	{
		tvTitle.setText(R.string.home_bottom_title2);

		pullToRefreshWebView.setMode(Mode.PULL_FROM_START);
		pullToRefreshWebView.getLoadingLayoutProxy().setLastUpdatedLabel(
				SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
		pullToRefreshWebView.setOnPullEventListener(pullEventListener);
		pullToRefreshWebView.setOnRefreshListener(this);
		webView = pullToRefreshWebView.getRefreshableView();
		webView.setWebViewClient(webViewClient);
		webView.setWebChromeClient(webChromeClient);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.addJavascriptInterface(new WebViewBindJS(getActivity()), WebViewBindJS.WEBVIEW_BIND_OBJECT);

		webView.loadUrl(SupplierApplication.getInstance().getUser().content.findChannelUrl);
		// webView.loadUrl("file:///android_asset/html/discovery.html");
	}

	private WebViewClient webViewClient = new WebViewClient()
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// view.loadUrl(url);
			return true;
		};

		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon)
		{
			progressBar.setVisibility(View.VISIBLE);
		};

		public void onPageFinished(WebView view, String url)
		{
			progressBar.setVisibility(View.GONE);
			refreshComplete();
		};
	};

	private WebChromeClient webChromeClient = new WebChromeClient()
	{
		public void onProgressChanged(WebView view, int newProgress)
		{
			progressBar.setProgress(newProgress);
			if (newProgress == 100)
			{
				progressBar.setVisibility(View.GONE);
			}
		};

	};

	@Override
	public void onResume()
	{
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10031));
		SysManager.analysis(R.string.p_type_page, R.string.p10031);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10031));
	}

	/**
	 * 刷新完成
	 */
	private void refreshComplete()
	{
		if (pullToRefreshWebView != null)
		{
			pullToRefreshWebView.onRefreshComplete();
		}
	}

	private OnPullEventListener<WebView> pullEventListener = new OnPullEventListener<WebView>()
	{
		@Override
		public void onPullEvent(PullToRefreshBase<WebView> refreshView, State state, Mode direction)
		{
			switch (direction)
			{
			case PULL_FROM_START:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
						SharedPreferenceManager.getInstance().getString(getFragmentTag(), ""));
				break;
			default:
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("");
				break;
			}
		}
	};

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<WebView> refreshView)
	{
		Util.saveChildLastRefreshTime(getActivity(), getFragmentTag());
		webView.reload();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<WebView> refreshView)
	{

	}

	@Override
	protected String getFragmentTag()
	{
		return DiscoveryFragment.class.getName();
	}

	private void initConnectionReceiver()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		getActivity().registerReceiver(connectionReceiver, intentFilter);
	}

	private BroadcastReceiver connectionReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected())
			{
				// unconnect network
			}
			else
			{
				webView.reload();
			}
		}
	};

}
