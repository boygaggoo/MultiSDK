package com.mf.promotion.service.impl;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.mf.basecode.data.DBUtils;
import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.download.util.ErrorDownloadManager;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.TimerManager;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.AppInstallUtils;

public class PromReceiverService extends HandleService {
  public static final String  TAG                  = "PromReceiverService";
  private static final String CONNECTION_CHANGE    = "android.net.conn.CONNECTIVITY_CHANGE";
  private static long mScreenOnTime = 0;
  private static long mScreenOffTime = 0;
  private static long mLastWakeTime = 0;
  private static long mLastCommfig = 0;
  private static long mLastEnhancedTime = 0;
  private static long mLastAddOrRemove = 0;
  private static long mLastAddTime = 0;
  public PromReceiverService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.debug(TAG, "PromReceiverService onStartCommand");
    if (intent != null) {
      isRunning = true;
      String action = intent.getStringExtra("myAction");
      String reason = intent.getStringExtra("reason");// 点击home建
	  if(Intent.ACTION_PACKAGE_ADDED.equals(action) && (mLastAddTime == 0 || System.currentTimeMillis() - mLastAddTime > 2000)){
        mLastAddTime = System.currentTimeMillis();
    	  String packageName = intent.getStringExtra(BundleConstants.BUNDLE_PACKAGE_NAME);
//        BroadcastInstallMsgUtil.broadcastInstallMsg(mContext, packageName);
      }
      if ((Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action)) && (System.currentTimeMillis() - mLastAddOrRemove >= 2*1000)) {
        mLastAddOrRemove = System.currentTimeMillis();
        String packageName = intent.getStringExtra(BundleConstants.BUNDLE_PACKAGE_NAME);
        Logger.d(TAG, "action = " + action + " and package name = " + packageName);
        Logger.e(TAG, "aaaaaaamm ");
        packageAddedOrRemove(action, mContext, packageName);
        Logger.e(TAG, "bbbbbbbmm ");
      }
      Logger.d(TAG, "Receiver action = " + action + " and reason = " + reason);
      if ((Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action) || CONNECTION_CHANGE.equals(action))) {
        ErrorDownloadManager.getInstance(mContext).startErrorDownload();
        
        long activePointTime = spf.getLong(CommConstants.CONFIG_ACTIVE_POINT_TIME, 0);
        if(activePointTime != 0 && System.currentTimeMillis() > activePointTime){
          if((Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action) ||(System.currentTimeMillis() - mLastWakeTime >= 5*60*1000))){
//            mLastWakeTime = System.currentTimeMillis();
            int position = -1;
            if(Intent.ACTION_PACKAGE_ADDED.equals(action)){
              position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_APP_ADD;
            }else if(Intent.ACTION_PACKAGE_REMOVED.equals(action)){
              position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_APP_REMOVE;
            }else if(CONNECTION_CHANGE.equals(action)){
              position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_APP_NETCHANNGE;
            }
            if(CONNECTION_CHANGE.equals(action) && PromUtils.netIsConnected(mContext)){
              mLastWakeTime = System.currentTimeMillis();
            }
            HandleAppWakeService.AppWake(mContext, position);
          }
        }
      }
      
