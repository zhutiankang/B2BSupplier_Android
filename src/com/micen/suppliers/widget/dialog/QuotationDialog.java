package com.micen.suppliers.widget.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.adapter.purchase.PurchaseQuotationChooseAdapter;


public class QuotationDialog extends DialogFragment
{

	private String title;
	private String hasChoose;
	private boolean isSingleChoose;
	private PurchaseQuotationChooseAdapter adapter;

	private TextView tvTitle;
	private ListView lvListView;

	private Button btCancle;
	private Button btOK;

	private String[] array;
	private String[] array_value;
	private String[] array_hasChoose;

	private int choosePositon;

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			// TODO Auto-generated method stub
			if (isSingleChoose)
			{
				adapter.setPosition(position);
			}
			else
			{
				adapter.addString(array[position]);
				adapter.addStringValue(array_value[position]);
			}
		}

	};

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.common_dialog_btn_cancel:
				dismiss();
				break;
			case R.id.common_dialog_btn_confirm:
				if (isSingleChoose)
				{
					setSingleResult();
				}
				else
				{
					setMulResult();
				}
				dismiss();
				break;
			default:
				break;
			}
		}
	};

	public static QuotationDialog newInstance(String title, boolean isSingleChoose, String[] array, String[] value,
			String hasChoose)
	{
		QuotationDialog dialog = new QuotationDialog();
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putBoolean("issinglechoose", isSingleChoose);
		bundle.putStringArray("data", array);
		bundle.putStringArray("value", value);
		bundle.putString("choose", hasChoose);
		dialog.setArguments(bundle);
		return dialog;
	}

	@Override
	public void onStart()
	{
		// TODO Auto-generated method stub
		super.onStart();
		if (getDialog() == null)
			return;

		WindowManager.LayoutParams dialogParams = getDialog().getWindow().getAttributes();
		dialogParams.width = Utils.toDip(getActivity(), 280);
		dialogParams.height = LayoutParams.WRAP_CONTENT;

		getDialog().getWindow().setAttributes(dialogParams);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		int style = DialogFragment.STYLE_NO_TITLE;
		int theme = android.R.style.Theme_Translucent;
		setStyle(style, theme);

		Bundle bundle = getArguments();

		if (null != bundle)
		{
			title = bundle.getString("title");
			isSingleChoose = bundle.getBoolean("issinglechoose");
			array = bundle.getStringArray("data");
			array_value = bundle.getStringArray("value");

			hasChoose = bundle.getString("choose");

			adapter = new PurchaseQuotationChooseAdapter(getActivity(), array, isSingleChoose);

			if (isSingleChoose && !Utils.isEmpty(hasChoose))
			{
				for (int i = 0; i < array_value.length; i++)
				{
					if (array_value[i].equals(hasChoose))
					{
						adapter.setPosition(i);
						break;
					}
				}
			}

			if (!isSingleChoose && !Utils.isEmpty(hasChoose))
			{
				array_hasChoose = hasChoose.split(",");
				for (int j = 0; j < array_hasChoose.length; j++)
				{
					for (int i = 0; i < array_value.length; i++)
					{
						if (array_value[i].equals(array_hasChoose[j]))
						{
							adapter.addString(array[i]);
							adapter.addStringValue(array_value[i]);
							break;
						}
					}
				}
			}

		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// builder.setAdapter(adapter, clickListener);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_purchase_dialog_choose, null);
		tvTitle = (TextView) view.findViewById(R.id.purchase_dialog_titleTextView);
		lvListView = (ListView) view.findViewById(R.id.purchase_dialog_chooseListView);

		btCancle = (Button) view.findViewById(R.id.common_dialog_btn_cancel);
		btOK = (Button) view.findViewById(R.id.common_dialog_btn_confirm);

		btCancle.setOnClickListener(clickListener);
		btOK.setOnClickListener(clickListener);

		LayoutParams params = lvListView.getLayoutParams();
		params.width = LayoutParams.MATCH_PARENT;

		if (array.length < 8)
		{
			params.height = LayoutParams.WRAP_CONTENT;
		}
		else
		{
			params.height = Utils.toDip(getActivity(), 365);
		}

		tvTitle.setText(title);
		lvListView.setAdapter(adapter);
		lvListView.setOnItemClickListener(itemClickListener);

		builder.setView(view);

		/*
		 * builder.setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		 * @Override public void onClick(DialogInterface dialog, int which) { // TODO Auto-generated method stub if
		 * (isSingleChoose) { setSingleResult(); } else { setMulResult(); } } }).setNegativeButton(R.string.cancel, new
		 * DialogInterface.OnClickListener() {
		 * @Override public void onClick(DialogInterface dialog, int id) { } });
		 */
		// builder.setSingleChoiceItems(adapter, -1, clickListener);

		return builder.create();
	}

	// 设置返回数据
	protected void setResult(int which)
	{
		// 判断是否设置了targetFragment
		if (getTargetFragment() == null)
			return;

		Intent intent = new Intent();
		intent.putExtra("name", array[which]);
		intent.putExtra("value", array_value[which]);
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

	}

	protected void setSingleResult()
	{
		choosePositon = adapter.getCurPosition();
		if (choosePositon != -1)
		{
			setResult(choosePositon);
		}
	}

	protected void setMulResult()
	{
		// 判断是否设置了targetFragment
		if (getTargetFragment() == null)
			return;
		ArrayList<String> list = adapter.getSelect();
		ArrayList<String> listvalue = adapter.getSelectValue();
		String name = "";
		String value = "";
		for (int i = 0; i < list.size(); i++)
		{
			if (i == 0)
			{
				name += list.get(i);
			}
			else
			{
				name = name + "," + list.get(i);
			}
		}

		for (int i = 0; i < listvalue.size(); i++)
		{
			if (i == 0)
			{
				value += listvalue.get(i);
			}
			else
			{
				value = value + "," + listvalue.get(i);
			}
		}

		Intent intent = new Intent();
		intent.putExtra("name", name);
		intent.putExtra("value", value);
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
	}

}
