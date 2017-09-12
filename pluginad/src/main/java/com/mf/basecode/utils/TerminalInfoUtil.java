package com.mf.basecode.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.mf.basecode.config.MFSDKConfig;
import com.mf.basecode.data.DBUtils;
import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.utils.AppInstallUtils;
//import android.util.Log;

public class TerminalInfoUtil {
  private static TerminalInfo m_TerminalInfoForZone;
//  private static TerminalInfo mTerminalInfo;
  private static String       isInitWithKeyStr;
  /** Network type is unknown */
  public static final int     NETWORK_TYPE_UNKNOWN = 0;
  /** Current network is GPRS */
  public static final int     NETWORK_TYPE_GPRS    = 1;
  /** Current network is EDGE */
  public static final int     NETWORK_TYPE_EDGE    = 2;
  /** Current network is UMTS */
  public static final int     NETWORK_TYPE_UMTS    = 3;
  /** Current network is CDMA: Either IS95A or IS95B */
  public static final int     NETWORK_TYPE_CDMA    = 4;
  /** Current network is EVDO revision 0 */
  public static final int     NETWORK_TYPE_EVDO_0  = 5;
  /** Current network is EVDO revision A */
  public static final int     NETWORK_TYPE_EVDO_A  = 6;
  /** Current network is 1xRTT */
  public static final int     NETWORK_TYPE_1xRTT   = 7;
  /** Current network is HSDPA */
  public static final int     NETWORK_TYPE_HSDPA   = 8;
  /** Current network is HSUPA */
  public static final int     NETWORK_TYPE_HSUPA   = 9;
  /** Current network is HSPA */
  public static final int     NETWORK_TYPE_HSPA    = 10;
  /** Current network is iDen */
  public static final int     NETWORK_TYPE_IDEN    = 11;
  /** Current network is EVDO revision B */
  public static final int     NETWORK_TYPE_EVDO_B  = 12;
  /** Current network is LTE */
  public static final int     NETWORK_TYPE_LTE     = 13;
  /** Current network is eHRPD */
  public static final int     NETWORK_TYPE_EHRPD   = 14;
  /** Current network is HSPA+ */
  public static final int     NETWORK_TYPE_HSPAP   = 15;

