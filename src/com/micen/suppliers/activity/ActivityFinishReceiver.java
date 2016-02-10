package com.micen.suppliers.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;


public class ActivityFinishReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (context instanceof Activity)
		{
			((Activity) context).finish();
		}
		else if (context instanceof FragmentActivity)
		{
			((FragmentActivity) context).finish();
		}
	}

}
