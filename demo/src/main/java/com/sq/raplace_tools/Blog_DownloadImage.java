package com.sq.raplace_tools;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;


/**
 * -需求： 从blog中提取图片链接,
 * 下载到本地，
 *  并且更换链接为本地路径
 */

public class Blog_DownloadImage {

    private static String MD_FILE_ENCODING = "utf-8";
    private static String LINE_ENDING = "\n";
    private static String HEADER_SPLIT_TAG = "---";

    private static String pattern = "\\[[^\\]]*\\]\\(http://upload-images.jianshu.io[^\\)]*\\)";
    private static Pattern regex = Pattern.compile(pattern);


    public static int WriteHeaderToMarkdownFile(final String cur_dir_name, File mdfile) {

        final String filename = mdfile.getName();
        // System.out.println("cur_dir_name == " + cur_dir_name + ", filename == " + filename);
        if (!filename.endsWith(".md")) {
            System.err.println("filename must be *.md file! " + filename);
            return 0;
        }

        int image_links_cnt = 0;
        try {
            List<String> rows = FileUtils.readLines(mdfile, MD_FILE_ENCODING);

            int header_cnt = 0;
            for (int k = 0; k < rows.size(); ++k) {
                final String cur_line = rows.get(k);
                if (header_cnt < 2) {
                    if (cur_line.startsWith(HEADER_SPLIT_TAG)) {
                        ++header_cnt;
                    }
                } else {
                    // 处理正文,正则查找
                    Matcher match = regex.matcher(cur_line);
                    if (match.find()) {
                        int cnt = match.groupCount();
                        if (cnt > 1) {
                            System.err.println("Match count bigger than 1." + cnt);
                        }
                        ++image_links_cnt;
                        final String mkdown_img = match.group(0);

                        // DownloadImage
                        // 获取md语法中的图片名称，并处理为小写
                        int ipos = mkdown_img.indexOf(']');
                        String picture_base_name = mkdown_img.substring(1, ipos).toLowerCase();

                        String complete_url = mkdown_img.substring(ipos + 2);
                        complete_url = complete_url.substring(0, complete_url.length() - 1);

                        String[] tmp_arr = complete_url.split("[\\?]");
                        String image_url = tmp_arr[0].replace("http://", "https://");

                        // 生成新的文件名
                        final String ext_name = FilenameUtils.getExtension(image_url);
                        final String image_file_name = picture_base_name + "." + ext_name;

                        // DownloadUtil.DownloadImage(image_url, "E:\\tmp\\upload\\" + image_file_name);
                        // System.out.println(mkdown_img);

                        final String replaced_cur_line = cur_line.replace(complete_url, "/upload/" + image_file_name);
                        rows.set(k, replaced_cur_line);
                    }
                }
            }

            // overwrite
            if (image_links_cnt > 0) {
                FileUtils.writeLines(mdfile, MD_FILE_ENCODING, rows, LINE_ENDING, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return image_links_cnt;
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
                iCount += WriteHeaderToMarkdownFile(cur_dir_name, dir);
            }
		}
        return iCount;
	}

	public static void main(String[] args) {

        String src_path = "E:\\tmp\\jianshu_bak";
        int iCount = travelDir("jianshu", src_path);
        System.out.println("Total find " + iCount + " images.");
	}
}
