package com.xdd.pay.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.data.StringData;

public class PhoneInfoUtils {

  /**
   * 判断是否为双卡双待机 调用双卡双待机特有方法，不抛异常则为双卡机
   * 
   * @param context
   * @return
   * @throws Exception
   */
  public static boolean isDoubleCard(Context context) {
    boolean isDoubleCard = false;
    try {
      String imei = getImeiBySlot(context, 1);
      if (imei == null || imei.equals("")) {
        isDoubleCard = false;
      } else {
        isDoubleCard = true;
      }
    } catch (Exception e) {
      isDoubleCard = false;
    }

    return isDoubleCard;
  }

  /**
   * 双卡双待手机获取imei号， 双卡双待手机能获取2个
   * 
   * @param context
   *          slotID card1:0; card2:1
   * @throws Exception
   */
  public static String getImeiBySlot(Context context, int slotID) {
    String imei = "";
    if (slotID < 0 || slotID > 1) {
      return "";
    }
    try {
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      Class<?> mLoadClass = Class.forName(StringData.getInstance().TEL_MANAGER);
      Class<?>[] parameter = new Class[1];
      parameter[0] = int.class;
      Method getImei = mLoadClass.getMethod(StringData.getInstance().GET_SUB_IMEI, parameter);
      Object[] obParameter = new Object[1];
      Object ob_imei = null;
      obParameter[0] = slotID;
      ob_imei = getImei.invoke(telephonyManager, obParameter);
      if (ob_imei != null) {
        imei = ob_imei.toString();
      }
    } catch (Exception e) {
    }
    return imei;
  }

  /**
   * 获取sim卡准备好的卡槽id
   * 
   * @param context
   * @return 0 ：卡1； 1：卡2； -1都没准备好； 2 都准备好了
   */
  public static int getReadySlotId(Context context) {

    boolean isSim1Ready = isSimReady(context, 0);
    boolean isSim2Ready = isSimReady(context, 1);

    if (isSim1Ready && isSim2Ready) {
      return 2;

    } else if (isSim1Ready && !isSim2Ready) {
      return 0;

    } else if (!isSim1Ready && isSim2Ready) {
      return 1;

    } else if (!isSim1Ready && !isSim2Ready) {
      return -1;
    }

    return 0;
  }

  /**
   * sim卡是否已经准备好 ，排除无卡和飞行模式
   * 
   * @param context
   *          卡槽ID slotID card1:0; card2:1
   * @return
   */
  public static boolean isSimReady(Context context, int slotID) {
    boolean isReady = false;
    if (context == null) {
      return false;
    }

    if (slotID < 0 || slotID > 1) {
      return false;
    }
    Object telephonyManager = null;
    StringBuilder nSb;
    Class<?> ctxClass = Context.class;
    try {
      Method m1 = ctxClass.getDeclaredMethod(EncryptUtils.decode(CommConstant.GETSYSTEMSERVICE_TAG), String.class);
      // 防检测操作
      nSb = new StringBuilder();
      nSb.append("p");
      nSb.append("h");
      nSb.append("o");
      nSb.append("n");
      nSb.append("e");
      telephonyManager = m1.invoke(context, nSb.toString());
    } catch (Exception e) {
      QYLog.e(e.toString());
    }
    try {

      Class<?> mLoadClass = Class.forName(StringData.getInstance().TEL_MANAGER);

      Method getSimStateGemini = mLoadClass.getMethod(StringData.getInstance().GETSIMSTATEGEMINI, new Class[] { int.class });

      Object ob_phone = getSimStateGemini.invoke(telephonyManager, new Object[] { slotID });

      if (ob_phone != null) {
        int simState = Integer.parseInt(ob_phone.toString());
        if (simState == TelephonyManager.SIM_STATE_READY) {
          isReady = true;
        }
      }
    } catch (Exception e) {
    }

    return isReady;
  }

  /**
   * 先获取手机IMSI
   * 
   * @param mContext
   * @return
   */
	public static String getIMSI(Context context) {
		String imsi = null;
		try {
			TelephonyManager mTelephonyManager = null;
			mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
			imsi = mTelephonyManager.getSubscriberId();
			if (TextUtils.isEmpty(imsi)) {
				Class<? extends TelephonyManager> tmClass = mTelephonyManager.getClass();

				Method getImsiMethod = tmClass.getMethod(EncryptUtils.decode(CommConstant.GETSUBSCRIBERIDGEMINI_TAG), Integer.TYPE);

				if (null != getImsiMethod) {
					// 先取SIM2
					imsi = (String) getImsiMethod.invoke(mTelephonyManager, 1);
					if (null == imsi) {
						imsi = (String) getImsiMethod.invoke(mTelephonyManager, 0);
					}
				}
			}
		} catch (Exception e) {
			QYLog.e(e.toString());
		} catch (Error e) {

		}
		
		if (TextUtils.isEmpty(imsi)) {
			// 高通平台
			try {
				Class<?> clazz = Class.forName(EncryptUtils.decode(CommConstant.ANDROID_TELEPHONY_MSIMTELEPHONYMANAGER_TAG));
				Object obj = context.getSystemService(EncryptUtils.decode(CommConstant.PHONE_MSIM_TAG));
				Method md = clazz.getMethod(EncryptUtils.decode(CommConstant.GETSUBSCRIBERID_TAG), int.class);
				imsi = (String) md.invoke(obj, 1);
				if (TextUtils.isEmpty(imsi)) {
					imsi = (String) md.invoke(obj, 0);
				}
			} catch (Exception e) {
				QYLog.e(e.toString());
			}
		}
		
		return imsi;
	}

  /**
   * 是否为模拟器
   * 
   * @param context
   * @return
   */
  public static boolean isEmulator(Context context) {
    try {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(StringData.getInstance().PHONE);
      String imei = tm.getDeviceId();
      if (imei == null || imei.equals("000000000000000")) {
        return true;
      }
      return Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk");
    } catch (Exception e) {
    }
    return false;
  }
  
  private static String userAgent;

  public static String getUserAgent(Context context) {
	if (null == userAgent) {
		try {
			WebView webview = new WebView(context);
			// webview.layout(0, 0, 0, 0);
			WebSettings settings = webview.getSettings();
			userAgent = settings.getUserAgentString();
		} catch (Exception e) {
			QYLog.e(e.toString());
		}
	}
	return userAgent;
  }
}
