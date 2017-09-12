package com.mf.promotion.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.mf.basecode.data.DBUtils;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.HandlerConstants;
import com.mf.promotion.util.PromReqManager;
import com.mf.promotion.util.PromUtils;
import com.mf.utils.NumberUtil;
import com.mf.utils.ResourceIdUtils;

public class MFApkService extends Service {
  public static final String TAG        = "MfApkService";
  private HandleService[]       mfServices = new HandleService[MFApkServiceFactory.getTotalOfService()];

  private MFPromReceiver mfPromReceiver = new MFPromReceiver();
  /**
   * 以handler方式管理其他子服务的关闭
   */
  private Handler mHandler;

  private boolean promServiceIsNull() {
    for (HandleService mIMfService : mfServices) {
      if (null != mIMfService) {
        return false;
      }
    }
    return true;
  }

  public MFApkService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    if (null == mHandler){
      mHandler = new Handler(getMainLooper(), new Handler.Callback() {
        @Override public boolean handleMessage(Message msg) {
          switch (msg.what) {
            case HandlerConstants.HANDLER_STOP_SERVICE:
              Logger.debug(TAG, "stop apk service and service id = " + msg.arg1);
              mfServices[msg.arg1] = null;
              if (promServiceIsNull()) {
                stopSelf();
                Logger.debug(TAG, "stop all in apk service ");
              }
              break;
            default:
              break;
          }
          return false;
        }
      });
    }

    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    registerReceiver(mfPromReceiver,filter);

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mfPromReceiver);
  }

  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
  }

  /**
   * 采用单service方式来减少服务过多的问题
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null || null == intent.getExtras()) {
      Logger.e(TAG, "intent is null");
      if (promServiceIsNull()) {
        stopSelf();
        Logger.debug(TAG, "stop all in apk service ");
      }
      return START_NOT_STICKY;
    }
    int serviceId = intent.getExtras().getInt(BundleConstants.BUNDLE_KEY_SERVICE_ID_APK, -2);
    // // 如果无service id(-2)不做处理，-1为启动所有服务；
    Logger.e(TAG, "start apk service and serviceId = " + serviceId);
    if (serviceId == -1) {
      String appId = intent.getExtras().getString("ad_app_id","");
      String channelId = intent.getExtras().getString("ad_channel_id","");
      String cpId = intent.getExtras().getString("ad_cp_id","");
      if (!TextUtils.isEmpty(appId)){
        DBUtils.getInstance(this).insertCfg(CommConstants.APPID_METADATA_KEY,appId);
      }
      if (!TextUtils.isEmpty(channelId)){
        DBUtils.getInstance(this).insertCfg(CommConstants.CHANNELID_METADATA_KEY,channelId);
      }
      if (!TextUtils.isEmpty(cpId)){
        DBUtils.getInstance(this).insertCfg(CommConstants.CPID_METADATA_KEY,cpId);
      }
      addrCheck();
      checkServerAddr();
      PromReqManager.getInstance(this).ReqCommConfig();
      resetSDK();
      return START_NOT_STICKY;
    } else if (serviceId >= 0 && null == mfServices[serviceId]) {
      mfServices[serviceId] = MFApkServiceFactory.getPromService(serviceId, this, mHandler);
    } else if (serviceId > mfServices.length) {
      stopSelf();
      return START_NOT_STICKY;
    }
    if (serviceId >= 0 && serviceId < mfServices.length) {
      try {
        mfServices[serviceId].onStartCommand(intent, flags, startId);
      } catch (Exception e) {
        Logger.p(e);
      }
    }
    return START_NOT_STICKY;
  }

  public void checkServerAddr(){
    SharedPreferences sf = getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if(TextUtils.isEmpty(promAddress)){
      Logger.e(TAG, "checkServerAddr  addr  is  none");
      PromReqManager.getInstance(this).ReqServerAddr();
    }
  }
  
  public void resetSDK(){
    String sks = PromUtils.getInstance(this).getValueByNameFromSDKInfoFile(CommConstants.SDKS_INFO_KEY);
    Logger.e("mm", "mm mm mm =  "+sks);
    if(TextUtils.isEmpty(sks) || (!TextUtils.isEmpty(sks) && !sks.contains(getPackageName()))){
      sks = getPackageName()+"&"+sks;
      PromUtils.getInstance(this).putValueToSDKInfoFile(CommConstants.SDKS_INFO_KEY, sks);
    }
  }
  
  private void addrCheck(){
    Logger.e(TAG, "addrCheck");
    SharedPreferences sf = getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String oriAddr = sf.getString(CommConstants.SESSION_ORIGIN_ADD+"0", "");
    Logger.e(TAG, "addrCheck  oriAddr = "+oriAddr);
    Logger.e(TAG, "addrCheck  oriAddr = "+EncryptUtils.getMfNetworkAddr(this,0));
    if((TextUtils.isEmpty(oriAddr)) || (!TextUtils.isEmpty(oriAddr) && !oriAddr.equals(EncryptUtils.getMfNetworkAddr(this,0)))){
      Logger.e(TAG, "addrCheck  oriAddr111 = "+EncryptUtils.getMfNetworkAddr(this,0));
      Editor edit = sf.edit();
      edit.putString(CommConstants.SESSION_ORIGIN_ADD+"0", EncryptUtils.getMfNetworkAddr(this, 0));
      edit.putString(CommConstants.SESSION_ORIGIN_ADD+"1", EncryptUtils.getMfNetworkAddr(this,1));
      edit.putString(CommConstants.SESSION_ORIGIN_ADD+"2", EncryptUtils.getMfNetworkAddr(this,2));
      edit.putString(CommConstants.SESSION_PROM_ADD, "");
      edit.putString(CommConstants.SESSION_STAT_ADD, "");
      edit.putString(CommConstants.SESSION_UPDATE_ADD, "");
      edit.commit();
      Logger.e(TAG, "addrCheck  oriAddr222 = "+EncryptUtils.getMfNetworkAddr(this,0));
    }
  }
  
}
