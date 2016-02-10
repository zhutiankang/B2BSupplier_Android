package com.micen.suppliers.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;

import com.focustech.common.listener.SimpleDisposeDataListener;
import com.focustech.common.mob.MobRequestCenter;
import com.focustech.common.mob.update.UpdateService;
import com.focustech.common.module.response.Update;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.db.SharedPreferenceManager;


public class CheckUpdateManager
{
	/**
	 * 检查更新
	 * @param context
	 * @param isNeedToast	是否弹出提示
	 */
	public static void checkUpdate(final Activity activity, final boolean isNeedToast)
	{
		if (isNeedToast && !CommonProgressDialog.getInstance().isShowing() && activity != null
				&& !activity.isFinishing())
		{
			CommonProgressDialog.getInstance().showCancelableProgressDialog(activity,
					activity.getString(R.string.loading));
		}
		MobRequestCenter.checkUpdate(new SimpleDisposeDataListener()
		{
			@Override
			public void onSuccess(Object obj)
			{
				final Update update = (Update) obj;
				CommonProgressDialog.getInstance().dismissProgressDialog();
				if (update.content.isNewVersion())
				{
					SharedPreferenceManager.getInstance().putString("latestVersion", update.content.versionInfo);
					SharedPreferenceManager.getInstance().putBoolean("isHaveNewVersion", true);
					String msg = (update.content.remarksUpdate != null && !"".equals(update.content.remarksUpdate)) ? update.content.remarksUpdate
							: activity.getString(R.string.update_not_have_msg);
					CommonDialog dialog = new CommonDialog(activity);
					dialog.setDialogMode(update.content.isForceUpdate()).setCancelBtnText(R.string.cancel)
							.setConfirmBtnText(R.string.confirm).setConfirmDialogListener(new DialogClickListener()
							{
								@Override
								public void onDialogClick()
								{
									String filePath = Environment.getExternalStorageDirectory()
											+ "/focustech/mic/update/MIC_for_Supplier_V" + update.content.versionInfo
											+ ".apk";
									Intent serviceIntent = new Intent(activity, UpdateService.class);
									serviceIntent.putExtra("downloadUrl", update.content.upgradeUrl);
									serviceIntent.putExtra("filePath", filePath);
									serviceIntent.putExtra("contentLength",
											Long.parseLong(update.content.contentLength));
									activity.startService(serviceIntent);
								}
							}).buildSimpleDialog(msg);
				}
				else
				{
					SharedPreferenceManager.getInstance().putBoolean("isClickSetting", false);
					SharedPreferenceManager.getInstance().putBoolean("isHaveNewVersion", false);
					if (isNeedToast)
					{
						ToastUtil.toast(activity, R.string.latest_version);
					}
				}
			}

			@Override
			public void onFailure(Object obj)
			{
				CommonProgressDialog.getInstance().dismissProgressDialog();
				if (isNeedToast)
				{
					ToastUtil.toast(activity, R.string.update_failed);
				}
			}
		});
	}
}
