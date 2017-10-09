/**
 * 
 */
package com.sunquan.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GetFileHash {

	public static String toHexString(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			// if (n<b.length-1) hs=hs+":";
		}
		return hs.toLowerCase();
	}
	/**
	 * hashType的值："MD5"，"SHA1"，"SHA-256"，"SHA-384"，"SHA-512"
	 */
	private static String getHash(String fileName, String hashType) throws NoSuchAlgorithmException, IOException  
    {  
        InputStream fis = new FileInputStream(fileName);  
        byte buffer[] = new byte[1024];  
        MessageDigest md5 = MessageDigest.getInstance(hashType);  
        for(int numRead = 0; (numRead = fis.read(buffer)) > 0;)  
        {  
            md5.update(buffer, 0, numRead);  
        }  
        fis.close();  
        return toHexString(md5.digest());  
    }  
	
	public static void main(String[] args) {
		
        String file="E:/Install Software/eclipse-cpp-oxygen-R-win32-x86_64.zip";
        try {
			String hash= getHash(file, "SHA-512");
	        System.out.println(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
