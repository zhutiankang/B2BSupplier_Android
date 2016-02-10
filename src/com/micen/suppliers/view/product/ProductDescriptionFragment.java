package com.micen.suppliers.view.product;

import java.io.IOException;
import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ZoomButtonsController;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.product.ProductDetailContent;
import com.micen.suppliers.util.CreateFiles;
import com.micen.suppliers.widget.product.NotifyWebView;
import com.micen.suppliers.widget.product.NotifyWebView.OnScrollChangedListener;


public class ProductDescriptionFragment extends Fragment implements OnClickListener
{
	private ProductDetailContent detail;
	private NotifyWebView descWebView;
	private ImageView scrollTopView;
	private OnScrollChangedListener scrollViewOnScrollChangedListener = new OnScrollChangedListener()
	{
		@Override
		public void onScrollChanged(NotifyWebView who, int l, int t, int oldl, int oldt)
		{
			scrollTopView.setVisibility(t == 0 ? View.GONE : View.VISIBLE);
		}
	};

	public ProductDescriptionFragment()
	{
	}

	public ProductDescriptionFragment(ProductDetailContent detail)
	{
		this.detail = detail;
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_product_description_layout, null);

		CreateFiles cf = new CreateFiles(getActivity());
		try
		{
			cf.createCacheFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		cf.print(detail.descriptionInfo);

		descWebView = (NotifyWebView) v.findViewById(R.id.wb_description);

		scrollTopView = (ImageView) v.findViewById(R.id.iv_scroll_top);
		scrollTopView.setOnClickListener(this);
		scrollTopView.setVisibility(View.GONE);
		// descWebView.loadUrl("file:///android_asset/ar_as/11.html");
		descWebView.getSettings().setSupportZoom(true);
		descWebView.getSettings().setBuiltInZoomControls(true);
		descWebView.getSettings().setJavaScriptEnabled(true);
		// descWebView.getSettings().setUseWideViewPort(true);
		if (Build.VERSION.SDK_INT > 10)
		{
			descWebView.getSettings().setDisplayZoomControls(false);
		}
		else
		{
			Class classType;
			Field field;
			try
			{
				classType = WebView.class;
				field = classType.getDeclaredField("mZoomButtonsController");
				field.setAccessible(true);
				ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(descWebView);
				mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
				try
				{
					field.set(descWebView, mZoomButtonsController);
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			}

		}
		// descWebView.getSettings().setUseWideViewPort(true);
		// descWebView.getSettings().setLoadWithOverviewMode(true);
		descWebView.loadUrl("file:///android_asset/ar_as/no-product-description.html");
		descWebView.setOnScrollChangedListener(scrollViewOnScrollChangedListener);
		if (!Utils.isEmpty(detail.descriptionInfo))
		{
			descWebView.loadUrl("file:///" + cf.getCacheFilePath());
		}
		else
		{
			descWebView.loadUrl("file:///android_asset/ar_as/no-product-description.html");
		}
		return v;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.iv_scroll_top:
			descWebView.scrollTo(10, 10);
			scrollTopView.setVisibility(View.INVISIBLE);
			break;
		}
	}
}
