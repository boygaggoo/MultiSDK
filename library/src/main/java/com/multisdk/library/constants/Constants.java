package com.multisdk.library.constants;

public class Constants {

  public static class Intent{
    public static final String INIT_KEY = "init";
    public static final int INIT_VALUE = 1;

    public static final String PAY_KEY = "pay";
    public static final int PAY_VALUE = 2;
  }

  public static class Plugin{
    public static final String PLUGIN_AD_PACKAGE_NAME = "com.plugin.ad";
    public static final String PLUGIN_PAY_PACKAGE_NAME = "com.plugin.pay";
  }

  public static class INIT{

    public static final String INIT_TIME = "init_time";

    public static final String TYPE_AD = "ad";
    public static final String TYPE_PAY = "pay";
    public static final String INIT_TYPE = "i_type";
    public static final String INIT_SW= "i_sw";
    public static final String INIT_RT= "i_rt";
    public static final String INIT_AT= "i_at";
    public static final String INIT_UP= "i_up";
    public static final String INIT_V= "i_v";
    public static final String INIT_DL = "i_dl";
    public static final String INIT_M5 = "i_m5";
  }

  public static class DIR{
    public static final String SP = "dir_sp";
    public static final String UPDATE_AD = "update_ad";
    public static final String UPDATE_PAY = "update_pay";
    public static final String LOAD_AD = "load_ad";
    public static final String LOAD_PAY = "load_pay";
    public static final String PUBLIC_AD = "public_ad";
    public static final String PUBLIC_PAY = "public_pay";
  }

  public static class FILE_NAME{
    public static final String AD_LOAD_APK_NAME = "ad_load_apk_name";
    public static final String PAY_LOAD_APK_NAME = "pay_load_apk_name";
    public static final String AD_UP_APK_NAME = "ad_up_apk_name";
    public static final String PAY_UP_APK_NAME = "pay_up_apk_name";
  }

  public static class FILE_TYPE{
    public static final String APK = ".apk";
  }

  public static class UPDATE{
    public static final String SP = "update_sp";
    public static final String LAST_UPDATE_TIME = "last_update_time";
  }
}
