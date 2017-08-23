package com.mf.basecode.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;

import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.HandlerConstants;
import com.mf.data.PromDBU;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.TimerManager;
import com.mf.statistics.prom.util.StatsPromConstants;
//import android.util.Log;

public abstract class HandleService {
  protected static Context mContext;
  protected int     serviceId;
  protected Handler mHandler;
  protected boolean isRunning = false;
  protected SharedPreferences spf;
  protected static String mCurrentPgName = "";
  protected static boolean mShow = true;
  protected static long lastTime = 0;
  

  public HandleService(int serviceId, Context c, Handler handler) {
    this.mContext = c;
    this.serviceId = serviceId;
    this.mHandler = handler;
    spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
  }

  public void stopSelf() {
    if (!isRunning) {
      Message msg = new Message();
      msg.what = HandlerConstants.HANDLER_STOP_SERVICE;
      msg.arg1 = serviceId;
      mHandler.dispatchMessage(msg);
    }
  }
  
  protected boolean showAdTime(){
    long curtime = System.currentTimeMillis();
    if(curtime - lastTime > 1*60*1000){
      lastTime = curtime;
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
              mShow = false;
              return false;
            }
          }
        }
      } catch (Exception e) {
        Logger.p(e);
      }
    }else{
      Logger.e("HandleService", "mshow true");
      return mShow;
    }
    mShow = true;
    Logger.e("HandleService", "showAdTime true");
    return true;
  }
  
  protected void startNextService(String key , int serviceId){
    try {
      Logger.e("HandleService", ""+key);
      String ruleShow = spf.getString(key, "");
      long firstInitTime = spf.getLong(CommConstants.FIRST_INIT_TIME, System.currentTimeMillis());
      if(!TextUtils.isEmpty(ruleShow)){
        String[] rules = ruleShow.split(";");
        for (int i = 0; i < rules.length; i++) {
          String[] rule = rules[i].split(",");
          int timePoint = Integer.parseInt(rule[0])*60*1000;
          int interval = Integer.parseInt(rule[1])*1000;
          Logger.e("HandleService", "interval "+interval+"  timePoint "+timePoint);
          if(i == rules.length - 1 || System.currentTimeMillis() - firstInitTime <= timePoint){
            Logger.e("HandleService", "1111 "+interval+"  timePoint "+timePoint);
            TimerManager.getInstance(mContext).startTimerByTime(System.currentTimeMillis()+interval, serviceId); 
            spf.edit().putLong(key+"startTime", System.currentTimeMillis()+interval).commit();
            break;
          }
        }
      }
    } catch (Exception e) {
      
    }
  }
  
  protected List<String> getHandleAdInfos(int position,String limitStr,String oneTimeNum,int promType){
    List<String> ads = new ArrayList<String>();
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
    
    ArrayList<AdDbInfo> list = PromDBU.getInstance(mContext).queryAdInfo(promType);
//    Log.e("HandleService", limitStr+"   "+oneTimeNum+"    list.size()= "+list.size()+"  oneNum = "+oneNum+"       limit = "+limit+"    "+list);
    for (AdDbInfo adDbInfo : list) {
      if(maxShowtimes < adDbInfo.getShowTimes()){
        maxShowtimes = adDbInfo.getShowTimes();
      }
      hasShowTimes = hasShowTimes+adDbInfo.getHasShowTimes();
    }
    if(position != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT&& limit != -1 &&(hasShowTimes >= limit || oneNum <= 0)){
      return ads;
    }
    int count = 0;
//    Log.e("HandleService", "  hasShowTimes = "+hasShowTimes+"   maxShowtimes = "+maxShowtimes+"   "+limitStr+"   "+oneTimeNum+"    list.size()= "+list.size()+"  oneNum = "+oneNum+"       limit = "+limit+"    "+list);
    for (int i = 1; i < maxShowtimes+1; i++) {
      for (AdDbInfo adDbInfo : list) {
        if(adDbInfo.getShowTimes() < i && count < oneNum){
          count++;
          ads.add(adDbInfo.getAdId());
        }
        if(count >= oneNum){
          break;
        }
      }
      if(count >= oneNum){
        break;
      }
    }
    return ads;
  }
  
  
  protected List<AdDbInfo> getHandleAdInfoList(int position,String limitStr,String oneTimeNum,int adCount,int promType){
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
    if(adCount != -1){
      oneNum = adCount;
    }
    ArrayList<AdDbInfo> list = PromDBU.getInstance(mContext).queryAdInfo(promType);
    if(list.size() > 0){
      Logger.d("HandleService", oneTimeNum+" 11  =  "+list.toString());
    }else{
      Logger.d("HandleService", oneTimeNum+" 11  =  null");
    }
    for (AdDbInfo adDbInfo : list) {
      if(maxShowtimes < adDbInfo.getShowTimes()){
        maxShowtimes = adDbInfo.getShowTimes();
      }
      hasShowTimes = hasShowTimes+adDbInfo.getHasShowTimes();
    }
    if(position != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT&& limit != -1 &&(hasShowTimes >= limit || oneNum <= 0)){
      return adlist;
    }
    
    boolean connect = PromUtils.netIsConnected(mContext);
    if(connect){
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
        if( list.get(i).getHasShowTimes() < list.get(i).getShowTimes() && (list.get(i).getInstalled() == PromDBU.NO_INSTALL || (!TextUtils.isEmpty(list.get(i).getReserved1()) && list.get(i).getReserved1().equals("1"))) && count < oneNum){
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
        if( list.get(i).getHasShowTimes() < list.get(i).getShowTimes()  && (list.get(i).getInstalled() == PromDBU.NO_INSTALL || (!TextUtils.isEmpty(list.get(i).getReserved1()) && list.get(i).getReserved1().equals("1"))) && count < oneNum){
          if(!adlist.contains(list.get(i))){
            count++;
            adlist.add(list.get(i));
            lastPostion = i;
          }
        }
      }
      if(promType != PromDBU.PROM_DESKTOPAD){
        if(lastPostion != -1){
          PromDBU.getInstance(mContext).resetShowmark(promType);
          PromDBU.getInstance(mContext).updateAdInfoShowmark(list.get(lastPostion), promType);
        }
      }
    }
    if(adlist.size() > 0){
      Logger.d("HandleService", oneTimeNum+"  =  "+adlist.toString());
    }else{
      Logger.d("HandleService", oneTimeNum+"  =  null");
    }
    return adlist;
  }
  
  protected List<AdDbInfo> getHandleAdInfoListForWakeUp(int position,String limitStr,String oneTimeNum,int promType){
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
    ArrayList<AdDbInfo> list = PromDBU.getInstance(mContext).queryAdInfo(promType);
    if(list.size() > 0){
      Logger.d("HandleService", oneTimeNum+" 11  =  "+list.toString());
    }else{
      Logger.d("HandleService", oneTimeNum+" 11  =  null");
    }
    for (AdDbInfo adDbInfo : list) {
      if(maxShowtimes < adDbInfo.getShowTimes()){
        maxShowtimes = adDbInfo.getShowTimes();
      }
      hasShowTimes = hasShowTimes+adDbInfo.getHasShowTimes();
    }
    if(position != StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT&& limit != -1 &&(hasShowTimes >= limit || oneNum <= 0)){
      return adlist;
    }
    
    boolean connect = PromUtils.netIsConnected(mContext);
    if(connect){
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
        String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(list.get(i).getPackageName(),list.get(i).getVersionCode());
        File f = new File(apkPath);
        if( list.get(i).getHasShowTimes() < list.get(i).getShowTimes() && list.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum && (list.get(i).getPreDown()== 1 || f.exists())){
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
        String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(list.get(i).getPackageName(),list.get(i).getVersionCode());
        File f = new File(apkPath);
        if( list.get(i).getHasShowTimes() < list.get(i).getShowTimes()  && list.get(i).getInstalled() == PromDBU.NO_INSTALL && count < oneNum && (list.get(i).getPreDown()== 1 || f.exists())){
          if(!adlist.contains(list.get(i))){
            count++;
            adlist.add(list.get(i));
            lastPostion = i;
          }
        }
      }
      if(promType != PromDBU.PROM_DESKTOPAD){
        if(lastPostion != -1){
          PromDBU.getInstance(mContext).resetShowmark(promType);
          PromDBU.getInstance(mContext).updateAdInfoShowmark(list.get(lastPostion), promType);
        }
      }
    }
    if(adlist.size() > 0){
      Logger.d("HandleService", oneTimeNum+"  =  "+adlist.toString());
    }else{
      Logger.d("HandleService", oneTimeNum+"  =  null");
    }
    return adlist;
  }
  
  public AdDbInfo getValue(List<AdDbInfo> mArrayList){  
    Random mRandom = new Random();  
    int a = -1;
    AdDbInfo value = null;
    if(mArrayList.size() > 0){
      a = mRandom.nextInt(mArrayList.size());  
      value = mArrayList.get(a);  
      System.out.println(value + "");  
      mArrayList.remove(a);  
    }
    return value;
  }  
  
  public static boolean isScreenOn(Context context){
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
    boolean screen = pm.isScreenOn();
    return screen;
  }
  
  public static boolean pSwitch(SharedPreferences sp ,String type){
    
    if ((sp.getInt(CommConstants.CONFIG_BATTERY_CHARGE, 1) == 1 || (sp.getInt(CommConstants.CONFIG_BATTERY_CHARGE, 1) == 0 && !PromUtils.getInstance(mContext)
        .isCharge())) && sp.getInt(CommConstants.CONFIG_COMMON_SWITCH, 1) == 1 && sp.getInt(type + "_switch", 1) == 1) {
      return true;
    }
    return false;
  }
  
  /**
   * 启动服务接口
   * 
   * @param intent
   * 
   * @param intent
   * @param flags
   * @param startId
   *          service的Id号
   * @return
   */
  public abstract void onStartCommand(Intent intent, int flags, int startId);
  public abstract void handledAds(List<String> ads);
//  public abstract void onServerAddressReponse(Session session);
//  protected void onServerAddressReponseError() {
//    Logger.e(NetworkConstants.TAG, "onServerAddressReponseError");
//  };
}
