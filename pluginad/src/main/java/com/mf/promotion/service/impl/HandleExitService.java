package com.mf.promotion.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.model.AdDbInfo;
import com.mf.promotion.activity.ExitActivity;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.PromUtils;

public class HandleExitService  extends HandleService {
  
  public static Timer mTimer = null;

  private ArrayList<AdDbInfo> list;
  public int times = -1;
  public int pro_times = -1;
  public List<String> plist = null;
  public String lastPackage = "";
  public boolean first = true;
  
  private static boolean isGetDspData = false;
//  private static ArrayList<AdDownInfo> mAdDownInfos = new ArrayList<AdDownInfo>();
  private static long getDspDataTime;
  
  public HandleExitService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
  }

  private static final String TAG     = "HandleExitService";


  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    Logger.e(TAG, "onStartCommand");
    if(!HandleService.pSwitch(spf, CommConstants.SHOW_RULE_EXIT)){
      return ;
    }
    
    times = spf.getInt(CommConstants.EXIT_TIMES, -1);
    if(times < 0){
      return;
    }
    
    long daytime = spf.getLong(CommConstants.EXIT_DAYTIME, 0);
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    if(Math.abs(calendar.getTimeInMillis()-daytime) > 23*3600*1000){
      spf.edit().putLong(CommConstants.EXIT_DAYTIME, calendar.getTimeInMillis()).commit();
      spf.edit().putInt(CommConstants.EXIT_SHOW_TIMES, 0).commit();
      Logger.e(TAG, "showPlugin   Calendar" + calendar.getTimeInMillis());
    }
    
    Logger.e(TAG, "times = "+times);
    pro_times = spf.getInt(CommConstants.EXIT_PROTECT_TIME, -1);
    if(pro_times < 0){
      return;
    }
    Logger.e(TAG, "times = "+pro_times);
    plist = getPackageList(CommConstants.EXIT_PACKAGES);
    if(plist.size() <= 0){
      return;
    }
    Logger.e(TAG, "plist = "+plist.size());
    try {
      list = PromDBU.getInstance(mContext).queryAdInfo(PromDBU.PROM_EXIT);
      if(list.size() <= 0){
        return;
      }
      
		if (mTimer == null) {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new RefreshTask(), 0, 1000);
	}
      
//      reqDspAppWall();
    } catch (Exception e) {
      
    }
  }
  
//  public void reqDspAppWall() {
//	  if(null == mAdDownInfos || mAdDownInfos.size() == 0){
//			
//		}else{
//			long time = System.currentTimeMillis() - getDspDataTime;
//			if(time < 30 * 60 * 1000){
//				return;
//			}
//		}
//		Logger.i("APP_WALL_DSP", "reqDspAppWall  aaaa");
//	  String a = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//    String c = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//    String pi = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//    String sspid = spf.getString(CommConstants.EXIT_SSPID, "");
//		
//		AdManager.getInstance().preLoadAppWallData(mContext, sspid,  pi+"@"+a,pi+"@"+c, new AppWallListener() {
//			
//			@Override
//			public void onSuccess(List<AdDownInfo> adDownInfos) {
//				Logger.i("APP_WALL_DSP", "reqDspAppWall,onSuccess");
//				getDspDataTime = System.currentTimeMillis();
//				isGetDspData = true;
//				mAdDownInfos.clear();
//				mAdDownInfos.addAll(adDownInfos);
//				//1.获取当日预下载总数
//	              int dayLimit = AdPreferenceUtil.getInstance(mContext).getAdListPreDownLimit();
//	            //2.获取已下载总数
//	              int hasDLLimit = AdPreferenceUtil.getInstance(mContext).getAdPreDownLimit();
//	              int readyDLNum = 0;
//	              if(dayLimit - hasDLLimit > 0){
//	                int dlNum = dayLimit - hasDLLimit;
//	                if(mAdDownInfos.size() > 0){
//	                  for (AdDownInfo adDownInfo : adDownInfos) {
//	                    if(adDownInfo.getPreDown().equals(String.valueOf(1)) && readyDLNum <= dlNum){
//	                      mAdDownInfos.remove(adDownInfo);
//	                      Logger.e(TAG, " mAdDownInfos size 2: " + mAdDownInfos.size());
//	                      createBgDL(adDownInfo);
//	                    }
//	                  }
//	                }
//	              }
//				if (mTimer == null) {
//					mTimer = new Timer();
//					mTimer.scheduleAtFixedRate(new RefreshTask(), 0, 1000);
//				}
//			}
//			
//			@Override
//			public void onSuccess() {
//				
//			}
//			
//			@Override
//			public void onFailed(boolean arg0, String arg1) {
//				Logger.i("APP_WALL_DSP", "reqDspAppWall,onFailed");
//				isGetDspData = false;
//				if (mTimer == null) {
//					mTimer = new Timer();
//					mTimer.scheduleAtFixedRate(new RefreshTask(), 0, 1000);
//				}
//			}
//		});
//	}
	
