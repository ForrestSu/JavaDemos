package com.sq.raplace_tools;

import java.io.*;

public class BatchRename {

	// 需要被替换的字符串
	public static String replaced_STR = "OLD_NAME";
	// 新的字符串
	public static String new_STR = "";
	// 测试模式只打印 新文件的名称，不真正做重命名
	public static Boolean is_test_mode = true;

	/**
	 * 重命名一个文件
	 * 注意：不会改变文件的最近修改日期
	 * @param f 文件
	 * @return
	 */
	public static boolean RenameSingleFile(File f) {
		boolean isSuccess = false;
		String filename = f.getName();
		if (filename.contains(replaced_STR)) {
			String newfile = filename.replace(replaced_STR, new_STR);
			String new_full_name = f.getParent() + File.separator + newfile;
			if (is_test_mode) {
				System.out.println(new_full_name);
				isSuccess = true;
			} else {
				isSuccess = f.renameTo(new File(new_full_name));
				if (!isSuccess) {
					System.err.println("fail to rename " + new_full_name);
				}
			}
		} else {
			System.err.println(filename);
		}
		return isSuccess;
	}

	// 递归遍历当前文件夹下的所有文件
	public static int ShowAllSubFile(String path) {
		File f = new File(path);
		String[] list = f.list();
		int iCount = 0;
		for (String s : list) {
			// System.out.println(s);
			String fullname = f.getPath() + File.separator + s;
			File subf = new File(fullname);
			// 如果当前s所代表的是文件夹
			if (subf.isDirectory())
				iCount += ShowAllSubFile(subf.getPath());
			else {
				if (RenameSingleFile(subf)) {
					++iCount;
				}
			}
		}
		return iCount;
	}

	public static void main(String[] args) {
		System.out.println("start....");
		/* 输入文件夹路径 */
		String inpath = "/Users/sq/Downloads/53 Go语言从入门到实战";
		int iCount = ShowAllSubFile(inpath);
		System.out.println("total success rename " + iCount + " files.");
		System.out.println("Complete.");
	}
}
