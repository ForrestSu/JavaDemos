package com.sq.raplace_tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriter {
    static FileWriter fw;  
    static BufferedWriter writer; 
    static String endline="\n";  
    public MyFileWriter(String outputfile, boolean append)  
    {
        endline = System.lineSeparator();  
        try {  
            // 设置成尾部追加方式  
            fw = new FileWriter(outputfile, append);  
            writer = new BufferedWriter(fw);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }   
    }
    /**
     * @param outputfile
     */
    public MyFileWriter(String outputfile)  
    {  
        this(outputfile, true);
    }
    public void write(String content) {  
        try {  
            writer.write(content);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    public void writeln(String content) {  
        write(content+endline);
    }
    //force flush fo file 
    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close()
    {
        flush();
        try {  
            if (writer != null)  
                writer.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        
       String outpathString="C:\\Users\\toptrade\\Desktop\\result.txt";  
       MyFileWriter obj= new MyFileWriter(outpathString);
       obj.writeln("sunquan");
       // 最后关掉输出流  
       obj.close();
       System.out.println("OK");
    }
   
}
