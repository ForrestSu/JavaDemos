package com.sq.raplace_tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class IniReader {
	
	public final static String F_CONFIGFILE = "conf/config.ini";
	public static String getProperties(String key, String DefaultValue) {
		return getProperties(F_CONFIGFILE, key, DefaultValue);
	} 

	public static String getProperties(String fileName, String key, String DefaultValue) {
		String sRet = DefaultValue;
		String filePath = "";
		File mFile = null;
		BufferedReader br = null;
		try {
			mFile = new File(fileName);
			// 文件不存在则创建
			if (!mFile.exists()) {
				mFile.createNewFile();
				System.out.println("create file:" + fileName);
				return sRet;
			}
			filePath = mFile.getAbsolutePath();
			br = new BufferedReader(new FileReader(filePath));
			String sbuf = br.readLine();
			while (sbuf != null) {
				sbuf = sbuf.trim();
				if (sbuf.startsWith(key + "=")) {
					sRet = sbuf.substring(key.length() + 1);
					break;
				}
				sbuf = br.readLine();
			}

		} catch (IOException e) {
			System.out.println("[Error] when read configure file "+filePath+ "! error="+ e.getMessage());
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("get ["+ key + "]=>"+ sRet);
		return sRet;
	}

	/**
	 * 向某个文件首行写入一行数据
	 * 
	 * @param fileName
	 * @param key
	 * @param integer
	 * @return
	 */
	public static boolean setProperties(String fileName, String key, long integer) {
		String fullFilePath = "";
		FileWriter fw;
		BufferedWriter writer;
		try {
			fullFilePath = (new File(fileName)).getAbsolutePath();
			// 设置成覆盖写的方式
			fw = new FileWriter(fullFilePath, false);
			writer = new BufferedWriter(fw);
			writer.write(key + "=" + integer);
			if (writer != null)
				writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
