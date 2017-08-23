/**   
 * @Title: PromStatsDBUtil.java 
 * @Package sdk.com.Joyreach.promStats.data 
 * @Description:  
 * @author Abner.Zhou   
 * @date 2012-7-18 上午10:44:52 
 * @version V1.0   
 */
package com.mf.statistics.prom.data;

import java.util.ArrayList;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.mf.basecode.network.object.AdLogInfo;

public class StatsPromDBUtils {
  public final static String      DATABASE_NAME                  = "MF_STATS";
  public final static int         DATABASE_VERSION               = 1;
  public static final String      PROM_STATISTICS_TABLE          = "stats_adlog_table";

  // columns' name statistics
  public static final String      PROM_STATS_ACTION              = "stats_action";
  public static final String      PROM_STATS_ADTAG               = "stats_adTag";
  public static final String      PROM_STATS_NUM                 = "stats_num";
  public static final String      PROM_STATS_SOURCE1             = "stats_source1";
  public static final String      PROM_STATS_RESERVED1           = "stats_reserved1";
  public static final String      PROM_STATS_RESERVED2           = "stats_reserved2";
  public static final String      PROM_STATS_RESERVED3           = "stats_reserved3";
  public static final String      PROM_STATS_RESERVED4           = "stats_reserved4";
  

  /**
   * 单实例
   */
  private static StatsPromDBUtils instance                       = null;

  private SQLiteDatabase          mSQLiteDatabase                = null;

  private SQLiteOpenHelper        mSqlOpenHelper;

  private Context                 mContext;

  private StatsPromDBUtils(Context context) {
    this.mContext = context;
  }

