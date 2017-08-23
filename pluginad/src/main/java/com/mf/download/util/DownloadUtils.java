package com.mf.download.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.data.PromDBU;
import com.mf.download.model.DownloadInfo;
import com.mf.download.thread.impl.DownloadApkThread;
import com.mf.handler.ExitListHandler;
import com.mf.handler.FloatListHandler;
import com.mf.handler.FolderIconHandler;
import com.mf.utils.AppInstallUtils;
//import android.util.Log;

public class DownloadUtils {
  public static final String                        TAG                     = "DownloadUtils";

  public static final int                           MAX_THREAD_COUNT        = 2;

  private static DownloadUtils                      mInstance               = null;

  private static Context                            mContext                = null;

  private HashMap<Integer, Object>                  downloadNotificationMap = new HashMap<Integer, Object>();

  private LinkedList<DownloadInfo>                  waitDownloadList        = new LinkedList<DownloadInfo>();

  private HashMap<MyPackageInfo, DownloadApkThread> downloadApkThreadMap    = new HashMap<MyPackageInfo, DownloadApkThread>();

//  private HashMap<MyPackageInfo, DownloadApkThread> selfUpdateThreadMap     = new HashMap<MyPackageInfo, DownloadApkThread>();

//  private HashMap<String, DownloadHtml5Thread>      downloadZipThreadMap    = new HashMap<String, DownloadHtml5Thread>();

  private static ArrayList<Handler>                 extraHandlerList        = new ArrayList<Handler>();
  // 显示图片，默认icon
  Bitmap                                            bitmap                  = null;

  private HashMap<MyPackageInfo, FolderIconHandler> folderIconHandlerMap    = new HashMap<MyPackageInfo, FolderIconHandler>();
  private HashMap<MyPackageInfo, FloatListHandler> FloatListHandlerMap    = new HashMap<MyPackageInfo, FloatListHandler>();
  private HashMap<MyPackageInfo, ExitListHandler> ExitListHandlerMap    = new HashMap<MyPackageInfo, ExitListHandler>();

  public static class MyNotifyDownloadHandler extends Handler {

    private String adid;
    
    private String apkPath;

    private String packageName;

    private int    progress;

    private int    version;

    private int    position;

    private int    source;

    public MyNotifyDownloadHandler(String apkPath, String adid,String packageName, int version, int position, int source) {
      this.apkPath = apkPath;
      this.adid = adid;
      this.packageName = packageName;
      this.version = version;
      this.position = position;
      this.source = source;
      Logger.e(TAG, " new MyNotifyDownloadHandler");
    }
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOADING:
        Bundle b = (Bundle) msg.obj;

