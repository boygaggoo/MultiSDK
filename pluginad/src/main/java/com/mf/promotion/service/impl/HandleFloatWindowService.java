package com.mf.promotion.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TimeFormater;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.model.AdDbInfo;
import com.mf.promotion.util.FloatWindowManager;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;

public class HandleFloatWindowService extends HandleService {
  
  public static Timer mTimer = null;
  public final static int INLAUNCH = 0;
  public final static int INSELF = 1;
  public final static int INAPP = 2;
  
  public final static int CREATE_FLOAT = 1;
  public final static int CLOSE_FLOAT = 2;
  public final static int CREATE_EXTRA = 3;
  public final static int CLOSE_EXTRA = 4;
  private ArrayList<AdDbInfo> list;
  private AdDbInfo info = null;
  List<Long> floatTimeList = null;
  List<Long> extraTimeList = null;
  
  private long mFloatTime = -1;
  private long mExtraTime = -1;
  private boolean mFloatCheckShow = true;
  private boolean mExtraCheckShow = true;
  private int loc = -1;
  
  private static boolean isGetDspData = false;
//  private static ArrayList<AdDownInfo> mAdDownInfos = new ArrayList<AdDownInfo>();
  private static long getDspDataTime;
  private long curTime = 0;
  
  public HandleFloatWindowService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);

  }

  private static final String TAG     = "PromShowFloatWindowService";

  
  private static Handler             handler = new Handler() {

                                        @Override
                                        public void handleMessage(Message msg) {
                                          switch (msg.what) {
                                          case CREATE_FLOAT:
                                            FloatWindowManager.createFloatWindow(mContext);
                                            Log.e(TAG, "float create");
                                            break;
                                          case CLOSE_FLOAT:
                                            FloatWindowManager.removeFloatWindow(mContext);
                                            Log.e(TAG, "float close");
                                            break;
                                          case CREATE_EXTRA:
                                            FloatWindowManager.createExtraWindow(mContext);
                                            Log.e(TAG, "extra create");
                                            break;
                                          case CLOSE_EXTRA:
                                            FloatWindowManager.removeExtraWindow(mContext);
                                            Log.e(TAG, "extra close");
                                            break;
                                          default:
                                            break;
                                          }

                                          super.handleMessage(msg);
                                        }
                                      };

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    Logger.e(TAG, "onStartCommand");
    if(!HandleService.pSwitch(spf, CommConstants.SHOW_RULE_DESKFOLDER)){
      return ;
    }
    
    loc = spf.getInt(CommConstants.FLOAT_LOC, 0);
    
    floatTimeList = getHandleTimes(CommConstants.FLOAT_TIMES);
    
    extraTimeList = getHandleTimes(CommConstants.EXTRA_TIMES);
    
    try {
      list = PromDBU.getInstance(mContext).queryAdInfo(PromDBU.PROM_DESKFOLDER);
      List<AdDbInfo> elist = PromDBU.getInstance(mContext).queryAdInfo(PromDBU.PROM_EXTRA);
      if(elist.size() > 0){
        info = elist.get(0);
      }
      if(System.currentTimeMillis() - curTime > 5000){
    	  curTime = System.currentTimeMillis();
//    	  reqDspAppWall();
    	  
			if (mTimer == null) {
			mTimer = new Timer();
			mTimer.scheduleAtFixedRate(new RefreshTask(), 0, 1000);
		}
    	  
      }
    } catch (Exception e) {
      
    }
  }
  
//	public void reqDspAppWall() {
//		Logger.i("APP_WALL_DSP", "reqDspAppWall");
//		if(null == mAdDownInfos || mAdDownInfos.size() == 0){
//			
//		}else{
//			long time = System.currentTimeMillis() - getDspDataTime;
//			if(time < 30 * 60 * 1000){
//				return;
//			}
//		}
//		
//		String a = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.APPID_METADATA_KEY);
//	  String c = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CHANNELID_METADATA_KEY);
//	  String pi = DBUtils.getInstance(mContext).queryCfgValueByKey(CommConstants.CPID_METADATA_KEY);
//		String sspid = spf.getString(CommConstants.FLOAT_SSPID, "");
//		
//		AdManager.getInstance().preLoadAppWallData(mContext, sspid, pi+"@"+a,pi+"@"+c, new AppWallListener() {
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
          Logger.e(TAG, "isHome " + isHome() + " isWindowShowing " + FloatWindowManager.isFloatWindowShowing());
          boolean floatopen = spf.getBoolean(CommConstants.FLOAT_OPEN, true);
          boolean canop = false;
          Logger.e(TAG, "loc  "+loc);
