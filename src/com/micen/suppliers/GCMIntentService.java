package com.micen.suppliers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import com.google.android.gcm.GCMBaseIntentService;
import com.micen.suppliers.util.Util;

@SuppressLint("Instantiatable")
public class GCMIntentService extends GCMBaseIntentService
{
	@SuppressLint("Instantiatable")
	protected GCMIntentService(String senderId)
	{
		super(Util.getGCMProjectNumber());
	}

	@Override
	protected void onError(Context arg0, String arg1)
	{

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1)
	{

	}

	@Override
	protected void onRegistered(Context arg0, String arg1)
	{

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1)
	{

	}

}
