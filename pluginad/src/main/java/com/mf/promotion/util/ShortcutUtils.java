package com.mf.promotion.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.BundleConstants;
import com.mf.data.PromDBU;
import com.mf.model.AdDbInfo;
import com.mf.promotion.activity.PromCommonShortcutActivity;
import com.mf.promotion.activity.PromHomeWapScreenActivity;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;

public class ShortcutUtils {
  public static boolean createShortcut(Context context, AdDbInfo apkInfo, String name, Bitmap iconImage, boolean duplicate, int position) {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setComponent(new ComponentName(context.getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
    if (apkInfo != null) {
      // 暂时用于交叉推广生成快捷方式
      intent.putExtra(BundleConstants.BUNDLE_AD_INFO_ADID,apkInfo.getAdId());
      intent.putExtra(BundleConstants.BUNDLE_PACKAGE_NAME, apkInfo.getPackageName());
      intent.putExtra(BundleConstants.BUNDLE_VERSION_CODE, apkInfo.getVersionCode());
      intent.putExtra(BundleConstants.BUNDLE_DOWNLOAD_URL, apkInfo.getAdDownUrl());
      intent.putExtra(BundleConstants.BUNDLE_MD5, apkInfo.getFileMd5());
      intent.putExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, position);
    }
    intent.putExtra(PromApkConstants.EXTRA_CLASS, PromCommonShortcutActivity.class.getCanonicalName());
    Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    shortcutIntent.putExtra("duplicate", false);
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
    if (iconImage != null) {
      shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconImage);
    } else {
      Logger.error("shortcut image is null");
      return false;
    }
    PromDBU.getInstance(context).updateAdInfoHasShowTimes(apkInfo, PromDBU.PROM_SHORTCUT, PromDBU.PROM_SHORTCUT_NAME);
    StatsPromUtils.getInstance(context).addDisplayAction(apkInfo.getAdId()+"/"+apkInfo.getPackageName(),StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT);
    Logger.debug("send create shortcut Broadcast");
    context.sendBroadcast(shortcutIntent);
    return true;
  }
  
  public static boolean createWebViewShortcut(Context context, AdDbInfo apkInfo, String name, Bitmap iconImage, boolean duplicate, int position) {
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setComponent(new ComponentName(context.getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
    if (apkInfo != null) {
      // 暂时用于交叉推广生成快捷方式
        intent.putExtra(BundleConstants.BUNDLE_AD_INFO_ADID, apkInfo.getAdId());
        intent.putExtra(BundleConstants.BUNDLE_AD_INFO_PROM_TYPE, apkInfo.getPromType());
        intent.putExtra(BundleConstants.BUNDLE_APP_INFO_POSITION, position);
    }
    
    intent.putExtra(PromApkConstants.EXTRA_CLASS, PromHomeWapScreenActivity.class.getCanonicalName());
    Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    shortcutIntent.putExtra("duplicate", false);
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
    if (iconImage != null) {
      shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconImage);
    } else {
      Logger.error("shortcut image is null");
      return false;
    }
    PromDBU.getInstance(context).updateAdInfoHasShowTimes(apkInfo, PromDBU.PROM_SHORTCUT, PromDBU.PROM_SHORTCUT_NAME);
    StatsPromUtils.getInstance(context).addDisplayAction(apkInfo.getAdId()+"/"+apkInfo.getPackageName(),StatsPromConstants.STATS_PROM_AD_INFO_POSITION_SHORTCUT);
    Logger.debug("send create shortcut Broadcast");
    context.sendBroadcast(shortcutIntent);
    return true;
  }
  
  

//  public static void createFolderShortcut(Context context, Bitmap iconImage, SharedPreferences spf) {
//    Intent intent = new Intent(Intent.ACTION_MAIN);
//    intent.setComponent(new ComponentName(context.getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
//    intent.putExtra(PromApkConstants.EXTRA_CLASS, PromFolderActivity.class.getCanonicalName());
//    Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//    shortcutIntent.putExtra("duplicate", false);
//    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
//    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "文件夹");
//    if (iconImage != null) {
//      shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconImage);
//    } else {
//      Logger.error("shortcut image is null");
//      return;
//    }
//    Logger.debug("send create shortcut Broadcast");
//    context.sendBroadcast(shortcutIntent);
//    spf.edit().putBoolean("foldershotcut", true).commit();
//    StatsPromUtils.getInstance(context).addDisplayAction("0/"+context.getPackageName(), StatsPromConstants.STATS_PROM_AD_INFO_POSITION_FOLDER);
//  }
  
//  public static void delShortcut(Context context, AdDbInfo apkInfo) {
//    Intent intent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
//    intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,apkInfo.getAdName());
//    ComponentName comp = new ComponentName(context.getPackageName(),PromApkConstants.HOST_PROXY_ACTIVITY);
//    intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent().setComponent(comp).setAction(Intent.ACTION_MAIN));
//    Logger.debug("send delShortcut Broadcast");
//    context.sendBroadcast(intent);
//  }
}
