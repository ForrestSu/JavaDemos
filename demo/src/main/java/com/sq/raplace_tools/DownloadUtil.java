package com.sq.raplace_tools;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadUtil {

    /**
     * -从网络下载图片
     * 
     * @param imageUrl  "http://example.com/image.jpg"
     * @param dest "D:/File/To/Save/To/image.jpg"
     * @return
     */
    public static boolean DownloadImage(String imageUrl, String dest) {
        boolean is_ok = false;
        try {
            try (InputStream in = new URL(imageUrl).openStream()) {
                Files.copy(in, Paths.get(dest));
            }
            is_ok = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is_ok;
    }

    /**
     * -从网络下载 mp3
     *
     * @param mp3url  "http://example.com/audio.mp3"
     * @param dest "D:/File/To/Save/To/audio.mp3"
     * @return
     */
    public static boolean DownloadMp3(String mp3url, String dest) throws IOException {
        URLConnection conn = new URL(mp3url).openConnection();
        InputStream is = conn.getInputStream();
        OutputStream outstream = new FileOutputStream(new File(dest));
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) > 0) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        return true;
    }
}
