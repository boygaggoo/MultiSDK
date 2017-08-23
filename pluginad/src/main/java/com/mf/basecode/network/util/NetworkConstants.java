package com.mf.basecode.network.util;

public class NetworkConstants {

  public static final String TAG                      = "MFNetwork";
  // 协议版本号
  public static final byte   PROTOCL_VERSION          = 2;
  // 协议请求代码
  public static final byte   PROTOCL_REQ              = 1;
  // 协议应答代码
  public static final byte   PROTOCL_RESP             = 2;
  // 协议包头固定长度
  public static final int    PROTOCOL_HEAD_LENGTH     = 28;
  // 连接重试次数
  public static final int    CONNECTION_MAX_RETRY     = 3;
  // 超时时间(豪秒)
  public static final int    CONNECTION_TIMEOUT       = 25 * 1000;
  // 超时时间(豪秒)
  public static final int    READWIRTE_TIMEOUT        = 30 * 1000;
  // 消息缓冲
  public static final int    CONNECTION_BUFFER_SIZE   = 5 * 1024;
  // 下载缓冲
  public static final int    DOWNLOAD_BUFFER_SIZE     = 10 * 1024;
  public static final int    HANDLER_NETWORK_SUCCESS  = 0;
  public static final int    HANDLER_NETWORK_FAIL     = 1;
  public static final int    HANDLER_DOWNLOAD_SUCCESS = 0;
  public static final int    HANDLER_DOWNLOAD_FAIL    = 1;
  public static final int    HANDLER_DOWNLOAD_STEP    = 2;
  public static final int    NERWORK_TYPE_FAIL        = 0;
  public static final int    NERWORK_TYPE_2G          = 1;
  public static final int    NERWORK_TYPE_3G          = 2;
  public static final int    NERWORK_TYPE_4G          = 5;
  public static final int    NERWORK_TYPE_WIFI        = 3;
  public static final int    NERWORK_TYPE_UNKNOWN     = 4;

}
