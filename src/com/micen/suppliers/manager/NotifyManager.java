package com.micen.suppliers.manager;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.focustech.common.broadcast.xmpp.Notifier;
import com.focustech.common.util.LogUtil;
import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.LoadingActivity_;
import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.activity.message.MessageDetailActivity_;
import com.micen.suppliers.activity.purchase.PurchaseActivity_;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.module.NotifyLink;
import com.micen.suppliers.module.NotifySetStatus;
import com.micen.suppliers.module.WebViewType;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.tencent.android.tpush.XGNotifaction;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushNotifactionCallback;


public class NotifyManager
{
	private static NotifyManager instance = null;

	private NotifyManager()
	{

	}

	public static NotifyManager getInstance()
	{
		if (instance == null)
		{
			instance = new NotifyManager();
		}
		return instance;
	}

	public void initNotifyCallback(final Context context)
	{
		XGPushManager.setNotifactionCallback(new XGPushNotifactionCallback()
		{
			@Override
			public void handleNotify(XGNotifaction xGNotifaction)
			{
				LogUtil.e("--------XGPushManager.setNotifactionCallback------------", xGNotifaction.toString());
				String customContent = xGNotifaction.getCustomContent();
				if (!Utils.isEmpty(customContent))
				{
					// 获取标签、内容、自定义内容
					String title = xGNotifaction.getTitle();
					String content = xGNotifaction.getContent();
					try
					{
						JSONObject contentJson = new JSONObject(customContent);
						if (contentJson.has("link") && contentJson.has("param") && contentJson.has("mId")
								&& contentJson.has("prod"))
						{
							// 其它的处理
							// 如果还要弹出通知，可直接调用以下代码或自己创建Notifaction，否则，本通知将不会弹出在通知栏中。
							// xGNotifaction.doNotify();
							showNotification(context, title, content, contentJson.getString("prod"),
									contentJson.getString("link"), contentJson.getString("param"),
									contentJson.getString("mId"));
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
				}

			}
		});
	}

	public void showNotification(Context context, String title, String content, String prod, String link, String param,
			String mId)
	{
		if (Utils.isEmpty(prod) || Utils.isEmpty(link) || Utils.isEmpty(param) || Utils.isEmpty(mId))
		{
			return;
		}
		if (Constants.APP_KEY.equals(prod))
		{
			if (isReceiveNotify(context, link))
			{
				Notifier notifier = new Notifier(context);
				notifier.notify(R.drawable.ic_notification, R.drawable.ic_launcher,
						context.getResources().getColor(R.color.color_0088F0), title, content,
						getNotificationContentIntent(context, link, mId, param));
			}
		}
	}

	/**
	 * 验证是否能收到消息提醒
	 * @param context
	 * @param appKey
	 * @param messageType
	 * @return
	 */
	private boolean isReceiveNotify(Context context, String messageLink)
	{
		SharedPreferences sp = context.getSharedPreferences(Constants.sharedPreDBName, Context.MODE_PRIVATE);
		// 开启接收消息总开关
		if (!sp.getBoolean(NotifySetStatus.getValue(NotifySetStatus.TotalSwitch), true))
		{
			return false;
		}
		// 是否开启夜间免打扰模式
		if (sp.getBoolean(NotifySetStatus.getValue(NotifySetStatus.NotDisturbSwitch), false))
		{
			Calendar cl = Calendar.getInstance(TimeZone.getDefault());
			int hourOfDay = cl.get(Calendar.HOUR_OF_DAY);
			if (hourOfDay >= 23 || hourOfDay <= 8)
			{
				return false;
			}
		}
		switch (NotifyLink.getValueByTag(messageLink))
		{
		case InquiryDetail:// 是否接收询盘消息
			if (!sp.getBoolean(NotifySetStatus.getValue(NotifySetStatus.InquirySwitch), true))
			{
				return false;
			}
			break;
		case Purchase:// 是否接收采购消息
			if (!sp.getBoolean(NotifySetStatus.getValue(NotifySetStatus.PurchaseSwitch), true))
			{
				return false;
			}
			break;
		case WebAddress:// 是否接收服务消息
		case Update:
			if (!sp.getBoolean(NotifySetStatus.getValue(NotifySetStatus.ServiceSwitch), true))
			{
				return false;
			}
			break;
		default:
			return false;
		}
		return true;
	}

	private PendingIntent getNotificationContentIntent(Context context, String link, String id, String param)
	{
		Intent intent = new Intent();
		if (!ActivityManager.getInstance().isHomeActivityAvailable())
		{
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.putExtra("messageLink", link);
			intent.putExtra("messageParam", param);
			intent.putExtra("messageId", id);
			intent.setComponent(new ComponentName(context.getPackageName(), LoadingActivity_.class.getName()));
		}
		else
		{
			switch (NotifyLink.getValueByTag(link))
			{
			case InquiryDetail:
				intent.setClass(context, MessageDetailActivity_.class);
				intent.putExtra("isFromBroadcast", true);
				intent.putExtra("messageId", id);
				intent.putExtra(MessageConstantDefine.isPurchase.toString(), true);
				intent.putExtra(MessageConstantDefine.mailId.toString(), param);
				intent.putExtra(MessageConstantDefine.action.toString(), "0");
				break;
			case Purchase:
				intent.setClass(context, PurchaseActivity_.class);
				intent.putExtra("fragment", "rfqneedquote");
				intent.putExtra("isFromBroadcast", true);
				intent.putExtra("messageId", id);
				break;
			case WebAddress:
				intent.setClass(context, WebViewActivity_.class);
				intent.putExtra("targetUri", param);
				intent.putExtra("targetType", WebViewType.getValue(WebViewType.Service));
				intent.putExtra("isFromBroadcast", true);
				intent.putExtra("messageId", id);
				break;
			default:
				break;
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		}
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		// | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_SINGLE_TOP
		// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，你需要给notification设置一个独一无二的requestCode
		int requestCode = (int) SystemClock.uptimeMillis();
		return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
