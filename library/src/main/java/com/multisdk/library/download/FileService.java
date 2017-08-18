package com.multisdk.library.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseIntArray;
import java.util.Map;

import static com.multisdk.library.download.DLHelper.FileService_deleteStr;
import static com.multisdk.library.download.DLHelper.FileService_getDataStr;
import static com.multisdk.library.download.DLHelper.FileService_saveStr;
import static com.multisdk.library.download.DLHelper.FileService_updateStr;

public class FileService {

  private DLHelper dlHelper;

  public FileService(Context context) {
    dlHelper = new DLHelper(context);
  }

  /**
   * 获取每条线程已经下载的文件长度
   *
   * @param path
   * @return
   */
  public SparseIntArray getData(String path) {
    SQLiteDatabase db = dlHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery(FileService_getDataStr, new String[] { path });
    SparseIntArray da = new SparseIntArray();
    while (cursor.moveToNext()) {
      da.put(cursor.getInt(0), cursor.getInt(1));
    }
    cursor.close();
    db.close();
    return da;
  }

  /**
   * 保存每条线程已经下载的文件长度
   *
   * @param path
   * @param map
   */
  public void save(String path, Map<Integer, Integer> map) {// int threadid,
    // int position
    SQLiteDatabase db = dlHelper.getWritableDatabase();
    db.beginTransaction();
    try {
      for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
        db.execSQL(FileService_saveStr, new Object[] { path, entry.getKey(), entry.getValue() });
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
    db.close();
  }

  /**
   * 实时更新每条线程已经下载的文件长度
   */
  public void update(String path, int threadId, int pos) {
    SQLiteDatabase db = dlHelper.getWritableDatabase();
    db.execSQL(FileService_updateStr, new Object[] { pos, path, threadId });
    db.close();
  }

  /**
   * 当文件下载完成后，删除对应的下载记录
   *
   * @param path
   */
  public void delete(String path) {
    SQLiteDatabase db = dlHelper.getWritableDatabase();
    db.execSQL(FileService_deleteStr, new Object[] { path });
    db.close();
  }
}
