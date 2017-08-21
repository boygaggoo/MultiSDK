package com.multisdk.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtil {

  private static SharedPreferences getSP(Context context){
    return context.getSharedPreferences(SPConfig.SP_NAME,Context.MODE_PRIVATE);
  }

  public static void saveInt(Context context, String type, String key, int val){
    saveConfig4Int(context, type + key, val);
  }

  public static void saveString(Context context, String type, String key, String val){
    saveConfig4String(context,type + key,val);
  }

  public static int getInt(Context context, String type, String key){
    return getConfig4Int(context, type + key);
  }

  public static String getString(Context context, String type, String key){
    return getConfig4String(context, type + key);
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

  public static long getConfig4Long(Context context,String key){
    return getSP(context).getLong(key,0L);
  }

  public static void saveConfig4Long(Context context,String key,long value){
    getSP(context).edit().putLong(key, value).apply();
  }

  private static class SPConfig{

    static final String SP_NAME = "multi_sdk_sp_name";

  }
}
