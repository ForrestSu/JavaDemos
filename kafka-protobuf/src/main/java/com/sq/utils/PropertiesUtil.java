package com.sq.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    public static final String TOPIC = "test";
    public static final String KAFKA_SERVER_URL = "192.168.0.223";
    public static final int KAFKA_SERVER_PORT = 9092;
    public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
    public static final int CONNECTION_TIMEOUT = 100000;
    public static final String CLIENT_ID = "SimpleConsumerDemoClient";

    public static Properties LoadProperties(String file_name) {
        File f = new File(file_name);
        if (!f.exists()) {
            System.err.println("file: " + file_name + " is not exist!");
            return null;
        }
        Properties props = new Properties();
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(file_name));
            props.load(in);
        } catch (IOException ioe) {
            System.err.println("Properties file: '" + file_name + "' could not be read." + ioe);
            return null;
        }
        return props;
    }
}
