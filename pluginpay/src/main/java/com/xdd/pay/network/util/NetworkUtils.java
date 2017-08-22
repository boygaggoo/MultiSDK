package com.xdd.pay.network.util;

import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.xdd.pay.network.object.NetworkAddr;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.QYLog;

/**
 * 文件名称: NetworkUtils.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 18:10:43<br>
 * 模块名称: 工具类<br>
 * 功能说明: 网络层工具<br>
 */
@SuppressLint("DefaultLocale")
public class NetworkUtils {
  public static final Integer ADDRESS_TYPE_NETWORK = 1;
  public static final Integer ADDRESS_TYPE_STATS_NETWORK = 2;
	
  /**
   * 获取推广域名
   * 
   * @return
   */
  public static NetworkAddr getNetworkAddr() {
    // return new NetworkAddr(EncryptUtils.getNetworkAddr());
	return new NetworkAddr(EncryptUtils.getNetworkAddrList(), ADDRESS_TYPE_NETWORK);
  }
  
  /**
   * 获取数据统计域名
   * 
   * @return
   */
  public static NetworkAddr getStatsNetworkAddr() {
     // return new NetworkAddr(EncryptUtils.getStatsNetworkAddr());
     return new NetworkAddr(EncryptUtils.getStatsNetworkAddrList(), ADDRESS_TYPE_STATS_NETWORK);
  }
  
  /**
   * 获取登入业务统计域名
   * 
   * @return
   */
  public static NetworkAddr getLoginLogAddr() {
    return new NetworkAddr(EncryptUtils.getLoginStatsNetworkAddr());
	// return new NetworkAddr(EncryptUtils.getLoginStatisticsAddrList());
  }
  
  /**
   * 获取登入域名
   * 
   * @return
   */
  public static NetworkAddr getLoginNetworkAddr() {
    return new NetworkAddr(EncryptUtils.getLoginNetworkAddr());
  }
  
  /**
   * 判断手机网络时候连接
   * 
   * @param context
   *          上下文
   * @return true：是；false：否
   */
  public static boolean isNetworkAvailable(Context context) {
    boolean ret = false;
    if (context != null) {
      ConnectivityManager cm = null;
      try {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
              ret = true;
            }
          }
      } catch (Exception e) {
        QYLog.p(e);
      }
    }
    return ret;
  }

  /**
   * 获取手机网络制式
   * 
   * @param context
   * @return -1：错误；0：未知；1：GSM；2：CDMA
   */
  public static int getPhoneType(Context context) {
    int ret = -1;
    try {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      ret = tm.getPhoneType();
    } catch (Exception e) {
    }

    return ret;
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
      
      if(info.getExtraInfo()!=null){
    	  if(info.getExtraInfo().toLowerCase().equals("cmwap")){
    		  return NetworkConstants.NERWORK_TYPE_CMWAP;
    	  } else if (info.getExtraInfo().toLowerCase().equals("cmnet")) {
    		  return NetworkConstants.NERWORK_TYPE_CMNET;
    	  } else if (info.getExtraInfo().toLowerCase().equals("wifi")) {
    		  return NetworkConstants.NERWORK_TYPE_WIFI;
    	  } else if (info.getExtraInfo().toLowerCase().equals("uniwap")) {
    		  return NetworkConstants.NERWORK_TYPE_UNIWAP;
    	  } else if (info.getExtraInfo().toLowerCase().equals("uninet")) {
    		  return NetworkConstants.NERWORK_TYPE_UNINET;
    	  } else if (info.getExtraInfo().toLowerCase().equals("ctwap")) {
    		  return NetworkConstants.NERWORK_TYPE_CTIWAP;
    	  } else if (info.getExtraInfo().toLowerCase().equals("ctnet")) {
    		  return NetworkConstants.NERWORK_TYPE_CTINET;
    	  }
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
}
