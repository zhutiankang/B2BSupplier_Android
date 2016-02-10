package com.micen.suppliers.module;

import com.focustech.common.util.Utils;


public enum SupplierAuthentication
{
	None("0"), AuditSupplier("1"), OnsiteCheck("2"), LicenseVerify("3");

	private String value;

	private SupplierAuthentication(String value)
	{
		this.value = value;
	}

	public static SupplierAuthentication getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return None;
		}
		for (SupplierAuthentication target : SupplierAuthentication.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return None;
	}

	public static String getValue(SupplierAuthentication target)
	{
		return target.value;
	}
}
