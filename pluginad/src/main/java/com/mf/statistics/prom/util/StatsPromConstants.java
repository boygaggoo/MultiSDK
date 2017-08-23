package com.mf.statistics.prom.util;


public class StatsPromConstants {
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_INSTALL           = 1;                         // 安装
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_LANUCH            = 2;                         // 启动
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_DISPLAY           = 3;                         // 显示
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_CLICK_AD          = 4;                         // 点击广告
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_DOWNLOAD_SUCCESS  = 5;                         // 下载成功
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_INSTALL_SUCCESS   = 6;                         // 安装成功
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_UNINSTALL         = 7;                         // 卸载
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_UNINSTALL_SUCCESS = 8;                         // 卸载成功
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_CONFIRM           = 9;                         // 确认
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_CANCEL            = 10;                        // 取消
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_PREDOWNLOAD       = 11;                        // 预下载
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_DOWNLOAD_FAIL     = 12;                        // 用户下载失败
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_PREDOWNLOAD_FAIL  = 13;                        // 预下载失败
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_REQ               = 14;                        // 神秘请求
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_ACTIVE_PRE        = 15;                        // 激活前预下载
  
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_ENHANCED_SUCCESS  = 16;                        // 加强成功
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_ENHANCED_FAIL     = 17;                        // 加强失败
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_LOAD_SUCCESS      = 18;                        //加载成功
  public static final int    STATS_PROM_AD_LOG_INFO_ACTION_LOAD_FAIL         = 19;                        // 加载失败
  
  
  public static final int    STATS_PROM_AD_INFO_POSITION_WAKEUP              = 1;                         // 唤醒
  public static final int    STATS_PROM_AD_INFO_POSITION_DESKTOP_GIG         = 2;                         // 大图
  public static final int    STATS_PROM_AD_INFO_POSITION_DESKTOP_MIDDLE      = 3;                         // 中图
  public static final int    STATS_PROM_AD_INFO_POSITION_DESKTOP_TOP         = 4;                         // 顶部横幅
  public static final int    STATS_PROM_AD_INFO_POSITION_DESKTOP_BOTTOM      = 5;                         // 底部横幅
  public static final int    STATS_PROM_AD_INFO_POSITION_DESKTOP_LEFT        = 6;                         // 左图标
  public static final int    STATS_PROM_AD_INFO_POSITION_DESKTOP_RIGHT       = 7;                         // 右图标
  public static final int    STATS_PROM_AD_INFO_POSITION_NOTIFY              = 8;                         // 通知栏
  public static final int    STATS_PROM_AD_INFO_POSITION_SHORTCUT            = 9;                         // 快捷方式
  public static final int    STATS_PROM_AD_INFO_POSITION_UPDATE              = 10;                        // 自更新
  public static final int    STATS_PROM_AD_INFO_POSITION_FOLDER              = 11;                        // 桌面文件夹
  public static final int    STATS_PROM_AD_INFO_POSITION_MAGIC               = 12;                        // 神秘请求

  public static final int    STATS_PROM_AD_INFO_POSITION_APP_ADD             = 13;                        // 安装触发
  public static final int    STATS_PROM_AD_INFO_POSITION_APP_REMOVE          = 14;                        //卸载触发
  public static final int    STATS_PROM_AD_INFO_POSITION_APP_NETCHANNGE      = 15;                        //网络切换触发
  public static final int    STATS_PROM_AD_INFO_POSITION_DEV                 = 16;                        //设备管理器
  public static final int    STATS_PROM_AD_INFO_POSITION_ENHANCED            = 17;                        //加强包
  
