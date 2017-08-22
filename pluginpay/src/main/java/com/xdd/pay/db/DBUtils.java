package com.xdd.pay.db;

import java.util.ArrayList;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.network.object.LogInfo;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.QYLog;

public class DBUtils {

  /*
   * 单实例
   */
  public static DBUtils    instance        = null;

  private Context          mContext;

  private SQLiteDatabase   mSQLiteDatabase = null;

  private SQLiteOpenHelper mSqlOpenHelper;

  public DBUtils(Context context) {
    this.mContext = context;
  }

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

  private boolean isOpen() {
    return mSQLiteDatabase != null && mSqlOpenHelper != null;
  }

  private void open() {
    try {
      mSqlOpenHelper = new PromotionDataBaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
      mSQLiteDatabase = mSqlOpenHelper.getWritableDatabase();
      mSQLiteDatabase.setLocale(Locale.CHINESE);
    } catch (Exception e) {
       QYLog.d(e.toString());
      e.printStackTrace();
    }
  }

  private String DATABASE_NAME    = EncryptUtils.decode(CommConstant.VI_DB_PAY_TAG);
  private int    DATABASE_VERSION = 1;

  public class PromotionDataBaseHelper extends SQLiteOpenHelper {
    public PromotionDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CONFIG + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + CONFIG_KEY + " text, " + CONFIG_VALUE
          + " text)";
      db.execSQL(sql);

      sql = "CREATE TABLE IF NOT EXISTS " + TABLE_LOG + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + LOG_RESULT + " INTEGER, " + LOG_TYPE + " INTEGER, "
          + LOG_PAY_TYPE + " INTEGER, " + LOG_SDK_TYPE + " INTEGER, " + LOG_PAY_ID + " text, " + LOG_PAY_CODE + " text, " + LOG_LOCAL_TIME + " text, "
          + LOG_PRICE + " INTEGER)";
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

    // 清除数据库表，更新时使用
    private void dropAll(SQLiteDatabase db) {
      // db.execSQL("drop table if exists " + TABLE_LOG);
    }
  }

  /*
   * ----------------------------COMMON----------------------------------
   */

  /**
   * 获取表中最后一条数据的_id
   * 
   * @return
   */
  private int queryLastId(String tableName) {
    int ret = 0;
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Cursor c = null;
    try {
      c = mSQLiteDatabase.query(tableName, new String[] { "_id" }, null, null, null, null, null);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (c != null) {
      if (c.getCount() > 0) {
        c.moveToLast();
        ret = c.getInt(c.getColumnIndex("_id"));
      }
      c.close();
    }
    return ret;
  }

  /**
   * 删除当前表中的所有数据
   * 
   * @param tableName
   */
  private void deleteAll(String tableName) {
    if (mSQLiteDatabase == null) {
      return;
    }
    int lastId = queryLastId(tableName);
    if (lastId > 0) {
      mSQLiteDatabase.delete(tableName, "_id > 0 and _id <= " + lastId, null);
    }
  }

  /*
   * ----------------------------TABLE_LOG----------------------------------
   */
  private String TABLE_LOG      = EncryptUtils.decode(CommConstant.TABLE_LOG_TAG);     // 计费信息

  private String LOG_RESULT     = EncryptUtils.decode(CommConstant.LOG_RESULT_TAG);    // 结果
  private String LOG_TYPE       = EncryptUtils.decode(CommConstant.LOG_TYPE_TAG);
  private String LOG_SDK_TYPE   = EncryptUtils.decode(CommConstant.LOG_SDK_TYPE_TAG);
  private String LOG_PAY_TYPE   = EncryptUtils.decode(CommConstant.LOG_PAY_TYPE_TAG);
  private String LOG_PAY_CODE   = EncryptUtils.decode(CommConstant.LOG_PAYCODE_TAG);   // 计费代码
  private String LOG_PAY_ID     = EncryptUtils.decode(CommConstant.LOG_PAY_ID_TAG);
  private String LOG_PRICE      = EncryptUtils.decode(CommConstant.LOG_PRICE_TAG);     // 价格
  private String LOG_LOCAL_TIME = EncryptUtils.decode(CommConstant.LOG_LOCAL_TIME_TAG); // 本地时间

  /*
   * 数据插入数据库
   */
  public void insertLog(LogInfo data) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(LOG_RESULT, data.getResult());
    initialValues.put(LOG_PAY_CODE, data.getPayCode());
    initialValues.put(LOG_PRICE, data.getPrice());
    initialValues.put(LOG_LOCAL_TIME, data.getLocalTime());
    initialValues.put(LOG_TYPE, data.getType());
    initialValues.put(LOG_PAY_ID, data.getPayId());
    initialValues.put(LOG_SDK_TYPE, data.getSdkType());
    initialValues.put(LOG_PAY_TYPE, data.getPayType());
    mSQLiteDatabase.insert(TABLE_LOG, null, initialValues);
  }

