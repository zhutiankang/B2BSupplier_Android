package com.micen.suppliers.activity.message;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.focustech.common.universalimageloader.core.ImageLoader;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.BaseActivity;
import com.micen.suppliers.module.message.MessageConstantDefine;
import com.micen.suppliers.util.ImageUtil;

public class MessageAttachmentImageBrowserActivity extends BaseActivity implements OnClickListener
{
	private ImageView attachmentImageBrowser;
	private String imageUrl;
	private ImageView btBack;
	private RelativeLayout rlCommonTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_details_attachment_image_browser);
		initNavigationBarStyle(true);
		initIntent();
		initView();
		initData();
	}

	private void initIntent()
	{
		imageUrl = getIntent().getStringExtra(MessageConstantDefine.attachmentImageUrl.toString());
	}

	private void initData()
	{
		ImageLoader.getInstance().displayImage(imageUrl, attachmentImageBrowser, ImageUtil.getProductImageOptions());
	}

	private void initView()
	{
		btBack = (ImageView) findViewById(R.id.common_title_back);
		attachmentImageBrowser = (ImageView) findViewById(R.id.attachment_image_browser);
		rlCommonTitle = (RelativeLayout) findViewById(R.id.rl_common_title);
		rlCommonTitle.setBackgroundColor(getResources().getColor(R.color.color_ffffff));
		btBack.setImageResource(R.drawable.ic_title_back_blue);
		llBack.setOnClickListener(this);
		llBack.setBackgroundResource(R.drawable.btn_white_common_btn);
		attachmentImageBrowser.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		switch (v.getId())
		{
		case R.id.common_ll_title_back:
		case R.id.attachment_image_browser:
			this.finish();
			break;

		default:
			break;
		}
	}

}
