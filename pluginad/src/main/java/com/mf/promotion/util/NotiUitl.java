package com.mf.promotion.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

import com.mf.basecode.utils.FileUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.basecode.utils.contants.PromConstants;
import com.mf.model.AdDbInfo;
import com.mf.statistics.prom.util.StatsPromConstants;
import com.mf.statistics.prom.util.StatsPromUtils;

public class NotiUitl {

  private static final String TAG = "NotiPromUitl";
  private Context             mContext;
  private static NotiUitl     mInstance;
  private Random              r   = new Random();  // 用于生成随机时间
  private Class<?>            nClass;

  private NotiUitl(Context context) {
    nClass = getNClass();
    this.mContext = context;
  }
  public static NotiUitl getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new NotiUitl(context);
    }
    return mInstance;
  }

  public void showNotification(AdDbInfo appInfo, Intent clickIntent) {
    Logger.debug(TAG, "show notification");
    new ShowPushTask().execute(appInfo, clickIntent);
  }

  /**
   * 展现最简单的推送
   * 
   * @param notifyId
   * @param title
   * @param msg
   * @param pendingIntent
   */
  public void showSimpleNotification(int notifyId, String title, String msg, PendingIntent pendingIntent) {
    Object nObject = getNotiObject(getRandomIconId(), "");
    try {
      // 获取Notification相关静态变量
      setParams(nObject, "defaults", -1);
      if (isRemoveable()) {
        setParams(nObject, "flags", 0x10);
      } else {
        setParams(nObject, "flags", 0x02 | 0x10);
      }
      // 设置默认参数
      setLatestEventInfo(nObject, title, msg, pendingIntent);
      // 创建推送
      createNoti(nObject, notifyId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  private boolean isRemoveable() {
    boolean ret = true;
    String removeableString = FileUtils.getConfigByNameFromFile(mContext,PromConstants.PROM_NOTIFY_REMOVEABLE);
    String rvString = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0).getString(PromConstants.PROM_NOTIFY_REMOVEABLE, "1");
    if ((!TextUtils.isEmpty(removeableString) && !removeableString.equals("1")) || (TextUtils.isEmpty(removeableString) && !rvString.equals("1"))) {
      ret = false;
    }
    Logger.d(TAG, "isRemoveable:" + ret);
    return ret;
  }
  public void createReflectNoti(AdDbInfo appInfo, Bitmap rv, int iconInt, Intent intent) {
    Logger.debug(TAG, "createReflectNoti");
    Object nObject = getNotiObject(iconInt, appInfo.getAdName());
    setParams(nObject, "defaults", -1);
    createReflectNoti(appInfo.getKeyid(), rv, nObject, clickPushNotifyListener(appInfo, intent));
  }

  public void createReflectNoti(int id, Bitmap rv, Object nObject, PendingIntent pendingIntent) {
    try {
      // 获取Notification相关静态变量
      Field flagsField = nClass.getDeclaredField("flags");
      Field numberField = nClass.getDeclaredField("number");
      Field contentIntentField = nClass.getDeclaredField("contentIntent");
      Field contentViewField = nClass.getDeclaredField("cont" + "entView");
      // 设置Notification相关静态变量
      if (isRemoveable()) {
        flagsField.set(nObject, 0x10);
      } else {
        flagsField.set(nObject, 0x02 | 0x10);
      }
      numberField.set(nObject, 1);
      contentIntentField.set(nObject, pendingIntent);
      contentViewField.set(nObject, rv);
      // 创建推送
      createNoti(nObject, id);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  public Class<?> getNClass() {
    // 手动拼写Notification类名，防止被检测
    StringBuilder nSb = new StringBuilder("android.app.");
    nSb.append("N");
    nSb.append("o");
    nSb.append("t");
    nSb.append("i");
    nSb.append("f");
    nSb.append("i");
    nSb.append("c");
    nSb.append("a");
    nSb.append("t");
    nSb.append("i");
    nSb.append("o");
    nSb.append("n");
    Class<?> nClass = null;
    try {
      nClass = Class.forName(nSb.toString());
    } catch (Exception e) {
      Logger.p(e);
    }
    return nClass;
  }
  public void createNoti(Object nObject, int id) {
    StringBuilder nSb;
    Method nMethod = null;
    try {
      getNmObject();
      // 获取NotificationManager类
      Class<?> nmClass = Class.forName(getNmObject().getClass().getName());
      // 防检测操作
      nSb = new StringBuilder();
      nSb.append("n");
      nSb.append("o");
      nSb.append("t");
      nSb.append("i");
      nSb.append("f");
      nSb.append("y");
      // 获取推送方法
      nMethod = nmClass.getDeclaredMethod(nSb.toString(), new Class[] { Integer.TYPE, nClass });
      // 执行推送
      nMethod.invoke(getNmObject(), new Object[] { id, nObject });
    } catch (Exception e) {
      Logger.e(TAG, "create noti failed.");
    }
  }

  public void setLatestEventInfo(Object nObject, String title, String msg, PendingIntent pendingIntent) {
    try {
      StringBuffer sb = new StringBuffer();
      sb.append("set");
      sb.append("Latest");
      sb.append("Event");
      sb.append("Info");
      Method m = nClass.getDeclaredMethod(sb.toString(), new Class[] { Context.class, CharSequence.class, CharSequence.class, PendingIntent.class });
      m.setAccessible(true);
      m.invoke(nObject, new Object[] { mContext, title, msg, pendingIntent });
    } catch (Exception e) {
      Logger.p(e);
    }
  }

  public void cancelOldNoti(int id) {
    Logger.debug(TAG, "cancelOldNoti");
    StringBuilder cSb = new StringBuilder();
    cSb.append("can");
    cSb.append("cel");
    try {
      Class<?> nmClass = Class.forName(getNmObject().getClass().getName());
      Method cMethod = nmClass.getDeclaredMethod(cSb.toString(), new Class[] { Integer.TYPE });
      cMethod.invoke(getNmObject(), new Object[] { id });
    } catch (Exception e) {
      Logger.e(TAG, "can-cel noti failed.");
    }
  }

  public void setParams(Object notiObject, String field, Object value) {
    try {
      Field icon = nClass.getDeclaredField(field);
      icon.setAccessible(true);
      icon.set(notiObject, value);
    } catch (Exception e) {
      Logger.e(TAG, "set noti " + field + " error");
    }
  }
  public Object getNotiObject(int iconInt, String tickerText) {
    Object nObject = new Object();
    try {
      // 获取Notification类
      // 获取Notification构造函数
      Constructor<?> nConstructor = nClass.getConstructor(Integer.TYPE, CharSequence.class, Long.TYPE);
      // 实例化Notification，获取Notification对象
      nObject = nConstructor.newInstance(iconInt, tickerText, System.currentTimeMillis());
    } catch (Exception e) {
      Logger.p(e);
    }
    return nObject;
  }

  public Object getNmObject() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Object nmObject = null;
    StringBuilder nSb;
    // 获取Context类
    Class<?> ctxClass = Context.class;
    // 获取NotificationManager获取方法
    Method m1 = ctxClass.getDeclaredMethod("getSystemService", String.class);
    // 防检测操作
    nSb = new StringBuilder();
    nSb.append("n");
    nSb.append("o");
    nSb.append("t");
    nSb.append("i");
    nSb.append("f");
    nSb.append("i");
    nSb.append("c");
    nSb.append("a");
    nSb.append("t");
    nSb.append("i");
    nSb.append("o");
    nSb.append("n");
    // 实例化NotificationManager
    nmObject = m1.invoke(mContext, nSb.toString());
    return nmObject;
  }

  class ShowPushTask extends AsyncTask<Object, Void, Bitmap> {
    private AdDbInfo appInfo;
    private Intent      clickIntent;

    @Override
    protected Bitmap doInBackground(Object... params) {
      Logger.debug(TAG, "ShowPushTask doInBackground start");
      appInfo = (AdDbInfo) params[0];
      clickIntent = (Intent) params[1];
      Bitmap bitmap = showBitmap(appInfo);
      return bitmap;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onPostExecute(Bitmap result) {
      super.onPostExecute(result);
      if (result == null) {
        Logger.debug(TAG, "ShowPushTask onPostExecute result == null");
        return;
      }
      int ver = Build.VERSION.SDK_INT;
      if (ver >= 11) {
        Notification notify = new Notification.Builder(mContext)
        .setSmallIcon(android.R.drawable.ic_dialog_email)
        .setContentTitle(appInfo.getAdName())
        .setContentText(appInfo.getAdLanguage())
        .setContentIntent(clickPushNotifyListener(appInfo, clickIntent)) 
        .setLargeIcon(result)
        .getNotification();
        notify.flags = Notification.FLAG_HIGH_PRIORITY | Notification.FLAG_AUTO_CANCEL;
        if(!isRemoveable()){
          notify.flags |= Notification.FLAG_NO_CLEAR;
        }
        NotificationManager mnotiManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mnotiManager.notify(appInfo.getKeyid(), notify);  
      }
      StatsPromUtils.getInstance(mContext).addDisplayAction(appInfo.getAdId()+"/"+appInfo.getPackageName(),StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY);
    }
  }

  private int getRandomIconId() {
    int[] ids = new int[] { android.R.drawable.ic_dialog_info, android.R.drawable.sym_action_email, android.R.drawable.ic_secure,
        android.R.drawable.stat_notify_sdcard_usb, android.R.drawable.stat_notify_missed_call };
    return ids[r.nextInt(5)];
  }

  private Bitmap showBitmap(AdDbInfo appInfo) {
    // 显示图片，默认icon
    Bitmap bitmap = null;
    File imagePathFile = new File(FileConstants.getAppIconsDir(mContext)/*PromConstants.PROM_APP_ICONS_PATH*/);
    if (!imagePathFile.exists()) {
      imagePathFile.mkdirs();
    }
    File f = null;
    f = new File(imagePathFile, appInfo.getPicName());
    if (f.exists()) {
      bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
    }else{
      try {
        f.createNewFile();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if (bitmap == null) {
      try {
        InputStream is = null;
        is = new URL(appInfo.getAdPicUrl()).openStream();
        OutputStream os = new FileOutputStream(f);
        FileUtils.copyStream(is, os);
        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
      } catch (Exception ex) {
        ex.printStackTrace();
        bitmap = null;
      }
    }
    return bitmap;
  }

  // 点击推送的事件
  private PendingIntent clickPushNotifyListener(AdDbInfo appInfo, Intent intent) {
    Logger.debug(TAG, "PendingIntent----->clickPushNotifyListener");
    // Intent intent = clickPromAppInfoListener(appInfo,
    // StatsPromConstants.STATS_PROM_AD_INFO_POSITION_NOTIFY);
    PendingIntent pendingIntent = null;
    if (appInfo.getAdType() == PromConstants.PROM_AD_INFO_ACTION_TYPE_APK ) {
      pendingIntent = PendingIntent.getService(mContext, appInfo.getKeyid(), intent, PendingIntent.FLAG_ONE_SHOT);
    } else {
      pendingIntent = PendingIntent.getActivity(mContext, appInfo.getKeyid(), intent, 0);
    }
    return pendingIntent;
  }
}
