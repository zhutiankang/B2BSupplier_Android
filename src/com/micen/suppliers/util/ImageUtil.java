package com.micen.suppliers.util;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.focustech.common.http.FocusClient;
import com.focustech.common.universalimageloader.core.DisplayImageOptions;
import com.focustech.common.universalimageloader.core.ImageLoader;
import com.micen.suppliers.R;


public class ImageUtil
{
	private static ImageLoader imageLoader;

	static
	{
		imageLoader = ImageLoader.getInstance();
	}

	private ImageUtil()
	{

	}

	public static ImageLoader getImageLoader()
	{
		return imageLoader;
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static DisplayImageOptions getImageOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.white)
		// .showImageForEmptyUri(R.drawable.magazine_load_image_hd)
		// .showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	public static DisplayImageOptions getSimpleImageOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher)
				.cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.ALPHA_8).build();
		return options;
	}

	public static DisplayImageOptions getInquiryImageLargerStubOptions(Context context)
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().setRequestPropertyKeys(new String[]
		{ "Cookie" }).setRequestPropertyValues(new String[]
		{ FocusClient.getCookieText(context) }).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 *@param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ALPHA_8;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inSampleSize = 2;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	* Utility function for decoding an image resource. The decoded bitmap will
	* be optimized for further scaling to the requested destination dimensions
	* and scaling logic.
	*
	* @param res The resources object containing the image data
	* @param resId The resource id of the image data
	* @param dstWidth Width of destination area
	* @param dstHeight Height of destination area
	* @param scalingLogic Logic to use to avoid image stretching
	* @return Decoded bitmap
	*/
	public static Bitmap decodeResource(Resources res, int resId, int dstWidth, int dstHeight, ScalingLogic scalingLogic)
	{
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inJustDecodeBounds = false;
		options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight,
				scalingLogic);
		Bitmap unscaledBitmap = BitmapFactory.decodeResource(res, resId, options);

		return unscaledBitmap;
	}

	/**
	 * Utility function for creating a scaled version of an existing bitmap
	 *
	 * @param unscaledBitmap Bitmap to scale
	 * @param dstWidth Wanted width of destination bitmap
	 * @param dstHeight Wanted height of destination bitmap
	 * @param scalingLogic Logic to use to avoid image stretching
	 * @return New scaled bitmap object
	 */
	public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
			ScalingLogic scalingLogic)
	{
		Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight,
				scalingLogic);
		Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight,
				scalingLogic);
		Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
		Canvas canvas = new Canvas(scaledBitmap);
		canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}

	/**
	 * ScalingLogic defines how scaling should be carried out if source and
	 * destination image has different aspect ratio.
	 *
	 * CROP: Scales the image the minimum amount while making sure that at least
	 * one of the two dimensions fit inside the requested destination area.
	 * Parts of the source image will be cropped to realize this.
	 *
	 * FIT: Scales the image the minimum amount while making sure both
	 * dimensions fit inside the requested destination area. The resulting
	 * destination dimensions might be adjusted to a smaller size than
	 * requested.
	 */
	public static enum ScalingLogic
	{
		CROP, FIT
	}

	/**
	 * Calculate optimal down-sampling factor given the dimensions of a source
	 * image, the dimensions of a destination area and a scaling logic.
	 *
	 * @param srcWidth Width of source image
	 * @param srcHeight Height of source image
	 * @param dstWidth Width of destination area
	 * @param dstHeight Height of destination area
	 * @param scalingLogic Logic to use to avoid image stretching
	 * @return Optimal down scaling sample size for decoding
	 */
	public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
			ScalingLogic scalingLogic)
	{
		if (scalingLogic == ScalingLogic.FIT)
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				return srcWidth / dstWidth;
			}
			else
			{
				return srcHeight / dstHeight;
			}
		}
		else
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				return srcHeight / dstHeight;
			}
			else
			{
				return srcWidth / dstWidth;
			}
		}
	}

	/**
	 * Calculates source rectangle for scaling bitmap
	 *
	 * @param srcWidth Width of source image
	 * @param srcHeight Height of source image
	 * @param dstWidth Width of destination area
	 * @param dstHeight Height of destination area
	 * @param scalingLogic Logic to use to avoid image stretching
	 * @return Optimal source rectangle
	 */
	public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
			ScalingLogic scalingLogic)
	{
		if (scalingLogic == ScalingLogic.CROP)
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				final int srcRectWidth = (int) (srcHeight * dstAspect);
				final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
				return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
			}
			else
			{
				final int srcRectHeight = (int) (srcWidth / dstAspect);
				final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
				return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
			}
		}
		else
		{
			return new Rect(0, 0, srcWidth, srcHeight);
		}
	}

	/**
	 * Calculates destination rectangle for scaling bitmap
	 *
	 * @param srcWidth Width of source image
	 * @param srcHeight Height of source image
	 * @param dstWidth Width of destination area
	 * @param dstHeight Height of destination area
	 * @param scalingLogic Logic to use to avoid image stretching
	 * @return Optimal destination rectangle
	 */
	public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
			ScalingLogic scalingLogic)
	{
		if (scalingLogic == ScalingLogic.FIT)
		{
			final float srcAspect = (float) srcWidth / (float) srcHeight;
			final float dstAspect = (float) dstWidth / (float) dstHeight;

			if (srcAspect > dstAspect)
			{
				return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
			}
			else
			{
				return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
			}
		}
		else
		{
			return new Rect(0, 0, dstWidth, dstHeight);
		}
	}

	public static DisplayImageOptions getSafeImageLargerStubOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().setRequestPropertyKeys(new String[]
		{ "Referer" }).setRequestPropertyValues(new String[]
		{ " http://www.made-in-jiangsu.com" }).showImageOnLoading(R.drawable.bg_mic_recommend_product_loading_large)
				.cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	public static DisplayImageOptions getSafeImageOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().setRequestPropertyKeys(new String[]
		{ "Referer" }).setRequestPropertyValues(new String[]
		{ " http://www.made-in-jiangsu.com" }).showImageOnLoading(R.drawable.bg_message_attachment_loading)
				.cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	public static DisplayImageOptions getRecommendImageOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.bg_message_attachment_loading).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	public static DisplayImageOptions getCommonImageOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;
	}

	public static DisplayImageOptions getMessageProductImageOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).showImageOnLoading(R.drawable.bg_message_product_onloading)
				.showImageOnFail(R.drawable.bg_message_product_default).build();
		return options;
	}

	public static DisplayImageOptions getProductImageOptions()
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnLoading(R.drawable.bg_mic_recommend_product_loading_large).build();
		return options;
	}

	/** 
	 * 转换图片成圆形 
	 *  
	 * @param bitmap 
	 *            传入Bitmap对象 
	 * @return 
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height)
		{
			roundPx = width / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		}
		else
		{
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
		paint.setColor(color);

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}

	/** 
	 * 转换图片成圆形 (附带描边)
	 *  
	 * @param bitmap 
	 *            传入Bitmap对象 
	 * @return 
	 */
	public static Bitmap toRoundBitmapWithStrok(Bitmap bitmap, int strokColor)
	{
		int canvasWidth = 120;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;

		if (width <= height)
		{
			roundPx = canvasWidth / 2;
			left = 0;
			top = 0;
			right = width;
			bottom = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = canvasWidth;
			dst_bottom = canvasWidth;
		}
		else
		{
			roundPx = canvasWidth / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = canvasWidth;
			dst_bottom = canvasWidth;
		}

		Bitmap output = Bitmap.createBitmap(canvasWidth, canvasWidth, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
		paint.setColor(color);

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		/** 
		 * 画最外层的大圆环 
		 */
		paint = new Paint();
		paint.setColor(strokColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(0.5f); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawCircle(roundPx, roundPx, roundPx - 0.5f, paint); // 画出圆环

		return output;
	}
}