  /**
   * 取实例
   * 
   * @param context
   * @return
   */
  public static synchronized StatsPromDBUtils getInstance(Context context) {
    if (instance == null) {
      instance = new StatsPromDBUtils(context);
    }
    if (!instance.isOpen()) {
      instance.open();
    }
    return instance;
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

  /* 插入一条数据 */
  public void insertAdLogInfo(AdLogInfo info) {
    if (mSQLiteDatabase == null) {
      return;
    }
    ContentValues initialValues = new ContentValues();
    initialValues.put(PROM_STATS_ACTION, info.getAction());
    initialValues.put(PROM_STATS_ADTAG, info.getAdTag());
    initialValues.put(PROM_STATS_NUM, info.getNum());
    initialValues.put(PROM_STATS_SOURCE1, info.getSource1());
    initialValues.put(PROM_STATS_RESERVED1, info.getReserved1());
    initialValues.put(PROM_STATS_RESERVED1, info.getReserved2());
    initialValues.put(PROM_STATS_RESERVED1, info.getReserved3());
    initialValues.put(PROM_STATS_RESERVED1, info.getReserved4());
    try {
      mSQLiteDatabase.insert(PROM_STATISTICS_TABLE, null, initialValues);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * 添加广告信息
   */
  public void addAdLogInfo(AdLogInfo info) {
    if (mSQLiteDatabase == null) {
      return;
    }
    AdLogInfo sta = queryAdLogInfo(info);

    if (sta != null) {
      sta.setNum(sta.getNum() + info.getNum());
      updateAdLogInfo(sta);
    } else {
      insertAdLogInfo(info);
    }
  }

  /* 删除一条数据 */
  public boolean deleteAdLogInfo(AdLogInfo info) {
    boolean ret = false;
    if (mSQLiteDatabase == null) {
      return ret;
    }
    try {
      ret = mSQLiteDatabase.delete(PROM_STATISTICS_TABLE,
          PROM_STATS_ACTION + "='" + info.getAction() + "' AND " + PROM_STATS_ADTAG + "='" + info.getAdTag() + "' AND " +  PROM_STATS_SOURCE1 + "='" + info.getSource1()
              + "' AND " + PROM_STATS_RESERVED1 + "='" + info.getReserved1() + "' AND " + PROM_STATS_RESERVED2 + "='" + info.getReserved2() + "' AND " + PROM_STATS_RESERVED3 + "='" + info.getReserved3()
              + "' AND " + PROM_STATS_RESERVED4 + "='" + info.getReserved4()+"'", null) > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /* 查询指定数据 */
  public AdLogInfo queryAdLogInfo(AdLogInfo info) {
    Cursor c = null;
    AdLogInfo r = null;
    if (mSQLiteDatabase == null) {
      return r;
    }
    try {
      c = mSQLiteDatabase.query(true, PROM_STATISTICS_TABLE, new String[] { PROM_STATS_ACTION, PROM_STATS_ADTAG, 
          PROM_STATS_NUM, PROM_STATS_SOURCE1, PROM_STATS_RESERVED1, PROM_STATS_RESERVED2, PROM_STATS_RESERVED3,PROM_STATS_RESERVED4 }, PROM_STATS_ACTION + "='" + info.getAction() + "' AND " + PROM_STATS_ADTAG 
          + "='" + info.getAdTag() + "' AND " +  PROM_STATS_SOURCE1 + "='" + info.getSource1()
          + "' AND " + PROM_STATS_RESERVED1 + "='" + info.getReserved1() + "' AND " + PROM_STATS_RESERVED2 + "='" + info.getReserved2() + "' AND " + PROM_STATS_RESERVED3 + "='" + info.getReserved3()
          + "' AND " + PROM_STATS_RESERVED4 + "='" + info.getReserved4()+"'", null, null, null, null, null);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    if (c != null) {
      if (c.getCount() > 0) {
        c.moveToFirst();
        r = new AdLogInfo();
        r.setAction(c.getInt(c.getColumnIndex(PROM_STATS_ACTION)));
        r.setAdTag(c.getString(c.getColumnIndex(PROM_STATS_ADTAG)));
        r.setNum(c.getInt(c.getColumnIndex(PROM_STATS_NUM)));
        r.setSource1((short) (c.getInt(c.getColumnIndex(PROM_STATS_SOURCE1))));
        r.setReserved1(c.getString(c.getColumnIndex(PROM_STATS_RESERVED1)));
        r.setReserved2(c.getString(c.getColumnIndex(PROM_STATS_RESERVED2)));
        r.setReserved3(c.getString(c.getColumnIndex(PROM_STATS_RESERVED3)));
        r.setReserved4(c.getString(c.getColumnIndex(PROM_STATS_RESERVED4)));

      }
      c.close();
    }
    return r;
  }

  /* 通过Cursor查询所有数据 */
  public Cursor fetchAllData() {
    Cursor ret = null;
    if (mSQLiteDatabase == null) {
      return ret;
    }
    try {
      ret = mSQLiteDatabase.query(PROM_STATISTICS_TABLE, new String[] { PROM_STATS_ACTION, PROM_STATS_ADTAG, PROM_STATS_NUM, PROM_STATS_SOURCE1,
           PROM_STATS_RESERVED1, PROM_STATS_RESERVED2, PROM_STATS_RESERVED3,PROM_STATS_RESERVED4 }, null, null, null, null, null);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * 获取所有统计的信息
   * 
   * @return
   */
  public ArrayList<AdLogInfo> queryAdLogInfoList() {
    ArrayList<AdLogInfo> ret = new ArrayList<AdLogInfo>();
    Cursor c = fetchAllData();

    if (c != null) {
      if (c.getCount() > 0) {
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
          AdLogInfo r = new AdLogInfo();

          r.setAction(c.getInt(c.getColumnIndex(PROM_STATS_ACTION)));
          r.setAdTag(c.getString(c.getColumnIndex(PROM_STATS_ADTAG)));
          r.setNum(c.getInt(c.getColumnIndex(PROM_STATS_NUM)));
          r.setSource1((short) (c.getInt(c.getColumnIndex(PROM_STATS_SOURCE1))));
          r.setReserved1(c.getString(c.getColumnIndex(PROM_STATS_RESERVED1)));
          r.setReserved2(c.getString(c.getColumnIndex(PROM_STATS_RESERVED2)));
          r.setReserved3(c.getString(c.getColumnIndex(PROM_STATS_RESERVED3)));
          r.setReserved4(c.getString(c.getColumnIndex(PROM_STATS_RESERVED4)));
          deleteAdLogInfo(r);
          ret.add(r);
        }
      }
    }

    c.close();
    return ret;
  }

  // 单条更新
  public boolean updateAdLogInfo(AdLogInfo info) {
    ContentValues initialValues = new ContentValues();
    boolean ret = false;
    if (mSQLiteDatabase == null) {
      return ret;
    }
    initialValues.put(PROM_STATS_ACTION, info.getAction());
    initialValues.put(PROM_STATS_ADTAG, info.getAdTag());
    initialValues.put(PROM_STATS_NUM, info.getNum());
    initialValues.put(PROM_STATS_SOURCE1, info.getSource1());
    initialValues.put(PROM_STATS_RESERVED1, info.getReserved1());
    initialValues.put(PROM_STATS_RESERVED2, info.getReserved2());
    initialValues.put(PROM_STATS_RESERVED3, info.getReserved3());
    initialValues.put(PROM_STATS_RESERVED4, info.getReserved4());

    try {
      ret = mSQLiteDatabase.update(PROM_STATISTICS_TABLE, initialValues,  PROM_STATS_ACTION + "='" + info.getAction() 
          + "' AND " + PROM_STATS_ADTAG + "='" + info.getAdTag() + "' AND " +  PROM_STATS_SOURCE1 + "='" + info.getSource1()
          + "' AND " + PROM_STATS_RESERVED1 + "='" + info.getReserved1() + "' AND " + PROM_STATS_RESERVED2 + "='" + info.getReserved2() + "' AND " + PROM_STATS_RESERVED3 + "='" + info.getReserved3()
          + "' AND " + PROM_STATS_RESERVED4 + "='" + info.getReserved4()+"'", null) > 0;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return ret;
  }

  /*
   * 获取最新一条数据的id
   */
  public int queryAdLogTableLastId() {
    int ret = 0;
    Cursor c = null;
    if (mSQLiteDatabase == null) {
      return ret;
    }
    try {
      c = mSQLiteDatabase.query(PROM_STATISTICS_TABLE, new String[] { "_id" }, null, null, null, null, null);
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
   * 删除本地统计信息
   * 
   * @param promAppInfo
   * @return
   */
  public void deleteAllAdLogInfo() {
    if (mSQLiteDatabase == null) {
      return;
    }
    int lastId = queryAdLogTableLastId();
    if (lastId > 0) {
      mSQLiteDatabase.delete(PROM_STATISTICS_TABLE, "_id > 0 and _id <= " + lastId, null);
    }
  }

  public class PromotionDataBaseHelper extends SQLiteOpenHelper {
    public PromotionDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      String sql = "CREATE TABLE IF NOT EXISTS " + PROM_STATISTICS_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + PROM_STATS_ACTION + " INTEGER, "
          + PROM_STATS_ADTAG + " text, " + PROM_STATS_NUM + " INTEGER, " + PROM_STATS_SOURCE1 + " INTEGER, " + PROM_STATS_RESERVED1 + " text, "
          + PROM_STATS_RESERVED2 + " text, " + PROM_STATS_RESERVED3 + " text, " + PROM_STATS_RESERVED4 + " text)";
      db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      try {
          dropAllTable(db);
          onCreate(db);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public void dropAllTable(SQLiteDatabase db) {
    try {
      String sql = "drop table if exists " + PROM_STATISTICS_TABLE;
      db.equals(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
