package com.xdd.pay.util.constant;

public class ReplyPloy {
	public static final byte DEFAULT = 0; // 不回复
	public static final byte STATIC = 1; // 固定内容回复
	public static final byte DYNAMIC = 2; // 动态内容回复
	public static final byte WEB = 3; // WEB动态验证码
	public static final byte STATIC_SELF = 4; // 发件用户固定内容回复
	public static final byte NET_DEAL_WITH = 5; // 联网处理
	
	public static final byte PLOY_SEND_MESSAGE = 1;
	public static final byte PLOY_SEND_DATA = 2;
	public static final byte PLOY_HTTP_GET = 3;
	public static final byte PLOY_HTTP_POST = 4;
}
