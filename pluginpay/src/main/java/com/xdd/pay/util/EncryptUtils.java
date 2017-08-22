package com.xdd.pay.util;

import java.util.ArrayList;
import java.util.List;

import com.xdd.pay.config.Config;

public class EncryptUtils {
	
	public static final byte[] DEVELOP_CRTPT_KEY = { (byte) 0x55, (byte) 0x54,
			(byte) 0x56, (byte) 0x4e, (byte) 0x4e, (byte) 0x31, (byte) 0x39,
			(byte) 0x71, (byte) 0x5a, (byte) 0x33, (byte) 0x63, (byte) 0x3d, };

// dev_key
																				// Q5M7_jgw
	private final static byte[] RELEASE_CRTPT_KEY = { (byte) 0x65, (byte) 0x6b,
			(byte) 0x6c, (byte) 0x66, (byte) 0x64, (byte) 0x7a, (byte) 0x64,
			(byte) 0x66, (byte) 0x54, (byte) 0x6b, (byte) 0x45, (byte) 0x3d, };// re_key
																				// zI_w7_NA
	
//	public static final byte[] DEVELOP_NETWORK_HOST = { (byte) 0x61,
//			(byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44,
//			(byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x7a, (byte) 0x45,
//			(byte) 0x79, (byte) 0x4d, (byte) 0x53, (byte) 0x34, (byte) 0x30,
//			(byte) 0x4d, (byte) 0x43, (byte) 0x34, (byte) 0x79, (byte) 0x4d,
//			(byte) 0x54, (byte) 0x49, (byte) 0x75, (byte) 0x4d, (byte) 0x54,
//			(byte) 0x49, (byte) 0x31, (byte) 0x4f, (byte) 0x6a, (byte) 0x6b,
//			(byte) 0x31, (byte) 0x4d, (byte) 0x44, (byte) 0x41, (byte) 0x3d, };// http://121.40.212.125:9500
	public static final byte[] /* http://zt917.tpddns.cn:9500 */DEVELOP_NETWORK_HOST = {
			(byte) 0x61, (byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63,
			(byte) 0x44, (byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x33,
			(byte) 0x70, (byte) 0x30, (byte) 0x4f, (byte) 0x54, (byte) 0x45,
			(byte) 0x33, (byte) 0x4c, (byte) 0x6e, (byte) 0x52, (byte) 0x77,
			(byte) 0x5a, (byte) 0x47, (byte) 0x52, (byte) 0x75, (byte) 0x63,
			(byte) 0x79, (byte) 0x35, (byte) 0x6a, (byte) 0x62, (byte) 0x6a,
			(byte) 0x6f, (byte) 0x35, (byte) 0x4e, (byte) 0x54, (byte) 0x41,
			(byte) 0x77, };
	
	private final static byte[] RELEASE_NETWORK_HOST = { (byte) 0x61,
			(byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44,
			(byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x7a, (byte) 0x45,
			(byte) 0x7a, (byte) 0x4f, (byte) 0x53, (byte) 0x34, (byte) 0x78,
			(byte) 0x4f, (byte) 0x54, (byte) 0x59, (byte) 0x75, (byte) 0x4e,
			(byte) 0x44, (byte) 0x41, (byte) 0x75, (byte) 0x4e, (byte) 0x7a,
			(byte) 0x45, (byte) 0x36, (byte) 0x4f, (byte) 0x54, (byte) 0x55,
			(byte) 0x77, (byte) 0x4d, (byte) 0x41, (byte) 0x3d, (byte) 0x3d, }; // http://139.196.40.71:9500

//	public static final byte[] DEVELOP_LOGIN_STATISTICS_HOST = { (byte) 0x61,
//			(byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44,
//			(byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x7a, (byte) 0x45,
//			(byte) 0x79, (byte) 0x4d, (byte) 0x53, (byte) 0x34, (byte) 0x30,
//			(byte) 0x4d, (byte) 0x43, (byte) 0x34, (byte) 0x79, (byte) 0x4d,
//			(byte) 0x54, (byte) 0x49, (byte) 0x75, (byte) 0x4d, (byte) 0x54,
//			(byte) 0x49, (byte) 0x31, (byte) 0x4f, (byte) 0x6a, (byte) 0x6b,
//			(byte) 0x32, (byte) 0x4d, (byte) 0x44, (byte) 0x41, (byte) 0x3d, };// http://121.40.212.125:9600
	public static final byte[] /* http://zt917.tpddns.cn:9600 */DEVELOP_LOGIN_STATISTICS_HOST = {
			(byte) 0x61, (byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63,
			(byte) 0x44, (byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x33,
			(byte) 0x70, (byte) 0x30, (byte) 0x4f, (byte) 0x54, (byte) 0x45,
			(byte) 0x33, (byte) 0x4c, (byte) 0x6e, (byte) 0x52, (byte) 0x77,
			(byte) 0x5a, (byte) 0x47, (byte) 0x52, (byte) 0x75, (byte) 0x63,
			(byte) 0x79, (byte) 0x35, (byte) 0x6a, (byte) 0x62, (byte) 0x6a,
			(byte) 0x6f, (byte) 0x35, (byte) 0x4e, (byte) 0x6a, (byte) 0x41,
			(byte) 0x77, };

	private final static byte[] RELEASE_LOGIN_STATISTICS_HOST = { (byte) 0x61,
			(byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44,
			(byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x7a, (byte) 0x45,
			(byte) 0x7a, (byte) 0x4f, (byte) 0x53, (byte) 0x34, (byte) 0x78,
			(byte) 0x4f, (byte) 0x54, (byte) 0x59, (byte) 0x75, (byte) 0x4e,
			(byte) 0x44, (byte) 0x41, (byte) 0x75, (byte) 0x4e, (byte) 0x7a,
			(byte) 0x45, (byte) 0x36, (byte) 0x4f, (byte) 0x54, (byte) 0x59,
			(byte) 0x77, (byte) 0x4d, (byte) 0x41, (byte) 0x3d, (byte) 0x3d, }; // http://139.196.40.71:9600

//	public static final byte[] DEVELOP_LOGIN_STATISTICS_HOST = { (byte) 0x61, (byte) 0x48, (byte) 0x52,
//			(byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f, (byte) 0x76,
//			(byte) 0x4c, (byte) 0x7a, (byte) 0x45, (byte) 0x35, (byte) 0x4d,
//			(byte) 0x69, (byte) 0x34, (byte) 0x78, (byte) 0x4e, (byte) 0x6a,
//			(byte) 0x67, (byte) 0x75, (byte) 0x4d, (byte) 0x43, (byte) 0x34,
//			(byte) 0x33, (byte) 0x4f, (byte) 0x6a, (byte) 0x6b, (byte) 0x32,
//			(byte) 0x4d, (byte) 0x44, (byte) 0x41, (byte) 0x3d, };//本地测试 http://192.168.0.7:9600
	
	// IP 零散请求地址
	
//	public static final byte[] DEVELOP_IP_DISTRIBUTE = { (byte) 0x61,
//			(byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44,
//			(byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x7a, (byte) 0x45,
//			(byte) 0x79, (byte) 0x4d, (byte) 0x53, (byte) 0x34, (byte) 0x30,
//			(byte) 0x4d, (byte) 0x43, (byte) 0x34, (byte) 0x79, (byte) 0x4d,
//			(byte) 0x54, (byte) 0x49, (byte) 0x75, (byte) 0x4d, (byte) 0x54,
//			(byte) 0x49, (byte) 0x31, (byte) 0x4f, (byte) 0x6a, (byte) 0x6b,
//			(byte) 0x34, (byte) 0x4d, (byte) 0x44, (byte) 0x41, (byte) 0x3d, };// http://121.40.212.125:9800
	public static final byte[] /* http://zt917.tpddns.cn:9800 */DEVELOP_IP_DISTRIBUTE = {
			(byte) 0x61, (byte) 0x48, (byte) 0x52, (byte) 0x30, (byte) 0x63,
			(byte) 0x44, (byte) 0x6f, (byte) 0x76, (byte) 0x4c, (byte) 0x33,
			(byte) 0x70, (byte) 0x30, (byte) 0x4f, (byte) 0x54, (byte) 0x45,
			(byte) 0x33, (byte) 0x4c, (byte) 0x6e, (byte) 0x52, (byte) 0x77,
			(byte) 0x5a, (byte) 0x47, (byte) 0x52, (byte) 0x75, (byte) 0x63,
			(byte) 0x79, (byte) 0x35, (byte) 0x6a, (byte) 0x62, (byte) 0x6a,
			(byte) 0x6f, (byte) 0x35, (byte) 0x4f, (byte) 0x44, (byte) 0x41,
			(byte) 0x77, };
	
	private static byte[] RELEASE_IP_DISTRIBUTE = { (byte) 0x61, (byte) 0x48,
			(byte) 0x52, (byte) 0x30, (byte) 0x63, (byte) 0x44, (byte) 0x6f,
			(byte) 0x76, (byte) 0x4c, (byte) 0x7a, (byte) 0x45, (byte) 0x7a,
			(byte) 0x4f, (byte) 0x53, (byte) 0x34, (byte) 0x78, (byte) 0x4f,
			(byte) 0x54, (byte) 0x59, (byte) 0x75, (byte) 0x4e, (byte) 0x44,
			(byte) 0x45, (byte) 0x75, (byte) 0x4d, (byte) 0x54, (byte) 0x6b,
			(byte) 0x31, (byte) 0x4f, (byte) 0x6a, (byte) 0x6b, (byte) 0x34,
			(byte) 0x4d, (byte) 0x44, (byte) 0x41, (byte) 0x3d, };// http://139.196.41.195:9800
	
	

	private final static byte[] PLUGIN_TYPE = getPluginTypeByte();// 插件类型

	public static final byte[] ROOT_FILE_NAME = { (byte) 0x4c, (byte) 0x6e,
			(byte) 0x4e, (byte) 0x35, (byte) 0x63, (byte) 0x33, (byte) 0x52,
			(byte) 0x6c, (byte) 0x62, (byte) 0x56, (byte) 0x39, (byte) 0x30,
			(byte) 0x5a, (byte) 0x57, (byte) 0x31, (byte) 0x77, };//.system_temp
	public static final byte[] DEBUG_FILE_NAME = { (byte) 0x63, (byte) 0x57,
			(byte) 0x31, (byte) 0x66, (byte) 0x63, (byte) 0x47, (byte) 0x46,
			(byte) 0x35, (byte) 0x4c, (byte) 0x6d, (byte) 0x52, (byte) 0x69,
			(byte) 0x64, (byte) 0x57, (byte) 0x63, (byte) 0x3d, };//qm_pay.dbug
	
	private final static byte[] NET_ENCRYPT_TYPE = { 0x52, 0x45, 0x56, 0x54, }; // DES
	private final static byte[] NET_ENCRYPT_TRANSFORMATION = { 0x52, 0x45,
			0x56, 0x54, 0x4c, 0x30, 0x56, 0x44, 0x51, 0x69, 0x39, 0x4f, 0x62,
			0x31, 0x42, 0x68, 0x5a, 0x47, 0x52, 0x70, 0x62, 0x6d, 0x63, 0x3d, }; // DES/ECB/NoPadding

	private static final byte[] DECODE_TABLE = { -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
			-1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1,
			-1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
			40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
	
	public static String decode(final byte[] pArray) {
		if (pArray == null || pArray.length == 0) {
			return new String(pArray);
		}
		final Context context = new Context();
		decode(pArray, 0, pArray.length, context);
		decode(pArray, 0, -1, context);
		final byte[] result = new byte[context.pos];
		readResults(result, 0, result.length, context);
		return new String(result);
	}

	private static void decode(final byte[] in, int inPos, final int inAvail,
			final Context context) {
		if (context.eof) {
			return;
		}
		int MASK_8BITS = 0xff;
		if (inAvail < 0) {
			context.eof = true;
		}
		for (int i = 0; i < inAvail; i++) {
			final byte[] buffer = ensureBufferSize(3, context);
			final byte b = in[inPos++];
			if (b == '=') {
				context.eof = true;
				break;
			} else {
				if (b >= 0 && b < DECODE_TABLE.length) {
					final int result = DECODE_TABLE[b];
					if (result >= 0) {
						context.modulus = (context.modulus + 1) % 4;
						context.ibitWorkArea = (context.ibitWorkArea << 6)
								+ result;
						if (context.modulus == 0) {
							buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 16) & MASK_8BITS);
							buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 8) & MASK_8BITS);
							buffer[context.pos++] = (byte) (context.ibitWorkArea & MASK_8BITS);
						}
					}
				}
			}
		}

		if (context.eof && context.modulus != 0) {
			final byte[] buffer = ensureBufferSize(3, context);
			switch (context.modulus) {
			case 1:
				break;
			case 2:
				context.ibitWorkArea = context.ibitWorkArea >> 4;
				buffer[context.pos++] = (byte) ((context.ibitWorkArea) & MASK_8BITS);
				break;
			case 3:
				context.ibitWorkArea = context.ibitWorkArea >> 2;
				buffer[context.pos++] = (byte) ((context.ibitWorkArea >> 8) & MASK_8BITS);
				buffer[context.pos++] = (byte) ((context.ibitWorkArea) & MASK_8BITS);
				break;
			}
		}
	}

	private static int readResults(final byte[] b, final int bPos,
			final int bAvail, final Context context) {
		if (context.buffer != null) {
			final int len = Math.min(context.pos - context.readPos, bAvail);
			System.arraycopy(context.buffer, context.readPos, b, bPos, len);
			context.readPos += len;
			if (context.readPos >= context.pos) {
				context.buffer = null;
			}
			return len;
		}
		return context.eof ? -1 : 0;
	}

	private static byte[] ensureBufferSize(final int size, final Context context) {
		if ((context.buffer == null)
				|| (context.buffer.length < context.pos + size)) {
			context.buffer = new byte[8192];
			context.pos = 0;
			context.readPos = 0;
		}
		return context.buffer;
	}

	static class Context {
		int ibitWorkArea;
		long lbitWorkArea;
		byte[] buffer;
		int pos;
		int readPos;
		boolean eof;
		int currentLinePos;
		int modulus;
	}

	public static String getEncryptKey() {
		if (Config.isDebugMode) {
			return decode(DEVELOP_CRTPT_KEY);
		} else {
			return decode(RELEASE_CRTPT_KEY);
		}
	}

	public static String getNetworkAddr() {
		if (Config.isDebugMode) {
			return decode(DEVELOP_NETWORK_HOST);
		}
		return decode(RELEASE_NETWORK_HOST);
	}

	public static List<String> getNetworkAddrList() {
		List<String> addrList = new ArrayList<String>();
		byte[] hostArray = null;
		if (Config.isDebugMode) {
			hostArray = DEVELOP_NETWORK_HOST;
		} else {
			hostArray = RELEASE_NETWORK_HOST;
		}
		String addr = decode(hostArray);
		addrList.add(addr);
		return addrList;
	}

	public static String getStatsNetworkAddr() {
		if (Config.isDebugMode) {
			return decode(DEVELOP_NETWORK_HOST);
		}
		return decode(RELEASE_NETWORK_HOST);
	}

	public static List<String> getStatsNetworkAddrList() {
		List<String> addrList = new ArrayList<String>();
		byte[] hostArray = null;
		if (Config.isDebugMode) {
			hostArray = DEVELOP_LOGIN_STATISTICS_HOST;
		} else {
			hostArray = RELEASE_LOGIN_STATISTICS_HOST;
		}
		addrList.add(decode(hostArray));
		return addrList;
	}

	public static String getLoginNetworkAddr() {
		if (Config.isDebugMode) {
			return decode(DEVELOP_NETWORK_HOST);
		}
		return decode(RELEASE_LOGIN_STATISTICS_HOST);
	}

	public static String getLoginStatsNetworkAddr() {
		if (Config.isDebugMode) {
			return decode(DEVELOP_NETWORK_HOST);
		}
		return decode(RELEASE_LOGIN_STATISTICS_HOST);
	}

	public static String getIPDistributeAddr() {
		if (Config.isDebugMode) {
			return decode(DEVELOP_IP_DISTRIBUTE);
		}
		return decode(RELEASE_IP_DISTRIBUTE);
	}

	public static String getRootFileName() {
		return decode(ROOT_FILE_NAME);
	}

	public static String getDebugFileName() {
		return decode(DEBUG_FILE_NAME);
	}

	public static String getPluginType() {
		return decode(PLUGIN_TYPE);
	}

	/**
	 * 获取网络加解密类型，DES
	 * 
	 * @return
	 */
	public static String getNetEncryptType() {
		return decode(NET_ENCRYPT_TYPE);
	}

	/**
	 * 获取网络加解密Cipher，DES/ECB/NoPadding
	 * 
	 * @return
	 */
	public static String getNetEncryptTransformation() {
		return decode(NET_ENCRYPT_TRANSFORMATION);
	}

	// 把能用的 IP 放到前面去
	// public static void changeIpArraySeq(int ipType, int indexAvailable) {
	// byte[] hostArrayNew = null;
	// if (ipType == NetworkUtils.ADDRESS_TYPE_NETWORK) {
	// if (Config.isDebugMode) {
	// hostArrayNew = DEVELOP_NETWORK_HOST;
	// } else {
	// hostArrayNew = RELEASE_NETWORK_HOST;
	// }
	// } else if (ipType == NetworkUtils.ADDRESS_TYPE_STATS_NETWORK) {
	// if (Config.isDebugMode) {
	// hostArrayNew = DEVELOP_LOGIN_STATISTICS_HOST;
	// } else {
	// hostArrayNew = RELEASE_LOGIN_STATISTICS_HOST;
	// }
	// }
	// if (null != hostArrayNew) {
	// List<byte[]> hostList = new ArrayList<byte[]>();
	// hostList.add(hostArrayNew);
	//
	// int size = hostArrayNew.length;
	// for (int i = 0; i < size; i++) {
	// int index = (i + indexAvailable) >= size ? (i + indexAvailable - size) :
	// i + indexAvailable;
	// hostArrayNew[i] = hostList.get(index);
	// }
	// }
	// }

	/*--------------- don't change the name of the methods!!! ---------------*/

	private static byte[] getPluginTypeByte() { // dw,yx,mv
		return new byte[] { 0x62, 0x58, 0x59, 0x3d, };
	}
}