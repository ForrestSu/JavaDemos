package com.sq.raplace_tools;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class Download_Html_Mp3 {
	private static String MD_FILE_ENCODING = "utf-8";
	private static String OutputPath = "/Users/sq/video_learn/21geek.bang/au/";

	/**
	 * 重命名一个文件
	 * 注意：不会改变文件的最近修改日期
	 * @param html 文件
	 * @return
	 */
	public static boolean RenameSingleFile(File html) {
		boolean isSuccess = false;
		Document doc = null;
		try {
			doc = Jsoup.parse(html, MD_FILE_ENCODING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (doc == null) {
			return false;
		}
		String filename = html.getName();
		Elements audio_list = doc.select("audio");
		if(audio_list.size() == 1){
			Element mp3 = audio_list.first();
			//String mp3FileName = mp3.attr("title");
			String mp3Url = mp3.attr("src");
			String mp3FileName = FilenameUtils.getBaseName(filename) +".mp3";
			//System.out.println(mp3FileName +" "+ mp3Url);

			try {
				DownloadUtil.DownloadMp3(mp3Url, OutputPath + mp3FileName);
				isSuccess = true;
				System.out.println(mp3FileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println(filename + ", audio_list.size() =" + audio_list.size());
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
		String inpath = "/Users/sq/video_learn/Learn/html/";
		int iCount = ShowAllSubFile(inpath);
		System.out.println("total success download " + iCount + " files.");
		System.out.println("Complete. == 38");
	}
}
