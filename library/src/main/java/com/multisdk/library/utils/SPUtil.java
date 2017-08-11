package com.multisdk.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtil {

  private static SharedPreferences getSP(Context context){
    return context.getSharedPreferences(SPConfig.SP_NAME,Context.MODE_PRIVATE);
  }

  public static void saveConfig4Int(Context context, String key, int val) {
    SharedPreferences spQY = getSP(context);
    Editor editor = spQY.edit();
    editor.putInt(key, val);
    editor.commit();
  }

  public static void saveConfig4String(Context context, String key, String val) {
    SharedPreferences spQY = getSP(context);
    Editor editor = spQY.edit();
    editor.putString(key, val);
    editor.commit();
  }

  public static int getConfig4Int(Context context, String key) {
    SharedPreferences spQY = getSP(context);
    return spQY.getInt(key, 0);
  }

  public static String getConfig4String(Context context, String key) {
    SharedPreferences spQY = getSP(context);
    return spQY.getString(key, "");
  }

  private static class SPConfig{

    static final String SP_NAME = "multi_sdk_sp_name";
    static final String SP_LAST_INIT_TIME = "multi_sdk_sp_last_init_time";
    static final String SP_SDK_VERSION_NAME = "multi_sdk_sp_sdk_version_name";

  }
}
