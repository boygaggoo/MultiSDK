package com.mf.statistics.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.content.Context;

public class StatsUtils {
  public static final String TAG       = "StatUtils";
  private static StatsUtils  mInstance = null;
  private static Context     mContext  = null;

  public static StatsUtils getInstance(Context context) {
    mContext = context;
    if (mInstance == null) {
      mInstance = new StatsUtils();
    }
    return mInstance;
  }

  private StatsUtils() {
  }

  /*
   * 保存时间戳
   */
  public void saveTimeStamp(String fileName, String timeStamp) {
    try {
      FileOutputStream fout = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
      fout.write(timeStamp.getBytes());
      fout.close();
    } catch (Exception e) {
    }
  }

//  public String getStatsPath() {
//    return FileConstants.FILE_ROOT_DIRECTORY + "/" + StatsConstants.STATS_DIR;
//  }

  /*
   * 根据文件名获取时间戳
   */
  public String getTimeStamp(String fileName) {
    String ret = null;
    try {
      FileInputStream fin = mContext.openFileInput(fileName);
      int length = fin.available();
      byte[] buffer = new byte[length];
      fin.read(buffer);
      ret = EncodingUtils.getString(buffer, "UTF-8");
      if (ret.equals(StatsConstants.TIME_STAMP_UPLOAD_SUCCESS)) {
        Date curDate = new Date(System.currentTimeMillis());
        ret = new SimpleDateFormat(StatsConstants.STATS_DATE_FORMAT).format(curDate);
        saveTimeStamp(fileName, ret);
      }
      fin.close();
    } catch (Exception e) {
      Date curDate = new Date(System.currentTimeMillis());
      ret = new SimpleDateFormat(StatsConstants.STATS_DATE_FORMAT).format(curDate);
      saveTimeStamp(fileName, ret);
    }
    return ret;
  }

}
