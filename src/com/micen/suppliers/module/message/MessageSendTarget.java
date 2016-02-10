package com.micen.suppliers.module.message;

import com.focustech.common.util.Utils;


public enum MessageSendTarget
{
	None("none"), Reply("reply"), Send("send"), SendByCatcode("catCode"), SendByProductId("productId"), SendByCompanyId(
			"companyId");

	private String value;

	private MessageSendTarget(String value)
	{
		this.value = value;
	}

	public static MessageSendTarget getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return None;
		}
		for (MessageSendTarget target : MessageSendTarget.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return None;
	}

	public static String getValue(MessageSendTarget target)
	{
		return target.value;
	}
}
