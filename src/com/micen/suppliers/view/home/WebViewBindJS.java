package com.micen.suppliers.view.home;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.module.WebViewType;


public class WebViewBindJS
{
	private Activity mActivity;
	public static final String WEBVIEW_BIND_OBJECT = "discovery";

	public WebViewBindJS(Activity activity)
	{
		mActivity = activity;
	}

	@JavascriptInterface
	public void jump(String url, String id)
	{
		Intent intent = new Intent(mActivity, WebViewActivity_.class);
		intent.putExtra("targetUri", url);
		intent.putExtra("targetType", WebViewType.getValue(WebViewType.Discovery));
		mActivity.startActivity(intent);
	}

}
