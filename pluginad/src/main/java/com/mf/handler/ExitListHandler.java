package com.mf.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.download.util.DownloadConstants;
import com.mf.download.util.DownloadUtils;
import com.mf.utils.AppInstallUtils;
import com.mf.utils.ResourceIdUtils;

public class ExitListHandler extends Handler {

  private TextView             mView;
  private Button             mButton;
  private Context            mContext;
  private String             apkPath;
  private String             adid;
  private String             packageName;
  private int                versionCode;
  private int                progress;
  private int                position;
  private int                source;
  private boolean            first = true;
  

  public ExitListHandler(Context mContext, String apkPath, String adid,String packageName, 
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
          mView.setText("下载中");
        }
        if(first){
          if(mButton != null){
            mButton.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_download_image")));
          }
        }
      }
      break;
    case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FAIL:
      if (mView != null) {
        mView.setText("重下");
        if(mButton != null){
          mButton.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_download_image")));
        }
        DownloadUtils.getInstance(mContext).removeExitListHandler(packageName, versionCode);
      }
      break;
    case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_PAUSE:
      if (mView != null) {
        
      }
      break;
    case DownloadConstants.DOWNLOAD_HANDLER_STATUS_DOWNLOAD_FINISH:
      if (mView != null) {
        mView.setText("安装");
      }
      if(mButton != null){
        mButton.setBackgroundDrawable(mContext.getResources().getDrawable(ResourceIdUtils.getInstance().getResourceId("R.drawable.exit_finish")));
      }
      DownloadUtils.getInstance(mContext).removeDownloadApkThread(new MyPackageInfo(packageName, versionCode));
      DownloadUtils.getInstance(mContext).removeExitListHandler(packageName, versionCode);
      if (AppInstallUtils.isOpenInstallRightNow(position, source)) {
        AppInstallUtils.installApp(mContext, apkPath, new MyPackageInfo(adid,packageName, versionCode, position, source));
      }
      break;
    }
  }

  public TextView getmView() {
    return mView;
  }

  public void setmView(TextView mView) {
    this.mView = mView;
  }

  public Button getmButton() {
    return mButton;
  }

  public void setmButton(Button mButton) {
    this.mButton = mButton;
  }
}

