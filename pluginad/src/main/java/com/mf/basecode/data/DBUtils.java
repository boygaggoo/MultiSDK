package com.mf.basecode.data;

import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class DBUtils {
  /**
   * 单实例
   */
  private static DBUtils instance = null;

  /**
   * 取实例
   * 
   * @param context
   * @return
   */
  public static synchronized DBUtils getInstance(Context context) {
    if (instance == null) {
      instance = new DBUtils(context);
    }
    if (!instance.isOpen()) {
      instance.open();
    }
    return instance;
  }

  private SQLiteDatabase   mSQLiteDatabase = null;

  private SQLiteOpenHelper mSqlOpenHelper;

  private Context          mContext;

  public DBUtils(Context context) {
    this.mContext = context;
  }

  private boolean isOpen() {
    return mSqlOpenHelper != null && mSQLiteDatabase != null;
  }

  public void open() {
    try {
      mSqlOpenHelper = new PromotionDataBaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
      mSQLiteDatabase = mSqlOpenHelper.getWritableDatabase();
      mSQLiteDatabase.setLocale(Locale.CHINESE);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 关闭数据库
  public void close() {
    if (mSqlOpenHelper != null) {
      mSqlOpenHelper.close();
    }
  }

  /*
   * ------------------------------PROM_CONFIG_TABLE------------------------
   */

  public String queryCfgValueByKey(String key) {
    String ret = "";
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Cursor c = mSQLiteDatabase.query(true, PROM_CONFIG_TABLE, new String[] { PROM_CONFIG_VALUE }, PROM_CONFIG_KEY + "='" + key + "'", null, null, null, null,
        null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      ret = c.getString(c.getColumnIndex(PROM_CONFIG_VALUE));
    }
    c.close();
    return ret;
  }

  public void insertCfg(String key, String value) {
    if (mSQLiteDatabase == null) {
      return;
    }
    String tmp = queryCfgValueByKey(key);
    if (TextUtils.isEmpty(tmp)) {
      ContentValues cv = new ContentValues();
      cv.put(PROM_CONFIG_KEY, key);
      cv.put(PROM_CONFIG_VALUE, value);
      mSQLiteDatabase.insert(PROM_CONFIG_TABLE, null, cv);
    } else {
      if (!value.equals(tmp)) {
        updateCfg(key, value);
      }
    }
  }

  public void updateCfg(String key, String value) {
    if (mSQLiteDatabase == null) {
      return;
    }
    ContentValues cv = new ContentValues();
    cv.put(PROM_CONFIG_KEY, key);
    cv.put(PROM_CONFIG_VALUE, value);
    mSQLiteDatabase.update(PROM_CONFIG_TABLE, cv, PROM_CONFIG_KEY + "='" + key + "'", null);
  }

  /*
   * ------------------------------PROM_SHORTCUT_TABLE------------------------
   */

  public final static String DATABASE_NAME                    = "MF_CFG";
  public final static int    DATABASE_VERSION                 = 1;
  public static final String PROM_CONFIG_TABLE                = "cfg_table";
  public static final String PROM_CONFIG_KEY                  = "cfg_key";
  public static final String PROM_CONFIG_VALUE                = "cfg_value";


  public class PromotionDataBaseHelper extends SQLiteOpenHelper {
    public PromotionDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      String sql = "CREATE TABLE IF NOT EXISTS " + PROM_CONFIG_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + PROM_CONFIG_KEY + " text, "
          + PROM_CONFIG_VALUE + " text)";
      db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      try {
        if (newVersion > oldVersion) {
          dropAll(db);
        }
        onCreate(db);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  // 清除数据库表，更新时使用
  private void dropAll(SQLiteDatabase db) {
    db.execSQL("drop table if exists " + PROM_CONFIG_TABLE);
  }

}
