package com.micen.suppliers.activity.qrcode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;

import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseFragmentActivity;
import com.micen.suppliers.view.qrcode.QRCodeResultFragment;
import com.micen.suppliers.view.qrcode.QRCodeSignInFragment;


public class QRCodeResultActivity extends BaseFragmentActivity
{

	private String fragmentTag = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase);
		initNavigationBarStyle(true);

		final FragmentManager fm = this.getSupportFragmentManager();

		Fragment mFragment = fm.findFragmentById(R.id.id_fragment_container);

		if (null == mFragment)
		{
			fragmentTag = this.getIntent().getStringExtra("fragment");
			mFragment = getFragment(fragmentTag);
			fm.beginTransaction().add(R.id.id_fragment_container, mFragment).commitAllowingStateLoss();
		}
	}

	private Fragment getFragment(String fragmentTag)
	{
		if ("SignIn".equals(fragmentTag))
		{
			return QRCodeSignInFragment.newInstance(getIntent().getStringExtra("activityId"), getIntent()
					.getStringExtra("topicName"));
		}
		return QRCodeResultFragment.newInstance(getIntent().getStringExtra("qrCodeContent"));

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		setResult(RESULT_OK);
		finish();
		return true;
	}

}
