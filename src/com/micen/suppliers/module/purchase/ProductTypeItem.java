package com.micen.suppliers.module.purchase;


public class ProductTypeItem implements ProductFacet
{
	public String prodTypeNameEn;
	public String prodTypeNameCn;
	public String prodType;

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return prodTypeNameCn;
	}

	@Override
	public String getValue()
	{
		// TODO Auto-generated method stub
		return prodType;
	}

}
