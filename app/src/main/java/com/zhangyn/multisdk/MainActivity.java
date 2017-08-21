package com.zhangyn.multisdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.multisdk.library.virtualapk.PluginManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  public static final String PLUGIN_PKG_NAME = "com.zhangyn.didiplugin";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadPlugin(this);
    setContentView(R.layout.activity_main);
    findViewById(R.id.show_plugin_img).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (PluginManager.getInstance(MainActivity.this).getLoadedPlugin(PLUGIN_PKG_NAME) == null) {
          Toast.makeText(getApplicationContext(), "插件未加载,请尝试重启APP", Toast.LENGTH_SHORT).show();
          return;
        }
        //Intent intent = new Intent();
        //intent.setClassName("com.zhangyn.didiplugin",
        //    "com.zhangyn.didiplugin.MainActivity");
        //MainActivity.this.startActivity(intent);
        sum();
      }
    });
  }

  public void sum(){
    try {
      Class clz = Class.forName("com.zhangyn.didiplugin.Util");
      if (clz == null){
        Log.e(TAG, "clz is null.");
      }else {
        Log.e(TAG, "clz is not null.");
        Method method = clz.getDeclaredMethod("sum",int.class,int.class);
        method.setAccessible(true);
        Object sum = method.invoke(clz,1,1);
        if (sum instanceof String){
          Log.e(TAG, "sum: " + sum);
        }
      }

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private void loadPlugin(Context base) {
    PluginManager pluginManager = PluginManager.getInstance(base);
    File apk = new File(getExternalStorageDirectory(), "didiplugin.apk");
    if (apk.exists()) {
      try {
        pluginManager.loadPlugin(apk);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      Toast.makeText(getApplicationContext(), "SDcard根目录未检测到plugin.apk插件", Toast.LENGTH_SHORT)
          .show();
    }
  }
}
