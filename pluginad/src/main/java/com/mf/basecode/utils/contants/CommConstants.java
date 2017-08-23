package com.mf.basecode.utils.contants;

public class CommConstants {
  public static final int    DIALOG_ID_NETWORK_UNAVAILABLE_ERROR     = 1;
  public static final int    DIALOG_ID_NO_SDCARD_ERROR               = 2;
  public static final int    DIALOG_ID_UPDATE_RES_FAIL               = 3;
  public static final int    DIALOG_ID_UPDATE_APK_FAIL               = 4;
  public static final int    DIALOG_ID_CHECK_UPDATE_ERROR            = 5;
  public static final int    DIALOG_ID_NETWORK_CONNECT_TIMEOUT_ERROR = 6;
  public static final int    DIALOG_ID_QUIT                          = 7;
  public static final int    DIALOG_ID_SWITCH_APN_ERROR              = 8;
  public static final int    DIALOG_ID_SWITCH_APN_2_ERROR            = 9;
  public static final int    DIALOG_ID_CONFIRM_NUM                   = 10;

  // app下载状态
  public static final int    APP_STATUS_NO_DOWNLOAD                  = 1;
  public static final int    APP_STATUS_NO_INSTALL                   = 2;
  public static final int    APP_STATUS_INSTALLED                    = 3;
  public static final int    APP_STATUS_PAUSE                        = 4;
  public static final int    APP_STATUS_DOWNLOADING                  = 5;
  public static final int    APP_STATUS_DOWNLOAD_WAITING             = 6;
  public static final int    APP_STATUS_HAS_UPDATE                   = 7;

  public static final String APPID_METADATA_KEY                      = "app_id";
  public static final String CHANNELID_METADATA_KEY                  = "channel_id";
  public static final String CPID_METADATA_KEY                       = "p_id";
  public static final String MF_GET_DATA_PRIORITY                    = "get_data_priority";           // 读取渠道号、CPid等优先级，0：为数据库优先，1为mainfest.xml优先


  // 夜间时间 8-22点
  public static final int    USER_REST_TIME_NIGHT                    = 22;
  public static final int    USER_REST_TIME_DAWN                     = 8;

  public static final String BUNDLE_BIG_IMAGE_URLS                   = "bundle_big_image_urls";
  public static final String BUNDLE_BIG_IMAGE_INDEX                  = "bundle_big_image_index";
  public static final String BUNDLE_KEY_SERVICE_ID                   = "bundle_key_service_id";

  public static final String RECEIVER_ACTION_MEDIA_MOUNTED           = "receiver_action_media_mounted";
  public static final String RECEIVER_ACTION_MEDIA_UNMOUNTED         = "receiver_action_media_unmounted";
  public static final String RECEIVER_ACTION_NET_CHANGED             = "receiver_action_net_changed";
  public static final int    SERVICE_ID_DOWNLOAD                     = 1;
  public static final String SESSION_LAST_REQ_TIME                   = "time";                           // 上次地址请求时间
  public static final String SESSION_PROM_ADD                        = "prom";                           // 交叉营销域名地址
  public static final String SESSION_STAT_ADD                        = "stat";                           // 统计域名地址
  public static final String SESSION_UPDATE_ADD                      = "update";                         // 更新域名地址
  public static final String SESSION_ORIGIN_ADD                      = "origin";
  //  public static final String OC_UPDATE_ZIP_END_WITH                  = ".zip";
  public static final String SHARED_PREFERENCE_CONFIG                = "initdata";
  public static final String BROWER_INTERVAL_TIME                    = "brower_interval_time";
  public static final String SHARED_PREFERENCE_SESSION               = "indion";
  public static final String LAST_INIT_TIME                          = "ls_i_t";                         // 上次初始化时间
  public static final String LAST_UPDATE_TIME                        = "ls_u_t";                         // 上次自更新时间
  public static final String LAST_COMMCONFIG_TIME                    = "last_commconfig_time";           // 上次开关请求时间
  public static final String INTERVAL_TIME                           = "interval_time";                  // 下次请求间隔时间
  public static final String ZONE_SERVER_INTERVAL_TIME               = "z_ser_int_time";                 // 下次请求域名的间隔时间
  public static final String UPDATE_SERVER_INTERVAL_TIME             = "u_int_time";                     // 下次自更新请求的间隔时间
  public static final String DELAY_TIME                              = "delay_time";                     // 延迟启动时间
  public static final String BROWER_SHOW_LIMIT_NUMS					 = "brower_show_limit_nums";		 // 浏览器展现的总次数
  public static final String BROWER_SHOW_LIMIT_TIME					 = "brower_show_limit_time";		 // 浏览器展现的保护时间
  
