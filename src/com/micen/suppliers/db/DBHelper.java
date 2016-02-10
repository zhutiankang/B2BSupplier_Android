package com.micen.suppliers.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.micen.suppliers.application.SupplierApplication;


public final class DBHelper extends SQLiteOpenHelper
{
	private static DBHelper helper = null;
	private static final String DATABASE_NAME = "micen.db";
	public static final String INTEGER_TYPE = " integer";
	private static final String TEXT_TYPE = " TEXT";
	public static final String ID = "id";
	public static final String TIME = "time";

	/**
	 * 目录浏览列表
	 */
	public static final String CATEGORIES_BROWSE_HISTORY_ = "categorieshistory";
	public static final String SEARCH_FLAG = "searchFlag";
	public static final String IS_FAVORITES = "isFavorites";
	public static final String SOURCE_SUBJECT = "sourceSubject";
	public static final String CATEGORIES_HISTORY = "categoriesHistory";
	public static final String CATEGORY = "category";
	public static final String VISIT_TIME = "visitTime";
	/**
	 * 搜索历史表
	 */
	public static final String RECENT_SEARCH_KEYWORDS = "recentSearchKeywords";
	public static final String RECENT_KEYWORDS = "recentKeywords";
	public static final String RECENT_KEYWORDS_VISIT_TIME = "visitTime";
	public static final String SEARCH_TYPE = "searchType";

	/**
	 * 询盘快捷模板
	 */
	public static final String MESSAGE_SHORT_CUT_TABLE = "messageShortCutTable";
	public static final String MESSAGE_SHORT_CUT_CONTENT = "messageShortCutContent";
	public static final String MESSAGE_SHORT_CUT_SELECTED = "messageShortCutSelected";
	public static final String MESSAGE_SHORT_CUT_ID = "messageShortCutID";

	private DBHelper()
	{
		super(SupplierApplication.getInstance().getApplicationContext(), DATABASE_NAME, null, 1);
	}

	public static DBHelper getInstance()
	{
		if (helper == null)
		{
			helper = new DBHelper();
		}
		return helper;
	}

	/**
	 * 关闭数据库
	 */
	public void closeDatabase(SQLiteDatabase db)
	{
		if (db != null && db.isOpen())
		{
			db.close();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.beginTransaction();
		try
		{
			createAllTables(db);
			db.setTransactionSuccessful();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

	private void createAllTables(SQLiteDatabase db)
	{
		String[] cloumns = new String[]
		{ SEARCH_FLAG + TEXT_TYPE, IS_FAVORITES + TEXT_TYPE, SOURCE_SUBJECT + TEXT_TYPE,
				CATEGORIES_HISTORY + TEXT_TYPE, CATEGORY + TEXT_TYPE, VISIT_TIME + TEXT_TYPE };
		createTable(db, CATEGORIES_BROWSE_HISTORY_, cloumns);
		cloumns = new String[]
		{ RECENT_KEYWORDS + TEXT_TYPE, RECENT_KEYWORDS_VISIT_TIME + TEXT_TYPE, SEARCH_TYPE + TEXT_TYPE };
		createTable(db, RECENT_SEARCH_KEYWORDS, cloumns);
		createMessageShortCut(db);
	}

	/**
	 * 创建询盘快捷模板工具
	 * @param db
	 */
	private void createMessageShortCut(SQLiteDatabase db)
	{
		String[] cloumns = new String[]
		{ MESSAGE_SHORT_CUT_CONTENT + TEXT_TYPE, MESSAGE_SHORT_CUT_SELECTED + TEXT_TYPE,
				MESSAGE_SHORT_CUT_ID + TEXT_TYPE };
		createTable(db, MESSAGE_SHORT_CUT_TABLE, cloumns);
	}

	/**
	 * @param sqliteDatabase    
	 * @param table 要创建的数据表名
	 * @param columns 列名
	 */
	private void createTable(SQLiteDatabase sqliteDatabase, String table, String[] columns)
	{
		String createTable = "create table if not exists ";
		String primaryKey = " Integer primary key autoincrement";
		String text = " text";
		char leftBracket = '(';
		char rightBracket = ')';
		char comma = ',';
		int stringBufferSize = 170;
		StringBuffer sql = new StringBuffer(stringBufferSize);
		sql.append(createTable).append(table).append(leftBracket).append(ID).append(primaryKey).append(comma);
		for (String column : columns)
		{
			sql.append(column);
			sql.append(comma);
		}
		sql.append(TIME).append(text).append(rightBracket);
		try
		{
			sqliteDatabase.execSQL(sql.toString());
		}
		catch (Exception e)
		{
			e.getMessage();
		}

	}

	/**
	 * drop表
	 * @param table 需要drop的表名
	 */
	public synchronized void dropTable(final SQLiteDatabase db, final String table)
	{
		SQLiteDatabase database = null;
		try
		{
			database = db == null ? getWritableDatabase() : db;
			database.execSQL("drop table if exists " + table);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 插入数据
	 * @param table
	 * @param nullColumnHack
	 * @param values
	 * @return
	 */
	public synchronized long insert(final String table, final String nullColumnHack, final ContentValues values)
	{
		SQLiteDatabase database = null;
		try
		{
			database = getWritableDatabase();
			return database.insert(table, nullColumnHack, values);
		}
		catch (Exception e)
		{
			return -1;
		}
		finally
		{
			closeDatabase(database);
		}
	}

	/**
	 * 删除数据
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int delete(final String table, final String whereClause, final String[] whereArgs)
	{
		SQLiteDatabase database = null;
		try
		{
			database = getWritableDatabase();
			return database.delete(table, whereClause, whereArgs);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			closeDatabase(database);
		}
	}

	/**
	 * 更新数据
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(final String table, final ContentValues values, final String whereClause, final String[] whereArgs)
	{
		SQLiteDatabase database = null;
		try
		{
			database = getWritableDatabase();
			return database.update(table, values, whereClause, whereArgs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			closeDatabase(database);
		}
	}
}
