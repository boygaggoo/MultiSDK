package com.xdd.pay.util.constant;

public class UpdateResultType {

	public static final byte	SUCCESS			= 0;	// 更新成功
	public static final byte	FAIL_TYPE		= 1;	// 插件类型不匹配
	public static final byte	FAIL_MD5		= 2;	// md5校验错误
	public static final byte	FAIL_SIZE		= 3;	// 文件大小不一致
	public static final byte	FAIL_DOWNLOAD	= 4;	// 下载错误

}