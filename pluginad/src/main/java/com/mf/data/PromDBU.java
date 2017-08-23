package com.mf.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.mf.basecode.model.MyPackageInfo;
import com.mf.basecode.utils.Logger;
import com.mf.download.model.DownloadInfo;
import com.mf.model.AdDbInfo;
import com.mf.model.AppDbStartInfo;
import com.mf.model.BrowerInfo;
import com.mf.model.UrlInfoBto;
import com.mf.network.object.AdInfo;
import com.mf.network.object.AppStartInfo;
import com.mf.promotion.util.PromUtils;
import com.mf.utils.AppInstallUtils;
//import android.util.Log;

public class PromDBU {
  /**
   * 单实例
   */
  private static PromDBU instance         = null;
  public final static int    DATABASE_VERSION = 18;

  /**
   * 取实例
   * 
   * @param context
   * @return
   */
  public static synchronized PromDBU getInstance(Context context) {
    if (instance == null) {
      instance = new PromDBU(context);
    }
    if (!instance.isOpen()) {
      instance.open();
    }
    return instance;
  }

  private SQLiteDatabase   mSQLiteDatabase = null;

  private SQLiteOpenHelper mSqlOpenHelper;

  private Context          mContext;

  public PromDBU(Context context) {
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
  
  public ArrayList<AdDbInfo> queryAllPreDownloadAdInfo() {
    ArrayList<AdDbInfo> ret = new ArrayList<AdDbInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    
    Cursor c = mSQLiteDatabase.query(true, PROM_PUSH_NOTIFY_TABLE, new String[] { AD_INFO_KEYID,AD_INFO_ADID, AD_INFO_ADNAME, AD_INFO_ADPICURL, AD_INFO_ADTYPE, AD_INFO_ADLANGUAGE, AD_INFO_ADDOWNURL, AD_INFO_PACKAGENAME, 
        AD_INFO_VERSIONCODE, AD_INFO_FILEMD5,AD_INFO_ADDISPLAYTYPE, AD_INFO_PREDOWN, AD_INFO_SHOWTIMES,AD_INFO_POSITION, AD_INFO_FILESIZE, AD_INFO_REMAINTIMES,AD_INFO_DOWNLOADTIMES,
        AD_INFO_SHOWMARK,AD_INFO_HASSHOWTIMES,AD_INFO_CREATETIME,AD_INFO_PROMTYPE, AD_INFO_PROMTYPENAME, AD_INFO_PICNAME,AD_INFO_INSTALL,AD_INFO_RESERVED1,AD_INFO_RESERVED2, 
        AD_INFO_RESERVED3, AD_INFO_SSPID,AD_INFO_SSPTYPE}, AD_INFO_PREDOWN+" = 1 OR "+AD_INFO_PREDOWN+" = 2 AND "+AD_INFO_CREATETIME+" > "+calendar.getTimeInMillis(), null, null, null, null, null);

    if (c.getCount() > 0) {
      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        AdDbInfo info = new AdDbInfo();
        info.setKeyid(c.getInt(c.getColumnIndex(AD_INFO_KEYID)));
        info.setAdId(c.getString(c.getColumnIndex(AD_INFO_ADID)));
        info.setAdName(c.getString(c.getColumnIndex(AD_INFO_ADNAME)));
        info.setAdPicUrl(c.getString(c.getColumnIndex(AD_INFO_ADPICURL)));
        info.setAdType(c.getInt(c.getColumnIndex(AD_INFO_ADTYPE)));
        info.setAdLanguage(c.getString(c.getColumnIndex(AD_INFO_ADLANGUAGE)));
        info.setAdDownUrl(c.getString(c.getColumnIndex(AD_INFO_ADDOWNURL)));
        info.setPackageName(c.getString(c.getColumnIndex(AD_INFO_PACKAGENAME)));
        info.setVersionCode(c.getInt(c.getColumnIndex(AD_INFO_VERSIONCODE)));
        info.setFileMd5(c.getString(c.getColumnIndex(AD_INFO_FILEMD5)));
        info.setAdDisplayType(c.getInt(c.getColumnIndex(AD_INFO_ADDISPLAYTYPE)));
        info.setPreDown(c.getInt(c.getColumnIndex(AD_INFO_PREDOWN)));
        info.setShowTimes(c.getInt(c.getColumnIndex(AD_INFO_SHOWTIMES)));
        info.setPosition(c.getInt(c.getColumnIndex(AD_INFO_POSITION)));
        info.setFileSize(c.getLong(c.getColumnIndex(AD_INFO_FILESIZE)));
        info.setRemainTimes(c.getInt(c.getColumnIndex(AD_INFO_REMAINTIMES)));
        info.setDownloadTimes(c.getInt(c.getColumnIndex(AD_INFO_DOWNLOADTIMES)));
        info.setDownloadTimes(c.getInt(c.getColumnIndex(AD_INFO_DOWNLOADTIMES)));
        info.setShowmark(c.getInt(c.getColumnIndex(AD_INFO_SHOWMARK)));
        info.setHasShowTimes(c.getInt(c.getColumnIndex(AD_INFO_HASSHOWTIMES)));
        info.setCreateTime(c.getLong(c.getColumnIndex(AD_INFO_CREATETIME)));
        info.setPromType(c.getInt(c.getColumnIndex(AD_INFO_PROMTYPE)));
        info.setPromTypeName(c.getString(c.getColumnIndex(AD_INFO_PROMTYPENAME)));
        info.setPicName(c.getString(c.getColumnIndex(AD_INFO_PICNAME)));
        info.setInstalled(c.getInt(c.getColumnIndex(AD_INFO_INSTALL)));
        info.setReserved1(c.getString(c.getColumnIndex(AD_INFO_RESERVED1)));
        info.setReserved2(c.getString(c.getColumnIndex(AD_INFO_RESERVED2)));
        info.setReserved3(c.getString(c.getColumnIndex(AD_INFO_RESERVED3)));
        info.setSspid(c.getString(c.getColumnIndex(AD_INFO_SSPID)));
        info.setSsptype(c.getInt(c.getColumnIndex(AD_INFO_SSPTYPE)));
        ret.add(info);
      }
    }
    c.close();
    return ret;
  }
  
