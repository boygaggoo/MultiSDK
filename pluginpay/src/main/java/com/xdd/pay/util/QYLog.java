package com.xdd.pay.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.xdd.pay.config.Config;
import com.xdd.pay.util.constant.ExceptionType;

import android.util.Log;

/**
 * 文件名称: Log.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-13 15:08:14<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class QYLog {
  public static final String OpenSns = "paylog";

  /**
   * 输出错误信息
   * 
   * @param tag
   * @param msg
   */
  public static void e(String TAG, String msg, Throwable e) {
    sendExceptionLog(msg, ExceptionType.CATCH_EXCEPTON);
    // 根据配置来判断是否打印日志
    if (!Config.isOpenLog)
      return;
    Log.e(TAG, msg, e);
  }

  /**
   * 调试信息
   * 
   * @param tag
   * @param msg
   */
  public static void e(String TAG, String msg) {
    sendExceptionLog(msg, ExceptionType.CATCH_EXCEPTON);
    if (!Config.isOpenLog)
      return;
    Log.e(TAG, msg);
  }

  /**
   * 调试信息
   * 
   * @param msg
   */
  public static void e(String msg) {
    sendExceptionLog(msg, ExceptionType.CATCH_EXCEPTON);
    if (!Config.isOpenLog)
      return;
    Log.e(OpenSns, msg);
  }

  /**
   * 调试信息
   * 
   * @param tag
   * @param msg
   */
  public static void d(String TAG, String msg) {
    if(TAG.equals("Debuglog")) {
       sendDebugLog(msg, ExceptionType.DEBUGINFO);
    }
    if (!Config.isOpenLog)
      return;
    Log.d(TAG, msg);
  }

  /**
   * 调试信息
   * 
   * @param tag
   * @param msg
   */
  public static void d(String msg) {
    if (!Config.isOpenLog)
      return;
    Log.d(OpenSns, msg);
  }

  public static void i(String TAG, String str) {
    if (!Config.isOpenLog)
      return;
    Log.i(TAG, str);
  }

  public static void w(String TAG, String str) {
    if (!Config.isOpenLog)
      return;
    Log.w(TAG, str);
  }

  /**
   * 打印log到指定文件
   * 
   * @param tag
   *          文件名
   * @param str
   *          内容 + 时间（自动生成）
   */
//  public static void printLogToFile(String tag, String str) {
//    if (!Config.isOpenLog)
//      return;
//    File dir = new File(FileUtils.getFileRootPath() + "/log");// 路径
//    if (!dir.exists()) {
//      dir.mkdirs();
//    }
//    File file = new File(dir, tag);
//    if (!file.exists()) {
//      try {
//        file.createNewFile();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//    long time = System.currentTimeMillis();
//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//    try {
//      FileUtils.writeFile(file, str + " -- " + sdf.format(new Date(time)) + "\n", true);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  /**
   * 删除指定log文件
   * 
   * @param tag
   *          文件名
   */
//  public static void deleteLogFile(String tag) {
//    File f = new File(FileUtils.getFileRootPath() + "/log/" + tag);
//    if (f.exists()) {
//      f.delete();
//    }
//  }

  /**
   * 打印异常
   * 
   * @param e
   */
  public static void p(Throwable e) {
      Writer result = new StringWriter();
      PrintWriter printWriter = new PrintWriter(result);
      e.printStackTrace(printWriter);
      String stacktrace = result.toString();
      sendExceptionLog(stacktrace, ExceptionType.CATCH_EXCEPTON);
      if (Config.isOpenLog) {
          e.printStackTrace();
      }
  }
  
  public static void sendExceptionLog(final String desp, final byte exceptionType) {
      if (PayUtils.isUploadlog) {
      new Thread(){
          public void run(){
              PayUtils.getInstance().sendExceptionLog(desp, false, exceptionType);
          }
      }.start();
      }
  }
  
  public static void sendDebugLog(final String desp, final byte exceptionType) {
      if (PayUtils.islog) {
      new Thread(){
          public void run(){
              PayUtils.getInstance().sendExceptionLog(desp, false, exceptionType);
          }
      }.start();
      }
  }

}
