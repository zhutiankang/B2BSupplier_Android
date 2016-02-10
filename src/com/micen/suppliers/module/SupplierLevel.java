package com.micen.suppliers.module;

import com.focustech.common.util.Utils;


public enum SupplierLevel
{
	Unknown("-1"), Free("0"), Cooperate("5"), Higher("30");

	private String value;

	private SupplierLevel(String value)
	{
		this.value = value;
	}

	public static SupplierLevel getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return Unknown;
		}
		for (SupplierLevel target : SupplierLevel.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return Unknown;
	}

	public static String getValue(SupplierLevel target)
	{
		return target.value;
	}
}
