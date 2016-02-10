package com.micen.suppliers.view.message;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.module.message.MessageConstantDefine;

@EFragment(R.layout.fragment_message_send)
public class SendFragment extends MessageSentBaseFragment
{
	public SendFragment()
	{

	}

	@AfterViews
	protected void initView()
	{
		super.initView();
		initIntent();
		tvTitle.setText("联系买家");
		sendTvContactsName.setText(replyReceive);
		sendSubjectEtSubject.setText("Reply about " + replySubject);
	}

	protected void initIntent()
	{
		// 发送界面
		// 主题、收件人
		replyMailId = getActivity().getIntent().getStringExtra(MessageConstantDefine.mailId.toString());
		replyReceive = getActivity().getIntent().getStringExtra("replyReceive");
		replySubject = getActivity().getIntent().getStringExtra("replySubject");
	}

	@Override
	public void sendEmail()
	{
		if ("".equals(sendSubjectEtSubject.getText().toString().trim()))
		{
			ToastUtil.toast(getActivity(), R.string.message_subject_isempty);
		}
		else
		{
			if ("".equals(sendEtContent.getText().toString().trim()))
			{
				ToastUtil.toast(getActivity(), R.string.message_content_isempty);
			}
			else
			{
				// 发送询盘部分
				// 事件统计 52 回复询盘发送 点击事件
				// SysManager.analysis(R.string.a_type_click, R.string.c52);
				//
				final String subject = sendSubjectEtSubject.getText().toString().trim();
				final String content = sendEtContent.getText().toString().trim();
				final String attachmentIds = getAttachmentIds();
				CommonProgressDialog.getInstance().showProgressDialog(getActivity(),
						getString(R.string.message_sent_loading));
				// 需要指定 region;
				RequestCenter.replyMessage(new DisposeDataListener()
				{

					@Override
					public void onSuccess(Object obj)
					{
						activityFinishProtect();
						ToastUtil.toast(getActivity(), getString(R.string.message_sent_success));
						CommonProgressDialog.getInstance().dismissProgressDialog();
						getActivity().finish();
					}

					@Override
					public void onDataAnomaly(Object anomalyMsg)
					{
						activityFinishProtect();
						ToastUtil.toast(getActivity(), anomalyMsg);
						CommonProgressDialog.getInstance().dismissProgressDialog();
					}

					@Override
					public void onNetworkAnomaly(Object anomalyMsg)
					{
						activityFinishProtect();
						ToastUtil.toast(getActivity(), anomalyMsg);
						CommonProgressDialog.getInstance().dismissProgressDialog();
					}
				}, replyMailId, subject, content, attachmentIds, "MIC for Supplier Android_quotation");
			}
		}
	}

	private void activityFinishProtect()
	{
		if (getActivity() == null || getActivity().isFinishing())
			return;
	}
}