  public ArrayList<AdDbInfo> queryAdInfo(int promType) {
    ArrayList<AdDbInfo> ret = new ArrayList<AdDbInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    Logger.e("promDBU", ""+calendar.getTimeInMillis()+"   promType ="+promType);
    
    Cursor c = mSQLiteDatabase.query(true, PROM_PUSH_NOTIFY_TABLE, new String[] { AD_INFO_KEYID,AD_INFO_ADID, AD_INFO_ADNAME, AD_INFO_ADPICURL, AD_INFO_ADTYPE, AD_INFO_ADLANGUAGE, AD_INFO_ADDOWNURL, AD_INFO_PACKAGENAME, 
        AD_INFO_VERSIONCODE, AD_INFO_FILEMD5,AD_INFO_ADDISPLAYTYPE, AD_INFO_PREDOWN, AD_INFO_SHOWTIMES,AD_INFO_POSITION, AD_INFO_FILESIZE, AD_INFO_REMAINTIMES,AD_INFO_DOWNLOADTIMES,
        AD_INFO_SHOWMARK,AD_INFO_HASSHOWTIMES,AD_INFO_CREATETIME,AD_INFO_PROMTYPE, AD_INFO_PROMTYPENAME, AD_INFO_PICNAME,AD_INFO_INSTALL,AD_INFO_RESERVED1,AD_INFO_RESERVED2, 
        AD_INFO_RESERVED3, AD_INFO_SSPID, AD_INFO_SSPTYPE }, AD_INFO_PROMTYPE +" = "+promType+" AND "+AD_INFO_CREATETIME+" > "+calendar.getTimeInMillis(), null, null, null, null, null);

    if (c.getCount() > 0) {
      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        AdDbInfo info = new AdDbInfo();
        info.setKeyid(c.getInt(c.getColumnIndex(AD_INFO_KEYID)));
        info.setAdId(c.getString(c.getColumnIndex(AD_INFO_ADID)));
        info.setAdName(c.getString(c.getColumnIndex(AD_INFO_ADNAME)));
        info.setAdPicUrl(c.getString(c.getColumnIndex(AD_INFO_ADPICURL)));
        info.setAdType(c.getInt(c.getColumnIndex(AD_INFO_ADTYPE)));
        info.setAdLanguage(c.getString(c.getColumnIndex(AD_INFO_ADLANGUAGE)));
        info.setAdDownUrl(c.getString(c.getColumnIndex(AD_INFO_ADDOWNURL)));
        info.setPackageName(c.getString(c.getColumnIndex(AD_INFO_PACKAGENAME)));
        info.setVersionCode(c.getInt(c.getColumnIndex(AD_INFO_VERSIONCODE)));
        info.setFileMd5(c.getString(c.getColumnIndex(AD_INFO_FILEMD5)));
        info.setAdDisplayType(c.getInt(c.getColumnIndex(AD_INFO_ADDISPLAYTYPE)));
        info.setPreDown(c.getInt(c.getColumnIndex(AD_INFO_PREDOWN)));
        info.setShowTimes(c.getInt(c.getColumnIndex(AD_INFO_SHOWTIMES)));
        info.setPosition(c.getInt(c.getColumnIndex(AD_INFO_POSITION)));
        info.setFileSize(c.getLong(c.getColumnIndex(AD_INFO_FILESIZE)));
        info.setRemainTimes(c.getInt(c.getColumnIndex(AD_INFO_REMAINTIMES)));
        info.setDownloadTimes(c.getInt(c.getColumnIndex(AD_INFO_DOWNLOADTIMES)));
        info.setShowmark(c.getInt(c.getColumnIndex(AD_INFO_SHOWMARK)));
        info.setHasShowTimes(c.getInt(c.getColumnIndex(AD_INFO_HASSHOWTIMES)));
        info.setCreateTime(c.getLong(c.getColumnIndex(AD_INFO_CREATETIME)));
        info.setPromType(c.getInt(c.getColumnIndex(AD_INFO_PROMTYPE)));
        info.setPromTypeName(c.getString(c.getColumnIndex(AD_INFO_PROMTYPENAME)));
        info.setPicName(c.getString(c.getColumnIndex(AD_INFO_PICNAME)));
        info.setInstalled(c.getInt(c.getColumnIndex(AD_INFO_INSTALL)));
        info.setReserved1(c.getString(c.getColumnIndex(AD_INFO_RESERVED1)));
        info.setReserved2(c.getString(c.getColumnIndex(AD_INFO_RESERVED2)));
        info.setReserved3(c.getString(c.getColumnIndex(AD_INFO_RESERVED3)));
        info.setSspid(c.getString(c.getColumnIndex(AD_INFO_SSPID)));
        info.setSsptype(c.getInt(c.getColumnIndex(AD_INFO_SSPTYPE)));
        ret.add(info);
      }
    }
    c.close();
    return ret;
  }


  public void insertAdInfo(AdInfo  info ,int promType,String promName) {
    AdDbInfo adinfo = getAdInfobyAdid(info.getAdId(), promType);
    if(adinfo != null){
      updateAdInfo(info, promType, promName);
      return;
    }
    ContentValues initialValues = new ContentValues();
    initialValues.put(AD_INFO_ADID, info.getAdId());
    initialValues.put(AD_INFO_ADNAME, info.getAdName());
    initialValues.put(AD_INFO_ADPICURL, info.getAdPicUrl());
    initialValues.put(AD_INFO_ADTYPE, info.getAdType());
    initialValues.put(AD_INFO_ADLANGUAGE, info.getAdLanguage());
    initialValues.put(AD_INFO_ADDOWNURL, info.getAdDownUrl());
    initialValues.put(AD_INFO_PACKAGENAME, info.getPackageName());
    initialValues.put(AD_INFO_VERSIONCODE, info.getVersionCode());
    initialValues.put(AD_INFO_FILEMD5, info.getFileMd5());
    initialValues.put(AD_INFO_ADDISPLAYTYPE, info.getAdDisplayType());
    initialValues.put(AD_INFO_PREDOWN, info.getPreDown());
    initialValues.put(AD_INFO_SHOWTIMES, info.getShowTimes());
    initialValues.put(AD_INFO_POSITION, info.getPosition());
    initialValues.put(AD_INFO_FILESIZE, info.getFileSize());
    initialValues.put(AD_INFO_REMAINTIMES, info.getRemainTimes());
    initialValues.put(AD_INFO_DOWNLOADTIMES, info.getDownloadTimes());
    initialValues.put(AD_INFO_SHOWMARK,0);
    initialValues.put(AD_INFO_HASSHOWTIMES,0);
    initialValues.put(AD_INFO_CREATETIME, System.currentTimeMillis());
    initialValues.put(AD_INFO_PROMTYPE, promType);
    initialValues.put(AD_INFO_PROMTYPENAME, promName);
    initialValues.put(AD_INFO_PICNAME, PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
    initialValues.put(AD_INFO_INSTALL, AppInstallUtils.checkAppInSys(mContext, info.getPackageName()));
    initialValues.put(AD_INFO_RESERVED1, info.getReserved1());
    initialValues.put(AD_INFO_RESERVED2, info.getReserved2());
    initialValues.put(AD_INFO_RESERVED3, info.getReserved3());
    initialValues.put(AD_INFO_SSPID, info.getSspid());
    initialValues.put(AD_INFO_SSPTYPE, info.getSsptype());
    mSQLiteDatabase.insert(PROM_PUSH_NOTIFY_TABLE, null, initialValues);
  }

  public void updateAdInfo(AdInfo info,int promType,String promName) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(AD_INFO_ADID, info.getAdId());
    initialValues.put(AD_INFO_ADNAME, info.getAdName());
    initialValues.put(AD_INFO_ADPICURL, info.getAdPicUrl());
    initialValues.put(AD_INFO_ADTYPE, info.getAdType());
    initialValues.put(AD_INFO_ADLANGUAGE, info.getAdLanguage());
    initialValues.put(AD_INFO_ADDOWNURL, info.getAdDownUrl());
    initialValues.put(AD_INFO_PACKAGENAME, info.getPackageName());
    initialValues.put(AD_INFO_VERSIONCODE, info.getVersionCode());
    initialValues.put(AD_INFO_FILEMD5, info.getFileMd5());
    initialValues.put(AD_INFO_ADDISPLAYTYPE, info.getAdDisplayType());
    initialValues.put(AD_INFO_PREDOWN, info.getPreDown());
    initialValues.put(AD_INFO_SHOWTIMES, info.getShowTimes());
//    initialValues.put(AD_INFO_INAPP, info.getInApp());
    initialValues.put(AD_INFO_POSITION, info.getPosition());
    initialValues.put(AD_INFO_FILESIZE, info.getFileSize());
    initialValues.put(AD_INFO_REMAINTIMES, info.getRemainTimes());
    initialValues.put(AD_INFO_DOWNLOADTIMES, info.getDownloadTimes());
    initialValues.put(AD_INFO_PROMTYPE, promType);
    initialValues.put(AD_INFO_PROMTYPENAME, promName);
    initialValues.put(AD_INFO_PICNAME, PromUtils.getPicNameFromPicUrl(info.getAdPicUrl()));
    initialValues.put(AD_INFO_INSTALL, AppInstallUtils.checkAppInSys(mContext, info.getPackageName()));
    initialValues.put(AD_INFO_RESERVED1, info.getReserved1());
    initialValues.put(AD_INFO_RESERVED2, info.getReserved2());
    initialValues.put(AD_INFO_RESERVED3, info.getReserved3());
    initialValues.put(AD_INFO_SSPID, info.getSspid());
    initialValues.put(AD_INFO_SSPTYPE, info.getSsptype());
    mSQLiteDatabase.update(PROM_PUSH_NOTIFY_TABLE, initialValues, AD_INFO_ADID + " = '"+info.getAdId()+"' AND " +AD_INFO_PROMTYPE +" = "+promType, null);
  }
  
  public void updateAdInfoHasShowTimes(AdDbInfo info,int promType,String promName) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(AD_INFO_HASSHOWTIMES, info.getHasShowTimes()+1);
    mSQLiteDatabase.update(PROM_PUSH_NOTIFY_TABLE, initialValues, AD_INFO_ADID + " = '"+info.getAdId()+"' AND " +AD_INFO_PROMTYPE +" = "+promType, null);
  }
  
  public void updateAdInfoShowmark(AdDbInfo info,int promType) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(AD_INFO_SHOWMARK, 1);
    mSQLiteDatabase.update(PROM_PUSH_NOTIFY_TABLE, initialValues, AD_INFO_ADID + " = '"+info.getAdId()+"' AND " +AD_INFO_PROMTYPE +" = "+promType, null);
  }
  
  public void resetShowmark(int promType) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(AD_INFO_SHOWMARK, 0);
    mSQLiteDatabase.update(PROM_PUSH_NOTIFY_TABLE, initialValues,  AD_INFO_PROMTYPE +" = "+promType, null);
  }
  
  public void updateAdInfoInstallStatusByPackageName(String packageName){
    ContentValues initialValues = new ContentValues();
    initialValues.put(AD_INFO_INSTALL, INSTALL);
    mSQLiteDatabase.update(PROM_PUSH_NOTIFY_TABLE, initialValues,  AD_INFO_PACKAGENAME +" = '"+packageName+"'", null);
  }
  
  public AdDbInfo getAdInfobyAdid(String adid,int promType){
    AdDbInfo info = null;
    if (mSQLiteDatabase == null) {
      return info;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    
    Cursor c = mSQLiteDatabase.query(true, PROM_PUSH_NOTIFY_TABLE, new String[] { AD_INFO_KEYID,AD_INFO_ADID, AD_INFO_ADNAME, AD_INFO_ADPICURL, AD_INFO_ADTYPE, AD_INFO_ADLANGUAGE, AD_INFO_ADDOWNURL, AD_INFO_PACKAGENAME, 
        AD_INFO_VERSIONCODE, AD_INFO_FILEMD5,AD_INFO_ADDISPLAYTYPE, AD_INFO_PREDOWN, AD_INFO_SHOWTIMES,AD_INFO_POSITION, AD_INFO_FILESIZE, AD_INFO_REMAINTIMES,AD_INFO_DOWNLOADTIMES,
        AD_INFO_SHOWMARK,AD_INFO_HASSHOWTIMES,AD_INFO_CREATETIME,AD_INFO_PROMTYPE, AD_INFO_PROMTYPENAME, AD_INFO_PICNAME,AD_INFO_INSTALL,AD_INFO_RESERVED1,AD_INFO_RESERVED2, 
        AD_INFO_RESERVED3, AD_INFO_SSPID, AD_INFO_SSPTYPE }, AD_INFO_ADID +"= '"+adid+"' AND "+AD_INFO_PROMTYPE+" = "+promType+" AND "+AD_INFO_CREATETIME+" > "+calendar.getTimeInMillis(), null, null, null, null, null);

    if (c.getCount() > 0) {
      c.moveToFirst();
      info = new AdDbInfo();
      info.setKeyid(c.getInt(c.getColumnIndex(AD_INFO_KEYID)));
      info.setAdId(c.getString(c.getColumnIndex(AD_INFO_ADID)));
      info.setAdName(c.getString(c.getColumnIndex(AD_INFO_ADNAME)));
      info.setAdPicUrl(c.getString(c.getColumnIndex(AD_INFO_ADPICURL)));
      info.setAdType(c.getInt(c.getColumnIndex(AD_INFO_ADTYPE)));
      info.setAdLanguage(c.getString(c.getColumnIndex(AD_INFO_ADLANGUAGE)));
      info.setAdDownUrl(c.getString(c.getColumnIndex(AD_INFO_ADDOWNURL)));
      info.setPackageName(c.getString(c.getColumnIndex(AD_INFO_PACKAGENAME)));
      info.setVersionCode(c.getInt(c.getColumnIndex(AD_INFO_VERSIONCODE)));
      info.setFileMd5(c.getString(c.getColumnIndex(AD_INFO_FILEMD5)));
      info.setAdDisplayType(c.getInt(c.getColumnIndex(AD_INFO_ADDISPLAYTYPE)));
      info.setPreDown(c.getInt(c.getColumnIndex(AD_INFO_PREDOWN)));
      info.setShowTimes(c.getInt(c.getColumnIndex(AD_INFO_SHOWTIMES)));
//      info.setInApp(c.getInt(c.getColumnIndex(AD_INFO_INAPP)));
      info.setPosition(c.getInt(c.getColumnIndex(AD_INFO_POSITION)));
      info.setFileSize(c.getLong(c.getColumnIndex(AD_INFO_FILESIZE)));
      info.setRemainTimes(c.getInt(c.getColumnIndex(AD_INFO_REMAINTIMES)));
      info.setDownloadTimes(c.getInt(c.getColumnIndex(AD_INFO_DOWNLOADTIMES)));
      info.setShowmark(c.getInt(c.getColumnIndex(AD_INFO_SHOWMARK)));
      info.setHasShowTimes(c.getInt(c.getColumnIndex(AD_INFO_HASSHOWTIMES)));
      info.setCreateTime(c.getLong(c.getColumnIndex(AD_INFO_CREATETIME)));
      info.setPromType(c.getInt(c.getColumnIndex(AD_INFO_PROMTYPE)));
      info.setPromTypeName(c.getString(c.getColumnIndex(AD_INFO_PROMTYPENAME)));
      info.setPicName(c.getString(c.getColumnIndex(AD_INFO_PICNAME)));
      info.setInstalled(c.getInt(c.getColumnIndex(AD_INFO_INSTALL)));
      info.setReserved1(c.getString(c.getColumnIndex(AD_INFO_RESERVED1)));
      info.setReserved2(c.getString(c.getColumnIndex(AD_INFO_RESERVED2)));
      info.setReserved3(c.getString(c.getColumnIndex(AD_INFO_RESERVED3)));
      info.setSspid(c.getString(c.getColumnIndex(AD_INFO_SSPID)));
      info.setSsptype(c.getInt(c.getColumnIndex(AD_INFO_SSPTYPE)));
    }
    c.close();
    return info;
  }
  
  public void deleteYesterdayAdInfoByPromType(int promtype) {
    if (mSQLiteDatabase == null) {
      return;
    }
    
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    
    mSQLiteDatabase.delete(PROM_PUSH_NOTIFY_TABLE, AD_INFO_PROMTYPE+" = "+promtype+" AND "+AD_INFO_CREATETIME+" < "+calendar.getTimeInMillis(), null);
  }
  
//  public void deleteYesterdayShortcutAdInfo(){
//    if (mSQLiteDatabase == null) {
//      return;
//    }
//    Calendar calendar = Calendar.getInstance();
//    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
//    
//    mSQLiteDatabase.delete(PROM_PUSH_NOTIFY_TABLE, AD_INFO_PROMTYPE + " = " + PROM_SHORTCUT , null);
//  }
  
  public ArrayList<AdDbInfo> queryYesterdayShortcutAdInfo() {
    ArrayList<AdDbInfo> ret = new ArrayList<AdDbInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    
    Cursor c = mSQLiteDatabase.query(true, PROM_PUSH_NOTIFY_TABLE, new String[] { AD_INFO_KEYID,AD_INFO_ADID, AD_INFO_ADNAME, AD_INFO_ADPICURL, AD_INFO_ADTYPE, AD_INFO_ADLANGUAGE, AD_INFO_ADDOWNURL, AD_INFO_PACKAGENAME, 
        AD_INFO_VERSIONCODE, AD_INFO_FILEMD5,AD_INFO_ADDISPLAYTYPE, AD_INFO_PREDOWN, AD_INFO_SHOWTIMES,AD_INFO_POSITION, AD_INFO_FILESIZE, AD_INFO_REMAINTIMES,AD_INFO_DOWNLOADTIMES,
        AD_INFO_SHOWMARK,AD_INFO_HASSHOWTIMES,AD_INFO_CREATETIME,AD_INFO_PROMTYPE, AD_INFO_PROMTYPENAME, AD_INFO_PICNAME,AD_INFO_INSTALL,AD_INFO_RESERVED1,AD_INFO_RESERVED2, 
        AD_INFO_RESERVED3, AD_INFO_SSPID, AD_INFO_SSPTYPE}, AD_INFO_PROMTYPE +" = "+PROM_SHORTCUT +" AND "+AD_INFO_CREATETIME+" < "+calendar.getTimeInMillis(), null, null, null, null, null);

    if (c.getCount() > 0) {
      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        AdDbInfo info = new AdDbInfo();
        info.setKeyid(c.getInt(c.getColumnIndex(AD_INFO_KEYID)));
        info.setAdId(c.getString(c.getColumnIndex(AD_INFO_ADID)));
        info.setAdName(c.getString(c.getColumnIndex(AD_INFO_ADNAME)));
        info.setAdPicUrl(c.getString(c.getColumnIndex(AD_INFO_ADPICURL)));
        info.setAdType(c.getInt(c.getColumnIndex(AD_INFO_ADTYPE)));
        info.setAdLanguage(c.getString(c.getColumnIndex(AD_INFO_ADLANGUAGE)));
        info.setAdDownUrl(c.getString(c.getColumnIndex(AD_INFO_ADDOWNURL)));
        info.setPackageName(c.getString(c.getColumnIndex(AD_INFO_PACKAGENAME)));
        info.setVersionCode(c.getInt(c.getColumnIndex(AD_INFO_VERSIONCODE)));
        info.setFileMd5(c.getString(c.getColumnIndex(AD_INFO_FILEMD5)));
        info.setAdDisplayType(c.getInt(c.getColumnIndex(AD_INFO_ADDISPLAYTYPE)));
        info.setPreDown(c.getInt(c.getColumnIndex(AD_INFO_PREDOWN)));
        info.setShowTimes(c.getInt(c.getColumnIndex(AD_INFO_SHOWTIMES)));
        info.setPosition(c.getInt(c.getColumnIndex(AD_INFO_POSITION)));
        info.setFileSize(c.getLong(c.getColumnIndex(AD_INFO_FILESIZE)));
        info.setRemainTimes(c.getInt(c.getColumnIndex(AD_INFO_REMAINTIMES)));
        info.setDownloadTimes(c.getInt(c.getColumnIndex(AD_INFO_DOWNLOADTIMES)));
        info.setShowmark(c.getInt(c.getColumnIndex(AD_INFO_SHOWMARK)));
        info.setHasShowTimes(c.getInt(c.getColumnIndex(AD_INFO_HASSHOWTIMES)));
        info.setCreateTime(c.getLong(c.getColumnIndex(AD_INFO_CREATETIME)));
        info.setPromType(c.getInt(c.getColumnIndex(AD_INFO_PROMTYPE)));
        info.setPromTypeName(c.getString(c.getColumnIndex(AD_INFO_PROMTYPENAME)));
        info.setPicName(c.getString(c.getColumnIndex(AD_INFO_PICNAME)));
        info.setInstalled(c.getInt(c.getColumnIndex(AD_INFO_INSTALL)));
        info.setReserved1(c.getString(c.getColumnIndex(AD_INFO_RESERVED1)));
        info.setReserved2(c.getString(c.getColumnIndex(AD_INFO_RESERVED2)));
        info.setReserved3(c.getString(c.getColumnIndex(AD_INFO_RESERVED3)));
        info.setSspid(c.getString(c.getColumnIndex(AD_INFO_SSPID)));
        info.setSsptype(c.getInt(c.getColumnIndex(AD_INFO_SSPTYPE)));
        ret.add(info);
      }
    }
    c.close();
    return ret;
  }

