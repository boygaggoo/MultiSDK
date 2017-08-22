package com.xdd.pay.util.constant;

public class PaySDKType {
    public static final byte DEFAULT      = 0; // 自有支付
    public static final byte LYFPAY       = 1; // 乐易付
    public static final byte SKYPAY       = 2; // 斯凯支付
    
    public static final byte NETWORK_TYPE = 0; // 自有联网计费
    
    public static final int INIT0 = 0; // 正常初始化
    public static final int INIT1 = 4; // 正常初始化失败，延迟初始化
    public static final int PAYINTI0 = 5; // 点击pay init
    public static final int PAYINTI1 = 6; // 到达支付次数初始化
}
