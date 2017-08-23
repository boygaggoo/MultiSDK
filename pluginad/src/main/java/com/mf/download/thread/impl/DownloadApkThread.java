package com.mf.download.thread.impl;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.utils.Logger;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.thread.AbstractDownloadThread;
import com.mf.download.util.DownloadConstants;
import com.mf.download.util.DownloadUtils;
import com.mf.download.util.ErrorDownloadManager;
import com.mf.statistics.prom.util.StatsPromUtils;

public class DownloadApkThread extends AbstractDownloadThread {
  public static final String TAG = "DownloadApkThread";
  
  private boolean            mPre;
  
  private String             mAdid;
  
  private int                mSource;

  private int                mPosition;

  private String             mPackageName;

  private int                mVersionCode;

  private boolean            isDownloadNext;
  
  private boolean            mResume = false;

  public DownloadApkThread(Context context, DownloadInfo downloadInfo) {
    mContext = context;
    mHandler = downloadInfo.getHandler();
    mAdid = downloadInfo.getAdid();
    mPackageName = downloadInfo.getPackageName();
    MD5 = downloadInfo.getMd5();
    mURL = downloadInfo.getUrl();
    mSource = downloadInfo.getSource();
    mPosition = downloadInfo.getPosition();
    mVersionCode = downloadInfo.getVersionCode();
    isDownloadNext = downloadInfo.isDownloadNext();
    Logger.e(TAG, "mURL:" + mURL);
    mSavePath = DownloadUtils.getInstance(mContext).getApkDownloadPath(downloadInfo.getPackageName());
    tmpFilePath = mSavePath + File.separator + mPackageName + "_r" + mVersionCode + ".tmp";
    downloadFilePath = mSavePath + File.separator + mPackageName + "_r" + mVersionCode + ".app";
    mResume = downloadInfo.isResume();
    mPre = downloadInfo.isPre();
  }

  @Override
  public void run() {
    // 准备文件
    try {
      File downloadPath = new File(mSavePath);
      if (!downloadPath.exists()) {
        downloadPath.mkdirs();
      }
      File downloadFile = new File(tmpFilePath);
      if (!downloadFile.exists()) {
        downloadFile.createNewFile();
      }
      FileInputStream fis = new FileInputStream(downloadFile);
      offset = fis.available();
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      connect();
    } catch (Exception e) {
      e.printStackTrace();
    }
    isDownloading = false;
    // 插入朋越统计

    Logger.e(TAG, "mPosition =" + mPosition);
//    if(mPre){
//      StatsPromUtils.getInstance(mContext).addPreDownloadSuccessAction(mAdid+"/"+mPackageName, mPosition);
//    }else{
//      StatsPromUtils.getInstance(mContext).addDownloadSuccessAction(mAdid+"/"+mPackageName, mPosition);
//    }
    
    if (isDownloadNext) {
      DownloadUtils.getInstance(mContext).downloadNextApk();
    }
  }

  public void sendStopMsg(int status) {
    Logger.e(TAG, "send status=" + status);
    Message msg = new Message();
    msg.what = status;
    Bundle b = new Bundle();
    b.putString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_ADID,mAdid);
    b.putString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME, mPackageName);
    b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_VERSION_CODE, mVersionCode);
    b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_POSITION, mPosition);
    b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_SOURCE, mSource);
    msg.obj = b;
    if(status == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH){
      PromDBU.getInstance(mContext).deleteDownloadInfoByPackageName(mPackageName);
      ErrorDownloadManager.getInstance(mContext).deleteDownloadInfo(mPackageName);
      DownloadUtils.getInstance(mContext).sendFinishMessage(mAdid,mPackageName,mVersionCode,mPosition,mSource,status);
      if(mPre){
        StatsPromUtils.getInstance(mContext).addPreDownloadSuccessAction(mAdid+"/"+mPackageName, mPosition);
      }else{
        StatsPromUtils.getInstance(mContext).addDownloadSuccessAction(mAdid+"/"+mPackageName, mPosition);
      }
    }
    if(status == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL){
      if(mPre){
        StatsPromUtils.getInstance(mContext).addPreDownloadFailAction(mAdid+"/"+mPackageName, mPosition);
      }else{
        StatsPromUtils.getInstance(mContext).addDownloadFailAction(mAdid+"/"+mPackageName, mPosition);
      }
    }
    MyPackageInfo info = new MyPackageInfo(mPackageName, mVersionCode);
    DownloadUtils.getInstance(mContext).removeDownloadApkThread(info);
//    DownloadUtils.getInstance(mContext).removeSelfUpdateThread(info);
//    Message msg = new Message();
//    msg.what = status;
//    Bundle b = new Bundle();
//    b.putString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME, mPackageName);
//    b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_VERSION_CODE, mVersionCode);
//    b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_POSITION, mPosition);
//    b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_SOURCE, mSource);
//    msg.obj = b;
    if(mHandler != null){
      mHandler.sendMessage(msg);
    }
  }

  @Override
  public void sendProgressMsg() {
    Message msg = new Message();
    msg.what = DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOADING;
    Bundle b = new Bundle();
    b.putString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME, mPackageName);
    b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_VERSION_CODE, mVersionCode);
    b.putFloat(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PROGRESS, disProgress);
    msg.obj = b;
    if(mHandler != null){
      mHandler.sendMessage(msg);
    }
  }
}
