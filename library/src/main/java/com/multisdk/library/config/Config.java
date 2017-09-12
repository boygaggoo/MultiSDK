package com.multisdk.library.config;

public class Config {

  public static final String SDK_VERSION_NAME = "1.0.0.0";
  public static final int SDK_VERSION_CODE = 1000;

  public static final String URL = "";

  public static final String AD_NAME_ASSETS = "xxxxxxxxx";
  public static final String AD_PASS_ASSETS = "xxxxxxxxx";

  public static final String PAY_NAME_ASSETS = "";
  public static final String PAY_PASS_ASSETS = "";
  public static final String PAY_CLASS = "com.s.y.i.MPay";
  public static final String PAY_CLASS_INIT = "init";
  public static final String PAY_CLASS_PAY = "pay";

  public static final String AD_PACKAGE_NAME = "com.mf";
  public static final String AD_CLASS = "com.mf.promotion.service.MFApkService";
  public static final String AD_SERVICE_ID = "prom_service_id_apk";
  public static final String AD_APP_ID = "ad_app_id";
  public static final String AD_CHANNEL_ID = "ad_channel_id";
  public static final String AD_CP_ID = "ad_cp_id";

  public static boolean isDebug(){
    return true;
  }
}
