package com.xdd.pay.util;

import android.content.*;

import java.lang.reflect.*;
import java.util.Locale;

import com.xdd.pay.constant.CommConstant;

import android.telephony.*;
import android.text.*;
import android.os.*;
import android.net.*;

public final class TelephonyMgr {
  public static final int NETWORK_CLASS_UNKNOWN = 0;
  public static final int NETWORK_CLASS_2_G     = 1;
  public static final int NETWORK_CLASS_3_G     = 2;
  public static final int NETWORK_CLASS_4_G     = 3;

  public static boolean isDualMode(final Context context) {
    try {
      if (Build.VERSION.SDK_INT >= 21) {
        return getSimCount(context) >= 2;
      }
      boolean isDualMode = HtcDualModeSupport.isDualMode();
      if (isDualMode) {
        return isDualMode;
      }
      isDualMode = MX4DualModeSupport.isDualMode();
      if (isDualMode) {
        return isDualMode;
      }
      final Method method = Class.forName(EncryptUtils.decode(CommConstant.ANDROID_OS_SERVICEMANAGER_TAG)).getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSERVICE_TAG), String.class);
      method.setAccessible(true);
      final String model = Build.MODEL;
      if (EncryptUtils.decode(CommConstant.PHILIPS_T939_TAG).equals(model)) {
        return method.invoke(null, EncryptUtils.decode(CommConstant.PHONE0_TAG)) != null && method.invoke(null, EncryptUtils.decode(CommConstant.PHONE1_TAG)) != null;
      }
      return (method.invoke(null, EncryptUtils.decode(CommConstant.PHONE_TAG)) != null && method.invoke(null, EncryptUtils.decode(CommConstant.PHONE2_TAG)) != null)
          || (method.invoke(null, EncryptUtils.decode(CommConstant.TELEPHONY_REGISTRY_TAG)) != null && method.invoke(null, EncryptUtils.decode(CommConstant.TELEPHONY_REGISTRY2_TAG)) != null);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e2) {
      e2.printStackTrace();
    } catch (InvocationTargetException e3) {
      e3.printStackTrace();
    } catch (IllegalAccessException e4) {
      e4.printStackTrace();
    } catch (ReflectHiddenFuncException e5) {
      e5.printStackTrace();
    }
    return false;
  }

  public static String getIMEI(final Context context) {
    final TelephonyManager tm = (TelephonyManager) context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_TAG));
    final String imei = tm.getDeviceId();
    return TextUtils.isEmpty((CharSequence) imei) ? "0" : imei;
  }

  public static String getIMSI(final Context context) {
    String imsi = null;
    if (isDualMode(context)) {
      if (isChinaMobileCard(context, 0)) {
        imsi = getSubscriberId(context, 0);
      } else if (isChinaMobileCard(context, 1)) {
        imsi = getSubscriberId(context, 1);
      }
    } else {
      imsi = getSubscriberId(context, 0);
    }
    if (TextUtils.isEmpty((CharSequence) imsi)) {
      imsi = getSubscriberId(context);
    }
    if (TextUtils.isEmpty((CharSequence) imsi)) {
      imsi = "0";
    }
    return imsi;
  }

  public static String getSubscriberId(final Context context, final int cardIndex) {
    final boolean isDualMode = isDualMode(context);
    String name = null;
    final String model = Build.MODEL;
    if (cardIndex == 0) {
      if (EncryptUtils.decode(CommConstant.PHILIPS_T939_TAG).equals(model)) {
        name = EncryptUtils.decode(CommConstant.IPHONESUBINFO0_TAG);
      } else {
        name = EncryptUtils.decode(CommConstant.IPHONESUBINFO_TAG);
      }
    } else {
      if (cardIndex != 1) {
        return getSubscriberId(context);
      }
      if (!isDualMode) {
        return null;
      }
      if (EncryptUtils.decode(CommConstant.PHILIPS_T939_TAG).equals(model)) {
        name = EncryptUtils.decode(CommConstant.IPHONESUBINFO1_TAG);
      } else {
        name = EncryptUtils.decode(CommConstant.IPHONESUBINFO2_TAG);
      }
    }
    if (isDualMode) {
      try {
        if (Build.VERSION.SDK_INT == 21) {
          return LollipopDualModeSupport.getSubscriberId(context, cardIndex);
        }
        if (Build.VERSION.SDK_INT >= 22) {
          return Lollipop_mr1DualModeSupport.getSubscriberId(context, cardIndex);
        }
        if (HtcDualModeSupport.isDualMode()) {
          return HtcDualModeSupport.getSubscriberId(cardIndex);
        }
        if (MX4DualModeSupport.isDualMode()) {
          return MX4DualModeSupport.getSubscriberId(cardIndex);
        }
        Method method = Class.forName(EncryptUtils.decode(CommConstant.ANDROID_OS_SERVICEMANAGER_TAG)).getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSERVICE_TAG), String.class);
        method.setAccessible(true);
        Object param = method.invoke(null, name);
        if (param == null && cardIndex == 1) {
          param = method.invoke(null, EncryptUtils.decode(CommConstant.IPHONESUBINFO1_TAG));
        }
        if (param == null) {
          return "0";
        }
        method = Class.forName(EncryptUtils.decode(CommConstant.COM_ANDROID_INTERNAL_TELEPHONY_IPHONESUBINFO$STUB_TAG)).getDeclaredMethod(EncryptUtils.decode(CommConstant.ASINTERFACE_TAG), IBinder.class);
        method.setAccessible(true);
        final Object stubObj = method.invoke(null, param);
        return (String) stubObj.getClass().getMethod(EncryptUtils.decode(CommConstant.GETSUBSCRIBERID_TAG), (Class<?>[]) new Class[0]).invoke(stubObj, new Object[0]);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e2) {
        e2.printStackTrace();
      } catch (InvocationTargetException e3) {
        e3.printStackTrace();
      } catch (IllegalAccessException e4) {
        e4.printStackTrace();
      } catch (ReflectHiddenFuncException e5) {
        e5.printStackTrace();
      }
    }
    return getSubscriberId(context);
  }

  public static String getSubscriberId(final Context context) {
    final TelephonyManager tm = (TelephonyManager) context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_TAG));
    return tm.getSubscriberId();
  }

  public static String getOperator(final String imsi) {
    if (TextUtils.isEmpty((CharSequence) imsi)) {
      return "0";
    }
    if (imsi.contains("46000") || imsi.contains("46002") || imsi.contains("46007")) {
      return "1";
    }
    if (imsi.contains("46001") || imsi.contains("46006")) {
      return "2";
    }
    if (imsi.contains("46003") || imsi.contains("46005")) {
      return "3";
    }
    return "0";
  }

  @SuppressWarnings("unused")
