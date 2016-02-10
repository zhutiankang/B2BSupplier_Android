package com.micen.suppliers.view.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.focustech.common.util.Utils;
import com.micen.suppliers.R;
import com.micen.suppliers.module.product.ProductDetailContent;
import com.micen.suppliers.module.product.ProductKeyValuePair;
import com.micen.suppliers.util.ImageUtil;
import com.micen.suppliers.util.Util;
import com.micen.suppliers.widget.product.NotifyScrollView;
import com.micen.suppliers.widget.product.NotifyScrollView.OnScrollChangedListener;
import com.micen.suppliers.widget.product.UnitPriceView;

public class ProductFragment extends Fragment
{
	protected ProductDetailContent detail;

	private NotifyScrollView notifyScrollView;

	private TextView nameText;

	private TextView minOrderText;

	private TextView singlePriceText1;

	private TextView singlePriceText2;

	private LinearLayout unitPricesLayout;

	private RelativeLayout imageLayout;

	private LinearLayout tradeInfoContainer;

	private LinearLayout basicInfoContainer;

	private LinearLayout additionalInfoContainer;

	private LinearLayout singlePriceLayout;

	private LinearLayout multiPriceLayout;

	private View titleView;

	private static final float MIN_ALPHA_RATIO = 0.5F;

	public ProductFragment()
	{

	}

	private OnScrollChangedListener onScrollChangedListener = new OnScrollChangedListener()
	{

		@Override
		public void onScrollChanged(NotifyScrollView who, int l, int t, int oldl, int oldt)
		{
			setTitleAlpha(t);
		}

	};

	private void setTitleAlpha(int t)
	{
		if (titleView == null)
		{
			return;
		}
		final int headerHeight = Math.abs(imageLayout.getHeight() - titleView.getHeight());
		final int minHeight = (int) (headerHeight * MIN_ALPHA_RATIO);
		final float ratio = (float) Math.min(Math.max(minHeight + t, minHeight), headerHeight) / headerHeight;
		final float finalRatio = Math.max(MIN_ALPHA_RATIO, ratio);
		final int newAlpha = (int) (finalRatio * 255);
		titleView.getBackground().setAlpha(newAlpha);
	}

	public ProductFragment(ProductDetailContent detail, View titleView)
	{
		this.detail = detail;
		this.titleView = titleView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View root = inflater.inflate(R.layout.fragment_product_detail_layout, null);
		notifyScrollView = (NotifyScrollView) root.findViewById(R.id.notify_scroll_view);
		nameText = (TextView) root.findViewById(R.id.tv_product_detail_name);
		minOrderText = (TextView) root.findViewById(R.id.tv_min_order);
		unitPricesLayout = (LinearLayout) root.findViewById(R.id.unit_prices);
		imageLayout = (RelativeLayout) root.findViewById(R.id.product_image_layout);
		tradeInfoContainer = (LinearLayout) root.findViewById(R.id.trade_info_container);
		basicInfoContainer = (LinearLayout) root.findViewById(R.id.basic_info_container);
		additionalInfoContainer = (LinearLayout) root.findViewById(R.id.additional_info_container);
		singlePriceLayout = (LinearLayout) root.findViewById(R.id.single_price_layout);
		multiPriceLayout = (LinearLayout) root.findViewById(R.id.multi_price_layout);
		singlePriceText1 = (TextView) root.findViewById(R.id.tv_single_price_1);
		singlePriceText2 = (TextView) root.findViewById(R.id.tv_single_price_2);
		notifyScrollView.setOnScrollChangedListener(onScrollChangedListener);
		init();

		return root;
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		// 事件统计$10023 产品详情页 页面事件
		// SysManager.analysis(R.string.a_type_page, R.string.p10023);
	}

	private void init()
	{
		setTitleAlpha(0);
		nameText.setText(detail.name);
		initImageLayout();
		initOrderLayout();
		initBasicLayout();
		initAddtionalLayout();
	}

