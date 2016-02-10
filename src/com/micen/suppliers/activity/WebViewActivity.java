package com.micen.suppliers.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.micen.suppliers.R;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.IntentManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.WebViewType;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class WebViewActivity extends BaseActivity
{
	@ViewById(R.id.common_webview)
	protected WebView webView;
	@ViewById(R.id.webview_progressbar)
	protected ProgressBar progressBar;
	@ViewById(R.id.ll_webview_button)
	protected LinearLayout btnLayout;
	@ViewById(R.id.common_webview_button)
	protected TextView btnWebView;

	@Extra("targetUri")
	protected String loadUrl;
	@Extra("targetType")
	protected String targetType;
	@Extra("isFromBroadcast")
	protected boolean isFromBroadcast = false;
	@Extra("messageId")
	protected String messageId;

	/**
	 * 是否绑定标题(true:不会根据网页内容实时变化)
	 */
	private boolean isBindTitle = false;
	/**
	 * 是否加载出错(true:不使用出错的标题)
	 */
	private boolean isLoadError = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@AfterViews
	protected void initView()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(this);
		btnLayout.setOnClickListener(this);

		switch (WebViewType.getValueByTag(targetType))
		{
		case Apply:
			btnWebView.setText(R.string.service_consult);
			break;
		case AboutUs:
			isBindTitle = true;
			tvTitle.setText(R.string.setting_aboutus);
			btnLayout.setVisibility(View.GONE);
			break;
		default:
			btnLayout.setVisibility(View.GONE);
			break;
		}

		webView.setWebViewClient(webViewClient);
		webView.setWebChromeClient(webChromeClient);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.loadUrl(loadUrl);

		if (isFromBroadcast && !Utils.isEmpty(messageId))
		{
			RequestCenter.updateMesssageStatus(messageId);
		}
	}

	private WebViewClient webViewClient = new WebViewClient()
	{
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		};

		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon)
		{
			progressBar.setVisibility(View.VISIBLE);
		};

		public void onPageFinished(WebView view, String url)
		{
			progressBar.setVisibility(View.GONE);
		};

		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			isLoadError = true;
			view.stopLoading();
			view.loadUrl("about:blank");
			CommonDialog dialog = new CommonDialog(WebViewActivity.this);
			dialog.setDialogMode(true).setConfirmBtnText(getString(R.string.confirm))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							back();
						}
					}).buildSimpleDialog(getString(R.string.discovery404));
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

		public void onReceivedTitle(WebView view, String title)
		{
			if (!isBindTitle && !isLoadError)
			{
				if (!Utils.isEmpty(title))
				{
					tvTitle.setText(title);
				}
				else
				{
					tvTitle.setText(R.string.app_name);
				}
			}
		};
	};

	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			back();
			break;
		case R.id.ll_webview_button:
			switch (WebViewType.getValueByTag(targetType))
			{
			case Apply:
				View view = LayoutInflater.from(this).inflate(R.layout.service_dialog_content, null);
				TextView serviceName = (TextView) view.findViewById(R.id.tv_service_title);
				serviceName.setText(R.string.service_consult);
				TextView serviceTime = (TextView) view.findViewById(R.id.tv_service_time);
				serviceTime.setText(R.string.setting_apply_service_time);
				TextView serviceTel = (TextView) view.findViewById(R.id.tv_service_tel);
				serviceTel.setText(R.string.setting_apply_service_tel);
				LinearLayout serviceCodeLayout = (LinearLayout) view.findViewById(R.id.ll_service_code);
				serviceCodeLayout.setVisibility(View.GONE);
				CommonDialog dialog = new CommonDialog(this);
				dialog.setDialogContentView(view).setCancelBtnText(R.string.cancel).setConfirmBtnText(R.string.call)
						.setConfirmTextColor(getResources().getColor(R.color.color_0088F0))
						.setConfirmDialogListener(new DialogClickListener()
						{
							@Override
							public void onDialogClick()
							{
								IntentManager.makeCall(WebViewActivity.this,
										getString(R.string.setting_apply_service_tel));
							}
						}).build();
				MobclickAgent.onEvent(WebViewActivity.this, "142");
				SysManager.analysis(R.string.c_type_click, R.string.c142);
				break;
			default:
				break;
			}
			break;
		}
	};

	private void back()
	{
		if (webView.canGoBack())
		{
			webView.goBack();
		}
		else
		{
			switch (WebViewType.getValueByTag(targetType))
			{
			case AboutUs:
				MobclickAgent.onEvent(WebViewActivity.this, "133");
				SysManager.analysis(R.string.c_type_click, R.string.c133);
				break;
			case Apply:
				break;
			case Service:
				MobclickAgent.onEvent(WebViewActivity.this, "116");
				SysManager.analysis(R.string.c_type_click, R.string.c116);
				break;
			case Discovery:
				MobclickAgent.onEvent(WebViewActivity.this, "139");
				SysManager.analysis(R.string.c_type_click, R.string.c139);
				break;
			default:
				break;
			}
			webViewClearCache();
			finish();
		}

	}

	@Override
	public void onResume()
	{
		super.onResume();
		switch (WebViewType.getValueByTag(targetType))
		{
		case Service:
			MobclickAgent.onPageStart(getString(R.string.p10024));
			SysManager.analysis(R.string.p_type_page, R.string.p10024);
			break;
		case AboutUs:
			MobclickAgent.onPageStart(getString(R.string.p10029));
			SysManager.analysis(R.string.p_type_page, R.string.p10029);
			break;
		case Discovery:
			MobclickAgent.onPageStart(getString(R.string.p10032));
			SysManager.analysis(R.string.p_type_page, R.string.p10032);
			break;
		default:
			break;
		}
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		switch (WebViewType.getValueByTag(targetType))
		{
		case Service:
			MobclickAgent.onPageEnd(getString(R.string.p10024));
			break;
		case AboutUs:
			MobclickAgent.onPageEnd(getString(R.string.p10029));
			break;
		case Discovery:
			MobclickAgent.onPageEnd(getString(R.string.p10032));
			break;
		default:
			break;
		}
		MobclickAgent.onPause(this);
	}

	@Override
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK))
		{
			if (webView.canGoBack())
			{
				webView.goBack(); // goBack()表示返回WebView的上一页面
			}
			else
			{
				webViewClearCache();
				finish();
			}
			return true;
		}
		return false;
	}

	private void webViewClearCache()
	{
		webView.clearHistory();
		webView.clearCache(true);
	}
}
