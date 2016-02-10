package com.micen.suppliers.manager;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.application.SupplierApplication;

public class PopupManager
{
	private static PopupManager popupView = null;
	private PopupWindow mPopupWin = null;

	private List<String> mList;
	private String[] mPopupWindowList;

	private PopupManager()
	{

	}

	public static PopupManager getInstance()
	{
		if (popupView == null)
		{
			popupView = new PopupManager();
		}
		return popupView;
	}

	/**
	 * 弹出常用菜单或系统菜单
	 * @param context
	 * @param contentView
	 * @param targetView
	 * @param isOftenMenu	是否弹出常用菜单
	 */
	public void showCommonTitlePopup(Context context, View contentView, View targetView, boolean isOftenMenu)
	{
		if (targetView == null)
			return;
		mPopupWin = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.setWidth(Utils.toDip(context, isOftenMenu ? 184 : 180));
		mPopupWin.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWin.showAsDropDown(targetView, isOftenMenu ? -Utils.toDip(context, 48) : 0, 0);
		mPopupWin.update();
		contentView.setFocusableInTouchMode(true);
		contentView.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (mPopupWin.isShowing()))
				{
					mPopupWin.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	public void showSharePopupWindowAtBottom(Context context, View contentView, View parent)
	{
		mPopupWin = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		mPopupWin.update();
		contentView.setFocusableInTouchMode(true);
		contentView.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (mPopupWin.isShowing()))
				{
					mPopupWin.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 弹出常用菜单或系统菜单
	 * @param context
	 * @param contentView
	 * @param targetView
	 * @param isOftenMenu	是否弹出常用菜单
	 */
	public void showSharePopup(Context context, View contentView, View targetView)
	{
		if (targetView == null)
			return;
		mPopupWin = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.setWidth(Utils.toDip(context, 184));
		mPopupWin.setHeight(Utils.toDip(context, 134));
		mPopupWin.showAsDropDown(targetView, 0, 0);
		mPopupWin.update();
		contentView.setFocusableInTouchMode(true);
		contentView.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (mPopupWin.isShowing()))
				{
					mPopupWin.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 搜索结果列表页弹出系统菜单
	 * @param context
	 * @param contentView
	 * @param targetView
	 */
	public void showSearchListSysPopup(Context context, View contentView, View targetView)
	{
		if (targetView == null)
			return;
		mPopupWin = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.setWidth(Utils.toDip(context, 131));
		mPopupWin.setHeight(Utils.toDip(context, 200));
		mPopupWin.showAsDropDown(targetView, -Utils.toDip(context, 85), 0);
	}

	/**
	 * 买家版邮件弹出部分
	 * @param context
	 * @param contentView
	 * @param targetView
	 * @param width
	 * @param height
	 * @return
	 */
	public PopupWindow showSelectTypePopupDown(Context context, View contentView, View targetView, int width,
			int height, int xoff, int yoff)
	{
		if (targetView == null)
			return null;
		mPopupWin = new PopupWindow(contentView, width, height);
		mPopupWin.setTouchable(true);
		// 设置popWindow里面的控件可以获取焦点,比如点击popWindow里面的EditText时会弹出键盘
		mPopupWin.setFocusable(true);
		// 必须设置背景，否则popWindow弹出后屏幕或者back键获取不到点击事件，不知道为什么
		mPopupWin.setBackgroundDrawable(new BitmapDrawable());
		// popWindow获取popWindow以外区域的点击事件后消失
		mPopupWin.setOutsideTouchable(true);
		mPopupWin.showAsDropDown(targetView, xoff, yoff);
		return mPopupWin;
	}

	/**
	 * 分配PopupWindow
	 * @param context
	 * @param contentView
	 * @param targetView
	 * @param width
	 * @param height
	 * @return
	 */
	public PopupWindow showAllocationPopUp(Context context, View contentView, View targetView, int width, int height,
			int gravity, int xoff, int yoff)
	{
		if (targetView == null)
			return null;
		mPopupWin = new PopupWindow(contentView, width, height);
		//
		mPopupWin.setTouchable(true);
		// 设置popWindow里面的控件可以获取焦点,比如点击popWindow里面的EditText时会弹出键盘
		mPopupWin.setFocusable(true);
		// 必须设置背景，否则popWindow弹出后屏幕或者back键获取不到点击事件，不知道为什么
		mPopupWin.setBackgroundDrawable(new BitmapDrawable());
		// popWindow获取popWindow以外区域的点击事件后消失
		mPopupWin.setOutsideTouchable(true);
		mPopupWin.showAtLocation(targetView, gravity, xoff, yoff);
		return mPopupWin;
	}

	/**
	 * 弹出图片获取方式
	 * @param context
	 * @param captureView
	 */
	public void showCapturePopup(Context context, View captureView)
	{
		if (captureView == null)
			return;
		mPopupWin = new PopupWindow(captureView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.setWidth(Utils.toDip(context, 200) + Utils.toDip(context, 20));
		mPopupWin.setHeight(Utils.toDip(context, 48 * 2 + 2) + Utils.toDip(context, 20));
		mPopupWin.showAtLocation(captureView, Gravity.CENTER, 0, 0);
	}

	/**
	 * RFQ页面弹出菜单
	 * @param context
	 * @param contentView
	 * @param targetView
	 * @param width
	 * @param height
	 * @param x
	 * @param y
	 */
	public void showCapturePopup(View contentView)
	{
		mPopupWin = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(SupplierApplication.getInstance().getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWin.setHeight(LayoutParams.WRAP_CONTENT);
		mPopupWin.showAtLocation(contentView, Gravity.CENTER, 0, 0);
	}

	/**
	 * 发送询盘推荐内容弹出窗口
	 * @param contentView
	 * @param height
	 */
	public void showInquiryRecommendPopup(View contentView)
	{
		mPopupWin = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(SupplierApplication.getInstance().getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWin.setHeight(LayoutParams.MATCH_PARENT);
		mPopupWin.showAtLocation(contentView, Gravity.CENTER, 0, 0);
	}

	/**
	 * 弹出姓别提示框
	 */
	public void showGenderRecommendPopup(View contentView)
	{
		mPopupWin = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(SupplierApplication.getInstance().getResources()));
		mPopupWin.setTouchable(true);
		mPopupWin.setFocusable(true);
		mPopupWin.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWin.setHeight(LayoutParams.MATCH_PARENT);
		mPopupWin.showAtLocation(contentView, Gravity.CENTER, 0, 0);
	}

	public void dismissPopup()
	{
		if (mPopupWin != null)
		{
			mPopupWin.dismiss();
		}
	}

	public boolean isShowing()
	{
		if (mPopupWin != null)
			return mPopupWin.isShowing();
		else return false;
	}

	private void addPopWindowList()
	{
		mList = new LinkedList<String>();
		for (int i = 0; i < mPopupWindowList.length; i++)
		{
			mList.add(mPopupWindowList[i]);
		}
	}

	/**
	 * 显示称呼弹出窗口
	 * @param v
	 * @param view
	 * @param context
	 * @param text
	 */
	public void showGenderPopupWindow(View v, View view, Activity context, final TextView text)
	{
		mPopupWin = new PopupWindow(v, 300, LayoutParams.WRAP_CONTENT);
		mPopupWin.setFocusable(true);
		mPopupWin.setOutsideTouchable(true);
		mPopupWin.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		mPopupWin.showAsDropDown(view, Utils.getWindowWidthPix(context) / 2 - mPopupWin.getWidth() / 2, 0);
	}

}
