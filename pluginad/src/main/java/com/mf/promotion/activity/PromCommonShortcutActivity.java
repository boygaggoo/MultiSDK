package com.mf.promotion.activity;

import android.app.Activity;
import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.download.model.DownloadInfo;
import com.mf.download.util.DownloadUtils;
import com.mf.promotion.util.PromUtils;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;
import com.mf.utils.AppInstallUtils;
import com.mf.utils.ResourceIdUtils;

public class PromCommonShortcutActivity extends Activity {
  private static final String TAG      = "ApkShortcutActivity";
  private int                 position = StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Logger.debug(TAG, "ApkShortcutActivity is onCreate");
    Intent intent = getIntent();
    if (intent != null) {
      String adid = intent.getStringExtra(BundleConstants.BUNDLE_AD_INFO_ADID);
      String packageName = intent.getStringExtra(BundleConstants.BUNDLE_PACKAGE_NAME);
      int versionCode = intent.getIntExtra(BundleConstants.BUNDLE_VERSION_CODE, 0);
      String downloadUrl = intent.getStringExtra(BundleConstants.BUNDLE_DOWNLOAD_URL);
      String md5 = intent.getStringExtra(BundleConstants.BUNDLE_MD5);
      position = intent.getIntExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, position);

      Logger.debug(TAG, "shortcut packageName = " + packageName);

      // 显示此界面，相当于点击快捷方式
//      StatsPromUtils.getInstance(that).addClickAction(adid+"/"+packageName,StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT);
      if (packageName != null) {
        int status = DownloadUtils.getInstance(this).getAppStatus(packageName, versionCode);
        PackageInfo pInfo = AppInstallUtils.getPackageInfoByPackageName(this, packageName);
        if (pInfo != null && pInfo.versionCode >= versionCode) {
          AppInstallUtils.launchOtherActivity(this, packageName, null);
          StatsPromUtils.getInstance(this).addLaunchAction(adid+"/"+packageName,StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT);
          finish();
          return;
        }
        StatsPromUtils.getInstance(this).addClickAction(adid+"/"+packageName,StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT);
        if (status != CommConstants.APP_STATUS_INSTALLED) {
          String apkPath = DownloadUtils.getInstance(this).getApkDownloadFilePath(packageName, versionCode);
          File downloadFile = new File(apkPath);
          if (!downloadFile.exists()) {
            try {
              Toast.makeText(this.getApplicationContext(), ResourceIdUtils.getInstance().getStringByResId("R.string.mf_checking"), Toast.LENGTH_LONG).show();
            } catch (Throwable e) {
              Logger.p(e);
            }
          }
          ArrayList<MyPackageInfo> commonShortcutInfoList = PromUtils.getInstance(this).getCommonShortcutInfoList();
          boolean exit = false;
          if (commonShortcutInfoList != null) {
            for (MyPackageInfo info : commonShortcutInfoList) {
              if (info.getPackageName().equals(packageName) && info.getVersionCode() == versionCode) {
                exit = true;
                break;
              }
            }
          }
          if (!exit) {
            PromUtils.getInstance(this).addCommonShortcutInfo(new MyPackageInfo(packageName, versionCode));
            SearchApkFileFromSDCardTask task = new SearchApkFileFromSDCardTask(adid,packageName, versionCode,downloadUrl, md5);
            task.execute();
          }
        }
      }
    } else {
      Logger.e(TAG, "intent is null");
    }
    finish();
  }

  /**
   * 从SD卡搜索apk文件
   */
  private class SearchApkFileFromSDCardTask extends AsyncTask<Void, Void, String> {
    String adid;
    String packageName;
    int    versionCode;
    String downloadUrl;
    String md5;

    public SearchApkFileFromSDCardTask(String adid,String packageName, int versionCode, String downloadUrl, String md5) {
      this.adid = adid;
      this.packageName = packageName;
      this.versionCode = versionCode;
      this.downloadUrl = downloadUrl;
      this.md5 = md5;
    }

    @Override
    protected String doInBackground(Void... params) {
      String apkPath = DownloadUtils.getInstance(PromCommonShortcutActivity.this).getApkDownloadFilePath(packageName, versionCode);
      File downloadFile = new File(apkPath);
      if (!downloadFile.exists()) {
        apkPath = null;
      }
      // 移除搜索
      PromUtils.getInstance(PromCommonShortcutActivity.this).removeCommonShortcutInfo(new MyPackageInfo(packageName, versionCode));
      return apkPath;
    }

    @Override
    protected void onPostExecute(String apkPath) {
      if (apkPath == null) {
        apkPath = DownloadUtils.getInstance(PromCommonShortcutActivity.this).getApkDownloadFilePath(packageName, versionCode);
        MyPackageInfo packageInfo = new MyPackageInfo(packageName, versionCode);
        DownloadInfo dinfo = new DownloadInfo(null,adid, packageName, versionCode, position, StatsPromConstants.STATS_PROM_AD_INFO_SOURCE_LAUNCH_ICON, downloadUrl, md5, true,false);
        if(DownloadUtils.getInstance(PromCommonShortcutActivity.this).isDownloadOrWait(dinfo, packageInfo)){
          Logger.e(TAG, "isDownloadOrWait");
          DownloadUtils.getInstance(PromCommonShortcutActivity.this).addExtraHandler(new DownloadUtils.MyNotifyDownloadHandler(apkPath,adid, packageName,  versionCode, position, 0));
        }else{
          Logger.e(TAG, "addDownloadApkThread");
          DownloadUtils.getInstance(PromCommonShortcutActivity.this).addDownloadApkThread(
              new DownloadInfo(new DownloadUtils.MyNotifyDownloadHandler(apkPath,adid, packageName,  versionCode, position, 0),
                  adid,packageName, versionCode, position, 0, downloadUrl, md5, true,false));
        }
//        if (!DownloadUtils.getInstance(that).getDownloadApkThreadMap().containsKey(new MyPackageInfo(packageName, versionCode))) {
//          DownloadUtils.MyNotifyDownloadHandler handler = new DownloadUtils.MyNotifyDownloadHandler(apkPath, adid, packageName, versionCode, position, StatsPromConstants.STATS_PROM_AD_INFO_SOURCE_LAUNCH_ICON);
//          DownloadUtils.getInstance(that).addDownloadApkThread(
//              new DownloadInfo(handler,adid, packageName, versionCode, position, StatsPromConstants.STATS_PROM_AD_INFO_SOURCE_LAUNCH_ICON, downloadUrl, md5, true,false));
//          try {
//            Toast.makeText(that.getApplicationContext(), ResourceIdUtils.getInstance().getStringByResId("R.string.mf_optimize_to_background"), Toast.LENGTH_LONG).show();
//          } catch (Throwable e) {
//            Logger.p(e);
//          }
//        }
      } else {
        PromUtils.getInstance(PromCommonShortcutActivity.this).install(PromCommonShortcutActivity.this.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0, apkPath,
            new MyPackageInfo(adid,packageName, versionCode, position, StatsPromConstants.STATS_PROM_AD_INFO_SOURCE_LAUNCH_ICON));
      }
    }
  }

}
