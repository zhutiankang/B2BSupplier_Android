package com.micen.suppliers.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.micen.suppliers.module.db.Module;


public final class DBDataHelper
{
	private static DBDataHelper dataHelper = null;
	private DBHelper dbHelper = null;
	private static final String SELECT = "select ";
	private static final String FROM = " from ";
	private static final String WHERE = " where ";
	private static final String ORDER_BY = " order by ";

	private DBDataHelper()
	{
		dbHelper = DBHelper.getInstance();
	}

	static
	{
		dataHelper = new DBDataHelper();
	}

	public static DBDataHelper getInstance()
	{
		return dataHelper;
	}

	/**
	 * 根据条件查询数据库表
	 * @param tableName
	 * @param showColumns
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @param cls
	 * @return
	 */
	public ArrayList<Module> select(String tableName, String showColumns, String selection, String selectionArgs,
			String orderBy, Class<?> cls)
	{
		synchronized (dbHelper)
		{
			ArrayList<Module> moduleList = new ArrayList<Module>();
			SQLiteDatabase db = null;
			try
			{
				db = dbHelper.getReadableDatabase();
				String sql = SELECT;
				sql += showColumns != null ? showColumns : "*";
				sql += FROM + tableName;
				if (selection != null && selectionArgs != null)
				{
					sql += WHERE + selection + " = " + selectionArgs;
				}
				if (orderBy != null)
				{
					sql += ORDER_BY + orderBy;
				}
				Cursor cursor = db.rawQuery(sql, null);
				changeToList(cursor, moduleList, cls);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				dbHelper.closeDatabase(db);
			}
			return moduleList;
		}
	}

	/**
	 * 查找数据表
	 * 
	 * @param table
	 *            要操作的表
	 * @param selection
	 *            匹配条件，例如"id>?and name <>?",不需要可以设为null
	 * @param selectionArgs
	 *            与selection相对应，里面的字符串将替换selection里的"?",
	 *            构成完整的匹配条件，例如{"6","jack"}
	 * @param orderby 排序参数
	 * @param moduleClass
	 *            要转化成的模型类Class,例如要转成WebPage则传入WebPage.class
	 * @return 数据模型集合,集合是的对象类型为moduleClass
	 */
	public ArrayList<Module> select(final String table, final String selection, final String[] selectionArgs,
			final String orderby, final Class<?> moduleClass)
	{
		SQLiteDatabase database = null;
		Cursor cursor = null;
		ArrayList<Module> moduleList = new ArrayList<Module>();
		synchronized (dbHelper)
		{

			try
			{
				database = dbHelper.getReadableDatabase();
				// 查询数据
				cursor = database.query(table, null, selection, selectionArgs, null, null, orderby, null);
				// 将结果转换成为数据模型
				changeToList(cursor, moduleList, moduleClass);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				dbHelper.closeDatabase(database);
			}
			return moduleList;
		}
	}

	private void changeToList(Cursor cursor, List<Module> modules, Class<?> moduleClass)
	{
		// 取出所有的列名
		int count = cursor.getCount();
		Module module;
		cursor.moveToFirst();
		synchronized (dbHelper)
		{
			try
			{
				// 遍历游标
				for (int i = 0; i < count; i++)
				{
					// 转化为moduleClass类的一个实例
					module = changeToModule(cursor, moduleClass);
					modules.add(module);
					cursor.moveToNext();
				}
			}
			catch (SecurityException e)
			{
				// Log.e(TAG, e + FusionCode.EMPTY_STRING);
			}
			catch (IllegalArgumentException e)
			{
				// Log.e(TAG, e + FusionCode.EMPTY_STRING);
			}
			catch (IllegalAccessException e)
			{
				// Log.e(TAG, e + FusionCode.EMPTY_STRING);
			}
			catch (InstantiationException e)
			{
				// Log.e(TAG, e + FusionCode.EMPTY_STRING);
			}
			catch (NoSuchFieldException e)
			{
				System.out.println("");
			}
			finally
			{
				cursor.close();
			}
		}
	}

