package com.xdd.pay.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.xdd.pay.data.StringData;

/**
 * 文件名称: PhoneNumUtils.java<br>
 * 作者: 刘晔 <br>
 * 邮箱: ye.liu@ocean-info.com <br>
 * 创建时间：2014-1-21 下午1:31:59<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class PhoneNumUtils {
  /**
   * 获取手机号码
   * 
   * @param context
   * @return
   */
  public static String getPhoneNum(Context context) {
    String phone = "";
    try {
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      phone = telephonyManager.getLine1Number();
    } catch (Exception e) {
    }
    return phone;
  }

  /**
   * 根据卡槽ID获取手机号
   * 
   * @param context
   * @param slotID
   * @return
   */
  public static String getPhoneNumBySlot(Context context, int slotID) {
    String phone = "";
    try {
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

      Class<?> mLoadClass = Class.forName(StringData.getInstance().TEL_MANAGER);

      Method getLine1NumberGemini = mLoadClass.getMethod(StringData.getInstance().GETLINE1NUMBERGEMINI, new Class[] { int.class });

      phone = (String) getLine1NumberGemini.invoke(telephonyManager, new Object[] { slotID });

    } catch (Exception e) {
      e.printStackTrace();
    }
    return phone;
  }

}