        if ((int) b.getFloat(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PROGRESS) > progress
            && (int) b.getFloat(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PROGRESS) != 100) {
          progress = (int) b.getFloat(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PROGRESS);
          // DownloadUtils.getInstance(mContext).showDownloadNotify(packageName,
          // version, notificationId, tickerText, appName, null, defIconId,
          // iconId, progress,
          // null, iconUrl, downloadUrl, md5, position, source, clickIntent,
          // false);
        }
        break;
      case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL:
        // DownloadUtils.getInstance(mContext).showDownloadNotify(packageName,
        // version, notificationId, null, appName, null, defIconId, iconId, -1,
        // null, iconUrl,
        // downloadUrl, md5, position, source, null, false);
        break;
      case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH:
        Logger.e(TAG, "sendFinishMessage  222");
        if (AppInstallUtils.isOpenInstallRightNow(position, source)) {
          Logger.e(TAG, "isOpenInstallRightNow");
          // DownloadUtils.getInstance(mContext).showDownloadNotify(packageName,
          // version, notificationId, null, null, null, defIconId, iconId, -2,
          // null, iconUrl,
          // downloadUrl, md5, position, source, null, false);
          Logger.e(TAG, "sendFinishMessage  222 path "+apkPath);
          Logger.e(TAG, "sendFinishMessage  222 adid "+adid+"packageName "+packageName+"version "+version+"position "+position +"source "+source);
          AppInstallUtils.installApp(mContext, apkPath, new MyPackageInfo(adid,packageName, version, position, source));
          DownloadUtils.getInstance(mContext).removeExtraHandler(this);
          // } else {
          // DownloadUtils.getInstance(mContext).showDownloadNotify(packageName,
          // version, notificationId,
          // appName + ResourceIdUtils.getStringByResId(mContext,
          // "R.string.mf_download_finish"), appName,
          // ResourceIdUtils.getStringByResId(mContext,
          // "R.string.mf_download_finish"), defIconId, iconId, 100, apkPath,
          // iconUrl, downloadUrl, md5, position,
          // source, null, true);
        }
        break;
      }
    }
    @Override
    public boolean equals(Object o) {
      if (o instanceof MyNotifyDownloadHandler) {
        MyNotifyDownloadHandler handler = (MyNotifyDownloadHandler) o;
        return handler.getPackageName().equals(packageName) && handler.getVersion() == version;
      }
      return false;
    }
    public String getPackageName() {
      return packageName;
    }
    public void setPackageName(String packageName) {
      this.packageName = packageName;
    }
    public int getVersion() {
      return version;
    }
    public void setVersion(int version) {
      this.version = version;
    }
    
    
    
  }

  /** 计数锁，保证线程安全 */
  private byte[] lock             = new byte[0];
  /** 计数器 */
  private int    downloadNotifyId = 90000;

  /**
   * 生成下载进度通知栏ID号，递增
   * 
   * @return 事件号
   */
  public int generateDownladNotifyId() {
    synchronized (lock) {
      if (downloadNotifyId == Long.MAX_VALUE) {
        downloadNotifyId = 90000;
      } else {
        ++downloadNotifyId;
      }
      return downloadNotifyId;
    }
  }

  public static DownloadUtils getInstance(Context context) {

    mContext = context;
    if (mInstance == null) {
      mInstance = new DownloadUtils();
    }
    return mInstance;
  }

  private DownloadUtils() {
  }

  /**
   * 根据不同信息显示需要的通知栏
   * 
   * @param packageName
   * @param versionCode
   * @param notifictionId
   * @param tickerText
   * @param title
   * @param msg
   * @param defIconId
   * @param iconId
   * @param progress
   * @param apkPath
   * @param iconUrl
   * @param downloadUrl
   * @param md5
   * @param position
   * @param source
   * @param pauseIntent
   * @param hasSound
   */
  // public void showDownloadNotify(String packageName, int versionCode, int
  // notifictionId, String tickerText, String title, String msg, int defIconId,
  // int iconId, int progress, String apkPath, String iconUrl, String
  // downloadUrl, String md5, int position, int source, Intent pauseIntent,
  // boolean hasSound) {
  // if (progress == -2) {
  // NotiUitl.getInstance(mContext).cancelOldNoti(notifictionId);
  // downloadNotificationMap.remove(notifictionId);
  // return;
  // }
  // Object downloadNotification = downloadNotificationMap.get(notifictionId);
  // RemoteViews rv;
  // if (downloadNotification == null) {
  // downloadNotification =
  // NotiUitl.getInstance(mContext).getNotiObject(android.R.drawable.stat_sys_download,
  // title);
  // rv = new RemoteViews(mContext.getPackageName(),
  // ResourceIdUtils.getResourceId(mContext,
  // "R.layout.mf_download_notify_layout"));
  // rv.setTextViewText(R.id.mf_tv_notify_title, title);
  // NotiUitl.getInstance(mContext).setParams(downloadNotification, "flags", (16
  // | 8));
  // NotiUitl.getInstance(mContext).setParams(downloadNotification, "vibrate",
  // null);
  //
  // if (!TextUtils.isEmpty(iconUrl)) {
  // File imagePathFile = new File(PromApkConstants.PROM_APP_ICONS_PATH);
  // if (!imagePathFile.exists()) {
  // imagePathFile.mkdirs();
  // }
  // File f = new File(imagePathFile, iconId + "");
  // if (!f.exists()) {
  // try {
  // f.createNewFile();
  // } catch (IOException e) {
  // Logger.p(e);
  // }
  // }
  // bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
  // if (bitmap == null) {
  // BitmapUtils.bind(new ImageView(mContext), iconUrl, f.getAbsolutePath(),
  // iconId + "", new BitmapUtils.Callback() {
  // @Override
  // public void onImageLoaded(ImageView view, Bitmap b) {
  // bitmap = b;
  // }
  // });
  // }
  // }
  // if (bitmap == null) {
  // rv.setImageViewResource(R.id.mf_iv_notify_icon, defIconId);
  // } else {
  // rv.setImageViewBitmap(R.id.mf_iv_notify_icon, bitmap);
  // }
  //
  // downloadNotificationMap.put(notifictionId, downloadNotification);
  // } else {
  // rv = new Smith<RemoteViews>(downloadNotification, "contentView").get();
  // }
  //
  // if (progress >= 0 && progress < 100) {
  // rv.setViewVisibility(R.id.mf_rl_download_notify_pb, View.VISIBLE);
  // rv.setProgressBar(R.id.mf_pb_download, 100, progress, false);
  // } else if (progress < 0) {
  // rv.setTextViewText(R.id.mf_tv_notify_title, title + "-" +
  // mContext.getResources().getString(R.string.mf_pause));
  // rv.setViewVisibility(R.id.mf_rl_download_notify_pb, View.GONE);
  // } else if (progress == 100) {
  // NotiUitl.getInstance(mContext).setParams(downloadNotification, "icon",
  // android.R.drawable.stat_sys_download_done);
  // rv.setViewVisibility(R.id.mf_rl_download_notify_pb, View.GONE);
  // rv.setViewVisibility(R.id.mf_tv_notify_msg, View.VISIBLE);
  // }
  //
  // if (pauseIntent == null) {
  // pauseIntent = new Intent();
  // }
  // PendingIntent pendingIntent = null;
  // PromAppInfo appInfo = new PromAppInfo();
  // appInfo.setAction(downloadUrl);
  // appInfo.setAppName(title);
  // appInfo.setFileVerifyCode(md5);
  // appInfo.setIconId(iconId);
  // appInfo.setPackageName(packageName);
  // appInfo.setUrl(iconUrl);
  // appInfo.setVer(versionCode);
  // appInfo.setType((byte) PromApkConstants.PROM_AD_INFO_ACTION_TYPE_APK);
  // appInfo.setActionType((byte) 1);
  // appInfo.setId(notifictionId);
  // if (progress == 100) {
  // Intent intent =
  // PromUtils.getInstance(mContext).clickPromAppInfoListener(appInfo,
  // position);
  // intent.putExtra(BundleConstants.BUNDLE_APP_INFO_SOURCE, source);
  // pendingIntent = PendingIntent.getService(mContext, appInfo.getId(), intent,
  // PendingIntent.FLAG_ONE_SHOT);
  // } else {
  // pendingIntent = PendingIntent.getActivity(mContext, notifictionId,
  // pauseIntent, 0);
  // }
  // NotiUitl.getInstance(mContext).setParams(downloadNotification, "defaults",
  // 0);
  // NotiUitl.getInstance(mContext).createReflectNoti(notifictionId, rv,
  // downloadNotification, pendingIntent);
  // }

  /**
   * 获取下载apk的路径，文件夹
   * 
   * @param packageName
   * @return
   */
  public String getApkDownloadPath(String packageName) {
    return FileConstants.getFileRootDirectory(mContext)  + packageName + "/abc";
  }

  