//  public void deletePushNofityById(int id,int promType) {
//    if (mSQLiteDatabase == null) {
//      return;
//    }
//    mSQLiteDatabase.delete(PROM_PUSH_NOTIFY_TABLE, PROM_APP_INFO_ID + "='" + id + "'", null);
//  }

  public void deletePushNofity() {
    if (mSQLiteDatabase == null) {
      return;
    }
    String sql = "delete from '" + PROM_PUSH_NOTIFY_TABLE + "';select * from sqlite_sequence;update sqlite_sequence set seq=0 where name="
        + PROM_PUSH_NOTIFY_TABLE;
    mSQLiteDatabase.execSQL(sql);
  }

  /*
   * ------------------------------PROM_CONFIG_TABLE------------------------
   */

//  public String queryCfgValueByKey(String key) {
//    String ret = "";
//    if (mSQLiteDatabase == null) {
//      return ret;
//    }
//    Cursor c = mSQLiteDatabase.query(true, PROM_CONFIG_TABLE, new String[] { PROM_CONFIG_VALUE }, PROM_CONFIG_KEY + "='" + key + "'", null, null, null, null,
//        null);
//    if (c.getCount() > 0) {
//      c.moveToFirst();
//      ret = c.getString(c.getColumnIndex(PROM_CONFIG_VALUE));
//    }
//    c.close();
//    return ret;
//  }

//  public void insertCfg(String key, String value) {
//    if (mSQLiteDatabase == null) {
//      return;
//    }
//    String tmp = queryCfgValueByKey(key);
//    if (TextUtils.isEmpty(tmp)) {
//      ContentValues cv = new ContentValues();
//      cv.put(PROM_CONFIG_KEY, key);
//      cv.put(PROM_CONFIG_VALUE, value);
//      mSQLiteDatabase.insert(PROM_CONFIG_TABLE, null, cv);
//    } else {
//      if (!value.equals(tmp)) {
//        updateCfg(key, value);
//      }
//    }
//  }
//
//  public void updateCfg(String key, String value) {
//    if (mSQLiteDatabase == null) {
//      return;
//    }
//    ContentValues cv = new ContentValues();
//    cv.put(PROM_CONFIG_KEY, key);
//    cv.put(PROM_CONFIG_VALUE, value);
//    mSQLiteDatabase.update(PROM_CONFIG_TABLE, cv, PROM_CONFIG_KEY + "='" + key + "'", null);
//  }


  /*
   * -------------------------PROM_INSTALLED_APP_TABLE---------------------- --
   */

  /**
   * 根据包名获取信息
   * 
   * @param packageName
   * @return
   */
  public ArrayList<MyPackageInfo> queryInstalledApkInfoByPackageName(String packageName) {
    ArrayList<MyPackageInfo> ret = new ArrayList<MyPackageInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Cursor c = mSQLiteDatabase.query(true, PROM_INSTALLED_APP_TABLE, new String[] { PROM_APP_INFO_PACKAGE_NAME, PROM_APP_INFO_VERSION },
        PROM_APP_INFO_PACKAGE_NAME + "='" + packageName + "'", null, null, null, null, null);
    if (c.getCount() > 0) {
      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        String pacakgeName = c.getString(c.getColumnIndex(PROM_APP_INFO_PACKAGE_NAME));
        int versionCode = c.getInt(c.getColumnIndex(PROM_APP_INFO_VERSION));
        ret.add(new MyPackageInfo(pacakgeName, versionCode));
      }
    }
    c.close();
    return ret;
  }

  public MyPackageInfo queryInstalledApkInfoByPackageInfo(MyPackageInfo pInfo) {
    MyPackageInfo ret = null;
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Cursor c = mSQLiteDatabase.query(true, PROM_INSTALLED_APP_TABLE, new String[] { PROM_APP_INFO_PACKAGE_NAME, PROM_APP_INFO_VERSION },
        PROM_APP_INFO_PACKAGE_NAME + "='" + pInfo.getPackageName() + "' and " + PROM_APP_INFO_VERSION + "='" + pInfo.getVersionCode() + "'", null, null, null,
        null, null);
    if (c.getCount() > 0) {
      c.moveToFirst();
      String pacakgeName = c.getString(c.getColumnIndex(PROM_APP_INFO_PACKAGE_NAME));
      int versionCode = c.getInt(c.getColumnIndex(PROM_APP_INFO_VERSION));
      ret = new MyPackageInfo(pacakgeName, versionCode);
    }
    c.close();
    return ret;
  }

  public void addInstalledApkInfo(MyPackageInfo pInfo) {
    MyPackageInfo tmp = queryInstalledApkInfoByPackageInfo(pInfo);
    if (tmp == null) {
      insertInstalledApkInfo(pInfo);
    }
  }

  public void insertInstalledApkInfo(MyPackageInfo pInfo) {
    if (mSQLiteDatabase == null) {
      return;
    }
    ContentValues cv = new ContentValues();
    cv.put(PROM_APP_INFO_PACKAGE_NAME, pInfo.getPackageName());
    cv.put(PROM_APP_INFO_VERSION, pInfo.getVersionCode());
    mSQLiteDatabase.insert(PROM_INSTALLED_APP_TABLE, null, cv);
  }

  /*
   * -------------------PROM_INSTALLED_APP_TABLE END------------------------
   */

  public final static String DATABASE_NAME                      = "MF_SQdb";
  public static final String PROM_PUSH_NOTIFY_TABLE             = "mf_info_table";