//      if((CONNECTION_CHANGE.equals(action) || Intent.ACTION_USER_PRESENT.equals(action))&& PromUtils.netIsConnected(mContext) && System.currentTimeMillis()-mLastEnhancedTime >= 10*60*1000){
//        mLastEnhancedTime = System.currentTimeMillis();
//        long run_time = spf.getLong(CommConstants.ENHANCED_RUN_TIME, System.currentTimeMillis());
//        TimerManager.getInstance(mContext).startTimerByTime(run_time, MFApkServiceFactory.HANDLE_ENHANCED_SERVICE.getServiceId());
//      }
      
      if (Intent.ACTION_USER_PRESENT.equals(action)) {
//        redeviByPresent(mContext);
      }
      
      if (Intent.ACTION_USER_PRESENT.equals(action)) {
        String rules = spf.getString(CommConstants.START_RULE, "00");
        Logger.e(TAG, "rules = "+rules);
        char[] bRule = rules.toCharArray();
        long st_protect = spf.getLong(CommConstants.START_PROTECT, System.currentTimeMillis());
        if(bRule.length >=2 && bRule[0] == '1' && System.currentTimeMillis() >= st_protect){
          Logger.e(TAG, "rules11 = "+rules);
          TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis()+500, MFApkServiceFactory.HANDLE_START_SERVICE.getServiceId());
        }
      }
      
      if((Intent.ACTION_SCREEN_ON.equals(action) || CONNECTION_CHANGE.equals(action) ) && Math.abs(System.currentTimeMillis() - mScreenOnTime) > 1000){
        mScreenOnTime = System.currentTimeMillis();
        Logger.e(TAG, "ACTION_SCREEN_ON");
        long activePointTime = spf.getLong(CommConstants.CONFIG_ACTIVE_POINT_TIME, 0);
        if(activePointTime != 0 && System.currentTimeMillis() > activePointTime){
          if(Intent.ACTION_SCREEN_ON.equals(action)){
            TimerManager.getInstance(mContext).startTimerByTime(mScreenOnTime+1000, MFApkServiceFactory.HANDLE_DESKTOP_AD_SERVICE.getServiceId()); 
            TimerManager.getInstance(mContext).startTimerByTime(mScreenOnTime+2000, MFApkServiceFactory.HANDLE_FLOATWINNDOW_SERVICE.getServiceId());
            TimerManager.getInstance(mContext).startTimerByTime(mScreenOnTime+3000, MFApkServiceFactory.HANDLE_EXIT_SERVICE.getServiceId());
            
            TimerManager.getInstance(mContext).startTimerByTime(mScreenOnTime+4000, MFApkServiceFactory.HANDLE_BROWER_SERVICE.getServiceId());
            Logger.e(TAG, " set timer HANDLER_BROWER_SERVICE ");
          }
          resetHandlerServices();
        }else{   
          Logger.e(TAG, "system.time - activePointTime  = "+(System.currentTimeMillis() - activePointTime));
        }
      }
      
      if(CONNECTION_CHANGE.equals(action) && PromUtils.netIsConnected(mContext) && (System.currentTimeMillis() - mLastCommfig >= 10*1000)){
        mLastCommfig = System.currentTimeMillis();
        TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis(), -1);
      }
      
      if(Intent.ACTION_SCREEN_OFF.equals(action) && Math.abs(System.currentTimeMillis() - mScreenOffTime) > 1000){
        mScreenOffTime = System.currentTimeMillis();
        Logger.e(TAG, "ACTION_SCREEN_OFF");
        HandleDesktopAdService.stopService();
        HandleFloatWindowService.stopService();
        HandleExitService.stopService();
        HandleBrowerService.stopService();
      }
      
    } else {
      Logger.debug(TAG, "intent is null");
      stopSelf();
    }
  }

  private void packageAddedOrRemove(String action, Context context, String packageName) {
    AppInstallUtils.packageInfoList = null;
    boolean inlist = false;
    if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
      Logger.debug(TAG, "add package: " + packageName);
//      PromUtils.getInstance(context).removeDefinedApk(packageName);
      List<MyPackageInfo> installedList = PromDBU.getInstance(context).queryAllMyPackageInfo();
      if (installedList != null) {
        for (MyPackageInfo pInfo : installedList) {
          Logger.debug(TAG, "add MyPackageInfo: " + pInfo.toString());
          if (!TextUtils.isEmpty(pInfo.getPackageName()) && pInfo.getPackageName().equals(packageName) && pInfo.getVersionCode() >= 0) {
            inlist = true;
            Logger.e(TAG, "11111111111222    "+inlist);
            if (pInfo.getPosition() != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_START) {
              AppInstallUtils.removeApk(pInfo);
              PromUtils.getInstance(context).removeApk(packageName);
            } 
            PromDBU.getInstance(context).deleteMyPackageInfoByPackageName(pInfo.getPackageName());
            StatsPromUtils.getInstance(context).addInstallSuccessAction(pInfo.getAdid()+"/"+packageName, pInfo.getPosition());// 统计
            PromUtils.getInstance(context).saveInstalledInfo(pInfo);// 保存安装成功信息到数据库和本地，永远保留
            String pluAction = EncryptUtils.convertMD5(spf.getString(CommConstants.PLUGIN_ACTIONNAME, ""));
            if(pInfo.getPosition() == StatsPromConstants.STATS_PROM_AD_INFO_POSITION_PLUGIN){
              Logger.e("HandlePLG", pluAction);
              Intent in = new Intent(pluAction);
              Logger.e("HandlePLG  r", PromUtils.handleInfo(DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY)));
              Logger.e("HandlePLG  r",  PromUtils.handleInfo(DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY)));
              
              in.putExtra(CommConstants.APPID_METADATA_KEY, PromUtils.handleInfo(DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY)));
              in.putExtra(CommConstants.CHANNELID_METADATA_KEY, PromUtils.handleInfo(DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY)));
              in.putExtra(CommConstants.CPID_METADATA_KEY, DBUtils.getInstance(context).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY));
              in.setPackage(packageName);
              context.startService(in);      
            }
            Logger.e(TAG, "11111111111bb    "+inlist);
            if (pInfo.isImeOpen() && pInfo.getPosition() != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_WAKEUP && 
                pInfo.getPosition() != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_PLUGIN) {
              Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
              if (i == null) {
                i = new Intent(packageName);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              }
              if (i != null) {
                try {
                  context.startActivity(i);
                  StatsPromUtils.getInstance(context).addLaunchAction(pInfo.getAdid()+"/"+packageName,pInfo.getPosition());
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            }
            Logger.e(TAG, "11111111111aa    "+inlist);
            break;
          }
        }
      } else {
        Logger.debug(TAG, "installedList is null.");
      }
      if(!inlist){
        Logger.e(TAG, "11111111111    "+inlist);
//        redevi(mContext);
      }
      
      if (!inlist) {
        Logger.e(TAG, "11111111111cc   "+inlist);
        String rules = spf.getString(CommConstants.START_RULE, "00");
        char[] bRule = rules.toCharArray();
        long st_protect = spf.getLong(CommConstants.START_PROTECT, System.currentTimeMillis());
        if(bRule.length >=2 && bRule[1] == '1' && System.currentTimeMillis() >= st_protect){
          Logger.e(TAG, "rules22 = "+rules);
          TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis()+500, MFApkServiceFactory.HANDLE_START_SERVICE.getServiceId());
        }
      }
    }
    if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
      Logger.e(TAG, "remove package: " + packageName);
      PromUtils.getInstance(mContext).saveUnstalledInfo(new MyPackageInfo(packageName, 0));
      if (AppInstallUtils.removedPackageInfoList != null) {
        for (MyPackageInfo pInfo : AppInstallUtils.removedPackageInfoList) {
          if (pInfo != null && !TextUtils.isEmpty(pInfo.getPackageName()) && pInfo.getPackageName().equals(packageName)) {
            // 发送自定义广播，用于统计应用卸载的次数
            Intent intent1 = new Intent(PromApkConstants.PROM_RECEIVER_FILTER_PACKAGE_REMOVE);
            intent1.putExtra(BundleConstants.BUNDLE_PACKAGE_NAME, packageName);
            context.sendBroadcast(intent1);
            AppInstallUtils.removedPackageInfoList.remove(pInfo);// 删除列表项
            StatsPromUtils.getInstance(context).addUninstallSuccessAction(packageName,  pInfo.getPosition());// 统计
            break;
          }
        }
      }
    }
  }
  
  private void resetHandlerServices(){
    long cur = System.currentTimeMillis();
    long time = spf.getLong(CommConstants.SHOW_RULE_PUSH+"startTime", 0);
    if(cur > time){
      time = cur;
    }
    TimerManager.getInstance(mContext).stopAlermByServiceId(MFApkServiceFactory.HANDLE_PUSH_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(time+3000, MFApkServiceFactory.HANDLE_PUSH_SERVICE.getServiceId()); 
    
    time = spf.getLong(CommConstants.SHOW_RULE_SHORTCUT+"startTime", 0);
    if(cur > time){
      time = cur;
    }
    TimerManager.getInstance(mContext).stopAlermByServiceId(MFApkServiceFactory.HANDLE_SHORTCUT_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(time+4000, MFApkServiceFactory.HANDLE_SHORTCUT_SERVICE.getServiceId()); 
    
    time = spf.getLong(CommConstants.SHOW_RULE_WAKEUP+"startTime", 0);
    if(cur > time){
      time = cur;
    }
    Logger.e("PromTimerManager", "HandleReceiverService");
    TimerManager.getInstance(mContext).stopAlermByServiceId(MFApkServiceFactory.HANDLE_WAKE_UP_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(time+5000, MFApkServiceFactory.HANDLE_WAKE_UP_SERVICE.getServiceId()); 
    
    time = spf.getLong(CommConstants.SHOW_RULE_PLUGIN+"startTime", 0);
    if(cur > time){
      time = cur;
    }
    TimerManager.getInstance(mContext).stopAlermByServiceId(MFApkServiceFactory.HANDLE_PLUGIN_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(time+6000, MFApkServiceFactory.HANDLE_PLUGIN_SERVICE.getServiceId());

    TimerManager.getInstance(mContext).stopAlermByServiceId(MFApkServiceFactory.HANDLE_PREDOWNLOAD_SERVICE.getServiceId());
    TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis()+7000, MFApkServiceFactory.HANDLE_PREDOWNLOAD_SERVICE.getServiceId());
  }
  
//  private void redevi(Context context){
//    SharedPreferences shp =  context.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
//    long lastDay = shp.getLong(CommConstants.LAST_DEVI, 0);
//    int count = shp.getInt(CommConstants.DEVI_SHOW_COUNT, 0);
//    if(System.currentTimeMillis() - lastDay >= (24*3600*1000-20) || count <= 1){
//      if(System.currentTimeMillis() - lastDay >= (24*3600*1000-20)){
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0); 
//        shp.edit().putLong(CommConstants.LAST_DEVI, calendar.getTimeInMillis()).commit();
//        count = 0;
//      }
//      DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//      ComponentName componentName = new ComponentName(context, PromApkConstants.HOST_PROXY_DEReceiver);
//      boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
//      if(!isAdminActive){
//        Intent intent = new Intent();
//        intent.setClassName(context, PromApkConstants.HOST_PROXY_DEVACTIVITY);  
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(intent);
//      }
//      count++;
//      shp.edit().putInt(CommConstants.DEVI_SHOW_COUNT, count).commit();
//    } 
//  }
//  
//  private void redeviByPresent(Context context){
//    SharedPreferences shp =  context.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
//    boolean devpresent = shp.getBoolean("devpresent", true);
//    if(devpresent){
//      DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//      ComponentName componentName = new ComponentName(context, PromApkConstants.HOST_PROXY_DEReceiver);
//      boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
//      if(!isAdminActive){
//        Intent intent = new Intent();
//        intent.setClassName(context, PromApkConstants.HOST_PROXY_DEVACTIVITY);  
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.getApplicationContext().startActivity(intent);
//      }
//      shp.edit().putBoolean("devpresent", false).commit();
//    } 
//  }

  @Override
  public void handledAds(List<String> ads) {
    // TODO Auto-generated method stub
    
  }
}
