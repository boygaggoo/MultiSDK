package com.mf.promotion.service.req;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.mf.basecode.network.connection.HTTPConnection;
import com.mf.basecode.network.protocol.GetCommonConfigReq;
import com.mf.basecode.network.protocol.GetCommonConfigResp;
import com.mf.basecode.network.serializer.AttributeUitl;
import com.mf.basecode.network.serializer.MFCom_Message;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;
import com.mf.basecode.utils.TimeFormater;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.PromConstants;
import com.mf.promotion.util.PromReqManager;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.TimerManager;
import com.mf.utils.AppInstallUtils;


public class ReqCommConfigToService{
  public static final String  TAG                  = "ReqConfig";
  
  protected Context                mContext;
  private SharedPreferences spf ;
  private byte isMajor = 2;

  public ReqCommConfigToService(Context c) {
    this.mContext = c;
    spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
  }

  private void onCommConfigResp(GetCommonConfigResp resp) {
    long millis = 0;
    long time = System.currentTimeMillis();
    if (resp != null) {
      millis = resp.getReqRelativeTime()*60*1000 + time;
      SharedPreferences.Editor editor = spf.edit();
      long first = spf.getLong(CommConstants.FIRST_INIT_TIME, 0);
      if(first <= 0){
        editor.putLong(CommConstants.FIRST_INIT_TIME, System.currentTimeMillis());
        editor.putLong(CommConstants.CONFIG_ACTIVE_POINT_TIME,resp.getActiveTimes()*1000+System.currentTimeMillis());
      }
      editor.putLong(CommConstants.LAST_COMMCONFIG_TIME, System.currentTimeMillis());
      if(!TextUtils.isEmpty(resp.getMagicData())){
//        editor.putString(CommConstants.CONFIG_MAGIC_DATA, resp.getMagicData());
        PromUtils.getInstance(mContext).saveMagicData(resp.getMagicData());
      }
      
      editor.putInt(CommConstants.CONFIG_REQ_RELATIVE_TIME,resp.getReqRelativeTime()*60*1000);
      editor.putInt(CommConstants.CONFIG_ACTIVE_TIMES,resp.getActiveTimes()*1000);
      editor.putInt(CommConstants.CONFIG_PRE_DOWNLOAD,resp.getPreDownload());
      
      editor.putInt(CommConstants.CONFIG_WAKEUP_SHOW_LIMIT,resp.getWakeupShowLimit());
      editor.putInt(CommConstants.CONFIG_RICE_MEDIA_SHOW_LIMIT,resp.getRichmediaShowLimit());
      editor.putInt(CommConstants.CONFIG_PUSH_SHOW_LIMIT,resp.getPushShowLimit());
      editor.putInt(CommConstants.CONFIG_WAKEUP_SHOW_ONE_TIMENUM,resp.getWakeupShowOneTimeNum());
      editor.putInt(CommConstants.CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM,resp.getRichmediaShowOneTimeNum());
      editor.putInt(CommConstants.CONFIG_QUICK_ICON_SHOW_ONE_TIMENUM,resp.getQuickIconShowOneTimeNum());
      editor.putInt(CommConstants.CONFIG_PUSH_SHOW_ONE_TIMENUM,resp.getPushShowOneTimeNum());
      
      editor.putLong(CommConstants.LAST_COMMCONFIG_TIME, System.currentTimeMillis());
      
      editor.putInt(CommConstants.CONFIG_BATTERY_CHARGE, resp.getBatteryCharge());
      
      editor.putInt(CommConstants.BROWER_SHOW_LIMIT_NUMS, resp.getBrowserShowLimitNum());
      editor.putLong(CommConstants.BROWER_SHOW_LIMIT_TIME, resp.getBrowserIntervalSecs());
      
      String pertime = resp.getPeriodTime();
      if(!TextUtils.isEmpty(pertime)){
        String[] times = pertime.split("-");
        try {
          if(times.length >= 2){
            editor.putInt(CommConstants.CONFIG_SHOW_START_TIME,Integer.parseInt(times[0]));
            editor.putInt(CommConstants.CONFIG_SHOW_END_TIME,Integer.parseInt(times[1]));
          }
        } catch (Exception ignored) {
        }
      }
      editor.putString(CommConstants.CONFIG_APP_PERIODS, resp.getPeriods());
      
      if (resp.getAdSwitch() == 1 && isMajor == 1) {// 开关为打开状态
        Logger.e(TAG, "switch is open.");
        sendAllPromReqs(resp.getReqConfig());
        editor.putInt(CommConstants.CONFIG_COMMON_SWITCH, 1);
      } else {
        clearSDKHost();
        editor.putInt(CommConstants.CONFIG_COMMON_SWITCH, 0);
        Logger.e(TAG, "switch is closed.");
      }
      editor.commit();
    } else {
      Logger.e(TAG, "GetCommonConfigResp is null and restart the service after " + PromConstants.PROM_REQ_FAILED_LOOP_REQ_INTERVAL + " min");
    }
    if (millis <= 0) {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(time);
      c.add(Calendar.MINUTE, PromConstants.PROM_REQ_FAILED_LOOP_REQ_INTERVAL);
      millis = c.getTimeInMillis();
    }
    Logger.e(TAG, "next request time =" + TimeFormater.formatTime(millis));
    TimerManager.getInstance(mContext).startTimerByTime(millis, -1); //serviceId = -1 ，表示在MfApkService里面重新启动开关请求
  }
  
  
  private void sendAllPromReqs(String rules){
    SharedPreferences.Editor editor = spf.edit();
    editor.putInt(CommConstants.SHOW_RULE_WAKEUP+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_DESKTOP+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_PUSH+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_SHORTCUT+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_DESKFOLDER+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_MAGIC+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_ENHANCED+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_PLUGIN+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_BROWER+"_switch", 1);
    editor.putInt(CommConstants.SHOW_RULE_START+"_switch", 1);
    if(!TextUtils.isEmpty(rules)){
      char[] bRule = rules.toCharArray();
      if(bRule.length >= 7){
        if(bRule[0] == '1'){
          PromReqManager.getInstance(mContext).ReqWakeupApk();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_WAKEUP+"_switch", 0);
        }
        if(bRule[1] == '1'){
//          PromReqManager.getInstance(mContext).ReqEnhanced();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_ENHANCED+"_switch", 0);
        }
        if(bRule[2] == '1'){
          PromReqManager.getInstance(mContext).ReqDesktopAd();      
        }else{
          editor.putInt(CommConstants.SHOW_RULE_DESKTOP+"_switch", 0);
        }
        if(bRule[3] == '1'){
          PromReqManager.getInstance(mContext).ReqPushNotify();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_PUSH+"_switch", 0);
        }
        if(bRule[4] == '1'){
          PromReqManager.getInstance(mContext).ReqFolderIcon();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_DESKFOLDER+"_switch", 0);
        }
        if(bRule[5] == '1'){
          PromReqManager.getInstance(mContext).ReqShotcut();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_SHORTCUT+"_switch", 0);
        }
        if(bRule[6] == '1'){
//          PromReqManager.getInstance(mContext).ReqMagic();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_MAGIC+"_switch", 0);
        }
        
        if(bRule[7] == '1'){
          PromReqManager.getInstance(mContext).ReqPlugin();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_PLUGIN+"_switch", 0);
        }
        
        if(bRule.length >= 9 && bRule[8] == '1'){
          PromReqManager.getInstance(mContext).ReqExit();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_EXIT+"_switch", 0);
        }
		
		 if(bRule.length >= 10 && bRule[9] == '1'){
        	Logger.error(TAG, " req brower ");
        	PromReqManager.getInstance(mContext).ReqBrower();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_BROWER+"_switch", 0);
        }
        
        if(bRule.length >= 11 && bRule[10] == '1'){
          PromReqManager.getInstance(mContext).ReqStart();
        }else{
          editor.putInt(CommConstants.SHOW_RULE_START+"_switch", 0);
        }
        
        PromReqManager.getInstance(mContext).HandleHandleAdInfo();
        PromReqManager.getInstance(mContext).HandlePreDownloadAdInfo();
        editor.commit();
        return ;
      }
    }
    PromReqManager.getInstance(mContext).ReqWakeupApk();