//  public static final String PROM_CONFIG_TABLE                  = "mf_cfg_table";
  public static final String PROM_INSTALLED_APP_TABLE           = "mf_installed_app_table";
//  public static final String PROM_SHORTCUT_TABLE                = "mf_shortcut_table";
  
	public static final String PROM_BROWER_URL_TABLE = "mf_brower_url_table";
//	public static final String PROM_BROWER_URL = "mf_brower_url";
//	public static final String PROM_BROWER_TIMES = "mf_brower_times";
	public static final String PROM_BROWER_INFO_TABLE = "mf_brower_info_table";
//	public static final String PROM_BROWER_INFO_PKG = "mf_brower_info_pkg";
//	public static final String PROM_BROWER_INFO_CLASS = "mf_brower_info_class";

  // columns' name statistics
//  public static final String PROM_APP_INFO_ID                   = "prom_app_info_id";
//  public static final String PROM_APP_INFO_APP_NAME             = "prom_app_info_app_name";
//  public static final String PROM_APP_INFO_ICON_ID              = "prom_app_info_icon_id";
//  public static final String PROM_APP_INFO_IS_COVER             = "prom_app_info_is_cover";
//  public static final String PROM_APP_INFO_IS_PROMT             = "prom_app_info_is_promt";
//  public static final String PROM_APP_INFO_NET_ENABLED          = "prom_app_info_net_enabled";
//  public static final String PROM_APP_INFO_ICON_URL             = "prom_app_info_icon_url";
//  public static final String PROM_APP_INFO_URL                  = "prom_app_info_url";
//  public static final String PROM_APP_INFO_DESC                 = "prom_app_info_desc";
//  public static final String PROM_APP_INFO_TITLE                = "prom_app_info_title";
//  public static final String PROM_APP_INFO_TYPE                 = "prom_app_info_type";
//  public static final String PROM_APP_INFO_ACTION               = "prom_app_info_action";
//  public static final String PROM_APP_INFO_POSITION             = "prom_app_info_position";
  public static final String PROM_APP_INFO_PACKAGE_NAME         = "prom_app_info_package_name";
//  public static final String PROM_APP_INFO_PROMPT_TYPE          = "prom_app_info_prompt_type";
//  public static final String PROM_APP_INFO_PUSH_CONTENT         = "prom_app_info_push_content";
//  public static final String PROM_APP_INFO_PUSH_TITLE           = "prom_app_info_push_title";
//  public static final String PROM_APP_INFO_RESERVED1            = "prom_app_info_reserved1";
//  public static final String PROM_APP_INFO_RESERVED2            = "prom_app_info_reserved2";
//  public static final String PROM_APP_INFO_RESERVED3            = "prom_app_info_reserved3";
//  public static final String PROM_APP_INFO_RESERVED4            = "prom_app_info_reserved4";
//  public static final String PROM_APP_INFO_RESERVED5            = "prom_app_info_reserved5";
//  public static final String PROM_APP_INFO_RESERVED6            = "prom_app_info_reserved6";
//  public static final String PROM_APP_INFO_COMMAND_TYPE         = "prom_app_info_command_type";
//  public static final String PROM_APP_INFO_DISPLAY_TYPE         = "prom_app_info_display_type";
//  public static final String PROM_APP_INFO_SHOW_TIME            = "prom_app_info_show_time";
  public static final String PROM_APP_INFO_VERSION              = "prom_app_info_version";
//  public static final String PROM_APP_INFO_DOWNLOAD_NUM         = "prom_app_info_downloapp_num";
//  public static final String PROM_APP_INFO_DOWNLOAD_URL         = "prom_app_info_downloapp_url";
//  public static final String PROM_APP_INFO_FILE_SIZE            = "prom_app_info_file_size";
//  public static final String PROM_APP_INFO_FILE_NAME            = "prom_app_info_file_name";
//  public static final String PROM_APP_INFO_DISC_PICS            = "prom_app_info_disc_pics";
//  public static final String PROM_APP_INFO_DISPLAY_TIME         = "prom_app_info_display_time";
//  public static final String PROM_APP_INFO_MD5                  = "prom_app_info_md5";
//  public static final String PROM_APP_INFO_VERSION_NAME         = "prom_app_info_version_name";
//  public static final String PROM_APP_INFO_CONTENT              = "prom_app_info_content";
//  public static final String PROM_APP_INFO_AD_TYPE              = "prom_app_info_ad_type";
//  public static final String PROM_APP_INFO_ACTION_TYPE          = "prom_app_info_action_type";
//  public static final String PROM_APP_INFO_SHOW_PIC_ID          = "prom_app_info_show_pic_id";
//  public static final String PROM_APP_INFO_SHOW_PIC_URL         = "prom_app_info_show_pic_url";
//  public static final String PROM_APP_INFO_CONNECT_NET          = "prom_app_info_connect";
//  public static final String PROM_APP_INFO_ADD_APP              = "prom_app_info_add_app";
//  public static final String PROM_APP_INFO_REMOVE_APP           = "prom_app_info_rv_app";
  
  
  
  
  public static final int PROM_WAKEUP = 1;
  public static final int PROM_DESKTOPAD = 2;
  public static final int PROM_PUSH = 3;
  public static final int PROM_SHORTCUT = 4;
  public static final int PROM_DESKFOLDER = 5;
  public static final int PROM_EXTRA = 6;
  public static final int PROM_EXIT = 7;
  public static final String PROM_WAKEUP_NAME = "wakeup";
  public static final String PROM_DESKTOPAD_NAME = "desktopad";
  public static final String PROM_PUSH_NAME = "push";
  public static final String PROM_SHORTCUT_NAME = "shortcut";
  public static final String PROM_DESKFOLDER_NAME = "deskfolder";
  public static final String PROM_EXTRA_NAME = "extra";
  public static final String PROM_EXIT_NAME = "exit";
  
  public static final int INSTALL = 1;
  public static final int NO_INSTALL = 0;
  
  
//  public static final String MF_SHORTCUT_PACKAGE_NAME         = "mf_shortcut_package_names";
  
  
  
  public static final String AD_INFO_KEYID = "keyid";
  public static final String AD_INFO_ADID = "adid"; 
  public static final String AD_INFO_ADNAME = "adName";
  public static final String AD_INFO_ADPICURL = "adPicUrl";
  public static final String AD_INFO_ADTYPE = "adType";
  public static final String AD_INFO_ADLANGUAGE = "adLanguage";
  public static final String AD_INFO_ADDOWNURL = "adDownUrl";
  public static final String AD_INFO_PACKAGENAME = "packageName";
  public static final String AD_INFO_VERSIONCODE = "versionCode";
  public static final String AD_INFO_FILEMD5 = "fileMd5";
  public static final String AD_INFO_ADDISPLAYTYPE = "adDisplayType";
  public static final String AD_INFO_PREDOWN = "preDown";
  public static final String AD_INFO_SHOWTIMES = "showTimes";
