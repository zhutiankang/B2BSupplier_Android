package com.micen.suppliers.activity.setting;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class FeedBackActivity extends BaseActivity
{
	@ViewById(R.id.cb_feedback_reason_function)
	protected CheckBox cbFunction;
	@ViewById(R.id.cb_feedback_reason_use)
	protected CheckBox cbUse;
	@ViewById(R.id.cb_feedback_reason_exception)
	protected CheckBox cbException;
	@ViewById(R.id.cb_feedback_reason_other)
	protected CheckBox cbOther;
	@ViewById(R.id.et_feedback_name)
	protected EditText etName;
	@ViewById(R.id.et_feedback_telephone)
	protected EditText etTelephone;
	@ViewById(R.id.et_feedback_content)
	protected EditText etContent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initNavigationBarStyle(false);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		tvTitle.setText(R.string.setting_feedback);
		btnFunction1.setImageResource(R.drawable.ic_feedback_post_gray);
		btnFunction1.setLayoutParams(new LayoutParams(Utils.toDip(this, 54), LayoutParams.MATCH_PARENT));
		btnFunction1.setVisibility(View.VISIBLE);
		llBack.setOnClickListener(this);
		btnFunction1.setOnClickListener(this);
		btnFunction1.setClickable(false);

		cbFunction.setOnCheckedChangeListener(checkedChange);
		cbUse.setOnCheckedChangeListener(checkedChange);
		cbException.setOnCheckedChangeListener(checkedChange);
		cbOther.setOnCheckedChangeListener(checkedChange);

		etContent.addTextChangedListener(contentWatcher);
	}

	private TextWatcher contentWatcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			if (!"".equals(etContent.getText().toString().trim()))
			{
				btnFunction1.setImageResource(R.drawable.ic_feedback_post);
				btnFunction1.setClickable(true);
			}
			else
			{
				btnFunction1.setImageResource(R.drawable.ic_feedback_post_gray);
				btnFunction1.setClickable(false);
			}
		}
	};

	private OnCheckedChangeListener checkedChange = new OnCheckedChangeListener()
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			buttonView.setBackgroundResource(isChecked ? R.drawable.bg_feedback_type_selected
					: R.drawable.bg_feedback_type);
			buttonView.setTextColor(isChecked ? getResources().getColor(R.color.white) : getResources().getColor(
					R.color.color_333333));
		}
	};

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			finish();
			break;
		case R.id.common_title_function1:
			CommonProgressDialog.getInstance().showProgressDialog(this, getString(R.string.loading));
			RequestCenter.feedback(assembleFeedbackContent(), new SimpleDisposeDataListener()
			{
				@Override
				public void onSuccess(Object obj)
				{
					CommonProgressDialog.getInstance().dismissProgressDialog();
					if (!isFinishing())
					{
						ToastUtil.toast(FeedBackActivity.this, R.string.feedback_success);
					}
					finish();
				}

				@Override
				public void onFailure(Object failedMsg)
				{
					CommonProgressDialog.getInstance().dismissProgressDialog();
					if (!isFinishing())
					{
						ToastUtil.toast(FeedBackActivity.this, failedMsg);
					}
				}

			});
			MobclickAgent.onEvent(FeedBackActivity.this, "132");
			SysManager.analysis(R.string.c_type_click, R.string.c132);
			break;
		}
	}

	/**
	 * 拼装反馈内容
	 * @return
	 */
	private String assembleFeedbackContent()
	{
		StringBuffer buffer = new StringBuffer();
		if (cbFunction.isChecked())
			buffer.append(cbFunction.getText().toString()).append(" ");
		if (cbUse.isChecked())
			buffer.append(cbUse.getText().toString()).append(" ");
		if (cbException.isChecked())
			buffer.append(cbException.getText().toString()).append(" ");
		if (cbOther.isChecked())
			buffer.append(cbOther.getText().toString());
		if (buffer.length() > 0)
			buffer.append("<br/>");
		if (!"".equals(etName.getText().toString().trim()))
			buffer.append(etName.getText().toString()).append("<br/>");
		if (!"".equals(etTelephone.getText().toString().trim()))
			buffer.append(etTelephone.getText().toString()).append("<br/>");
		if (!"".equals(etContent.getText().toString().trim()))
			buffer.append(etContent.getText().toString());
		return buffer.toString();
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10028));
		SysManager.analysis(R.string.p_type_page, R.string.p10028);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10028));
		MobclickAgent.onPause(this);
	}
}
