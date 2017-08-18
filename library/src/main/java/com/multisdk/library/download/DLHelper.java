package com.multisdk.library.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DLHelper extends SQLiteOpenHelper {

  private static final String DOWNLOAD_NAME = "download_name.db";
  private static final int DOWNLOAD_VERSION = 1;
  private static final String DOWNLOAD_TABLE = "download_table";
  private static final String DOWNLOAD_PATH = "download_path";
  private static final String THREAD_ID = "thread_id";
  private static final String DOWNLOAD_DOWN_LENGTH = "download_length";

  static final String FileService_getDataStr = "select "+THREAD_ID+", "+DOWNLOAD_DOWN_LENGTH+" from "+DOWNLOAD_TABLE+" where "+DOWNLOAD_PATH+"=?";
  static final String FileService_saveStr = "insert into "+DOWNLOAD_TABLE+"("+DOWNLOAD_PATH+", "+THREAD_ID+", "+DOWNLOAD_DOWN_LENGTH+") values(?,?,?)";
  static final String FileService_updateStr = "update "+DOWNLOAD_TABLE+" set "+DOWNLOAD_DOWN_LENGTH+"=? where "+DOWNLOAD_PATH+"=? and "+THREAD_ID+"=?";
  static final String FileService_deleteStr = "delete from "+DOWNLOAD_TABLE+" where "+DOWNLOAD_PATH+"=?  ";

  public DLHelper(Context context) {
    super(context, DOWNLOAD_NAME, null, DOWNLOAD_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE IF NOT EXISTS " + DOWNLOAD_TABLE + " (id integer primary key autoincrement, " + DOWNLOAD_PATH + " varchar(100), "
        + THREAD_ID + " INTEGER, " + DOWNLOAD_DOWN_LENGTH + " INTEGER)");
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + DOWNLOAD_TABLE);
    onCreate(db);
  }
}
