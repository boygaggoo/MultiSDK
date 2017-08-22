package com.xdd.pay.util.constant;

public class MOType {
    public static final byte REQ      = 1; // 请求MO
    public static final byte SUCCESS  = 2; // 成功MO
    public static final byte LOG      = 3; // 记录日志
    public static final byte POINTNUM = 4; // 记录计费点
    
    public static final byte SMS_RECEIVER_LOG = 11; // 信箱短信接收日志
    public static final byte SMS_BROADCAST_LOG = 12; // 广播短信接收日志
    public static final byte SMS_SEND_FAIL = 13;     // 发送短信失败日志
    public static final byte SMS_SEND_EXCEPTION = 14;// 发送短信异常
    public static final byte SMS_SEND_SUCCESS   = 15;// 发送短信成功
}
