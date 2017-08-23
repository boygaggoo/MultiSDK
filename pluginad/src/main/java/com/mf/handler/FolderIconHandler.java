package com.mf.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.download.util.DownloadConstants;
import com.mf.download.util.DownloadUtils;
import com.mf.promotion.widget.CircleProgressView;
import com.mf.utils.AppInstallUtils;

public class FolderIconHandler extends Handler {

  private CircleProgressView mView;
  private Context            mContext;
  private String             apkPath;
  private String             adid;
  private String             packageName;
  private int                versionCode;
  private int                progress;
  private int                position;
  private int                source;

  public FolderIconHandler(Context mContext, String apkPath, String adid,String packageName, 
      int versionCode,
       int progress,int position, int source) {
    super();
    this.adid = adid;
    this.mContext = mContext;
    this.apkPath = apkPath;
    this.packageName = packageName;
    this.versionCode = versionCode;
    this.progress = progress;
    this.position = position;
    this.source = source;
  }

  @Override
  public void handleMessage(Message msg) {
    // TODO Auto-generated method stub
    switch (msg.what) {
    case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOADING:
      Bundle b = (Bundle) msg.obj;
      if ((int) b.getFloat(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PROGRESS) > progress
          && (int) b.getFloat(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PROGRESS) != 100) {
        progress = (int) b.getFloat(DownloadConstants.DOWNLOAD_HANDLER_BUNDLE_PROGRESS);
        if (mView != null) {
          mView.setProgress(progress);
        }
      }
      break;
    case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL:
      if (mView != null) {
        mView.setDownloading(false);
      }
      break;
    case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_PAUSE:
      if (mView != null) {
        mView.setDownloading(false);
      }
      break;
    case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH:
      if (mView != null) {
        mView.setProgress(100);
      }
      DownloadUtils.getInstance(mContext).removeDownloadApkThread(new MyPackageInfo(packageName, versionCode));
      DownloadUtils.getInstance(mContext).removeFolderIconHandler(packageName, versionCode);
      if (AppInstallUtils.isOpenInstallRightNow(position, source)) {
        // DownloadUtils.getInstance(mContext).showDownloadNotify(packageName,
        // version, notificationId, null, null, null, defIconId, iconId, -2,
        // null, iconUrl,
        // downloadUrl, md5, position, source, null, false);
        AppInstallUtils.installApp(mContext, apkPath, new MyPackageInfo(adid,packageName, versionCode, position, source));
      }
      break;
    }
  }

  public CircleProgressView getmView() {
    return mView;
  }

  public void setmView(CircleProgressView mView) {
    this.mView = mView;
  }
}