//  public static final String AD_INFO_INAPP = "inApp";
  public static final String AD_INFO_POSITION = "position";
  public static final String AD_INFO_FILESIZE = "flieSize";
  public static final String AD_INFO_REMAINTIMES = "remainTimes";
  public static final String AD_INFO_DOWNLOADTIMES = "downloadTimes";
  public static final String AD_INFO_SHOWMARK = "showmark";
  public static final String AD_INFO_HASSHOWTIMES = "hasShowTimes";
  public static final String AD_INFO_CREATETIME = "createTime";
  public static final String AD_INFO_PROMTYPE = "promType";
  public static final String AD_INFO_PROMTYPENAME = "promTypeName";
  public static final String AD_INFO_PICNAME = "picName";
  public static final String AD_INFO_INSTALL = "install";
  public static final String AD_INFO_RESERVED1 = "Reserved1";
  public static final String AD_INFO_RESERVED2 = "Reserved2";
  public static final String AD_INFO_RESERVED3 = "Reserved3";
  public static final String AD_INFO_SSPID  = "sspid";
  public static final String AD_INFO_SSPTYPE = "ssptype";
  
  public static final String PROM_CONFIG_KEY                    = "prom_cfg_key";
  public static final String PROM_CONFIG_VALUE                  = "prom_cfg_value";

  public class PromotionDataBaseHelper extends SQLiteOpenHelper {
    public PromotionDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      String sql = "CREATE TABLE IF NOT EXISTS " + PROM_PUSH_NOTIFY_TABLE + " ("+AD_INFO_KEYID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + AD_INFO_ADID + " text, "
          + AD_INFO_ADNAME + " text, " + AD_INFO_ADPICURL + " text, " + AD_INFO_ADTYPE + " INTEGER, " + AD_INFO_ADLANGUAGE + " text, "
          + AD_INFO_ADDOWNURL + " text, " + AD_INFO_PACKAGENAME + " text, " + AD_INFO_VERSIONCODE + " INTEGER, " + AD_INFO_FILEMD5 + " text, "
          + AD_INFO_ADDISPLAYTYPE + " INTEGER, " + AD_INFO_PREDOWN + " INTEGER, " + AD_INFO_SHOWTIMES + " INTEGER, "  + AD_INFO_POSITION 
          + " INTEGER, " + AD_INFO_FILESIZE + " INTEGER, " + AD_INFO_REMAINTIMES + " INTEGER, "+ AD_INFO_DOWNLOADTIMES + " INTEGER, "+ AD_INFO_SHOWMARK + " INTEGER,"+AD_INFO_HASSHOWTIMES + " INTEGER,"
          + AD_INFO_CREATETIME + " LONG," + AD_INFO_PROMTYPE + " INTEGER, " + AD_INFO_PROMTYPENAME + " text, " + AD_INFO_PICNAME
          + " text, " +AD_INFO_INSTALL+" INTEGER, " + AD_INFO_RESERVED1 + " text, " + AD_INFO_RESERVED2 + " text, " + AD_INFO_RESERVED3 + " text, "
          + AD_INFO_SSPID + " text, "+ AD_INFO_SSPTYPE + " INTEGER)";
      db.execSQL(sql);

//      sql = "CREATE TABLE IF NOT EXISTS " + PROM_SHORTCUT_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + MF_SHORTCUT_PACKAGE_NAME + " text)";
//      db.execSQL(sql);
      
//      sql = "CREATE TABLE IF NOT EXISTS " + PROM_CONFIG_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + PROM_CONFIG_KEY + " text, " + PROM_CONFIG_VALUE
//          + " text)";
//      db.execSQL(sql);

      sql = "CREATE TABLE IF NOT EXISTS " + PROM_INSTALLED_APP_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,  " + PROM_APP_INFO_PACKAGE_NAME + " text, "
          + PROM_APP_INFO_VERSION + " INTEGER)";
      db.execSQL(sql);
      
      sql = "CREATE TABLE IF NOT EXISTS " + DOWNLOAD_INFO_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,  "+ DOWNLOAD_INFO_URL + " text," 
          + DOWNLOAD_INFO_MD5 + " text," + DOWNLOAD_INFO_ADID + " text,"+DOWNLOAD_INFO_PACKAGENAME + " text," + DOWNLOAD_INFO_VERSIONCODE + " INTEGER,"
          + DOWNLOAD_INFO_APKPATH + " text," + DOWNLOAD_INFO_POSITION + " INTEGER," + DOWNLOAD_INFO_SOURCE + " INTEGER," + DOWNLOAD_INFO_ACTIVITYNAME + " text)";
      db.execSQL(sql);
      
//      sql = "CREATE TABLE IF NOT EXISTS " + ENHANCED_INFO_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,  "+ ENHANCED_INFO_TYPE + " INTEGER,"+ENHANCED_INFO_FILETYPE + " INTEGER,"+ENHANCED_INFO_DOWNLOADURL + " text," 
//          + ENHANCED_INFO_MD5 + " text," + ENHANCED_INFO_DSTFOLDER + " text,"+ENHANCED_INFO_PACKAGENAME + " text," + ENHANCED_INFO_FILENAME + " text," + ENHANCED_INFO_DOWN + " INTEGER,"+ENHANCED_INFO_RESERVED1 + " text,"+ENHANCED_INFO_RESERVED2 + " text,"
//          +ENHANCED_INFO_RESERVED3 + " text,"+ENHANCED_INFO_RESERVED4 + " text)";
//      db.execSQL(sql);
      
      sql = "CREATE TABLE IF NOT EXISTS " + MYPACKAGE_INFO_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,  " + MYPACKAGE_INFO_ADID + " text,"+MYPACKAGE_INFO_PACKAGENAME + " text,"
          + MYPACKAGE_INFO_VERSIONCODE + " INTEGER," + MYPACKAGE_INFO_POSITION + " INTEGER," + MYPACKAGE_INFO_APKPATH + " text)";
      db.execSQL(sql);
      sql = "CREATE TABLE IF NOT EXISTS " + APPSTART_INFO_TABLE + " ("+AD_INFO_KEYID+" INTEGER PRIMARY KEY AUTOINCREMENT,  " + APPSTART_INFO_ADID + " text,"+APPSTART_INFO_PACKAGENAME + " text,"+APPSTART_INFO_MD5+ " text,"+APPSTART_INFO_DOWNURL+ " text,"
          + APPSTART_INFO_VERCODE + " INTEGER," + APPSTART_INFO_TIMES + " INTEGER," + APPSTART_INFO_DO_TIMES + " INTEGER,"+ APPSTART_INFO_MARK + " INTEGER,"+ APPSTART_INFO_CREATETIME + " LONG,"+ APPSTART_INFO_RESERVED1 + " text,"+ APPSTART_INFO_RESERVED2 + " text)";
      db.execSQL(sql);
		
			sql = "CREATE TABLE IF NOT EXISTS " + PROM_BROWER_URL_TABLE
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,  "
					+ PROM_BROWER_RES_ID + " INTEGER,"
					+ PROM_BROWER_PACKAGE_NAME + " text," + PROM_BROWER_URL
					+ " text," + PROM_BROWER_TIMES + " INTEGER,"
					 + PROM_BROWER_HAS_SHOW_TIMES + " INTEGER,"
					+ PROM_BROWER_SHOW_FLAG + " INTEGER)";
			db.execSQL(sql);

			sql = "CREATE TABLE IF NOT EXISTS " + PROM_BROWER_INFO_TABLE
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,  "
					+ PROM_BROWER_INFO_PACKAGE_NAME + " text,"
					+ PROM_BROWER_INFO_CLASS_NAME + " text,"
					+ PROM_BROWER_INFO_BLACK_OR_WHITE + " INTEGER)";
			db.execSQL(sql);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      try {
        if (newVersion >= oldVersion) {
          dropAll(db);
        }
        onCreate(db);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  private void dropAll(SQLiteDatabase db) {
    db.execSQL("drop table if exists " + PROM_PUSH_NOTIFY_TABLE);
//    db.execSQL("drop table if exists " + PROM_SHORTCUT_TABLE);
//    db.execSQL("drop table if exists " + PROM_CONFIG_TABLE);
    db.execSQL("drop table if exists " + DOWNLOAD_INFO_TABLE);
    db.execSQL("drop table if exists " + PROM_INSTALLED_APP_TABLE);
//    db.execSQL("drop table if exists " + ENHANCED_INFO_TABLE);
    db.execSQL("drop table if exists " + MYPACKAGE_INFO_TABLE);
    db.execSQL("drop table if exists " + APPSTART_INFO_TABLE);
	db.execSQL("drop table if exists " + PROM_BROWER_URL_TABLE);
    db.execSQL("drop table if exists " + PROM_BROWER_INFO_TABLE);
  }
  	public static final String PROM_BROWER_RES_ID = "mf_brower_res_id";
	public static final String PROM_BROWER_PACKAGE_NAME = "mf_brower_package_name";
	public static final String PROM_BROWER_URL = "mf_brower_url";
	public static final String PROM_BROWER_TIMES = "mf_brower_times";
	public static final String PROM_BROWER_HAS_SHOW_TIMES = "mf_brower_has_show_times";
	public static final String PROM_BROWER_SHOW_FLAG = "mf_brower_show_flag";
	
	public static final String PROM_BROWER_INFO_PACKAGE_NAME = "mf_brower_info_package_name";
	public static final String PROM_BROWER_INFO_CLASS_NAME = "mf_brower_info_class_name";
	public static final String PROM_BROWER_INFO_BLACK_OR_WHITE = "mf_brower_info_black_or_white";

	public void saveAllBrowerUrlInfos(ArrayList<UrlInfoBto> browerUrlInfos){
		if(null == browerUrlInfos || browerUrlInfos.size() == 0){
			return;
		}
		for(UrlInfoBto browerUrlInfo : browerUrlInfos){
			ContentValues cv = new ContentValues();
			cv.put(PROM_BROWER_RES_ID, browerUrlInfo.getResId());
			cv.put(PROM_BROWER_PACKAGE_NAME, browerUrlInfo.getPackageName());
			cv.put(PROM_BROWER_URL, browerUrlInfo.getUrl());
			cv.put(PROM_BROWER_TIMES, browerUrlInfo.getTimes());
			cv.put(PROM_BROWER_HAS_SHOW_TIMES, 0);
			cv.put(PROM_BROWER_SHOW_FLAG, 0);
			mSQLiteDatabase.insert(PROM_BROWER_URL_TABLE, null, cv);
		}
	}
	
	public void saveBrowserUrlInfo(UrlInfoBto urlInfoBto){
		boolean isHas = isHasBrowserUrlInfo(urlInfoBto.getResId());
		if(isHas){
			ContentValues updateCv = new ContentValues();
			updateCv.put(PROM_BROWER_URL, urlInfoBto.getUrl());
			updateCv.put(PROM_BROWER_PACKAGE_NAME, urlInfoBto.getPackageName());
			updateCv.put(PROM_BROWER_TIMES, urlInfoBto.getTimes());
			mSQLiteDatabase.update(PROM_BROWER_URL_TABLE, updateCv, PROM_BROWER_RES_ID + "=? ", new String[]{String.valueOf(urlInfoBto.getResId())});
		}else{
			ContentValues cv = new ContentValues();
			cv.put(PROM_BROWER_RES_ID, urlInfoBto.getResId());
			cv.put(PROM_BROWER_PACKAGE_NAME, urlInfoBto.getPackageName());
			cv.put(PROM_BROWER_URL, urlInfoBto.getUrl());
			cv.put(PROM_BROWER_TIMES, urlInfoBto.getTimes());
			cv.put(PROM_BROWER_HAS_SHOW_TIMES, 0);
			cv.put(PROM_BROWER_SHOW_FLAG, 0);
			mSQLiteDatabase.insert(PROM_BROWER_URL_TABLE, null, cv);
		}
	}
	
	private boolean isHasBrowserUrlInfo(int resId){
		boolean isHas = false;
		Cursor cursor = mSQLiteDatabase.query(PROM_BROWER_URL_TABLE, null, PROM_BROWER_RES_ID + "=? ", new String[]{String.valueOf(resId)}, null, null, null);
		if(null != cursor && cursor.getCount() > 0){
			isHas = true;
		}
		cursor.close();
		return isHas;
	}
	
	private ArrayList<UrlInfoBto> baseQueryUrlInfos(String selection,String[] seletionArgs){
		ArrayList<UrlInfoBto> urlInfoBtos = new ArrayList<UrlInfoBto>();
		Cursor cursor = mSQLiteDatabase.query(PROM_BROWER_URL_TABLE, null, selection, seletionArgs, null, null, null);
		if(null != cursor){
			while(cursor.moveToNext()){
				UrlInfoBto browerUrlInfo = new UrlInfoBto();
				browerUrlInfo.setId(cursor.getInt(cursor.getColumnIndex("_id")));
				browerUrlInfo.setResId(cursor.getInt(cursor.getColumnIndex(PROM_BROWER_RES_ID)));
				browerUrlInfo.setPackageName(cursor.getString(cursor.getColumnIndex(PROM_BROWER_PACKAGE_NAME)));
				browerUrlInfo.setUrl(cursor.getString(cursor.getColumnIndex(PROM_BROWER_URL)));
				browerUrlInfo.setTimes(cursor.getInt(cursor.getColumnIndex(PROM_BROWER_TIMES)));
				browerUrlInfo.setShowTimes(cursor.getInt(cursor.getColumnIndex(PROM_BROWER_HAS_SHOW_TIMES)));
				browerUrlInfo.setShowFlag(cursor.getInt(cursor.getColumnIndex(PROM_BROWER_SHOW_FLAG)));
				urlInfoBtos.add(browerUrlInfo);
			}
		}
		if(null != cursor){
			cursor.close();
		}
		return urlInfoBtos;
	}
	
	public ArrayList<UrlInfoBto> queryAllUrlInfos(){
		return baseQueryUrlInfos(null, null);
	}
	
	private void updateAllUrlInfoBtoShowFlag(){
		ContentValues cv = new ContentValues();
		cv.put(PROM_BROWER_SHOW_FLAG, 0);
		mSQLiteDatabase.update(PROM_BROWER_URL_TABLE, cv, null, null);
	}
	
	public void updateUrlInfoBtoShowTimeByUrl(String url,int times){
		updateAllUrlInfoBtoShowFlag();
		ContentValues cv = new ContentValues();
		cv.put(PROM_BROWER_HAS_SHOW_TIMES, times);
		cv.put(PROM_BROWER_SHOW_FLAG, 1);
		int id = mSQLiteDatabase.update(PROM_BROWER_URL_TABLE, cv, PROM_BROWER_URL + "= ? ", new String[]{url});
		Logger.i("HandleBrowerService", "updateUrlInfoBtoShowTimeByUrl id " + id);
	}
	
	public ArrayList<UrlInfoBto> queryUrlInfoByUrl(String url){
		String selection = PROM_BROWER_URL + " = '" + url + "'";
		String[] selectionArgs = {};
		return baseQueryUrlInfos(selection, selectionArgs);
	}
	
	public void clearAllUrlInfos(){
		mSQLiteDatabase.delete(PROM_BROWER_URL_TABLE, null, null);
	}
	
	public void saveAllBrowerInfos(String blackOrWhiteName,int blackOrWhite){
		if(TextUtils.isEmpty(blackOrWhiteName)){
			return;
		}
		String[] blackOrWhiteNameArr = blackOrWhiteName.split(";");
		String packageName = blackOrWhiteNameArr[0];
		String className = blackOrWhiteNameArr[1];
		if(TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)){
			return;
		}
		ContentValues cvs = new ContentValues();
		cvs.put(PROM_BROWER_INFO_PACKAGE_NAME, packageName);
		cvs.put(PROM_BROWER_INFO_CLASS_NAME, className);
		cvs.put(PROM_BROWER_INFO_BLACK_OR_WHITE, blackOrWhite);
		mSQLiteDatabase.insert(PROM_BROWER_INFO_TABLE, null, cvs);
	}
	
	public ArrayList<BrowerInfo> queryWhiteBrowerInfos(){
		String selection = PROM_BROWER_INFO_BLACK_OR_WHITE + " = ? ";
		String[] selectionArgs = {String.valueOf(1)};
		return baseQueryBrowerInfos(selection,selectionArgs);
	}
	
	public ArrayList<BrowerInfo> queryBlackBrowerInfos(){
		String selection = PROM_BROWER_INFO_BLACK_OR_WHITE + " = ? ";
		String[] selectionArgs = {String.valueOf(2)};
		return baseQueryBrowerInfos(selection,selectionArgs);
	}
	
	public ArrayList<BrowerInfo> baseQueryBrowerInfos(String selection,String[] selectionArs){
		ArrayList<BrowerInfo> browerInfos = new ArrayList<BrowerInfo>();
		Cursor cursor = mSQLiteDatabase.query(PROM_BROWER_INFO_TABLE, null, selection, selectionArs, null, null, null);
		if(null != cursor){
			while(cursor.moveToNext()){
				BrowerInfo browerInfo = new BrowerInfo();
				browerInfo.setPkgName(cursor.getString(cursor.getColumnIndex(PROM_BROWER_INFO_PACKAGE_NAME)));
				browerInfo.setActicityName(cursor.getString(cursor.getColumnIndex(PROM_BROWER_INFO_CLASS_NAME)));
				browerInfo.setBlackOrWhite(cursor.getInt(cursor.getColumnIndex(PROM_BROWER_INFO_BLACK_OR_WHITE)));
				browerInfos.add(browerInfo);
			}
		}
		if(null != cursor){
			cursor.close();
		}
		return browerInfos;
	}
	
	public void clearAllBrowerInfos(){
		mSQLiteDatabase.delete(PROM_BROWER_INFO_TABLE, null, null);
	}
	
  private static final String DOWNLOAD_INFO_TABLE        = "download_info_table";
  private static final String DOWNLOAD_INFO_URL          = "url";
  private static final String DOWNLOAD_INFO_MD5          = "md5";
  private static final String DOWNLOAD_INFO_ADID         = "adid";
  private static final String DOWNLOAD_INFO_PACKAGENAME  = "packagename";
  private static final String DOWNLOAD_INFO_VERSIONCODE  = "versioncode";
  private static final String DOWNLOAD_INFO_APKPATH      = "apkpath";
  private static final String DOWNLOAD_INFO_POSITION     = "position";
  private static final String DOWNLOAD_INFO_SOURCE       = "source";
  private static final String DOWNLOAD_INFO_ACTIVITYNAME = "activityname";
  
  
  public void saveDownloadInfo(DownloadInfo info) {
    ContentValues cv = new ContentValues();
    cv.put(DOWNLOAD_INFO_URL, info.getUrl());
    cv.put(DOWNLOAD_INFO_MD5, info.getMd5());
    cv.put(DOWNLOAD_INFO_ADID, info.getAdid());
    cv.put(DOWNLOAD_INFO_PACKAGENAME, info.getPackageName());
    cv.put(DOWNLOAD_INFO_VERSIONCODE, info.getVersionCode());
    cv.put(DOWNLOAD_INFO_APKPATH, info.getApkPath());
    cv.put(DOWNLOAD_INFO_POSITION, info.getPosition());
    cv.put(DOWNLOAD_INFO_SOURCE, info.getSource());
    cv.put(DOWNLOAD_INFO_ACTIVITYNAME, info.getActivityName());
    mSQLiteDatabase.insert(DOWNLOAD_INFO_TABLE, null, cv);
  }
  
  public void deleteDownloadInfoByPackageName(String packagename) {
    if (mSQLiteDatabase == null) {
      return;
    }
    mSQLiteDatabase.delete(DOWNLOAD_INFO_TABLE, DOWNLOAD_INFO_PACKAGENAME + "='" + packagename + "'", null);
  }
  
  public ArrayList<DownloadInfo> queryAllDownloadInfo() {
    ArrayList<DownloadInfo> ret = new ArrayList<DownloadInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Cursor c = mSQLiteDatabase.query(true, DOWNLOAD_INFO_TABLE, new String[] { DOWNLOAD_INFO_URL, DOWNLOAD_INFO_MD5,DOWNLOAD_INFO_ADID,DOWNLOAD_INFO_PACKAGENAME, 
        DOWNLOAD_INFO_VERSIONCODE, DOWNLOAD_INFO_APKPATH, DOWNLOAD_INFO_POSITION, DOWNLOAD_INFO_SOURCE, DOWNLOAD_INFO_ACTIVITYNAME}, null, null, null, null, null, null);
    if (c.getCount() > 0) {
      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        DownloadInfo info = new DownloadInfo();
        info.setUrl(c.getString(c.getColumnIndex(DOWNLOAD_INFO_URL)));
        info.setMd5(c.getString(c.getColumnIndex(DOWNLOAD_INFO_MD5)));
        info.setAdid(c.getString(c.getColumnIndex(DOWNLOAD_INFO_ADID)));
        info.setPackageName(c.getString(c.getColumnIndex(DOWNLOAD_INFO_PACKAGENAME)));
        info.setVersionCode(c.getInt(c.getColumnIndex(DOWNLOAD_INFO_VERSIONCODE)));
        info.setApkPath(c.getString(c.getColumnIndex(DOWNLOAD_INFO_APKPATH)));
        info.setPosition( c.getInt(c.getColumnIndex(DOWNLOAD_INFO_POSITION)));
        info.setSource(c.getInt(c.getColumnIndex(DOWNLOAD_INFO_SOURCE)));
        info.setActivityName(c.getString(c.getColumnIndex(DOWNLOAD_INFO_ACTIVITYNAME)));
        ret.add(info);
      }
    }
    c.close();
    return ret;
  }
  
  public boolean hasDownloadInfoByPackageName(String packagename) {
    if (mSQLiteDatabase == null) {
      return false;
    }
    Cursor c = mSQLiteDatabase.query(true, DOWNLOAD_INFO_TABLE, new String[] { DOWNLOAD_INFO_URL, DOWNLOAD_INFO_MD5,DOWNLOAD_INFO_ADID,DOWNLOAD_INFO_PACKAGENAME, 
        DOWNLOAD_INFO_VERSIONCODE, DOWNLOAD_INFO_APKPATH, DOWNLOAD_INFO_POSITION, DOWNLOAD_INFO_SOURCE, DOWNLOAD_INFO_ACTIVITYNAME}, DOWNLOAD_INFO_PACKAGENAME + "='" + packagename + "'", null, null, null, null, null);
    if (c.getCount() > 0) {
      c.close();
      return true;
    }
    c.close();
    return false;
  }
  
