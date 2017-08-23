package com.mf.promotion.util;

import com.mf.promotion.service.MFApkService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.text.TextUtils;

import com.mf.basecode.data.DBUtils;
import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.network.object.PgInfo;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.FileUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.data.PromDBU;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.network.object.AdInfo;
import com.mf.promotion.activity.PromDesktopAdActivity;
import com.mf.promotion.activity.PromHomeWapScreenActivity;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.utils.AppInstallUtils;

public class PromUtils {
  public static final String       TAG       = "PromotionUtils";

  private static PromUtils         mInstance = null;
  private static Context           mContext  = null;

  private String                   packageName;
  private ArrayList<MyPackageInfo> commonShortcutInfoList;
  public static String blackListstr = "";
//  public static String sdkPackageStr = "";

  public static PromUtils getInstance(Context context) {
    mContext = context;
    if (mInstance == null) {
      mInstance = new PromUtils();
    }
    return mInstance;
  }

  private PromUtils() {
    packageName = mContext.getPackageName();
  }

  /**
   * 创建交叉推广推送通知
   * 
   */
  public void showPushNotify(AdDbInfo appInfo) {
    NotiUitl.getInstance(mContext).showNotification(appInfo, clickPromAppInfoListener(appInfo.getAdType(),appInfo.getAdId(),appInfo.getPromType(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY));
  }

  public void showDefinedNotify(AdDbInfo appInfo, Intent clickIntent) {
    NotiUitl.getInstance(mContext).showNotification(appInfo, clickIntent);
  }
  public void saveShortcutInfo(AdDbInfo info) {
    // 保存创建过的快捷方式信息：数据库和SD卡上双份
//    PromDBU.getInstance(mContext).insertShortcut(info.getPackageName());
    PromUtils.getInstance(mContext).saveInfoToSD(PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
  }
  
//  public void deleteShortcutInfo(AdDbInfo info){
//    PromUtils.getInstance(mContext).deleteInfoToSD(PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
//  }

  /**
   * 根据数据库和sd卡上的信息判断是否已经创建错该快捷方式
   * 
   * @param info
   * @return ture:已经存在；false：不存在
   */
  public boolean isShortcutExists(AdInfo info) {
    boolean result = false;
    try {
      Logger.e(TAG, "isShortcutExists- form SD  = " + (PromUtils.getInstance(mContext).isInfoExistFormSD(PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()))));
      if (PromUtils.getInstance(mContext).isInfoExistFormSD(getPicNameFromPicUrl(info.getAdPicUrl()))) {
      result = true;
      }
    } catch (Exception e) {
      Logger.p(e);
    }
    
    return result;
  };
  
  public boolean isShortcutExists(AdDbInfo info) {
    boolean result = false;
    try {
      Logger.e(TAG, "isShortcutExists- form SD  = " + (PromUtils.getInstance(mContext).isInfoExistFormSD(PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()))));
      if (PromUtils.getInstance(mContext).isInfoExistFormSD(info.getPicName())) {
      result = true;
      }
    } catch (Exception e) {
      Logger.p(e);
    }
    return result;
  };

  /**
   * 删除应用下载的文件
   * 
   * @param packageName
   * @param version
   */
  public void deleteAppFile(String packageName, int version) {
    String file1 = DownloadUtils.getInstance(mContext).getApkDownloadPath(packageName) + "/" + packageName + "_r" + version + ".app";
    String file2 = DownloadUtils.getInstance(mContext).getApkDownloadPath(packageName) + "/" + packageName + "_r" + version + ".tmp";
    File f1 = new File(file1);
    if (f1.exists()) {
      f1.delete();
    }
    File f2 = new File(file2);
    if (f2.exists()) {
      f2.delete();
    }
  }

  public String getPackageName() {
    return packageName;
  }