	private void initAddtionalLayout()
	{
		for (ProductKeyValuePair item : detail.additionalInfoList)
		{
			View paramLayout = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_product_param, null);
			TextView param1Text = (TextView) paramLayout.findViewById(R.id.tv_param1);
			TextView param2Text = (TextView) paramLayout.findViewById(R.id.tv_param2);
			param1Text.setText(Html.fromHtml(item.key + ":"));
			param2Text.setText(Html.fromHtml(Util.replaceHtmlStr(String.valueOf(item.value))));
			additionalInfoContainer.addView(paramLayout);
		}
	}

	private void initBasicLayout()
	{
		for (ProductKeyValuePair item : detail.basicInfoList)
		{
			View paramLayout = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_product_param, null);
			TextView param1Text = (TextView) paramLayout.findViewById(R.id.tv_param1);
			TextView param2Text = (TextView) paramLayout.findViewById(R.id.tv_param2);
			param1Text.setText(Html.fromHtml(item.key + ":"));
			param2Text.setText(Html.fromHtml(Util.replaceHtmlStr(String.valueOf(item.value))));
			basicInfoContainer.addView(paramLayout);
		}
	}

	private void initImageLayout()
	{
		// // 获取到详细页展示的第一章图片uri
		// SharedPreferenceManager.getInstance().putString("thumbUri", detail.images.get(0));
		// 创建Imageview,添加到imageLayout 中
		ImageView image = new ImageView(getActivity());
		image.setId(R.id.message_capture_image);
		RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, Utils.toDip(
				getActivity(), 280));
		relParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		image.setLayoutParams(relParams);
		ImageUtil.getImageLoader().displayImage(detail.images.get(0), image, ImageUtil.getProductImageOptions());
		imageLayout.addView(image);
	}

	/**
	 * Initialize product order layout
	 */
	private void initOrderLayout()
	{
		String orderUnit = null;
		if (detail != null && detail.minOrderInfo != null)
		{
			orderUnit = detail.minOrderInfo.orderUnit;
		}
		if (Utils.isEmpty(orderUnit))
		{
			orderUnit = "";
		}
		minOrderText.setText(getString(R.string.product_order_unit, orderUnit));

		initPricesLayout();

		initTradeLayout();
	}

	private void initTradeLayout()
	{
		ArrayList<ProductKeyValuePair> tradeValueList = new ArrayList<ProductKeyValuePair>();
		for (ProductKeyValuePair item : detail.tradeInfoList)
		{
			if (filterInvalidKey(item.key) && !Utils.isEmpty(item.value.toString()))
			{
				tradeValueList.add(new ProductKeyValuePair(item.key, item.value));
			}
		}

		for (ProductKeyValuePair item : tradeValueList)
		{
			View paramLayout = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_product_param, null);
			TextView param1Text = (TextView) paramLayout.findViewById(R.id.tv_param1);
			TextView param2Text = (TextView) paramLayout.findViewById(R.id.tv_param2);
			param1Text.setText(Html.fromHtml(item.key + ":"));
			param2Text.setText(Html.fromHtml(Util.replaceHtmlStr(String.valueOf(item.value))));
			tradeInfoContainer.addView(paramLayout);
		}
	}

	private static final String[] invalidTradeInfoKeys =
	{ "ProdPrice", "splitUnitPrice", "ProdPriceUnit", "orderUnit", "unitPrice", "Price" };

	private boolean filterInvalidKey(String key)
	{
		for (String invalidKey : invalidTradeInfoKeys)
		{
			if (invalidKey.equals(key))
				return false;
		}
		return true;
	}

	private void initPricesLayout()
	{
		singlePriceText2.setSelected(true);
		try
		{
			if (detail != null && detail.minOrderInfo != null && detail.minOrderInfo.splitUnitPrice != null)
			{
				ArrayList<String> splitUnitPrice = detail.minOrderInfo.splitUnitPrice;
				if (!splitUnitPrice.isEmpty())
				{
					String[] price;
					if (splitUnitPrice.size() == 1)
					{
						price = splitUnitPrice.get(0).split(":");
						if (price.length == 2)
						{
							singlePriceLayout.setVisibility(View.VISIBLE);
							singlePriceText1.setText(price[0]);
							SpannableString builder = new SpannableString("$" + price[1]);
							builder.setSpan(new AbsoluteSizeSpan(10, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
							singlePriceText2.setText(builder);
						}
					}
					else
					{
						Collections.sort(splitUnitPrice, new Comparator<String>()
						{

							@Override
							public int compare(String lhs, String rhs)
							{
								String[] lhsSplit = lhs.split(":");
								String[] rhsSplit = rhs.split(":");
								if (lhsSplit.length > 0 && rhsSplit.length > 0 && TextUtils.isDigitsOnly(lhsSplit[0])
										&& TextUtils.isDigitsOnly(rhsSplit[0]))
								{
									return Integer.parseInt(lhsSplit[0]) - Integer.parseInt(rhsSplit[0]);
								}
								return -1;
							}
						});
						multiPriceLayout.setVisibility(View.VISIBLE);
						for (int i = 0; i < splitUnitPrice.size(); i++)
						{
							price = splitUnitPrice.get(i).split(":");
							if (price.length == 2)
							{
								UnitPriceView view = new UnitPriceView(getActivity(), price[0], price[1]);
								unitPricesLayout.addView(view);
							}
						}
					}
				}
				else
				{
					singlePriceLayout.setVisibility(View.VISIBLE);
					singlePriceText1.setText(R.string.negotiable);
					singlePriceText2.setVisibility(View.GONE);
				}
			}
			else
			{
				singlePriceLayout.setVisibility(View.VISIBLE);
				singlePriceText1.setText(R.string.negotiable);
				singlePriceText2.setVisibility(View.GONE);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
