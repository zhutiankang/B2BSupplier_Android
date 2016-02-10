package com.micen.suppliers.module.message;

import com.focustech.common.util.Utils;



public enum MessageConstantDefine
{
	None("none"),
	/**
	 *询盘ID：mailId/通讯中的ID
	 */
	businessId("businessId"), companyId("companyId"),
	/**
	 * 发/收件箱或者通讯录
	 */
	sourceType("sourceType"),
	/**
	 * 是否允许添加该买家
	 */
	allowAdditions("allowAdditions"),
	/**
	 * 是否需要屏蔽界面中部分功能
	 * 适用于 买家行为记录
	 * 屏蔽上一封、下一封、分配、回复、删除、买家行为记录
	 */
	isNeedToShieldFunction("isNeedToShieldFunction"),
	/**
	 * 询盘修改标识
	 */
	delectMessage("delectMessage"),
	/**
	 * 是否为报价入口
	 * 屏蔽上一封，下一封显示
	 */
	isPurchase("isPurchase"),
	/**
	 * 是否允许删除
	 */
	isAllowDelete("isAllowDelete"),
	/**
	 * 甄别回复/发送
	 */
	messageSendTarget("messageSendTarget"),
	/**
	 * 邮箱标识
	 */
	messageEmail("messageEmail"),
	/**
	 * 邮件Id
	 */
	mailId("mailId"),
	/**
	 * 收/发邮箱标识
	 */
	action("action"),

	isInsertDB("isInsertDB"),

	shortCutKey("shortCutKey"),

	BroadcastActionTag("com.made-in-china."),

	contactsPersion("contactsPersion"),
	/**
	 * 首页进入询盘--全部--未读标识
	 */
	checkUnReadMessage("checkUnReadMessage"),

	// 广播所用字段
	/**
	 * 分配
	 */
	distributed("distributed"),
	/**
	 * 分配业务员ID
	 */
	distributedOperatorId("distributedOperatorId"),
	/**
	 *阅读询盘
	 */
	read("read"),
	/**
	 *删除询盘
	 */
	delete("delete"),
	/**
	 *回复询盘
	 */
	reply("reply"),
	/**
	 *通讯录删除
	 */
	deleteContacts("deleteContacts"),
	/**
	 *通讯录删除位置
	 */
	deleteContactsPosition("deleteContactsPosition"),
	/**
	 * 询盘详情页查看大图url
	 */
	attachmentImageUrl("attachmentImageUrl");

	private String value;

	private MessageConstantDefine(String value)
	{
		this.value = value;
	}

	public static MessageConstantDefine getValueByTag(String type)
	{
		if (Utils.isEmpty(type))
		{
			return None;
		}
		for (MessageConstantDefine target : MessageConstantDefine.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return None;
	}

	public static String getValue(MessageConstantDefine target)
	{
		return target.value;
	}
}
