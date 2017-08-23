package com.mf.promotion.widget;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.contants.CommConstants;
import com.mf.basecode.utils.contants.FileConstants;
import com.mf.promotion.activity.PromFloatWindowAdActivity;
import com.mf.promotion.util.PromApkConstants;
import com.mf.promotion.util.PromUtils;
import com.mf.promotion.util.ScreenUtils;
import com.mf.utils.BitmapUtils;
import com.mf.utils.ResourceIdUtils;

public class FloatWindowView extends LinearLayout {

  /**
   * 记录小悬浮窗的宽度
   */
  public static int                  viewWidth;

  /**
   * 记录小悬浮窗的高度
   */
  public static int                  viewHeight;

  /**
   * 记录系统状态栏的高度
   */
  private static int                 statusBarHeight;

  /**
   * 用于更新小悬浮窗的位置
   */
  private WindowManager              windowManager;

  /**
   * 小悬浮窗的参数
   */
  private WindowManager.LayoutParams mParams;

  /**
   * 记录当前手指位置在屏幕上的横坐标值
   */
  private float                      xInScreen;

  /**
   * 记录当前手指位置在屏幕上的纵坐标值
   */
  private float                      yInScreen;

  /**
   * 记录手指按下时在屏幕上的横坐标的值
   */
  private float                      xDownInScreen;

  /**
   * 记录手指按下时在屏幕上的纵坐标的值
   */
  private float                      yDownInScreen;

  /**
   * 记录手指按下时在小悬浮窗的View上的横坐标的值
   */
  private float                      xInView;

  /**
   * 记录手指按下时在小悬浮窗的View上的纵坐标的值
   */
  private float                      yInView;

  private DisplayMetrics             metrics = new DisplayMetrics();

  private final float                density;
  private Context                    mContext;
  private static final String        TAG     = "FloatWindowView";

  public FloatWindowView(Context mContext) {
    super(mContext);
    this.mContext = mContext;
    metrics = mContext.getResources().getDisplayMetrics();
    density = metrics.density;
    windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

    RelativeLayout ll = new RelativeLayout(mContext);
    ImageView tv = new ImageView(mContext);
//    tv.setText("FW");
//    tv.setTextSize(30);
//    tv.setTextColor(Color.WHITE);
//    tv.setBackgroundColor(Color.RED);
    Bitmap b = getFloatIconBitmap();

    viewWidth = ScreenUtils.dip2px(mContext, 50);
    viewHeight = ScreenUtils.dip2px(mContext, 50);
    if(b != null){
      tv.setImageBitmap(b);
    }
    ll.addView(tv, new RelativeLayout.LayoutParams(viewWidth, viewHeight));
    addView(ll, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    Logger.e(TAG, "FloatWindow");
  }
  
  private Bitmap getFloatIconBitmap(){
    Bitmap b = null;
    File iconDir = new File(FileConstants.getFloatIconDir(mContext)/*geFileConstants.FLOAT_ICON_DIR*/);
    if (!iconDir.exists()) {
      iconDir.mkdirs();
    }
    
    SharedPreferences spf = mContext.getSharedPreferences(CommConstants.SHARED_PREFERENCE_CONFIG, 0);
    String url = spf.getString(CommConstants.FLOAT_ICON_URL, "");
    if(!TextUtils.isEmpty(url)){
      File iconFile = new File(iconDir, PromUtils.getPicNameFromPicUrl(url));
      if (iconFile.exists()) {
        try {
          b = BitmapFactory.decodeFile(iconFile.getAbsolutePath());
          return b;
        } catch (OutOfMemoryError e) {
         Logger.p(e);
        }
      }
    } 
    try {
      b = BitmapFactory.decodeStream(mContext.getAssets().open("icon.png"));
    } catch (IOException e) {
      Logger.p(e);
    }catch (OutOfMemoryError e) {
      Logger.p(e);
    }
    return b;
  }

  @SuppressWarnings("unused")
  private Bitmap getFloatWindowIcon(Resources res) {
    Bitmap map = BitmapFactory.decodeResource(res, ResourceIdUtils.getInstance().getResourceId("R.drawable.mf_fw_icon"));
    Logger.e(TAG, "density is: " + density);
    if (null == map) {
      Logger.e(TAG, "bitmap before zoom is null");
    } else if (null != map) {
      Logger.e(TAG, "bitmap before zoom is not null");
    }
    if (density == 1) {
      map = BitmapUtils.zoomBitmap(map, 48, 48);
    } else if (density > 1) {
      map = BitmapUtils.zoomBitmap(map, (int) (density * 48), (int) (density * 48));
    } else {
      map = BitmapUtils.zoomBitmap(map, 32, 32);
    }

    if (null == map) {
      Logger.e(TAG, "bitmap after zoom is null");
    } else if (null != map) {
      Logger.e(TAG, "bitmap after zoom is not null");
    }
    return map;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
      xInView = event.getX();
      yInView = event.getY();
      xDownInScreen = event.getRawX();
      yDownInScreen = event.getRawY() - getStatusBarHeight();
      xInScreen = event.getRawX();
      yInScreen = event.getRawY() - getStatusBarHeight();
      break;
    case MotionEvent.ACTION_MOVE:
      xInScreen = event.getRawX();
      yInScreen = event.getRawY() - getStatusBarHeight();
      if (Math.abs(xDownInScreen - xInScreen) < 7 && Math.abs(yDownInScreen-yInScreen) < 7) {
        break;
      }
      // 手指移动的时候更新小悬浮窗的位置
      updateViewPosition();
      break;
    case MotionEvent.ACTION_UP:
      // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
      if (Math.abs(xDownInScreen - xInScreen) < 7 && Math.abs(yDownInScreen-yInScreen) < 7) {
        goToADActivity();
      }
      break;
    default:
      break;
    }
    return true;
  }

  /**
   * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
   * 
   * @param params
   *          小悬浮窗的参数
   */
  public void setParams(WindowManager.LayoutParams params) {
    mParams = params;
  }

  /**
   * 更新小悬浮窗在屏幕中的位置。
   */
  private void updateViewPosition() {
    mParams.x = (int) (xInScreen - xInView);
    mParams.y = (int) (yInScreen - yInView);
    windowManager.updateViewLayout(this, mParams);
  }

  /**
   * 启动推荐应用界面
   */
  private void goToADActivity() {
    // 跳转Activity界面
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.setComponent(new ComponentName(mContext.getPackageName(), PromApkConstants.HOST_PROXY_ACTIVITY));
    intent.putExtra(PromApkConstants.EXTRA_CLASS, PromFloatWindowAdActivity.class.getCanonicalName());
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    Logger.debug(TAG, "PromFloatWindowAdActivity ");
    mContext.getApplicationContext().startActivity(intent);
  }

  /**
   * 用于获取状态栏的高度。
   * 
   * @return 返回状态栏高度的像素值。
   */
  private int getStatusBarHeight() {
    if (statusBarHeight == 0) {
      try {
        Class<?> c = Class.forName("com.android.internal.R$dimen");
        Object o = c.newInstance();
        Field field = c.getField("status_bar_height");
        int x = (Integer) field.get(o);
        statusBarHeight = getResources().getDimensionPixelSize(x);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return statusBarHeight;
  }

}