  /*
   * 展示桌面图片广告
   */
  public void showDesktopAdImage(String ads) {
    if (mContext == null) {
      return;
    }
//    if (inSelfApp()) {
//      Logger.debug(TAG, "inSelfApp ");
//      return;
//    }
    Logger.debug(TAG, "showDesktopAdImage");
    // 判断是否处于桌面状态
//    if (!TextUtils.isEmpty(adInfo.getInApp())
//        && ((isOnLauncher() && adInfo.getInApp().equals("0")) || (isInApp() && adInfo.getInApp().equals("1")) || adInfo.getInApp().equals("2"))) {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.setComponent(new ComponentName(mContext.getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
      intent.putExtra(PromApkConstants.EXTRA_CLASS, PromDesktopAdActivity.class.getCanonicalName());
      intent.putExtra(BundleConstants.BUNDLE_DESKTOP_ADS, ads);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      Logger.debug(TAG, "showDesktopAdImage start PromDesktopAdActivity ");
      mContext.startActivity(intent);
//    } else {
//      Logger.debug(TAG, "not on launch, does not show the ad. ");
//    }
  }

  /**
   * 判断是否是处于桌面
   * 
   * @return
   */
  private boolean isOnLauncher() {
    ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
    String runningPackage = rti.get(0).topActivity.getPackageName();
    ArrayList<String> desktopAppPackages = getHomePackages(mContext);
    if (desktopAppPackages.contains(runningPackage)) {
      return true;
    } else {
      String topAcitvityName = AppInstallUtils.getTopActivityName(mContext).toLowerCase();
      if (topAcitvityName.contains("launcher")) {
        return true;
      } else {
        return false;
      }
    }
  }

  public static ArrayList<String> getHomePackages(Context context) {
    ArrayList<String> names = new ArrayList<String>();
    PackageManager packageManager = context.getPackageManager();
    // 属性
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    for (ResolveInfo ri : resolveInfo) {
      names.add(ri.activityInfo.packageName);
    }
    return names;
  }

  /**
   * 判断是否是处于应用内
   * 
   * @return
   */
  public boolean isInApp() {
    try {
      if (isOnLauncher()) {
        return false;
      }
      
      ActivityManager manager = (ActivityManager) mContext.getSystemService(Service.ACTIVITY_SERVICE);
      List<RunningTaskInfo> tasksInfo = manager.getRunningTasks(1);
      if (tasksInfo.size() > 0) {
        String topActivityName = tasksInfo.get(0).topActivity.getClassName();
        if(topActivityName.equals(PromApkConstants.HOST_PROXY_ACTIVITY)){
          return false;
        }
        String topPackageName = tasksInfo.get(0).topActivity.getPackageName();
        if(topPackageName.equals(mContext.getPackageName())){
          return false;
        }
        PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(topPackageName, 0);
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
          // 第三方应用
          if(TextUtils.isEmpty(blackListstr)){
            blackListstr = FileUtils.getConfigByNameFromFile(mContext,PromApkConstants.PROM_DESKTOP_AD_BLACKLIST);
            Logger.d(TAG, "blackListstr  == " + blackListstr);
          }
          String[] blackList = blackListstr.split(";");
          for (String string : blackList) {
            Logger.d(TAG, "string  == " + string+"   topPackageName = "+topPackageName);
            if (topPackageName.contains(string) && !TextUtils.isEmpty(string)) {
              Logger.d(TAG, "blacklist  app  on top" + topPackageName);
              return false;
            }
          }
          String  sdkPackageStr = getValueByNameFromSDKInfoFile(CommConstants.SDKS_INFO_KEY);
          String[] sdkPackagelist = sdkPackageStr.split("&");
          for (String sdkpgname : sdkPackagelist) {
            if(!TextUtils.isEmpty(sdkpgname) && topPackageName.equals(sdkpgname)){
              Logger.d(TAG, "sdkPackagelist  app  on top" + topPackageName);
              return false;
            }
          }
          Logger.d(TAG, "third party  app  on top" + topPackageName);
          return true;
        } else {
          Logger.d(TAG, "system  app  on top" + topPackageName);
          return false;
        }

      }
    } catch (Exception e) {
      // TODO: handle exception
    }

