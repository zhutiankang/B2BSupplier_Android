package com.micen.suppliers.widget.product;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class NotifyWebView extends WebView
{

	PointF downP = new PointF();
	/** 触摸时当前的点 **/
	PointF curP = new PointF();

	public interface OnScrollChangedListener
	{
		void onScrollChanged(NotifyWebView who, int l, int t, int oldl, int oldt);
	}

	private OnScrollChangedListener mOnScrollChangedListener;

	public NotifyWebView(Context context)
	{
		super(context);
	}

	public NotifyWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public NotifyWebView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		if (mOnScrollChangedListener != null)
		{
			mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	public void setOnScrollChangedListener(OnScrollChangedListener listener)
	{
		mOnScrollChangedListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub

		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			// webview被点击到，即可滑动
			curP.x = event.getX();
			curP.y = event.getY();
			// 通知父控件现在进行的是本控件的操作，不要对我的操作进行干扰
			getParent().requestDisallowInterceptTouchEvent(true);
			break;

		case MotionEvent.ACTION_MOVE:
			float lastY = event.getY(event.getPointerCount() - 1);
			float lastX = event.getX(event.getPointerCount() - 1);

			if (Math.abs(lastX - curP.x) < Math.abs(lastY - curP.y))
			{
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}

		return super.onTouchEvent(event);
	}

}
