package com.xdd.pay.imp;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.xdd.pay.util.PayAgentUtils;
import com.xdd.pay.util.PayUtils;

public class QmPay {
//    public static void init(Context context, final Handler handler) {
//    	PayAgentUtils.init(context, handler);
//    }
    public static void init(Context context,String s1,String s2) {
      PayAgentUtils.init(context,s1,s2);
    }

    public static void pay(Activity activity, Handler handler, String pointNum, int price) {
    	PayAgentUtils.pay(activity, handler, pointNum, price);
    }

    public static void netWorkPay(Activity activity, Handler handler, String pointNum, int price) {
    	PayAgentUtils.netWorkPay(activity, handler, pointNum, price);
    }
    
    public static void statistics(Context context, Handler handler, String pointNum, int price) {
		PayAgentUtils.statistics(context, handler, pointNum, price);
    }
    
	public static void onResume(Context context) {
		PayUtils.getInstance().onResume(context);
	}

	public static void onPause(Context context) {
		PayUtils.getInstance().onPause(context);
	}

	public static void onEvent(Context context, String eventId,
			HashMap<String, String> map) {
		PayUtils.getInstance().onEvent(context, eventId, map);
	}
	
//	public static void cleanCache(){
//		PushSDK.INSTANCE.clearCache();
//	}
}