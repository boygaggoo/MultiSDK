package com.mf.promotion.util;

import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.mf.basecode.config.MFSDKConfig;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.basecode.utils.contants.SeparatorConstants;

public class TimerManager {

  public static Context       mContext;
  public static TimerManager  mInstance;
  private AlarmManager        alarmManager;
  private int                 curTimerIndex  = 0;                 // 用来记录定时器启动个数
  private static final int    TIMER_COUNT    = 15;                 // 定时器数量，除开关定时器
  private static final int    TIMER_INTERVAL = 2;                 // 启动定时器间隔，单位：miao
  private Random              r              = new Random();      // 用于生成随机时间
  private static final String TAG            = "PromTimerManager";

  private TimerManager(Context c) {
    mContext = c;
    alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
  }

  public static TimerManager getInstance(Context c) {
    if (mInstance == null) {
      mInstance = new TimerManager(c);
    }
    return mInstance;
  }

  /**
   * 通过serviceId 来设置定时器来启动
   */
  public void startAlermByServiceId(int serviceId) {
    startAlermByServiceId(serviceId, 0, false);
  }

  /**
   * 启动定时器
   * 
   * @param serviceId
   * @param interval
   *          重复时间，单位：ms
   * 
   */
  public void startAlermByServiceId(int serviceId, long interval) {
    startAlermByServiceId(serviceId, interval, false);
  }

  public void startAlermByServiceId(int serviceId, long interval, boolean immediately) {
    Logger.debug(TAG, "startAlermByServiceId and serviceid=" + serviceId);
    try {
      int startMunites = MFSDKConfig.getInstance().isDebugMode() ? 0 : getCurTimeStartTime();
      long millis = System.currentTimeMillis();
      if (!immediately) {
        millis += startMunites * 1000 ;
      }
      Logger.debug(TAG, "Alerm will start at " + TimeFormater.formatTime(millis));
      if (interval != 0) {
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, interval, getPendingIntent(serviceId, null));
      } else {
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis, getPendingIntent(serviceId, null));
      }
    } catch (Exception e) {
      Logger.p(e);
      Logger.debug(TAG, "Alerm  start error.");
    }
  }

  public void stopAlermByServiceId(int serviceId) {
    alarmManager.cancel(getPendingIntent(serviceId, null));
  }

  public void startTimerByTime(long millis, int serviceId) {
    startTimerByTime(millis, serviceId, null);
  }
  /**
   * 按时间设定定时器
   */
  public void startTimerByTime(long millis, int serviceId, Bundle b) {
    if (millis <= 0) {
      int minute = MFSDKConfig.getInstance().isDebugMode() ? 0 : 10;
      millis = System.currentTimeMillis() + r.nextInt(5 * 1000);
    }
    Logger.debug(TAG, "startTimerByTime at " + TimeFormater.formatTime(millis) + " and serviceid=" + serviceId);
    PendingIntent pi = getPendingIntent(serviceId, b);
    alarmManager.cancel(pi);
    alarmManager.set(AlarmManager.RTC_WAKEUP, millis, pi);
  }
  private PendingIntent getPendingIntent(int serviceId, Bundle b) {
    Intent pandingIntent = new Intent();
    pandingIntent.setClassName(mContext, PromApkConstants.HOST_PROXY_SERVICE_CLASS_PATH);
    pandingIntent.putExtra(BundleConstants.BUNDLE_KEY_SERVICE_ID_APK, serviceId);
    int id = serviceId;
    if (b != null) {
      // 如果是推送，会有多条PendingIntent，故用推送的id为PendingIntent的requestCode
      id = b.getInt(BundleConstants.BUNDLE_PUSH_NOTIFICATION_ID, serviceId);
      pandingIntent.putExtras(b);
    }
    PendingIntent mPendingIntent = PendingIntent.getService(mContext, id, pandingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    return mPendingIntent;
  }

  /**
   * 动态获取定时器启动时间
   * 
   * @return
   */
  private int getCurTimeStartTime() {
    curTimerIndex++;
    if (curTimerIndex > TIMER_COUNT) {
      curTimerIndex = 1;
    }
    return curTimerIndex * TIMER_INTERVAL;
  }

//  public void clearPushNotifyTimer(int... id) {
//    if (id != null && id.length > 0) {
//      for (int i : id) {
//        alarmManager.cancel(getPendingIntent(i, null));
//      }
//    }
//    List<PromAppInfo> oriAdInfos = PromDBUtils.getInstance(mContext).queryAllPushNotify();
//    for (PromAppInfo adInfo : oriAdInfos) {
//      alarmManager.cancel(getPendingIntent(adInfo.getId(), null));
//    }
//  }

  public void startTimerByTime(String timeString, int serviceId) {
    startTimerByTime(timeString, serviceId, null);
  }
  public void startTimerByTime(String timeString, int serviceId, Bundle b) {
    String[] timeStrings = timeString.split(SeparatorConstants.SEPARATOR_HOUR_MINUTE);
    if (timeStrings.length > 1) {
      Calendar c = Calendar.getInstance();
      if (!TextUtils.isEmpty(timeStrings[0])) {
        c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeStrings[0]));
      }
      c.set(Calendar.MINUTE, (Integer.valueOf(timeStrings[1])));
      c.set(Calendar.SECOND, c.get(Calendar.SECOND));
      startTimerByTime(c.getTimeInMillis()+5000, serviceId, b);
    }
  }
  public void startTimerByTime(int hour, int min, int serviceId, Bundle b) {
    Calendar c = Calendar.getInstance();
    c.set(Calendar.HOUR_OF_DAY, hour);
    c.set(Calendar.MINUTE, min);
    startTimerByTime(c.getTimeInMillis(), serviceId, b);
  }
}
