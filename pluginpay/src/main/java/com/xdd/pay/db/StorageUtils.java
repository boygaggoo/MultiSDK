package com.xdd.pay.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StorageUtils {

	private static SharedPreferences getSP(Context context) {
		return context.getSharedPreferences(StorageConfig.NAME, Context.MODE_PRIVATE);
	}

	public static void saveConfig4Int(Context context, String key, int val) {
		SharedPreferences spQY = getSP(context);
		Editor editor = spQY.edit();
		editor.putInt(key, val);
		editor.commit();
	}

	public static void saveConfig4String(Context context, String key, String val) {
		SharedPreferences spQY = getSP(context);
		Editor editor = spQY.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public static int getConfig4Int(Context context, String key) {
		SharedPreferences spQY = getSP(context);
		return spQY.getInt(key, 0);
	}

	public static String getConfig4String(Context context, String key) {
		SharedPreferences spQY = getSP(context);
		return spQY.getString(key, "");
	}

}