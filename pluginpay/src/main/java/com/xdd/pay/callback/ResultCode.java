package com.xdd.pay.callback;

public class ResultCode {
	public static final int PAY_SUCCESS = 0; // 支付成功
	public static final int PAY_FAIL = 1000; // 支付失败
	public static final int POINT_NUM_INVALID = 1001; // 计费点无效
	public static final int POINT_NUM_IS_NULL = 1002; // 计费点为空
	public static final int IS_PAYING = 1003; // 支付中
	public static final int NO_SIM = 1004; // 无sim卡
	public static final int ACTIVITY_INVALID = 1005; // 当前界面实例无效
	public static final int PAY_CANCEL = 1006; // 用户取消支付
	public static final int CONFIRM_DIALOG_ERROR = 1007; // 二次确认框错误
	public static final int PRICE_INVALID = 1008; // 计费金额无效
	public static final int PAY_TIME_OUT = 1009; // 支付超时
	public static final int NET_WORK_PAY_FAIL = 1010; // 联网计费失败
	public static final int PAY_INFO_INVALID = 1011; // 计费代码无通道
	public static final int INIT_FAIL = 1012; // 初始化失败
	public static final int USER_REFUSE_PAY = 1013; // 用户拒绝支付
	public static final int CLICK_TOO_QUICK = 1014; // 点击太快
	public static final int INIT_SUCCESS = 1015; //初始化成功
	public static final int INIT_EXCEPTION = 1016; //初始化异常
	public static final int PLEASE_DO_INIT = 1017; //请进行初始化
}
