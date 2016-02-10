package com.micen.suppliers.module;

import com.focustech.common.util.Utils;


public enum WebViewType
{
	UnKnow("-1"), Apply("0"), AboutUs("1"), Service("2"), Discovery("3");

	private String value;

	private WebViewType(String value)
	{
		this.value = value;
	}

	public static WebViewType getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return UnKnow;
		}
		for (WebViewType target : WebViewType.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return UnKnow;
	}

	public static String getValue(WebViewType target)
	{
		return target.value;
	}
}
