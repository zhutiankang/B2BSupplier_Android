package com.micen.suppliers.module.user;

import java.io.Serializable;


public class UserInfo implements Serializable
{
	/**
	 * 备用Email
	 */
	public String backEmail;
	/**
	 * 所在部门
	 */
	public String department;
	/**
	 * 电子邮箱地址
	 */
	public String email;
	/**
	 * 用户名称
	 */
	public String fullName;
	/**
	 * 性别称谓：
	 * 	0：Mr.
	 * 	1：Mrs.
	 *	2：Ms.
	 *	3：Miss
	 */
	public String gender;
	/**
	 * 邮箱是否验证：true验证
	 */
	public String isEmailConfirmed;
	/**
	 * 移动电话
	 */
	public String mobile;
	/**
	 * 职位
	 */
	public String position;
	/**
	 * 未读询盘邮件
	 */
	public String unreadMail;
	/**
	 * 未读报价条数 
	 */
	public String unreadRFQ;
	/**
	 * 用户角色（发邮件时用）
	 */
	public String userRole;
	public String manageGroupFlag;
	public String manageAllProdFlag;
	public String receiveMessageFlag;

	/**
	 * 是否管理员
	 * @return
	 */
	public boolean isManager()
	{
		return "0".equals(userRole);
	}

	/**
	 * 是否有询盘权限
	 * @return
	 */
	public boolean isReceiveInquiry()
	{
		return "1".equals(receiveMessageFlag);
	}

	public String getGenderContent()
	{
		String genderTag = "";
		switch (UserGenderType.getValueByTag(gender))
		{
		case Mr:
			genderTag = "先生";
			break;
		case Mrs:
			genderTag = "夫人";
			break;
		case Ms:
			genderTag = "女士";
			break;
		case Miss:
			genderTag = "女士";
			break;
		default:
			break;
		}
		return genderTag;
	}
}
