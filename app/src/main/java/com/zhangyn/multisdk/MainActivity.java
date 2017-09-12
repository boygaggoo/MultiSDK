package com.zhangyn.multisdk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import com.multisdk.library.utils.ReflectUtil;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  public static final String PLUGIN_PKG_NAME = "com.zhangyn.didiplugin";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Log.e(TAG, "onCreate: imsi" + getPhoneImsi(this) );
    Log.e(TAG, "onCreate: imei" + getPhoneImei(this) );

    findViewById(R.id.show_plugin_img).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        ReflectUtil.initAD(MainActivity.this);

      }
    });
  }

  /**
   * 获取手机IMSI号
   *
   * @param context
   * @return
   */
  public static String getPhoneImsi(Context context) {
    String imsi = null;
    TelephonyManager mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
    try {
      imsi = mTelephonyManager.getSubscriberId();
    } catch (Exception e) {
    }
    if (null == imsi) {
      Class<? extends TelephonyManager> tmClass = mTelephonyManager.getClass();
      try {
        Method getImsiMethod = tmClass.getMethod("getSubscriberIdGemini", Integer.TYPE);
        if (null != getImsiMethod) {
          // 先取SIM2
          imsi = (String) getImsiMethod.invoke(mTelephonyManager, 1);
          if (null == imsi) {
            imsi = (String) getImsiMethod.invoke(mTelephonyManager, 0);
          }
        }
      } catch (Throwable e) {
      }
    }
    if (imsi==null) {
      PhoneImsiImeiExpandGet a = new PhoneImsiImeiExpandGet();
      a.init(context);
      imsi = a.getImsi("1");
      if(imsi.equals("1")){
        imsi = null;
      }
    }
    return imsi;
  }

  public static String getPhoneImei(Context context) {
    String imei = null;
    TelephonyManager mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
    try {
      imei = mTelephonyManager.getDeviceId();
    } catch (Exception e) {

    }
    if (imei==null) {
      PhoneImsiImeiExpandGet a = new PhoneImsiImeiExpandGet();
      a.init(context);
      imei = a.getImei("2");
      if(imei.equals("2")){
        imei = null;
      }
    }
    return imei;
  }
}
