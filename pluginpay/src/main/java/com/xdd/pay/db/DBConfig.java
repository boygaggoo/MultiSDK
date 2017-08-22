package com.xdd.pay.db;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.util.EncryptUtils;

/**
 * 文件名称: DBConstants.java<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class DBConfig {

	public static final String MAC = EncryptUtils.decode(CommConstant.MAC_TAG); // mac地址
	public static final String SMS_CENTER = EncryptUtils.decode(CommConstant.SMS_CENTER_TAG); // 短消息中心号
	public static final String FIRST_REQ_TIME = EncryptUtils.decode(CommConstant.FIRST_REQ_TIME_TAG); // 第一次请求开关时间，服务器的时间
	public static final String NEW_USER = EncryptUtils.decode(CommConstant.NEW_USER_TAG); // 是否为新增
	public static final String ROOT_SYMBOL = EncryptUtils.decode(CommConstant.ROOT_SYMBOL_TAG); // 手机root标志位，1：root；0：未root
	public static final String PAY_DATE = EncryptUtils.decode(CommConstant.PAY_DATE_TAG);// 支付时间，加密

	public static final String DOWNLOAD_PRE = "download_";// 下载文件key前缀
//	public static final String DOWNLOAD_JAR = DOWNLOAD_PRE + DownloadConstants.FILE_TYPE_JAR; // 自更新jar包名称
//	public static final String DOWNLOAD_SO = DOWNLOAD_PRE + DownloadConstants.FILE_TYPE_SO;// 自更新so文件名称

}
