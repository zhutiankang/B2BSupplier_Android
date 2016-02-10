package com.micen.suppliers.manager;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.micen.suppliers.activity.home.HomeActivity;

import android.app.Activity;


public class ActivityManager
{
	private static ActivityManager manager = null;
	private static HashMap<String, SoftReference<Activity>> activityMap;
	static
	{
		manager = new ActivityManager();
		activityMap = new HashMap<String, SoftReference<Activity>>();
	}

	private ActivityManager()
	{

	}

	public static ActivityManager getInstance()
	{
		return manager;
	}

	public void put(Activity act)
	{
		activityMap.put(act.toString(), new SoftReference<Activity>(act));
	}

	public void remove(Activity act)
	{
		activityMap.remove(act.toString());
	}

	public boolean isHomeActivityAvailable()
	{
		boolean result = false;
		Set<String> set = activityMap.keySet();
		Iterator<String> iter = set.iterator();
		while (iter.hasNext())
		{
			String actName = iter.next();
			Activity currentAct = activityMap.get(actName).get();
			if (currentAct instanceof HomeActivity && !currentAct.isFinishing())
			{
				result = true;
				break;
			}
		}
		return result;
	}

	public void finishAllActivity()
	{
		Set<String> set = activityMap.keySet();
		Iterator<String> iter = set.iterator();
		while (iter.hasNext())
		{
			String actName = iter.next();
			Activity currentAct = activityMap.get(actName).get();
			if (currentAct != null)
			{
				currentAct.finish();
				currentAct = null;
			}
		}
		activityMap.clear();
		activityMap = null;
	}
}
