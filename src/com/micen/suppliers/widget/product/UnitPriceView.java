package com.micen.suppliers.widget.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.micen.suppliers.R;

public class UnitPriceView extends LinearLayout
{
	
	private TextView minOrderText;
	
	private TextView priceText;
	
	private String minOrder;
	
	private String price;

	public UnitPriceView(Context context, String minOrder, String price)
	{
		super(context);
		this.minOrder = minOrder;
		this.price = price;
		init();
	}

	private void init()
	{
		LayoutInflater.from(getContext()).inflate(R.layout.list_item_product_unit_price, this);
		
		minOrderText = (TextView) findViewById(R.id.min_order);
		priceText = (TextView) findViewById(R.id.price);
		
		minOrderText.setText(minOrder);
		priceText.setText(price);
	}

}