//  public String getChannidDirName(){
//    try {
//      File dir = new File(FileConstants.FILE_ROOT_DIRECTORY + "/" +mContext.getPackageName());
//      if(dir.exists()){
//        File[] list = dir.listFiles();
//        if(list != null){
//          for (File file : list) {
//            String path = file.getPath();
//            int position = path.lastIndexOf("/");
//            String name = path.substring(position+1, path.length());
//            if(name.contains("@")){
//              return "/"+name;
//            }
//            
//          }
//        }
//      }
//    } catch (Exception e) {
//      // TODO: handle exception
//    }
//    return "";
//    
//  }
  
  /**
   * 获取下载apk的绝对路径，文件
   * 
   * @param packageName
   * @param versionCode
   * @return
   */
  public String getApkDownloadFilePath(String packageName, int versionCode) {
    return DownloadUtils.getInstance(mContext).getApkDownloadPath(packageName) + "/" + packageName + "_r" + versionCode + ".app";
  }

  /**
   * 增加额外下载handler，用于其他接入的下载接收器，如游戏大王等
   * 
   * @param handler
   */
  public void addExtraHandler(Handler handler) {
    if(extraHandlerList != null && !extraHandlerList.contains(handler)){
      Logger.e(TAG, "addExtraHandler 111");
      extraHandlerList.add(handler);
    }
    Logger.e(TAG, "addExtraHandler 222");
  }

  public void addFolderIconHandler(FolderIconHandler handler, String packageName, int versionCode) {
    folderIconHandlerMap.put(new MyPackageInfo(packageName, versionCode), handler);
  }

  public FolderIconHandler getFolderIconHandler(String packageName, int versionCode) {
    return folderIconHandlerMap.get(new MyPackageInfo(packageName, versionCode));
  }

  public void removeFolderIconHandler(String packageName, int versionCode) {
    if (folderIconHandlerMap != null) {
      folderIconHandlerMap.remove(new MyPackageInfo(packageName, versionCode));
    }
  }
  
  public void addFloatListHandler(FloatListHandler handler, String packageName, int versionCode) {
    FloatListHandlerMap.put(new MyPackageInfo(packageName, versionCode), handler);
  }

  public FloatListHandler getFloatListHandler(String packageName, int versionCode) {
    return FloatListHandlerMap.get(new MyPackageInfo(packageName, versionCode));
  }

  public void removeFloatListHandler(String packageName, int versionCode) {
    if (FloatListHandlerMap != null) {
      FloatListHandlerMap.remove(new MyPackageInfo(packageName, versionCode));
    }
  }


  public void addExitListHandler(ExitListHandler handler, String packageName, int versionCode) {
    ExitListHandlerMap.put(new MyPackageInfo(packageName, versionCode), handler);
  }

  public ExitListHandler getExitListHandler(String packageName, int versionCode) {
    return ExitListHandlerMap.get(new MyPackageInfo(packageName, versionCode));
  }

  public void removeExitListHandler(String packageName, int versionCode) {
    if (ExitListHandlerMap != null) {
      ExitListHandlerMap.remove(new MyPackageInfo(packageName, versionCode));
    }
  }

  /**
   * 删除额外handler，终止发送信息
   * 
   * @param handler
   */
  public void removeExtraHandler(Handler handler) {
    extraHandlerList.remove(handler);
  }

  /**
   * 获取额外handler列表
   * 
   * @return
   */
  public ArrayList<Handler> getExtraHandlerList() {
    return extraHandlerList;
  }

  /**
   * 获取下载列表
   * 
   * @return
   */
  public HashMap<MyPackageInfo, DownloadApkThread> getDownloadApkThreadMap() {
    return downloadApkThreadMap;
  }

