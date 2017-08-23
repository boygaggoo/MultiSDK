package com.mf.promotion.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.mf.activity.BaseActivity;
import com.mf.basecode.utils.Logger;
import com.mf.promotion.util.ServiceManager;

public class PromOnePixelActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Logger.debug("PromOnePixelActivity", "onCreate");
    WindowManager.LayoutParams params = that.getWindow().getAttributes();
    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    params.format = PixelFormat.RGBA_8888;
    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;//
    params.gravity = Gravity.CENTER | Gravity.TOP;
    params.x = 5;
    params.y = 5;
    params.width = 1;
    params.height = 1;
    params.alpha = 0.6f;
    int color = Color.argb(0x00, 0x00, 0x00, 0x00);
    ColorDrawable drawable = new ColorDrawable(color);
    that.getWindow().setBackgroundDrawable(drawable);
    that.getWindow().setAttributes(params);
    that.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  }
  @Override
  protected void onPause() {
    Logger.debug("PromOnePixelActivity", "onPause");
  }

  @Override
  protected void onRestart() {
    Logger.debug("PromOnePixelActivity", "onRestart");
  }
  @Override
  protected void onResume() {
    Logger.debug("PromOnePixelActivity", "onResume");
  }
  @Override
  protected void onStart() {
    Logger.debug("PromOnePixelActivity", "onStart");
  }
  @Override
  protected void onStop() {
    Logger.debug("PromOnePixelActivity", "onStop");
  }

  @Override
  protected void onDestroy() {
    Logger.debug("PromOnePixelActivity", "onDestroy");
    ServiceManager.getInstance(that).startStayService();
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  }
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.addCategory(Intent.CATEGORY_HOME);
      that.startActivity(intent);
      new Handler(that.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
          ServiceManager.getInstance(that).startStayService();
          that.finish();
        }
      }, 100);
      return true;
    }
    return false;
  }

}
