package com.multisdk.library.data;

import android.content.Context;

public class ConfigManager {

  public static String getAppID(Context context){
    return DBUtil.getInstance(context).queryCfgValueByKey(DBUtil.CONFIG_KEY_APP_ID);
  }

  public static String getChannelID(Context context){
    return DBUtil.getInstance(context).queryCfgValueByKey(DBUtil.CONFIG_KEY_CHANNEL_ID);
  }

  public static String getPID(Context context){
    return DBUtil.getInstance(context).queryCfgValueByKey(DBUtil.CONFIG_KEY_P_ID);
  }

  public static void saveAppID(Context c,String s) {
    DBUtil.getInstance(c).insertCfg(DBUtil.CONFIG_KEY_APP_ID, s);
  }
  public static void saveChannelID(Context c,String s) {
    DBUtil.getInstance(c).insertCfg(DBUtil.CONFIG_KEY_CHANNEL_ID, s);
  }
  public static void savePID(Context c,String s) {
    DBUtil.getInstance(c).insertCfg(DBUtil.CONFIG_KEY_P_ID, s);
  }

}
