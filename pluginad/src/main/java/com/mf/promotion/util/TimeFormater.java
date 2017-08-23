package com.mf.promotion.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mf.basecode.utils.Logger;

public class TimeFormater {
  public static String formatTime(long time) {
    String result = "" + time;
    try {
      if (time > 0) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = new Date(time);
        result = sdf.format(date);
      }
    } catch (Throwable e) {
      Logger.p(e);
    }
    return result;
  }
}
