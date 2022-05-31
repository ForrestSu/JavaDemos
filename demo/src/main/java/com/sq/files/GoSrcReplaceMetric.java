package com.sq.files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * -需求： 从blog中提取图片链接,
 * 下载到本地，
 * 并且更换链接为本地路径
 */

public class GoSrcReplaceMetric {

    private static final String MD_FILE_ENCODING = "utf-8";
    private static final String LINE_ENDING = "\n";


    public static int WriteHeaderToMarkdownFile(final String curDirName, File gofile) {
        final String filename = gofile.getName();
        // System.out.println("cur_dir_name == " + cur_dir_name + ", filename == " + filename);
        if (filename.endsWith("_test.go")) {
            return 0;
        }
        if (!filename.endsWith(".go")) {
            // System.err.println("filename must be *.go file! " + filename);
            return 0;
        }

        int occurs = 0;
        try {
            List<String> rows = FileUtils.readLines(gofile, MD_FILE_ENCODING);
            for (int k = 0; k < rows.size(); ++k) {
               final String cur_line = rows.get(k);
                // 处理正文,正则查找
                if (cur_line.contains("common.ReportCounterMetrics")) {
                    int count = StringUtils.countMatches(cur_line, ",");
                    if (count == 1) {
                        occurs++;
                        // 1 收集所有counter，然后正则提取
                        // 2 按照第二个 key 提取注释
                        // 3 按照记录排序
                        //   cache.AlbumCacheWriteFail
                        //   cache.AlbumCacheWriteOK
                        // 4 生成代码
                        //  var (
                        //    AlbumCacheWriteFailCounter = metrics.Counter("cache.AlbumCacheWriteFail")
                        // 5 进行第二遍扫描，替换代码
                        // rows.set(k, replaced_cur_line);
                    }
                }
            }
            // overwrite
//            if (image_links_cnt > 0) {
//                FileUtils.writeLines(gofile, MD_FILE_ENCODING, rows, LINE_ENDING, false);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return occurs;
    }

    //递归遍历当前文件夹下的所有文件
    public static int travelDir(final String curDirName, final String path) {
        File f = new File(path);
        String[] list = f.list();

        int iCount = 0;
        for (String fname : list) {
            // System.out.println(fname);
            File dir = new File(f.getPath() + File.separator + fname);
            if (dir.getName().equals(".git")) {
                continue;
            }
            if (dir.isDirectory()){
                iCount += travelDir(fname, dir.getPath());
            }
            else {
                iCount += WriteHeaderToMarkdownFile(curDirName, dir);
            }
        }
        return iCount;
    }

    public static void main(String[] args) {
        String src_path = "/Users/sq/go_workspace/aid_free_sys";
        int iCount = travelDir("aid_free_sys", src_path);
        System.out.println("Total find " + iCount + " counter.");
    }
}
