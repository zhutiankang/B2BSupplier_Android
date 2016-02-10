package com.micen.suppliers.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.micen.suppliers.R;


public class CountryImageView extends ImageView
{

	private boolean isNeedStroke = false;

	public CountryImageView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CountryImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CountryImageView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (isNeedStroke)
		{
			Rect rec = canvas.getClipBounds();
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.color_393d40)); // 颜色
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1.0f);
			paint.setAntiAlias(true);
			canvas.drawRect(rec, paint);
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm)
	{
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		isNeedStroke = true;
		invalidate();
	}
}
