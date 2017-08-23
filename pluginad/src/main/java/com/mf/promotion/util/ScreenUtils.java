package com.mf.promotion.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.DisplayMetrics;

import com.mf.basecode.config.MFSDKConfig;
import com.mf.basecode.utils.Logger;

public class ScreenUtils {

  public final static boolean isScreenLocked(Context c) {
    boolean isScreenLocked = false;
    try {
      android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(Context.KEYGUARD_SERVICE);
      PowerManager pm = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
      isScreenLocked = !pm.isScreenOn() && mKeyguardManager.inKeyguardRestrictedInputMode();
    } catch (Exception e) {
      Logger.p(e);
    }
    Logger.debug("isScreenLocked：" + isScreenLocked);
    return isScreenLocked || MFSDKConfig.getInstance().isDebugMode();
  }
  
  public static int dip2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }
  
  public static int getScreenWidth(Context context) {
    DisplayMetrics dm = new DisplayMetrics();
    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    return (int) (dm.widthPixels);
  }
  
  public static int getScreenHeight(Context context) {
    DisplayMetrics dm = new DisplayMetrics();
    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    return (int) (dm.heightPixels);
  }
}
