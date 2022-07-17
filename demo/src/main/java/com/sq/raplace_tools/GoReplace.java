package com.sq.raplace_tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// 上报记录
class Item {
    String key;
    String value;

    public Item(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String ToInc(String prefix) {
        return String.format("%scommon.%s%s.Incr()", prefix, key, value);
    }

    // CacheVidPutFail   = metrics.Counter("Cache-VidPutFail")   // 视频缓存-写入失败
    public String ToDeclare(String note) {
        return String.format("%s%s = metrics.Counter(\"%s-%s\") // %s", key, value, key, value, note);
    }
}

class SortByKV implements Comparator<Item> {
    @Override
    public int compare(Item a, Item b) {
        if (a.key.equals(b.key)) {
            return a.value.compareTo(b.value);
        }
        return a.key.compareTo(b.key);
    }
}


public class GoReplace {
    private static final String MD_FILE_ENCODING = "utf-8";
    private static final String LINE_ENDING = "\n";

    private static final String PatternStr = "([A-Z]\\w+)";
    private static final Pattern PatternRE = Pattern.compile(PatternStr);
    // 记录所有 Item
    private List<Item> reminds = new ArrayList<>();

    private static Item parseLine(String line) {
        List<String> result = new ArrayList<>();
        Matcher m = PatternRE.matcher(line);
        while (m.find()) {
            result.add(m.group(0));
        }
        return new Item(result.get(1), result.get(2));
    }

    @Test
    public void test() {
        Item ret = parseLine("common.ReportCounterMetrics(common.Ticket, common.Sport12WebVuid)");
        System.out.println(ToStringBuilder.reflectionToString(ret));
    }

    private int rewriteGoFile(File gofile, boolean modify) {
        final String fileName = gofile.getName();
        if (!fileName.endsWith(".go")) {
            // System.out.println(fileName);
            return 0;
        }
        int total = 0;
        try {
            List<String> rows = FileUtils.readLines(gofile, MD_FILE_ENCODING);
            for (int k = 0; k < rows.size(); ++k) {
                final String line = rows.get(k);
                // 处理正文,正则查找
                int pos = line.indexOf("common.ReportCounterMetrics");
                if (pos > 0) {
                    int count = StringUtils.countMatches(line, ",");
                    if (count == 1) {
                        total++;
                        Item item = parseLine(line);
                        reminds.add(item);
                        rows.set(k, item.ToInc(line.substring(0, pos)));
                    }
                }
            }
            // overwrite file
            if (total > 0 && modify) {
                FileUtils.writeLines(gofile, MD_FILE_ENCODING, rows, LINE_ENDING, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return total;
    }

    //递归遍历当前文件夹下的所有文件
    public int VisitDir(final String path) {
        File f = new File(path);
        String[] list = f.list();

        int iCount = 0;
        for (String fileName : list) {
            File file = new File(f.getPath() + File.separator + fileName);
            // 忽略隐藏文件 .*
            if (file.getName().startsWith(".")) {
                continue;
            }
            if (file.isDirectory()) {
                iCount += VisitDir(file.getPath());
            } else {
                iCount += rewriteGoFile(file, true);
            }
        }
        return iCount;
    }


    // 生成定义
    public static void generateDefine(List<Item> reminds,
                                      Map<String, String> keyNotes,
                                      Map<String, String> valueNotes) {
        reminds.sort(new SortByKV());
        for (Item item : reminds) {
            // 块注释
            if (keyNotes.containsKey(item.key)) {
                System.out.println("// " + item.key + " " + keyNotes.get(item.key));
                keyNotes.remove(item.key);
            }
            // 定义
            if (valueNotes.containsKey(item.value)) {
                System.out.println(item.ToDeclare(valueNotes.get(item.value)));
            } else {
                System.err.println(item.value + "Not found!");
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        Map<String, String> valueNotes = loadNotes("demo/resources/metric.go");
        Map<String, String> keyNotes = loadNotes("demo/resources/key.go");

        String src_path = "/Users/sq/go_workspace/aid_free_sys";
        GoReplace rpl = new GoReplace();
        int iCount = rpl.VisitDir(src_path);
        generateDefine(rpl.reminds, keyNotes, valueNotes);
        System.out.println("Replaced total " + iCount + "！");
    }

    // value comments
    public static Map<String, String> loadNotes(String fileName) {
        Map<String, String> notes = new HashMap<>();
        final File metric = new File(fileName);
        try {
            List<String> rows = FileUtils.readLines(metric, MD_FILE_ENCODING);
            for (int k = 0; k < rows.size(); ++k) {
                final String line = rows.get(k).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                String[] arrs = line.split("//");
                if (arrs.length != 2) {
                    System.err.println(k + line);
                    continue;
                }
                String key = arrs[0].trim();
                String comment = arrs[1].trim();
                // System.out.println(key + "+" + comment);
                notes.put(key, comment);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return notes;
    }
}



