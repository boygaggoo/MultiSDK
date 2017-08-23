package com.mf.promotion.service.impl;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.network.util.NetworkUtils;
import com.mf.basecode.service.HandleService;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadUtils;
import com.mf.model.AdDbInfo;
import com.mf.promotion.service.MFApkServiceFactory;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.ShortcutUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.utils.AppInstallUtils;
import com.mf.utils.BitmapUtils;
//import android.util.Log;

public class HandleShortcutService extends HandleService{
  private List<AdDbInfo> list;
  public static final String TAG = "PromSCService";
  private AdDbInfo info;
//  private Handler downloadHandler = new Handler() {
//    @Override
//    public void handleMessage(Message msg) {
//      handleShortcutMessage(msg);
//    }
//  };

  public HandleShortcutService(int serviceId, Context c, Handler handler) {
    super(serviceId, c, handler);
    Logger.debug(TAG, "PromSCService created");
  }
  @Override
  public void onStartCommand(Intent intent, int flags, int startId) {
    Logger.e(TAG, "onStartCommand ");
    boolean connect = PromUtils.netIsConnected(mContext);
    if(!connect){
      Logger.e(TAG, "connect");
      return ;
    }
    if(!HandleService.pSwitch(spf, CommConstants.SHOW_RULE_SHORTCUT)){
      return ;
    }
    startNextService(CommConstants.SHOW_RULE_SHORTCUT,MFApkServiceFactory.HANDLE_SHORTCUT_SERVICE.getServiceId());
    if(!PromUtils.getInstance(mContext).checkHost()){
      Logger.e(TAG, "checkHost false");
      return;
    }
    list = getHandleAdInfoList(StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT,"", CommConstants.CONFIG_QUICK_ICON_SHOW_ONE_TIMENUM,-1, PromDBU.PROM_SHORTCUT);
    if(list == null || list.size() <= 0){
      return ;
    }
    showShortcut();
  }
  
  private void showShortcut() {
    new Thread(new Runnable() {
      
      @Override
      public void run() {
        Logger.e(TAG,"showScut");
        try {
          Iterator<AdDbInfo> iter = list.iterator();  
          while(iter.hasNext()){
            AdDbInfo info = iter.next();
            if(info.getAdType() == 1){
            if(!PromUtils.getInstance(mContext).isShortcutExists(info)){   
              PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(mContext, info.getPackageName());
              String apkPath = DownloadUtils.getInstance(mContext).getApkDownloadFilePath(info.getPackageName(), info.getVersionCode());
              if (pInfo != null) {
                if (!PromUtils.getInstance(mContext).hasInstalled(new MyPackageInfo(info.getPackageName(), info.getVersionCode()))) {
                  File f = new File(apkPath);
                  if (f.exists() && showAdTime()) {
                    if(ShortcutUtils.createShortcut(mContext, info, info.getAdName(), getShortcut(info), false,
                        StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT)){
                      PromUtils.getInstance(mContext).saveShortcutInfo(info);
                    }
                  }
                } 
              } else {
                File f = new File(apkPath);
                if (f.exists() && showAdTime()) {
                  if(ShortcutUtils.createShortcut(mContext, info, info.getAdName(), getShortcut(info), false,
                      StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT)){
                    PromUtils.getInstance(mContext).saveShortcutInfo(info);
                  }
                } else {
                  if ((info.getAdType() == 1 && info.getPreDown() == 1 && NetworkUtils.getNetworkType(mContext) == NetworkConstants.NERWORK_TYPE_WIFI) || info.getPreDown()== 2){
                    DownloadInfo dinfo = new DownloadInfo(null,info.getAdId(),
                        info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT, 0, info.getAdDownUrl(), info.getFileMd5(), true,true);
                    MyPackageInfo packageInfo = new MyPackageInfo(info.getAdId(),info.getPackageName(), info.getVersionCode(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT, 0, true);
                    if(DownloadUtils.getInstance(mContext).isDownloadOrWait(dinfo, packageInfo)){
                      Logger.e(TAG, "down11111 ");
//                      DownloadUtils.getInstance(mContext).addExtraHandler(downloadHandler);
                    }else if(info.getAdType() == 1){
                      Logger.e(TAG, "down22222 ");
                      DownloadUtils.getInstance(mContext).addDownloadApkThread(dinfo);
                    }
                  }
                  if(showAdTime() && ShortcutUtils.createShortcut(mContext, info, info.getAdName(), getShortcut(info), false,
                      StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT)){
                    PromUtils.getInstance(mContext).saveShortcutInfo(info);
                  }
                }
              }
            }
          }else if(info.getAdType() == 2){
            
            if(!PromUtils.getInstance(mContext).isShortcutExists(info)){
                if (!PromUtils.getInstance(mContext).hasInstalled(new MyPackageInfo(info.getPackageName(), info.getVersionCode()))) {
                  if ( showAdTime()) {
//                    if(TextUtils.isEmpty(info.getReserved2()) || info.getReserved2().equals("0")){
                      if(ShortcutUtils.createWebViewShortcut(mContext, info, info.getAdName(), getShortcut(info), false,
                          StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT)){
                        PromUtils.getInstance(mContext).saveShortcutInfo(info);
                      }
//                    }else if(!TextUtils.isEmpty(info.getReserved2())&& info.getReserved2().equals("1")){
//                      if(ShortcutUtils.createBrowserShortcut(mContext, info, info.getAdName(), getShortcut(info), false,
//                          StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT)){
//                        PromUtils.getInstance(mContext).saveShortcutInfo(info);
//                      }
//                    }
                  }
                } 
            }
            
          }
          }
        } catch (Exception e) {
          Logger.p(e);
        }
      }
    }).start();
   }
  
  private Bitmap getShortcut(AdDbInfo info) {
    Bitmap bitmap = BitmapUtils.getBitmapByUrl(info.getAdPicUrl());
    DisplayMetrics     metrics         = new DisplayMetrics();
    metrics = mContext.getResources().getDisplayMetrics();
    float density = metrics.density;
    Logger.d(TAG, "bitmap is " + (bitmap == null ? "null" : "bitmap getDensity:" + bitmap.getDensity()));
    if (density == 1) {
      bitmap = BitmapUtils.zoomBitmap(bitmap, 48, 48);
    } else if (density > 1) {
      bitmap = BitmapUtils.zoomBitmap(bitmap, (int) (density * 48), (int) (density * 48));
    } else {
      bitmap = BitmapUtils.zoomBitmap(bitmap, 32, 32);
    }
    Logger.d(TAG, "bitmap is " + (bitmap == null ? "null" : "not null"));
    return bitmap;
  }
  
  @Override
  public void handledAds(List<String> ads) {
    
  }
  
  public void handleShortcutMessage(android.os.Message msg) {
//    Bundle b = (Bundle) msg.obj;
//    final String packageName = b.getString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME);
//    if (!TextUtils.isEmpty(packageName)) {
//      if (msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH || 
//          msg.what == DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL) {
//        final Iterator<AdDbInfo> iter = list.iterator();  
//        while(iter.hasNext()){  
//          info = iter.next();  
//          if(info.getPackageName().equals(packageName)){
//            Log.e(TAG, "down 55555");
//            new Thread(new Runnable() {
//              
//              @Override
//              public void run() {
//                ShortcutUtils.createShortcut(mContext, info, info.getAdName(), getShortcut(info), false,
//                    StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT);
//                iter.remove();   
//              }
//            }).start();    
//          }
//        }    
//      }
//    }
  }
   
}
