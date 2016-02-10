package com.micen.suppliers.application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import org.androidannotations.annotations.EApplication;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;

import com.focustech.common.CommonConfiguration;
import com.focustech.common.CommonConfiguration.PushWay;
import com.focustech.common.CommonConfigurationHelper;
import com.focustech.common.http.FocusClient;
import com.focustech.common.listener.CrashListener;
import com.focustech.common.mob.MicCrashHandler;
import com.focustech.common.mob.analysis.FocusAnalyticsTracker;
import com.focustech.common.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.focustech.common.universalimageloader.cache.memory.MemoryCache;
import com.focustech.common.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.focustech.common.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.focustech.common.universalimageloader.core.ImageLoader;
import com.focustech.common.universalimageloader.core.ImageLoaderConfiguration;
import com.focustech.common.universalimageloader.core.assist.QueueProcessingType;
import com.focustech.common.util.Utils;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.db.SharedPreferenceManager;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.NotifyManager;
import com.micen.suppliers.module.user.User;
import com.tencent.android.tpush.XGPushConfig;


@EApplication
public class SupplierApplication extends Application
{
	private static SupplierApplication application = null;
	private User user = null;
	private static int startFromLoading = 1;

	public static SupplierApplication getInstance()
	{
		return application;
	}

	public static void setStartFromLoading()
	{
		startFromLoading = 2;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		application = this;
		FocusClient.setPersistentCookieStore(getApplicationContext());
		initImageLoader(getApplicationContext());
		initExceptionHandler();
		// 这里已经配置了 push 及 userpkid
		initCommonConfig();
		FocusAnalyticsTracker.getInstances().startAnalyticsService(180000);
		NotifyManager.getInstance().initNotifyCallback(this);
		RequestCenter.registerDevice();

		/*
		 * try { GCMRegistrar.checkDevice(this); GCMRegistrar.checkManifest(this); final String regId =
		 * GCMRegistrar.getRegistrationId(this); if (regId.equals("")) { GCMRegistrar.register(this,
		 * Util.getGCMProjectNumber()); } else { LogUtil.v("GCMRegistrar", "Already registered"); } } catch
		 * (RuntimeException e) { }
		 */
	}

	/**
	 * 初始化公共模块配置
	 */
	private void initCommonConfig()
	{
		// 这边使用信鸽，userpkid使用信鸽的token
		// 如果使用GCM，则使用GCM的token
		CommonConfiguration builer = new CommonConfiguration.Builder(this).setProductName(Constants.APP_KEY)
				.setReLoginAction(Constants.APP_RELOGIN_ACTION).setPushWay(PushWay.TENCENTXG)
				.setUserPkId(XGPushConfig.getToken(application)).build();
		CommonConfigurationHelper.getInstance().init(builer);
	}

	/**
	 * 初始化异常处理对象
	 */
	private void initExceptionHandler()
	{
		MicCrashHandler crashHandler = MicCrashHandler.getInstance();
		crashHandler.init(getApplicationContext(), new CrashListener()
		{
			@Override
			public void onCrash()
			{
				Process.killProcess(Process.myPid());
				System.exit(0);
			}
		});
	}

	public static void initImageLoader(Context context)
	{
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);

		MemoryCache memoryCache;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
		{
			memoryCache = new LruMemoryCache(memoryCacheSize);
		}
		else
		{
			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
		}
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.memoryCache(memoryCache);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	public User getUser()
	{
		if (startFromLoading == 1)
		{
			String str_value = SharedPreferenceManager.getInstance().getString("user", "");
			if (!Utils.isEmpty(str_value))
			{
				ObjectInputStream OIS = null;
				ByteArrayInputStream BAIS;
				int length = str_value.length() / 2;
				byte[] bytes = new byte[length];

				for (int i = 0; i < length; i++)
				{
					bytes[i] = (byte) Integer.parseInt(str_value.substring(i << 1, (i << 1) + 2), 16);
				}

				BAIS = new ByteArrayInputStream(bytes);
				try
				{
					OIS = new ObjectInputStream(BAIS);
					this.user = (User) (OIS.readObject());
					OIS.close();
				}
				catch (StreamCorruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;

		ObjectOutputStream OOS = null;
		ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
		try
		{
			OOS = new ObjectOutputStream(BAOS);
			OOS.writeObject(user);
			byte[] abc = BAOS.toByteArray();
			StringBuilder builder = new StringBuilder();
			for (byte b : abc)
			{
				builder.append(String.format("%02x", b));
			}
			SharedPreferenceManager.getInstance().putString("user", builder.toString());
			OOS.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
