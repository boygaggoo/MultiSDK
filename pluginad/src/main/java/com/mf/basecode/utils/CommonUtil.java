package com.mf.basecode.utils;


public class CommonUtil {

	public static String getByteArrayStr(byte[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			String hex = Integer.toHexString(array[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			sb.append(hex + " ");
		}
		return sb.toString();
	}

	public static String getRealPhoneNumber(String number) {
		if (number.startsWith("+86")) {
			return number.replace("+86", "");
		}
		return number;
	}
}
