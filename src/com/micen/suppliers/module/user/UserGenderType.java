package com.micen.suppliers.module.user;

import com.focustech.common.util.Utils;

public enum UserGenderType
{
	None("none"), Mr("0"), Mrs("1"), Ms("2"), Miss("3");
	private String value;

	private UserGenderType(String value)
	{
		this.value = value;
	}

	public static UserGenderType getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return None;
		}
		for (UserGenderType target : UserGenderType.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return None;
	}

	public static String getValue(UserGenderType target)
	{
		return target.value;
	}

}
