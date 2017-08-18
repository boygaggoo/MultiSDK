package com.multisdk.library.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import com.multisdk.library.network.obj.InitInfo;
import java.util.ArrayList;
import java.util.Locale;

public class DBUtil {

  private Context mContext;

  private static volatile DBUtil sInst = null;
  public static DBUtil getInstance(Context context){
    DBUtil inst = sInst;
    if (inst == null){
      synchronized (DBUtil.class){
        inst = sInst;
        if (inst == null){
          inst = new DBUtil(context);
          sInst = inst;
        }
      }
    }
    if (!sInst.isOpen()){
      sInst.openDB();
    }
    return inst;
  }

  private DBUtil(Context context){
    mContext = context.getApplicationContext();
  }

  private SQLiteOpenHelper mDBHelper = null;
  private SQLiteDatabase mDB = null;

  private void openDB(){
    mDBHelper = new DBHelper(mContext,DB_NAME,null,DB_VERSION);
    mDB = mDBHelper.getWritableDatabase();
    mDB.setLocale(Locale.CHINESE);
  }

  private boolean isOpen(){
    return mDBHelper != null && mDB != null;
  }

// config start
  public String queryCfgValueByKey(String key) {
    String ret = "";
    if (mDB == null) {
      return ret;
    }
    Cursor c = mDB.query(true, CONFIG_TABLE, new String[] { CONFIG_VALUE }, CONFIG_KEY + "='" + key + "'", null, null, null, null,
        null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      ret = c.getString(c.getColumnIndex(CONFIG_VALUE));
    }
    c.close();
    return ret;
  }

  public void insertCfg(String key, String value) {
    Log.e("DBUtils", "key:" + key + " value: " + value);
    if (mDB == null) {
      Log.e("DBUtils", " mSQLiteDatabase is null.");
      return;
    }
    String tmp = queryCfgValueByKey(key);
    Log.e("DBUtils", " tmp: " + tmp);
    if (TextUtils.isEmpty(tmp)) {
      ContentValues cv = new ContentValues();
      cv.put(CONFIG_KEY, key);
      cv.put(CONFIG_VALUE, value);
      mDB.insert(CONFIG_TABLE, null, cv);
    } else {
      if (!value.equals(tmp)) {
        updateCfg(key, value);
      }
    }
  }

  public void updateCfg(String key, String value) {
    if (mDB == null) {
      return;
    }
    ContentValues cv = new ContentValues();
    cv.put(CONFIG_KEY, key);
    cv.put(CONFIG_VALUE, value);
    mDB.update(CONFIG_TABLE, cv, CONFIG_KEY + "='" + key + "'", null);
  }

  public static final String CONFIG_KEY_APP_ID = "config_key_app_id";
  public static final String CONFIG_KEY_CHANNEL_ID = "config_key_channel_id";
  public static final String CONFIG_KEY_P_ID = "config_key_p_id";
// config end.

  private static final String DB_NAME = "multi.db";
  private static final int DB_VERSION = 1;

  private static final String CONFIG_TABLE = "multi_sdk_config_table";
  private static final String CONFIG_KEY = "multi_sdk_config_key";
  private static final String CONFIG_VALUE = "multi_sdk_config_value";

  private class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
        int version) {
      super(context, name, factory, version);
    }

    @Override public void onCreate(SQLiteDatabase db) {

      String sql = "CREATE TABLE IF NOT EXISTS "
          + CONFIG_TABLE
          + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
          + CONFIG_KEY
          + " text, "
          + CONFIG_VALUE
          + " text)";
      db.execSQL(sql);

      //String init = "CRETE TABLE IF NOT EXISTS " + INIT_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
      //    + INIT_TYPE + " INTEGER, "
      //    + INIT_SW + " INTEGER, "
      //    + INIT_RT + " INTEGER, "
      //    + INIT_AT + " INTEGER, "
      //    + INIT_UP + " INTEGER, "
      //    + INIT_V + " INTEGER, "
      //    + INIT_DL + " text, "
      //    + INIT_M5 + " text) ";
      //db.execSQL(init);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      try {
        if (newVersion > oldVersion) {
          db.execSQL("drop table if exists " + CONFIG_TABLE);
          //db.execSQL("drop table if exists " + INIT_TABLE);
        }
        onCreate(db);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
