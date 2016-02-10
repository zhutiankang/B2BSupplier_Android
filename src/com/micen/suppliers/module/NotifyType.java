package com.micen.suppliers.module;

import com.focustech.common.util.Utils;


public enum NotifyType
{
	Unknown("-1"), Inquiry("1"), Purchase("2"), Service("4"), NotDisturb("5");

	private String value;

	private NotifyType(String value)
	{
		this.value = value;
	}

	public static NotifyType getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return Unknown;
		}
		for (NotifyType target : NotifyType.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return Unknown;
	}

	public static String getValue(NotifyType target)
	{
		return target.value;
	}
}
