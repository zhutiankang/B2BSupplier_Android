package com.micen.suppliers.activity.message;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.DBHelper;
import com.micen.suppliers.manager.DataManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.db.MessageShortCut;
import com.umeng.analytics.MobclickAgent;


@EActivity
public class MessageAddShortCutActivity extends BaseActivity
{
	@ViewById(R.id.rl_common_title)
	protected RelativeLayout rlCommonTitle;
	@ViewById(R.id.mail_et_shortup_add)
	protected EditText mailEtShortupAdd;
	private CommonDialog commonDialog;
	private ArrayList<MessageShortCut> mailShortCuts;
	private Boolean shortcutIsContains = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_add_short_cut);
		initNavigationBarStyle(false);
	}

	@AfterViews
	protected void initView()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		btnTextFunction.setVisibility(View.VISIBLE);
		btnTextFunction.setText(R.string.message_save);
		btnTextFunction.setTextColor(getResources().getColor(R.color.color_005799));
		llBack.setOnClickListener(this);
		tvTitle.setText(R.string.message_add_shortcut);
		tvTitle.setTextColor(getResources().getColor(R.color.color_ffffff));
		// 初始化EditText
		// mailEtShortupAdd.setKeyListener(RFQKeyListener.getInstance(Constants.rfqEditTextDigits));
		mailEtShortupAdd.addTextChangedListener(watcher);
		mailEtShortupAdd.setFilters(new InputFilter[]
		{ new InputFilter.LengthFilter(Constants.ADD_SHORT_CUT_LENGTH) });
		btnTextFunction.setOnClickListener(this);
		btnTextFunction.setClickable(false);
		mailShortCuts = new ArrayList<MessageShortCut>();
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);

		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			MobclickAgent.onEvent(MessageAddShortCutActivity.this, "45");
			SysManager.analysis(R.string.c_type_click, R.string.c045);
			back();
			break;
		case R.id.common_title_text_function:
			saveShortCut();
			MobclickAgent.onEvent(MessageAddShortCutActivity.this, "46");
			SysManager.analysis(R.string.c_type_click, R.string.c046);
			break;
		default:
			break;
		}

	}

	/**
	 * 保存快捷短语
	 */
	private void saveShortCut()
	{
		// 先进行判断用户是否输入了数据
		final String shortCut = mailEtShortupAdd.getText().toString();
		if (!Utils.isEmpty(shortCut.trim()))
		{
			if (shortCut.length() >= Constants.ADD_SHORT_CUT_LENGTH)
			{
				ToastUtil.toast(MessageAddShortCutActivity.this, R.string.message_short_cut_words_limit);
			}
			else
			{
				// 根据时间戳，将相关数据保存到数据库，保存前，判断输入的内容是否已经保存过
				mailShortCuts.clear();
				mailShortCuts = DataManager.getInstance().selectMessageShortCutTable(null, null);
				if (mailShortCuts != null && mailShortCuts.size() < 15)
				{
					for (int i = 0; i < mailShortCuts.size(); i++)
					{
						if (!Utils.isEmpty(mailShortCuts.get(i).messageShortCutContent.toString().trim())
								&& mailShortCuts.get(i).messageShortCutContent.toString().trim()
										.equals(shortCut.trim()))
						{
							shortcutIsContains = true;
							ToastUtil.toast(MessageAddShortCutActivity.this, R.string.message_short_cut_exist);
						}
					}
					// 判断是否包含，然后执行数据库操作
					if (!shortcutIsContains)
					{

						MessageShortCut messageShortCut = new MessageShortCut();
						messageShortCut.messageShortCutContent = shortCut;
						messageShortCut.messageShortCutSelected = "false";
						messageShortCut.messageShortCutID = (System.currentTimeMillis() + "").trim();
						// 进行数据库插入,ID通过时间戳来标识
						long uid = DataManager.getInstance().insertInToMessageShortCutTable(
								DBHelper.MESSAGE_SHORT_CUT_TABLE, messageShortCut);

						if (uid != -1)
						{
							ToastUtil.toast(MessageAddShortCutActivity.this, R.string.message_short_cut_save_success);
							finish();
						}
						else
						{
							ToastUtil.toast(MessageAddShortCutActivity.this, R.string.message_short_cut_save_failed);
						}
					}
					else
					{
						shortcutIsContains = false;
					}
				}
				else
				{
					ToastUtil.toast(MessageAddShortCutActivity.this, R.string.message_short_cut_num_limit);
				}
			}
		}
	}

	/**
	 * 监听字符串是否超过指定长度
	 */
	private TextWatcher watcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			if (s.length() >= Constants.ADD_SHORT_CUT_LENGTH)
			{
				ToastUtil.toast(MessageAddShortCutActivity.this, R.string.message_short_cut_words_limit);
			}
			else if (s.length() == 0)
			{
				btnTextFunction.setTextColor(getResources().getColor(R.color.color_005799));
				btnTextFunction.setClickable(false);
			}
			else
			{
				btnTextFunction.setTextColor(getResources().getColor(R.color.color_ffffff));
				btnTextFunction.setClickable(true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void afterTextChanged(Editable s)
		{
		}
	};

	public void back()
	{
		quitDetection();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			// return true;//返回真表示返回键被屏蔽掉
			quitDetection();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 退出检测
	 */
	private void quitDetection()
	{
		if (mailEtShortupAdd != null)
		{
			if (mailEtShortupAdd.getText().toString().trim().length() > 0)
			{
				commonDialog = new CommonDialog(this);
				commonDialog.setCancelBtnText(getString(R.string.cancel))
						.setConfirmBtnText(getString(R.string.confirm))
						.setConfirmDialogListener(new DialogClickListener()
						{
							@Override
							public void onDialogClick()
							{
								finish();
							}
						}).buildSimpleDialog(getString(R.string.exit_dialog_msg));
			}
			else
			{
				finish();
			}
		}
	}
}
