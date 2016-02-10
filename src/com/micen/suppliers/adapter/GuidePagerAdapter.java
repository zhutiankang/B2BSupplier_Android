package com.micen.suppliers.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.micen.suppliers.R;


public class GuidePagerAdapter extends PagerAdapter
{
	public int[] res = new int[]
	{ R.drawable.ic_guide01, R.drawable.ic_guide02, R.drawable.ic_guide03 };
	private Context context;

	public GuidePagerAdapter(Context context)
	{
		super();
		this.context = context;
	}

	@Override
	public int getCount()
	{
		return res.length;
	}

	/**
	 * Create the page for the given position. The adapter is responsible for
	 * adding the view to the container given here, although it only must ensure
	 * this is done by the time it returns from {@link #finishUpdate()}.
	 * 
	 * @param container
	 *            The containing View in which the page will be shown.
	 * @param position
	 *            The page position to be instantiated.
	 * @return Returns an Object representing the new page. This does not need
	 *         to be a View, but can be some other container of the page.
	 */
	@Override
	public Object instantiateItem(View collection, int position)
	{
		ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.guide_item, null);
		view.setBackgroundResource(res[position]);
		View btStart = view.findViewById(R.id.btn_guide_start);
		btStart.setOnClickListener(btStartListener);
		if (position == 2)
		{
			btStart.setVisibility(View.VISIBLE);
		}
		else
		{
			btStart.setVisibility(View.GONE);
		}
		((ViewPager) collection).addView(view, 0);
		return view;
	}

	private OnClickListener btStartListener;

	public void setBtStartListener(OnClickListener btStartListener)
	{
		this.btStartListener = btStartListener;
	}

	/**
	 * Remove a page for the given position. The adapter is responsible for
	 * removing the view from its container, although it only must ensure this
	 * is done by the time it returns from {@link #finishUpdate()}.
	 * 
	 * @param container
	 *            The containing View from which the page will be removed.
	 * @param position
	 *            The page position to be removed.
	 * @param object
	 *            The same object that was returned by
	 *            {@link #instantiateItem(View, int)}.
	 */
	@Override
	public void destroyItem(View collection, int position, Object view)
	{
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == ((View) object);
	}

	/**
	 * Called when the a change in the shown pages has been completed. At this
	 * point you must ensure that all of the pages have actually been added or
	 * removed from the container as appropriate.
	 * 
	 * @param container
	 *            The containing View which is displaying this adapter's page
	 *            views.
	 */
	@Override
	public void finishUpdate(View arg0)
	{
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1)
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0)
	{
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}
}
