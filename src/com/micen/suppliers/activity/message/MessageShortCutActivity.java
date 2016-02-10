package com.micen.suppliers.activity.message;

import java.util.ArrayList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.adapter.message.MessageShortCutListAdapter;
import com.micen.suppliers.db.DBHelper;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.manager.DataManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.db.MessageShortCut;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.view.PageStatusView;
import com.micen.suppliers.view.PageStatusView.PageStatus;
import com.umeng.analytics.MobclickAgent;



@EActivity
public class MessageShortCutActivity extends BaseActivity implements OnItemLongClickListener
{
	@ViewById(R.id.message_short_cut_lv)
	protected ListView messageShortCutLv;
	@ViewById(R.id.rl_common_title)
	protected RelativeLayout rlCommonTitle;
	@ViewById(R.id.broadcast_page_status)
	protected PageStatusView statusView;

	private Boolean isInsertDB = false;

	private ArrayList<MessageShortCut> mailShortCuts;

	protected CommonDialog dialog;

	private MessageShortCutListAdapter adapter;
	private int delectPos;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_shortcut);
		initNavigationBarStyle(false);

	}

	@AfterViews
	protected void initView()
	{
		MobclickAgent.openActivityDurationTrack(false);
		initTitleBar();
		messageShortCutLv.setOnItemLongClickListener(this);
		initDB();
		selectDBUpdataLV();
		messageShortCutLv.setOnItemClickListener(onItemClickListener);

	}

	private void initTitleBar()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(this);
		btnFunction5.setVisibility(View.VISIBLE);
		btnFunction5.setImageResource(R.drawable.ic_title_add);
		btnFunction5.setOnClickListener(this);
		tvTitle.setText(R.string.message_short_cut);
		tvTitle.setTextColor(getResources().getColor(R.color.color_ffffff));
	}

	/**
	 * 数据库初始化操作
	 */
	private void initDB()
	{
		// 获取sp的值，判断是否插入过相关模板
		isInsertDB = SharedPreferenceManager.getInstance().getBoolean(MessageConstantDefine.isInsertDB.toString(),
				false);
		if (!isInsertDB)
		{
			MessageShortCut mailShortCut = new MessageShortCut();
			mailShortCut.messageShortCutContent = getString(R.string.message_short_cut_default_First);
			mailShortCut.messageShortCutSelected = "false";
			mailShortCut.messageShortCutID = "1434014044000";
			// 进行数据库插入操作
			DataManager.getInstance().insertInToMessageShortCutTable(DBHelper.MESSAGE_SHORT_CUT_TABLE, mailShortCut);
			mailShortCut = new MessageShortCut();
			mailShortCut.messageShortCutContent = getString(R.string.message_short_cut_default_Second);
			mailShortCut.messageShortCutSelected = "false";
			mailShortCut.messageShortCutID = "1434014044001";
			DataManager.getInstance().insertInToMessageShortCutTable(DBHelper.MESSAGE_SHORT_CUT_TABLE, mailShortCut);
			mailShortCut = new MessageShortCut();
			mailShortCut.messageShortCutContent = getString(R.string.message_short_cut_default_Third);
			mailShortCut.messageShortCutSelected = "false";
			mailShortCut.messageShortCutID = "1434014044002";
			DataManager.getInstance().insertInToMessageShortCutTable(DBHelper.MESSAGE_SHORT_CUT_TABLE, mailShortCut);

			SharedPreferenceManager.getInstance().putBoolean(MessageConstantDefine.isInsertDB.toString(), true);
		}

	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			MobclickAgent.onEvent(MessageShortCutActivity.this, "37");
			SysManager.analysis(R.string.c_type_click, R.string.c037);
			finish();
			break;
		case R.id.common_title_function5:
			// 跳转到快捷短语添加页
			startActivity(new Intent(MessageShortCutActivity.this, MessageAddShortCutActivity_.class));
			MobclickAgent.onEvent(MessageShortCutActivity.this, "40");
			SysManager.analysis(R.string.c_type_click, R.string.c040);
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume()
	{
		selectDBUpdataLV();
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(getString(R.string.p10005));
		SysManager.analysis(R.string.p_type_page, R.string.p10005);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(getString(R.string.p10005));
		MobclickAgent.onPause(this);
	}

	@Override
	public void onBackPressed()
	{
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	/**
	 * 查询数据库中的模板信息
	 */
	private void selectDBUpdataLV()
	{
		// 查询数据库
		mailShortCuts = DataManager.getInstance().selectMessageShortCutTable(null, null);
		if (mailShortCuts != null && mailShortCuts.size() != 0)
		{
			statusView.setVisibility(View.GONE);
			messageShortCutLv.setVisibility(View.VISIBLE);
			adapter = new MessageShortCutListAdapter(mailShortCuts, MessageShortCutActivity.this);
			messageShortCutLv.setAdapter(adapter);
			// 模板条数限制：超过15条，给出提示
			if (adapter.getCount() >= 15)
			{
				btnFunction5.setVisibility(View.GONE);
			}
			else
			{
				btnFunction5.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			messageShortCutLv.setVisibility(View.GONE);
			statusView.setVisibility(View.VISIBLE);
			statusView.setMode(PageStatus.PageEmpty);
		}
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos, long id)
		{
			MobclickAgent.onEvent(MessageShortCutActivity.this, "39");
			SysManager.analysis(R.string.c_type_click, R.string.c039);
			// 界面跳转，并将对应的内容传递给上一个界面
			setResult(RESULT_OK, new Intent().putExtra(MessageConstantDefine.shortCutKey.toString(),
					mailShortCuts.get(pos).messageShortCutContent));
			finish();
		}
	};

	@Override
	public boolean onItemLongClick(AdapterView<?> adaptview, View v, int position, long id)
	{
		MobclickAgent.onEvent(MessageShortCutActivity.this, "43");
		SysManager.analysis(R.string.c_type_click, R.string.c043);
		delectPos = position;
		if (dialog != null)
		{
			dialog.show();
			return true;
		}
		dialog = new CommonDialog(this);
		dialog.setCancelBtnText(getString(R.string.cancel)).setConfirmBtnText(getString(R.string.confirm))
				.setConfirmDialogListener(dialogClickListener)
				.buildSimpleDialog(getString(R.string.message_short_cut_delect));
		return true;
	}

	DialogClickListener dialogClickListener = new DialogClickListener()
	{
		@Override
		public void onDialogClick()
		{
			MobclickAgent.onEvent(MessageShortCutActivity.this, "44");
			SysManager.analysis(R.string.c_type_click, R.string.c044);
			// 数据库中删除成功后，进行界面刷新
			int delectNum = DataManager.getInstance().deleteMailShortCut(DBHelper.MESSAGE_SHORT_CUT_TABLE,
					DBHelper.MESSAGE_SHORT_CUT_ID, mailShortCuts.get(delectPos).messageShortCutID);
			if (delectNum != 0)
			{
				mailShortCuts.remove(delectPos);
				adapter.notifyDataSetChanged();
				btnFunction5.setVisibility(View.VISIBLE);
				if (mailShortCuts.size() == 0)
				{
					statusView.setVisibility(View.VISIBLE);
					statusView.setMode(PageStatus.PageEmpty);
					messageShortCutLv.setVisibility(View.GONE);
				}
			}
		}
	};
}
