package com.micen.suppliers.module.user;

import java.io.Serializable;


public class CompanyInfo implements Serializable
{
	/**
	 * 年营业额
	 */
	public String annualTurnover;
	/**
	 * 交易范围
	 */
	public String businessRange;
	/**
	 * 交易类型
	 */
	public String businessType;
	/**
	 * 城市
	 */
	public String city;
	/**
	 * 地址
	 */
	public String companyAddress;
	/**
	 * 所在公司Id
	 */
	public String companyId;
	/**
	 * 操作者状态码
	 */
	public String operatorId;
	/**
	 * 公司标识（发邮件时需要）
	 */
	public String companyIdentity;
	/**
	 * 公司名
	 */
	public String companyName;
	/**
	 * 公司状态：
	 *	0：New
	 *	1：Approved
	 *	2：Rejected
	 *	3：Revised
	 *	4：Suspended
	 *	5：Deleted
	 */
	public String companyStat;
	/**
	 * 国家
	 */
	public String country;
	/**
	 * 公司简介
	 */
	public String description;
	/**
	 * 雇员数
	 */
	public String employeeNumber;
	/**
	 * 传真
	 */
	public String fax;
	/**
	 * 主页URL
	 */
	public String homepage;
	/**
	 * 是否是准会员(发邮件需要)
	 */
	public String isBeforePremium;
	/**
	 * 公司logo url
	 */
	public String logo;
	/**
	 * 公司级别
	 * 0：免费供应商
	 * 5：合作会员
	 * 30：高级供应商
	 */
	public String csLevel;
	/**
	 * 认证类型
	 * 0：无认证
	 * 1：AuditSupplier 认证
	 * 2：OnsiteCheck 认证
	 * 3: LicenseVerify 认证
	 */
	public String auditType;
	/**
	 * 产品关键字
	 */
	public String productKeyword;
	/**
	 * 省份
	 */
	public String province;
	public String systemCertification;
	/**
	 * 电话号码
	 */
	public String telephone;
	/**
	 * 时区
	 */
	public String timeZone;
	/**
	 * 交易市场
	 */
	public String trademark;
	/**
	 * 邮编
	 */
	public String zipCode;
}
