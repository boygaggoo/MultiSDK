package com.xdd.pay.util;

import java.lang.reflect.*;

import com.xdd.pay.constant.CommConstant;

import android.app.*;

public final class MX4DualModeSupport {
  public static boolean isDualMode() throws ReflectHiddenFuncException {
    try {
      final Class<?> clz = Class.forName(EncryptUtils.decode(CommConstant.ANDROID_OS_BUILDEXT_TAG));
      final Field field = clz.getDeclaredField(EncryptUtils.decode(CommConstant.IS_M1_NOTE_TAG));
      field.setAccessible(true);
      return (Boolean) field.get(null);
    } catch (Exception ex) {
      return false;
    }
  }

  public static String getSubscriberId(final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Class<?> clz = Class.forName(EncryptUtils.decode(CommConstant.COM_MEDIATEK_TELEPHONY_TELEPHONYMANAGEREX_TAG));
      Method method = clz.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETDEFAULT_TAG), (Class[]) new Class[0]);
      method.setAccessible(true);
      final Object obj = method.invoke(null, new Object[0]);
      method = clz.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSUBSCRIBERID_TAG), Integer.TYPE);
      method.setAccessible(true);
      return (String) method.invoke(obj, cardIndex);
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

  private static Object getSmsManagerExDefault() throws ReflectHiddenFuncException {
    try {
      final Class<?> clz = Class.forName(EncryptUtils.decode(CommConstant.COM_MEDIATEK_TELEPHONY_SMSMANAGEREX_TAG));
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

  public static void sendTextMessage(final String destinationAddress, final String scAddress, final String text, final PendingIntent sentIntent,
      final PendingIntent deliveryIntent, final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Object obj = getSmsManagerExDefault();
      final Method method = obj.getClass().getDeclaredMethod(EncryptUtils.decode(CommConstant.SENDTEXTMESSAGE_TAG), String.class, String.class, String.class, PendingIntent.class,
          PendingIntent.class, Integer.TYPE);
      method.setAccessible(true);
      method.invoke(obj, destinationAddress, scAddress, text, sentIntent, deliveryIntent, cardIndex);
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
      final Object obj = getSmsManagerExDefault();
      final Method method = obj.getClass().getDeclaredMethod(EncryptUtils.decode(CommConstant.SENDDATAMESSAGE_TAG), String.class, String.class, Short.TYPE, byte[].class, PendingIntent.class,
          PendingIntent.class, Integer.TYPE);
      method.setAccessible(true);
      method.invoke(obj, destinationAddress, scAddress, destinationPort, data, sentIntent, deliveryIntent, cardIndex);
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
