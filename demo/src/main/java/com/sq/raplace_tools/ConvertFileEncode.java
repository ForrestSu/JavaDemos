package com.sq.raplace_tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class ConvertFileEncode {

	/**
	 * @param srcdir 源文件目录
	 * @param srcDecoding 源文件编码 likes ["GB2312", "UTF-8"]
	 * @param destdir    目标文件目录(不存在则创建)
	 * @param destEncoding 目标文件编码
	 * @param extensions 源文件扩展名 likes ["java", "sql"]
	 * @return 成功转换文件的个数
	 */
	public static int ConvertEnCode(String srcdir, String srcDecoding, String destdir ,String destEncoding, String[] extensions)
	{
		File dir = new File(destdir);
		// 级联创建文件夹
		if (!dir.exists()) {
			dir.mkdirs();
		}	
		destdir = dir.getAbsolutePath();
		
		// 递归获取所有源文件
		Collection<File> sourceFiles = FileUtils.listFiles(new File(srcdir), extensions, true);

		int iCount = sourceFiles.size();
		for (File destFiles : sourceFiles) {
			// UTF8格式文件路径
			String writeFileName = destdir + destFiles.getAbsolutePath().substring(srcdir.length()); 
			// 使用GB2312 读取数据，然后用UTF-8写入数据
			try {
				FileUtils.writeLines(new File(writeFileName), destEncoding , FileUtils.readLines(destFiles, srcDecoding));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return iCount;
	}
	
	
	public static void main(String[] args) {
		
	    // GB2312 编码格式源码路径
	   String src="D:\\isolardata\\configfile\\iSTMaster\\sqlGroup";
		// 转为UTF-8 编码格式源码路径
	   String dest = "D:\\ems\\src\\iSolar\\conf\\sqlGroup\\utf8";
	   String[] suffix =  new String[] { "sql" };
	   
	   int iCount =  ConvertEnCode(src, "GB2312", dest, "UTF-8", suffix);
	   System.out.println("成功转换: "+ iCount + " 文件个数!");
	}
}
