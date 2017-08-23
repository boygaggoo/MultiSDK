package com.mf.promotion.util;

import android.content.Context;

import com.mf.basecode.utils.Logger;
import com.mf.promotion.service.MFApkServiceFactory;

public class ServiceManager {
  private static final String   TAG = "MfService";
  private TimerManager          mTimerManager;
  private static ServiceManager mInstance;
  private Context               mContext;

  private ServiceManager(Context c) {
    this.mContext = c;
    mTimerManager = TimerManager.getInstance(c);
  }
  public static ServiceManager getInstance(Context c) {
    if (mInstance == null) {
      synchronized (ServiceManager.class) {
        if (mInstance == null) {
          mInstance = new ServiceManager(c);
        }
      }
    }
    return mInstance;
  }

  public void startStayService() {
    Logger.d(TAG, "startStayService");
    mTimerManager.startAlermByServiceId(MFApkServiceFactory.STAY_SERVICE.getServiceId(), 0, true);
  }
}
