package com.micen.suppliers.module.user;

import java.io.Serializable;
import java.util.ArrayList;

public class UserContent implements Serializable
{
	public UserInfo userInfo;
	public CompanyInfo companyInfo;
	public FavoriteInfo favoriteInfo;
	public ArrayList<ServiceInfo> advancedService;
	public String serviceCode;
	public String findChannelUrl;
}