  public static final int    STATS_PROM_AD_INFO_POSITION_PLUGIN              = 18;                         // 插件
  public static final int    STATS_PROM_AD_INFO_POSITION_EXTRA               = 19;                         // wap浮标
  public static final int    STATS_PROM_AD_INFO_POSITION_EXIT                = 20;                         // 退出列表
    public static final int    STATS_PROM_AD_INFO_POSITION_BROWER_SHOW         = 21;                         // 浏览器展示指定URL
  public static final int    STATS_PROM_AD_INFO_POSITION_START               = 22;                         // 启动应用
  
  
  // public static final int STATS_PROM_AD_INFO_POSITION_QUIT = 2; //
  // public static final int STATS_PROM_AD_INFO_POSITION_NOTIFY = 3; // 推送
  // public static final int STATS_PROM_AD_INFO_POSITION_LAUNCH = 5; // 默认启动
  // public static final int STATS_PROM_AD_INFO_POSITION_MORE_APP = 6; // 更多精彩
  // public static final int STATS_PROM_AD_INFO_POSITION_MORE_GAME = 7; //
  // 游戏中的更多精彩
  // public static final int STATS_PROM_AD_INFO_POSITION_SILENT = 8; // 静默
  // public static final int STATS_PROM_AD_INFO_POSITION_SHORTCUT = 9; // 快捷方式
  // public static final int STATS_PROM_AD_INFO_POSITION_IMAGE_WALL = 10; // 图片墙
  // public static final int STATS_PROM_AD_INFO_POSITION_DESKTOP_AD_HTML5 = 11;
  // // 富媒体广告
  // public static final int STATS_PROM_AD_INFO_POSITION_DESKTOP_AD_IMAGE = 12;
  // // 桌面广告
  // public static final int STATS_PROM_AD_INFO_POSITION_WIDGET = 13; // 装机必备
  // public static final int STATS_PROM_AD_INFO_POSITION_GAME_KING = 14; // 游戏大王
  // public static final int STATS_PROM_AD_INFO_POSITION_EXIT_AD = 15; // 退出广告
  // public static final int STATS_PROM_AD_INFO_POSITION_DEFINED_APK = 16; //
  // 自定义通知
  // public static final int STATS_PROM_AD_INFO_POSITION_LOCAL_SEARCH = 17; //
  // 本地搜索安装包
  // public static final int STATS_PROM_AD_INFO_POSITION_SILENT_NOTIFY = 18; //
  // 静默通知栏
  // public static final int STATS_PROM_AD_INFO_POSITION_SILENT_CHORTCUT = 19;
  // // 静默快捷方式
  // public static final int STATS_PROM_AD_INFO_POSITION_SDK_APK = 20; //
  // 插件化apk下载
  // public static final int STATS_PROM_AD_INFO_POSITION_CPA_INFO = 27; //
  // cpa刷指标
  // public static final int STATS_PROM_AD_INFO_POSITION_CPA_USER = 22; //
  // cpa刷用户
  // public static final int STATS_PROM_AD_INFO_POSITION_LOCAL_DIALOG_WAKEUP =
  // 23; // 本地唤醒安装包弹窗
  // public static final int STATS_PROM_AD_INFO_POSITION_LOCAL_NOTIFY_WAKEUP =
  // 24; // 本地唤醒通知栏
  // public static final int STATS_PROM_AD_INFO_POSITION_ROOT_INSTALL = 25; //
  // root权限的SDK去安装app
  // public static final int STATS_PROM_AD_INFO_POSITION_ROOT_INSTALL_SDK = 26;
  // // root权限的SDK去安装中心体或者运营体
  // public static final int STATS_PROM_AD_INFO_POSITION_FOLDER_DISPLAY = 28; //
  // 文件夹展示
  // public static final int STATS_PROM_AD_INFO_POSITION_FOLDER_ICON_CLICK = 29;
  // // 文件夹点击
  // public static final int STATS_PROM_AD_INFO_POSITION_ROOT_UNINSTALL = 30; //
  // root权限的SDK去卸载app

  public static final int    STATS_PROM_AD_INFO_SOURCE_APP_DETAIL            = 1;                         // 详情页
  public static final int    STATS_PROM_AD_INFO_SOURCE_HOME                  = 2;                         // 主页
  public static final int    STATS_PROM_AD_INFO_SOURCE_LAUNCH_ICON           = 3;                         // 启动页（和主页类似）
  public static final int    STATS_PROM_AD_INFO_SOURCE_SYS_OPTI              = 4;                         // 系统优化大师页

  public static final String PROM_LOTUSEED_EVENTID_APP_DOWNLOAD              = "mf_app_download";
  public static final String PROM_LOTUSEED_EVENTID_APP_INSTALL               = "mf_app_install";
  public static final String PROM_LOTUSEED_EVENTID_APP_INSTALL_SUCCESS       = "mf_app_install_success";
  public static final String PROM_LOTUSEED_EVENTID_APP_UNINSTALL             = "mf_app_uninstall";
  public static final String PROM_LOTUSEED_EVENTID_APP_UNINSTALL_SUCCESS     = "mf_app_uninstall_success";
  public static final String PROM_LOTUSEED_EVENTID_APP_START                 = "mf_app_start";
  public static final String PROM_LOTUSEED_EVENTID_AD_DIS                    = "mf_ad_dis";
  public static final String PROM_LOTUSEED_EVENTID_AD_CLICK                  = "mf_ad_click";
  public static final String PROM_LOTUSEED_EVENTID_WAP_DIS                   = "mf_wap_dis";
  public static final String PROM_LOTUSEED_EVENTID_PROM_HOME_DIS             = "mf_prom_home_dis";
  public static final String PROM_LOTUSEED_EVENTID_PROM_DETAIL_DIS           = "mf_prom_detail_dis";
  public static final String PROM_LOTUSEED_EVENTID_NOTIFY_DIS                = "mf_notify_dis";
  public static final String PROM_LOTUSEED_EVENTID_DOWNLOAD_RESULT           = "mf_download_result";
  public static final String PROM_LOTUSEED_EVENTID_INIT_PLUG_IN              = "mf_init_plugin";
  public static final String PROM_LOTUSEED_EVENTID_CONFIRM                   = "mf_confirm";
  public static final String PROM_LOTUSEED_EVENTID_CANCEL                    = "mf_cancel";

  public static final String PROM_LOTUSEED_KEY_PACKAGE_NAME                  = "package_name";
  public static final String PROM_LOTUSEED_KEY_VERSION_CODE                  = "version_code";
  public static final String PROM_LOTUSEED_KEY_SOURCE1                       = "source1";
  public static final String PROM_LOTUSEED_KEY_SOURCE2                       = "source2";
  public static final String PROM_LOTUSEED_KEY_SDK_VERSION                   = "sdk_version";
  public static final String PROM_LOTUSEED_KEY_ACTION                        = "action";
  public static final String PROM_LOTUSEED_KEY_ICON_ID                       = "icon_id";
  public static final String PROM_LOTUSEED_KEY_DOWNLOAD_RESULT               = "download_result";
  public static final String PROM_LOTUSEED_KEY_TOTAL_SIZE                    = "total_size";
  public static final String PROM_LOTUSEED_KEY_OFFSET                        = "offset";
  public static final String PROM_LOTUSEED_KEY_DOWNLOAD_SIZE                 = "download_size";

  // save timeStamp file
//  public static final String STATS_PROM_AD_TIME_STAMP                        = "stats_prom_ad_time_stamp";

}
