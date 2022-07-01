package com.sq.raplace_tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class MyFileUtils {

	/**
	 *  read all file's name from spath 
	 * @param spath
	 * @return sorted files Set
	 */
	public static Set<String> ListFiles(String spath) {
		Set<String> result = new TreeSet<String>();
		File dir = new File(spath);
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; ++i) {
				if (files[i].isFile()) {
					result.add(files[i].getName());
				}
			}
		}
		return result;
	}
	
	/**
	 * check the Directory exists before write, if no create it!
	 * @param path
	 * @param fileName
	 * @param data
	 * @return true when success, false when write fail!
	 */
	public static boolean WriteBytes2File(String path, String fileName, byte[] data) {
		File dir = new File(path);
		if (!dir.exists()) {
			System.out.println("mkdirs [" + path +"] "+dir.mkdirs());
		}
		try {
			String fullfile = dir.getAbsolutePath() + File.separator + fileName;
			FileOutputStream out = new FileOutputStream(fullfile);
			out.write(data, 0, data.length);
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: can't find file! " + e.getMessage());
		} catch (IOException e) {
			System.err.println("ERROR: WriteBytes2File()!" + e.getMessage());
		}
		return false;
	}
 
	/**
	 * 从文件中读取字节码
	 * @param fileName 
	 * @return byte[] Read Successful
	 *     null Failure
	 */
	public static byte[] ReadFileAsBytes(String fileName) {
		File file = new File(fileName);
		if ((!file.exists()) || file.length() <= 0) {
			return null;
		}
		int len = (int) file.length();
		byte[] b = new byte[len];
		FileInputStream in;
		try {
			in = new FileInputStream(file);
			int count = in.read(b, 0, len);
			if (count != len) {
				System.err.println("ERROR: read file, need " + count + " bytes, but just read " + len + " bytes actually!");
			}
			in.close();
			return b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
