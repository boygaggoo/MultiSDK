package com.xdd.pay.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import com.xdd.pay.data.StringData;
import com.xdd.pay.db.DBConfig;
import com.xdd.pay.db.DBUtils;
import com.xdd.pay.network.object.LocationInfo;

public class LocationUtils {
  private static LocationInfo mLocationInfo;

  public static LocationInfo getLocationInfo(Context context) {
    if (mLocationInfo == null) {
      mLocationInfo = new LocationInfo();
      mLocationInfo.setCellId(getCellId(context));
      mLocationInfo.setIp(getLocalIpAddress());
      mLocationInfo.setLac(getLac(context));
      // 排除短信息中心号地址可能获取不到的情况
      String smsCenter = getSmsCenter(context);
      if (TextUtils.isEmpty(smsCenter)) {
        smsCenter = DBUtils.getInstance(context).queryCfgValueByKey(DBConfig.SMS_CENTER);
      } else {
        DBUtils.getInstance(context).addCfg(DBConfig.SMS_CENTER, smsCenter);
      }
      mLocationInfo.setSmsCenter(smsCenter);
    }
//     mLocationInfo.setLatitude(GpsUtils.getInstance(context).getLatitude());
//     mLocationInfo.setLongitude(GpsUtils.getInstance(context).getLongitude());
    QYLog.d(mLocationInfo.toString());
    return mLocationInfo;
  }

  /**
   * 从收件箱的短信里获取短信中心号
   * 
   * @param context
   * @return
   */
  public static String getSmsCenter(Context context) {

    String service_center = "";
    try {

      Cursor cursor = context.getContentResolver().query(Uri.parse(StringData.getInstance().URI_SMS_INBOX), null, null, null, "date desc");
      if (cursor != null && cursor.getCount() > 0) {
        cursor.moveToFirst();
        do {
          String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
          if (address.startsWith("10")) {
            service_center = cursor.getString(cursor.getColumnIndexOrThrow("service_center"));
            if (service_center != null && !service_center.equals("")) {
              break;
            }
          }
        } while (cursor.moveToNext());

        cursor.close();
      }
    } catch (Exception e) {
      QYLog.e("exception:" + e);
    }
    return service_center;
  }

  /**
   * 获取lac
   * 
   * @param context
   * @return
   */
  public static int getLac(Context context) {
    int lac = 0;
    try {
      TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      CellLocation location = mTelephonyManager.getCellLocation();
      if (location instanceof GsmCellLocation) {
        lac = ((GsmCellLocation) location).getLac();
      } else if (location instanceof CdmaCellLocation) {
        lac = ((CdmaCellLocation) location).getNetworkId();
      }
    } catch (Exception e) {
      QYLog.e("exception:" + e);
    }
    return lac;
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
          String ipv4 = null;
          if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
            return ipv4;
          }
        }
      }
    } catch (Exception ex) {
      QYLog.e("exception:" + ex);
    }
    return null;
  }

  /**
   * 获取基站id
   * 
   * @param context
   * @return
   */
  public static int getCellId(Context context) {
    int cellId = 0;
    try {
      TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      CellLocation location = mTelephonyManager.getCellLocation();
      if (location instanceof GsmCellLocation) {
        cellId = ((GsmCellLocation) location).getCid();
      } else if (location instanceof CdmaCellLocation) {
        cellId = ((CdmaCellLocation) location).getBaseStationId();
      }
    } catch (Exception e) {
      QYLog.e("exception:" + e);
    }
    return cellId;
  }
}
