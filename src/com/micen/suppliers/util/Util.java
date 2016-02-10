package com.micen.suppliers.util;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.VmPolicy;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.micen.suppliers.R;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.module.product.ProductKeyValuePair;
import com.micen.suppliers.module.search.SearchLocation;


public class Util
{
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue)
	{
		return (int) (dpValue * Constants.density + 0.5f);
	}

	public static String getPrice(List<String> values)
	{
		String result = null;
		if (values != null)
		{
			if (values.size() == 1)
			{
				int index = values.get(0).indexOf(":");
				index = index + 1;
				if (index > 0 && index < values.get(0).length())
				{
					result = values.get(0).substring(index);
					if (result.startsWith("-1"))
					{
						result = null;
					}
					else
					{
						result = "$" + result;
					}
				}
			}
			else if (values.size() > 1)
			{
				int index = 0;
				double max = 0.0;
				double min = -1.0;
				double tmp = 0.0;

				String strLow = null;
				String strHigh = null;

				for (String str : values)
				{
					index = str.indexOf(":");

					index = index + 1;
					if (index > 0 && index < str.length())
					{
						result = str.substring(index);
						if (result.startsWith("-1"))
						{
							result = null;
						}
						else
						{
							try
							{
								tmp = Double.parseDouble(result);
							}
							catch (Exception e)
							{

							}

							if (tmp > max)
							{
								max = tmp;
								strHigh = result;
							}

							if (min < 0 || tmp < min)
							{
								min = tmp;
								strLow = result;
							}

							if (strLow == null)
							{
								strLow = result;
							}
						}
					}
				}

				result = "$" + strLow;
				if (strHigh != null)
				{
					result = result + "-" + strHigh;
				}

			}
		}
		return result;
	}

	/**
	 * 获取设备屏幕密度
	 * @return
	 */
	public static float getWindowDensity(Activity activity)
	{
		if (activity != null)
		{
			DisplayMetrics dm = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			return dm.density;
		}
		return 1.0f;
	}

	/**
	 * 格式化日期
	 * @param date
	 * @return yyyy-MM-dd HH:mm
	 */
	public static String formatDate(String date)
	{

		return formatDate(date, "yyyy-MM-dd HH:mm");
	}

	/**
	 * 格式化日期
	 * @param date
	 * @return
	 */
	public static String formatDateToEn(String date)
	{

		return formatDate(date, Constants.enDateTemplate);
	}

	/**
	 * 格式化日期
	 * @param date
	 * @param enDateTemplate 日期格式
	 * @return enDateTemplate
	 */
	public static String formatDate(String date, String enDateTemplate)
	{
		if ("".equals(date) || date == null)
		{
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(enDateTemplate, getLocale());
		Date dt = new Date();
		try
		{
			dt = new Date(Long.parseLong(date));
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		return sdf.format(dt);
	}

	public static String getLeftDay(String date)
	{
		if ("".equals(date) || date == null)
		{
			return "";
		}
		Date cur = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", getLocale());
		String curTime = sdf.format(cur);

		Date dt = new Date();
		long day = 0;
		try
		{
			dt = sdf.parse(curTime);
			long l = Long.parseLong(date) - dt.getTime();
			if (l > 0)
			{
				day = l / (24 * 60 * 60 * 1000);
			}
			else
			{
				day = 0;
			}
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}

		return Long.toString(day);
	}

	/**
	 * 判断时间：今天，昨天,yyyy-MM-dd...
	 * @param date
	 * @return 
	 */
	public static String formatDateForMessageList(String date)
	{
		long time = Long.parseLong(date);
		long mHourInMillis = 86400000;
		long currTime = System.currentTimeMillis();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTime(new Date(currTime));
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH);
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);

		mCalendar.set(year, month, day, 0, 0, 0);
		long minToday = mCalendar.getTimeInMillis() - mHourInMillis;

		mCalendar.set(year, month, day, 23, 59, 59);
		long maxToday = mCalendar.getTimeInMillis() - mHourInMillis;

		if (time >= minToday && time <= maxToday)
		{

			return "昨天";
		}
		else
		{
			if (formatDate(date, "yyyy-MM-dd").equals(formatDate(currTime + "", "yyyy-MM-dd")))
			{
				return "今天";
			}
			else
			{
				return formatDate(date, "yyyy-MM-dd");
			}
		}
	}

	/**
	  * 根据一个日期，返回是星期几的字符串
	  * 
	  * @param sdate
	  * @return
	  */
	@SuppressLint("SimpleDateFormat")
	public static String getWeek(String sdate)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(Long.parseLong(sdate)));
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}

	public static String getWeekStr(String sdate)
	{
		String str = "";
		str = getWeek(sdate);
		if ("星期日".equals(str))
		{
			str = "周日";
		}
		else if ("星期一".equals(str))
		{
			str = "周一";
		}
		else if ("星期二".equals(str))
		{
			str = "周二";
		}
		else if ("星期三".equals(str))
		{
			str = "周三";
		}
		else if ("星期四".equals(str))
		{
			str = "周四";
		}
		else if ("星期五".equals(str))
		{
			str = "周五";
		}
		else if ("星期六".equals(str))
		{
			str = "周六";
		}
		return str;
	}

	/**
	 * 转义HTML特殊字符
	 * @param content
	 * @return
	 */
	public static String transformHtml(String content)
	{
		if (content == null)
			return "";
		String html = content;
		html = html.replaceAll("&", "&amp;");
		html = html.replaceAll("\"", "&quot;"); // "
		// html = html.replaceAll("\t", "&nbsp;&nbsp;");// 替换跳格
		// html = html.replaceAll(" ", "&nbsp;");// 替换空格
		html = html.replaceAll("<", "&lt;");
		html = html.replaceAll(">", "&gt;");
		return html;
	}

	/**
	 * 转义HTML特殊字符
	 * @param content
	 * @return
	 */
	public static String transToHtml(String content)
	{
		if (content == null)
			return "";
		String html = content;
		// html = html.replaceAll("&amp;", "&");
		// html = html.replaceAll("&quot;", "\""); // "
		// html = html.replaceAll("\t", "&nbsp;&nbsp;");// 替换跳格
		// html = html.replaceAll("&nbsp;", " ");// 替换空格
		html = html.replaceAll("&lt;", "<");
		html = html.replaceAll("&gt;", ">");
		return html;
	}

	/**
	 * 获得刷新时间 formart 格式 “yyyy-MM-dd HH:mm”
	 * @return
	 */
	public static String getFormartRefershTime()
	{
		SimpleDateFormat formart = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		return formart.format(new Date());
	}

	public static String getFormartTime(String strFormart)
	{
		SimpleDateFormat formart = new SimpleDateFormat(strFormart, getLocale());
		return formart.format(new Date());
	}

	public static void saveChildLastRefreshTime(Context context, String key)
	{
		final String label = context.getString(R.string.mic_last_updated) + Util.getFormartRefershTime();
		SharedPreferenceManager.getInstance().putString(key, label);
	}

	/**
	 * 给数字添加千位分隔符
	 * @param number
	 * @return
	 */
	public static String getNumKb(long number)
	{
		NumberFormat formatter = new DecimalFormat("###,###");
		return formatter.format(number) + "";
	}

	/**
	 * 根据ListView中子项的总高度，重置ListView的高度
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView)
	{
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
		{
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++)
		{
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static void setScrollViewHeightBasedOnChild(ScrollView scrollView)
	{
		View child = scrollView.getChildAt(0);
		ViewGroup.LayoutParams params = (LayoutParams) scrollView.getLayoutParams();
		params.height = child.getHeight();
		scrollView.setLayoutParams(params);
	}

	/**
	* 是否是英文
	* @param c
	* @return
	*/
	public static boolean isEnglish(String charaString)
	{
		return charaString.matches("^[a-zA-Z]*");
	}

	private static final boolean isChinese(char c)
	{
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
		{
			return true;
		}
		return false;
	}

	/**
	 * 是否包含中文
	 * @param strName
	 * @return	true:包含
	 */
	public static boolean isChinese(String strName)
	{
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++)
		{
			char c = ch[i];
			if (isChinese(c))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 计算两个时间点的时长是否超过24小时
	 */
	public static String calculateTime()
	{
		String timeout = String.valueOf(System.currentTimeMillis() - Constants.DAY_TIME);
		return timeout;
	}

	public static List<String> getMidResolutionPicture(List<String> list)
	{
		List<String> lt = new ArrayList<String>();
		if (list.size() == 1)
		{
			lt = list;
		}
		else
		{
			for (int i = 0; i < list.size() / 3; i++)
			{
				lt.add(list.get(3 * i + 1));
			}
		}
		return lt;
	}

	public static ArrayList<String> getHighResolutionPicture(List<String> list)
	{
		ArrayList<String> lt = new ArrayList<String>();
		if (list.size() == 1)
		{
			lt = (ArrayList<String>) list;
		}
		else
		{
			for (int i = 0; i < list.size() / 3; i++)
			{
				lt.add(list.get(3 * i));
			}
		}
		return lt;
	}

	/**
	 * 将JSON列表数据转化为ArrayList
	 * @param jsonObj
	 * @param pairList
	 * @throws JSONException
	 */
	public static void setJsonValueToPairList(JSONObject jsonObj, ArrayList<ProductKeyValuePair> pairList)
			throws JSONException
	{
		Iterator<?> it = jsonObj.keys();
		ProductKeyValuePair pair;
		while (it.hasNext())
		{
			pair = new ProductKeyValuePair();
			pair.key = it.next().toString();
			if (jsonObj.get(pair.key) instanceof JSONObject)
			{
				ArrayList<ProductKeyValuePair> tempPairList = new ArrayList<ProductKeyValuePair>();
				ProductKeyValuePair tempPair;
				Iterator<?> tempIt = jsonObj.getJSONObject(pair.key).keys();
				while (tempIt.hasNext())
				{
					tempPair = new ProductKeyValuePair();
					tempPair.key = tempIt.next().toString();
					tempPair.value = jsonObj.getJSONObject(pair.key).getJSONArray(tempPair.key).get(0);
					tempPairList.add(tempPair);
				}
				pair.value = tempPairList;
			}
			else
			{
				pair.value = jsonObj.get(pair.key).toString();
			}
			pairList.add(pair);
		}
	}

	/**
	 * 根据设定好的符验证输入字符串是否满足条件
	 * @param value
	 * @return
	 */
	public static boolean isValidKeyWord(String value)
	{
		String regex = "^[a-zA-Z0-9-]{6,20}+$";
		return Pattern.matches(regex, value);
	}

	/**
	 * 隐藏系统软件盘
	 */
	public static void hideSoftInputMethod(Context context, View v)
	{
		/* 隐藏软键盘 */
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager.isActive())
		{
			inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}

	/**
	 * 获取格式转换的语言
	 * @return
	 */
	public static Locale getLocale()
	{
		return Locale.ENGLISH;
	}

	/**
	 * 取出数组中的所有值组装成一个字符串
	 * @param data
	 * @return
	 */
	public static String getAllListValue(ArrayList<String> data)
	{
		String value = "";
		if (data == null)
			return value;
		for (int i = 0; i < data.size(); i++)
		{
			value = value + data.get(i) + (i == data.size() - 1 ? "" : ",");
		}
		return value;
	}

	/** 
	 * 替换字符串中特殊字符 
	 */
	public static String replaceHtmlStr(String strData)
	{
		if (strData == null)
		{
			return "";
		}
		strData = strData.replace("<", "&lt;");
		strData = strData.replace(">", "&gt;");
		strData = strData.replace("\"", "&quot;");
		return strData;
	}

	/**
	 * 按照APP设置去决定是否显示图片
	 */
	public static void showFitImage(String isShowImage, String imagePath, ImageView imageView)
	{
		/**
		 * 判断图片是否显示
		 */
		if ("2201000000".equals(isShowImage) || "1713000000".equals(isShowImage))
		{// 是限制级
			// 判断 是否开启浏览图片
			if (SharedPreferenceManager.getInstance().getBoolean("isDisplaySafeImage", true))
			{// 默认关闭
				imageView.setImageResource(R.drawable.bg_product_fort_adult);
			}
			else
			{
				ImageUtil.getImageLoader().displayImage(imagePath, imageView, ImageUtil.getSafeImageOptions());
			}
		}
		else
		{
			ImageUtil.getImageLoader().displayImage(imagePath, imageView, ImageUtil.getSafeImageOptions());
		}
	}

	/**
	 * 按照APP设置去决定是否显示产品详情页的图片
	 * @param isShowImage
	 * @param imagePath
	 * @param imageView
	 */
	public static void showProductDetailImage(String isShowImage, String imagePath, ImageView imageView)
	{
		/**
		 * 判断图片是否显示
		 */
		if ("2201000000".equals(isShowImage) || "1713000000".equals(isShowImage))
		{// 是限制级
			// 判断 是否开启浏览图片
			if (SharedPreferenceManager.getInstance().getBoolean("isDisplaySafeImage", true))
			{// 默认关闭
				imageView.setImageResource(R.drawable.bg_product_fort_adult_large);
			}
			else
			{
				ImageUtil.getImageLoader()
						.displayImage(imagePath, imageView, ImageUtil.getSafeImageLargerStubOptions());
			}
		}
		else
		{
			ImageUtil.getImageLoader().displayImage(imagePath, imageView, ImageUtil.getSafeImageLargerStubOptions());
		}
	}

	/**
	 * 收集设备参数信息
	 * @param ctx
	 */
	public static void collectDeviceInfo(Context ctx, HashMap<String, String> infos)
	{
		try
		{
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null)
			{
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		}
		catch (NameNotFoundException e)
		{
			e.getMessage();
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields)
		{
			try
			{
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			}
			catch (Exception e)
			{
				e.getMessage();
			}
		}
	}

	public static long availableSDCard()
	{
		File path = Environment.getExternalStorageDirectory();
		long availableSize = 0;
		if (path != null)
		{
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlock = stat.getAvailableBlocks();
			availableSize = availableBlock * blockSize;
		}

		return availableSize / (1024 * 1024);
	}

	/**
	 * RFQ默认最小时间（毫秒）
	 * 选择的日期如果大大于等于当前日期加上偏移值之后的日期，则验证通过
	 * @return
	 */
	public static Date getMinTime()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + Constants.DAY_OFFSET
				- 1);
		return cal.getTime();
	}

	/**
	 * 检测一个字符串中是否包含英文字符
	 * @return
	 */
	public static boolean isHasEnglish(String source)
	{
		if (source != null && !"".equals(source))
		{
			for (int i = 0; i < source.length(); i++)
			{
				if (Character.isLetter(source.charAt(i)))
				{ // 用char包装类中的判断字母的方法判断每一个字符
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据输入的分隔符接取字符串
	 */
	public static String CutString(String source, String format)
	{
		String[] des = source.split(format);
		return des[des.length - 1];
	}

	/**
	 * 得到不同产品的总数
	 * @param locations
	 * @return
	 */
	public static long getLocationsNumber(ArrayList<SearchLocation> locations)
	{
		long size = 0;
		if (locations == null || locations.size() == 0)
		{
			size = 0;
		}
		else
		{
			for (SearchLocation location : locations)
			{
				size += Integer.parseInt(location.num);
			}
		}
		return size;
	}

	public static void startDeveloperMode()
	{
		if (Constants.DEV_MODE)
		{
			StrictMode.setThreadPolicy(new ThreadPolicy.Builder().detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new VmPolicy.Builder().detectAll().penaltyLog().build());
		}
	}

	public static void dispatchCancelEvent(View view)
	{
		view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
				MotionEvent.ACTION_CANCEL, 0, 0, 0));
	}

	public static float parseFloat(String value)
	{
		float f = 0;
		try
		{
			f = Float.parseFloat(value);
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		return f;
	}

	public static float parseProductStar(String value)
	{
		float f = 0;
		int star = 0;
		try
		{
			star = Integer.parseInt(value);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			star = 1;
		}
		switch (star)
		{
		case 1:
			f = 2;
			break;
		case 2:
			f = 3;
			break;
		case 3:
			f = 4;
			break;
		case 4:
			f = 4.5f;
			break;

		default:
			break;
		}

		return f;
	}

	public static void hideSoftKeyboard(Activity activity)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static void showSoftKeyboard(Activity activity, View view)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		// inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		inputMethodManager.showSoftInput(view, 0);
		// inputMethodManager.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static String getGCMProjectNumber()
	{
		String projectNumber = "";
		try
		{
			ApplicationInfo appInfo = SupplierApplication
					.getInstance()
					.getPackageManager()
					.getApplicationInfo(SupplierApplication.getInstance().getPackageName(),
							PackageManager.GET_META_DATA);
			projectNumber = appInfo.metaData.getString("GCM_PROJECT_NUMBER");
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return projectNumber;
	}
}
