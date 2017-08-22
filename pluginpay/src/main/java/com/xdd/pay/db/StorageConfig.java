package com.xdd.pay.db;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.util.EncryptUtils;

public class StorageConfig {

	public static final String NAME = EncryptUtils.decode(CommConstant.UNKNOWN_TAG);// qy_pay
	
	public static final String VERIFYLIST_KEY = EncryptUtils.decode(CommConstant.VERIFYLIST_TAG);
	public static final String INTERCEPTLIST_KEY = EncryptUtils.decode(CommConstant.INTERCEPTLIST_TAG);
	public static final String PAYINFO_KEY = EncryptUtils.decode(CommConstant.PAYINFO_TAG_TAG);
	public static final String POINTNUM_KEY = EncryptUtils.decode(CommConstant.POINTNUM_TAG);
	public static final String PRICE_KEY = EncryptUtils.decode(CommConstant.PRICE_TAG);
	public static final String LOG_LIST_KEY = EncryptUtils.decode(CommConstant.LOGLIST_TAG);
	public static final String CLIENT_LINK_ID_KEY = EncryptUtils.decode(CommConstant.CLIENTLINKID_TAG);
	
	public static final String PAY_COUNT_ALL = EncryptUtils.decode(CommConstant.A_TAG); // 总支付次数
	public static final String PAY_COUNT_DAY = EncryptUtils.decode(CommConstant.B_TAG); // 一天内的支付次数
	public static final String PAY_COUNT_SESSION = EncryptUtils.decode(CommConstant.C_TAG); // 一天会话的支付次数
	public static final String[] PAY_COUNT_CONFUSE = { EncryptUtils.decode(CommConstant.AA_TAG), EncryptUtils.decode(CommConstant.BB_TAG), EncryptUtils.decode(CommConstant.CC_TAG) };
	public static final String UNCATCH_EXCEPTION = EncryptUtils.decode(CommConstant.UNCATCHEXCEPTION_TAG);
	public static final String PAY_DAY = EncryptUtils.decode(CommConstant.PAYDAY_TAG); // 支付时间

	public static final String SDK_VERSION_NAME = EncryptUtils.decode(CommConstant.SDK_VERSION_NAME_TAG);
	public static final String DOWNLOAD_JAR = EncryptUtils.decode(CommConstant.DOWNLOADJAR_TAG);
	public static final String DOWNLOAD_SO = EncryptUtils.decode(CommConstant.DOWNLOADSO_TAG);
	public static final String THIRDDOWNLOAD_JAR = EncryptUtils.decode(CommConstant.THIRDDOWNLOADJAR_TAG);
    public static final String THIRDDOWNLOAD_SO = EncryptUtils.decode(CommConstant.THIRDDOWNLOADSO_TAG);
    
    public static final String PAY_APP_KEY = EncryptUtils.decode(CommConstant.PAY_APP_KEY);
    public static final String PAY_CHANNEL_ID = EncryptUtils.decode(CommConstant.PAY_CHANNEL_ID);
}