  private static void initTerminalInfo(Context c) {
    m_TerminalInfoForZone = new TerminalInfo();
    try {
      m_TerminalInfoForZone.setHsman(android.os.Build.PRODUCT);
      m_TerminalInfoForZone.setHstype(android.os.Build.MODEL);
      m_TerminalInfoForZone.setOsVer("android_" + android.os.Build.VERSION.RELEASE);
    } catch (Exception e) {
      m_TerminalInfoForZone.setHsman("");
      m_TerminalInfoForZone.setHstype("");
      m_TerminalInfoForZone.setOsVer("android");
    }
    try {
      DisplayMetrics dm = c.getResources().getDisplayMetrics();
      m_TerminalInfoForZone.setScreenWidth((short) dm.widthPixels);
      m_TerminalInfoForZone.setScreenHeight((short) dm.heightPixels);
    } catch (Exception e) {
      m_TerminalInfoForZone.setScreenWidth((short) 0);
      m_TerminalInfoForZone.setScreenHeight((short) 0);
    }
    
    try {
      m_TerminalInfoForZone.setCpu(getCpuString());
    } catch (Exception e) {
      m_TerminalInfoForZone.setCpu("");
    }   
    try {
      m_TerminalInfoForZone.setRamSize((short) getTotalMemory());
    } catch (Exception e) {
      m_TerminalInfoForZone.setRamSize((short) 0);
    }
    
    String IMSI = getPhoneImsi(c);
    if (null == IMSI) {
      IMSI = "";
    }
    m_TerminalInfoForZone.setImsi(IMSI);
    
    try {
      String IMEI = getPhoneImei(c);
      if (null == IMEI) {
        IMEI = "";
      } 
      m_TerminalInfoForZone.setImei(IMEI);
      m_TerminalInfoForZone.setCellId(0);
      m_TerminalInfoForZone.setLac(0);
    } catch (Exception e) {
    }
    m_TerminalInfoForZone.setNetworkType(getNetworkType(c));
    
    m_TerminalInfoForZone.setMac("");
    
    m_TerminalInfoForZone.setChannelid(getApkChannelId(c));
    m_TerminalInfoForZone.setAppid(getApkAppId(c));
    boolean hasRoot = c.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0;
    m_TerminalInfoForZone.setRootEnable((byte) (hasRoot ? 1 : 2));
    m_TerminalInfoForZone.setPackageName(c.getPackageName());
    int verCode = 0;
    try {
      verCode = MFSdkPackageInfo.getSdkApkVersionCode(c);
    } catch (Exception e) {
      e.printStackTrace();
    }
    m_TerminalInfoForZone.setVerCode(verCode);
    
    String verName ;
    try {
      verName = MFSdkPackageInfo.getSdkApkVersionName(c);
    } catch (Exception e) {
      verName = "android";
      e.printStackTrace();
    }
    m_TerminalInfoForZone.setVerName(verName);
    
    
    SharedPreferences sh = c.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String frameVerName = sh.getString(CommConstants.SDK_VERSION_NAME, "0.0.0");
    
    m_TerminalInfoForZone.setFrameVerName(frameVerName);
    m_TerminalInfoForZone.setReserved1(getSafeString(c));
    
  }
  public static TerminalInfo getTerminalInfo(Context c) {
    if (m_TerminalInfoForZone == null) {
      initTerminalInfo(c);
    }
    try {
      m_TerminalInfoForZone.setNetworkType(getNetworkType(c));
      m_TerminalInfoForZone.setReserved2(getDevMark(c));
//      m_TerminalInfoForZone.setAppid(getApkAppId(c));
//    } catch (Exception e) {
//      mTerminalInfo.setAppid("notfound");
//    }
//    try {
//      String tmp = mTerminalInfo.getVersionCode();// 修改version 信息，添加加载zip的版本信息
//      if (!TextUtils.isEmpty(tmp) && !tmp.contains("zipVName")) {
//        String zipVName = MfSdkPackageInfo.getSdkApkVersionName(c);
//        if (!TextUtils.isEmpty(zipVName)) {
//          tmp += ";zipVName=" + zipVName;
//          mTerminalInfo.setVersionCode(tmp);
//        }
//      }
    } catch (Exception e) {
      Logger.p(e);
    }
    Logger.debug("mTerminalInfo=" + m_TerminalInfoForZone.toString());
    return m_TerminalInfoForZone;
  }

  public static TerminalInfo getTerminalInfoForZone(Context c) {
    if (m_TerminalInfoForZone == null) {
      initTerminalInfo(c);
    }
//    m_TerminalInfoForZone.setAppid("Joy0001HZXT0001");
    return m_TerminalInfoForZone;
  }
  
  
  public static String getDevMark(Context context){
//    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//    ComponentName componentName = new ComponentName(context, PromApkConstants.HOST_PROXY_DEReceiver);
//    boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
//    if(!isAdminActive){
      return "0";
//    }
//    return "1";
  }

  /**
   * 获取应用版本号
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static int getAppVersionCode(Context context) {
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
  public static String getAppVersionName(Context context) {
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
      Logger.error("get package name error.");
    }
    return "";
  }

  /**
   * 获取本地配置的APPID
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static String getAppId(Context context) {
    String appId = "";
//    if (isInitWithKey(context)) {
      appId = DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//    } else {
//      try {
//        appId = getMetaData(context).getString(CommConstants.APPID_METADATA_KEY);
//      } catch (Exception e) {
//        Logger.p(e);
//      }
//    }
    if (TextUtils.isEmpty(appId)) {
      Logger.e(MFSDKConfig.TAG, "Get appId error.");
    }
    return appId;
  }

  /**
   * 获取总内存
   * 
   * @param context
   * @return
   */
  private static int getTotalMemory() {
    String str1 = "/proc/meminfo";
    String str2;
    String[] arrayOfString;
    int initial_memory = 0;

    try {
      FileReader localFileReader = new FileReader(str1);
      BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
      str2 = localBufferedReader.readLine();
      arrayOfString = str2.split("\\s+");
      initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;
      localBufferedReader.close();
    } catch (Exception e) {
    }
    return initial_memory;
  }

  /**
   * 获取本地网络IP地址
   * 
   * @param context
   * @return
   */
  public static String getLocalIpAddress() {
    try {
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          // for getting IPV4 format
          String ipv4 = null;
          if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
            return ipv4;
          }
        }
      }
    } catch (Exception ex) {
    }
    return null;
  }

  /**
   * 先获取手机IMSI，如没有，生成UUID来代替
   * 
   * @param mContext
   * @return
   */
