package com.mf.promotion.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

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
import com.mf.promotion.util.TimerManager;
import com.mf.statistics.prom.util.StatsPromConstants;

public class HandlePreDownLoadService extends HandleService{
  public static final String TAG = "HandlePreDownLoadService";
  private static List<AdDbInfo> list = new ArrayList<AdDbInfo>();
  private Handler downloadHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      handlePreDownloadMessage(msg);
    }
  };
  public HandlePreDownLoadService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
   Logger.e(TAG, "HandlePreDownLoadService   oooo");
   TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis()+60*60*1000, MFApkServiceFactory.HANDLE_PREDOWNLOAD_SERVICE.getServiceId());
   boolean connect = PromUtils.netIsConnected(mContext);
   if(!connect){
     Logger.e(TAG, "connect");
     return ;
   }
   getPreDownListAll();
   startDownloadListItem();
  }
  public void handlePreDownloadMessage(android.os.Message msg) {
    Bundle b = (Bundle) msg.obj;
    final String packageName = b.getString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME);
    if (!TextUtils.isEmpty(packageName)) {
      if (msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH || 
          msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL) {
        startDownloadListItem();
        Logger.e(TAG, "HandlePreDownLoadService   222");
      }
    }
  }
  
  private void startDownloadListItem(){
    Logger.e(TAG, "list = "+list.size());
    AdDbInfo info = null;
    for (AdDbInfo adDbInfo : list) {
      if ((adDbInfo.getAdType() == 1 && adDbInfo.getPreDown() == 1 && NetworkUtils.getNetworkType(mContext) == NetworkConstants.NERWORK_TYPE_WIFI) || adDbInfo.getPreDown()== 2){
        String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(adDbInfo.getPackageName(), adDbInfo.getVersionCode());
        File downloadFile = new File(apkPath);
        if(!downloadFile.exists()){
          Logger.e(TAG, "111adid = "+adDbInfo.getAdId());
          int position = -1;
          if(adDbInfo.getPromType() == PromDBU.PROM_WAKEUP){
            position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP;
          }else if (adDbInfo.getPromType() == PromDBU.PROM_DESKTOPAD){
            position = HandleDesktopAdService.getDeskAdPosition(adDbInfo);
          }else if (adDbInfo.getPromType() == PromDBU.PROM_PUSH){
            position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY;
          }else if (adDbInfo.getPromType() == PromDBU.PROM_SHORTCUT){
            position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT;
          }else if (adDbInfo.getPromType() == PromDBU.PROM_DESKFOLDER){
            position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER;
          }
          DownloadInfo dinfo = new DownloadInfo(downloadHandler, adDbInfo.getAdId(),adDbInfo.getPackageName(), adDbInfo.getVersionCode(), position,
              0, adDbInfo.getAdDownUrl(), adDbInfo.getFileMd5(), true,true);
          Logger.e(TAG, "HandlePreDownLoadService   3333");
          if(!DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, dinfo)){
            Logger.e(TAG, "HandlePreDownLoadService   444");
            DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
            info = adDbInfo;
            Logger.e(TAG, "info   = "+info.toString());
            break;
          }
        }
      }
    }
    if(info != null){
      list.remove(info);
    }
  }
  
  private void getPreDownListAll() {
    getPreDownAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP,CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT, CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM, PromDBU.PROM_DESKTOPAD);
    
    getPreDownAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP,CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT, CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM, PromDBU.PROM_EXIT);
    
    getPreDownAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY,CommConstants.CONFIG_PUSH_SHOW_LIMIT, CommConstants.CONFIG_PUSH_SHOW_ONE_TIMENUM, PromDBU.PROM_PUSH);
    
    getPreDownAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT,"", CommConstants.CONFIG_QUICK_ICON_SHOW_ONE_TIMENUM, PromDBU.PROM_SHORTCUT);
    
    getPreDownAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP,CommConstants.CONFIG_WAKEUP_SHOW_LIMIT, CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM, PromDBU.PROM_WAKEUP);
  }
  
  
  protected void getPreDownAdInfoList(int position,String limitStr,String oneTimeNum,int promType){
    List<AdDbInfo> adlist = new ArrayList<AdDbInfo>();
    int limit = -1;//spf.getInt(CommConstants.CONFIG_WAKEUP_SHOW_LIMIT, -1);
    int oneNum = 0;//spf.getInt(CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM, 0);
    int maxShowtimes = 0;
    int hasShowTimes = 0;
    if(!TextUtils.isEmpty(limitStr)){
      limit = spf.getInt(limitStr, -1);
    }
    if(!TextUtils.isEmpty(oneTimeNum)){
      oneNum = spf.getInt(oneTimeNum, 0);
    }
    ArrayList<AdDbInfo> plist = PromDBU.getInstance(mContext).queryAdInfo(promType);
    Logger.d(TAG, "Handle list.size = "+plist.size());
    for (AdDbInfo adDbInfo : plist) {
      if(maxShowtimes < adDbInfo.getShowTimes()){
        maxShowtimes = adDbInfo.getShowTimes();
      }
      hasShowTimes = hasShowTimes+adDbInfo.getHasShowTimes();
    }
    if(position != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT&& limit != -1 &&(hasShowTimes >= limit || oneNum <= 0)){
      return ;
    }
    
    boolean connect = PromUtils.getInstance(mContext).netIsConnected(mContext);
    if(connect){
      int count = 0;    
      int markPosition = -1;
      int lastPostion = -1;
      Logger.d(TAG, "Handle list.size 1111 ");
      for (int i = 0; i < plist.size(); i++) {
        if(plist.get(i).getShowmark() == 1){
          markPosition = i;
          break;
        }
      }
      Logger.d(TAG, "Handle list.size = "+list.size());
      for (int i = markPosition+1; i < plist.size(); i++) {
        if(count >= oneNum){
          break;
        }
        if( plist.get(i).getHasShowTimes() < plist.get(i).getShowTimes() && plist.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum){
          if(!adlist.contains(plist.get(i))){
            count++;
            adlist.add(plist.get(i));
            lastPostion = i;
          }
        }
      }
      Logger.d(TAG, "markPosition = "+markPosition);
      for (int i = 0; i < markPosition +1; i++) {
        if(count >= oneNum){
          break;
        }
        if( plist.get(i).getHasShowTimes() < plist.get(i).getShowTimes()  && plist.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum){
          Logger.d(TAG, "promType = "+promType+"  i  = "+i);
          if(!adlist.contains(plist.get(i))){
            count++;
            adlist.add(plist.get(i));
            lastPostion = i;
          }
        }
      }
    }
    Iterator<AdDbInfo> iter = adlist.iterator();  
    while(iter.hasNext()){  
      AdDbInfo info = iter.next();
      if (info.getPreDown() == 1 || info.getPreDown()== 2) {
        if(!list.contains(info)){
          list.add(info);
        }
      }
    }
  }
  @Override
  public void handledAds(List<String> ads) {
    
  }

}
