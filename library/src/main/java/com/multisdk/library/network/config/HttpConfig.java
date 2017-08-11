package com.multisdk.library.network.config;

public class HttpConfig {

  public static final String TAG = "network";
  // 协议版本号
  public static final byte PROTOCOL_VERSION = 1;
  // 协议请求代码
  public static final byte PROTOCOL_REQ = 1;
  // 协议应答代码
  public static final byte PROTOCOL_RESP = 2;
  // 协议包头固定长度
  public static final int PROTOCOL_HEAD_LENGTH = 28;
  // 连接重试次数
  public static final int CONNECTION_MAX_RETRY = 3;
  // 超时时间(豪秒)
  public static final int CONNECTION_TIMEOUT = 25 * 1000;
  // 超时时间(豪秒)
  public static final int READ_WRITE_TIMEOUT = 30 * 1000;
  // 消息缓冲
  public static final int CONNECTION_BUFFER_SIZE = 5 * 1024;

  //编解码 KEY
  public static final String MESSAGE_CODE_KEY = "message_code_key";
}
