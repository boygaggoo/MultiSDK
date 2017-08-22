package com.xdd.pay.util;

import java.lang.reflect.Field;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * 文件名称: PhoneInfoUtils.java<br>
 * 作者: 刘晔 <br>
 * 邮箱: ye.liu@ocean-info.com <br>
 * 创建时间：2014-1-21 下午2:24:17<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class PackageInfoUtils {
  public static List<PackageInfo>   packageInfoList          = null;

  /** 
   * 获取应用程序名称 
   */  
  public static String getAppName(Context context)  
  {  
      try  
      {  
          PackageManager packageManager = context.getPackageManager();  
          PackageInfo packageInfo = packageManager.getPackageInfo(  
                  context.getPackageName(), 0);  
          int labelRes = packageInfo.applicationInfo.labelRes;  
          return context.getResources().getString(labelRes);  
      } catch (NameNotFoundException e)  
      {  
          e.printStackTrace();  
      }  
      return "NameNotFound";  
  } 
  
  /**
   * 获取应用版本号
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static int getVersionCode(Context context) {
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
      return pi.versionCode;
    } catch (Exception e) {
    }
    return 0;
  }

  /**
   * 获取应用版本名称
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static String getVersionName(Context context) {
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
      return pi.versionName;
    } catch (Exception e) {
    }
    return "";
  }

  /**
   * 获取包名
   * 
   * @param context
   * @return
   */
  public static String getPackageName(Context context) {
    try {
      return context.getPackageName();
    } catch (Exception e) {
      QYLog.e("get package name error.");
    }
    return "";
  }

  /**
   * 获取AndroidManifest.xml配置信息
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static Bundle getMetaData(Context context) {
    Bundle bundle = null;
    try {
    ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
    bundle = ai.metaData;
    } catch (Exception e) {
      QYLog.e("get meta data error.");
    }
    if (bundle == null) {
      bundle = new Bundle();
    }
    
    return bundle;
  }

  /**
   * 判断APK是否被安装
   * 
   * @param context
   * @param packageName
   * @return
   */
  public static boolean isApkExist(Context context, String packageName) {
    if (packageName == null || "".equals(packageName))
      return false;
    return getPackageInfoByPackageName(context, packageName) != null;
  }

  /**
   * 根据包名获取包信息
   * 
   * @param context
   * @param packageName
   * @return
   */
  public static PackageInfo getPackageInfoByPackageName(Context context, String packageName) {
    if (packageInfoList == null) {
      packageInfoList = context.getPackageManager().getInstalledPackages(0);
    }
    for (int i = 0; i < packageInfoList.size(); i++) {
      PackageInfo p = packageInfoList.get(i);
      if (p.packageName.equals(packageName)) {
        return p;
      }
    }
    return null;
  }

  public static List<PackageInfo> getPackageInfoList(Context context) {
    if (packageInfoList == null) {
      packageInfoList = context.getPackageManager().getInstalledPackages(0);
    }
    return packageInfoList;
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
  public static PackageInfo getPackageInfoFromAPKFile(PackageManager packageManager, String path) {
    return packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
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

  /**
   * 获取apk 安装的时间
   * 
   * @param pInfo
   * @return
   */
  public static long getApkInstallTime(PackageInfo pInfo) {
    long ret = 0;
    Class<?> cPackageInfo = pInfo.getClass();
    Field fTime;
    try {
      fTime = cPackageInfo.getDeclaredField("firstInstallTime");
      fTime.setAccessible(true);
      ret = fTime.getLong(pInfo);
    } catch (Exception e) {
    }
    return ret;
  }

  /**
   * 获取apk 更新的时间
   * 
   * @param pInfo
   * @return
   */
  public static long getApkLastUpdateTime(PackageInfo pInfo) {
    long ret = 0;
    Class<?> cPackageInfo = pInfo.getClass();
    Field fTime;
    try {
      fTime = cPackageInfo.getDeclaredField("lastUpdateTime");
      fTime.setAccessible(true);
      ret = fTime.getLong(pInfo);
    } catch (Exception e) {
    }
    return ret;
  }
}
