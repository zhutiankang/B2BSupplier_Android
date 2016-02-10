package com.micen.suppliers.view.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.capturepicture.CameraTakePhoto;
import com.focustech.common.capturepicture.GalleryTakePhoto;
import com.focustech.common.capturepicture.TakePhoto;
import com.focustech.common.capturepicture.TakePhoto.TakePhotoFailReason;
import com.focustech.common.capturepicture.TakePhoto.TakePhotoListener;
import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.util.Utils;
import com.focustech.common.widget.dialog.CommonDialog;
import com.focustech.common.widget.dialog.CommonDialog.DialogClickListener;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.message.MessageShortCutActivity_;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.constant.Constants;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.manager.PopupManager;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.module.message.MessageContentFiles;
import com.micen.suppliers.module.message.MessageDetailUpLoadFile;
import com.umeng.analytics.MobclickAgent;

@EFragment
public class MessageSentBaseFragment extends Fragment implements OnClickListener, TextWatcher
{
	@ViewById(R.id.common_ll_title_back)
	protected LinearLayout llBack;
	@ViewById(R.id.common_title_back)
	protected ImageView btnBack;
	@ViewById(R.id.common_title_name)
	protected TextView tvTitle;
	@ViewById(R.id.common_title_function3)
	protected ImageView btnFunction3;
	@ViewById(R.id.common_title_function4)
	protected ImageView btnFunction4;
	@ViewById(R.id.common_title_function5)
	protected ImageView btnFunction5;
	@ViewById(R.id.rl_common_title)
	protected RelativeLayout messageTitleRl;
	@ViewById(R.id.message_send_tv_contacts)
	protected TextView sendTvContacts;
	@ViewById(R.id.message_send_tv_contacts_name)
	protected TextView sendTvContactsName;
	@ViewById(R.id.message_send_subject_et_subject)
	protected EditText sendSubjectEtSubject;
	@ViewById(R.id.message_send_subject_iv_clear)
	protected ImageView sendSubjectIvClear;
	@ViewById(R.id.message_send_attachment_ll)
	protected LinearLayout sendAttachmentLl;
	@ViewById(R.id.message_send_attachment_ll_attachment)
	protected LinearLayout sendAttachmentLlAttachment;
	@ViewById(R.id.message_send_et_content)
	protected EditText sendEtContent;
	protected CommonDialog commonDialog;

	protected int popupViewWidth = 0;
	protected TakePhoto takePhoto;
	protected View fileAddView;
	protected View fileDisplayView;
	/** 图片裁剪完成回调标示 **/
	protected static final int PHOTO_REQUEST_CUT = 0x00000002;
	/** 模板选择回调标示 **/
	protected static final int SHORTCUTCODE = 0x00000005;
	// 用户进行二次编辑的控件TAG的size
	protected Long fileDisplayViewSize = 0L;
	/** 文件大小限制2M**/
	protected Long fileSizeLimit = 2097152l;
	protected int currentmailSendAttachmentIndex = 0;
	protected Long filesSize = 0l;
	protected Long fileSize;
	protected String files;
	protected String fileName;
	protected String fileType;
	protected int currentCaptureChildIndex = 0;
	protected final int UPLOAD_FILE_COMPLETE = 0;
	protected final int POST_INQUIRY_FAILED = 1;
	protected String shortCutContent = "";

	protected String replySubject;
	protected String replyReceive;
	protected String replyMailId = "";
	private Boolean isGoldMember = true;

	/**
	 * 记录当前光标位置
	 */
	protected int selection = 0;

	protected void initView()
	{
		popupViewWidth = Utils.getWindowWidthPix(getActivity()) / 4 * 3;
		sendAttachmentLlAttachment.addView(createCaptureEmptyChildView());
		initOnClickListener();
		initData();
		initTitleBar();
	}

	protected void initData()
	{
		isGoldMember = "30".equals(SupplierApplication.getInstance().getUser().content.companyInfo.csLevel) ? true
				: false;

	}

	/**
	 * 
	 * 初始化TitleBar
	 */
	private void initTitleBar()
	{
		btnBack.setImageResource(R.drawable.ic_title_back);
		llBack.setOnClickListener(this);
		tvTitle.setTextColor(getResources().getColor(R.color.color_ffffff));
		if (isGoldMember)
		{
			btnFunction3.setVisibility(View.VISIBLE);
			btnFunction3.setImageResource(R.drawable.ic_message_add_attachment);
			btnFunction3.setOnClickListener(this);
		}
		btnFunction4.setVisibility(View.VISIBLE);
		btnFunction4.setImageResource(R.drawable.ic_message_add_shortcut);
		btnFunction4.setOnClickListener(this);
		btnFunction5.setVisibility(View.VISIBLE);
		btnFunction5.setImageResource(R.drawable.ic_message_post);
		btnFunction5.setOnClickListener(this);
	}