  /**
   * 获取所有log信息
   * 
   * @return
   */
  public ArrayList<LogInfo> queryAllLog() {
    ArrayList<LogInfo> ret = new ArrayList<LogInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Cursor c = mSQLiteDatabase.query(true, TABLE_LOG, new String[] { LOG_RESULT, LOG_TYPE, LOG_SDK_TYPE, LOG_PAY_TYPE, LOG_PAY_ID, LOG_PAY_CODE, LOG_PRICE,
        LOG_LOCAL_TIME }, null, null, null, null, null, null);
    if (c.getCount() > 0) {
      for (c.moveToFirst(); c.isAfterLast(); c.moveToNext()) {
        LogInfo data = new LogInfo();
        data.setResult((byte) c.getInt(c.getColumnIndex(LOG_RESULT)));
        data.setPayCode(c.getString(c.getColumnIndex(LOG_PAY_CODE)));
        data.setPrice(c.getInt(c.getColumnIndex(LOG_PRICE)));
        data.setLocalTime(c.getString(c.getColumnIndex(LOG_LOCAL_TIME)));
        data.setType((byte) c.getInt(c.getColumnIndex(LOG_TYPE)));
        data.setPayType((byte) c.getInt(c.getColumnIndex(LOG_SDK_TYPE)));
        data.setSdkType((byte) c.getInt(c.getColumnIndex(LOG_PAY_TYPE)));
        data.setPayId(c.getString(c.getColumnIndex(LOG_PAY_ID)));
        ret.add(data);
      }
    }
    c.close();
    return ret;
  }

  /**
   * 删除所有log
   */
  public void deleteAllLog() {
    deleteAll(TABLE_LOG);
  }

  /*
   * ------------------------------TABLE_CONFIG------------------------
   */
  public static final String TABLE_CONFIG = EncryptUtils.decode(CommConstant.TABLE_CFG_TAG);
  public static final String CONFIG_KEY   = EncryptUtils.decode(CommConstant.CFG_KEY_TAG);
  public static final String CONFIG_VALUE = EncryptUtils.decode(CommConstant.CFG_VALUE_TAG);

  public String queryCfgValueByKey(String key) {
    String ret = "";
    if (mSQLiteDatabase == null) {
//        QYLog.d("---queryCfgValueByKey-------mSQLiteDatabase == null-----"+key);
      return ret;
    }
    Cursor c = mSQLiteDatabase.query(true, TABLE_CONFIG, new String[] { CONFIG_VALUE }, CONFIG_KEY + "='" + key + "'", null, null, null, null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      ret = c.getString(c.getColumnIndex(CONFIG_VALUE));
    }
    c.close();
    return ret;
  }

  public void addCfg(String key, String value) {
    if (mSQLiteDatabase == null) {
//        QYLog.d("---addCfg-------mSQLiteDatabase == null-key----"+key);
//        QYLog.d("---addCfg-------mSQLiteDatabase == null-value----"+value);
      return;
    }
    String tmp = queryCfgValueByKey(key);
    if (TextUtils.isEmpty(tmp)) {
      ContentValues cv = new ContentValues();
      cv.put(CONFIG_KEY, key);
      cv.put(CONFIG_VALUE, value);
      mSQLiteDatabase.insert(TABLE_CONFIG, null, cv);
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
    cv.put(CONFIG_KEY, key);
    cv.put(CONFIG_VALUE, value);
    mSQLiteDatabase.update(TABLE_CONFIG, cv, CONFIG_KEY + "='" + key + "'", null);
  }

}
