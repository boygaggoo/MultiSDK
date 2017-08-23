package com.mf.promotion.service.impl;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.network.util.NetworkUtils;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.utils.AppInstallUtils;
//import android.util.Log;


public class HandlePushService extends HandleService {
  private static List<AdDbInfo> list;
  public static final String TAG = "HandlePushService";
//  private Handler downloadHandler = new Handler() {
//    @Override
//    public void handleMessage(Message msg) {
//      handlePushDownloadMessage(msg,mContext,list );
//    }
//  };
  
  public HandlePushService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
    Logger.debug(TAG, "PromShowPNService created");
  }
  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.e(TAG, "onStartCommand ");
    boolean connect = PromUtils.netIsConnected(mContext);
    if(!connect){
      Logger.e(TAG, "connect");
      return ;
    }
    if(!HandleService.pSwitch(spf, CommConstants.SHOW_RULE_PUSH)){
      return ;
    }
    startNextService(CommConstants.SHOW_RULE_PUSH,MFApkServiceFactory.HANDLE_PUSH_SERVICE.getServiceId());
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    list = getHandleAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY,CommConstants.CONFIG_PUSH_SHOW_LIMIT, CommConstants.CONFIG_PUSH_SHOW_ONE_TIMENUM,-1, PromDBU.PROM_PUSH);
    handledAdList(list);    
  }
  @Override
  public void handledAds(List<String> ads) {

  }
  
  public void handledAdList(List<AdDbInfo> list) {
    Logger.e("HandlePushService", ""+list.size());
    for (AdDbInfo info : list) {
        doPush(info);
        Logger.e("HandlePushService", ""+info.toString());     
      }
  } 
  
  public static void handlePushDownloadMessage(android.os.Message msg,Context context , List<AdDbInfo> adlist) {
//    Bundle b = (Bundle) msg.obj;
//    final String packageName = b.getString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME);
//    if (!TextUtils.isEmpty(packageName)) {
//      if (msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH) {
//        for (AdDbInfo info : adlist) {
//          if(info.getPackageName().equals(packageName)){
//            Log.e(TAG, "down 55555");
//            showPush(info);
//          }
//        }
//      } else if (msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL) {
//        
//      }
//    }
  }
  private void doPush(AdDbInfo info){
    PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(mContext, info.getPackageName());
    String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
    if (pInfo != null) {
      if (!PromUtils.getInstance(mContext).hasInstalled(new MyPackageInfo(info.getPackageName(), info.getVersionCode()))) {
        File f = new File(apkPath);
        Logger.e(TAG, "p 1"+apkPath);
        if (f.exists() && showAdTime()) {
          showPush(info);
        }
      } 
    } else {
      File f = new File(apkPath);
      if (f.exists() && showAdTime()) {
        Logger.e(TAG, "p 2"+apkPath);
          showPush(info);
      } else {
        Logger.e(TAG, "p 3"+apkPath);
        if ((info.getAdType() == 1 &&info.getPreDown() == 1 && NetworkUtils.getNetworkType(mContext) == NetworkConstants.NERWORK_TYPE_WIFI) || info.getPreDown()== 2){
          DownloadInfo dinfo = new DownloadInfo(null,info.getAdId(),
              info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY, 0, info.getAdDownUrl(), info.getFileMd5(), true,true);
          MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY, 0, true);
          if(DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, packageInfo)){
            Logger.e(TAG, "down11111 ");
//            DownloadUtils.getInstance(mContext).addExtraHandler(downloadHandler);
          }else if(info.getAdType() == 1){
            Logger.e(TAG, "down22222 ");
            DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
          }
        }
        if(showAdTime()){
          showPush(info);
        }
      }
    }
  }
  
  private static void showPush(AdDbInfo info){
//    Logger.m("push", info.toString());
    Logger.error(TAG, "showPush");
//    StatsPromUtils.getInstance(mContext).addDisplayAction(info.getAdId()+"/"+info.getPackageName(),StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY);
    Logger.error(TAG, "showPush111");
    PromUtils.getInstance(mContext).showPushNotify(info);
    Logger.error(TAG, "showPush222");
    PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(info, PromDBU.PROM_PUSH, PromDBU.PROM_PUSH_NAME);
  }
}
