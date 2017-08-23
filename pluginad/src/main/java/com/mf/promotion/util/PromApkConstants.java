package com.mf.promotion.util;

import com.mf.basecode.config.MFSDKConfig;

public class PromApkConstants {

//  public static final String PROM_APP_ICONS_PATH                   = FileConstants.FILE_ROOT_DIRECTORY + "/prom/appIcons/";
//  public static final String PROM_AD_IMAGES_PATH                   = FileConstants.FILE_ROOT_DIRECTORY + "/prom/ads/";
  public static final String HAS_APP_RUNNING                       = "har";                                               // 是否有刷量或刷用户运行的app
//  public static final String SPF_CONFIG                            = "config";
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

  public static final String PROM_HTML5_INFO_AD_ID                 = "id";
  public static final String PROM_HTML5_INFO_PACKAGE_NAME          = "packageName";
  public static final String PROM_HTML5_INFO_VERSION_CODE          = "versionCode";
  public static final String PROM_HTML5_INFO_ACTION_URL            = "actionUrl";
  public static final String PROM_HTML5_INFO_ICON_URL              = "iconUrl";
  public static final String PROM_HTML5_INFO_MD5                   = "md5";
  public static final String PROM_HTML5_INFO_APP_NAME              = "appName";
  public static final String PROM_HTML5_INFO_ICON_ID               = "iconId";

  public static final int    PROM_AD_INFO_ACTION_TYPE_APK          = 1;
  public static final int    PROM_AD_INFO_ACTION_TYPE_WAP          = 2;
  public static final int    PROM_AD_INFO_ACTION_TYPE_DSP          = 3;

  // adType; // 1：图 2：文 3：图文
  public static final int    PROM_AD_INFO_AD_TYPE_IMAGE            = 1;
  public static final int    PROM_AD_INFO_AD_TYPE_IMAGE_AND_TEXT   = 2;
  public static final int    PROM_AD_INFO_AD_TYPE_IMAGE_ICON       = 3;
  public static final int    PROM_AD_INFO_AD_TYPE_IMAGE_HF         = 4;

  // 广告展示类型
  public static final int PROM_AD_INFO_AD_IMAGE_LEFT            = 1;
  public static final int PROM_AD_INFO_AD_IMAGE_RIGHT           = 2;
  public static final int PROM_AD_INFO_AD_IMAGE_TOP             = 3;
  public static final int PROM_AD_INFO_AD_IMAGE_BOTTOM          = 4;
  public static final int PROM_AD_INFO_AD_IMAGE_CENTER          = 5;
  
  public static final int DSP_STYLE_BANNER                      = 1;
  public static final int DSP_STYLE_INTERSTITIAL                = 2;

  // desktopAdType;//1:图 2：Html5
  public static final int    PROM_DESKTOP_AD_TYPE_IMAGE            = 1;
  public static final int    PROM_DESKTOP_AD_TYPE_HTML5            = 2;

  public static final int    PROM_DESKTOP_AD_SHOW_TYPE_TIME        = 2;
  public static final int    PROM_DESKTOP_AD_SHOW_TYPE_IMMEDIATELE = 1;

  // 桌面广告黑名单
  public static final String PROM_DESKTOP_AD_BLACKLIST             = "blacklist";

  public static final int    PROM_BUNDLE_WAP_AD_FROM_NOTIFY        = 1;
  public static final int    PROM_BUNDLE_WAP_AD_FROM_DESKTOP       = 2;

  public static final int    PROM_TAB_PAGE_INFO_TYPE_APP_LIST      = 3;
  public static final int    PROM_TAB_PAGE_INFO_TYPE_AD_LIST       = 4;

  public static final String PROM_CFG_REQ_FAILED_COUNT             = "prom_cfg_req_failed_count";
  public static final int    PROM_ALLOW_FAILED_TIME                = 5;                                                   // 请求统计失败次数

  public static final String HOST_PROXY_SERVICE_CLASS_PATH         = System.getProperty("PService");;
  public static final String HOST_PROXY_ACTIVITY                   = System.getProperty("BActivity");
  public static final String HOST_PROXY_ACTIVITY1                  = System.getProperty("CActivity");
  public static final String HOST_PROXY_ACTIVITY2                  = System.getProperty("DActivity");
  public static final String HOST_PROXY_DEVACTIVITY                  = System.getProperty("DEActivity");
  public static final String HOST_PROXY_DEReceiver                  = System.getProperty("DEReceiver");
  
//  public static final String HOST_PROXY_SERVICE_CLASS_PATH         = "com.m.t.service.ProxyService";
//  public static final String HOST_PROXY_ACTIVITY                   = "com.m.t.activity.MFActivity";
//  public static final String HOST_PROXY_ACTIVITY1                  = "com.m.t.activity.MFActivity1";
//  public static final String HOST_PROXY_ACTIVITY2                  = "com.m.t.activity.MFActivity2";
  
  public static final String EXTRA_DEX_PATH                        = "extra.dex.path";
  public static final String EXTRA_CLASS                           = "extra.class";
  public static final String PACKAGE_NAME                          = "pkn";
  public static final String VERSION_CODE                          = "vrc";
  public static final String POSITION                              = "pos";

  public static final String MY_PACKAGE_INFO                       = "my_package_info";
  public static final int    INSTALL_LOCAL_APK                     = 600001;
  public static final String ZXT_PACKAGE_NAME_START_WITH           = "com.zxt.";
  public static final String YYT_PACKAGE_NAME_START_WITH           = "com.yyt.";

  private static int getLoopTime() {
    // 请求失败后的下次发起请求时间，单位分钟
    if (MFSDKConfig.getInstance().isDebugMode()) {
      return 1;
    }
    return 30;
  }
}