    return true;
  }
  
  public String getTopPackageName(){
    String topPackageName = "";
    ActivityManager manager = (ActivityManager) mContext.getSystemService(Service.ACTIVITY_SERVICE);
    List<RunningTaskInfo> tasksInfo = manager.getRunningTasks(1);
    if (tasksInfo.size() > 0) {
      topPackageName = tasksInfo.get(0).topActivity.getPackageName();
      
    }
    return topPackageName;
    
  }
  
  
  public boolean isInRunningTasks(String pkage){
    String packageName = "";
    ActivityManager manager = (ActivityManager) mContext.getSystemService(Service.ACTIVITY_SERVICE);
    List<RunningTaskInfo> tasksInfo = manager.getRunningTasks(15);
    if (tasksInfo.size() > 0) {
      for (RunningTaskInfo runningTaskInfo : tasksInfo) {
        packageName = runningTaskInfo.topActivity.getPackageName();
        Logger.e("HandleExitService", "packageName = "+packageName);
        if(packageName.equals(pkage)){
          return true;
        }
      }
    }
    return false;
    
  }
  /**
   * 点击图标或广告触发的事件
   */
  public Intent clickPromAppInfoListener(int adType,String adid,int promType, int position) {
    Intent intent = null;
    switch (adType) {
    case PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK:
      intent = new Intent();
      intent.setClassName(mContext, MFApkService.class.getName());
      intent.putExtra(BundleConstants.BUNDLE_KEY_SERVICE_ID_APK, MFApkServiceFactory.HANDLER_APP_SERVICE.getServiceId());
      intent.putExtra(BundleConstants.BUNDLE_AD_INFO_ADID, adid);
      intent.putExtra(BundleConstants.BUNDLE_AD_INFO_PROM_TYPE, promType);
      intent.putExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, position);
      break;
    case PromApkConstants.PROM_AD_INFO_ACTION_TYPE_WAP:
      intent = new Intent();
      intent.setClassName(mContext, PromHomeWapScreenActivity.class.getName());
      intent.putExtra(PromApkConstants.EXTRA_CLASS, PromHomeWapScreenActivity.class.getCanonicalName());
      intent.putExtra(BundleConstants.BUNDLE_AD_INFO_ADID, adid);
      intent.putExtra(BundleConstants.BUNDLE_AD_INFO_PROM_TYPE, promType);
      intent.putExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, position);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      break;
