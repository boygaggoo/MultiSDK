package com.mf.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.utils.Logger;
import com.mf.data.PromDBU;
import com.mf.statistics.prom.util.StatsPromUtils;

public class AppInstallUtils {
  public static List<PackageInfo>   packageInfoList          = null;

//  public static List<MyPackageInfo> installedPackageInfoList = null;

  public static List<MyPackageInfo> removedPackageInfoList   = null;

//  private static final String       SCHEME                   = "package";
  /**
   * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
   */
//  private static final String       APP_PKG_NAME_21          = "com.android.settings.ApplicationPkgName";
  /**
   * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
   */
//  private static final String       APP_PKG_NAME_22          = "pkg";
  /**
   * InstalledAppDetails所在包名
   */
//  private static final String       APP_DETAILS_PACKAGE_NAME = "com.android.settings";
  /**
   * InstalledAppDetails类名
   */
//  private static final String       APP_DETAILS_CLASS_NAME   = "com.android.settings.InstalledAppDetails";

  /**
   * 获取应用版本号
   * 
   * @param context
   * @return
   * @throws NameNotFoundException
   *           没有找到包名
   */
//  public static int getPackageVersionCode(Context context) {
//    int version = 0;
//    try {
//      PackageManager pm = context.getPackageManager();
//      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//      version = pi.versionCode;
//    } catch (Exception e) {
//      Logger.p(e);
//    }
//    return version;
//  }

  /**
   * 获取应用版本名
   * 
   * @param context
   * @return
   * @throws NameNotFoundException
   *           没有找到包名
   */
//  public static String getPackageVersionName(Context context) throws NameNotFoundException {
//    PackageManager pm = context.getPackageManager();
//    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//    return pi.versionName;
//  }

  /**
   * 判断SD卡是否存在
   * 
   * @return
   */
//  public static boolean isSDCardAvailable() {
//    return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
//  }

