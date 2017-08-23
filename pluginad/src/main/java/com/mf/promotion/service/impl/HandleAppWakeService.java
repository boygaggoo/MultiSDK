package com.mf.promotion.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.utils.AppInstallUtils;

public class HandleAppWakeService {
  private static SharedPreferences spf = null;
  private static int mPosition = -1;
  public static void AppWake(Context context,int postion){
    spf = context.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    mPosition = postion;
    if(!HandleService.isScreenOn(context)){
      return ;
    }
    
    if(!HandleService.pSwitch(spf, CommConstants.SHOW_RULE_WAKEUP)){
      return ;
    }
    
    List<AdDbInfo> list = null;
    if(mPosition == StatsPromConstants.STATS_PROM_AD_INFO_POSITION_APP_NETCHANNGE){
      boolean connect = PromUtils.netIsConnected(context);
      if(!connect){
        Logger.e("HandleAppWakeService", " no connect");
        return ;
      }else{
        Logger.e("HandleAppWakeService", "connect");
      }
      list = getListByNet(spf, context, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP,CommConstants.CONFIG_WAKEUP_SHOW_LIMIT, CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM, PromDBU.PROM_WAKEUP);
    }else{
      list = getHandleAdInfoList(spf, context, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP,CommConstants.CONFIG_WAKEUP_SHOW_LIMIT, CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM, PromDBU.PROM_WAKEUP); 
    }
    
    Logger.e("PromReceiverService", list.toString());
    handledAdList(context,list);
  }
  
  public static void handledAdList(Context context,List<AdDbInfo> list) {
    for (AdDbInfo adDbInfo : list) {
      doWakeUp(context,adDbInfo);
    }
  }
  
