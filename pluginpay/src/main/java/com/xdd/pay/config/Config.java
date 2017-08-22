package com.xdd.pay.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.xdd.pay.util.FileUtils;

public class Config {
  public static final String SDK_VERSION_NAME = "1.0.0.2";

  public static final int    SDK_VERSION_CODE = 1002;

  private static Properties  p                = null;

  // 是否打印日志
  public static boolean      isOpenLog        = false;
  // 内外网判断
  public static boolean      isDebugMode      = false;

  static {
    if (p == null) {
      File myFile = FileUtils.getDebugFile();
      if (myFile != null && myFile.exists()) {
        p = new Properties();
        FileInputStream fis = null;
        try {
          fis = new FileInputStream(myFile);
          p.load(fis);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (fis != null) {
            try {
              fis.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
    initDebugMode();
    initLog();
  }

  // 如果有该属性且为b，设为内网，否则为现网
  private static void initDebugMode() {
    if (p != null) {
      String b = p.getProperty("a");
      if ("1".equals(b)) {
        isDebugMode = true;
      }
    }
  }
  /**
   * b=1，则打开log信息
   */
  private static void initLog() {
    if (p != null) {
      String b = p.getProperty("b");
      if ("1".equals(b)) {
        isOpenLog = true;
      }
    }
  }
}
