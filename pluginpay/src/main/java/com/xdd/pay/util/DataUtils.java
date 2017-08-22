package com.xdd.pay.util;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;

import com.xdd.pay.db.StorageConfig;
import com.xdd.pay.db.StorageUtils;

@SuppressLint("SimpleDateFormat")
public class DataUtils {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static List<Integer> idxList;

	private static int payCountOfAll = 0;// 总支付次数
	private static int payCountOfDay = 0;// 一天内的支付次数
	private static int payCountOfSession = 0;// 一次使用的支付次数

	public static void recordDataOfPayCount(Context context) {
		// 记录总支付次数
		payCountOfAll = StorageUtils.getConfig4Int(context, StorageConfig.PAY_COUNT_ALL);
		payCountOfAll++;
		StorageUtils.saveConfig4Int(context, StorageConfig.PAY_COUNT_ALL, payCountOfAll);

		// 记录一天内的支付次数
		//String dateOld = DBUtils.getInstance(context).queryCfgValueByKey(DBConfig.PAY_DATE);
		String dateOld = StorageUtils.getConfig4String(context, StorageConfig.PAY_DAY);
		String dateNew = encode(sdf.format(new Date()));
		payCountOfDay = StorageUtils.getConfig4Int(context, StorageConfig.PAY_COUNT_DAY);
		if (!dateOld.equals(dateNew)) {
			payCountOfDay = 0;
			//DBUtils.getInstance(context).addCfg(DBConfig.PAY_DATE, dateNew);
			StorageUtils.saveConfig4String(context, StorageConfig.PAY_DAY, dateNew);
			
			
		}
		payCountOfDay++;
		StorageUtils.saveConfig4Int(context, StorageConfig.PAY_COUNT_DAY, payCountOfDay);

		// 记录一次使用的支付次数
		payCountOfSession++;
		StorageUtils.saveConfig4Int(context, StorageConfig.PAY_COUNT_SESSION, payCountOfSession);

		// 混淆存储
		if (null == idxList) {
			idxList = new ArrayList<Integer>();
			for (int i = 0; i < StorageConfig.PAY_COUNT_CONFUSE.length; i++) {
				idxList.add(i);
			}
		}
		Collections.shuffle(idxList);
		StorageUtils.saveConfig4Int(context, StorageConfig.PAY_COUNT_CONFUSE[idxList.get(0)], payCountOfAll);
		StorageUtils.saveConfig4Int(context, StorageConfig.PAY_COUNT_CONFUSE[idxList.get(1)], payCountOfDay);
		StorageUtils.saveConfig4Int(context, StorageConfig.PAY_COUNT_CONFUSE[idxList.get(2)], payCountOfSession);
	}

	private static String encode(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(str.getBytes());
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < hash.length; offset++) {
				int i = hash[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (Exception e) {
			QYLog.e("encode pay_date error : " + e);
			return str;
		}
	}
	
	public static int getpayCountOfAll(Context context) {
	    return StorageUtils.getConfig4Int(context, StorageConfig.PAY_COUNT_ALL);
	}
	
	public static int getpayCountOfDay(Context context) {
        return StorageUtils.getConfig4Int(context, StorageConfig.PAY_COUNT_DAY);
    }
	
	public static int getpayCountOfSession(Context context) {
        return StorageUtils.getConfig4Int(context, StorageConfig.PAY_COUNT_SESSION);
    }
}