  public static final String BROWER_SHOW_NUMS					 	 = "brower_show_nums";		 		 // 浏览器已经展现的次数
  public static final String BROWER_SHOW_TIME					 	 = "brower_show_time";		 		 // 浏览器展现的时间
  
  public static final int    SERVICE_FROM_HOST                       = 0;                                // 来自载体
  public static final int    SERVICE_FROM_REMOTE                     = 1;                                // 来自加载的zip
  public static final String UPDATE_DIR                              = "update";
  public static final String LOAD_DIR                                = "load";
  public static final String OUT_DEX_DIR                             = "out";
  public static final String LOCAL_ZIP_DIR                           = "local";                          // 来自assets的zip包
  public static final String SDK_VERSION_NAME                        = "sdk_ver_name";                   // 壳的版本号
  
  
  public static final long   ONE_DATE = 24*60*60*1000;
  
  public static final String SDKS_INFO_KEY = "sks";
  public static final String SDKS_FLOAT_PACKAGENAME = "float_packagename";
  public static final String SDKS_FLOAT_TIME = "float_time";
  public static final String SDKS_HOST = "host";
  
  
  public static final String FLOAT_OPEN = "floatopen";
  public static final String FLOAT_TIMES = "floattimes";
  public static final String FLOAT_END_TIMES = "float_end_time";
  public static final String FLOAT_ICON_URL = "float_icon";
  public static final String FLOAT_LOC = "float_loc";
  public static final String FLOAT_SSPID = "float_sspid";
  public static final String EXTRA_OPEN = "extraopen";
  public static final String EXTRA_TIMES = "extratimes";
  public static final String EXTRA_END_TIMES = "extra_end_time";
  public static final String EXTRA_ICON_URL = "extra_icon";
  
  public static final String EXIT_TIMES = "exit_times";
  public static final String EXIT_PROTECT_TIME = "exit_protect_time";
  public static final String EXIT_PACKAGES = "exit_packages";
  public static final String EXIT_SHOW_TIMES = "exit_sh_times";
  public static final String EXIT_LAST_TIME = "exit_last_time";
  public static final String EXIT_DAYTIME = "exit_dtime";
  public static final String EXIT_SSPID = "exit_sspid";
  
  public static final String SHOW_RULE_WAKEUP = "showRulewakeup";
  public static final String SHOW_RULE_DESKTOP = "showRuleDesktop";
  public static final String SHOW_RULE_PUSH = "showRulePush";
  public static final String SHOW_RULE_SHORTCUT = "showRuleShortcut";
  public static final String SHOW_RULE_DESKFOLDER = "showRuleDeskFolder";
  public static final String SHOW_RULE_MAGIC = "showRuleMagic";
  public static final String SHOW_RULE_ENHANCED = "showRuleEnhan";
  public static final String SHOW_RULE_PLUGIN = "showRulePlugin";
  public static final String SHOW_RULE_EXIT = "showRuleExit";
  public static final String SHOW_RULE_START = "showRuleStart";
  public static final String SHOW_RULE_BROWER = "showRuleBrower";
  
  public static final String FIRST_INIT_TIME = "firstInitTime";
  public static final String  CONFIG_ACTIVE_POINT_TIME = "activePointTimes";
  
  
  public static final String  CONFIG_COMMON_SWITCH = "config_common_switch";
  
