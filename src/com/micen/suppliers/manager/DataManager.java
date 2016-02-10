package com.micen.suppliers.manager;

import java.util.ArrayList;

import com.micen.suppliers.db.DBDataHelper;
import com.micen.suppliers.db.DBHelper;
import com.micen.suppliers.module.db.CategoryHistory;
import com.micen.suppliers.module.db.MessageShortCut;
import com.micen.suppliers.module.db.Module;
import com.micen.suppliers.module.db.SearchRecord;


public class DataManager
{
	private static DataManager dbManager;

	public static DataManager getInstance()
	{
		if (dbManager == null)
		{
			synchronized (DataManager.class)
			{
				if (dbManager == null)
				{
					dbManager = new DataManager();
				}
			}
		}
		return dbManager;
	}

	/**
	* 数据库中插入搜索关键词
	* @param keyWords
	*/
	public void insertKeyWords(String keyWords, String searchType)
	{
		SearchRecord recentSearch = new SearchRecord();
		ArrayList<Module> modules = DBDataHelper.getInstance().select(DBHelper.RECENT_SEARCH_KEYWORDS, null,
				DBHelper.RECENT_KEYWORDS, "'" + keyWords + "'", null, SearchRecord.class);
		if (modules != null && modules.size() != 0)
		{
			DBDataHelper.getInstance().delete(DBHelper.RECENT_SEARCH_KEYWORDS,
					DBHelper.RECENT_KEYWORDS + "=" + "'" + keyWords + "'", null);
		}
		recentSearch.recentKeywords = keyWords;
		recentSearch.visitTime = String.valueOf(System.currentTimeMillis());
		recentSearch.searchType = searchType;
		DBDataHelper.getInstance().insert(DBHelper.RECENT_SEARCH_KEYWORDS, recentSearch);
	}

	/**
	* 刷新最近搜索关键词列表
	*/
	public ArrayList<SearchRecord> refreshRecentKeywordsList(String searchType)
	{

		ArrayList<Module> modules = DBDataHelper.getInstance().select(DBHelper.RECENT_SEARCH_KEYWORDS, null,
				DBHelper.SEARCH_TYPE, "'" + searchType + "'", "visitTime DESC", SearchRecord.class);
		int maxSize = 5;
		ArrayList<Module> deleteModules = new ArrayList<Module>();
		if (modules.size() > maxSize)
		{
			for (int i = maxSize; i < modules.size(); i++)
			{
				DBDataHelper.getInstance().delete(DBHelper.RECENT_SEARCH_KEYWORDS, modules.get(i));
				deleteModules.add(modules.get(i));
			}
		}
		while (deleteModules.size() > 0)
		{
			modules.remove(deleteModules.get(0));
			deleteModules.remove(deleteModules.get(0));

		}

		ArrayList<SearchRecord> recentSearchDataList = new ArrayList<SearchRecord>();
		for (int i = 0; i < modules.size(); i++)
		{
			SearchRecord moduleBean = (SearchRecord) modules.get(i);
			recentSearchDataList.add(moduleBean);
		}
		return recentSearchDataList;
	}

	public ArrayList<CategoryHistory> initRecentCategory()
	{
		ArrayList<CategoryHistory> categoriesBrowseHisBean = new ArrayList<CategoryHistory>();
		// 目录浏览历史数据
		ArrayList<Module> categroiesModules = DBDataHelper.getInstance().select(DBHelper.CATEGORIES_BROWSE_HISTORY_,
				null, null, null, "visitTime DESC", CategoryHistory.class);
		int number = categroiesModules.size() > 3 ? 3 : categroiesModules.size();

		for (int i = 0; i < number; i++)
		{
			CategoryHistory moduleBean = (CategoryHistory) categroiesModules.get(i);
			categoriesBrowseHisBean.add(moduleBean);
		}
		return categoriesBrowseHisBean;
	}

	public void insertCategoryHistory(String categoryHistory, String categoryCode)
	{
		ArrayList<Module> modules = DBDataHelper.getInstance().select(DBHelper.CATEGORIES_BROWSE_HISTORY_, null,
				DBHelper.CATEGORY, "'" + categoryCode + "'", null, CategoryHistory.class);
		CategoryHistory module;
		if (modules != null && modules.size() != 0)
		{
			for (int i = 0; i < modules.size(); i++)
			{
				module = (CategoryHistory) modules.get(i);
				module.visitTime = String.valueOf(System.currentTimeMillis());
				DBDataHelper.getInstance().update(DBHelper.CATEGORIES_BROWSE_HISTORY_, module);
			}
		}
		else
		{
			module = new CategoryHistory();
			module.searchFlag = "rfq";
			module.isFavorites = "true";
			module.sourceSubject = "rfq";
			module.categoriesHistory = categoryHistory;
			module.category = categoryCode;
			module.visitTime = String.valueOf(System.currentTimeMillis());
			DBDataHelper.getInstance().insert(DBHelper.CATEGORIES_BROWSE_HISTORY_, module);
		}
	}

