package com.xdd.pay.util;

import android.content.Context;
import android.content.res.Resources;

public class ResourceIdUtils {
	/**
	 * �����Դ���ȡ��Դid
	 * @param context
	 * @param name ��R.layout.aa
	 * @return
	 */
	public static int getResourceId(Context context, String name) {
		Resources res = context.getResources();
		String[] strs = name.split("\\.");
		return res.getIdentifier(strs[2], strs[1], context.getPackageName());
	}
}
