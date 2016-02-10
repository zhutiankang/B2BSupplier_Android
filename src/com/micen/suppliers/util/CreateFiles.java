package com.micen.suppliers.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;

public class CreateFiles
{
	// 获取SDcard路径
	private static String CACHE_FILE_PATH;
	private static String FILE_NAME = "/hhaudio.html";
	private File dir;

	public CreateFiles(Activity activity)
	{
		CACHE_FILE_PATH = activity.getFilesDir().toString();
	}

	public void createCacheFile() throws IOException
	{
		File file = new File(CACHE_FILE_PATH);
		if (!file.exists())
		{
			// 按照指定的路径创建文件夹
			file.mkdirs();
		}
		dir = new File(CACHE_FILE_PATH + FILE_NAME);
		if (!dir.exists())
		{
			// 在指定的文件夹中创建文件
			dir.createNewFile();
		}
		else
		{
			dir.delete();
			// 在指定的文件夹中创建文件
			dir.createNewFile();
		}
	}

	// 向已创建的文件中写入数据
	public void print(String str)
	{
		str = Util.transToHtml(str);
		//str = str.replace("<html>", "<html><head><meta name=\"viewport\" content=\"user-scalable=no, initial-scale=1, maximum-scale=1, minimum-scale=1, width=device-width\"></head><body>");
		//str = str.replace("</html>","</body></html>");
		
		//str = str.replace("400px;", "0px");
		
		
		
		str = str.replace("<div class=\"StyleTableProd\"><table>", "<table>");
		
		//str = str.replace("</table>[.|\\s|\\r|\\n]*</div>", "</table>");
		
		str = str.replaceAll(".StyleTableProd table", "table");
		
		str = str.replaceAll(".StyleTableProd", "table");
		
		str = str.replace("<style typr=", "<style type=");
		
		FileWriter fw = null;;
		BufferedWriter bw = null;
		if (dir.length() == 0)
		{
			try
			{
				fw = new FileWriter(CACHE_FILE_PATH + FILE_NAME, true);//
				// 创建FileWriter对象，用来写入字符流
				bw = new BufferedWriter(fw); // 将缓冲对文件的输出
				bw.write(str + "\n"); // 写入文件
				bw.newLine();
				bw.flush(); // 刷新该流的缓冲
				bw.close();
				fw.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private String insertString(String str)
	{
		StringBuffer original = new StringBuffer(str);
		original.insert(original.indexOf("<div class=\"detail-item\"") + 24, "style=\"width:800px;overflow-x:auto;\"");
		if (original.toString().contains("<div class=\"StyleTableProd\""))
		{
			StringBuffer originalNew = new StringBuffer(original.toString());
			originalNew.insert(originalNew.indexOf("<div class=\"StyleTableProd\"") + 27,
					"style=\"width:800px;overflow-x:auto;\"");
			return originalNew.toString();
		}
		return original.toString();
	}

	public String getCacheFilePath()
	{
		return CACHE_FILE_PATH + FILE_NAME;
	}
}
