package com.micen.suppliers.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.micen.suppliers.R;


public class SearchListProgressBar extends RelativeLayout
{
	private RelativeLayout viewGroup;

	public SearchListProgressBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	public SearchListProgressBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public SearchListProgressBar(Context context)
	{
		super(context);
		initView(context);
	}

	private void initView(Context context)
	{
		viewGroup = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.data_loading_layout, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		addView(viewGroup, params);
	}
}
