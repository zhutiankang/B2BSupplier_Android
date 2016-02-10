package com.micen.suppliers.activity;

import java.util.Calendar;
import java.util.TimeZone;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.focustech.common.http.FocusClient;
import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.associatemail.MailBoxAssociateTokenizer;
import com.focustech.common.widget.associatemail.MailBoxAssociateView;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.home.HomeActivity_;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.user.User;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class LoginActivity extends BaseActivity
{
	@ViewById(R.id.associate_email_clear)
	protected ImageView btnEmailClear;
	@ViewById(R.id.associate_email_input)
	protected MailBoxAssociateView emailInput;
	@ViewById(R.id.password_clear)
	protected ImageView btnPasswordClear;
	@ViewById(R.id.ic_password_show)
	protected ImageView btnPasswordShow;
	@ViewById(R.id.login_input_password)
	protected EditText passwordInput;
	@ViewById(R.id.btn_login)
	protected TextView btnLogin;

	@StringArrayRes(R.array.recommend_mailbox)
	protected String[] recommendMailBox;

	private boolean isShowPassword = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initNavigationBarStyle(true);
		MobclickAgent.openActivityDurationTrack(false);
	}

	@AfterViews
	protected void initView()
	{
		btnEmailClear.setOnClickListener(this);
		btnPasswordClear.setOnClickListener(this);
		btnPasswordShow.setOnClickListener(this);

		emailInput.setText(SharedPreferenceManager.getInstance().getString("lastLoginName", ""));
		passwordInput.setText(SharedPreferenceManager.getInstance().getString("lastLoginPassword", ""));
		Utils.setEditTextCursorPosition(emailInput);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.associate_mail_list_item,
				R.id.tv_recommend_mail, recommendMailBox);

		emailInput.setAdapter(adapter);
		emailInput.setTokenizer(new MailBoxAssociateTokenizer());
		passwordInput.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		passwordInput.setOnKeyListener(onKeyListener);
		btnLogin.setOnClickListener(this);
		emailInput.addTextChangedListener(watcher);
		passwordInput.addTextChangedListener(watcher);
		emailInput.setOnClickListener(this);
		passwordInput.setOnClickListener(this);
		emailInput.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// TODO Auto-generated method stub
				// 事件统计 115 点击使用邮箱联想（登录页） 点击事件
				MobclickAgent.onEvent(LoginActivity.this, "117");
				SysManager.analysis(R.string.c_type_click, R.string.c117);
			}
		});
		passwordInput.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				MobclickAgent.onEvent(LoginActivity.this, "118");
				SysManager.analysis(R.string.c_type_click, R.string.c118);
			}
		});
		checkBtnStatus();
	}

	private TextWatcher watcher = new TextWatcher()
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
			checkBtnStatus();
		}
	};

	private void checkBtnStatus()
	{
		if (!"".equals(emailInput.getText().toString().trim()) && !"".equals(passwordInput.getText().toString().trim()))
		{
			btnLogin.setClickable(true);
			btnLogin.setTextColor(getResources().getColor(R.color.white));
		}
		else
		{
			btnLogin.setClickable(false);
			btnLogin.setTextColor(getResources().getColor(R.color.color_8ccdff));
		}
		btnEmailClear.setVisibility("".equals(emailInput.getText().toString()) ? View.GONE : View.VISIBLE);
		btnPasswordClear.setVisibility("".equals(passwordInput.getText().toString()) ? View.GONE : View.VISIBLE);
	}

	private OnKeyListener onKeyListener = new OnKeyListener()
	{
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event)
		{
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
			{
				SysManager.getInstance().dismissInputKey(LoginActivity.this);
				sendLoginRequest();
				return true;
			}
			return false;
		}
	};

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.btn_login:
			sendLoginRequest();
			MobclickAgent.onEvent(LoginActivity.this, "120");
			SysManager.analysis(R.string.c_type_click, R.string.c120);
			break;
		case R.id.associate_email_clear:
			emailInput.setText("");
			break;
		case R.id.password_clear:
			passwordInput.setText("");
			break;
		case R.id.ic_password_show:
			if (!isShowPassword)
			{
				isShowPassword = true;
				passwordInput.setInputType(EditorInfo.TYPE_CLASS_TEXT);
				btnPasswordShow.setBackgroundResource(R.drawable.ic_password_show_on);
			}
			else
			{
				isShowPassword = false;
				passwordInput.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
				btnPasswordShow.setBackgroundResource(R.drawable.ic_password_show_off);
			}
			Utils.setEditTextCursorPosition(passwordInput);
			MobclickAgent.onEvent(LoginActivity.this, "119");
			SysManager.analysis(R.string.c_type_click, R.string.c119);
			break;
		}
	}

	private DisposeDataListener loginListener = new SimpleDisposeDataListener()
	{
		@Override
		public void onSuccess(Object result)
		{
			FocusClient.setPersistentCookieStore(LoginActivity.this);
			CommonProgressDialog.getInstance().dismissProgressDialog();
			SharedPreferenceManager.getInstance().putBoolean("isAutoLogon", true);
			SharedPreferenceManager.getInstance().putString("lastLoginName", emailInput.getText().toString().trim());
			SharedPreferenceManager.getInstance().putString("lastLoginPassword",
					passwordInput.getText().toString().trim());
			SharedPreferenceManager.getInstance().putLong("lastLoginTime",
					Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis());
			SupplierApplication.getInstance().setUser((User) result);
			RequestCenter.boundAccount(new SimpleDisposeDataListener(), true);
			ToastUtil.toast(LoginActivity.this, R.string.login_successful);
			startActivity(new Intent(LoginActivity.this, HomeActivity_.class));
			finish();
		}

		public void onFailure(Object failedMsg)
		{
			CommonProgressDialog.getInstance().dismissProgressDialog();
			ToastUtil.toast(LoginActivity.this, failedMsg);
		};

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			CommonDialog dialog = new CommonDialog(this);
			dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
					.setConfirmDialogListener(new DialogClickListener()
					{
						@Override
						public void onDialogClick()
						{
							SysManager.exitSystem(LoginActivity.this);
						}
					}).buildSimpleDialog(getString(R.string.exit_dialog_msg));
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 发送登陆请求
	 */
	private void sendLoginRequest()
	{
		if ("".equals(emailInput.getText().toString().trim()) || "".equals(passwordInput.getText().toString().trim()))
		{
			ToastUtil.toast(LoginActivity.this, R.string.login_input_error);
			return;
		}
		else if (passwordInput.getText().toString().trim().length() <= 6)
		{
			ToastUtil.toast(LoginActivity.this, R.string.login_password_too_short);
			return;
		}
		else
		{
			CommonProgressDialog.getInstance().showProgressDialog(this, getString(R.string.loading));
			String userName = emailInput.getText().toString().trim();
			String passWord = passwordInput.getText().toString().trim();
			RequestCenter.login(userName, passWord, loginListener);
		}
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10025));
		SysManager.analysis(R.string.p_type_page, R.string.p10025);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10025));
		MobclickAgent.onPause(this);
	}

}
