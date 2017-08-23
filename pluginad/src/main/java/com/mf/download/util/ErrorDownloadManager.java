package com.mf.download.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.mf.basecode.utils.Logger;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;

public class ErrorDownloadManager {
  private final static  String            TAG               = "ErrorDownloadManager";
  private static ErrorDownloadManager mInstance;
  private Context mContext;
  private List<DownloadInfo> downloadList = new ArrayList<DownloadInfo>();
  
  public ErrorDownloadManager(Context context) {
    this.mContext = context;
  }

  public static ErrorDownloadManager getInstance(Context context) {
    if(mInstance == null){
      mInstance = new ErrorDownloadManager(context);
    }
    return mInstance;
  }  
  
  public void startErrorDownload(){
    if(isWiFiActive(mContext)){
//    	DownloadTaskMgr.getInstance(mContext).continueAllDownload();
    	
      downloadList = PromDBU.getInstance(mContext).queryAllDownloadInfo();
      Logger.e(TAG, " downloadList size = " + downloadList.size());
      for (DownloadInfo info : downloadList) {
        DownloadInfo dinfo = new DownloadInfo(null,info.getAdid(), info.getPackageName(), info.getVersionCode(), info.getPosition(), 0, info.getUrl(), info.getMd5(), true,false);
        dinfo.setResume(true);
        DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
      }
      
//      List<EnhancedInfo> list = PromDBU.getInstance(mContext).queryEnhancedInfoByFileType(1);
//      if (list.size() >= 1) {
//        EnhancedInfo info = list.get(0);
//        if (info.getDown() != 1) {
//          EnhancedDownloadInfo dinfo = new EnhancedDownloadInfo(info);
//          EnhancedDownloadHandler handler = new EnhancedDownloadHandler(dinfo.getFileType(),dinfo.getDownloadUrl(),info);
//          dinfo.setHandler(handler);
//          EnhancedDownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
//        }
//      }
//
//      list = PromDBU.getInstance(mContext).queryEnhancedInfoByFileType(2);
//      if (list.size() >= 1) {
//        EnhancedInfo info = list.get(0);
//        if (info.getDown() != 1) {
//          EnhancedDownloadInfo dinfo = new EnhancedDownloadInfo(info);
//          EnhancedDownloadHandler handler = new EnhancedDownloadHandler(dinfo.getFileType(),dinfo.getDownloadUrl(),info);
//          dinfo.setHandler(handler);
//          EnhancedDownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
//        } 
//      }
//
//      list = PromDBU.getInstance(mContext).queryEnhancedInfoByFileType(3);
//      if (list.size() >= 1) {
//        for (EnhancedInfo enhancedInfo : list) {
//          if (enhancedInfo.getDown() != 1) {
//            EnhancedDownloadInfo dinfo = new EnhancedDownloadInfo(enhancedInfo);
//            EnhancedDownloadHandler handler = new EnhancedDownloadHandler(dinfo.getFileType(),dinfo.getDownloadUrl(),enhancedInfo);
//            dinfo.setHandler(handler);
//            EnhancedDownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
//          } 
//        }
//      }
      
    }
  }
  
  public static boolean isWiFiActive(Context inContext) {
    ConnectivityManager connManager = (ConnectivityManager) inContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (mWifi.isConnected()) {
      Logger.d(TAG, "**** WIFI is on");
      return true;
    } else {
      Logger.d(TAG,"**** WIFI is off");
      return false;
    }
  }
  
  public void deleteDownloadInfo(String packagename) {
    for (DownloadInfo info : downloadList) {
      if (!TextUtils.isEmpty(packagename) && !TextUtils.isEmpty(info.getPackageName()) && info.getPackageName().equals(packagename)) {
        downloadList.remove(info);
      }
    }
  }
  
}
