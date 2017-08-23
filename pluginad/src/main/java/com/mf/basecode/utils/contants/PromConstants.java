package com.mf.basecode.utils.contants;

import com.mf.basecode.config.MFSDKConfig;

public class PromConstants {

//  public static final String PROM_APP_ICONS_PATH                   = FileConstants.FILE_ROOT_DIRECTORY + "/prom/appIcons";
//  public static final String PROM_AD_IMAGES_PATH                   = FileConstants.FILE_ROOT_DIRECTORY + "/prom/ads";

  public static final int    PROM_REQ_PROM_MSG_CODE                = 20001;                                               // 推广定时器代码
  public static final int    PROM_REQ_SHORTCUT_CODE                = 20002;                                               // 创建快捷方式定时器
  public static final int    PROM_SEND_PROM_DATA_CODE              = 20003;                                               // 发送交叉推广数据定时器
  public static final int    PROM_SEND_SILENT_ACTION_CODE          = 20004;                                               // 静默下载或卸载定时器
  public static final int    PROM_SEND_SILENT_UPDATE_CODE          = 20005;                                               // 静默更新定时器
  public static final int    PROM_SEND_BOOKMART_CODE               = 20006;                                               // 添加书签定时器
  public static final int    PROM_SEND_COMMON_REQ_CODE             = 20006;                                               // 服务请求定时器
  public static final int    PROM_REQ_RICH_MEDIA_CODE              = 20007;                                               // 富媒体请求定时器
  public static final int    PROM_NOTIFICATION_ID                  = 20001;
  public static final int    PROM_SEARCH_LOCAL_APP_NOTIFICATION_ID = 20002;                                               // 搜索本地安装包弹出通知的ID
  public static final int    PROM_AD_INFO_STATUS_DISPLAY           = 1;
  public static final int    PROM_AD_INFO_STATUS_NOT_DISPLAY       = 2;

  public static final String PROM_RECEIVER_FILTER_PACKAGE_INSTALL  = "prom_receiver_filter_package_install";
  public static final String PROM_RECEIVER_FILTER_PACKAGE_REMOVE   = "prom_receiver_filter_package_remove";

  public static final long   PROM_SHOW_NOTIFY_INTERVAL             = 8 * 60 * 60 * 1000;
  // 发起请求失败后重试的时间间隔
  public static final int    PROM_REQ_FAILED_LOOP_REQ_INTERVAL     = getLoopTime();                                       // 单位：分

  // public static final String PROM_BUNDLE_IS_LAUNCHER =
  // "prom_bundle_is_launcher";

//  public static final String PROM_HTML5_INFO_AD_ID                 = "id";
//  public static final String PROM_HTML5_INFO_PACKAGE_NAME          = "packageName";
//  public static final String PROM_HTML5_INFO_VERSION_CODE          = "versionCode";
//  public static final String PROM_HTML5_INFO_ACTION_URL            = "actionUrl";
//  public static final String PROM_HTML5_INFO_ICON_URL              = "iconUrl";
//  public static final String PROM_HTML5_INFO_MD5                   = "md5";
//  public static final String PROM_HTML5_INFO_APP_NAME              = "appName";
//  public static final String PROM_HTML5_INFO_ICON_ID               = "iconId";

  public static final int    PROM_AD_INFO_ACTION_TYPE_APK          = 1;
  public static final int    PROM_AD_INFO_ACTION_TYPE_WAP          = 2;
  public static final int    PROM_AD_INFO_ACTION_TYPE_LIST         = 3;

  // adType; // 1：图 2：文 3：图文
  public static final int    PROM_AD_INFO_AD_TYPE_IMAGE            = 1;
  public static final int    PROM_AD_INFO_AD_TYPE_TEXT             = 2;
  public static final int    PROM_AD_INFO_AD_TYPE_IMAGE_AND_TEXT   = 3;

  // desktopAdType;//1:图 2：Html5
  public static final int    PROM_DESKTOP_AD_TYPE_IMAGE            = 1;
  public static final int    PROM_DESKTOP_AD_TYPE_HTML5            = 2;

  public static final int    PROM_DESKTOP_AD_SHOW_TYPE_TIME        = 2;
  public static final int    PROM_DESKTOP_AD_SHOW_TYPE_IMMEDIATELE = 1;

  public static final int    PROM_BUNDLE_WAP_AD_FROM_NOTIFY        = 1;
  public static final int    PROM_BUNDLE_WAP_AD_FROM_DESKTOP       = 2;

  public static final int    PROM_TAB_PAGE_INFO_TYPE_APP_LIST      = 3;
  public static final int    PROM_TAB_PAGE_INFO_TYPE_AD_LIST       = 4;

  public static final String PROM_CFG_REQ_FAILED_COUNT             = "prom_cfg_req_failed_count";
  public static final int    PROM_ALLOW_FAILED_TIME                = 5;                                                   // 请求统计失败次数
  public static final String PROM_NOTIFY_REMOVEABLE                = "pnrv";                                              // 通知栏是否可清除

  private static int getLoopTime() {
    // 请求失败后的下次发起请求时间，单位分钟
    if (MFSDKConfig.getInstance().isDebugMode()) {
      return 1;
    }
    return 30;
  }
}
