package com.zhangyn.multisdk;

import android.app.Application;
import android.content.Context;
import com.multisdk.library.imp.MultiSDK;
import com.multisdk.library.virtualapk.PluginManager;

public class App extends Application {

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiSDK.prepare(base);
  }

  @Override public void onCreate() {
    super.onCreate();
    MultiSDK.getInstance(this).init("test","test","p0000");
  }
}
