package com.micen.suppliers.module.purchase;


public class ProductGroupItem implements ProductFacet
{
	public String num;
	public String groupName;
	public String groupId;
	public String encryptFlag;
	public String groupPassword;

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return groupName;
	}

	@Override
	public String getValue()
	{
		// TODO Auto-generated method stub
		return groupId;
	}

}