	private Module changeToModule(Cursor cursor, Class<?> moduleClass) throws IllegalAccessException,
			InstantiationException, SecurityException, NoSuchFieldException
	{
		synchronized (dbHelper)
		{
			// 取出所有的列名
			String[] columnNames = cursor.getColumnNames();
			String filedValue;
			int columncount = columnNames.length;
			Field field;
			Module module = (Module) moduleClass.newInstance();
			// 遍历有列
			for (int j = 0; j < columncount; j++)
			{
				// 根据列名找到相对应 的字段
				field = moduleClass.getField(columnNames[j]);
				filedValue = cursor.getString(j);
				if (filedValue != null)
				{
					field.set(module, filedValue.trim());
				}
			}
			return module;
		}
	}

	/**
	 * 向数据库插入数据
	 * 
	 * @param table
	 *            要操作的表
	 * @param module
	 *            数据模型，id设为自增，若没指定，则自动生成；若指定则插入指定的id,
	 *            但是在数据表已存在该id的情况下会导致插入失败，建议不指定id
	 * @return 插入行的行号,可以看作是id
	 */
	public long insert(final String table, final Module module)
	{
		synchronized (dbHelper)
		{
			ContentValues values = moduleToContentValues(module);
			return dbHelper.insert(table, null, values);
		}
	}

	/**
	 * 向数据库更新数据
	 * 
	 * @param table
	 *            要操作的表
	 * @param module
	 * @return 更新行的行号,可以看作是id
	 */
	public long update(final String table, final Module module)
	{
		synchronized (dbHelper)
		{
			ContentValues values = moduleToContentValues(module);
			return dbHelper.update(table, values, DBHelper.ID + "=?", new String[]
			{ module.id });
		}
	}

	/**
	 * 删除一条数据记录
	 * 
	 * @param table
	 *            要操作的表
	 * @param module
	 *            数据模型,该操作是根据id删除的,若id为null或不正确是不能成功删除的
	 * @return 数据表受影响的行数,0或1
	 */
	public int delete(final String table, final Module module)
	{
		synchronized (dbHelper)
		{
			return dbHelper.delete(table, "id=?", new String[]
			{ module.id });
		}
	}

	/**
	 * 删除数据
	 * 
	 * @param table
	 *            要操作的表
	 * @param whereClause
	 *            匹配条件，例如"id>?and name <>?",符合条件的记录将被删除，
	 *            不需要可以设为null，这样整张表的内容都会被删除
	 * @param whereArgs
	 *            与selection相对应，里面的字符串将替换selection里的"?",
	 *            构成完整的匹配条件，例如{"6","jack"}
	 * @return 数据表受影响的行数
	 */
	public int delete(final String table, final String whereClause, final String[] whereArgs)
	{
		synchronized (dbHelper)
		{

			return dbHelper.delete(table, whereClause, whereArgs);

		}
	}

	/**
	 * 将Module类型转成ContentValues
	 * 
	 * @param module
	 *            源Module
	 * @return ContentValues
	 * @author shenghua.lin
	 */
	private ContentValues moduleToContentValues(final Module module)
	{
		ContentValues values = new ContentValues();
		Field[] fields = module.getClass().getFields();
		String fieldName;
		String fieldValue;
		int fieldValueForInt = -1;
		try
		{
			for (Field field : fields)
			{
				fieldName = field.getName();
				if (field.get(module) instanceof String)
				{
					fieldValue = (String) field.get(module);
					if (fieldValue != null)
					{
						values.put(fieldName, fieldValue.trim());
					}
					else
					{
						values.put(fieldName, "");
					}
				}
				else if (field.get(module) instanceof Integer)
				{
					fieldValueForInt = (Integer) field.get(module);
					if (fieldValueForInt != -1)
					{
						values.put(fieldName, fieldValueForInt);
					}
				}
			}
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		return values;
	}

}
