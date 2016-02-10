package com.micen.suppliers.module.purchase;


public class ProductStarItem implements ProductFacet
{
	public String prodStarNameEn;
	public String prodStarNameCn;
	public String prodStar;

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return prodStarNameCn;
	}

	@Override
	public String getValue()
	{
		// TODO Auto-generated method stub
		return prodStar;
	}

}
