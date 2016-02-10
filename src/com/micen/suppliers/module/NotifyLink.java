package com.micen.suppliers.module;

import com.focustech.common.util.Utils;


public enum NotifyLink
{
	Unknown("-1"), InquiryDetail("4"), Purchase("7"), WebAddress("8"), Update("9");

	private String value;

	private NotifyLink(String value)
	{
		this.value = value;
	}

	public static NotifyLink getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return Unknown;
		}
		for (NotifyLink target : NotifyLink.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return Unknown;
	}

	public static String getValue(NotifyLink target)
	{
		return target.value;
	}
}
