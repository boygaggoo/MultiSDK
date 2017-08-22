package com.multisdk.library.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.multisdk.library.config.Config;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {

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
}
