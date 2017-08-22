package com.xdd.pay.network.util;

public class NetworkConstants {
	// 协议版本号
	public static final byte PROTOCL_VERSION = 1;
	// 协议请求代码
	public static final byte PROTOCL_REQ = 1;
	// 协议应答代码
	public static final byte PROTOCL_RESP = 2;
	// 协议包头固定长度
	public static final int PROTOCOL_HEAD_LENGTH = 28;

	// 连接重试次数
	public static final int CONNECTION_MAX_RETRY = 2;
	// 超时时间(豪秒)
	public static final int CONNECTION_TIMEOUT = 25 * 1000;
	// 超时时间(豪秒)
	public static final int READWIRTE_TIMEOUT = 45 * 1000;
	// 消息缓冲
	public static final int CONNECTION_BUFFER_SIZE = 10 * 1024;

	public static final int NERWORK_TYPE_FAIL = 0;
	public static final int NERWORK_TYPE_2G = 1;
	public static final int NERWORK_TYPE_3G = 2;
	public static final int NERWORK_TYPE_WIFI = 3;
	public static final int NERWORK_TYPE_UNKNOWN = 4;
	public static final int NERWORK_TYPE_CMWAP = 5;
	public static final int NERWORK_TYPE_CMNET = 6;
	public static final int NERWORK_TYPE_UNIWAP = 7;
	public static final int NERWORK_TYPE_UNINET = 8;
	public static final int NERWORK_TYPE_CTIWAP = 9;
	public static final int NERWORK_TYPE_CTINET = 10;

	public static final int NETWORK_RESPONSE_SUCCESS = 0;
	public static final int NETWORK_RESPONSE_ERROR = 1;

	// WEB计费
	public static final int WEB_RESPONSE_SUCCESS = 0;
	public static final int WEB_RESPONSE_ERROR = 1;

	// 网游计费计费状态
	public static final String NET_GAME_ALREADY_LOGGED_IN = "0"; // 已登入
	public static final String NET_GAME_NOT_LOGGED_IN = "1"; // 未登入
}
