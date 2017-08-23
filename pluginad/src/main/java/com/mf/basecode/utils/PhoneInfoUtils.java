package com.mf.basecode.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;

public class PhoneInfoUtils {
  public static final String CHANNEL_ID_KEY = "channelid";

  public static String       DIR_NAME       = "phoneInfo";
  public static String       UUID_FILE      = "uuidInfo";
//  public static String       PATH           = FileConstants.FILE_ROOT_DIRECTORY;

  /**
   * 获取mac地址
   */
//  public static String getMacAddress(Context mContext) {
//    String mac = "";
//    try {
//      WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//      WifiInfo info = wifi.getConnectionInfo();
//      mac = info.getMacAddress();
//    } catch (Exception e) {
//      Logger.p(e);
//    }
//    return mac;
//  }
  /**
   * 获取总内存
   * 
   * @param context
   * @return
   */
//  public static int getTotalMemory() {
//    String str1 = "/proc/meminfo";
//    String str2;
//    String[] arrayOfString;
//    int initial_memory = 0;
//
//    try {
//      FileReader localFileReader = new FileReader(str1);
//      BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
//      str2 = localBufferedReader.readLine();
//      arrayOfString = str2.split("\\s+");
//      initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;
//      localBufferedReader.close();
//    } catch (Exception e) {
//    }
//    return initial_memory;
//  }

  /**
   * 获取android当前可用内存大小
   * 
   * @param context
   * @return
   */
//  public static int getAvailMemory(Context context) {
//    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//    MemoryInfo mi = new MemoryInfo();
//    am.getMemoryInfo(mi);
//
//    return (int) (mi.availMem / 1024);
//  }

  /**
   * @Title: isSDExists
   * @Description:
   * @param @return
   * @return boolean
   * @throws
   */
//  public static boolean isSDExists() {
//    boolean ret = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//    return ret;
//  }

  /**
   * 获取SD卡容量
   * 
   * @return
   */
  public static long getSDcardRoom() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      File sdcardDir = Environment.getExternalStorageDirectory();
      StatFs sf = new StatFs(sdcardDir.getPath());
      long blockSize = sf.getBlockSize();
      long blockCount = sf.getBlockCount();
      return blockSize * blockCount;
    }
    return 0;
  }

  /**
   * 获取可用SD卡容量
   * 
   * @return
   */
  public static long getAvailableSDcardRoom() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      File sdcardDir = Environment.getExternalStorageDirectory();
      StatFs sf = new StatFs(sdcardDir.getPath());
      long blockSize = sf.getBlockSize();
      // long blockCount = sf.getBlockCount();
      long availCount = sf.getAvailableBlocks();
      return blockSize * availCount;
    }
    return 0;
  }

  /**
   * 获取ROM大小
   * 
   * @return
   */
  public static long getMobileRomRoom() {
    File sdcardDir = Environment.getDataDirectory();
    StatFs sf = new StatFs(sdcardDir.getPath());
    long blockSize = sf.getBlockSize();
    long blockCount = sf.getBlockCount();
    return blockSize * blockCount;
  }

  /**
   * 获取剩余ROM大小
   * 
   * @return
   */
  public static long getAvailableMobileRoom() {
    File sdcardDir = Environment.getDataDirectory();
    StatFs sf = new StatFs(sdcardDir.getPath());
    long blockSize = sf.getBlockSize();
    long availCount = sf.getAvailableBlocks();
    return blockSize * availCount;
  }
  
  
  public static boolean isCharging(Context context){
    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent batteryStatus = context.registerReceiver(null, ifilter);
    
    int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                         status == BatteryManager.BATTERY_STATUS_FULL;
    return isCharging;
  }
  
  

  /**
   * 获取剩余内存（RAM）大小
   * 
   * @return
   */
//  public long getAvailableMemory(Context context) {
//    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
//    am.getMemoryInfo(mi);
//    return mi.availMem;
//  }

  /**
   * 获取手机密度
   * 
   * @param context
   * @return
   */
//  public static float getDensity(Context context) {
//    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//    return metrics.density;
//  }

  /**
   * Role:获取当前设置的电话号码
   */
//  public static String getNativePhoneNumber(Context context) {
//    String nativePhoneNumber = null;
//    try {
//      TelephonyManager mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
//      nativePhoneNumber = mTelephonyManager.getLine1Number();
//    } catch (Exception e) {
//    }
//    return nativePhoneNumber;
//  }

  
  
}
