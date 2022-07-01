package com.sq.raplace_tools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/***
 * 从简书迁移 blog
 */

/**
 * -需求： 转换Blog 
 * 1 先访问 https://www.jianshu.com/u/6a0dba2307a4 获取每篇文章的名称+更新时间
 * 2 读取本地的文件，为每篇blog 追加 如下 Front Matter
---
title: "文章名称"    //提取文件名即可(去除后缀)
date: 2016-01-26T00:03:04
updated:          // 暂且先等于date
toc: true
comments: true
categories: [Git] // 采用文件夹名称
tags: [github]    // 从文章名称中提取关键字
---
 * 
 * 3 其他问题，怎么给文章摘要添加图片显示
  一种解决办法：在Front Matter 之后添加
 <img src="https://faithlove.github.io/pic/2018/RMTP_1/topPicPre.png" width=50% />
哇，漂亮的小姐姐(❤ ω ❤)
<!--more-->
 */

public class Blog_Migration {

    private static String MD_FILE_ENCODING = "utf-8";
    private static String LINE_ENDING = "\n";
    private static String HEADER_SPLIT_TAG = "---";
    private static Map<String, String> MAP_DATE_DICT = null;
    /*
     * TODO each page is 9 records 在线从网页抓取 
     * order_by=shared_at&page=3
     */
    /*Map<String, String> data = new HashMap<String, String>();
    data.put("order_by", "shared_at");
    data.put("page", "11");
    Document doc = Jsoup.connect(url)
            .header("x-infinitescroll", "true")
            .data(data)
            .get();
    System.out.println(doc);
    */

    public static Map<String, String> ParseHtml(File html) {
        Document doc = null;
        try {
            doc = Jsoup.parse(html, MD_FILE_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        Elements note_list = doc.select("ul.note-list");
        Elements elements = note_list.select("li");
        System.out.println("elements.size() == " + elements.size());
        for (Element note : elements) {
            Element title_node = note.select("a.title").first();
            Element time_node = note.select("span.time").first();
            String title = title_node.text();
            String shared_at = time_node.attr("data-shared-at");
            // System.out.println("title:" + title + ", shared_at=" + shared_at);
            result.put(title, shared_at);
        }
        return result;
    }

    public static boolean WriteHeaderToMarkdownFile(final String cur_dir_name, File mdfile) {

        final String filename = mdfile.getName();
        // System.out.println("cur_dir_name == " + cur_dir_name + ", filename == " + filename);
        if (!filename.endsWith(".md")) {
            System.err.println("filename must be *.md file! " + filename);
            return false;
        }
        final String title = FilenameUtils.getBaseName(filename);
        String real_title = title;
        String shared_at = MAP_DATE_DICT.get(title);
        if (shared_at == null) {
            // 文章保存到本地，文件会被改名(其中小数点会被改成"-"， 空格也会被替换为 "-")
            for (Entry<String, String> entry : MAP_DATE_DICT.entrySet()) {
                final String origin = entry.getKey();
                final String replaced = origin.replaceAll("[\\.]", "-")
                        .replaceAll(" ", "-")
                        .replaceAll("[?/:]", "-");
                if (title.equals(replaced)) {
                    real_title = entry.getKey();
                    shared_at = entry.getValue();
                    break;
                }
            }
            if (shared_at == null) {
                System.err.println("title " + title + " is not match!");
            }
        }

        List<String> headers = new ArrayList<>();
        headers.add(HEADER_SPLIT_TAG);
        headers.add("title: \"" + real_title + "\"");
        headers.add("date: " + shared_at);
        headers.add("updated: " + shared_at);
        headers.add("toc: true");
        headers.add("comments: true");
        headers.add("categories: [" + cur_dir_name + "]");
        headers.add("tags: [" + cur_dir_name + "]");
        headers.add(HEADER_SPLIT_TAG + "\n");

        boolean is_write_headers = true;
        try {
            List<String> data = FileUtils.readLines(mdfile, MD_FILE_ENCODING);
            if (data.size() > 0) {
                final String first_line = data.get(0);
                if (first_line.startsWith(HEADER_SPLIT_TAG)) {
                    is_write_headers = false;
                }
            }
            if (is_write_headers) {
                byte[] file_data_content = FileUtils.readFileToByteArray(mdfile);
                // 1 write headers
                boolean append = false;
                FileUtils.writeLines(mdfile, MD_FILE_ENCODING, headers, LINE_ENDING, append);
                // 2 write content
                FileUtils.writeByteArrayToFile(mdfile, file_data_content, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return is_write_headers;
	}
    //递归遍历当前文件夹下的所有文件
    public static int travelDir(final String cur_dir_name, final String path) {
		File f = new File(path);
		String[] list = f.list();

        int iCount = 0;
        for (String fname : list) {
            // System.out.println(fname);
            File dir = new File(f.getPath() + File.separator + fname);
			if (dir.isDirectory())
                iCount += travelDir(fname, dir.getPath());
			else {
                if (WriteHeaderToMarkdownFile(cur_dir_name, dir)) {
                    ++iCount;
                }
            }
		}
        return iCount;
	}

	public static void main(String[] args) {
        System.out.println("start ....");

        final File html = new File("resources/ColdRomantic-简书.html");
        MAP_DATE_DICT = ParseHtml(html);

        String src_path = "E:\\tmp\\jianshu_bak";
        int iCount = travelDir("jianshu", src_path);
        System.out.println("Total modify " + iCount + " files.");
	}
}
