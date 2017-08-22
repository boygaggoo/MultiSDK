package com.xdd.pay.util;

import java.lang.reflect.Method;

import com.xdd.pay.constant.CommConstant;

import android.content.Context;
import android.net.ConnectivityManager;

public class GprsUtils {
  /**
   * 打开gprs
   * 
   * @param context
   */
  public static void openGprs(Context context) {
    try {
      ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      Method method = connManager.getClass().getMethod(EncryptUtils.decode(CommConstant.SETMOBILEDATAENABLED_TAG), new Class[] { Boolean.TYPE });
      method.setAccessible(true);
      method.invoke(connManager, new Object[] { true });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
