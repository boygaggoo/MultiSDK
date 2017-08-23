package com.mf.basecode.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class PhoneImsiImeiExpandGet {
	public String phoneImsi = null;
	
	private static int simId_1=0;
    private int simId_2=0;
    private String imsi_1="";
    private String imsi_2="";
    private String imei_1="";
    private String imei_2="";
    @SuppressWarnings("unused")
	private int phoneType_1=0;
    @SuppressWarnings("unused")
	private int phoneType_2=0;
    @SuppressWarnings("unused")
	private String defaultImsi="";
    @SuppressWarnings("unused")
	private boolean isMtkDoubleSim = false;
    @SuppressWarnings("unused")
	private boolean isMtkSecondDoubleSim = false;
    @SuppressWarnings("unused")
	private boolean isSpreadDoubleSim = false;
    @SuppressWarnings("unused")
	private boolean isQualcommDoubleSim = false;
      
    private String spreadTmService="";
    
    public void init(Context context) {
    	initMtkDoubleSim(context);
    	if (isInvalidImsiOrImei(imsi_1)&& isInvalidImsiOrImei(imsi_2)) {
    		initMtkSecondDoubleSim(context);
    		if(isInvalidImsiOrImei(imsi_1)&& isInvalidImsiOrImei(imsi_2)) {
    			initSpreadDoubleSim(context);
    			if(isInvalidImsiOrImei(imsi_1)&& isInvalidImsiOrImei(imsi_2)) {
    				initQualcommDoubleSim(context);
        		}
    		}
    	}
    }
    
    private boolean isInvalidImsiOrImei(final String str) {
    	if(TextUtils.isEmpty(str)||str.length()<14)
    		return true;
    	return false;
    }
    
    public String getImsi(final String imsi){
    	if (isInvalidImsiOrImei(imsi_1)&& isInvalidImsiOrImei(imsi_2)){
    		return imsi;
    	} else {
    		if(!isInvalidImsiOrImei(imsi_1)&& !imsi_1.equals(imsi)) {
    			return imsi_1;
    		} else if(!isInvalidImsiOrImei(imsi_2)&& !imsi_2.equals(imsi)) {
    			return imsi_2;
    		}
    	}
    	return imsi;
    }
    
    public String getImei(final String imei){
    	if (isInvalidImsiOrImei(imei_1)&& isInvalidImsiOrImei(imei_2)){
    		return imei;
    	} else {
    		if(!isInvalidImsiOrImei(imei_1)&& !imei_1.equals(imei)) {
    			return imei_1;
    		} else if(!isInvalidImsiOrImei(imei_2)&& !imei_2.equals(imei)) {
    			return imei_2;
    		}
    	}
    	return imei;
    }
    
	private void initMtkDoubleSim(Context mContext) {
    try {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);
 
            Method m = TelephonyManager.class.getDeclaredMethod(
                    "getSubscriberIdGemini", int.class);
            imsi_1 = (String) m.invoke(tm, simId_1);
            imsi_2 = (String) m.invoke(tm, simId_2);
 
            Method m1 = TelephonyManager.class.getDeclaredMethod(
                    "getDeviceIdGemini", int.class);
            imei_1 = (String) m1.invoke(tm, simId_1);
            imei_2 = (String) m1.invoke(tm, simId_2);
 
            Method mx = TelephonyManager.class.getDeclaredMethod(
                    "getPhoneTypeGemini", int.class);
            phoneType_1 = (Integer) mx.invoke(tm, simId_1);
            phoneType_2 = (Integer) mx.invoke(tm, simId_2);
 
            if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2))) {
                defaultImsi = imsi_2;
            }
            if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1))) {
                defaultImsi = imsi_1;
            }
        } catch (Exception e) {
            isMtkDoubleSim = false;
            return;
        }
        isMtkDoubleSim = true;
    }
    
    private void initMtkSecondDoubleSim(Context mContext) {
        try {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName("com.android.internal.telephony.Phone");
            Field fields1 = c.getField("GEMINI_SIM_1");
            fields1.setAccessible(true);
            simId_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField("GEMINI_SIM_2");
            fields2.setAccessible(true);
            simId_2 = (Integer) fields2.get(null);
 
            Method mx = TelephonyManager.class.getMethod("getDefault",
                    int.class);
            TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
            TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);
 
            imsi_1 = tm1.getSubscriberId();
            imsi_2 = tm2.getSubscriberId();
 
            imei_1 = tm1.getDeviceId();
            imei_2 = tm2.getDeviceId();
 
            phoneType_1 = tm1.getPhoneType();
            phoneType_2 = tm2.getPhoneType();
 
            if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2))) {
                defaultImsi = imsi_2;
            }
            if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1))) {
                defaultImsi = imsi_1;
            }
 
        } catch (Exception e) {
            isMtkSecondDoubleSim = false;
            return;
        }
        isMtkSecondDoubleSim = true;
    }
    
    private void initSpreadDoubleSim(Context mContext) {
        try {
            Class<?> c = Class
                    .forName("com.android.internal.telephony.PhoneFactory");
            Method m = c.getMethod("getServiceName", String.class, int.class);
            spreadTmService = (String) m
                    .invoke(c, Context.TELEPHONY_SERVICE, 1);
 
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            imsi_1 = tm.getSubscriberId();
            imei_1 = tm.getDeviceId();
            phoneType_1 = tm.getPhoneType();
            TelephonyManager tm1 = (TelephonyManager) mContext.getSystemService(spreadTmService);
            imsi_2 = tm1.getSubscriberId();
            imei_2 = tm1.getDeviceId();
            phoneType_2 = tm1.getPhoneType();
            if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2))) {
                defaultImsi = imsi_2;
            }
            if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1))) {
                defaultImsi = imsi_1;
            }
 
        } catch (Exception e) {
            isSpreadDoubleSim = false;
            return;
        }
        isSpreadDoubleSim = true;
    }
    
    private void initQualcommDoubleSim(Context mContext) {
    	try {
    	TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    	Class<?> cx = Class
    	.forName("android.telephony.MSimTelephonyManager");
    	Object obj =mContext.getSystemService(
    	"phone_msim");
    	simId_1 = 0;
    	simId_2 = 1;
    	  
    	Method mx = cx.getMethod("getDataState");

    	//int stateimei_2 = tm.getDataState();
    	//Method mde = cx.getMethod("getDefault");
    	Method md = cx.getMethod("getDeviceId", int.class);
    	Method ms = cx.getMethod("getSubscriberId", int.class);
    	//Method mp = cx.getMethod("getPhoneType");
    	  
    	imei_1 = (String) md.invoke(obj, simId_1);
    	imei_2 = (String) md.invoke(obj, simId_2);
    	  
    	imsi_1 = (String) ms.invoke(obj, simId_1);
    	imsi_2 = (String) ms.invoke(obj, simId_2);
    	  
    	int statephoneType_1 = tm.getDataState();
    	int statephoneType_2 = (Integer) mx.invoke(obj);
    	Log.e("tag", statephoneType_1 + "---" + statephoneType_2);
    	  
    	} catch (Exception e) {
    	isQualcommDoubleSim = false;
    	return;
    	}
    	isQualcommDoubleSim = true;
    	  
    	}
}