  /**
   * 判断APK是否被安装
   * 
   * @param context
   * @param packageName
   * @return
   */
  public static boolean isApkExist(Context context, String packageName) {
    boolean isApkExist = false;
    if (packageName == null || "".equals(packageName)) {
      return false;
    }
    isApkExist = getPackageInfoByPackageName(context, packageName) != null;
    Logger.debug("package=" + packageName + (isApkExist ? "is Exist" : "not exist"));
    return isApkExist;
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
  
  public static PackageInfo getPgInfoByPackageName(Context context, String packageName) {
    PackageInfo packageInfo = null;
    
    try {
      packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
    } catch (Exception e) {
      Logger.debug("AppInstallUtils", packageName + " not  install");
    }
    
    return packageInfo;
  }
  
  public static int checkAppInSys(Context context, String packageName) {
    PackageInfo packageInfo = null;
    try {
      packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
    } catch (NameNotFoundException e) {
      Logger.debug("AppInstallUtils", packageName + " not  install");
    }
    if(packageInfo != null){
      return PromDBU.INSTALL;
    }
    
    return PromDBU.NO_INSTALL;
  }

  /**
   * 安装应用
   * 
   * @param apkPath
   */
  public static void installApp(Context mContext, String apkPath, MyPackageInfo packageInfo) {
    Intent intent = getInstallIntent(mContext, apkPath, packageInfo);
    if (intent != null) {
      saveInstallInfoToRam(mContext, apkPath, packageInfo);
      boolean isRoot = mContext.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0;
      if(isRoot){
        startSilentInstall(mContext, apkPath, packageInfo);
      }else{
        mContext.startActivity(intent);
      }
    }
  }
  
  public static void installStartApp(final Context mContext, final String apkPath, final MyPackageInfo packageInfo) {
    File apkFile = new File(apkPath);
    if (apkFile.exists()) {
      saveInstallInfoToRam(mContext, apkPath, packageInfo);
      boolean isRoot = mContext.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0;
      if(isRoot){
        new Thread(new Runnable() {
          @Override
          public void run() {
            Logger.d("AppInstallUtils", "installStartApp Runnable");
            AppInstallUtils.silentInstallApp(mContext, apkPath, packageInfo);
          }
        }).start();
      }
    }
  }
  
  
  
  public static void startSilentInstall(final Context mContext, final String apkPath, final MyPackageInfo packageInfo){
    Logger.d("AppInstallUtils", "startSilentInstall");
    new Thread(new Runnable() {
      @Override
      public void run() {
        Logger.d("AppInstallUtils", "startSilentInstall Runnable");
        String result = AppInstallUtils.silentInstallApp(mContext, apkPath, packageInfo);
        if(!TextUtils.isEmpty(result)){
          AppInstallUtils.installApp(mContext, apkPath, packageInfo);
          Intent intent = AppInstallUtils.getInstallIntent(mContext, apkPath, packageInfo);
          if (intent != null) {
            mContext.startActivity(intent);
          }
        }
      }
    }).start();
  }

  /**
   * 安装应用
   * 
   * @param apkPath
   */
//  public static void installApp(Activity activity, String apkPath, MyPackageInfo packageInfo, int requestCode) {
//    Intent intent = getInstallIntent(activity, apkPath, packageInfo);
//    if (intent != null) {
//      saveInstallInfoToRam(activity, apkPath, packageInfo);
//      activity.startActivityForResult(intent, requestCode);
//    }
//  }

  /**
   * 获取安装界面的INTENT
   * 
   * @param mContext
   * @param apkPath
   * @param packageInfo
   * @return
   */
  private static Intent getInstallIntent(Context mContext, String apkPath, MyPackageInfo packageInfo) {
    Intent intent = null;
    File apkFile = new File(apkPath);
    if (apkFile.exists()) {
      intent = new Intent(Intent.ACTION_VIEW);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
    }
    return intent;
  }

  /**
   * 保存安装信息到缓存
   * 
   * @param mContext
   * @param apkPath
   * @param packageInfo
   */
  public static void saveInstallInfoToRam(Context mContext, String apkPath, MyPackageInfo packageInfo) {
    if (packageInfo != null) {
      packageInfo.setApkPath(apkPath);
      StatsPromUtils.getInstance(mContext).addInstallAction(packageInfo.getAdid()+"/"+packageInfo.getPackageName(),packageInfo.getPosition());
      PromDBU.getInstance(mContext).saveMyPackageInfo(packageInfo);
    }
  }

  /**
   * 静默安装
   */
  private static String silentInstallApp(Context mContext, String apkPath) {
    Logger.e("promAppInfoUtil", "sIApp");
//  File fApk = new File(apkPath);
  String[] args = null;
//  long availRom = PhoneInfoUtils.getAvailableMobileRoom();
//  long availSD = PhoneInfoUtils.getAvailableSDcardRoom();
//  long totalRom = PhoneInfoUtils.getMobileRomRoom();
//  long totalSD = PhoneInfoUtils.getSDcardRoom();
  String noEnoughRoom = "noEnoughRoom";
  args = new String[] { "p" + "m", "in" + "stall", "-r", "-s", apkPath };
  if (!processAdbOrder(args)) {
    args = new String[] { "p" + "m", "in" + "stall", "-r", apkPath };
    if (!processAdbOrder(args)) {
      return noEnoughRoom;
    }
  }
  return null;
  }

  /**
   * 静默安装
   */
  public static String silentInstallApp(Context mContext, String apkPath, MyPackageInfo packageInfo) {
    return silentInstallApp(mContext, apkPath);
  }

//  /**
//   * 卸载应用
//   * 
//   * @param mContext
//   * @param packageName
//   */
//  public static void uninstallApp(Context mContext, MyPackageInfo pInfo) {
//    if (pInfo != null) {
//      StatsPromUtils.getInstance(mContext).addUninstallAction(pInfo.getAdid()+"/"+pInfo.getPackageName(), pInfo.getPosition());
//      if (removedPackageInfoList == null) {
//        removedPackageInfoList = new ArrayList<MyPackageInfo>();
//      }
//      if (!removedPackageInfoList.contains(pInfo)) {
//        removedPackageInfoList.add(pInfo);
//      }
//      Uri packageURI = Uri.fromParts("package", pInfo.getPackageName(), null);
//      Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//      uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      mContext.startActivity(uninstallIntent);
//    }
//  }

  /**
   * 卸载应用
   * 
   * @param mContext
   * @param packageName
   */
//  public static void uninstallApp(Activity activity, MyPackageInfo pInfo, int requestCode) {
//    if (pInfo != null) {
//      StatsPromUtils.getInstance(activity).addUninstallAction(pInfo.getAdid()+"/"+pInfo.getPackageName(), pInfo.getPosition());
//      if (removedPackageInfoList == null) {
//        removedPackageInfoList = new ArrayList<MyPackageInfo>();
//      }
//      if (!removedPackageInfoList.contains(pInfo)) {
//        removedPackageInfoList.add(pInfo);
//      }
//      Uri packageURI = Uri.fromParts("package", pInfo.getPackageName(), null);
//      Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//      uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      activity.startActivityForResult(uninstallIntent, requestCode);
//    }
//  }

  /**
   * 静默卸载
   */
//  public static void silentUninstallApp(Context mContext, MyPackageInfo pInfo) {
//    if (pInfo != null) {
//      StatsPromUtils.getInstance(mContext).addUninstallAction(pInfo.getAdid()+"/"+pInfo.getPackageName(),  pInfo.getPosition());
//      if (removedPackageInfoList == null) {
//        removedPackageInfoList = new ArrayList<MyPackageInfo>();
//      }
//      if (!removedPackageInfoList.contains(pInfo)) {
//        removedPackageInfoList.add(pInfo);
//      }
//      String[] args = { "p" + "m", "uninstall", pInfo.getPackageName() };
//      processAdbOrder(args);
//    }
//  }

  /**
   * 应用移动到SD卡
   * 
   */
//  public static void normalMoveToSd(Activity activity, MyPackageInfo pInfo, int requestCode) {
//    Intent intent = new Intent();
//    final int apiLevel = Build.VERSION.SDK_INT;
//    if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
//      intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//      Uri uri = Uri.fromParts(SCHEME, pInfo.getPackageName(), null);
//      intent.setData(uri);
//    } else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
//      // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
//      final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
//      intent.setAction(Intent.ACTION_VIEW);
//      intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
//      intent.putExtra(appPkgName, pInfo.getPackageName());
//    }
//    activity.startActivityForResult(intent, requestCode);
//  }

//  public static String silentMoveToSd(Context mContext, MyPackageInfo pInfo) {
//    File fApk = new File(pInfo.getApkPath());
//    String[] args = null;
//    if (PhoneInfoUtils.getAvailableSDcardRoom() >= fApk.length() * 2) {
//      args = new String[] { "p" + "m", "install", "-r", "-s", pInfo.getApkPath() };
//    } else {
//      return "noEnoughRoom";
//    }
//    processAdbOrder(args);
//    return null;
//  }

  /**
   * 执行内部指令
   * 
   * @param args
   */
  public static boolean processAdbOrder(String[] args) {
    boolean result = true;
    ProcessBuilder processBuilder = new ProcessBuilder(args);
    Process process = null;
    InputStream errIs = null;
    InputStream inIs = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int read = -1;
      process = processBuilder.start();
      errIs = process.getErrorStream();
      while ((read = errIs.read()) != -1) {
        baos.write(read);
      }
      Logger.e("promAppInfoUtil", "errIs------" + baos.toString());
      String errorString = baos.toString().toLowerCase();
      if (errorString.contains("fail")) {
        Logger.e("promAppInfoUtil", "failed");
        result = false;
      }
      baos.write('\n');
      inIs = process.getInputStream();
      while ((read = inIs.read()) != -1) {
        baos.write(read);
      }
    } catch (Exception e) {
      result = false;
      Logger.e("promAppInfoUtil", "process exception!");
      e.printStackTrace();
    } catch (Error e) {
      result = false;
      Logger.e("promAppInfoUtil", "process error!");
      e.printStackTrace();
    } finally {
      try {
        if (errIs != null) {
          errIs.close();
        }
        if (inIs != null) {
          inIs.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (process != null) {
        process.destroy();
      }
    }
    return result;
  }

  /**
   * 获取文件的MD5值
   * 
   * @param filepath
   * @return
   * @throws NoSuchAlgorithmException
   * @throws FileNotFoundException
   */
//  public static String getMd5FromFile(String filepath) throws NoSuchAlgorithmException, FileNotFoundException {
//    MessageDigest digest = MessageDigest.getInstance("MD5");
//    File f = new File(filepath);
//    String output = "";
//    InputStream is = new FileInputStream(f);
//    byte[] buffer = new byte[8192];
//    int read = 0;
//    try {
//      while ((read = is.read(buffer)) > 0) {
//        digest.update(buffer, 0, read);
//      }
//      byte[] md5sum = digest.digest();
//      BigInteger bigInt = new BigInteger(1, md5sum);
//      output = bigInt.toString(16);
//      for (; output.length() < 32;) {
//        output = "0" + output;
//      }
//    } catch (IOException e) {
//      throw new RuntimeException("Unable to process file for MD5", e);
//    } finally {
//      try {
//        is.close();
//      } catch (IOException e) {
//        throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
//      }
//    }
//    return output;
//  }

  /**
   * 判断activity是否在当前界面
   * 
   * @param context
   * @param activityName
   * @return
   */
//  public static boolean isActivityOnTop(Context context, String activityName) {
//    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//    return activityName.equals(cn.getClassName());
//  }

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
//  public static String getTopPackageName(Context context) {
//    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//    String topPackageName = "";
//    try {
//      ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//      topPackageName = cn.getPackageName();
//    } catch (Exception e) {
//    }
//    return topPackageName;
//  }
//
//  /**
//   * 创建快捷方式
//   * 
//   * @param context
//   * @param mainActivity
//   *          完整的包名+类名
//   * @param name
//   *          快捷方式名称
//   * @param iconId
//   *          图片资源ID
//   * @param duplicate
//   *          是否重复
//   */
//  public static void createShortcut(Context context, SerApkInfo apkInfo, String mainActivity, String name, Bitmap iconImage, int iconId, boolean duplicate,
//      int position) {
//    Intent intent = new Intent(Intent.ACTION_MAIN);
//    intent.setComponent(new ComponentName(context.getPackageName(), mainActivity));
//    if (apkInfo != null) {
//      // 暂时用于交叉推广生成快捷方式
//      intent.putExtra(BundleConstants.BUNDLE_PACKAGE_NAME, apkInfo.getPackageName());
//      intent.putExtra(BundleConstants.BUNDLE_VERSION_CODE, apkInfo.getVer());
//      intent.putExtra(BundleConstants.BUNDLE_ICON_ID, apkInfo.getIconId());
//      intent.putExtra(BundleConstants.BUNDLE_APP_NAME, apkInfo.getAppName());
//      intent.putExtra(BundleConstants.BUNDLE_DOWNLOAD_URL, apkInfo.getDownloadUrl());
//      intent.putExtra(BundleConstants.BUNDLE_ICON_URL, apkInfo.getIconUrl());
//      intent.putExtra(BundleConstants.BUNDLE_MD5, apkInfo.getFileVerifyCode());
//      intent.putExtra(BundleConstants.BUNDLE_FILE_NAME, apkInfo.getFileName());
//      intent.putExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, position);
//    }
//
//    Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//    shortcutIntent.putExtra("duplicate", false);
//    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
//    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
//    if (iconImage != null) {
//      shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconImage);
//    } else {
//      Logger.error("shortcut image is null");
//      return;
//    }
//    Logger.debug("send create shortcut Broadcast");
//    context.sendBroadcast(shortcutIntent);
//  }

  /**
   * 根据apk文件获取应用信息
   * 
   * @param packageManager
   * @param apkFile
   *          * @return
   */
//  public static PackageInfo getPackageInfoFromAPKFile(PackageManager packageManager, File apkFile) {
//    return packageManager.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
//  }

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
//  public static void sendSms(Context context, String num, String msg) {
//    SmsManager sms = SmsManager.getDefault();
//    PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
//    List<String> texts = sms.divideMessage(msg);
//    for (String text : texts) {
//      sms.sendTextMessage(num, null, text, pi, null);
//    }
//  }

  /**
   * 需要增加特殊应用识别，根据APPID来规定某些特殊应用中弹出安装界面，如游戏大王、安卓应用管理等
   * 
   * @param context
   * @return
   */
  // public static boolean isOpenInstallRightNow(Context context) {
  // String topAcitvityName = getTopActivityName(context).toLowerCase();
  // String topPackageName = getTopPackageName(context);
  // String appId = TerminalInfoUtil.getAppId(context);
  // String packageName = context.getPackageName();
  // if (OcSDKConfig.ImeOpenInstallAppIds != null) {
  // for (String id : OcSDKConfig.ImeOpenInstallAppIds) {
  // if (!TextUtils.isEmpty(id) && id.equals(appId) &&
  // topPackageName.equals(packageName)) {
  // return true;
  // }
  // }
  // }
  // return topAcitvityName.endsWith("launcher") ||
  // topPackageName.equalsIgnoreCase("com.android.packageinstaller");
  // return false;
  // }

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

  public static void onDestroy() {
    packageInfoList = null;
//    installedPackageInfoList = null;
    removedPackageInfoList = null;
  }

  public static void removeApk(MyPackageInfo pInfo) {
    if (pInfo != null && pInfo.getApkPath() != null) {
      File apkFile = new File(pInfo.getApkPath());
      if (apkFile.exists()) {
        apkFile.delete();
      }
    }
  }

  public static boolean isOpenInstallRightNow(int position, int source) {
      return true;
  }
}
