package com.mf.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.mf.basecode.utils.Logger;
import com.mf.utils.ResourceIdUtils;

public abstract class BaseActivity extends Activity {

  private static final String TAG               = "Client-BaseActivity";

  public static final String  FROM              = "extra.from";
  public static final int     FROM_INTERNAL     = 0;
  public static final int     FROM_EXTERNAL     = 1;
  public static final String  EXTRA_CLASS       = "extra.class";


  /**
   * 代理activity，可以当作Context来使用，会根据需要来决定是否指向this
   */
  protected Activity          mProxyActivity;

  /**
   * 等同于mProxyActivity，可以当作Context来使用，会根据需要来决定是否指向this<br/>
   * 可以当作this来使用
   */
  protected Activity          that;
  protected int               mFrom             = FROM_INTERNAL;

  public void setProxy(Activity proxyActivity, Resources resources) {
    Logger.d(TAG, "setProxy: proxyActivity= " + proxyActivity);
    mProxyActivity = proxyActivity;
    ResourceIdUtils.getInstance().setResources(resources);
    that = mProxyActivity;
  }

  public void setFoucsChange(){
    Logger.e(TAG, "setFoucsChange = ");
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mFrom = savedInstanceState.getInt(FROM, FROM_INTERNAL);
    }
    if (mFrom == FROM_INTERNAL) {
      super.onCreate(savedInstanceState);
      mProxyActivity = this;
      that = mProxyActivity;
    }
    Logger.d(TAG, "onCreate: from= " + (mFrom == FROM_INTERNAL ? "FROM_INTERNAL" : "FROM_EXTERNAL"));
  }

  protected void startActivityByProxy(String className) {
    if (mProxyActivity == this) {
      Intent intent = new Intent();
      intent.setClassName(this, className);
      mProxyActivity.startActivity(intent);
    } 
  }

  public void startActivityForResultByProxy(String className, int requestCode) {
    if (mProxyActivity == this) {
      Intent intent = new Intent();
      intent.setClassName(this, className);
      mProxyActivity.startActivityForResult(intent, requestCode);
    }
  }

  @Override
  public void setContentView(View view) {
    if (mProxyActivity == this) {
      super.setContentView(view);
    } else {
      mProxyActivity.setContentView(view);
    }
  }

  @Override
  public void setContentView(View view, LayoutParams params) {
    if (mProxyActivity == this) {
      super.setContentView(view, params);
    } else {
      mProxyActivity.setContentView(view, params);
    }
  }

  @Override
  public void setContentView(int layoutResID) {
    if (mProxyActivity == this) {
      super.setContentView(layoutResID);
    } else {
      mProxyActivity.setContentView(layoutResID);
    }
  }

  @Override
  public void addContentView(View view, LayoutParams params) {
    if (mProxyActivity == this) {
      super.addContentView(view, params);
    } else {
      mProxyActivity.addContentView(view, params);
    }
  }

  @Override
  public View findViewById(int id) {
    if (mProxyActivity == this) {
      return super.findViewById(id);
    } else {
      return mProxyActivity.findViewById(id);
    }
  }
}