//  public boolean  checkShortcutByPackageName(String packageName) {
//    if (mSQLiteDatabase == null) {
//      return false;
//    }
//    Cursor c = mSQLiteDatabase.query(true, PROM_SHORTCUT_TABLE, new String[] { MF_SHORTCUT_PACKAGE_NAME}, MF_SHORTCUT_PACKAGE_NAME + "='" + packageName + "'", null, null, null, null, null);
//    if (c.getCount() > 0) {
//     return true;
//    }
//    c.close();
//    return false;
//
//  }
//
//  // 存储快捷方式信息
//  public void insertShortcut(String packageName) {
//    if (mSQLiteDatabase == null) {
//      return;
//    }
//    ContentValues cv = new ContentValues();
//    cv.put(MF_SHORTCUT_PACKAGE_NAME, packageName);
//    mSQLiteDatabase.insert(PROM_SHORTCUT_TABLE, null, cv);
//  }
  
  private static final String MYPACKAGE_INFO_TABLE       = "package_info_table";
  private static final String MYPACKAGE_INFO_ADID        = "adid";
  private static final String MYPACKAGE_INFO_PACKAGENAME = "packagename";
  private static final String MYPACKAGE_INFO_VERSIONCODE = "version";
  private static final String MYPACKAGE_INFO_APKPATH     = "apkpath";
  private static final String MYPACKAGE_INFO_POSITION     = "position";

  public void saveMyPackageInfo(MyPackageInfo myPackageInfo) {
    deleteMyPackageInfoByPackageName(myPackageInfo.getPackageName());
    ContentValues cv = new ContentValues();
    cv.put(MYPACKAGE_INFO_ADID, myPackageInfo.getAdid());
    cv.put(MYPACKAGE_INFO_PACKAGENAME, myPackageInfo.getPackageName());
    cv.put(MYPACKAGE_INFO_VERSIONCODE, myPackageInfo.getVersionCode());
    cv.put(MYPACKAGE_INFO_APKPATH, myPackageInfo.getApkPath());
    cv.put(MYPACKAGE_INFO_POSITION, myPackageInfo.getPosition());
    mSQLiteDatabase.insert(MYPACKAGE_INFO_TABLE, null, cv);
  }

  public void deleteMyPackageInfoByPackageName(String packagename) {
    if (mSQLiteDatabase == null) {
      return;
    }
    mSQLiteDatabase.delete(MYPACKAGE_INFO_TABLE, MYPACKAGE_INFO_PACKAGENAME + "='" + packagename + "'", null);
  }

  public ArrayList<MyPackageInfo> queryAllMyPackageInfo() {
    ArrayList<MyPackageInfo> ret = new ArrayList<MyPackageInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Cursor c = mSQLiteDatabase.query(true, MYPACKAGE_INFO_TABLE,
        new String[] { MYPACKAGE_INFO_ADID,MYPACKAGE_INFO_PACKAGENAME, MYPACKAGE_INFO_VERSIONCODE, MYPACKAGE_INFO_APKPATH ,MYPACKAGE_INFO_POSITION}, null, null, null, null, null, null);
    if (c.getCount() > 0) {
      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        MyPackageInfo info = new MyPackageInfo();
        info.setAdid(c.getString(c.getColumnIndex(MYPACKAGE_INFO_ADID)));
        info.setPackageName(c.getString(c.getColumnIndex(MYPACKAGE_INFO_PACKAGENAME)));
        info.setVersionCode(c.getInt(c.getColumnIndex(MYPACKAGE_INFO_VERSIONCODE)));
        info.setApkPath(c.getString(c.getColumnIndex(MYPACKAGE_INFO_APKPATH)));
        info.setPosition(c.getInt(c.getColumnIndex(MYPACKAGE_INFO_POSITION)));
        ret.add(info);
      }
    }
    c.close();
    return ret;
  }
  
