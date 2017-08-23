package com.mf.promotion.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.mf.basecode.utils.Logger;
import java.util.Locale;

public class TimeFormater {
  public static String formatTime(long time) {
    String result = "" + time;
    try {
      if (time > 0) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault());
        Date date = new Date(time);
        result = sdf.format(date);
      }
    } catch (Throwable e) {
      Logger.p(e);
    }
    return result;
  }
}
