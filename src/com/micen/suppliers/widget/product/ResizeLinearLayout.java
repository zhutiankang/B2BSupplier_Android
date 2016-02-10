package com.micen.suppliers.widget.product;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLinearLayout extends LinearLayout
{
	private OnResizeListener mListener;

	public interface OnResizeListener
	{
		void OnResize(int w, int h, int oldw, int oldh);
	}

	public void setOnResizeListener(OnResizeListener l)
	{
		mListener = l;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		if (mListener != null)
		{
			mListener.OnResize(w, h, oldw, oldh);
		}
	}

	public ResizeLinearLayout(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ResizeLinearLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ResizeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

}