//	public static ArrayList<AdDownInfo> getDspAppWallData(){
//		if((null != mAdDownInfos) && isGetDspData){
//			return mAdDownInfos;
//		}else{
//			return null;
//		}
//	}

  class RefreshTask extends TimerTask {
    @Override
    public void run() {
      try {
        Logger.e(TAG, "aaaaaa");    
          String topPackage = PromUtils.getInstance(mContext).getTopPackageName();
          Logger.e(TAG, "topPackage = "+topPackage);
          if(TextUtils.isEmpty(lastPackage) || TextUtils.isEmpty(topPackage)){
            lastPackage = topPackage;
            return;
          }
          int curtime = spf.getInt(CommConstants.EXIT_SHOW_TIMES, -1);
          if(curtime >= times){
            Logger.e(TAG, "curtime = "+curtime+"    times = "+times);
            return;
          }
          long last_time = spf.getLong(CommConstants.EXIT_LAST_TIME, -1);
          if(System.currentTimeMillis() - last_time < pro_times*1000 ){
            Logger.e(TAG, "System.currentTimeMillis() = "+System.currentTimeMillis()+"    pro_times = "+pro_times);
            return;
          }
          if(!TextUtils.isEmpty(topPackage) && !topPackage.equals(lastPackage)){
            Logger.e(TAG, "lastPackage = "+lastPackage);
            Logger.e(TAG, "plist = "+plist);
            if(checkExit(plist, lastPackage)){  
              Logger.e(TAG, "lastPackage 11= "+lastPackage);
              if(isHome()){
                Logger.e(TAG, "lastPackage 22= "+lastPackage);
//                if(!PromUtils.getInstance(mContext).isInRunningTasks(lastPackage)){
                  Logger.e(TAG, "lastPackage 33= "+lastPackage);
                  spf.edit().putInt(CommConstants.EXIT_SHOW_TIMES, curtime+1).commit();
                  spf.edit().putLong(CommConstants.EXIT_LAST_TIME, System.currentTimeMillis()).commit();
                  goToExitActivity();
//                }
              }
            }
            lastPackage = topPackage;
          }
      } catch (Exception e) {
        
      }
    }
  }
  
  
  private void goToExitActivity() {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setComponent(new ComponentName(mContext.getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
    intent.putExtra(PromApkConstants.EXTRA_CLASS, ExitActivity.class.getCanonicalName());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Logger.debug(TAG, "PromFloatWindowAdActivity ");
    mContext.getApplicationContext().startActivity(intent);
  }

  
  
  /**
   * 判断当前界面是否是桌面
   */
  private boolean isHome() {
    ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
    return getHomes().contains(rti.get(0).topActivity.getPackageName());
  }

  /**
   * 获得属于桌面的应用的应用包名称
   * 
   * @return 返回包含所有包名的字符串列表
   */
  private List<String> getHomes() {
    List<String> names = new ArrayList<String>();
    PackageManager packageManager = mContext.getPackageManager();
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    for (ResolveInfo ri : resolveInfo) {
      names.add(ri.activityInfo.packageName);
    }
    return names;
  }
  
  private boolean checkExit(List<String> list,String lastpackage){
    if(TextUtils.isEmpty(lastpackage)){
      return false;
    }
    for (String str : list) {
      if(!TextUtils.isEmpty(str) && lastpackage.equals(str)){
        return true;
      }
      
    }
    return false;
  }
  
  
  private List<String> getPackageList(String strTimes){
    List<String> pkList = new ArrayList<String>();
    try {
      String displayRule = spf.getString(strTimes,"");
      if(!TextUtils.isEmpty(displayRule)){
        String[] parts = displayRule.split(";");
          for (int j = 0; j < parts.length; j++) {
            if(!TextUtils.isEmpty(parts[j])){
              pkList.add(parts[j]);
            }
          }
      }
      Logger.e(TAG, pkList.toString());
    } catch (Exception e) {
      Logger.p(e);
    }
    return pkList;
  }
  
  public static void stopService(){
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
      return;
    }
  }

  @Override
  public void handledAds(List<String> ads) {
    // TODO Auto-generated method stub
    
  }
  
  public boolean checkFloatData(){
    List<AdDbInfo> dblist = PromDBU.getInstance(mContext).queryAdInfo(PromDBU.PROM_DESKFOLDER);
    if(dblist.size() <= 0){
      return false; 
    }
    return true;
  }
  
//  private void createBgDL(AdDownInfo adDownInfo){
//	    int preLimit = AdPreferenceUtil.getInstance(mContext).getAdListPreDownLimit();
//	    Logger.e("Adbs", "limit:" + preLimit);
//	    if(preLimit <= 0){
//	      return;
//	    }
//	    int preLimitDb = AdPreferenceUtil.getInstance(mContext).getAdPreDownLimit();
//	      if(preLimitDb >= preLimit){
//	        return;
//	      }else{
//	        startDLFromAdInfo(adDownInfo);
//	      }
//	  }
	  
//	  private void startDLFromAdInfo(AdDownInfo adDownInfo){
//	    DownloadInfo downloadInfo = CastUtil.castToDownloadInfo(mContext, adDownInfo);
//	    AppUtil.downloadApp(mContext, downloadInfo);
//	  }
}