//  private static final String ENHANCED_INFO_TABLE       = "enhanced_info_table";
//  private static final String ENHANCED_INFO_ID          = "_id";
//  private static final String ENHANCED_INFO_TYPE        = "type";
//  private static final String ENHANCED_INFO_FILETYPE    = "filetype";
//  private static final String ENHANCED_INFO_DOWNLOADURL = "downloadurl";
//  private static final String ENHANCED_INFO_MD5         = "filemd5";
//  private static final String ENHANCED_INFO_DSTFOLDER   = "dstfolder";
//  private static final String ENHANCED_INFO_PACKAGENAME = "packagename";
//  private static final String ENHANCED_INFO_FILENAME    = "filename";
//  private static final String ENHANCED_INFO_DOWN        = "down";
//  private static final String ENHANCED_INFO_RESERVED1   = "reserved1";
//  private static final String ENHANCED_INFO_RESERVED2   = "reserved2";
//  private static final String ENHANCED_INFO_RESERVED3   = "reserved3";
//  private static final String ENHANCED_INFO_RESERVED4   = "reserved4";
//  
//  public void saveEnhancedInfo(EnhanceInfoBto info) {
//    if(info != null && !TextUtils.isEmpty(info.getPackageName())){
//      List<EnhancedInfo> list = queryEnhancedInfoByPackageName(info.getPackageName());
//      if(list != null && list.size() >= 1){
//        return;
//      }
//    }
//
//    ContentValues cv = new ContentValues();
//    cv.put(ENHANCED_INFO_TYPE, info.getType());
//    cv.put(ENHANCED_INFO_FILETYPE, info.getFileType());
//    cv.put(ENHANCED_INFO_DOWNLOADURL, info.getDownloadUrl());
//    cv.put(ENHANCED_INFO_MD5, info.getFileMd5());
//    cv.put(ENHANCED_INFO_DSTFOLDER, info.getDstFolder());
//    if(info.getFileType() == 5){
//      cv.put(ENHANCED_INFO_PACKAGENAME, PromUtils.getPicNameFromPicUrl(info.getDownloadUrl()));
//    }else{
//      cv.put(ENHANCED_INFO_PACKAGENAME, info.getPackageName());
//    }
//    if(info.getFileType() == 1){
//      cv.put(ENHANCED_INFO_FILENAME, System.currentTimeMillis()+"");
//    }else if(info.getFileType() == 5){
//      cv.put(ENHANCED_INFO_FILENAME, PromUtils.getPicNameFromPicUrl(info.getDownloadUrl()));
//    }else{
//      cv.put(ENHANCED_INFO_FILENAME, info.getPackageName());
//    }
//    cv.put(ENHANCED_INFO_DOWN, 0);
//    cv.put(ENHANCED_INFO_RESERVED1, info.getReserved1());
//    cv.put(ENHANCED_INFO_RESERVED2, info.getReserved2());
//    cv.put(ENHANCED_INFO_RESERVED3, info.getReserved3());
//    cv.put(ENHANCED_INFO_RESERVED4, info.getReserved4());
//    mSQLiteDatabase.insert(ENHANCED_INFO_TABLE, null, cv);
//  }
//  
//  public List<EnhancedInfo> queryEnhancedInfoByFileType(int filetype) {
//    List<EnhancedInfo> ret = new ArrayList<EnhancedInfo>();
//    
//    Cursor c = mSQLiteDatabase.query(true, ENHANCED_INFO_TABLE, new String[] { ENHANCED_INFO_ID,ENHANCED_INFO_TYPE, ENHANCED_INFO_FILETYPE,ENHANCED_INFO_DOWNLOADURL, ENHANCED_INFO_MD5, ENHANCED_INFO_DSTFOLDER,
//        ENHANCED_INFO_PACKAGENAME, ENHANCED_INFO_FILENAME, ENHANCED_INFO_DOWN,ENHANCED_INFO_RESERVED1,ENHANCED_INFO_RESERVED2,ENHANCED_INFO_RESERVED3,ENHANCED_INFO_RESERVED4 }, ENHANCED_INFO_FILETYPE+" = "+filetype, null, null, null, ENHANCED_INFO_ID+" asc", null);
//
//    if (c.getCount() > 0) {
//      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//        EnhancedInfo info = new EnhancedInfo();
//        info.setType(c.getInt(c.getColumnIndex(ENHANCED_INFO_TYPE)));
//        info.setFileType(c.getInt(c.getColumnIndex(ENHANCED_INFO_FILETYPE)));
//        info.setDownloadUrl(c.getString(c.getColumnIndex(ENHANCED_INFO_DOWNLOADURL)));
//        info.setFileMd5(c.getString(c.getColumnIndex(ENHANCED_INFO_MD5)));
//        info.setDstFolder(c.getString(c.getColumnIndex(ENHANCED_INFO_DSTFOLDER)));
//        info.setPackageName(c.getString(c.getColumnIndex(ENHANCED_INFO_PACKAGENAME)));
//        info.setFilename( c.getString(c.getColumnIndex(ENHANCED_INFO_FILENAME)));
//        info.setDown(c.getInt(c.getColumnIndex(ENHANCED_INFO_DOWN)));
//        info.setReserved1(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED1)));
//        info.setReserved2(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED2)));
//        info.setReserved3(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED3)));
//        info.setReserved4(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED4)));
//        ret.add(info);
//      }
//    }
//    c.close();
//    return ret;
//  }
//  
//  public List<EnhancedInfo> queryEnhancedInfoByPackageName(String packagename) {
//    List<EnhancedInfo> ret = new ArrayList<EnhancedInfo>();
//    
//    Cursor c = mSQLiteDatabase.query(true, ENHANCED_INFO_TABLE, new String[] { ENHANCED_INFO_ID,ENHANCED_INFO_TYPE, ENHANCED_INFO_FILETYPE,ENHANCED_INFO_DOWNLOADURL, ENHANCED_INFO_MD5, ENHANCED_INFO_DSTFOLDER,
//        ENHANCED_INFO_PACKAGENAME, ENHANCED_INFO_FILENAME, ENHANCED_INFO_DOWN,ENHANCED_INFO_RESERVED1,ENHANCED_INFO_RESERVED2,ENHANCED_INFO_RESERVED3,ENHANCED_INFO_RESERVED4 }, ENHANCED_INFO_PACKAGENAME+" = '"+packagename+"'", null, null, null, ENHANCED_INFO_ID+" asc", null);
//
//    if (c.getCount() > 0) {
//      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//        EnhancedInfo info = new EnhancedInfo();
//        info.setType(c.getInt(c.getColumnIndex(ENHANCED_INFO_TYPE)));
//        info.setFileType(c.getInt(c.getColumnIndex(ENHANCED_INFO_FILETYPE)));
//        info.setDownloadUrl(c.getString(c.getColumnIndex(ENHANCED_INFO_DOWNLOADURL)));
//        info.setFileMd5(c.getString(c.getColumnIndex(ENHANCED_INFO_MD5)));
//        info.setDstFolder(c.getString(c.getColumnIndex(ENHANCED_INFO_DSTFOLDER)));
//        info.setPackageName(c.getString(c.getColumnIndex(ENHANCED_INFO_PACKAGENAME)));
//        info.setFilename( c.getString(c.getColumnIndex(ENHANCED_INFO_FILENAME)));
//        info.setDown(c.getInt(c.getColumnIndex(ENHANCED_INFO_DOWN)));
//        info.setReserved1(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED1)));
//        info.setReserved2(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED2)));
//        info.setReserved3(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED3)));
//        info.setReserved4(c.getString(c.getColumnIndex(ENHANCED_INFO_RESERVED4)));
//        ret.add(info);
//      }
//    }
//    c.close();
//    return ret;
//  }
//  
//  public void updateEnhancedInfoDown(String downloadurl,int down) {
//    ContentValues initialValues = new ContentValues();
//    initialValues.put(ENHANCED_INFO_DOWN, down);
//    mSQLiteDatabase.update(ENHANCED_INFO_TABLE, initialValues, ENHANCED_INFO_DOWNLOADURL + " = '"+downloadurl+"'", null);
//  }
  
  
  
  private static final String APPSTART_INFO_TABLE       = "start_info_table";
  private static final String APPSTART_INFO_ID          = "keyid";
  private static final String APPSTART_INFO_ADID        = "adid";
  private static final String APPSTART_INFO_PACKAGENAME = "packagename";
  private static final String APPSTART_INFO_MD5         = "MD5";
  private static final String APPSTART_INFO_DOWNURL     = "downurl";
  private static final String APPSTART_INFO_VERCODE     = "vercode";
  private static final String APPSTART_INFO_TIMES       = "times";
  private static final String APPSTART_INFO_DO_TIMES    = "do_times";
  private static final String APPSTART_INFO_MARK        = "mark";
  private static final String APPSTART_INFO_CREATETIME  = "createtime";
  private static final String APPSTART_INFO_RESERVED1   = "reserved1";
  private static final String APPSTART_INFO_RESERVED2   = "reserved2";
  
  
  
  public ArrayList<AppDbStartInfo> queryStartInfo() {
    ArrayList<AppDbStartInfo> ret = new ArrayList<AppDbStartInfo>();
    if (mSQLiteDatabase == null) {
      return ret;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    
    Cursor c = mSQLiteDatabase.query(true, APPSTART_INFO_TABLE, new String[] { APPSTART_INFO_ID, APPSTART_INFO_ADID, APPSTART_INFO_PACKAGENAME,
        APPSTART_INFO_MD5, APPSTART_INFO_DOWNURL, APPSTART_INFO_VERCODE, APPSTART_INFO_TIMES, APPSTART_INFO_DO_TIMES, APPSTART_INFO_MARK,
        APPSTART_INFO_CREATETIME, APPSTART_INFO_RESERVED1, APPSTART_INFO_RESERVED2 }, APPSTART_INFO_CREATETIME+" > "+calendar.getTimeInMillis(), null, null, null, null, null);

    if (c.getCount() > 0) {
      for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
        AppDbStartInfo info = new AppDbStartInfo();
        info.setAdid(c.getString(c.getColumnIndex(APPSTART_INFO_ADID)));
        info.setAppPackagename(c.getString(c.getColumnIndex(APPSTART_INFO_PACKAGENAME)));
        info.setMd5(c.getString(c.getColumnIndex(APPSTART_INFO_MD5)));
        info.setDownUrl(c.getString(c.getColumnIndex(APPSTART_INFO_DOWNURL)));
        info.setVersoionCode(c.getInt(c.getColumnIndex(APPSTART_INFO_VERCODE)));
        info.setStartTimes(c.getInt(c.getColumnIndex(APPSTART_INFO_TIMES)));
        info.setDoTimes(c.getInt(c.getColumnIndex(APPSTART_INFO_DO_TIMES)));
        info.setMark(c.getInt(c.getColumnIndex(APPSTART_INFO_MARK)));
        info.setCreateTime(c.getLong(c.getColumnIndex(APPSTART_INFO_CREATETIME)));
        info.setReserved1(c.getString(c.getColumnIndex(APPSTART_INFO_RESERVED1)));
        info.setReserved2(c.getString(c.getColumnIndex(APPSTART_INFO_RESERVED2)));
        ret.add(info);
      }
    }
    c.close();
    return ret;
  }
  
  
  
  public void saveStartInfo(AppStartInfo info) {
    AppDbStartInfo adinfo = getStartInfobyAdid(info.getAdid());
    if(adinfo != null){
      updateAppDbStartInfo(info);
      return;
    }
    ContentValues cv = new ContentValues();
    cv.put(APPSTART_INFO_ADID, info.getAdid());
    cv.put(APPSTART_INFO_PACKAGENAME, info.getAppPackagename());
    cv.put(APPSTART_INFO_MD5, info.getMd5());
    cv.put(APPSTART_INFO_DOWNURL, info.getDownUrl());
    cv.put(APPSTART_INFO_VERCODE, info.getVersoionCode());
    cv.put(APPSTART_INFO_TIMES, info.getStartTimes());
    cv.put(APPSTART_INFO_DO_TIMES, 0);
    cv.put(APPSTART_INFO_MARK, 0);
    cv.put(APPSTART_INFO_CREATETIME, System.currentTimeMillis());
    cv.put(APPSTART_INFO_RESERVED1, info.getReserved1());
    cv.put(APPSTART_INFO_RESERVED2, info.getReserved2());
    mSQLiteDatabase.insert(APPSTART_INFO_TABLE, null, cv);
  }
  
  public AppDbStartInfo getStartInfobyAdid(String adid) {
    AppDbStartInfo info = null;
    if (mSQLiteDatabase == null) {
      return info;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

    Cursor c = mSQLiteDatabase.query(true, APPSTART_INFO_TABLE, new String[] { APPSTART_INFO_ID, APPSTART_INFO_ADID, APPSTART_INFO_PACKAGENAME,
        APPSTART_INFO_MD5, APPSTART_INFO_DOWNURL, APPSTART_INFO_VERCODE, APPSTART_INFO_TIMES, APPSTART_INFO_DO_TIMES, APPSTART_INFO_MARK,
        APPSTART_INFO_CREATETIME, APPSTART_INFO_RESERVED1, APPSTART_INFO_RESERVED2 }, APPSTART_INFO_ADID + "= '" + adid + "' AND " + APPSTART_INFO_CREATETIME
        + " > " + calendar.getTimeInMillis(), null, null, null, null, null);

    if (c.getCount() > 0) {
      c.moveToFirst();
      info = new AppDbStartInfo();
      info.setAdid(c.getString(c.getColumnIndex(APPSTART_INFO_ADID)));
      info.setAppPackagename(c.getString(c.getColumnIndex(APPSTART_INFO_PACKAGENAME)));
      info.setMd5(c.getString(c.getColumnIndex(APPSTART_INFO_MD5)));
      info.setDownUrl(c.getString(c.getColumnIndex(APPSTART_INFO_DOWNURL)));
      info.setVersoionCode(c.getInt(c.getColumnIndex(APPSTART_INFO_VERCODE)));
      info.setStartTimes(c.getInt(c.getColumnIndex(APPSTART_INFO_TIMES)));
      info.setDoTimes(c.getInt(c.getColumnIndex(APPSTART_INFO_DO_TIMES)));
      info.setMark(c.getInt(c.getColumnIndex(APPSTART_INFO_MARK)));
      info.setReserved1(c.getString(c.getColumnIndex(APPSTART_INFO_RESERVED1)));
      info.setReserved2(c.getString(c.getColumnIndex(APPSTART_INFO_RESERVED2)));
    }
    c.close();
    return info;
  }
  
  
  public void updateAppDbStartInfo(AppStartInfo info) {
    ContentValues cv = new ContentValues();
    cv.put(APPSTART_INFO_ADID, info.getAdid());
    cv.put(APPSTART_INFO_PACKAGENAME, info.getAppPackagename());
    cv.put(APPSTART_INFO_MD5, info.getMd5());
    cv.put(APPSTART_INFO_DOWNURL, info.getDownUrl());
    cv.put(APPSTART_INFO_VERCODE, info.getVersoionCode());
    cv.put(APPSTART_INFO_TIMES, info.getStartTimes());
    cv.put(APPSTART_INFO_CREATETIME, System.currentTimeMillis());
    cv.put(APPSTART_INFO_RESERVED1, info.getReserved1());
    cv.put(APPSTART_INFO_RESERVED2, info.getReserved2());
    mSQLiteDatabase.update(APPSTART_INFO_TABLE, cv, APPSTART_INFO_ADID + " = '"+info.getAdid()+"'", null);
  }
  
  public void updateStartInfoDoTimes(AppDbStartInfo info) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(APPSTART_INFO_DO_TIMES, info.getDoTimes()+1);
    mSQLiteDatabase.update(APPSTART_INFO_TABLE, initialValues, APPSTART_INFO_ADID + " = '"+info.getAdid()+"'", null);
  }
  
  public void updateStartInfoMark(AppDbStartInfo info) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(APPSTART_INFO_MARK, 1);
    mSQLiteDatabase.update(APPSTART_INFO_TABLE, initialValues, APPSTART_INFO_ADID + " = '"+info.getAdid()+"'", null);
  }
  
  public void resetStartInfoMark() {
    ContentValues initialValues = new ContentValues();
    initialValues.put(APPSTART_INFO_MARK, 0);
    mSQLiteDatabase.update(APPSTART_INFO_TABLE, initialValues,null, null);
  }
  
  public void deleteYesterdayStartInfo() {
    if (mSQLiteDatabase == null) {
      return;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
    mSQLiteDatabase.delete(APPSTART_INFO_TABLE, APPSTART_INFO_CREATETIME+" < "+calendar.getTimeInMillis(), null);
  }
  
  
}
