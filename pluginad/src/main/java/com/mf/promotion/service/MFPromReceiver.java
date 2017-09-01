package com.mf.promotion.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.mf.basecode.config.MFSDKConfig;
import com.mf.basecode.utils.contants.CommConstants;

public class MFPromReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    Intent service = new Intent(context,MFApkService.class);
    service.putExtra("myAction",action);
    service.putExtra("prom_service_id_apk",0);

    if (Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action)){
      if (null != intent.getData()){
        String packageName = intent.getData().getSchemeSpecificPart();
        service.putExtra("bundle_package_name",packageName);
      }
    }

    context.startService(service);

    if (isNeedInit(context)){
      Intent intent1 = new Intent(context,MFApkService.class);
      service.putExtra("prom_service_id_apk",-1);
      context.startService(intent1);
    }
  }

  private boolean isNeedInit(Context context){
    SharedPreferences sp = context.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG,Context.MODE_PRIVATE);
    long first = sp.getLong(CommConstants.FIRST_INIT_TIME, 0);
    int req = sp.getInt(CommConstants.CONFIG_REQ_RELATIVE_TIME,60 * 60 * 1000);
    if (System.currentTimeMillis() - first > (MFSDKConfig.getInstance().isReInit() ? 30 * 60 * 1000 : req)){
      return true;
    }
    return false;
  }
}
