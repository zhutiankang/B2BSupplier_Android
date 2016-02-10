package com.micen.suppliers.adapter.message;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


public class CommonFragmentAdapter extends FragmentStatePagerAdapter
{
	private ArrayList<Fragment> fragmentList;

	public CommonFragmentAdapter(Activity activity, FragmentManager fm, ArrayList<Fragment> fragmentList)
	{
		super(fm);
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int arg0)
	{
		return fragmentList.get(arg0);
	}

	@Override
	public int getCount()
	{
		return null == fragmentList ? 0 : fragmentList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		super.destroyItem(container, position, object);
	}
}
