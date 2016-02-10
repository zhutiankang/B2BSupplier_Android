package com.micen.suppliers.module.message;

import java.util.ArrayList;

public class MessageDetail
{
	/**
	 * 分配操作标识
	 */
	public String allocateOperatorId;

	/**
	 * 附件数量
	 */
	public String attachmentNum;
	/**
	 * 附件集合
	 */
	public ArrayList<MessageDetailsAttachmentList> attachments;

	/**
	 * 操作时间
	 */
	public String date;

	/**
	 * 是否已经分配
	 */
	public String isAllocate;

	/**
	 * 是否未读
	 */
	public String isUnread;

	/**
	 * 邮件内容
	 */
	public String mailContent;

	/**
	 * 邮件标识
	 */
	public String mailId;

	/**
	 * 产品ID
	 */
	public String productId;

	/**
	 * 产品缩略图URL
	 */
	public String productImageUrl;

	/**
	 * 产品名称
	 */
	public String productName;
	/**
	 * 产品状态标识 1：可查看
	 */
	public String viewFlag;
	/**
	 * 产品状态说明
	 */
	public String productStatus;

	/**
	 * 收件人对象
	 */
	public MessageDetailReceiver receiver;
	/**
	 * 发件人对象
	 */
	public MessageDetailSender sender;
	/**
	 * 主题
	 */
	public String subject;

}
