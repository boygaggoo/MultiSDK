package com.multisdk.library.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.multisdk.library.service.SDKInitService;

public class TimerUtil {

  private Context mContext;

  private AlarmManager alarmManager;

  private static volatile TimerUtil sInst = null;
  public static TimerUtil getInstance(Context context){
    TimerUtil inst = sInst;
    if (inst == null){
      synchronized (TimerUtil.class){
        inst = sInst;
        if (inst == null){
          inst = new TimerUtil(context);
          sInst = inst;
        }
      }
    }
    return inst;
  }

  private TimerUtil(Context context){
    mContext = context.getApplicationContext();
    alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
  }

  public void startTimerByTime(long millis){
    alarmManager.set(AlarmManager.RTC_WAKEUP,millis,getPendingIntent());
  }

  private PendingIntent getPendingIntent(){
    Intent intent = new Intent();
    intent.setClassName(mContext, SDKInitService.class.getName());
    return PendingIntent.getService(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
  }
}