	protected void initOnClickListener()
	{
		sendSubjectIvClear.setOnClickListener(this);
		sendSubjectEtSubject.addTextChangedListener(this);
		sendEtContent.addTextChangedListener(this);
		// 设置正文部分字数限制
		sendEtContent.setFilters(new InputFilter[]
		{ new InputFilter.LengthFilter(Constants.REPLAY_MESSAGE_LENGTH) });
	}

	protected void sendEmail()
	{

	}

	/**
	 * 供Activity的TitleBar中的添加附件按钮调用
	 */
	public void showPopup()
	{
		SysManager.getInstance().dismissInputKey(getActivity());
		// 获取到sendAttachmentLlAttachment中显示的最后一个对象
		if (sendAttachmentLlAttachment != null)
		{
			ImageView preImage = (ImageView) sendAttachmentLlAttachment.getChildAt(
					sendAttachmentLlAttachment.getChildCount() - 1).findViewById(R.id.message_capture_image);
			PopupManager.getInstance().showCapturePopup(createPopupView(createCapturePopupView(null), popupBgClick));
			fileDisplayView = fileAddView = preImage;
		}

	}

	public void startActivityForShortCut()
	{
		// 获得当前光标位置
		selection = sendEtContent.getSelectionStart();
		startActivityForResult((new Intent(getActivity(), MessageShortCutActivity_.class)), SHORTCUTCODE);
	}

	protected RelativeLayout createPopupView(View contentView, OnClickListener bgClick)
	{
		RelativeLayout popupLayout = new RelativeLayout(getActivity());
		popupLayout.addView(createPopupBgView(bgClick));
		popupLayout.addView(contentView);
		return popupLayout;
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected ImageView createPopupBgView(OnClickListener click)
	{
		ImageView bgImage = new ImageView(getActivity());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		bgImage.setLayoutParams(params);
		bgImage.setImageResource(R.color.black);
		if (Build.VERSION.SDK_INT > 10)
		{
			bgImage.setAlpha(0.6f);
		}
		else
		{
			bgImage.setAlpha(153);
		}
		bgImage.setOnClickListener(click);
		return bgImage;
	}

	/**
	 * 创建图片获取方式弹出层布局
	 * @param displayView
	 * @return
	 */
	protected LinearLayout createCapturePopupView(View displayView)
	{
		LinearLayout contentLayout = new LinearLayout(getActivity());
		contentLayout.setBackgroundResource(R.color.color_ffffff);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(popupViewWidth, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		contentLayout.setLayoutParams(params);
		contentLayout.setOrientation(LinearLayout.VERTICAL);

		contentLayout.addView(createCaptureText(R.id.message_popup_gallery,
				R.string.message_sent_attachment_choose_from_Gallery, displayView));

		View line = new View(getActivity());
		LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.toDip(getActivity(), 0.5f));
		line.setBackgroundResource(R.color.color_cccccc);
		line.setLayoutParams(lineParams);
		contentLayout.addView(line);
		contentLayout.addView(createCaptureText(R.id.message_popup_camera,
				R.string.message_sent_attachment_take_photos, displayView));

		// 添加添加分隔线 和add attachment选项
		View attachmentline = new View(getActivity());
		LayoutParams attachmentlineParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				Utils.toDip(getActivity(), 0.5f));
		attachmentline.setBackgroundResource(R.color.color_cccccc);
		attachmentline.setLayoutParams(attachmentlineParams);
		contentLayout.addView(attachmentline);

		contentLayout.addView(createCaptureText(R.id.message_popup_cancel, R.string.cancel, displayView));

		return contentLayout;
	}

