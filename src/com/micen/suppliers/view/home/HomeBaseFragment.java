package com.micen.suppliers.view.home;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.micen.suppliers.R;
import com.micen.suppliers.db.SharedPreferenceManager;


@EFragment
public abstract class HomeBaseFragment extends Fragment implements OnClickListener
{
	public HomeBaseFragment()
	{

	}

	@AfterViews
	protected abstract void initView();

	protected abstract String getFragmentTag();

	@Override
	public void onClick(View v)
	{

	}
}
