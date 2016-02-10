package com.micen.suppliers.module.message;

import java.util.ArrayList;

public class Message
{
	public ArrayList<MessageContent> mail;
	/**
	 * 已分配邮件数
	 */
	public String allocateNum;

	/**
	 * 最近三个月的询盘数
	 */
	public String recentMailNum;
	/**
	 *  未分配邮件数
	 */
	public String unAllocateNum;
}
