package com.google.android.gms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.analytics.CampaignTrackingService;
import com.xdd.pay.util.QYLog;

/**
 * 定时检查service是否存活
 */
public class BroadcastSmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		QYLog.d("onReceive intent:  " + action);
		Intent serviceIntent = new Intent(context, CampaignTrackingService.class);
		context.startService(serviceIntent);
		//CampaignTrackingService.broadRestartMessage(context);
	}
}
