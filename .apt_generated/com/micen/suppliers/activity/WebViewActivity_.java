//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.1.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.micen.suppliers.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.micen.suppliers.R.id;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WebViewActivity_
    extends WebViewActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String IS_FROM_BROADCAST_EXTRA = "isFromBroadcast";
    public final static String MESSAGE_ID_EXTRA = "messageId";
    public final static String LOAD_URL_EXTRA = "targetUri";
    public final static String TARGET_TYPE_EXTRA = "targetType";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectExtras_();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static WebViewActivity_.IntentBuilder_ intent(Context context) {
        return new WebViewActivity_.IntentBuilder_(context);
    }

    public static WebViewActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new WebViewActivity_.IntentBuilder_(fragment);
    }

    public static WebViewActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new WebViewActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        btnFunction2 = ((ImageView) hasViews.findViewById(id.common_title_function2));
        btnFunction5 = ((ImageView) hasViews.findViewById(id.common_title_function5));
        btnTextFunction = ((TextView) hasViews.findViewById(id.common_title_text_function));
        btnBack = ((ImageView) hasViews.findViewById(id.common_title_back));
        btnFunction3 = ((ImageView) hasViews.findViewById(id.common_title_function3));
        tvTitle = ((TextView) hasViews.findViewById(id.common_title_name));
        llBack = ((LinearLayout) hasViews.findViewById(id.common_ll_title_back));
        btnFunction4 = ((ImageView) hasViews.findViewById(id.common_title_function4));
        btnFunction1 = ((ImageView) hasViews.findViewById(id.common_title_function1));
        webView = ((WebView) hasViews.findViewById(id.common_webview));
        btnLayout = ((LinearLayout) hasViews.findViewById(id.ll_webview_button));
        btnWebView = ((TextView) hasViews.findViewById(id.common_webview_button));
        progressBar = ((ProgressBar) hasViews.findViewById(id.webview_progressbar));
        initView();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(IS_FROM_BROADCAST_EXTRA)) {
                isFromBroadcast = extras_.getBoolean(IS_FROM_BROADCAST_EXTRA);
            }
            if (extras_.containsKey(MESSAGE_ID_EXTRA)) {
                messageId = extras_.getString(MESSAGE_ID_EXTRA);
            }
            if (extras_.containsKey(LOAD_URL_EXTRA)) {
                loadUrl = extras_.getString(LOAD_URL_EXTRA);
            }
            if (extras_.containsKey(TARGET_TYPE_EXTRA)) {
                targetType = extras_.getString(TARGET_TYPE_EXTRA);
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<WebViewActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, WebViewActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), WebViewActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), WebViewActivity_.class);
            fragmentSupport_ = fragment;
        }

        @Override
        public void startForResult(int requestCode) {
            if (fragmentSupport_!= null) {
                fragmentSupport_.startActivityForResult(intent, requestCode);
            } else {
                if (fragment_!= null) {
                    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                        fragment_.startActivityForResult(intent, requestCode, lastOptions);
                    } else {
                        fragment_.startActivityForResult(intent, requestCode);
                    }
                } else {
                    if (context instanceof Activity) {
                        Activity activity = ((Activity) context);
                        ActivityCompat.startActivityForResult(activity, intent, requestCode, lastOptions);
                    } else {
                        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                            context.startActivity(intent, lastOptions);
                        } else {
                            context.startActivity(intent);
                        }
                    }
                }
            }
        }

        public WebViewActivity_.IntentBuilder_ isFromBroadcast(boolean isFromBroadcast) {
            return super.extra(IS_FROM_BROADCAST_EXTRA, isFromBroadcast);
        }

        public WebViewActivity_.IntentBuilder_ messageId(String messageId) {
            return super.extra(MESSAGE_ID_EXTRA, messageId);
        }

        public WebViewActivity_.IntentBuilder_ loadUrl(String loadUrl) {
            return super.extra(LOAD_URL_EXTRA, loadUrl);
        }

        public WebViewActivity_.IntentBuilder_ targetType(String targetType) {
            return super.extra(TARGET_TYPE_EXTRA, targetType);
        }

    }

}