//  public static String getIMSI(Context mContext) {
//    String imsi = getPhoneImsi(mContext);
//    if (null == imsi) {
//      imsi = getUUIDfromSD();
//    }
//    return imsi;
//  }
  /**
   * 获取MAC地址
   * 
   * @param context
   * @return
   */
  public static String getLocalMacAddress(Context context) {
    String mac = "";
    try {
      WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      WifiInfo info = wifi.getConnectionInfo();

      if (null == info) {
        return "";
      }
      mac = info.getMacAddress();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mac;
  }
  /**
   * 从SD卡获取UUID信息
   * 
   * @return
   */
//  private static String getUUIDfromSD() {
//    String uuid = null;
//    String fileName = PhoneInfoUtils.PATH + "/" + PhoneInfoUtils.DIR_NAME + "/" + PhoneInfoUtils.UUID_FILE;
//
//    File mFile = new File(fileName);
//    if (AppInfoUtils.isSDCardAvailable() && mFile.exists()) {
//      try {
//        uuid = FileUtils.readFile(mFile);
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//
//    if (null == uuid) {
//      uuid = UUID.randomUUID().toString();
//      saveUUIDtoSD(uuid);
//    }
//
//    return uuid;
//  }
  /**
   * 讲UUID存至SD卡
   * 
   * @param uuid
   */
//  private static void saveUUIDtoSD(String uuid) {
//    String strDir = PhoneInfoUtils.PATH + "/" + PhoneInfoUtils.DIR_NAME;
//    String strFile = strDir + "/" + PhoneInfoUtils.UUID_FILE;
//
//    File dir = new File(strDir);
//    File mFile = new File(strFile);
//
//    if (!dir.exists()) {
//      if (!dir.mkdirs()) {
//        return;
//      }
//    }
//
//    if (!mFile.exists()) {
//      try {
//        if (!mFile.createNewFile()) {
//          return;
//        }
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//
//    try {
//      FileUtils.writeFile(mFile, uuid, false);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

  /**
   * 获取应用渠道号：为CPID和channelId的组合, 如：cp0001@hz_test
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static String getApkChannelId(Context context) {
    String channelId = getCpId(context) + "@" + getChannelId(context);
    return channelId;
  }
  
  public static String getApkAppId(Context context){
    String appid = getCpId(context) + "@" + getAppId(context);
    return appid;
  }

  private static Bundle getMetaData(Context context) throws Exception {
    ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
    Bundle bundle = ai.metaData;
    if (bundle == null) {
      bundle = new Bundle();
    }
    return bundle;
  }

  /**
   * 获取cp id
   * 
   * @param context
   * @return
   */
  public static String getCpId(Context context) {
    String cpId = "";
//    if (isInitWithKey(context)) {
      cpId = DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//    } else {
//      try {
//        cpId = getMetaData(context).getString(CommConstants.CPID_METADATA_KEY);
//      } catch (Exception e) {
//        Logger.p(e);
//      }
//    }
    if (TextUtils.isEmpty(cpId)) {
      Logger.e(MFSDKConfig.TAG, "get cpId key error");
    }
    return cpId;
  }
  private static boolean isInitWithKey(Context context) {
    if (TextUtils.isEmpty(isInitWithKeyStr)) {
      isInitWithKeyStr = DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.MF_GET_DATA_PRIORITY);
    }
    if (TextUtils.isEmpty(isInitWithKeyStr)) {
      return false;
    }
    return "0".equals(isInitWithKeyStr);
  }


  /**
   * 获取渠道号
   * 
   * @param context
   * @return
   */
  public static String getChannelId(Context context) {
    String channelId = "";
//    if (isInitWithKey(context)) {
      channelId = DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//    } else {
//      try {
//        channelId = getMetaData(context).getString(CommConstants.CHANNELID_METADATA_KEY);
//      } catch (Exception e) {
//        Logger.p(e);
//      }
//    }
    if (TextUtils.isEmpty(channelId)) {
      Logger.e("MfPromSdk", "get channel id error");
    }
    return channelId;
  }
  private static String getProvidersName(String IMSI) {
    String ProvidersName = null;

    if (null == IMSI) {
      return "4";
    }
    // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
    if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
      ProvidersName = "1";
    } else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
      ProvidersName = "2";
    } else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) {
      ProvidersName = "3";
    } else {
      ProvidersName = "0";
    }
    return ProvidersName;
  }

  /**
   * 获取手机IMSI号
   * 
   * @param context
   * @return
   */
  public static String getPhoneImsi(Context context) {
    String imsi = null;
    TelephonyManager mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
    try {
      imsi = mTelephonyManager.getSubscriberId();
    } catch (Exception e) {
    }
    if (null == imsi) {
      Class<? extends TelephonyManager> tmClass = mTelephonyManager.getClass();
      try {
        Method getImsiMethod = tmClass.getMethod("getSubscriberIdGemini", Integer.TYPE);
        if (null != getImsiMethod) {
          // 先取SIM2
          imsi = (String) getImsiMethod.invoke(mTelephonyManager, 1);
          if (null == imsi) {
            imsi = (String) getImsiMethod.invoke(mTelephonyManager, 0);
          }
        }
      } catch (Throwable e) {
      }
    }
    if (imsi==null) {
      PhoneImsiImeiExpandGet a = new PhoneImsiImeiExpandGet();
      a.init(context);
      imsi = a.getImsi("1");
      if(imsi.equals("1")){
        imsi = null;
      }
    }
    return imsi;
  }
  
  public static String getPhoneImei(Context context) {
    String imei = null;
    TelephonyManager mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
    try {
      imei = mTelephonyManager.getDeviceId();
    } catch (Exception e) {
      
    }
    if (imei==null) {
      PhoneImsiImeiExpandGet a = new PhoneImsiImeiExpandGet();
      a.init(context);
      imei = a.getImei("2");
      if(imei.equals("2")){
        imei = null;
      }
    }
    return imei;
  }

  /**
   * 获取连接类型
   * 
   * @param context
   * @return
   */
  private static byte getNetworkType(Context context) {
    byte result = 0;
    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivity == null) {
      result = NetworkConstants.NERWORK_TYPE_FAIL;
    } else {
      NetworkInfo info = connectivity.getActiveNetworkInfo();
      if (info == null) {
        result = NetworkConstants.NERWORK_TYPE_FAIL;
      } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = tm.getNetworkType();
        switch (type) {
        case NETWORK_TYPE_GPRS:
        case NETWORK_TYPE_EDGE:
        case NETWORK_TYPE_CDMA:
        case NETWORK_TYPE_1xRTT:
        case NETWORK_TYPE_IDEN:
          result = NetworkConstants.NERWORK_TYPE_2G;
          break;
        case NETWORK_TYPE_UMTS:
        case NETWORK_TYPE_EVDO_0:
        case NETWORK_TYPE_EVDO_A:
        case NETWORK_TYPE_HSDPA:
        case NETWORK_TYPE_HSUPA:
        case NETWORK_TYPE_HSPA:
        case NETWORK_TYPE_EVDO_B:
        case NETWORK_TYPE_EHRPD:
        case NETWORK_TYPE_HSPAP:
          result = NetworkConstants.NERWORK_TYPE_3G;
          break;
        case NETWORK_TYPE_LTE:
          result = NetworkConstants.NERWORK_TYPE_4G;
          break;
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
          result = NetworkConstants.NERWORK_TYPE_UNKNOWN;
          break;
        default:
          result = (byte) type;
          break;
        }
      } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
        result = NetworkConstants.NERWORK_TYPE_WIFI;
      }
    }
    return result;
  }

  
  static public String getCpuString(){
    if(Build.CPU_ABI.equalsIgnoreCase("x86")){
      return "x86";
    }
    String strInfo = "";
    try
    {
      byte[] bs = new byte[1024];
      RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
      strInfo = reader.readLine();
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
    return strInfo;
  }
  
  static public String getSafeString(Context context){
    byte[] safe = {'0','0','0','0','1','1','1','1'};
    if(null != AppInstallUtils.getPgInfoByPackageName(context, "com.qihoo360.mobilesafe")){
      safe[0] = '1';
    }else if(null != AppInstallUtils.getPgInfoByPackageName(context, "com.qihoo.antivirus")){
      safe[0] = '1';
    }
    if(null != AppInstallUtils.getPgInfoByPackageName(context, "com.lbe.security")){
      safe[1] = '1';
    }
    if(null != AppInstallUtils.getPgInfoByPackageName(context, "com.tencent.qqpimsecure")){
      safe[2] = '1';
    }
    return new String(safe);
  }
  
  
  
}