//  /**
//   * 获取自更新下载列表
//   * 
//   * @return
//   */
//  public HashMap<MyPackageInfo, DownloadApkThread> getSelfUpdateThreadMap() {
//    return selfUpdateThreadMap;
//  }

  /**
   * 增加下载apk的线程
   * 
   * @param downloadInfo
   * @return
   */
  public boolean addDownloadApkThread(DownloadInfo downloadInfo) {
    Logger.e(TAG, "addDownloadApkThread  ");
    boolean ret = true;
    boolean addWaiting = false;
    MyPackageInfo packageInfo = new MyPackageInfo(downloadInfo.getAdid(),downloadInfo.getPackageName(), downloadInfo.getVersionCode(), downloadInfo.getPosition());
    if (downloadApkThreadMap.size() < MAX_THREAD_COUNT) {
      Logger.e(TAG, "addDownloadApkThread 1111 ");
      DownloadApkThread thread = downloadApkThreadMap.get(packageInfo);
      if (thread == null || !thread.isAlive() || !thread.isDownloading()) {
        Logger.e(TAG, "addDownloadApkThread 222 ");
        downloadApkThreadMap.remove(packageInfo);
        thread = new DownloadApkThread(mContext, downloadInfo);
        downloadApkThreadMap.put(packageInfo, thread);
        thread.start();
        if(downloadInfo.isSaveDb() && !PromDBU.getInstance(mContext).hasDownloadInfoByPackageName(downloadInfo.getPackageName())){
          PromDBU.getInstance(mContext).saveDownloadInfo(downloadInfo);
        }
      } else {
        ret = false;
        downloadInfo = null;
        packageInfo = null;
      }
    } else {
      ret = false;
      addWaiting = true;
    }
    if (addWaiting && downloadInfo != null && !waitDownloadList.contains(downloadInfo)) {
      waitDownloadList.add(downloadInfo);
    }
    return ret;
  }

  /**
   * 下载等待队列中的apk
   * 
   * @return
   */
  public boolean downloadNextApk() {
    if (waitDownloadList != null) {
      DownloadInfo downloadInfo = waitDownloadList.poll();
      if (downloadInfo != null) {
        return addDownloadApkThread(downloadInfo);
      }
    }
    return false;
  }

  /**
   * 判断是否在等待队列
   * 
   * @param downloadInfo
   * @return
   */
  public boolean isDownloadWaiting(DownloadInfo downloadInfo ) {
    return waitDownloadList.contains(downloadInfo);
  }
  
  public boolean isDownloadOrWait(DownloadInfo downloadInfo,MyPackageInfo pkinfo){
    if(waitDownloadList.contains(downloadInfo)){
      Logger.e(TAG, "isDownloadOrWait  111");
      return true;
    }else{
    }
    if(downloadApkThreadMap.containsKey(pkinfo)){
      Logger.e(TAG, "isDownloadOrWait  222");
      return true;
    }
    return false;
  }

