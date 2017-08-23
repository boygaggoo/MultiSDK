package com.mf.promotion.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.mf.basecode.utils.Logger;
import com.mf.promotion.widget.ExtraWindowView;
import com.mf.promotion.widget.FloatWindowView;

public class FloatWindowManager {
  private static final String    TAG = "FloatWindowManager";

  /**
   * 小悬浮窗View的实例
   */
  private static FloatWindowView floatWindow;

  /**
   * 大悬浮窗View的实例
   */
   private static ExtraWindowView extraWindow;

  /**
   * 小悬浮窗View的参数
   */
  private static LayoutParams    floatWindowParams;

  /**
   * 大悬浮窗View的参数
   */
   private static LayoutParams extraWindowParams;

  /**
   * 用于控制在屏幕上添加或移除悬浮窗
   */
  private static WindowManager   mWindowManager;

  /**
   * 用于获取手机可用内存
   */
  // private static ActivityManager mActivityManager;

  /**
   * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
   * 
   * @param context
   *          必须为应用程序的Context.
   */
  @SuppressWarnings("deprecation")
  public static void createFloatWindow(Context context) {
    Logger.e(TAG, "createSmallWindow");
    WindowManager windowManager = getWindowManager(context);
    int screenWidth = windowManager.getDefaultDisplay().getWidth();
    int screenHeight = windowManager.getDefaultDisplay().getHeight();
    if (floatWindow == null) {
      floatWindow = new FloatWindowView(context);
      if (floatWindowParams == null) {
        floatWindowParams = new LayoutParams();
        floatWindowParams.type = LayoutParams.TYPE_PHONE;
        floatWindowParams.format = PixelFormat.RGBA_8888;
        floatWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        floatWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        floatWindowParams.width = FloatWindowView.viewWidth;
        floatWindowParams.height = FloatWindowView.viewHeight;
        floatWindowParams.x = screenWidth;
        floatWindowParams.y = screenHeight / 2;
      }
      floatWindow.setParams(floatWindowParams);
      windowManager.addView(floatWindow, floatWindowParams);
    }
  }

  /**
   * 将小悬浮窗从屏幕上移除。
   * 
   * @param context
   *          必须为应用程序的Context.
   */
  public static void removeFloatWindow(Context context) {
    if (floatWindow != null) {
      WindowManager windowManager = getWindowManager(context);
      windowManager.removeView(floatWindow);
      floatWindow = null;
    }
  }

  /**
   * 创建一个大悬浮窗。位置为屏幕正中间。
   * 
   * @param context
   *          必须为应用程序的Context.
   */
  public static void createExtraWindow(Context context) {
    WindowManager windowManager = getWindowManager(context);
    int screenWidth = windowManager.getDefaultDisplay().getWidth();
    int screenHeight = windowManager.getDefaultDisplay().getHeight();
    if (extraWindow == null) {
      extraWindow = new ExtraWindowView(context);
      if (extraWindowParams == null) {
        extraWindowParams = new LayoutParams();
        extraWindowParams.type = LayoutParams.TYPE_PHONE;
        extraWindowParams.format = PixelFormat.RGBA_8888;
        extraWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        extraWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        extraWindowParams.width = ExtraWindowView.viewWidth;
        extraWindowParams.height = ExtraWindowView.viewHeight;
        extraWindowParams.x = screenWidth;
        extraWindowParams.y = screenHeight / 2+200;
      }
      extraWindow.setParams(extraWindowParams);
      windowManager.addView(extraWindow, extraWindowParams);
    } 
    
//    WindowManager windowManager = getWindowManager(context);
//    int screenWidth = windowManager.getDefaultDisplay().getWidth();
//    int screenHeight = windowManager.getDefaultDisplay().getHeight();
//    if (extraWindow == null) {
//      extraWindow = new ExtraWindowView(context);
//      if (extraWindowParams == null) {
//        extraWindowParams = new LayoutParams();
//        extraWindowParams.x = screenWidth / 2 - ExtraWindowView.viewWidth / 2;
//        extraWindowParams.y = screenHeight / 2 - ExtraWindowView.viewHeight / 2;
//        extraWindowParams.type = LayoutParams.TYPE_PHONE;
//        extraWindowParams.format = PixelFormat.RGBA_8888;
//        extraWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
//        extraWindowParams.width = ExtraWindowView.viewWidth;
//        extraWindowParams.height = ExtraWindowView.viewHeight;
//      }
//      extraWindow.setParams(extraWindowParams);
//      windowManager.addView(extraWindow, extraWindowParams);
//    }
  }

  /**
   * 将大悬浮窗从屏幕上移除。
   * 
   * @param context
   *          必须为应用程序的Context.
   */
  public static void removeExtraWindow(Context context) {
    if (extraWindow != null) {
      WindowManager windowManager = getWindowManager(context);
      windowManager.removeView(extraWindow);
      extraWindow = null;
    }
  }

  /**
   * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
   * 
   * @param context
   *          可传入应用程序上下文。
   */
  // public static void updateUsedPercent (Context context) {
  // if (smallWindow != null) {
  // TextView percentView = (TextView)
  // smallWindow.findViewById(R.id.tv_float_window_num);
  // percentView.setText(getUsedPercentValue(context));
  // }
  // }

  /**
   * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
   * 
   * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
   */
  public static boolean isFloatWindowShowing() {
    return floatWindow != null;
  }
  
  public static boolean isExtraWindowShowing() {
    return extraWindow != null;
  }

  /**
   * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
   * 
   * @param context
   *          必须为应用程序的Context.
   * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
   */
  private static WindowManager getWindowManager(Context context) {
    if (mWindowManager == null) {
      mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
    return mWindowManager;
  }

  /**
   * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
   * 
   * @param context
   *          可传入应用程序上下文。
   * @return ActivityManager的实例，用于获取手机可用内存。
   */
  // private static ActivityManager getActivityManager(Context context) {
  // if (mActivityManager == null) {
  // mActivityManager = (ActivityManager)
  // context.getSystemService(Context.ACTIVITY_SERVICE);
  // }
  // return mActivityManager;
  // }

  /**
   * 计算已使用内存的百分比，并返回。
   * 
   * @param context
   *          可传入应用程序上下文。
   * @return 已使用内存的百分比，以字符串形式返回。
   */
  // public static String getUsedPercentValue(Context context) {
  // String dir = "/proc/meminfo";
  // try {
  // FileReader fr = new FileReader(dir);
  // BufferedReader br = new BufferedReader(fr, 2048);
  // String memoryLine = br.readLine();
  // String subMemoryLine =
  // memoryLine.substring(memoryLine.indexOf("MemTotal:"));
  // br.close();
  // long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll("\\D+",
  // ""));
  // long availableSize = getAvailableMemory(context) / 1024;
  // int percent = (int) ((totalMemorySize - availableSize) / (float)
  // totalMemorySize * 100);
  // return percent + "%";
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // return "悬浮窗";
  // }

  /**
   * 获取当前可用内存，返回数据以字节为单位。
   * 
   * @param context
   *          可传入应用程序上下文。
   * @return 当前可用内存。
   */
  // private static long getAvailableMemory(Context context) {
  // ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
  // getActivityManager(context).getMemoryInfo(mi);
  // return mi.availMem;
  // }

}
