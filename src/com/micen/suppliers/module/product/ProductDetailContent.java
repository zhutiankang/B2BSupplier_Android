package com.micen.suppliers.module.product;

import java.util.ArrayList;

public class ProductDetailContent
{
	public String additionalInfo;
	public String basicInfo;
	public String tradeInfo;
	public String auditType;
	public String catCode;
	public String companyId;
	public String companyName;
	public String companyProvince;
	public String description;
	public String descriptionInfo;
	public ArrayList<String> images;
	public String isFavorite;
	public String isFeature;
	public String memberType;
	public String name;
	public String productId;
	public String webAddress;

	public String pricevalue;
	public String orderUnit;
	public String tradeTerms;

	/**
	 * "minOrderInfo": {
	  		"orderUnit": " Pieces",
	  		"splitUnitPrice": ["1:4.75"],
	  		"prodPriceUnit": "Piece",
	  		"prodPrice": "4.75"
		},
	 */
	public ProductMinOrderInfo minOrderInfo;

	/**
	 * 此三个属性由于不确定，所以不能用具体的属性值处理，只能将其先作为字符串，然后再做解析
	 */
	public ArrayList<ProductKeyValuePair> tradeInfoList;
	public ArrayList<ProductKeyValuePair> basicInfoList;
	public ArrayList<ProductKeyValuePair> additionalInfoList;

	public boolean isFavorite()
	{
		return "true".equals(isFavorite);
	}

	public boolean isFeature()
	{
		return "true".equals(isFeature);
	}
}
