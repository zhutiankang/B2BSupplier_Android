package com.micen.suppliers.module.qrcode;

import com.focustech.common.util.Utils;

public enum QRCodeTypeDefine
{
	None("none"), Sign("1"), Login("2"), Uri("3");

	private String value;

	private QRCodeTypeDefine(String value)
	{
		this.value = value;
	}

	public static QRCodeTypeDefine getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return None;
		}
		for (QRCodeTypeDefine target : QRCodeTypeDefine.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return None;
	}

	public static String getValue(QRCodeTypeDefine target)
	{
		return target.value;
	}
}