private static int getSimState(final String simState) {
    try {
      final Method method = Class.forName(EncryptUtils.decode(CommConstant.ANDROID_OS_SYSTEMPROPERTIES_TAG)).getDeclaredMethod(EncryptUtils.decode(CommConstant.FIND_TAG), String.class);
      method.setAccessible(true);
      String prop = (String) method.invoke(null, simState);
      if (prop != null) {
        prop = prop.split(",")[0];
      }
      if (EncryptUtils.decode(CommConstant.ABSENT_TAG).equals(prop)) {
        return 1;
      }
      if (EncryptUtils.decode(CommConstant.PIN_REQUIRED_TAG).equals(prop)) {
        return 2;
      }
      if (EncryptUtils.decode(CommConstant.PUK_REQUIRED_TAG).equals(prop)) {
        return 3;
      }
      if (EncryptUtils.decode(CommConstant.NETWORK_LOCKED_TAG).equals(prop)) {
        return 4;
      }
      if (EncryptUtils.decode(CommConstant.READY_TAG).equals(prop)) {
        return 5;
      }
      return 0;
    } catch (Exception ex) {
      return 0;
    }
  }

  public static boolean isChinaMobileCard(final Context context, final int cardIndex) {
    return "1".equals(getOperator(getSubscriberId(context, cardIndex)));
  }

  public static boolean isChinaMobileCard(final Context context) {
    return isSimcardAvailable(context) && "1".equals(getOperator(getIMSI(context)));
  }

  public static boolean isSimcardOK(final Context context) {
    if (isDualMode(context)) {
      return isChinaMobileCard(context, 0) || isChinaMobileCard(context, 1) || isChinaMobileCard(context);
    }
    return isChinaMobileCard(context);
  }

  public static boolean isInternationalRoaming(final Context context) {
    final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_TAG));
    final String simCountryCode = telephonyManager.getSimCountryIso();
    final String internetCountryCode = telephonyManager.getNetworkCountryIso();
    return !TextUtils.isEmpty((CharSequence) simCountryCode) && !TextUtils.isEmpty((CharSequence) internetCountryCode)
        && !simCountryCode.equals(internetCountryCode);
  }

  public static String getNetworkApnType(final Context context) {
    final NetworkInfo info = getActiveNetworkInfo(context);
    if (info == null) {
      return "NONE";
    }
    if (info.getType() == 1) {
      return "WIFI";
    }
    final String extraInfo = info.getExtraInfo();
    if (extraInfo == null) {
      return info.getTypeName().toUpperCase(Locale.getDefault());
    }
    return extraInfo.toUpperCase(Locale.getDefault());
  }

  public static NetworkInfo getActiveNetworkInfo(final Context context) {
    final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(EncryptUtils.decode(CommConstant.CONNECTIVITY_TAG));
    return connectivityManager.getActiveNetworkInfo();
  }

  public static boolean isSimcardAvailable(final Context context) {
    final TelephonyManager tm = (TelephonyManager) context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_TAG));
    return tm.getSimState() == 5;
  }

  public static boolean isNetworkConnected(final Context context) {
    final ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(EncryptUtils.decode(CommConstant.CONNECTIVITY_TAG));
    final NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();
    final boolean ret = activeNetwork != null && activeNetwork.isConnected();
    if (ret) {
      // Util.log("Network", "network [" + activeNetwork.getExtraInfo() +
      // "] is connected.", true);
    }
    return ret;
  }

  public static int getNetworkSubType(final Context context) {
    final ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(EncryptUtils.decode(CommConstant.CONNECTIVITY_TAG));
    final NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();
    if (activeNetwork == null || !activeNetwork.isConnected()) {
      return 0;
    }
    return activeNetwork.getSubtype();
  }

  public class NetworkType {
    public static final int NETWORK_TYPE_IDEN   = 11;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_LTE    = 13;
    public static final int NETWORK_TYPE_EHRPD  = 14;
    public static final int NETWORK_TYPE_HSPAP  = 15;
  }

  public static final class Operator {
    public static final String NONE  = "0";
    public static final String CMCC  = "1";
    public static final String CUCC  = "2";
    public static final String CTCC  = "3";
    public static final String OTHER = "4";
  }

  public static final class ApnType {
    public static final String WIFI   = "WIFI";
    public static final String NONE   = "NONE";
    public static final String CMNET  = "CMNET";
    public static final String CMWAP  = "CMWAP";
    public static final String UNINET = "UNNET";
    public static final String UNIWAP = "UNWAP";
    public static final String CTNET  = "CTNET";
    public static final String CTWAP  = "CTWAP";
  }

  public static int getSimCount(final Context context) throws ReflectHiddenFuncException {
    final TelephonyManager tm = (TelephonyManager) context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_TAG));
    try {
      Method method = null;
      try {
        method = TelephonyManager.class.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSIMCOUNT_TAG), (Class<?>[]) new Class[0]);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
      method.setAccessible(true);
      return (Integer) method.invoke(tm, new Object[0]);
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