//  /**
//   * 自更新下载
//   * 
//   * @param downloadInfo
//   */
//  public void addSelfUpdateThread(DownloadInfo downloadInfo) {
//    MyPackageInfo packageInfo = new MyPackageInfo(downloadInfo.getAdid(),downloadInfo.getPackageName(), downloadInfo.getVersionCode(), downloadInfo.getPosition(),
//        downloadInfo.getSource());
//    DownloadApkThread thread = selfUpdateThreadMap.get(packageInfo);
//    if (thread == null || !thread.isAlive() || !thread.isDownloading()) {
//      StatsPromUtils.getInstance(mContext).addAppDownloadAction(downloadInfo.getAdid()+"/"+downloadInfo.getPackageName(), downloadInfo.getVersionCode(), 0, 0, 0);
//      selfUpdateThreadMap.remove(packageInfo);
//      thread = new DownloadApkThread(mContext, downloadInfo);
//      selfUpdateThreadMap.put(packageInfo, thread);
//      thread.start();
//    } else {
//      downloadInfo = null;
//      packageInfo = null;
//    }
//  }

//  /**
//   * html下载
//   * 
//   * @param html5Info
//   */
//  public void addDownloadZipThread(Handler downloadHandler, Html5Info html5Info, String filePath) {
//    DownloadHtml5Thread thread = downloadZipThreadMap.get(html5Info.getId());
//    if (thread == null || !thread.isAlive() || !thread.isDownloading()) {
//      downloadZipThreadMap.remove(html5Info.getId());
//      thread = new DownloadHtml5Thread(mContext, downloadHandler, html5Info, filePath);
//      downloadZipThreadMap.put("" + html5Info.getId(), thread);
//      thread.start();
//    }
//  }

