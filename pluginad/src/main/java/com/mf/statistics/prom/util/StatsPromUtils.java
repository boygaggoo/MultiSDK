package com.mf.statistics.prom.util;

import android.content.Context;

import com.mf.basecode.network.object.AdLogInfo;
import com.mf.basecode.utils.Logger;

/**
 * @ClassName: PromStatsUtil
 * @Description:
 * @author Abner.Zhou
 * @date 2012-7-18 上午10:43:29
 * 
 */
public class StatsPromUtils {
  public static final String    TAG       = "PromStatsUtil";

  private static StatsPromUtils mInstance = null;

  private static Context        mContext  = null;

  /**
   * @Title: PromUtils
   * @Description:
   * @param
   * @return void
   * @throws
   */
  private StatsPromUtils() {
  }
  /**
   * @Title: getInstance
   * @Description:
   * @param @param context
   * @param @return
   * @return PromStatsUtil
   * @throws
   */
  public static StatsPromUtils getInstance(Context context) {
    mContext = context;
    if (mInstance == null) {
      mInstance = new StatsPromUtils();
    }
    return mInstance;
  }
  // 展示，安装，点击等统计
  public synchronized void sendAdLogInfoReq(AdLogInfo info) {
    Logger.debug(TAG, "sendAdLogInfoReq");
    StatsReqManager.getInstance(mContext).sendAdRecord(info);
  }

//  // 下载统计
//  public synchronized void sendDownloadResult(MyDownloadResult result) {
//    StatsReqManager.getInstance(mContext).sendDownloadRecord(result);
//  }

  public void addInstallAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1,StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_INSTALL);
  }
  
  public void addLaunchAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_LANUCH);
  }
  
  public void addDisplayAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
  }
  
  public void addClickAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1,  StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_CLICK_AD);
  }
  
  public void addDownloadSuccessAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DOWNLOAD_SUCCESS);
  }
  public void addInstallSuccessAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1,StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_INSTALL_SUCCESS);
  }
  
  public void addUninstallAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1,  StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_UNINSTALL);
  }
  
  public void addUninstallSuccessAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1,  StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_UNINSTALL_SUCCESS);
  }
  
  public void addConfirmAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_CONFIRM);
  }
  
  public void addCancelAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1,StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_CANCEL);
  }
  
  
  public void addPreDownloadSuccessAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_PREDOWNLOAD);
  }
  
  public void addDownloadFailAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DOWNLOAD_FAIL);
  }
  
  public void addPreDownloadFailAction(String adTag,int source1) {
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_PREDOWNLOAD_FAIL);
  }

  public void addMagicAction(String adTag,int source1){
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_REQ);
  }
  
  public void addBrowerShow(String adTag,int resource){
	  addAdLogInfoToDB(adTag, resource, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
  }
  public void addEnchanedSuccessAction(String adTag,int source1){
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_ENHANCED_SUCCESS);
  }
  public void addEnchanedFailAction(String adTag,int source1){
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_ENHANCED_FAIL);
  }
  public void addLoadSuccessAction(String adTag,int source1){
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_LOAD_SUCCESS);
  }
  public void addLoadFailAction(String adTag,int source1){
    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_LOAD_FAIL);
  }

  
//  
//  
//  public void addAppUninstallAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1,  StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_UNINSTALL);
//  }
//
//  public void addAppUninstallSuccessAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1,  StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_UNINSTALL_SUCCESS);
//  }
//
//  public void addPromWapDisplayAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
//  }
//
//  public void addPromHomeDisplayAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1,StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
//  }
//
//  public void addPromDetailDisplayAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
//  }
//
//  public void addNotifyDisplayAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
//  }
//  public void addSilentNotifyAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
//  }
//  public void addLocalNotifyDisplayAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY);
//  }
//  public void addPlugInfInitAction(String adTag,int source1) {
//    addAdLogInfoToDB(adTag, source1, StatsPromConstants.STATS_PROM_AD_LOG_INFO_ACTION_LANUCH);
//  }

  private void addAdLogInfoToDB(String adTag,int source1, int action) {
    AdLogInfo info = new AdLogInfo();
    info.setAction(action);
    info.setAdTag(adTag);
    info.setNum(1);
    info.setSource1((short) source1);
    sendAdLogInfoReq(info);
  }
//  private Map<String, String> getStatMap(String packageName, int iconId, int position, int source, int action) {
//    Map<String, String> map = new HashMap<String, String>();
//    map.put(StatsPromConstants.PROM_LOTUSEED_KEY_PACKAGE_NAME, packageName);
//    map.put(StatsPromConstants.PROM_LOTUSEED_KEY_ACTION, "" + action);
//    map.put(StatsPromConstants.PROM_LOTUSEED_KEY_SOURCE1, "" + position);
//    map.put(StatsPromConstants.PROM_LOTUSEED_KEY_SOURCE2, "" + source);
//    map.put(StatsPromConstants.PROM_LOTUSEED_KEY_ICON_ID, "" + iconId);
//    return map;
//  }
}
