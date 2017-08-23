package com.mf.basecode.network.util;

import java.net.URLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtils {
  /**
   * 判断手机网络时候连接
   * 
   * @param context
   *          上下文
   * @return true：是；false：否
   */
  public static boolean isNetworkAvailable(Context context) {
    if (context != null) {
      ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

      if (cm == null)
        return false;
      NetworkInfo netinfo = cm.getActiveNetworkInfo();
      if (netinfo == null) {
        return false;
      }
      if (netinfo.isConnected()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取网络类型
   * 
   * @return 类型 {@link sdk.com.Joyreach.Update.util.NetworkConstants}
   */
  public static byte getNetworkType(Context context) {
    ConnectivityManager connectivity = null;
    try {
      connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    } catch (Exception e) {
    }
    if (connectivity == null) {
      return NetworkConstants.NERWORK_TYPE_FAIL;
    } else {
      NetworkInfo info = connectivity.getActiveNetworkInfo();
      if (info == null) {
        return NetworkConstants.NERWORK_TYPE_FAIL;
      }
      if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = tm.getNetworkType();
        switch (type) {
        case TelephonyManager.NETWORK_TYPE_EDGE:
        case TelephonyManager.NETWORK_TYPE_GPRS:
        case TelephonyManager.NETWORK_TYPE_UMTS:
        case TelephonyManager.NETWORK_TYPE_CDMA:
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
        case TelephonyManager.NETWORK_TYPE_1xRTT:
          return NetworkConstants.NERWORK_TYPE_2G;
        case TelephonyManager.NETWORK_TYPE_HSDPA:
        case TelephonyManager.NETWORK_TYPE_HSUPA:
        case TelephonyManager.NETWORK_TYPE_HSPA:
          return NetworkConstants.NERWORK_TYPE_3G;
        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
          return NetworkConstants.NERWORK_TYPE_UNKNOWN;
        }
      } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
        return NetworkConstants.NERWORK_TYPE_WIFI;
      }
    }
    return NetworkConstants.NERWORK_TYPE_FAIL;
  }

  /**
   * 根据URL链接获取该链接资源长度
   * 
   * @param urlConnection
   *          链接
   * @return 资源长度
   */
  public static long getLengthByURLConnection(URLConnection urlConnection) {
    String sHeader;
    long length = 0;
    if (urlConnection != null) {
      for (int i = 1;; i++) {
        sHeader = urlConnection.getHeaderFieldKey(i);
        if (sHeader != null) {
          if (sHeader.equalsIgnoreCase("Content-Length")) {
            length = Integer.parseInt(urlConnection.getHeaderField(sHeader));
            break;
          }
        } else
          break;
      }
    }
    return length;
  }

  /**
   * 判断地址是否可用
   * 
   * @param addr
   *          地址
   * @param type
   *          链接类型{@link com.oz.system.basecode.net.NetworkService}
   * @return
   */
//  public static boolean isAddrEnable(NetworkAddr addr) {
//    boolean enable = true;
//    try {
//      URL url = new URL("http://" + addr.getServerAddress() + "/macs");
//      url.openConnection();
//    } catch (IOException e) {
//      enable = false;
//    }
//    return enable;
//  }
}
