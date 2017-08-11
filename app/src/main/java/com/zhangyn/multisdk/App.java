package com.zhangyn.multisdk;

import android.app.Application;
import android.content.Context;
import com.multisdk.library.virtualapk.PluginManager;

public class App extends Application {

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    PluginManager.getInstance(this).init();
  }
}
