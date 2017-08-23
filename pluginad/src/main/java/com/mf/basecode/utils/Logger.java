package com.mf.basecode.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

import com.mf.basecode.config.MFSDKConfig;



public class Logger {
  public static final String      OpenSns = "MfPromSDKLog";
  private static StringBuffer     sb      = new StringBuffer();
  private static SimpleDateFormat sdf     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
  private static File             logFile;
  private static BufferedWriter   bw;
  private static FileWriter       fw;
  private static int              count;
  private static PrintLogThread   printThread;
  private static String           uuidStr = "";

  private static synchronized File getLogFile() {
    if (logFile != null) {
      return logFile;
    }
    int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
    int month = Calendar.getInstance(Locale.CHINA).get(Calendar.MONTH) + 1;
    int day = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH);
    String dastr = year + "-" + month + "-" + day;
    if (logFile == null || !logFile.getName().equals(dastr + ".log")) {
      File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + File.separator + ".moz"
          + File.separator);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      logFile = new File(dir, dastr + ".log");
      try {
        fw = new FileWriter(logFile, true);
        bw = new BufferedWriter(fw);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return logFile;
  }
  /**
   * 输出错误信息
   * 
   * @param tag
   * @param msg
   */
  public static void error(String TAG, String msg, Throwable e) {
    // 根据配置来判断是否打印日志
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.e(TAG, msg);
  }

  /**
   * 调试信息
   * 
   * @param tag
   * @param msg
   */
  public static void error(String TAG, String msg) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.e(TAG, uuidStr + "\t " + msg);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + msg);
  }

  /**
   * 调试信息
   * 
   * @param msg
   */
  public static void error(String msg) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.e(OpenSns, uuidStr + "\t " + msg);
    printLogToFile(sdf.format(new Date()), OpenSns + "\t" + msg);
  }

  /**
   * 调试信息
   * 
   * @param tag
   * @param msg
   */
  public static void debug(String TAG, String msg) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.d(TAG, uuidStr + "\t " + msg);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + msg);
  }

  /**
   * 调试信息
   * 
   * @param tag
   * @param msg
   */
  public static void debug(String msg) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.d(OpenSns, uuidStr + "\t " + msg);
    printLogToFile(sdf.format(new Date()), OpenSns + "\t" + msg);
  }

  public static void logD(String TAG, String str) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.d(TAG, uuidStr + "\t " + str);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + str);
  }

  public static void i(String TAG, String str) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.i(TAG, uuidStr + "\t " + str);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + str);
  }

  public static void d(String TAG, String str) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.d(TAG, uuidStr + "\t " + str);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + str);
  }

  public static void w(String TAG, String str) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.w(TAG, uuidStr + "\t " + str);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + str);
  }

  public static void e(String TAG, String str) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.e(TAG, uuidStr + "\t " + str);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + str);
  }
  
  public static void m(String TAG, String str){
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    Log.e(TAG, uuidStr + "\t " + str);
    printLogToFile(sdf.format(new Date()), TAG + "\t" + str);
  }

  /**
   * 打印log到指定文件
   * 
   * @param tag
   *          文件名
   * @param str
   *          内容 + 时间（自动生成）
   */
  public static synchronized void printLogToFile(final String tag, final String str) {
    if (!MFSDKConfig.getInstance().isOpenLog())
      return;
    sb.append(tag + "\t" + str + "\n");
    if (printThread == null || !printThread.isAlive()) {
      printThread = new PrintLogThread();
      printThread.start();
    }
  }
  /**
   * 打印异常
   * 
   * @param e
   */
  public static void p(final Throwable e) {
    if (MFSDKConfig.getInstance().isOpenLog()) {
      e.printStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      e.printStackTrace(pw);
      pw.flush();
      sw.flush();
      sb.append(sdf.format(new Date()) + "\t " + sw.toString() + "\n");
    }
  }

  static class PrintLogThread extends Thread {

    @Override
    public void run() {
      while (true) {
        if (count >= 10 && sb.length() == 0) {
          break;
        } else if (sb.length() == 0) {
          count++;
          continue;
        } else {
          count = 0;
        }
        try {
          File file = getLogFile();
          if (!file.exists()) {
            file.createNewFile();
          }
          bw.write(sb.toString());
          bw.flush();
          sb.setLength(0);
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          sleep(300);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      try {
        if (bw != null) {
          bw.close();
        }
        if (fw != null) {
          fw.close();
        }
        logFile = null;
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }
}
