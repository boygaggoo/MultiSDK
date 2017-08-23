package com.mf.basecode.utils;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.SmsManager;

public class AppInfoUtils {

  /**
   * 获取应用版本号
   * 
   * @param context
   * @return
   * @throws NameNotFoundException
   *           没有找到包名
   */
  public static int getPackageVersionCode(Context context) {
    int version = 0;
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
      version = pi.versionCode;
    } catch (Exception e) {
      Logger.p(e);
    }
    return version;
  }

  /**
   * 获取应用版本名
   * 
   * @param context
   * @return
   * @throws NameNotFoundException
   *           没有找到包名
   */
  public static String getPackageVersionName(Context context) throws NameNotFoundException {
    PackageManager pm = context.getPackageManager();
    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
    return pi.versionName;
  }

  /**
   * 判断SD卡是否存在
   * 
   * @return
   */
  public static boolean isSDCardAvailable() {
    return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
  }

  /**
   * 判断activity是否在当前界面
   * 
   * @param context
   * @param activityName
   * @return
   */
  public static boolean isActivityOnTop(Context context, String activityName) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
    return activityName.equals(cn.getClassName());
  }

  /**
   * 获取当前界面的activityName
   * 
   * @param context
   * @return
   */
  public static String getTopActivityName(Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    String topActivityName = "";
    try {
      ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
      topActivityName = cn.getClassName();
    } catch (Exception e) {
    }
    return topActivityName;
  }

  /**
   * 获取当前界面的packageNmae
   * 
   * @param context
   * @return
   */
  public static String getTopPackageName(Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    String topPackageName = "";
    try {
      ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
      topPackageName = cn.getPackageName();
    } catch (Exception e) {
    }
    return topPackageName;
  }

  /**
   * 根据apk文件获取应用信息
   * 
   * @param packageManager
   * @param apkFile
   * @return
   */
  public static PackageInfo getPackageInfoFromAPKFile(PackageManager packageManager, File apkFile) {
    return packageManager.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
  }

  /**
   * 启动其他应用
   * 
   * @param context
   * @param packageName
   */
  public static void launchOtherActivity(Context context, String packageName, Bundle b) {
    Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
    if (i == null) {
      i = new Intent(packageName);
      i.setAction(Intent.ACTION_MAIN);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    if (b != null) {
      i.putExtras(b);
    }
    if (i != null) {
      try {
        context.startActivity(i);
      } catch (Exception e) {
        Logger.p(e);
      }
    }
  }

  /**
   * 发送短信
   * 
   * @param context
   * @param num
   *          号码
   * @param msg
   *          信息
   */
  public static void sendSms(Context context, String num, String msg) {
    SmsManager sms = SmsManager.getDefault();
    PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
    List<String> texts = sms.divideMessage(msg);
    for (String text : texts) {
      sms.sendTextMessage(num, null, text, pi, null);
    }
  }

  /**
   * 回到手机桌面
   * 
   * @param context
   */
  public static void backHome(Context context) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

}