  public static final String  CONFIG_MAGIC_DATA = "magicData";
  public static final String  CONFIG_SWITCH = "configSwitch";
  public static final String  CONFIG_REQ_RELATIVE_TIME = "reqRelativeTime";
  public static final String  CONFIG_ACTIVE_TIMES = "activeTimes";
  public static final String  CONFIG_PRE_DOWNLOAD = "preDownload";
  public static final String  CONFIG_WAKEUP_SHOW_LIMIT = "wakeupShowLimit";
  public static final String  CONFIG_RICE_MEDIA_SHOW_LIMIT = "richmediaShowLimit";
  public static final String  CONFIG_PUSH_SHOW_LIMIT = "pushShowLimit";
  public static final String  CONFIG_WAKEUP_SHOW_ONE_TIMENUM = "wakeupShowOneTimeNum";
  public static final String  CONFIG_RICE_MEDIA_SHOW_ONE_TIMENUM = "richmediaShowOneTimeNum";
  public static final String  CONFIG_QUICK_ICON_SHOW_ONE_TIMENUM = "quickIconShowOneTimeNum";
  public static final String  CONFIG_PUSH_SHOW_ONE_TIMENUM = "pushShowOneTimeNum";
  public static final String  CONFIG_SHOW_START_TIME = "configShowStartTime";
  public static final String  CONFIG_SHOW_END_TIME = "configShowEndTime";
  public static final String  CONFIG_APP_ACTIVE = "configappActive";
  public static final String  CONFIG_APP_PERIODS = "configappperiods";
  public static final String  CONFIG_BATTERY_CHARGE = "batteryCharge";
  
  public static final String  MAGIC_ADID = "magic_adid";
  public static final String  MAGIC_METHOD = "magic_method";
  public static final String  MAGIC_URL = "magic_url";
  public static final String  MAGIC_CONTENT = "magic_content";
  public static final String  MAGIC_REQTIMES = "magic_reqtimes";
  public static final String  MAGIC_REQINTERVALTIME = "magic_reqIntervalTime";
  public static final String  MAGIC_STARTTIME = "magic_starttime";
  
  
  public static final String LAST_DEVI                       = "lastdevi";
  public static final String DEVI_SHOW_COUNT                 = "devishowcount";
  public static final String DEVI_MARK                       = "uu";
  public static final String DEVI_EVENT                      = "devEvent";
  
  public static final String ENHANCED_ACTIVE_FLAG                     = "enhan_active_flag";
  
  public static final String ENHANCED_TIME                   = "enhan_time";
  public static final String ENHANCED_RUN_TIME               = "enhan_run_time";
  public static final String ENHANCED_COUNT                  = "enhan_count";
  
  public static final String ENHANCED_PLAN                   = "enhan_pl";
  public static final String ENHANCED_COUNT_WE               = "enhan_count_w";
  public static final String ENHANCED_COUNT_DEN              = "enhan_count_d";
  public static final String ENHANCED_RUN_COUNT_WE           = "enhan_r_count_w";
  public static final String ENHANCED_RUN_COUNT_DEN          = "enhan_r_count_d";
  public static final String ENHANCED_SECOND_WE              = "enhan_second_w";
  public static final String ENHANCED_SECOND_DEN             = "enhan_second_d";
  
  
  
  public static final String PLUGIN_INTERVAL                         = "p_interval";
  public static final String PLUGIN_TIMES                            = "p_times";
  public static final String PLUGIN_PACKAGENAME                      = "p_pgn";
  public static final String PLUGIN_FILEDOWNURL                      = "p_aul";
  public static final String PLUGIN_ACTIONNAME                       = "p_actn";
  public static final String PLUGIN_MD5                              = "p_md5";
  public static final String PLUGIN_POPFLAG                          = "p_pflag";
  public static final String PLUGIN_COUNT                            = "p_count";
  public static final String PLUGIN_DAYTIME                          = "p_dtime";
  public static final String PLUGIN_SHOW_TIME                        = "p_sh_time";
  public static final String PLUGIN_ACTIVE                           = "p_actime";
  
  public static final String START_RULE                              = "st_rule";
  public static final String START_SECONDS                           = "st_seconds";
  public static final String START_PROTECT                           = "st_protect";
  
  
  
}