  private static void doWakeUp(Context context,AdDbInfo info){
    PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(context, info.getPackageName());
    String apkPath = DownloadUtils.getInstance(context).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
    if (pInfo != null) {
      if (!PromUtils.getInstance(context).hasInstalled(new MyPackageInfo(info.getPackageName(), info.getVersionCode()))) {
        File f = new File(apkPath);
        if (f.exists() && showAdTime()) {
          MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), mPosition, 0, true);
          AppInstallUtils.installApp(context, apkPath, packageInfo);
          if(mPosition == StatsPromConstants.STATS_PROM_AD_INFO_POSITION_APP_NETCHANNGE){
            PromDBU.getInstance(context).updateAdInfoHasShowTimes(info, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
          }
        }
      } 
    } else {
      File f = new File(apkPath);
      if (f.exists() && showAdTime()) {
        MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), mPosition, 0, true);
        AppInstallUtils.installApp(context, apkPath, packageInfo);
        if(mPosition == StatsPromConstants.STATS_PROM_AD_INFO_POSITION_APP_NETCHANNGE){
          PromDBU.getInstance(context).updateAdInfoHasShowTimes(info, PromDBU.PROM_WAKEUP, PromDBU.PROM_WAKEUP_NAME);
        }
      }
    }
  }
  protected static boolean showAdTime(){
    long curtime = System.currentTimeMillis();
      try {
        String periods = spf.getString(CommConstants.CONFIG_APP_PERIODS,"");
        if(!TextUtils.isEmpty(periods)){
          String[] parts = periods.split(";");
          for (int i = 0; i < parts.length; i++) {
            String[] times = parts[i].split("-");
            
            String[] stpoint = times[0].split(":");
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), Integer.parseInt(stpoint[0]), Integer.parseInt(stpoint[1]));
            long start = c.getTimeInMillis();
            
            
            String[] endpoint = times[1].split(":");
            c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), Integer.parseInt(endpoint[0]), Integer.parseInt(endpoint[1]));
            long end = c.getTimeInMillis();
            if(start < curtime && curtime < end){
              Logger.e("HandleService", "showAdTime false");
              return false;
            }
          }
        }
      } catch (Exception e) {
        Logger.p(e);
      }
    Logger.e("HandleService", "showAdTime true");
    return true;
  }
  
  protected static List<AdDbInfo> getHandleAdInfoList( SharedPreferences spf,Context context,int position,String limitStr,String oneTimeNum,int promType){
    List<AdDbInfo> adlist = new ArrayList<AdDbInfo>();
    int limit = -1;//spf.getInt(CommConstants.CONFIG_WAKEUP_SHOW_LIMIT, -1);
    int oneNum = 0;//spf.getInt(CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM, 0);
    int maxShowtimes = 0;
    int hasShowTimes = 0;

    if(!TextUtils.isEmpty(limitStr)){
      limit = spf.getInt(limitStr, -1);
    }
//    if(!TextUtils.isEmpty(oneTimeNum)){
      oneNum = 1;//spf.getInt(oneTimeNum, 0);
//    }
    ArrayList<AdDbInfo> list = PromDBU.getInstance(context).queryAdInfo(promType);
    Logger.d("desktop111", "Handle list.size = "+list.size());
    for (AdDbInfo adDbInfo : list) {
      if(maxShowtimes < adDbInfo.getShowTimes()){
        maxShowtimes = adDbInfo.getShowTimes();
      }
      hasShowTimes = hasShowTimes+adDbInfo.getHasShowTimes();
    }
    if(position != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT&& limit != -1 && oneNum <= 0){
      return adlist;
    }
    
    int count = 0;    
    int markPosition = -1;
    int lastPostion = -1;
    for (int i = 0; i < list.size(); i++) {
      if(list.get(i).getShowmark() == 1){
        markPosition = i;
        break;
      }
    }
    for (int i = markPosition+1; i < list.size(); i++) {
      if(count >= oneNum){
        break;
      }
      String apkPath = DownloadUtils.getInstance(context).getApkDownloadFilePath(list.get(i).getPackageName(),list.get(i).getVersionCode());
      File f = new File(apkPath);
      if(  list.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum && f.exists()){
        if(!adlist.contains(list.get(i))){
          count++;
          adlist.add(list.get(i));
          lastPostion = i;
        }
      }
    }
    for (int i = 0; i < markPosition +1; i++) {
      if(count >= oneNum){
        break;
      }
      String apkPath = DownloadUtils.getInstance(context).getApkDownloadFilePath(list.get(i).getPackageName(),list.get(i).getVersionCode());
      File f = new File(apkPath);
      if(  list.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum && f.exists()){
        if(!adlist.contains(list.get(i))){
          count++;
          adlist.add(list.get(i));
          lastPostion = i;
        }
      }
    }
    if(promType != PromDBU.PROM_DESKTOPAD){
      if(lastPostion != -1){
        PromDBU.getInstance(context).resetShowmark(promType);
        PromDBU.getInstance(context).updateAdInfoShowmark(list.get(lastPostion), promType);
      }
    }
    
    return adlist;
  }
  
  protected static List<AdDbInfo> getListByNet(SharedPreferences spf,Context context,int position,String limitStr,String oneTimeNum,int promType){
    List<AdDbInfo> adlist = new ArrayList<AdDbInfo>();
    int limit = -1;//spf.getInt(CommConstants.CONFIG_WAKEUP_SHOW_LIMIT, -1);
    int oneNum = 0;//spf.getInt(CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM, 0);
    int maxShowtimes = 0;
    int hasShowTimes = 0;

    if(!TextUtils.isEmpty(limitStr)){
      limit = spf.getInt(limitStr, -1);
    }
//    if(!TextUtils.isEmpty(oneTimeNum)){
      oneNum = 1;//spf.getInt(oneTimeNum, 0);
//    }
    ArrayList<AdDbInfo> list = PromDBU.getInstance(context).queryAdInfo(promType);
    Logger.d("desktop111", "Handle list.size = "+list.size());
    for (AdDbInfo adDbInfo : list) {
      if(maxShowtimes < adDbInfo.getShowTimes()){
        maxShowtimes = adDbInfo.getShowTimes();
      }
      hasShowTimes = hasShowTimes+adDbInfo.getHasShowTimes();
    }
    if(position != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT&& limit != -1 &&(hasShowTimes >= limit || oneNum <= 0)){
      return adlist;
    }
    
    int count = 0;    
    int markPosition = -1;
    int lastPostion = -1;
    for (int i = 0; i < list.size(); i++) {
      if(list.get(i).getShowmark() == 1){
        markPosition = i;
        break;
      }
    }
    for (int i = markPosition+1; i < list.size(); i++) {
      if(count >= oneNum){
        break;
      }
      String apkPath = DownloadUtils.getInstance(context).getApkDownloadFilePath(list.get(i).getPackageName(),list.get(i).getVersionCode());
      File f = new File(apkPath);
      if( list.get(i).getHasShowTimes() < list.get(i).getShowTimes() && list.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum && f.exists()){
        if(!adlist.contains(list.get(i))){
          count++;
          adlist.add(list.get(i));
          lastPostion = i;
        }
      }
    }
    for (int i = 0; i < markPosition +1; i++) {
      if(count >= oneNum){
        break;
      }
      String apkPath = DownloadUtils.getInstance(context).getApkDownloadFilePath(list.get(i).getPackageName(),list.get(i).getVersionCode());
      File f = new File(apkPath);
      if( list.get(i).getHasShowTimes() < list.get(i).getShowTimes()  && list.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum && f.exists()){
        if(!adlist.contains(list.get(i))){
          count++;
          adlist.add(list.get(i));
          lastPostion = i;
        }
      }
    }
    if(promType != PromDBU.PROM_DESKTOPAD){
      if(lastPostion != -1){
        PromDBU.getInstance(context).resetShowmark(promType);
        PromDBU.getInstance(context).updateAdInfoShowmark(list.get(lastPostion), promType);
      }
    }
    return adlist;
  }
  
}