//    PromReqManager.getInstance(mContext).ReqEnhanced();
    PromReqManager.getInstance(mContext).ReqDesktopAd();
    PromReqManager.getInstance(mContext).ReqPushNotify();
    PromReqManager.getInstance(mContext).ReqFolderIcon();
    PromReqManager.getInstance(mContext).ReqShotcut();
    PromReqManager.getInstance(mContext).ReqMagic();
    PromReqManager.getInstance(mContext).ReqPlugin();
    PromReqManager.getInstance(mContext).ReqExit();
	  PromReqManager.getInstance(mContext).ReqBrower();
    PromReqManager.getInstance(mContext).ReqStart();
    PromReqManager.getInstance(mContext).HandleHandleAdInfo();
    PromReqManager.getInstance(mContext).HandlePreDownloadAdInfo();
    editor.commit();
  }

  public void sendRequest(){
    long lsTime = spf.getLong(CommConstants.LAST_COMMCONFIG_TIME, 0);
    int relativeTime = spf.getInt(CommConstants.CONFIG_REQ_RELATIVE_TIME, 0)-5*1000;
    int realRelativeTime = spf.getInt(CommConstants.CONFIG_REQ_RELATIVE_TIME, 0);
    if(System.currentTimeMillis() - lsTime < relativeTime){
      TimerManager.getInstance(mContext).stopAlermByServiceId(-1);
      TimerManager.getInstance(mContext).startTimerByTime((realRelativeTime + lsTime), -1);
      Logger.e(TAG, "System.currentTimeMillis() - lsTime < relativeTime  and send req after "+(realRelativeTime-(System.currentTimeMillis() - lsTime))/1000);
      return ;
    }
    checkSDKHost();
    GetCommonConfigReq req = new GetCommonConfigReq();
    req.setTerminalInfo(TerminalInfoUtil.getTerminalInfo(mContext));
    String magicData = PromUtils.getInstance(mContext).getMagicData();//spf.getString(CommConstants.CONFIG_MAGIC_DATA, "");
    req.setMagicData(magicData);
    req.setIsMajor(isMajor);
    Logger.e(TAG, req.toString());
    SharedPreferences sf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_SESSION, 0);
    String promAddress = sf.getString(CommConstants.SESSION_PROM_ADD, "");
    GetCommonConfigResp resp = null;
    if(!TextUtils.isEmpty(promAddress)){
      HTTPConnection httpConnection = HTTPConnection.getInstance();
      MFCom_Message respMessage = httpConnection.post(promAddress, req);
      if (respMessage != null && respMessage.head.code == AttributeUitl.getMessageCode(GetCommonConfigResp.class)) {
        resp = (GetCommonConfigResp) respMessage.message;
        Logger.debug(TAG, resp.toString());
      } else {
        Logger.error("GetCommonConfigreq on reponse error");
      }
    }else{
      Logger.error("promAddress is none");
    }
    onCommConfigResp(resp);
  }
  
  private void checkSDKHost(){
    String host = PromUtils.getInstance(mContext).getValueByNameFromSDKInfoFile(CommConstants.SDKS_HOST);
    PackageInfo info = AppInstallUtils.getPgInfoByPackageName(mContext, host);
    if(TextUtils.isEmpty(host) || host.contains(mContext.getPackageName()) || info == null){
      PromUtils.getInstance(mContext).putValueToSDKInfoFile(CommConstants.SDKS_HOST, mContext.getPackageName());
      isMajor = 1;
    }else{
      isMajor = 2;
      String sks = PromUtils.getInstance(mContext).getValueByNameFromSDKInfoFile(CommConstants.SDKS_INFO_KEY);
      if(!TextUtils.isEmpty(sks)){
        String[] sdks = sks.split("&");
        for (int i = 0; i < sdks.length; i++) {
          if(!TextUtils.isEmpty(sdks[i]) && !sdks[i].equals(mContext.getPackageName())){
            sendSelfBroadcast(sdks[i]);
          }
        }
      }
      
    }
  }
  
  private void clearSDKHost(){
    if(isMajor == 1){
      Logger.e(TAG, "clearSDKHost");
      PromUtils.getInstance(mContext).putValueToSDKInfoFile(CommConstants.SDKS_HOST, "");
    }
  }
  
  private void sendSelfBroadcast(String packageName){
    Intent intent = new Intent();  
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    // 3.1以后的版本直接设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES的value：32
    if (android.os.Build.VERSION.SDK_INT >= 12) {
        intent.setFlags(32);
    }
    intent.setPackage(packageName);
    intent.setAction("com.mf.st.R");
    mContext.sendBroadcast(intent);
  }
}
