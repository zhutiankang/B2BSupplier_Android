package com.micen.suppliers.module;

import com.focustech.common.util.Utils;


public enum NotifySetStatus
{
	Unknown("unknown"), TotalSwitch("totalSwitch"), InquirySwitch("inquirySwitch"), PurchaseSwitch("purchaseSwitch"), OperationSwitch(
			"operationSwitch"), ServiceSwitch("serviceSwitch"), NotDisturbSwitch("notDisturbSwitch");

	private String value;

	private NotifySetStatus(String value)
	{
		this.value = value;
	}

	public static NotifySetStatus getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return Unknown;
		}
		for (NotifySetStatus target : NotifySetStatus.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return Unknown;
	}

	public static String getValue(NotifySetStatus target)
	{
		return target.value;
	}
}
