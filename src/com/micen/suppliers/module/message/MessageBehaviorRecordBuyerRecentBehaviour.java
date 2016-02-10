package com.micen.suppliers.module.message;

import java.util.ArrayList;

public class MessageBehaviorRecordBuyerRecentBehaviour
{
	public String peers;
	/**
	 * 常用的搜索关键词
	 */
	public ArrayList<String> recentHotKeywords;
	/**
	 * 常游览的目录
	 * 字段名需要确认
	 */
	public ArrayList<String> recentHotcategories;

	public ArrayList<MessageBehaviorRecordRFQ> rfq;
	/**
	 * 累计发送询盘
	 */
	public String sendInquiries;
	/**
	 * 给我发询盘
	 */
	public String sendInquiriesToMe;
	/**
	 * 给同行发询盘
	 */
	public String sendPeerInquiries;

}
