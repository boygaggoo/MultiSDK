package com.mf.basecode.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.mf.basecode.utils.FileUtils;

/**
 * 功能说明:
 * 根据SD根目录是否有debug文件目录，目录中是否有U1Mo5Y2zbt.properties文件，存在该文件时即打印日志，若里面有键值对a=b
 * ，则切换联网方式为内网<br>
 */
public class MFSDKConfig {

  public static final String TAG                  = "OOO";

  private static MFSDKConfig instance             = null;

  private Properties         p                    = null;

  // 是否打印日志
  private boolean            isOpenLog            = false;
  // 内外网判断
  private boolean            isDebugMode          = false;
  // 用于判断是否可以短时间内多次初始化
  private boolean            isReInit             = false;

  public static MFSDKConfig getInstance() {
    if (instance == null) {
      instance = new MFSDKConfig();
    }
    return instance;
  }

  private MFSDKConfig() {
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
    initReInit();
  }

  private void initReInit() {
    if (p != null) {
      String c = p.getProperty("c");
      if ("d".equals(c)) {
        isReInit = true;
      }
    }
//    Log.d(TAG, "isReInit = " + isReInit);
  }

  // 如果有该属性且为b，设为内网，否则为现网
  private void initDebugMode() {
    if (p != null) {
      String b = p.getProperty("a");
      if ("b".equals(b)) {
        isDebugMode = true;
      }
    }
//    Log.d(TAG, "initDebugMode = " + isDebugMode);
  }
  /**
   * b=1，则打开log信息
   */
  private void initLog() {
    if (p != null) {
      String b = p.getProperty("b");
      if ("1".equals(b)) {
        isOpenLog = true;
      }
    }
//    Log.d(TAG, "isOpenLog = " + isOpenLog);
  }

  public boolean isOpenLog() {
    return isOpenLog;
  }

  public boolean isDebugMode() {
    return isDebugMode;
  }

  public boolean isReInit() {
    return isReInit;
  }

}
