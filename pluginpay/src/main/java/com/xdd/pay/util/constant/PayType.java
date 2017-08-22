package com.xdd.pay.util.constant;

public class PayType {
  public static final int    DEFAULT          = 0;              // 普通计费
  public static final int    NET              = 1;              // 联网请求支付
  public static final int    MSG_NET          = 4;              // 短信认证请求支付
  public static final int    NET_GAME         = 6;              // 网游计费
  public static final int    WEB              = 9;              // WEB计费
  public static final int    WAP              = 10;             // WAP计费
  public static final int    SENDTWO          = 11;             // 连续发送两条短信
  public static final int    NEWTYPE          = 13;             // 任务接口
  public static final int    CMCCPAY          = 14;             // cmcc计费接口
  
  //0922 普石强联网计费
  public static final int    PUSHI_PAY        = 15;             // 普石强联网sdk计费类型
  //1214 Sky计费
  public static final int    SKY_PAY          = 16;             // SkyPay计费类型

  // WEB计费请求类型
  public static final String WEB_LOGGED_IN    = "web_logged_in"; // WEB第一次登入
  public static final String WEB_REQ_PAY      = "web_req_pay";  // WEB登入后请求计费
  public static final String WEB_PAY_RESP     = "web_pay_resp"; // WEB计费结果

  // 联网请求
  public static final int    NET_WORK_DEFAULT = 0;              // 默认联网请求
  public static final int    NET_WORK_NOT_PAY = 1;              // 联网请求后不支付

}
