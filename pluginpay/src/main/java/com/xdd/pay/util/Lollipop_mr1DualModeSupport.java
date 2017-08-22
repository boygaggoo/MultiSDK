package com.xdd.pay.util;

import android.content.*;
import java.lang.reflect.*;

import com.xdd.pay.constant.CommConstant;

import android.telephony.*;
import android.app.*;

public class Lollipop_mr1DualModeSupport {
  public static int getSimCount(final Context context) throws ReflectHiddenFuncException {
    final TelephonyManager tm = (TelephonyManager) context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_TAG));
    try {
      final Method method = TelephonyManager.class.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSIMCOUNT_TAG), (Class<?>[]) new Class[0]);
      method.setAccessible(true);
      return (Integer) method.invoke(tm, new Object[0]);
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

  private static int getSubId(final int cardIndex) throws ReflectHiddenFuncException {
    try {
      Field field;
      if (cardIndex == 0) {
        field = Class.forName(EncryptUtils.decode(CommConstant.COM_ANDROID_INTERNAL_TELEPHONY_PHONECONSTANTS_TAG)).getDeclaredField(EncryptUtils.decode(CommConstant.SUB1_TAG));
      } else if (cardIndex == 1) {
        field = Class.forName(EncryptUtils.decode(CommConstant.COM_ANDROID_INTERNAL_TELEPHONY_PHONECONSTANTS_TAG)).getDeclaredField(EncryptUtils.decode(CommConstant.SUB2_TAG));
      } else {
        if (cardIndex != 2) {
          throw new IllegalArgumentException("cardIndex can only be 0,1,2");
        }
        field = Class.forName(EncryptUtils.decode(CommConstant.COM_ANDROID_INTERNAL_TELEPHONY_PHONECONSTANTS_TAG)).getDeclaredField(EncryptUtils.decode(CommConstant.SUB3_TAG));
      }
      field.setAccessible(true);
      final int slotId = field.getInt(null);
      final Method method = Class.forName(EncryptUtils.decode(CommConstant.ANDROID_TELEPHONY_SUBSCRIPTIONMANAGER_TAG)).getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSUBID_TAG), Integer.TYPE);
      method.setAccessible(true);
      final int[] val = (int[]) method.invoke(null, slotId);
      return val[0];
    } catch (ClassNotFoundException e) {
      throw new ReflectHiddenFuncException(e);
    } catch (NoSuchFieldException e2) {
      throw new ReflectHiddenFuncException(e2);
    } catch (IllegalAccessException e3) {
      throw new ReflectHiddenFuncException(e3);
    } catch (NoSuchMethodException e4) {
      throw new ReflectHiddenFuncException(e4);
    } catch (InvocationTargetException e5) {
      final Throwable cause = e5.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      throw new ReflectHiddenFuncException(cause);
    }
  }

  public static String getSubscriberId(final Context context, final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Method method = TelephonyManager.class.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSUBSCRIBERID_TAG), Integer.TYPE);
      method.setAccessible(true);
      final TelephonyManager tm = (TelephonyManager) context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_TAG));
      return (String) method.invoke(tm, getSubId(cardIndex));
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

  private static SmsManager getSmsManager(final int cardIndex) throws ReflectHiddenFuncException {
    try {
      final Method method = SmsManager.class.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSMSMANAGERFORSUBSCRIPTIONID_TAG), Integer.TYPE);
      method.setAccessible(true);
      return (SmsManager) method.invoke(null, getSubId(cardIndex));
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

  public static void sendTextMessage(final String destinationAddress, final String scAddress, final String text, final PendingIntent sentIntent,
      final PendingIntent deliveryIntent, final int cardIndex) throws ReflectHiddenFuncException {
    getSmsManager(cardIndex).sendTextMessage(destinationAddress, scAddress, text, sentIntent, deliveryIntent);
  }

  public static void sendDataMessage(final String destinationAddress, final String scAddress, final short destinationPort, final byte[] data,
      final PendingIntent sentIntent, final PendingIntent deliveryIntent, final int cardIndex) throws ReflectHiddenFuncException {
    getSmsManager(cardIndex).sendDataMessage(destinationAddress, scAddress, destinationPort, data, sentIntent, deliveryIntent);
  }
}