//  /**
//   * 删除html下载
//   * 
//   * @param html5Info
//   */
//  public void removeDownloadZipThread(int id) {
//    if (downloadZipThreadMap != null) {
//      DownloadHtml5Thread thread = downloadZipThreadMap.remove(id);
//      if (thread != null && thread.isAlive()) {
//        thread.onPause();
//      }
//    }
//  }

  /**
   * 删除下载apk任务
   * 
   * @param key
   */
  public void removeDownloadApkThread(MyPackageInfo key) {
    if (downloadApkThreadMap != null) {
      DownloadApkThread thread = downloadApkThreadMap.remove(key);
      if (thread != null && thread.isAlive()) {
        thread.onPause();
      }
    }
  }

  /**
   * 删除等待apk任务
   * 
   * @param key
   */
  public void removeWaitApkThread(String pgn) {
    if (waitDownloadList != null) {
      List<DownloadInfo> list = new ArrayList<DownloadInfo>();
      for (DownloadInfo info : waitDownloadList) {
        if (!TextUtils.isEmpty(info.getPackageName()) && info.getPackageName().equals(pgn)) {
          list.add(info);
        }
      }
      for (DownloadInfo info : list) {
        waitDownloadList.remove(info);
      }
    }
  }

//  /**
//   * 删除自更新下载任务
//   * 
//   * @param key
//   */
//  public void removeSelfUpdateThread(MyPackageInfo key) {
//    if (selfUpdateThreadMap != null) {
//      DownloadApkThread thread = selfUpdateThreadMap.remove(key);
//      if (thread != null && thread.isAlive()) {
//        thread.onPause();
//      }
//    }
//  }

  /**
   * 删除所有下载进程
   */
  public void removeAllThread() {
    if (downloadApkThreadMap != null) {
      Set<MyPackageInfo> keySet = downloadApkThreadMap.keySet();
      MyPackageInfo[] keyArray = new MyPackageInfo[downloadApkThreadMap.size()];
      int i = 0;
      for (MyPackageInfo key : keySet) {
        keyArray[i++] = key;
      }
      for (MyPackageInfo key : keyArray) {
        removeDownloadApkThread(key);
      }
    }
  }

  /**
   * 清楚等待队列
   */
  public void clearWaitingAppList() {
    if (waitDownloadList != null) {
      waitDownloadList.clear();
    }
  }

  /**
   * 获取应用的状态
   * 
   * @param context
   * @param packageName
   * @param version
   * @return
   */
  public int getAppStatus(String packageName, int versionCode) {
    if (downloadApkThreadMap.containsKey(new MyPackageInfo(packageName, versionCode))) {
      return CommConstants.APP_STATUS_DOWNLOADING;
    }
    if (waitDownloadList.contains(new DownloadInfo(packageName, versionCode))) {
      return CommConstants.APP_STATUS_DOWNLOAD_WAITING;
    }
    PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(mContext, packageName);
    if (TextUtils.isEmpty(packageName) || versionCode < 0) {
      return -1;
    }
    int status = 0;
    if (pInfo != null) {
      if (pInfo.versionCode >= versionCode) {
        return CommConstants.APP_STATUS_INSTALLED;
      } else {
        status = CommConstants.APP_STATUS_HAS_UPDATE;
      }
    }
    File path = new File(DownloadUtils.getInstance(mContext).getApkDownloadPath(packageName));
    if (path.exists()) {
      File f = new File(path, packageName + "_r" + versionCode + ".app");
      if (f.exists()) {
        return CommConstants.APP_STATUS_NO_INSTALL;
      }
      f = new File(path, packageName + "_r" + versionCode + ".tmp");
      if (f.exists()) {
        return CommConstants.APP_STATUS_PAUSE;
      }
    }
    if (status != 0) {
      return status;
    }
    return CommConstants.APP_STATUS_NO_DOWNLOAD;
  }

  public void onDestroy() {
    clearWaitingAppList();
    removeAllThread();
  }
  
  public void sendFinishMessage(String adid,String packageName,int versionCode,int position,int source,int status){
    Logger.e(TAG, "sendFinishMessage");
    for (Handler extraHandler : extraHandlerList) {
//      Message msg2 = extraHandler.obtainMessage(msg.what, msg.obj);
      Logger.e(TAG, "sendFinishMessage  111");
      Message msg = new Message();
      msg.what = status;
      Bundle b = new Bundle();
      b.putString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_ADID,adid);
      b.putString(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PACKAGE_NAME, packageName);
      b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_VERSION_CODE, versionCode);
      b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_POSITION, position);
      b.putInt(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_SOURCE, source);
      msg.obj = b;
      extraHandler.sendMessage(msg);
    }
  }

}
