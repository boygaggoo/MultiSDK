package com.multisdk.library.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
  /**
   * 获取文件的MD5值
   * 
   * @param filepath
   * @return
   * @throws NoSuchAlgorithmException
   * @throws FileNotFoundException
   */
  public static String getMd5FromFile(String filepath) throws NoSuchAlgorithmException, FileNotFoundException {
    MessageDigest digest = MessageDigest.getInstance("MD5");
    File f = new File(filepath);
    String output = "";
    InputStream is = new FileInputStream(f);
    byte[] buffer = new byte[8192];
    int read = 0;
    try {
      while ((read = is.read(buffer)) > 0) {
        digest.update(buffer, 0, read);
      }
      byte[] md5sum = digest.digest();
      BigInteger bigInt = new BigInteger(1, md5sum);
      output = bigInt.toString(16);
      for (; output.length() < 32;) {
        output = "0" + output;
      }
    } catch (IOException e) {
      int i = 402;
      throw new RuntimeException("" + i, e);
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        throw new RuntimeException("close failed MD5 calculation", e);
      }
    }
    return output;
  }
}
