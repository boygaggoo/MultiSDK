package com.multisdk.library.imp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.multisdk.library.constants.Constants;
import com.multisdk.library.data.ConfigManager;
import com.multisdk.library.service.SDKInitService;
import com.multisdk.library.virtualapk.PluginManager;

public class MultiSDK {

  private static final String TAG = "MultiSDK";
  private Context mContext;

  private static volatile MultiSDK sInst = null;
  public static MultiSDK getInstance(Context context){
    MultiSDK inst = sInst;
    if (inst == null){
      synchronized (MultiSDK.class){
        inst = sInst;
        if (inst == null){
          inst = new MultiSDK(context);
          sInst = inst;
        }
      }
    }
    return inst;
  }

  private MultiSDK(Context context){
    mContext = context.getApplicationContext();
  }

  public void prepare(Context base){
    PluginManager.getInstance(base);
  }

  public void init(String a,String c,String p){
    if (mContext == null){
      Log.e(TAG, "error.");
    }

    String APP_ID = a;
    String CHANNEL_ID = c;
    String p_ID = p;

    if (TextUtils.isEmpty(APP_ID)){
      APP_ID = ConfigManager.getAppID(mContext);
    }else {
      ConfigManager.saveAppID(mContext, APP_ID);
    }
    if (TextUtils.isEmpty(CHANNEL_ID)){
      CHANNEL_ID = ConfigManager.getChannelID(mContext);
    }else {
      ConfigManager.saveChannelID(mContext, CHANNEL_ID);
    }
    if (TextUtils.isEmpty(p_ID)){
      p_ID = ConfigManager.getPID(mContext);
    }else {
      ConfigManager.savePID(mContext, p_ID);
    }

    if (TextUtils.isEmpty(APP_ID) || TextUtils.isEmpty(CHANNEL_ID) || TextUtils.isEmpty(p_ID)){
      Log.e(TAG, "init: error");
      return;
    }

    Intent intent = new Intent();
    intent.setClassName(mContext, SDKInitService.class.getName());
    intent.putExtra(Constants.Intent.INIT_KEY,Constants.Intent.INIT_VALUE);
    mContext.startService(intent);
  }

  public void pay(Activity activity,String pointNum,int price,Callback callback){
    Intent intent = new Intent();
    intent.setClassName(mContext, SDKInitService.class.getName());
    intent.putExtra(Constants.Intent.PAY_KEY,Constants.Intent.PAY_VALUE);
    mContext.startService(intent);
  }
}
