package com.micen.suppliers.view.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.focustech.common.listener.DisposeDataListener;
import com.focustech.common.universalimageloader.core.assist.FailReason;
import com.focustech.common.universalimageloader.core.listener.ImageLoadingListener;
import com.focustech.common.util.ToastUtil;
import com.focustech.common.widget.dialog.CommonProgressDialog;
import com.micen.suppliers.R;
import com.micen.suppliers.application.SupplierApplication;
import com.micen.suppliers.http.RequestCenter;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.widget.CircleImageView;


public class QRCodeSignInFragment extends Fragment implements OnClickListener
{

	private RelativeLayout rlCommonTitle;
	private LinearLayout llTitleBack;
	private TextView titlteTvName;
	private ImageView titleIvBack;
	private TextView signTvUserName;
	private CircleImageView signIvUserLogo;
	private TextView signTvUserCompanyName;
	private TextView signTvTopicName;
	private TextView signTvSign;

	private String activityId;
	private String topicName;

	public static QRCodeSignInFragment newInstance(String activityId, String topicName)
	{
		QRCodeSignInFragment fragment = new QRCodeSignInFragment();
		Bundle bundle = new Bundle();
		bundle.putString("activityId", activityId);
		bundle.putString("topicName", topicName);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Bundle bundle = getArguments();
		activityId = bundle.getString("activityId");
		topicName = bundle.getString("topicName");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_qrcode_sign_in, null);
		initView(view);
		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		initViewData();
		super.onActivityCreated(savedInstanceState);
	}

	private void initViewData()
	{
		signTvTopicName.setText(topicName);
		signTvUserName.setText(SupplierApplication.getInstance().getUser().content.userInfo.fullName + "　"
				+ SupplierApplication.getInstance().getUser().content.userInfo.getGenderContent());
		signTvUserCompanyName.setText(SupplierApplication.getInstance().getUser().content.companyInfo.companyName);
		ImageUtil.getImageLoader().displayImage(SupplierApplication.getInstance().getUser().content.companyInfo.logo,
				signIvUserLogo, new ImageLoadingListener()
				{
					@Override
					public void onLoadingStarted(String imageUri, View view)
					{
						signIvUserLogo.setImageResource(R.drawable.ic_head_sign_in_default);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason)
					{
						signIvUserLogo.setImageResource(R.drawable.ic_head_sign_in_default);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
					{
						if (loadedImage != null)
						{
							signIvUserLogo.setImageBitmap(loadedImage);
						}
						else
						{
							signIvUserLogo.setImageResource(R.drawable.ic_head_sign_in_default);
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view)
					{

					}
				});

	}

	private void initView(View view)
	{
		rlCommonTitle = (RelativeLayout) view.findViewById(R.id.rl_common_title);
		llTitleBack = (LinearLayout) view.findViewById(R.id.common_ll_title_back);
		titleIvBack = (ImageView) view.findViewById(R.id.common_title_back);
		signIvUserLogo = (CircleImageView) view.findViewById(R.id.qrcode_sign_iv_user_logo);
		titlteTvName = (TextView) view.findViewById(R.id.common_title_name);
		signTvUserName = (TextView) view.findViewById(R.id.qrcode_sign_tv_user_name);
		signTvUserCompanyName = (TextView) view.findViewById(R.id.qrcode_sign_ll_user_company_name);
		signTvTopicName = (TextView) view.findViewById(R.id.qrcode_sign_tv_topic_name);
		signTvSign = (TextView) view.findViewById(R.id.qrcode_sign_ll_sign);
		titleIvBack.setBackgroundResource(R.drawable.ic_title_back_blue);
		titlteTvName.setText(getResources().getString(R.string.qrcode_title_name_sign_in));
		titlteTvName.setTextColor(getResources().getColor(R.color.color_333333));
		rlCommonTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));
		llTitleBack.setOnClickListener(this);
		signTvSign.setOnClickListener(this);
		llTitleBack.setBackgroundResource(R.drawable.bg_common_white_btn);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
			break;
		case R.id.qrcode_sign_ll_sign:
			startSendSignIn();
			break;

		default:
			break;
		}

	}

	private void startSendSignIn()
	{
		CommonProgressDialog.getInstance().showCancelableProgressDialog(getActivity(),
				getString(R.string.qrcode_sign_in_sending));
		RequestCenter.sendSignIn(getActivity(), activityId, signInListener);
		signTvSign.setClickable(false);
	}

	DisposeDataListener signInListener = new DisposeDataListener()
	{

		@Override
		public void onSuccess(Object obj)
		{
			dismissDialogAndSetClickable(false);
			// TODO Auto-generated method stub
			signTvSign.setText(R.string.qrcode_sign_in_success);
			ToastUtil.toast(getActivity(), R.string.qrcode_sign_in_success);
		}

		@Override
		public void onDataAnomaly(Object anomalyMsg)
		{
			dismissDialogAndSetClickable(true);
			// TODO Auto-generated method stub
			ToastUtil.toast(getActivity(), anomalyMsg);
		}

		@Override
		public void onNetworkAnomaly(Object anomalyMsg)
		{
			ToastUtil.toast(getActivity(), anomalyMsg);
			dismissDialogAndSetClickable(true);
		}

		private void dismissDialogAndSetClickable(boolean clickable)
		{
			CommonProgressDialog.getInstance().dismissProgressDialog();
			signTvSign.setClickable(clickable);
		}

	};

}
