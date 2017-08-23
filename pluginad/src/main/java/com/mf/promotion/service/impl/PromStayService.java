package com.mf.promotion.service.impl;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;

public class PromStayService extends HandleService {

  public PromStayService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.debug("PromStayService", "onStartCommand");
  }

  @Override
  public void handledAds(List<String> ads) {
    
    
  }

}
