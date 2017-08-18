package com.multisdk.library.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import com.multisdk.library.config.Config;
import com.multisdk.library.data.ConfigManager;
import com.multisdk.library.network.obj.TerminalInfo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;

public class TerminalInfoUtil {

  private static TerminalInfo m_TerminalInfoForZone;
  /** Network type is unknown */
  public static final int     NETWORK_TYPE_UNKNOWN = 0;
  /** Current network is GPRS */
  private static final int     NETWORK_TYPE_GPRS    = 1;
  /** Current network is EDGE */
  private static final int     NETWORK_TYPE_EDGE    = 2;
  /** Current network is UMTS */
  private static final int     NETWORK_TYPE_UMTS    = 3;
  /** Current network is CDMA: Either IS95A or IS95B */
  private static final int     NETWORK_TYPE_CDMA    = 4;
  /** Current network is EVDO revision 0 */
  private static final int     NETWORK_TYPE_EVDO_0  = 5;
  /** Current network is EVDO revision A */
  private static final int     NETWORK_TYPE_EVDO_A  = 6;
  /** Current network is 1xRTT */
  private static final int     NETWORK_TYPE_1xRTT   = 7;
  /** Current network is HSDPA */
  private static final int     NETWORK_TYPE_HSDPA   = 8;
  /** Current network is HSUPA */
  private static final int     NETWORK_TYPE_HSUPA   = 9;
  /** Current network is HSPA */
  private static final int     NETWORK_TYPE_HSPA    = 10;
  /** Current network is iDen */
  private static final int     NETWORK_TYPE_IDEN    = 11;
  /** Current network is EVDO revision B */
  private static final int     NETWORK_TYPE_EVDO_B  = 12;
  /** Current network is LTE */
  private static final int     NETWORK_TYPE_LTE     = 13;
  /** Current network is eHRPD */
  private static final int     NETWORK_TYPE_EHRPD   = 14;
  /** Current network is HSPA+ */
  private static final int     NETWORK_TYPE_HSPAP   = 15;

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
    m_TerminalInfoForZone.setPackageName(c.getPackageName());
    m_TerminalInfoForZone.setVerCode(Config.SDK_VERSION_CODE);

    m_TerminalInfoForZone.setVerName(Config.SDK_VERSION_NAME);
  }
  public static TerminalInfo getTerminalInfo(Context c) {
    if (m_TerminalInfoForZone == null) {
      initTerminalInfo(c);
    }
    try {
      m_TerminalInfoForZone.setNetworkType(getNetworkType(c));
      m_TerminalInfoForZone.setAppid(getApkAppId(c));
    } catch (Exception e) {
      m_TerminalInfoForZone.setAppid("notfound");
    }
    return m_TerminalInfoForZone;
  }

  public static void setTerminalInfoDestory(){
    m_TerminalInfoForZone = null;
  }

  public static TerminalInfo getTerminalInfoForZone(Context c) {
    if (m_TerminalInfoForZone == null) {
      initTerminalInfo(c);
    }
    return m_TerminalInfoForZone;
  }



  /**
   * 获取总内存
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
      initial_memory = Integer.valueOf(arrayOfString[1]) / 1024;
      localBufferedReader.close();
    } catch (Exception e) {
    }
    return initial_memory;
  }

  public static String getApkChannelId(Context context) {
    return ConfigManager.getPID(context) + "@" + ConfigManager.getChannelID(context);
  }

  public static String getApkAppId(Context context){
    return ConfigManager.getChannelID(context) + "@" + ConfigManager.getAppID(context);
  }

  /**
   * 获取手机IMSI号
   *
   * @param context
   * @return
   */
  private static String getPhoneImsi(Context context) {
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

    if (imsi==null ) {
      PhoneImsiImeiExpandGet a = new PhoneImsiImeiExpandGet();
      a.init(context);
      imsi = a.getImsi("1");
      if(imsi.equals("1")){
        imsi = null;
      }
    }

    return imsi;
  }


  private static String getPhoneImei(Context context) {
    String imei = null;
    TelephonyManager mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
    try {
      imei = mTelephonyManager.getDeviceId();
    } catch (Exception e) {
    }
    if (imei==null ) {
      PhoneImsiImeiExpandGet a = new PhoneImsiImeiExpandGet();
      a.init(context);
      imei = a.getImei("2");
      if(imei.equals("2")){
        imei = null;
      }
    }
    return imei;
  }

  private static final int    NERWORK_TYPE_FAIL        = 0;
  private static final int    NERWORK_TYPE_2G          = 1;
  private static final int    NERWORK_TYPE_3G          = 2;
  private static final int    NERWORK_TYPE_4G          = 5;
  private static final int    NERWORK_TYPE_WIFI        = 3;
  private static final int    NERWORK_TYPE_UNKNOWN     = 4;

  private static byte getNetworkType(Context context) {
    byte result = 0;
    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivity == null) {
      result = NERWORK_TYPE_FAIL;
    } else {
      NetworkInfo info = connectivity.getActiveNetworkInfo();
      if (info == null) {
        result = NERWORK_TYPE_FAIL;
      } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = tm.getNetworkType();
        switch (type) {
          case NETWORK_TYPE_GPRS:
          case NETWORK_TYPE_EDGE:
          case NETWORK_TYPE_CDMA:
          case NETWORK_TYPE_1xRTT:
          case NETWORK_TYPE_IDEN:
            result = NERWORK_TYPE_2G;
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
            result = NERWORK_TYPE_3G;
            break;
          case NETWORK_TYPE_LTE:
            result = NERWORK_TYPE_4G;
            break;
          case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            result = NERWORK_TYPE_UNKNOWN;
            break;
          default:
            result = (byte) type;
            break;
        }
      } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
        result = NERWORK_TYPE_WIFI;
      }
    }
    return result;
  }

  private static String getCpuString(){
    if(Build.CPU_ABI.equalsIgnoreCase("x86")){
      return "x86";
    }
    String strInfo = "";
    try
    {
      byte[] bs = new byte[1024];
      RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
      strInfo = reader.readLine();
      //      reader.read(bs);
      //      String ret = new String(bs);
      //      int index = ret.indexOf(0);
      //      if(index != -1) {
      //        strInfo = ret.substring(0, index);
      //      } else {
      //        strInfo = ret;
      //      }
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
    return strInfo;
  }

}
