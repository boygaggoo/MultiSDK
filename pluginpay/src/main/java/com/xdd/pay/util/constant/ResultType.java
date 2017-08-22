package com.xdd.pay.util.constant;

public class ResultType {
    public static final byte SUCCESS           = 0; // 成功
    public static final byte FAIL              = 1; // 失败
    public static final byte REFUSE            = 2; // 用户拒绝
    public static final byte IS_WHITE          = 3; // 白名单
    public static final byte IS_BLACK          = 4; // 黑名单
    public static final byte IS_INIT_FAIL      = 5; // 初始化失败
    public static final byte POINT_NUM_IS_NULL = 6; // 计费点为空
    public static final byte NO_SIM            = 7; // imsi为空
    public static final byte POINT_NUM_INVALID = 8; // 计费点无效
    public static final byte PAY_INFO_INVALID  = 9; // 无可用计费代码
    public static final byte PRICE_INVALID     = 10; // 计费金额无效
    public static final byte TIME_OUT0         = 11; // 计费超时,成功代码
    public static final byte TIME_OUT1         = 12; // 计费超时,失败代码
    public static final byte SECOND_REFUSE     = 13; // 用户二次确认拒绝
}