	/**
	 * 创建图片获取方式弹出层布局中的文本控件
	 * 
	 * @param id
	 * @param textResId
	 * @param displayView
	 * @return
	 */
	protected TextView createCaptureText(int id, int textResId, View displayView)
	{
		TextView textView = new TextView(getActivity());
		textView.setId(id);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.toDip(getActivity(), 48));
		textView.setLayoutParams(params);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundResource(R.drawable.btn_common_menu_item);
		textView.setText(textResId);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		textView.setTextColor(getResources().getColor(R.color.color_333333));
		textView.setOnClickListener(captureGalleryViewOnClick(displayView));
		return textView;
	}

	/**
	 * 设置图片获取方式弹出层事件
	 * 
	 * @param displayView
	 * @return
	 */
	protected OnClickListener captureGalleryViewOnClick(final View displayView)
	{
		OnClickListener click = new OnClickListener()
		{
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.message_popup_gallery:
					takePhoto = new GalleryTakePhoto(MessageSentBaseFragment.this, null, listener);
					break;
				case R.id.message_popup_camera:
					takePhoto = new CameraTakePhoto(MessageSentBaseFragment.this, null, listener);
					break;
				case R.id.message_popup_cancel:
					break;
				}
				PopupManager.getInstance().dismissPopup();
			}
		};
		return click;
	}

	protected OnClickListener popupBgClick = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			PopupManager.getInstance().dismissPopup();
		}
	};
	protected TakePhotoListener listener = new TakePhotoListener()
	{
		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String imagePath, View displayView, Bitmap bitmap, Long fileSize)
		{
			activityfinishProtect();
			// 获取到用户点击的控件，获得TAG中的filesize
			if ((MessageContentFiles) fileDisplayView.getTag() != null)
			{
				fileDisplayViewSize = ((MessageContentFiles) fileDisplayView.getTag()).fileSize;
			}
			else
			{
				fileDisplayViewSize = 0l;
			}
			displayView = fileAddView;
			// 判断文件大小，并且进行容器容量计算
			if (fileSize > fileSizeLimit || retrieveTheFileSize() + fileSize - fileDisplayViewSize > fileSizeLimit)
			{
				ToastUtil.toast(getActivity(), R.string.message_sent_attachment_limitation);
			}
			else
			{
				MessageContentFiles file = computerFileSize(imagePath, fileSize);

				if (file != null)
				{
					if (displayView instanceof ImageView)
					{
						((ImageView) displayView).setImageBitmap(bitmap);
					}
					else
					{
						Drawable drawable = new BitmapDrawable(getActivity().getResources(), bitmap);
						displayView.setBackgroundDrawable(drawable);
					}
					displayView.setTag(file);
					// 保存完图片路径后显示删除按钮
					((RelativeLayout) displayView.getParent()).findViewById(R.id.message_capture_del_image)
							.setVisibility(View.VISIBLE);
					if (View.VISIBLE != sendAttachmentLl.getVisibility())
					{
						sendAttachmentLl.setVisibility(View.VISIBLE);
					}
				}
			}
		}

		/**
		 * 递归计算父布局中的已存在的文件大小总和
		 */
		protected Long retrieveTheFileSize()
		{
			if (currentmailSendAttachmentIndex < sendAttachmentLlAttachment.getChildCount())
			{
				// 获取对应子控件对象
				ImageView imageView = (ImageView) sendAttachmentLlAttachment.getChildAt(currentmailSendAttachmentIndex)
						.findViewById(R.id.message_capture_image);
				if (imageView.getTag() != null)
				{
					currentmailSendAttachmentIndex++;
					MessageContentFiles files = (MessageContentFiles) imageView.getTag();
					filesSize = files.fileSize + retrieveTheFileSize();
					return filesSize;
				}
				else
				{
					currentmailSendAttachmentIndex = 0;
					return 0l;
				}
			}
			else
			{
				currentmailSendAttachmentIndex = 0;
				filesSize = 0l;
			}
			return 0l;
		}

		/**
		 * 根据file的地址路径，获取文件的大小，文件名和文件类型。
		 * 
		 * @param path
		 * @return
		 */
		protected MessageContentFiles computerFileSize(String path, Long size)
		{
			MessageContentFiles contentFiles = new MessageContentFiles();
			try
			{
				// 获取对应uri文件的大小，限制文件大小总和为3M
				if (size != null)
				{
					fileSize = size;
				}
				else
				{
					File file = new File(path);
					FileInputStream fileInputStream = new FileInputStream(file);
					fileSize = (long) fileInputStream.available();
					fileInputStream.close();
				}

				if (fileSize > fileSizeLimit)
				{
					ToastUtil.toast(getActivity(), R.string.message_sent_attachment_limitation);
					return null;
				}
				files = path.substring(path.lastIndexOf("/") + 1);
				fileName = files;
				fileType = files.substring(files.lastIndexOf(".") + 1);
				// 数据添加，TAG绑定
				contentFiles.fileLocalPath = path;
				contentFiles.fileName = fileName;
				contentFiles.fileSize = fileSize;
				contentFiles.fileType = fileType;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return null;
			}

			return contentFiles;
		}

		@Override
		public void onFail(TakePhotoFailReason failReason)
		{
			if (failReason == TakePhotoFailReason.SDCardNotFound)
			{
				ToastUtil.toast(getActivity(), R.string.nosdcard);
			}
		}

		@Override
		public void onSuccess(String imagePath, View displayView, Bitmap bitmap)
		{
		}
	};

	private void activityfinishProtect()
	{
		if (getActivity() == null || getActivity().isFinishing())
			return;
	}

	/**
	 * 创建图片子布局
	 * 
	 * @return
	 */
	protected RelativeLayout createCaptureEmptyChildView()
	{
		RelativeLayout childItemLayout = new RelativeLayout(getActivity());
		LayoutParams params = new LayoutParams(Utils.toDip(getActivity(), 90), Utils.toDip(getActivity(), 90));
		params.leftMargin = params.rightMargin = Utils.toDip(getActivity(), 10);
		childItemLayout.setLayoutParams(params);

		ImageView image = new ImageView(getActivity());
		image.setId(R.id.message_capture_image);
		RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(Utils.toDip(getActivity(), 80),
				Utils.toDip(getActivity(), 80));
		relParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT | RelativeLayout.ALIGN_PARENT_BOTTOM);
		image.setLayoutParams(relParams);
		image.setBackgroundResource(R.drawable.ic_message_capture_add);
		image.setOnClickListener(this);
		childItemLayout.addView(image);

		ImageView delImage = new ImageView(getActivity());
		delImage.setId(R.id.message_capture_del_image);
		relParams = new RelativeLayout.LayoutParams(Utils.toDip(getActivity(), 22), Utils.toDip(getActivity(), 22));
		relParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT | RelativeLayout.ALIGN_PARENT_TOP);
		delImage.setLayoutParams(relParams);
		delImage.setBackgroundResource(R.drawable.ic_message_capture_delete);
		delImage.setOnClickListener(this);
		delImage.setVisibility(View.GONE);
		childItemLayout.addView(delImage);
		return childItemLayout;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			if (SHORTCUTCODE == requestCode)
			{
				// 插入快捷询盘短语操作
				upDataEditText(requestCode, resultCode, data);
			}
			else
			{
				afterTakeCaptureOperate(requestCode, resultCode, data);
			}
		}
	}

	/**
	 * 添加快捷询盘模板
	 */
	protected void upDataEditText(int requestCode, int resultCode, Intent data)
	{
		/**
		 * 1.获取到邮件正文的内容,及用户需要添加的快捷短语
		 * 2.甄别是否已经包含了快捷短语
		 * 3.包含的情况下，不进行短语添加
		 * 4.否则进行添加，并重新确定光标的位置
		 */
		shortCutContent = data.getStringExtra(MessageConstantDefine.shortCutKey.toString());
		if (!Utils.isEmpty(shortCutContent))
		{

			Editable editable = sendEtContent.getText();

			if (Utils.isEmpty(editable.toString().trim()))
			{
				sendEtContent.setText(shortCutContent + "\r\n");
				sendEtContent.setSelection((shortCutContent + "\r\n").length());
			}
			else
			{
				// 插入快捷短语
				StringBuffer buffer = new StringBuffer();
				buffer.append(shortCutContent);
				buffer.append("\r\n");
				editable.insert(selection, buffer.toString());
				selection = selection + buffer.toString().length();
				sendEtContent.setText(editable.toString());
				sendEtContent.setSelection(selection);
			}
		}
	}

	/**
	 * 图片获取完成后的处理方法
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	protected void afterTakeCaptureOperate(int requestCode, int resultCode, Intent data)
	{
		if (takePhoto != null)
		{
			takePhoto.onActivityResult(requestCode, resultCode, data);
		}
		if (sendAttachmentLlAttachment != null)
		{

			ImageView preImage = (ImageView) sendAttachmentLlAttachment.getChildAt(
					sendAttachmentLlAttachment.getChildCount() - 1).findViewById(R.id.message_capture_image);
			if (sendAttachmentLlAttachment.getChildCount() < 3 && preImage.getDrawable() != null)
			{
				sendAttachmentLlAttachment.addView(createCaptureEmptyChildView());
			}
		}
	}

	/**
	 * 退出当前界面
	 */
	public void back()
	{
		if (sendEtContent != null)
		{
			final boolean isContainAttachment = ((ImageView) sendAttachmentLlAttachment.getChildAt(0).findViewById(
					R.id.message_capture_image)).getTag() != null;
			if (sendEtContent.getText().toString().trim().length() > 0 || isContainAttachment)
			{
				commonDialog = new CommonDialog(getActivity());
				commonDialog.setCancelBtnText(getString(R.string.cancel))
						.setConfirmBtnText(getString(R.string.confirm))
						.setConfirmDialogListener(new DialogClickListener()
						{
							@Override
							public void onDialogClick()
							{
								getActivity().finish();
							}
						}).buildSimpleDialog(getString(R.string.message_sent_back));
			}
			else
			{
				// 事件统计 51 取消回复询盘 点击事件
				// SysManager.analysis(R.string.a_type_click, R.string.c51);
				getActivity().finish();
			}
		}
		else
		{
			return;
		}
	}

	/**
	 * 发送询盘
	 * 1.进行相关信息判断（发送按钮是否可以点击已经做了判断）
	 * 2.进行上传附件，获取对应的ID，并且加入对应的TAG
	 * 3.将附件上传后获得的ID进行拼接...添加到发送请求中
	 */
	protected Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case UPLOAD_FILE_COMPLETE:
				currentCaptureChildIndex = 0;
				// 上传成功后，发送询盘
				sendEmail();
				break;
			case POST_INQUIRY_FAILED:
				CommonProgressDialog.getInstance().dismissProgressDialog();
				ToastUtil.toast(getActivity(), R.string.message_sent_upload_file_false);
				break;
			}
		};
	};

	public void startUploadFile()
	{
		CommonProgressDialog.getInstance().showProgressDialog(getActivity(), getString(R.string.mic_loading));
		if (currentCaptureChildIndex >= sendAttachmentLlAttachment.getChildCount())
		{
			handler.sendEmptyMessage(UPLOAD_FILE_COMPLETE);
			return;
		}
		ImageView imageView = (ImageView) sendAttachmentLlAttachment.getChildAt(currentCaptureChildIndex).findViewById(
				R.id.message_capture_image);
		if (imageView.getTag() == null)
		{
			handler.sendEmptyMessage(UPLOAD_FILE_COMPLETE);
			return;
		}
		MessageContentFiles files = (MessageContentFiles) imageView.getTag();
		if (!Utils.isEmpty(files.fileId) || files.fileLocalPath == null)
		{
			currentCaptureChildIndex++;
			startUploadFile();
		}
		if (files.fileLocalPath != null)
		{
			RequestCenter.uploadFile(uploadFileListener, files.fileLocalPath, "messageAttachment");
		}
	}

	protected DisposeDataListener uploadFileListener = new DisposeDataListener()
	{
		@Override
		public void onSuccess(Object result)
		{
			activityfinishProtect();
			ImageView imageView = (ImageView) sendAttachmentLlAttachment.getChildAt(currentCaptureChildIndex)
					.findViewById(R.id.message_capture_image);
			MessageContentFiles files = (MessageContentFiles) imageView.getTag();
			MessageDetailUpLoadFile uploadFile = (MessageDetailUpLoadFile) result;
			if (files.fileId == null || "".equals(files.fileId))
			{
				files.fileId = uploadFile.content;
			}
			imageView.setTag(files);
			currentCaptureChildIndex++;
			startUploadFile();
		}

		@Override
		public void onDataAnomaly(Object failedReason)
		{
			CommonProgressDialog.getInstance().dismissProgressDialog();
			handler.sendEmptyMessage(POST_INQUIRY_FAILED);
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			CommonProgressDialog.getInstance().dismissProgressDialog();
			handler.sendEmptyMessage(POST_INQUIRY_FAILED);
		}
	};

	/**
	 * 获得上传附件的ids
	 * @return 将id以字符串进行拼接,以","号隔开
	 */
	protected String getAttachmentIds()
	{
		String attachmentIds = "";
		if (sendAttachmentLlAttachment.getChildCount() > 1)
		{
			MessageContentFiles filesTag = null;
			for (int i = 0; i < sendAttachmentLlAttachment.getChildCount(); i++)
			{
				filesTag = (MessageContentFiles) sendAttachmentLlAttachment.getChildAt(i)
						.findViewById(R.id.message_capture_image).getTag();
				if (filesTag != null)
				{
					attachmentIds = attachmentIds + filesTag.fileId + ",";
				}
			}
			attachmentIds = attachmentIds.substring(0, attachmentIds.length() - 1);
		}
		return attachmentIds;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.message_send_subject_iv_clear:
			sendSubjectEtSubject.setText("");
			break;
		// 设置监听，用户点击后，弹出PopWindows
		case R.id.message_capture_image:
			// SysManager.getInstance().dismissInputKey(getActivity());
			PopupManager.getInstance().showCapturePopup(createPopupView(createCapturePopupView(v), popupBgClick));
			fileDisplayView = fileAddView = v;
			MobclickAgent.onEvent(getActivity(), "36");
			SysManager.analysis(R.string.c_type_click, R.string.c036);
			break;
		// 用户点击图片右上角的删除按钮
		case R.id.message_capture_del_image:
			if (sendAttachmentLlAttachment != null)
			{
				if (sendAttachmentLlAttachment.getChildCount() > 1)
				{
					sendAttachmentLlAttachment.removeView((RelativeLayout) v.getParent());
					ImageView preImage = (ImageView) sendAttachmentLlAttachment.getChildAt(
							sendAttachmentLlAttachment.getChildCount() - 1).findViewById(R.id.message_capture_image);
					if (preImage.getDrawable() != null)
					{
						sendAttachmentLlAttachment.addView(createCaptureEmptyChildView());
					}
					else
					{
						if (sendAttachmentLlAttachment.getChildCount() == 1)
						{
							sendAttachmentLl.setVisibility(View.GONE);
						}
					}
				}
				else
				{
					((RelativeLayout) v.getParent()).findViewById(R.id.message_capture_image).setBackgroundResource(
							R.drawable.ic_message_capture_add);
					v.setVisibility(View.GONE);
				}
			}

			break;

		case R.id.common_ll_title_back:
			back();
			break;
		case R.id.common_title_function3:
			// 添加附件
			showPopup();
			MobclickAgent.onEvent(getActivity(), "34");
			SysManager.analysis(R.string.c_type_click, R.string.c034);
			break;
		case R.id.common_title_function4:
			// 添加快捷模板
			startActivityForShortCut();
			MobclickAgent.onEvent(getActivity(), "35");
			SysManager.analysis(R.string.c_type_click, R.string.c035);
			break;
		case R.id.common_title_function5:
			// 发送询盘
			startUploadFile();
			MobclickAgent.onEvent(getActivity(), "33");
			SysManager.analysis(R.string.c_type_click, R.string.c033);
			break;

		default:
			break;
		}

	}

	/**
	 * 监听字符串是否超过指定长度
	 */
	protected TextWatcher watcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			// 设置中文字数限制提醒
			if (s.length() >= Constants.REPLAY_MESSAGE_LENGTH)
			{
				ToastUtil.toast(getActivity(), R.string.message_reply_limit);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void afterTextChanged(Editable s)
		{
		}
	};

	@Override
	public void afterTextChanged(Editable s)
	{
		checkPostBtnStatus();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{

	}

	protected void checkPostBtnStatus()
	{
		sendSubjectIvClear.setVisibility(!"".equals(sendSubjectEtSubject.getText().toString()) ? View.VISIBLE
				: View.GONE);
		if (!"".equals(sendSubjectEtSubject.getText().toString().trim())
				&& !"".equals(sendEtContent.getText().toString().trim()))
		{
			onStatusChanged(true);
		}
		else
		{
			onStatusChanged(false);
		}
	}

	/**
	 * 修改发送按钮状态
	 * @param isEnabled
	 */
	public void onStatusChanged(boolean isEnabled)
	{
		if (isEnabled)
		{
			// 可以点击
			btnFunction5.setEnabled(true);
			btnFunction5.setImageResource(R.drawable.ic_message_post);
		}
		else
		{
			// 不可以点击
			btnFunction5.setEnabled(false);
			btnFunction5.setImageResource(R.drawable.ic_message_post_gray);
		}
	}

	protected void startSendBroadCast(String tag)
	{
		Intent intent = new Intent();
		intent.putExtra(tag + MessageConstantDefine.mailId.toString(), replyMailId);
		intent.setAction(MessageConstantDefine.getValue(MessageConstantDefine.BroadcastActionTag) + tag);
		getActivity().sendBroadcast(intent);
	}

}
