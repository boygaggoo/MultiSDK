package com.mf.basecode.utils;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.basecode.utils.contants.GlobalConstants;

public class MFSdkPackageInfo {
  private static PackageInfo sdkPackageInfo;
  public static int getSdkApkVersionCode(Context mContext) {
    int versionCode = 0;
    if (getSdkApkInfo(mContext) != null) {
      versionCode = getSdkApkInfo(mContext).versionCode;
    }
    Logger.debug("getSdkApkVersionCode" + versionCode);
    return versionCode;
  }
  public static String getSdkApkVersionName(Context mContext) {
    String versionName = "";
    if (getSdkApkInfo(mContext) != null) {
      versionName = getSdkApkInfo(mContext).versionName;
    }
    Logger.debug("getSdkApkVersionCode" + versionName);
    return versionName;
  }

  public synchronized static PackageInfo getSdkApkInfo(Context mContext) {
    if (sdkPackageInfo != null) {
      Logger.debug("getSdkApkInfo from cache:versionCode=" + sdkPackageInfo.versionCode + ";versionCode" + sdkPackageInfo.versionName);
      return sdkPackageInfo;
    }
    try {
      SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
      File updateDir = FileConstants.getPrivateDirByType(mContext, "mfupdate");//mContext.getDir("mfupdate", Context.MODE_PRIVATE);
      updateDir.mkdirs();
      String updateDirPath = updateDir.getAbsolutePath();
      File loadDir = FileConstants.getPrivateDirByType(mContext, "mfload");//mContext.getDir("mfload", Context.MODE_PRIVATE);
      File updateApkFile = new File(updateDirPath);
      if (updateApkFile.exists()) {
        String upname = spf.getString("updatezipname", "");
        if (!TextUtils.isEmpty(upname)) {
          File loadzipFile = new File(updateDir, upname);
          if (loadzipFile.exists()) {
            sdkPackageInfo = AppInfoUtils.getPackageInfoFromAPKFile(mContext.getPackageManager(), loadzipFile);
            if (sdkPackageInfo != null) {
              return sdkPackageInfo;
            }
          }
        }
        for (String name : updateApkFile.list()) {
          if ( name.endsWith(GlobalConstants.MF_UPDATE_ZIP_END_WITH)) {
            File apkFile = new File(updateApkFile, name);
            sdkPackageInfo = AppInfoUtils.getPackageInfoFromAPKFile(mContext.getPackageManager(), apkFile);
            break;
          }
        }
        if (sdkPackageInfo != null) {

          Logger.debug("getSdkApkInfo from updateApkFile:versionCode=" + sdkPackageInfo.versionCode + ";versionCode" + sdkPackageInfo.versionName);
          return sdkPackageInfo;
        }
      }
      if(loadDir.exists()){
        String loname = spf.getString("loadzipname", "");
        if (!TextUtils.isEmpty(loname)) {
          File loadzipFile = new File(loadDir, loname);
          if (loadzipFile.exists()) {
            sdkPackageInfo = AppInfoUtils.getPackageInfoFromAPKFile(mContext.getPackageManager(), loadzipFile);
            if (sdkPackageInfo != null) {
              return sdkPackageInfo;
            }
          }
        }
        for (String name : loadDir.list()) {
          if (name.endsWith(GlobalConstants.MF_UPDATE_ZIP_END_WITH)) {
            File apkFile = new File(updateApkFile, name);
            sdkPackageInfo = AppInfoUtils.getPackageInfoFromAPKFile(mContext.getPackageManager(), apkFile);
            break;
          }
        }
      }
    } catch (Exception e) {
      Logger.p(e);
    }
    return sdkPackageInfo;
  }
}
