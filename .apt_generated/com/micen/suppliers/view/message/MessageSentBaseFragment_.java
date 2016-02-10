//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.1.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.micen.suppliers.view.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class MessageSentBaseFragment_
    extends com.micen.suppliers.view.message.MessageSentBaseFragment
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    private View contentView_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    @Override
    public View findViewById(int id) {
        if (contentView_ == null) {
            return null;
        }
        return contentView_.findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        return contentView_;
    }

    @Override
    public void onDestroyView() {
        contentView_ = null;
        super.onDestroyView();
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static MessageSentBaseFragment_.FragmentBuilder_ builder() {
        return new MessageSentBaseFragment_.FragmentBuilder_();
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        sendSubjectEtSubject = ((EditText) hasViews.findViewById(com.micen.suppliers.R.id.message_send_subject_et_subject));
        sendSubjectIvClear = ((ImageView) hasViews.findViewById(com.micen.suppliers.R.id.message_send_subject_iv_clear));
        sendAttachmentLl = ((LinearLayout) hasViews.findViewById(com.micen.suppliers.R.id.message_send_attachment_ll));
        btnBack = ((ImageView) hasViews.findViewById(com.micen.suppliers.R.id.common_title_back));
        tvTitle = ((TextView) hasViews.findViewById(com.micen.suppliers.R.id.common_title_name));
        btnFunction3 = ((ImageView) hasViews.findViewById(com.micen.suppliers.R.id.common_title_function3));
        sendAttachmentLlAttachment = ((LinearLayout) hasViews.findViewById(com.micen.suppliers.R.id.message_send_attachment_ll_attachment));
        sendTvContactsName = ((TextView) hasViews.findViewById(com.micen.suppliers.R.id.message_send_tv_contacts_name));
        sendTvContacts = ((TextView) hasViews.findViewById(com.micen.suppliers.R.id.message_send_tv_contacts));
        btnFunction5 = ((ImageView) hasViews.findViewById(com.micen.suppliers.R.id.common_title_function5));
        sendEtContent = ((EditText) hasViews.findViewById(com.micen.suppliers.R.id.message_send_et_content));
        llBack = ((LinearLayout) hasViews.findViewById(com.micen.suppliers.R.id.common_ll_title_back));
        btnFunction4 = ((ImageView) hasViews.findViewById(com.micen.suppliers.R.id.common_title_function4));
        messageTitleRl = ((RelativeLayout) hasViews.findViewById(com.micen.suppliers.R.id.rl_common_title));
    }

    public static class FragmentBuilder_
        extends FragmentBuilder<MessageSentBaseFragment_.FragmentBuilder_, com.micen.suppliers.view.message.MessageSentBaseFragment>
    {


        @Override
        public com.micen.suppliers.view.message.MessageSentBaseFragment build() {
            MessageSentBaseFragment_ fragment_ = new MessageSentBaseFragment_();
            fragment_.setArguments(args);
            return fragment_;
        }

    }

}