//    case PromApkConstants.PROM_AD_INFO_ACTION_TYPE_LIST:
//      Logger.debug(TAG, "PROM_AD_INFO_ACTION_TYPE_LIST=3");
//      break;
    }
    return intent;
  }

  /**
   * 保存已生成过的应用信息至SD卡 <br>
   * 如快捷方式和静默安装 <br>
   * 只根据所提供的文件名创建文件，目的：防止重复安装或重复出现
   * 
   */
  public void saveInfoToSD(String fileName) {
    Logger.debug(TAG, "saveInfoToSD -->" + fileName);
    String path = FileConstants.getFileRootDirectory(mContext) + "info";

    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
    File infoFile = new File(path + "/" + fileName);
    if (!infoFile.exists()) {
      try {
        infoFile.createNewFile();
        Logger.debug(TAG, "create new file " + infoFile.getName());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void deleteInfoToSD(String fileName){
    Logger.debug(TAG, "deleteInfoToSD -->" + fileName);
    String path = FileConstants.getFileRootDirectory(mContext) + "info";

    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
    File infoFile = new File(path + "/" + fileName);
    if (infoFile.exists()) {
      infoFile.delete();
    }
  }

  /**
   * 根据相应信息判断是否已生成过快捷方式
   * 
   * @return
   */
  public boolean isInfoExistFormSD(String fileName) {
    String path = FileConstants.getFileRootDirectory(mContext) + "info/";
    File file = new File(path + fileName);
    if (file.exists()) {
      return true;
    } else {
      return false;
    }
  }

  // 获取快捷方式列表
  public ArrayList<MyPackageInfo> getCommonShortcutInfoList() {
    return commonShortcutInfoList;
  }
  public void addCommonShortcutInfo(MyPackageInfo packageInfo) {
    if (commonShortcutInfoList == null) {
      commonShortcutInfoList = new ArrayList<MyPackageInfo>();
    }
    commonShortcutInfoList.add(packageInfo);
  }

  public void removeCommonShortcutInfo(MyPackageInfo packageInfo) {
    if (commonShortcutInfoList != null) {
      commonShortcutInfoList.remove(packageInfo);
    }
  }
  /**
   * 优先静默
   * 
   * @param hasRoot
   * @param apkPath
   * @param info
   */
  public void install(boolean hasRoot, String apkPath, MyPackageInfo info) {
    if (info != null) {
      info.setApkPath(apkPath);
      if (!hasRoot || mContext.getPackageName().equals(info.getPackageName())) {
        AppInstallUtils.installApp(mContext, apkPath, info);
      } else {
        info.setImeOpen(false);
        AppInstallUtils.saveInstallInfoToRam(mContext, apkPath, info);
        String error = AppInstallUtils.silentInstallApp(mContext, apkPath, info);
        if (error != null) {
          try {
            PromDBU.getInstance(mContext).deleteMyPackageInfoByPackageName(info.getPackageName());
          } catch (Exception e) {
          }
        } else {
          // 安装后存储安装信息
//          Logger.debug(TAG, "hasRoot=" + hasRoot + " ---install success and apk path = " + apkPath + " and save install info to DB");
//          addInstalledInfo(info);
        }
      }
    }
  }

//  private void addInstalledInfo(MyPackageInfo info) {
//    String tmpNameString = EncryptUtils.getEncrypt(info.getPackageName());
//    PromDBU.getInstance(mContext).insertCfg(tmpNameString, "true");
//  }

//  public boolean isInstalledBefore(String packageName) {
//    boolean result = false;
//    String tmpNameString = EncryptUtils.getEncrypt(packageName);
//    String isInstalled = PromDBU.getInstance(mContext).queryCfgValueByKey(tmpNameString);
//    if (!TextUtils.isEmpty(isInstalled) || isInfoExistFormSD(tmpNameString)) {
//      result = true;
//    }
//    return result;
//  }

//  public boolean isInstalled(String packageName) {
//    boolean result = false;
//    String tmpPackName = EncryptUtils.getEncrypt(packageName);
//    String value = PromDBU.getInstance(mContext).queryCfgValueByKey(tmpPackName);
//    if (!TextUtils.isEmpty(value)) {
//      result = true;
//    }
//    return result;
//  }

  private File getApkFile(String fileName) {
    File f = new File(getDefinedPath(), fileName);
    File apkFile = new File(getDefinedPath(), EncryptUtils.getEncrypt(getPackageNameFormFile(fileName)));
    if (f.exists()) {
      f.renameTo(apkFile);
    }
    return apkFile;
  }

  private String getPackageNameFormFile(String name) {
    String packageName = "";
    try {
      PackageManager pm = mContext.getPackageManager();
      PackageInfo info = pm.getPackageArchiveInfo(getDefinedPath() + name, PackageManager.GET_ACTIVITIES);
      if (info != null) {
        ApplicationInfo appInfo = info.applicationInfo;
        packageName = appInfo.packageName;
      }
    } catch (Exception e) {
      Logger.p(e);
    }
    return packageName;
  }

  public void removeDefinedApk(String packageName) {
    deleteDefinedFileByPath(getDefinedPath() + EncryptUtils.getEncrypt(packageName));
  }

  public void deleteDefinedFileByPath(String path) {
    if (!TextUtils.isEmpty(path)) {
      File file = new File(path);
      if (file.exists()) {
        boolean result = file.delete();
        Logger.debug("MfPromDefinde", "delete the file = " + path + " and delete result = " + result);
      }
    }
  }
  public String getDefinedPath() {
    return "/data/data/" + mContext.getPackageName() + "/";
  }

  public List<String> getDefineFiles() {
    File file = new File(getDefinedPath());
    List<String> fileNames = new ArrayList<String>();
    if (file.exists() && file.canRead()) {
      fileNames = Arrays.asList(file.list());
      for (String a : fileNames) {
        Logger.debug(TAG, "file:" + a);
      }
    }
    return fileNames;
  }

  public static final String installedInfoFile   = ".iimf";
  public static final String unInstalledInfoFile = ".uimf";

  /**
   * 将所有SDK接收到的APK包卸载记录到数据库及本地SD卡中
   * 
   * @param packageInfo
   */
  public void saveUnstalledInfo(MyPackageInfo packageInfo) {
    // 记录到数据库
    if (hasUninstalled(packageInfo)) {
      return;
    }
    PromDBU.getInstance(mContext).addInstalledApkInfo(packageInfo);
    // 记录到本地SD卡, /sdcard/.com.android.app/.uidb
    File dirFile = new File(FileConstants.getFileRootDirectory(mContext));
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }
    File file = new File(dirFile, unInstalledInfoFile);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        Logger.p(e);
      }
    }
    try {
      FileUtils.writeFile(file, packageInfo.getPackageName() + "&" + packageInfo.getVersionCode() + "\n", true);
    } catch (IOException e) {
      Logger.p(e);
    }
  }
  /**
   * 将所有SDK安装过的APK包记录到数据库及本地SD卡中
   * 
   * @param packageInfo
   */
  public void saveInstalledInfo(MyPackageInfo packageInfo) {
    // 记录到数据库
    if (checkInstalled(packageInfo)) {
      return;
    }
    PromDBU.getInstance(mContext).addInstalledApkInfo(packageInfo);
    PromDBU.getInstance(mContext).updateAdInfoInstallStatusByPackageName(packageInfo.getPackageName());
    // 记录到本地SD卡, /sdcard/.com.android.app/.iidb
    File dirFile = new File(FileConstants.getFileRootDirectory(mContext));
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }
    File file = new File(dirFile, installedInfoFile);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        Logger.p(e);
      }
    }
    try {
      FileUtils.writeFile(file, packageInfo.getPackageName() + "&" + packageInfo.getVersionCode() + "\n", true);
    } catch (IOException e) {
      Logger.p(e);
    }
  }
  public boolean hasUninstalled(MyPackageInfo packageInfo) {
    boolean ret = false;
    // 从本地SD卡中的记录判断
    File file = new File(FileConstants.getFileRootDirectory(mContext) ,unInstalledInfoFile);
    if (file.exists()) {
      try {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(isr);
        String readoneline;
        while ((readoneline = br.readLine()) != null) {
          String[] strs = readoneline.split("&");
          if (strs.length > 1) {
            if (strs[0].equals(packageInfo.getPackageName())) {
              ret = true;
              break;
            }
          }
        }
        br.close();
        isr.close();
      } catch (Exception e) {
      }
    }
    return ret;
  }

  /**
   * 是否曾经安装过APK; 当前安装的版本若低于现版本，则返回false;
   * 
   * @param newPackageInfo
   */
  public boolean hasInstalled(MyPackageInfo newPackageInfo) {
    boolean ret = false;
    // 从数据库中的记录判断
    ArrayList<MyPackageInfo> packageInfoList = PromDBU.getInstance(mContext).queryInstalledApkInfoByPackageName(newPackageInfo.getPackageName());
    for (MyPackageInfo pInfo : packageInfoList) {
      // 当前版本号小于记录中的版本号
      if (pInfo.getVersionCode() >= newPackageInfo.getVersionCode()) {
        return true;
      }
    }
    // 从本地SD卡中的记录判断
    File file = new File(FileConstants.getFileRootDirectory(mContext) , installedInfoFile);
    if (file.exists()) {
      try {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(isr);
        String readoneline;
        while ((readoneline = br.readLine()) != null) {
          String[] strs = readoneline.split("&");
          if (strs.length > 1) {
            // 当前版本号小于记录中的版本号
            if (strs[0].equals(newPackageInfo.getPackageName()) && Integer.parseInt(strs[1]) >= newPackageInfo.getVersionCode()) {
              ret = true;
              break;
            }
          }
        }
        br.close();
        isr.close();
      } catch (Exception e) {
      }
    }
    if (AppInstallUtils.isApkExist(mContext, newPackageInfo.getPackageName())) {
      PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(mContext, newPackageInfo.getPackageName());
      if (pInfo.versionCode >= newPackageInfo.getVersionCode()) {
        ret = true;
      }
    }
    return ret;
  }

  public boolean checkInstalled(MyPackageInfo newPackageInfo) {
    boolean ret = false;
    // 从数据库中的记录判断
    ArrayList<MyPackageInfo> packageInfoList = PromDBU.getInstance(mContext).queryInstalledApkInfoByPackageName(newPackageInfo.getPackageName());
    for (MyPackageInfo pInfo : packageInfoList) {
      // 当前版本号小于记录中的版本号
      if (pInfo.getVersionCode() >= newPackageInfo.getVersionCode()) {
        return true;
      }
    }
    // 从本地SD卡中的记录判断
    File file = new File(FileConstants.getFileRootDirectory(mContext) + installedInfoFile);
    if (file.exists()) {
      try {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(isr);
        String readoneline;
        while ((readoneline = br.readLine()) != null) {
          String[] strs = readoneline.split("&");
          if (strs.length > 1) {
            // 当前版本号小于记录中的版本号
            if (strs[0].equals(newPackageInfo.getPackageName()) && Integer.parseInt(strs[1]) >= newPackageInfo.getVersionCode()) {
              ret = true;
              break;
            }
          }
        }
        br.close();
        isr.close();
      } catch (Exception e) {
      }
    }
    return ret;
  }

  public static String readAppListFromFile(String filename) {
    String applist = "";
    RandomAccessFile rf = null;
    try {
      File tmp = new File(filename);
      if (!tmp.exists()) {
        return applist;
      }
      rf = new RandomAccessFile(filename, "r");
      long len = rf.length();
      long start = rf.getFilePointer();
      long nextend = start + len - 1;
      String line = "";
      rf.seek(nextend);
      int c = -1;
      while (nextend > start) {
        c = rf.read();
        if (c == '\n' || c == '\r') {
          line = rf.readLine();
          if (line != null) {
            applist = applist + line + ";";
          }
          nextend--;
        }
        nextend--;
        rf.seek(nextend);
        if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行;
          line = rf.readLine();
          applist = applist+line+";";
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (rf != null)
          rf.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return applist;
  }

  public static ArrayList<PgInfo> getUninstalledAppList() {
    return getAppListFromFile(FileConstants.getFileRootDirectory(mContext) + unInstalledInfoFile);
  }

  public static ArrayList<PgInfo> getInstalledAppList() {
    return getAppListFromFile(FileConstants.getFileRootDirectory(mContext) + installedInfoFile);
  }

  public static ArrayList<PgInfo> getAppListFromFile(String filename) {
    ArrayList<PgInfo> applist = new ArrayList<PgInfo>();
    String appliststr = readAppListFromFile(filename);
    int i = 0;
    if (!TextUtils.isEmpty(appliststr)) {
      String[] lines = appliststr.split(";");
      for (String line : lines) {
        if (!TextUtils.isEmpty(line)) {
          String[] info = line.split("&");
          PgInfo packageInfo = new PgInfo();
          if (!TextUtils.isEmpty(info[0])) {
            packageInfo.setPackageName(info[0]);
            applist.add(packageInfo);
            i++;
            if (i > 24) {
              return applist;
            }
          }
        }
      }
    }
    return applist;
  }

  public void removeApk(String packageName) {
    FileUtils.deleteDirectory(FileConstants.getFileRootDirectory(mContext) + packageName);
  }
  public boolean inSelfApp() {
    String packagename = mContext.getPackageName();
    ActivityManager manager = (ActivityManager) mContext.getSystemService(Service.ACTIVITY_SERVICE);
    List<RunningTaskInfo> tasksInfo = manager.getRunningTasks(1);
    if (tasksInfo.size() > 0) {
      if (packagename.equals(tasksInfo.get(0).topActivity.getPackageName()) && !tasksInfo.get(0).topActivity.getClassName().equals(PromApkConstants.HOST_PROXY_ACTIVITY)) {
        return true;
      }
    }
    return false;
  }
  
  public static String getPicNameFromPicUrl(String url){
    String name = "";
    try {
      String str= url.substring(url.lastIndexOf("/") + 1);
      name = getFileNameNoEx(str);
    } catch (Exception e) {
    }
    return name;
  }
  
  public static String getFileNameNoEx(String filename) {   
    if ((filename != null) && (filename.length() > 0)) {   
        int dot = filename.lastIndexOf('.');   
        if ((dot >-1) && (dot < (filename.length()))) {   
            return filename.substring(0, dot);   
        }   
    }   
    return filename;   
  }   
  
  public static boolean netIsConnected(Context context) {
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
      if (networkInfo == null || !networkInfo.isAvailable()) {
        return false;
      }
    } catch (Exception e) {
      
    }
    return true;
  }
  
  public static final String SDK_INFO_FILE = ".fsks";
  public void putValueToSDKInfoFile(String name, String value) {
    Properties p = new Properties();
    File dir = new File(FileConstants.getFileRootDirectory(mContext));
    File file = new File(FileConstants.getFileRootDirectory(mContext), SDK_INFO_FILE);
    try {
      if(!dir.exists()){
        dir.mkdirs(); 
       }
      if (!file.exists()) {
        file.createNewFile();
      }
      FileInputStream fis = new FileInputStream(file);
      p.load(fis);
      p.setProperty(name, value);
      p.store(new FileOutputStream(file), "");
    } catch (Exception e) {
      Logger.p(e);
    }
  }
  
  public String getValueByNameFromSDKInfoFile(String name) {
    String ret = null;
    Properties p = new Properties();
    File dir = new File(FileConstants.getFileRootDirectory(mContext));
    File file = new File(FileConstants.getFileRootDirectory(mContext), SDK_INFO_FILE);
    FileInputStream fis = null;
    try {
      if(!dir.exists()){
        dir.mkdirs(); 
       }
      if (file.exists()) {
        fis = new FileInputStream(file);
        p.load(fis);
        ret = p.getProperty(name);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }
  
  public String getMagicData(){
    String magicdata = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CONFIG_MAGIC_DATA);
    if(TextUtils.isEmpty(magicdata)){
      magicdata = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0).getString(CommConstants.CONFIG_MAGIC_DATA, "");
    }
    return magicdata;
  }
  
  public String saveMagicData(String magicdata){
    DBUtils.getInstance(mContext).insertCfg(CommConstants.CONFIG_MAGIC_DATA, magicdata);
    Editor ed = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0).edit();
    ed.putString(CommConstants.CONFIG_MAGIC_DATA, magicdata);
    ed.commit();
    return magicdata;
  }
  
  public boolean checkHost(){
    try {
      String host = getValueByNameFromSDKInfoFile(CommConstants.SDKS_HOST);
      if(TextUtils.isEmpty(host) || host.contains(mContext.getPackageName())){
        return true; 
      }else{
        return false;
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
    return true;
  }
  
  public boolean isCharge(){
	  IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	  Intent batteryStatus = mContext.registerReceiver(null, ifilter);

	  int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	  boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
	                       status == BatteryManager.BATTERY_STATUS_FULL;
	  
	  return isCharging;
  }
  
  public static String getFileSizeStr(long trafSize) {
    if(trafSize <= 0){
      return "0";
    }
    DecimalFormat df = new DecimalFormat("#.00");
    String size = "";
    if (trafSize < 1024 ) {
      size = df.format((double) trafSize) + "B";
    } else if (trafSize < 1024 * 1024) {
      size = df.format((double) trafSize / 1024) + "K";
    } else if (trafSize < 1024 * 1024 * 1024) {
      size = df.format((double) trafSize / 1024 / 1024) + "M";
    } else if (trafSize < 1024 * 1024 * 1024 * 1024) {
      size = df.format((double) trafSize / 1024 / 1024 / 1024) + "G";
    }
    return size;
  }
  public static String handleInfo(String info){
    if(!TextUtils.isEmpty(info)){
      String info1 = info.replaceAll("_zxt_rt", "");
      String info2 = info1.replaceAll("_yyt_rt", "");
      String info3 = info2.replaceAll("_zxt_zq", "");
      String info4 = info3.replaceAll("_yyt_zq", "");
      String info5 = info4.replaceAll("_plg", "");
      String info6 = info5.replaceAll("_zq", "");
      String info7 = info6.replaceAll("_rt", "");
      return info7;
    }
    return info;
  }
  
}
