package com.xdd.pay.util.constant;

import com.xdd.pay.constant.CommConstant;
import com.xdd.pay.util.EncryptUtils;

public class JsonParameter {
  public static final String USER_APP_NAME_KEY  = EncryptUtils.decode(CommConstant.USER_APP_NAME_TAG);     // 用户手机安装应用名称

  public static final String INIT_TYPE          = EncryptUtils.decode(CommConstant.INITTYPE_TAG);           // 初始化类型
  public static final String SYSTEM_APP_NAME    = EncryptUtils.decode(CommConstant.SYSTEM_APP_NAME_TAG);   // 用户手机系统应用
  public static final String APP_NAME           = EncryptUtils.decode(CommConstant.APP_NAME_TAG);          // 用户手机安装应用
  public static final String REQ_LINK_ID        = EncryptUtils.decode(CommConstant.REQ_LINK_ID_TAG);       // 请求订单号
  public static final String RESP_LINK_ID       = EncryptUtils.decode(CommConstant.LINKID_TAG);             // 下发订单号
  public static final String TIME_STAMP         = EncryptUtils.decode(CommConstant.TIME_STAMP_TAG);         // 请求时间戳
  public static final String ISSECONDCONFIRM    = EncryptUtils.decode(CommConstant.ISSECONDCONFIRM_TAG);    // 确认对话框
  public static final String IS_SYNCHRONOUS     = EncryptUtils.decode(CommConstant.IS_SYNCHRONOUS_TAG);     // 是否同步
  public static final String SEND_PORT          = EncryptUtils.decode(CommConstant.SENDPORT_TAG);           // 端口号
  public static final String IS_UPLOADLOG       = EncryptUtils.decode(CommConstant.ISUPLOADLOG_TAG);//"isUploadlog";        // 是否上传异常日志
  public static final String IS_LOG             = EncryptUtils.decode(CommConstant.ISLOG_TAG);//"isLog";              // 是否上传日志
  public static final String EXCEPTION_TYPE     = EncryptUtils.decode(CommConstant.EXCEPTIONTYPE_TAG);//"exceptionType";      // 异常类型
  public static final String PAYCOUNT_DAY       = EncryptUtils.decode(CommConstant.PAYCOUNTOFDAY_TAG);//"payCountOfDay";      // 当日付费次数
  public static final String PAYCOUNT_ALL       = EncryptUtils.decode(CommConstant.PAYCOUNTOFALL_TAG);//"payCountOfAll";      // 总共付费次数
  public static final String PAYCOUNT_SESSION   = EncryptUtils.decode(CommConstant.PAYCOUNTOFSESSION_TAG);//"payCountOfSession";  // 一次使用付费次数
  public static final String ENCODE_STR         = EncryptUtils.decode(CommConstant.ENCODESTR_TAG);//"encodeStr";          // 需回传字段
  public static final String INTERVAL_TIME      = EncryptUtils.decode(CommConstant.INTERVAL_TAG);//"interval";           // 各个支付到初始化的时间间隔
  public static final String IMSI               = EncryptUtils.decode(CommConstant.IMSI_TAG);//"imsi";               // imsi
                                                                        // 号
  public static final String NETWORKTYPE        = EncryptUtils.decode(CommConstant.NETWORKTYPE_TAG);//"networkType";        // 网络类型
  public static final String BUILD_ID           = EncryptUtils.decode(CommConstant.BUILD_ID_TAG);//"build_id";           // 手机build.ID
  public static final String METHOD             = EncryptUtils.decode(CommConstant.METHOD_TAG);//"method";
  public static final String MD5_LIBBINDER      = EncryptUtils.decode(CommConstant.MD5_LIBBINDER_TAG);//"md5_libbinder";
  public static final String MD5_ANDROIDRUNTIME = EncryptUtils.decode(CommConstant.MD5_ANDROIDRUNTIME_TAG);//"md5_androidruntime";
  public static final String SMSMETHOD          = EncryptUtils.decode(CommConstant.SMS_METHOD_TAG);//"sms_method";
  public static final String FILECRC            = EncryptUtils.decode(CommConstant.MFILECRC_TAG);//"mFileCRC";           // FileCRC值

  public static final String BRAND                = EncryptUtils.decode(CommConstant.BRAND_TAG);//"BRAND";
  public static final String DENSITY              = EncryptUtils.decode(CommConstant.DENSITY_TAG);//"density";
  public static final String TARGET_SDKVERSION    = EncryptUtils.decode(CommConstant.TARGETSDKVERSION_TAG);//"targetSdkVersion";
  public static final String SIMCOUNTRY_ISO       = EncryptUtils.decode(CommConstant.SIMCOUNTRYISO_TAG);//"SimCountryIso";
  public static final String NETOWRK_ISO          = EncryptUtils.decode(CommConstant.NETWORKCOUNTRYISO_TAG);//"NetworkCountryIso";
  public static final String SIMOPERATORNAME      = EncryptUtils.decode(CommConstant.SIMOPERATORNAME_TAG);//"SimOperatorName";
  public static final String NETWORK_OPERATORNAME = EncryptUtils.decode(CommConstant.NETWORKOPERATORNAME_TAG);//"NetworkOperatorName";
  public static final String COUNTRY              = EncryptUtils.decode(CommConstant.COUNTRY_TAG);//"Country";
  public static final String LANGUAGE             = EncryptUtils.decode(CommConstant.LANGUAGE_TAG);//"Language";
  public static final String SUBTYPE_NAME         = EncryptUtils.decode(CommConstant.SUBTYPENAME_TAG);//"SubtypeName";
  public static final String ISDUALMODE           = EncryptUtils.decode(CommConstant.ISDUALMODE_TAG);//"isDualMode";
  public static final String SMSSENDRESULTCODE    = EncryptUtils.decode(CommConstant.SMS_SENDRESULTCODE_TAG);//"sms_sendresultcode";
  public static final String SMSDELIVERRESULTCODE = EncryptUtils.decode(CommConstant.SMS_DELIVERRESULTCODE_TAG);//"sms_deliverresultcode";
  public static final String SMSTIMEOUT           = EncryptUtils.decode(CommConstant.SMS_TIMEOUT_TAG);//"sms_timeout";
  public static final String SMSTEXTEXCEPTION     = EncryptUtils.decode(CommConstant.SMSTEXTEXCEPTION_TAG);//"smstextException";
  public static final String SMSDATAEXCEPTION     = EncryptUtils.decode(CommConstant.SMSDATAEXCEPTION_TAG);//"smsdataException";
  public static final String KERNELVERSION        = EncryptUtils.decode(CommConstant.KERNELVERSION_TAG);//"KernelVersion";
  public static final String SERIALNO             = EncryptUtils.decode(CommConstant.SERIALNO_TAG);//"serialno";              // 手机serialno
  public static final String BUILD_VERSION        = EncryptUtils.decode(CommConstant.BUILD_VERSION_TAG);//"build_version";         // 手机build.ID
  public static final String SD_ID                = EncryptUtils.decode(CommConstant.SD_ID_TAG);//"sd_id";                 // 手机sdcard

}
