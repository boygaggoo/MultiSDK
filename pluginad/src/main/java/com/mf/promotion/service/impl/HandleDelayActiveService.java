package com.mf.promotion.service.impl;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.TimerManager;
//import android.util.Log;

public class HandleDelayActiveService extends HandleService{

  public HandleDelayActiveService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
    // TODO Auto-generated constructor stub
    Logger.e("HandleDelayActiveService", "init");
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    // TODO Auto-generated method stub
    Logger.e("HandleDelayActiveService", "onStartCommand");
    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    long activePointTime = spf.getLong(CommConstants.CONFIG_ACTIVE_POINT_TIME, 0);
    long times = 0;
    boolean active = spf.getBoolean(CommConstants.CONFIG_APP_ACTIVE, false);
    if(System.currentTimeMillis() < activePointTime || !active){
      times = activePointTime;
    }else{
      return ;
    }
    spf.edit().putBoolean(CommConstants.CONFIG_APP_ACTIVE, true).commit();
    Logger.e("PromTimerManager", "HandledelayService");
    TimerManager.getInstance(mContext).startTimerByTime(times, MFApkServiceFactory.HANDLE_DESKTOP_AD_SERVICE.getServiceId()); 
    TimerManager.getInstance(mContext).startTimerByTime(times+1000, MFApkServiceFactory.HANDLE_PUSH_SERVICE.getServiceId()); 
    TimerManager.getInstance(mContext).startTimerByTime(times+2000, MFApkServiceFactory.HANDLE_SHORTCUT_SERVICE.getServiceId()); 
    TimerManager.getInstance(mContext).startTimerByTime(times+3000, MFApkServiceFactory.HANDLE_WAKE_UP_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(times+4000, MFApkServiceFactory.HANDLE_FLOATWINNDOW_SERVICE.getServiceId());
//    EnhancedDo.getInstance(mContext).enhancedActive(times, spf);
//    TimerManager.getInstance(mContext).startTimerByTime(times+12000, MFApkServiceFactory.HANDLE_PLUGIN_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(times+6000, MFApkServiceFactory.HANDLE_EXIT_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(times+7000, MFApkServiceFactory.HANDLE_BROWER_SERVICE.getServiceId());
  }

  @Override
  public void handledAds(List<String> ads) {
    // TODO Auto-generated method stub
    
  }
  
  
}
