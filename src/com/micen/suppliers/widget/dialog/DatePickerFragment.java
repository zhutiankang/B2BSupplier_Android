package com.micen.suppliers.widget.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
	private String title;

	public static DatePickerFragment newInstance(String t)
	{
		DatePickerFragment dialog = new DatePickerFragment();

		Bundle bundle = new Bundle();
		bundle.putString("title", t);
		dialog.setArguments(bundle);
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (null != bundle)
		{
			title = bundle.getString("title");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);

		// 设置最小时间
		DatePicker picker = dialog.getDatePicker();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 7);

		picker.setMinDate(calendar.getTimeInMillis());

		if (!Utils.isEmpty(title))
		{
			dialog.setTitle(title);
		}
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (OnClickListener) dialog);

		// dialog.setButton(getString(R.string.ok), null);

		// 设置最小时间

		return dialog;
	}

	public void onDateSet(DatePicker view, int year, int month, int day)
	{
		// Do something with the date chosen by the user
		// 判断是否设置了targetFragment
		if (getTargetFragment() == null)
			return;

		month = month + 1;
		String strdate = String.format("%04d-%02d-%02d", year, month, day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date date = new Date();
		try
		{
			date = sdf.parse(strdate);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent intent = new Intent();
		intent.putExtra("name", strdate);
		intent.putExtra("value", Long.toString(date.getTime()));

		// Long time = year-1790)
		// intent.putExtra("value", view.get)
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
	}

}
