package com.xdd.pay.util;

import android.telephony.*;
import java.lang.reflect.*;

import com.xdd.pay.constant.CommConstant;

import android.app.*;
import android.os.*;

public final class HtcDualModeSupport {
  public static boolean isDualMode() throws ReflectHiddenFuncException {
    try {
      final Class<?> clz = Class.forName(EncryptUtils.decode(CommConstant.COM_HTC_TELEPHONY_HTCTELEPHONYMANAGER_TAG));
      Method method = clz.getDeclaredMethod(EncryptUtils.decode(CommConstant.DUALPHONEENABLE_TAG), (Class[]) new Class[0]);
      method.setAccessible(true);
      final boolean dualPhoneEnable = (Boolean) method.invoke(null, new Object[0]);
      if (dualPhoneEnable) {
        return dualPhoneEnable;
      }
      method = clz.getDeclaredMethod(EncryptUtils.decode(CommConstant.DUALGSMPHONEENABLE_TAG), (Class[]) new Class[0]);
      method.setAccessible(true);
      return (Boolean) method.invoke(null, new Object[0]);
    } catch (InvocationTargetException e) {
      final Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectHiddenFuncException(cause);
    } catch (Exception ex) {
      return false;
    }
  }

  private static Object getHtcTelephonyManagerDefault() throws ReflectHiddenFuncException {
    try {
      final Class<?> clz = Class.forName(EncryptUtils.decode(CommConstant.COM_HTC_TELEPHONY_HTCTELEPHONYMANAGER_TAG));
      final Method method = clz.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETDEFAULT_TAG), (Class[]) new Class[0]);
      method.setAccessible(true);
      return method.invoke(null, new Object[0]);
    } catch (ClassNotFoundException e) {
      throw new ReflectHiddenFuncException(e);
    } catch (NoSuchMethodException e2) {
      throw new ReflectHiddenFuncException(e2);
    } catch (IllegalAccessException e3) {
      throw new ReflectHiddenFuncException(e3);
    } catch (InvocationTargetException e4) {
      final Throwable cause = e4.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectHiddenFuncException(cause);
    }
  }

  private static int getHtcTelephonyManagerPhoneSlot(final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Class<?> clz = Class.forName(EncryptUtils.decode(CommConstant.COM_HTC_TELEPHONY_HTCTELEPHONYMANAGER_TAG));
      Field field;
      if (cardIndex == 0) {
        field = clz.getDeclaredField(EncryptUtils.decode(CommConstant.PHONE_SLOT1_TAG));
      } else {
        if (cardIndex != 1) {
          throw new IllegalArgumentException("cardIndex can only be 0 or 1");
        }
        field = clz.getDeclaredField(EncryptUtils.decode(CommConstant.PHONE_SLOT2_TAG));
      }
      field.setAccessible(true);
      return field.getInt(null);
    } catch (ClassNotFoundException e) {
      throw new ReflectHiddenFuncException(e);
    } catch (NoSuchFieldException e2) {
      throw new ReflectHiddenFuncException(e2);
    } catch (IllegalAccessException e3) {
      throw new ReflectHiddenFuncException(e3);
    }
  }

  public static String getSubscriberId(final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Object obj = getHtcTelephonyManagerDefault();
      return (String) obj.getClass().getMethod(EncryptUtils.decode(CommConstant.GETSUBSCRIBERIDEXT_TAG), Integer.TYPE).invoke(obj, getHtcTelephonyManagerPhoneSlot(cardIndex));
    } catch (NoSuchMethodException e) {
      throw new ReflectHiddenFuncException(e);
    } catch (IllegalAccessException e2) {
      throw new ReflectHiddenFuncException(e2);
    } catch (InvocationTargetException e3) {
      final Throwable cause = e3.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectHiddenFuncException(cause);
    }
  }

  private static Object newHtcWrapIfSmsManager() throws ReflectHiddenFuncException {
    try {
      final Class<?> clz = Class.forName(EncryptUtils.decode(CommConstant.COM_HTC_WRAP_ANDROID_TELEPHONY_HTCWRAPIFSMSMANAGER_TAG));
      final Constructor<?> con = clz.getDeclaredConstructor(SmsManager.class);
      con.setAccessible(true);
      return con.newInstance(SmsManager.getDefault());
    } catch (ClassNotFoundException e) {
      throw new ReflectHiddenFuncException(e);
    } catch (NoSuchMethodException e2) {
      throw new ReflectHiddenFuncException(e2);
    } catch (InstantiationException e3) {
      throw new ReflectHiddenFuncException(e3);
    } catch (IllegalAccessException e4) {
      throw new ReflectHiddenFuncException(e4);
    } catch (InvocationTargetException e5) {
      final Throwable cause = e5.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectHiddenFuncException(cause);
    }
  }

  public static void sendTextMessage(final String destinationAddress, final String scAddress, final String text, final PendingIntent sentIntent,
      final PendingIntent deliveryIntent, final Bundle bundle, final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Object obj = newHtcWrapIfSmsManager();
      final Method method = obj.getClass().getDeclaredMethod(EncryptUtils.decode(CommConstant.SENDTEXTMESSAGEEXT_TAG), String.class, String.class, String.class, PendingIntent.class,
          PendingIntent.class, Bundle.class, Integer.TYPE);
      method.setAccessible(true);
      method.invoke(obj, destinationAddress, scAddress, text, sentIntent, deliveryIntent, bundle, getHtcTelephonyManagerPhoneSlot(cardIndex));
    } catch (NoSuchMethodException e) {
      throw new ReflectHiddenFuncException(e);
    } catch (IllegalAccessException e2) {
      throw new ReflectHiddenFuncException(e2);
    } catch (InvocationTargetException e3) {
      final Throwable cause = e3.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectHiddenFuncException(cause);
    }
  }

  public static void sendDataMessage(final String destinationAddress, final String scAddress, final short destinationPort, final byte[] data,
      final PendingIntent sentIntent, final PendingIntent deliveryIntent, final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Object obj = newHtcWrapIfSmsManager();
      final Method method = obj.getClass().getDeclaredMethod(EncryptUtils.decode(CommConstant.SENDDATAMESSAGEEXT_TAG), String.class, String.class, Short.TYPE, byte[].class, PendingIntent.class,
          PendingIntent.class, Integer.TYPE);
      method.setAccessible(true);
      method.invoke(obj, destinationAddress, scAddress, destinationPort, data, sentIntent, deliveryIntent, getHtcTelephonyManagerPhoneSlot(cardIndex));
    } catch (NoSuchMethodException e) {
      throw new ReflectHiddenFuncException(e);
    } catch (IllegalAccessException e2) {
      throw new ReflectHiddenFuncException(e2);
    } catch (InvocationTargetException e3) {
      final Throwable cause = e3.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectHiddenFuncException(cause);
    }
  }
}
