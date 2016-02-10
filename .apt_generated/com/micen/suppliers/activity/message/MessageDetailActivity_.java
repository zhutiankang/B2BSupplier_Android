//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.1.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.micen.suppliers.activity.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.micen.suppliers.R.id;
import com.micen.suppliers.view.PageStatusView;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class MessageDetailActivity_
    extends MessageDetailActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String MESSAGE_ID_EXTRA = "messageId";
    public final static String IS_FROM_BROADCAST_EXTRA = "isFromBroadcast";

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

    public static MessageDetailActivity_.IntentBuilder_ intent(Context context) {
        return new MessageDetailActivity_.IntentBuilder_(context);
    }

    public static MessageDetailActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new MessageDetailActivity_.IntentBuilder_(fragment);
    }

    public static MessageDetailActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new MessageDetailActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        btnFunction4 = ((ImageView) hasViews.findViewById(id.common_title_function4));
        llBack = ((LinearLayout) hasViews.findViewById(id.common_ll_title_back));
        tvTitle = ((TextView) hasViews.findViewById(id.common_title_name));
        btnFunction3 = ((ImageView) hasViews.findViewById(id.common_title_function3));
        btnBack = ((ImageView) hasViews.findViewById(id.common_title_back));
        btnFunction2 = ((ImageView) hasViews.findViewById(id.common_title_function2));
        btnTextFunction = ((TextView) hasViews.findViewById(id.common_title_text_function));
        btnFunction5 = ((ImageView) hasViews.findViewById(id.common_title_function5));
        btnFunction1 = ((ImageView) hasViews.findViewById(id.common_title_function1));
        attachmentLl = ((LinearLayout) hasViews.findViewById(id.message_detail_attachment_ll));
        userrlOtherPeopleCountryImage = ((RelativeLayout) hasViews.findViewById(id.message_detail_user_rl_other_people_country_image));
        subjectTvDate = ((TextView) hasViews.findViewById(id.message_detail_subject_tv_date));
        userIvOtherPeopleCountryImage = ((ImageView) hasViews.findViewById(id.message_detail_user_iv_other_people_country_image));
        messageDetailLl = ((LinearLayout) hasViews.findViewById(id.message_detail_ll));
        userTvOtherPeople = ((TextView) hasViews.findViewById(id.message_detail_user_tv_other_people));
        productLlInterest = ((LinearLayout) hasViews.findViewById(id.message_detail_product_ll_interest));
        userTvOtherPeopleName = ((TextView) hasViews.findViewById(id.message_detail_user_tv_other_people_name));
        userLLOtherPeopleLocation = ((LinearLayout) hasViews.findViewById(id.message_detail_user_ll_other_people_location));
        attachmentHl = ((LinearLayout) hasViews.findViewById(id.message_attachment_hl));
        tvContent = ((TextView) hasViews.findViewById(id.message_detail_tv_content));
        productTvProductName = ((TextView) hasViews.findViewById(id.message_detail_product_tv_product_name));
        userTvOur = ((TextView) hasViews.findViewById(id.message_detail_user_tv_our));
        progressBar = ((RelativeLayout) hasViews.findViewById(id.message_details_progressbar));
        productIvThumb = ((ImageView) hasViews.findViewById(id.message_detail_product_iv_thumb));
        messageTitleRl = ((RelativeLayout) hasViews.findViewById(id.rl_common_title));
        subjectTvTitle = ((TextView) hasViews.findViewById(id.message_detail_subject_tv_title));
        userTvBehaviorRecord = ((TextView) hasViews.findViewById(id.message_detail_user_tv_behavior_record));
        userTvotherPeopleLocationContent = ((TextView) hasViews.findViewById(id.message_detail_user_tv_other_people_location_content));
        productLl = ((LinearLayout) hasViews.findViewById(id.message_detail_product_ll));
        messageDetailScroll = ((ScrollView) hasViews.findViewById(id.message_detail_scroll));
        userTvOurName = ((TextView) hasViews.findViewById(id.message_detail_user_tv_our_name));
        statusView = ((PageStatusView) hasViews.findViewById(id.broadcast_page_status));
        initView();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(MESSAGE_ID_EXTRA)) {
                messageId = extras_.getString(MESSAGE_ID_EXTRA);
            }
            if (extras_.containsKey(IS_FROM_BROADCAST_EXTRA)) {
                isFromBroadcast = extras_.getBoolean(IS_FROM_BROADCAST_EXTRA);
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<MessageDetailActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, MessageDetailActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), MessageDetailActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), MessageDetailActivity_.class);
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

        public MessageDetailActivity_.IntentBuilder_ messageId(String messageId) {
            return super.extra(MESSAGE_ID_EXTRA, messageId);
        }

        public MessageDetailActivity_.IntentBuilder_ isFromBroadcast(boolean isFromBroadcast) {
            return super.extra(IS_FROM_BROADCAST_EXTRA, isFromBroadcast);
        }

    }

}