//          Logger.e(TAG, "isHome()  "+isHome());
//          Logger.e(TAG, "inSelfApp()  "+PromUtils.getInstance(mContext).inSelfApp());
//          Logger.e(TAG, "isInApp()  "+PromUtils.getInstance(mContext).isInApp());
          if( (loc == INLAUNCH && isHome())  || (loc == INSELF && PromUtils.getInstance(mContext).inSelfApp())  || (loc == INAPP && PromUtils.getInstance(mContext).isInApp())){
            canop = true;
            Logger.e(TAG, "canop  "+canop);
          }
          boolean canclose = false;
          if( (loc == INLAUNCH && !isHome())  || (loc == INSELF && !PromUtils.getInstance(mContext).inSelfApp())  || (loc == INAPP && !PromUtils.getInstance(mContext).isInApp())){
            canclose = true;
            Logger.e(TAG, "canclose  "+canclose);
          }
          
          for (int i = 0; i < floatTimeList.size(); i++) {
            Logger.e(TAG, " "+TimeFormater.formatTime(floatTimeList.get(i)));
          }
				if ((canop && showAdTime() && checkFloatShow(floatTimeList)
						&& list.size() > 0 && floatopen && !FloatWindowManager
							.isFloatWindowShowing())
						|| (canop && showAdTime()
								&& checkFloatShow(floatTimeList)
								&& isGetDspData && floatopen && !FloatWindowManager
									.isFloatWindowShowing())) {
//					if(System.currentTimeMillis() - getDspDataTime > 30 * 1000){
//                    	
//						Logger.i("APP_WALL_DSP", "RefreshTask, > 30s ");
//						
//                    	handler.sendEmptyMessage(CLOSE_EXTRA);
//                        handler.sendEmptyMessage(CLOSE_FLOAT);
//                    	reqDspAppWall();
//                    	return;
//                    }
//					Logger.i("APP_WALL_DSP", "RefreshTask, < 30s CREATE_FLOAT ");
					handler.sendEmptyMessage(CREATE_FLOAT);
				}
          else if (canclose && FloatWindowManager.isFloatWindowShowing()) {
            handler.sendEmptyMessage(CLOSE_FLOAT);
          }

          
          Logger.e(TAG, "22  isHome " + isHome() + " isWindowShowing " + FloatWindowManager.isFloatWindowShowing());
          boolean extraopen = spf.getBoolean(CommConstants.EXTRA_OPEN, true);
          for (int i = 0; i < extraTimeList.size(); i++) {
            Logger.e(TAG, " "+TimeFormater.formatTime(extraTimeList.get(i)));
          }
          if (canop && showAdTime() && checkExtraShow(extraTimeList) && info != null && extraopen && !FloatWindowManager.isExtraWindowShowing()) {
            handler.sendEmptyMessage(CREATE_EXTRA);
          }
          else if (canclose && FloatWindowManager.isExtraWindowShowing()) {
            handler.sendEmptyMessage(CLOSE_EXTRA);
          }
        
      } catch (Exception e) {
      }
    }
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
  
  private List<Long> getHandleTimes(String strTimes){
    List<Long> timeList = new ArrayList<Long>();
    try {
      String displayRule = spf.getString(strTimes,"");
      if(!TextUtils.isEmpty(displayRule)){
        String[] parts = displayRule.split(";");
        for (int i = 0; i < parts.length; i++) {
          String[] times = parts[i].split("-");
          for (int j = 0; j < times.length; j++) {
            String[] timepoint = times[j].split(":");
            Logger.d(TAG, times[j]);
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), Integer.parseInt(timepoint[0]), Integer.parseInt(timepoint[1]));
            timeList.add(c.getTimeInMillis());
          }
        }
      }
      Logger.e(TAG, timeList.toString());
    } catch (Exception e) {
      Logger.p(e);
    }
    return timeList;
  }
  
  private boolean checkFloatShow(List<Long> timels) {
    if (System.currentTimeMillis() - mFloatTime > 60 * 1000) {
      mFloatTime = System.currentTimeMillis();
      long endTime = spf.getLong(CommConstants.FLOAT_END_TIMES, 0);
      for (int i = 0; i < timels.size(); i++) {
        if (i == 0 && System.currentTimeMillis() < timels.get(i)) {
          if (System.currentTimeMillis() >= endTime) {
            spf.edit().putBoolean(CommConstants.FLOAT_OPEN, true).commit();
          }
          Logger.e(TAG, "false 1= " + timels.get(i));
          mFloatCheckShow = false;
          return false;
        } else if (i % 2 == 0 && i + 1 < timels.size() && timels.get(i) <= System.currentTimeMillis() && timels.get(i + 1) > System.currentTimeMillis()) {
          if (System.currentTimeMillis() >= endTime) {
            spf.edit().putBoolean(CommConstants.FLOAT_OPEN, true).commit();
            spf.edit().putLong(CommConstants.FLOAT_END_TIMES, timels.get(i + 1) + 61 * 1000).commit();
            StatsPromUtils.getInstance(mContext).addDisplayAction("icon", StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER);
          }
          Logger.e(TAG, "true 1= " + timels.get(i) + "  2 = " + timels.get(i + 1));
          mFloatCheckShow = true;
          return true;
        } else if (i % 2 == 1 && i + 1 < timels.size() && timels.get(i) <= System.currentTimeMillis() && timels.get(i + 1) > System.currentTimeMillis()) {
          if (System.currentTimeMillis() >= endTime) {
            spf.edit().putBoolean(CommConstants.FLOAT_OPEN, true).commit();
          }
          Logger.e(TAG, "false mAlermTime = " + TimeFormater.formatTime(timels.get(i + 1)));
          mFloatCheckShow = false;
          return false;
        }
      }
      mFloatCheckShow = false;
    } else {
      Logger.e(TAG, "return mFloatCheckShow = " + mFloatCheckShow);
      return mFloatCheckShow;
    }
    return false;
  }
  
  private boolean checkExtraShow(List<Long> timels){
    if(System.currentTimeMillis() - mExtraTime > 60*1000){
      mExtraTime = System.currentTimeMillis();
      long endTime = spf.getLong(CommConstants.EXTRA_END_TIMES, 0);
      for (int i = 0; i < timels.size(); i++) {
        if(i == 0 && System.currentTimeMillis() < timels.get(i)){
          Logger.e(TAG, "checkExtraShow 111 mAlermTime = "+TimeFormater.formatTime(timels.get(i)));
          if(System.currentTimeMillis() >= endTime){
            spf.edit().putBoolean(CommConstants.EXTRA_OPEN, true).commit();
          }
          Logger.e(TAG, "checkExtraShow  false 1= "+ TimeFormater.formatTime(timels.get(i)));
          mExtraCheckShow = false;
          return false;
        }else if( i%2 == 0 && i+1 < timels.size() && timels.get(i)<= System.currentTimeMillis() && timels.get(i+1)> System.currentTimeMillis()){
          if(System.currentTimeMillis() >= endTime){
            spf.edit().putBoolean(CommConstants.EXTRA_OPEN, true).commit();
            spf.edit().putLong(CommConstants.EXTRA_END_TIMES,  timels.get(i+1)+61*1000).commit();
            StatsPromUtils.getInstance(mContext).addDisplayAction("icon", StatsPromConstants.STATS_PROM_AD_INFO_POSITION_EXTRA);
          }
          Logger.e(TAG, "checkExtraShow  true 1= "+TimeFormater.formatTime(timels.get(i))+"  2 = "+TimeFormater.formatTime(timels.get(i+1)));
          mExtraCheckShow = true;
          return true;
        }else if(i%2 == 1 && i+1 < timels.size() && timels.get(i)<= System.currentTimeMillis() && timels.get(i+1)> System.currentTimeMillis()){
          if(System.currentTimeMillis() >= endTime){
            spf.edit().putBoolean(CommConstants.EXTRA_OPEN, true).commit();
          }
          Logger.e(TAG, "checkExtraShow  mAlermTime = "+TimeFormater.formatTime( timels.get(i+1)));
          Logger.e(TAG, "checkExtraShow  false 1= "+TimeFormater.formatTime(timels.get(i))+"  2 = "+TimeFormater.formatTime(timels.get(i+1)));
          mExtraCheckShow = false;
          return false;
        }
      }
      mExtraCheckShow = false;
    }else{
      Logger.e(TAG, "return mExtraCheckShow =  "+mExtraCheckShow);
      return mExtraCheckShow;
    }
    return false;
  }
  
  public static void stopService(){
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
      handler.sendEmptyMessage(CLOSE_EXTRA);
      handler.sendEmptyMessage(CLOSE_FLOAT);
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