	public void deleteAllRecentKeyWord()
	{
		DBDataHelper.getInstance().delete(DBHelper.RECENT_SEARCH_KEYWORDS, null, null);
	}

	// /**
	// * 查出已浏览有效数据并转换为相应类型
	// * @return
	// */
	// public ArrayList<String> changeToSearchListRecoder(String type)
	// {
	// ArrayList<Module> modules = DBDataHelper.getInstance().select(DBHelper.SEARCH_LIST_TYPE_TABLE, null,
	// DBHelper.SEARCH_LIST_TYPE, "'" + type + "'", null, ListRecord.class);
	// ArrayList<String> productIDs = new ArrayList<String>();
	// for (int i = 0; i < modules.size(); i++)
	// {
	// productIDs.add(((ListRecord) modules.get(i)).searchListID);
	// }
	// return productIDs;
	// }
	//
	// /**
	// * 将浏览过的数据加入到表中
	// * @param product
	// */
	// public void insertInToSearchListTable(String id, String type)
	// {
	// // TODO Auto-generated method stub
	// ArrayList<Module> modules = DBDataHelper.getInstance().select(DBHelper.SEARCH_LIST_TYPE_TABLE, null,
	// DBHelper.SEARCH_LIST_ID, "'" + id + "'", null, ListRecord.class);
	// ListRecord searchRecoder = null;
	// if (modules != null && modules.size() != 0)
	// {
	// searchRecoder = ((ListRecord) modules.get(0));
	// searchRecoder.time = String.valueOf(System.currentTimeMillis());
	// DBDataHelper.getInstance().update(DBHelper.SEARCH_LIST_TYPE_TABLE, searchRecoder);
	// }
	// else
	// {
	// // 是一条新记录．．
	// searchRecoder = new ListRecord();
	// searchRecoder.searchListID = id;
	// searchRecoder.time = String.valueOf(System.currentTimeMillis());
	// searchRecoder.searchListType = type;
	// DBDataHelper.getInstance().insert(DBHelper.SEARCH_LIST_TYPE_TABLE, searchRecoder);
	// }
	// }

	/**
	 * 根据字段名删除表中的数据
	 * @param Table 表名
	 * @param Key	字段名
	 * @param Value 限制条件
	 * @return
	 */
	public int deleteMailShortCut(String table, String Key, String Value)
	{
		int delectNum = DBDataHelper.getInstance().delete(table, Key + "= ?", new String[]
		{ Value });
		return delectNum;
	}

	/**
	 * 插入新数据
	 * @param Table	表名
	 * @param mailShortCut	对象
	 * @return	返回-1：插入失败
	 */
	public long insertInToMessageShortCutTable(String table, MessageShortCut mailShortCut)
	{
		return DBDataHelper.getInstance().insert(table, mailShortCut);
	}

	/**
	 * 查询表中的数据
	 * @param Table	表名
	 * @return MailShortCut对象集合
	 */
	public ArrayList<MessageShortCut> selectMessageShortCutTable(String selection, String[] selectionArgs)
	{
		ArrayList<Module> modules = DBDataHelper.getInstance().select(DBHelper.MESSAGE_SHORT_CUT_TABLE, selection,
				selectionArgs, null, MessageShortCut.class);
		ArrayList<MessageShortCut> mailShortCuts = new ArrayList<MessageShortCut>();
		if (modules != null && modules.size() != 0)
		{
			for (int i = 0; i < modules.size(); i++)
			{
				mailShortCuts.add((MessageShortCut) modules.get(i));
			}
		}
		return mailShortCuts;
	}

	/**
	 * 根据key和value定位数据库中的数据，然后进行信息的更新
	 * @param table	表名
	 * @param key	字段名
	 * @param value	内容
	 * @param updataValue 替换的信息:现只针对mailsShortCutSelected的修改
	 */
	public void updateMailShortCutTable(String table, String key, String value, String updataValue)
	{
		// TODO Auto-generated method stub
		ArrayList<Module> modules = DBDataHelper.getInstance().select(table, null, key, "'" + value + "'", null,
				MessageShortCut.class);

		MessageShortCut mailShortCut = null;
		if (modules != null && modules.size() != 0)
		{
			mailShortCut = ((MessageShortCut) modules.get(0));
			mailShortCut.messageShortCutSelected = updataValue;
			DBDataHelper.getInstance().update(table, mailShortCut);
		}
	}
}
