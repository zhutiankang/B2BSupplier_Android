package com.micen.suppliers.module.purchase;


public class ProductMasterItem implements ProductFacet
{
	public String userName;
	public String operatorId;

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return userName;
	}

	@Override
	public String getValue()
	{
		// TODO Auto-generated method stub
		return operatorId;
	}
}
