package com.zhangyn.multisdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.multisdk.library.utils.ReflectUtil;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  public static final String PLUGIN_PKG_NAME = "com.zhangyn.didiplugin";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.show_plugin_img).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ReflectUtil.initAD(MainActivity.this);
      }
    });
  }
}
