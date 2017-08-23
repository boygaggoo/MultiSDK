package com.mf.promotion.service.impl;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.AppInstallUtils;

public class PromHandleAppService extends HandleService {

  public static final String TAG = "PromShowDownloadNotifyService";
  private AdDbInfo           appInfo;
  private int                position;
  private int                source;
  private String             adid;
  private int                promType;
  public PromHandleAppService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
    mContext = c;
  }

  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.e(TAG, "onStartCommand");
    if (intent != null) {
      adid = intent.getStringExtra(BundleConstants.BUNDLE_AD_INFO_ADID);
      promType = intent.getIntExtra(BundleConstants.BUNDLE_AD_INFO_PROM_TYPE, -1);
      if(TextUtils.isEmpty(adid) || promType == -1){
        return;
      }
      appInfo = PromDBU.getInstance(mContext).getAdInfobyAdid(adid, promType);
      position = intent.getIntExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, 0);
      source = intent.getIntExtra(BundleConstants.BUNDLE_APP_INFO_SOURCE, 0);
      // 如果是推送方式过来相当于点击推送的效果，记录点击推送条数

      try {
        StatsPromUtils.getInstance(mContext).addClickAction(appInfo.getAdId()+"/"+appInfo.getPackageName(), position);
      } catch (Exception e) {
        Logger.p(e);
      }

    }
    Logger.e(TAG, "appInfo = " + (appInfo == null ? "null" : appInfo.toString()) + "-----position=" + position);
    if (appInfo != null) {
      PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(mContext, appInfo.getPackageName());
      String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(appInfo.getPackageName(), appInfo.getVersionCode());
      if (pInfo != null) {// 应用已安装，启动
        if (!PromUtils.getInstance(mContext).hasInstalled(new MyPackageInfo(appInfo.getPackageName(), appInfo.getVersionCode()))) {
          // 若版本高于当前已安装版本，启动更新
          File f = new File(apkPath);
          if (f.exists()) {// 安装包已下载，打开安装界面安装
            MyPackageInfo packageInfo = new MyPackageInfo(appInfo.getAdId(),appInfo.getPackageName(), appInfo.getVersionCode(), position, source, true);
            AppInstallUtils.installApp(mContext, apkPath, packageInfo);
          }
        } else {
          intent = mContext.getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          mContext.startActivity(intent);
        }
      } else {
        File f = new File(apkPath);
        MyPackageInfo packageInfo = new MyPackageInfo(appInfo.getAdId(),appInfo.getPackageName(), appInfo.getVersionCode(), position, source, true);
        if (f.exists()) {// 安装包已下载，打开安装界面安装
          AppInstallUtils.installApp(mContext, apkPath, packageInfo);
        } else {// 下载应用
          int notifyId = DownloadUtils.getInstance(mContext).generateDownladNotifyId();
          Logger.e(TAG, "notifyId = " + notifyId);
          
          
          DownloadInfo dinfo = new DownloadInfo(null,appInfo.getAdId(),
              appInfo.getPackageName(), appInfo.getVersionCode(), position, 0, appInfo.getAdDownUrl(), appInfo.getFileMd5(), true,true);
          if(DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, packageInfo)){
            DownloadUtils.getInstance(mContext).addExtraHandler(new DownloadUtils.MyNotifyDownloadHandler(apkPath,appInfo.getAdId(), appInfo.getPackageName(),  appInfo.getVersionCode(), position, 0));
          }else{
            DownloadUtils.getInstance(mContext).addDownloadApkThread(
                new DownloadInfo(new DownloadUtils.MyNotifyDownloadHandler(apkPath,appInfo.getAdId(), appInfo.getPackageName(),  appInfo.getVersionCode(), position, 0),
                    appInfo.getAdId(),appInfo.getPackageName(), appInfo.getVersionCode(), position, source, appInfo.getAdDownUrl(), appInfo.getFileMd5(), true,false));
          }
        }
      }
    }
  }

  @Override
  public void handledAds(List<String> ads) {
    
  }

}
