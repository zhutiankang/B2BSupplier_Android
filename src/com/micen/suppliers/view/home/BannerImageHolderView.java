package com.micen.suppliers.view.home;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.focustech.common.widget.circulatebanner.CBPageAdapter;
import com.micen.suppliers.R;
import com.micen.suppliers.activity.WebViewActivity_;
import com.micen.suppliers.manager.SysManager;
import com.micen.suppliers.module.WebViewType;
import com.micen.suppliers.module.promotion.SystemPromotionBanner;
import com.micen.suppliers.util.ImageUtil;
import com.umeng.analytics.MobclickAgent;


public class BannerImageHolderView implements CBPageAdapter.Holder<SystemPromotionBanner>
{
	private LayoutParams params;
	private ImageView imageView;

	@Override
	public View createView(Context context)
	{
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		imageView = new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setLayoutParams(params);
		return imageView;
	}

	@Override
	public void UpdateUI(final Context context, final int position, final SystemPromotionBanner data)
	{
		ImageUtil.getImageLoader().displayImage(data.imageSrc, imageView, ImageUtil.getImageOptions());
		imageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(context, WebViewActivity_.class);
				intent.putExtra("targetUri", data.detailUrl);
				intent.putExtra("targetType", WebViewType.getValue(WebViewType.Discovery));
				context.startActivity(intent);
				MobclickAgent.onEvent(context, "7");
				SysManager.analysis(R.string.c_type_click, R.string.c007);
			}
		});
	}
}
