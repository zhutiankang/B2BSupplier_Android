package com.micen.suppliers.module.product;

import java.io.Serializable;
import java.util.ArrayList;

public class CompanyContent implements Serializable
{
	private static final long serialVersionUID = -3857231146684541879L;
	public String companyAddress;
	public String department;
	public String memberType;
	public String annualTurnover;
	public String auditTimes;
	public String city;
	public String auditType;
	public String description;
	public String zipCode;
	public String isFavorite;
	public String province;
	public String isVIP;
	public String sence;
	public String memberSince;
	public String logo;
	public String businessScope;
	public String fax;
	public String lastLoginDate;
	public String updateTime;
	public String businessType;
	public String companyName;
	public String homepage;
	public String country;
	public String trademark;
	public String companyId;
	public String employeeNumber;
	public String telephone;
	public String contactPerson;
	public String mobile;
	public String position;
	public String mainProducts;
	public ArrayList<CompanyProductGroup> productGroup;

	public boolean isFavorite()
	{
		return "true".equals(isFavorite);
	}
}
