package com.multisdk.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.multisdk.library.config.Config;
import com.multisdk.library.constants.Constants;
import com.multisdk.library.data.ConfigManager;
import com.multisdk.library.virtualapk.PluginManager;
import com.multisdk.library.virtualapk.internal.LoadedPlugin;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {

  private static final String TAG = "ReflectUtil";

  public static void payInit(Context context,String s1,String s2){
    if (null == context || TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)){
      Log.e("i", " error param.");
      return;
    }
    try {
      Class<?> clz = Class.forName(Config.PAY_CLASS);
      if (null != clz){
        Method initMethod = clz.getDeclaredMethod(Config.PAY_CLASS_INIT,Context.class,String.class,String.class);
        initMethod.setAccessible(true);
        initMethod.invoke(clz,context,s1,s2);
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

  public static void pay(Activity activity, Handler handler, String pointNum, int price){
    if (null == activity || null == handler || TextUtils.isEmpty(pointNum) || price < 0){
      Log.e("p", " error param.");
      return;
    }
    try {
      Class<?> clz = Class.forName(Config.PAY_CLASS);
      if (null != clz){
        Method initMethod = clz.getDeclaredMethod(Config.PAY_CLASS_PAY,Activity.class,Handler.class,String.class,int.class);
        initMethod.setAccessible(true);
        initMethod.invoke(clz,activity,handler,pointNum,price);
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

  public static void initAD(Context context){
    Intent intent = new Intent();
    intent.setClassName(Config.AD_PACKAGE_NAME,Config.AD_CLASS);
    intent.putExtra(Config.AD_SERVICE_ID,-1);
    intent.putExtra(Config.AD_APP_ID, ConfigManager.getAppID(context));
    intent.putExtra(Config.AD_CHANNEL_ID,ConfigManager.getChannelID(context));
    intent.putExtra(Config.AD_CP_ID,ConfigManager.getPID(context));

    LoadedPlugin plugin = PluginManager.getInstance(context).getLoadedPlugin(Constants.Plugin.PLUGIN_AD_PACKAGE_NAME);
    if (null != plugin){
      Log.e(TAG, "plugin: pkgName:" + plugin.getPackageName() + "\n" + "serviceInfo:" + plugin.getPackageInfo().toString());
    }

    Log.e(TAG, "initAD: pkgName:" + intent.getComponent().getPackageName() );
    Log.e(TAG, "initAD: className:" + intent.getComponent().getClassName() );
    context.startService(intent);
  }
}
