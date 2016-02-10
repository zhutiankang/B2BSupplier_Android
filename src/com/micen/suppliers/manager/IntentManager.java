package com.micen.suppliers.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class IntentManager
{
	/**
	 * 调用系统方法拨号
	 * @param activity
	 * @param number
	 */
	public static void makeCall(Context context, String number)
	{
		try
		{
			// 用intent启动拨打电话
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
			context.startActivity(intent);
		}
		catch (ActivityNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 调用系统方法发送邮件
	 * @param activity
	 * @param email
	 */
	public static void sendMail(Context context, String email)
	{
		try
		{
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:" + email));
			context.startActivity(intent);
		}
		catch (ActivityNotFoundException e)
		{
			e.printStackTrace();
		}
	}

}
