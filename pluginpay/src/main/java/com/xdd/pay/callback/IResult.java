package com.xdd.pay.callback;

/**
 * 支付结果通知回调
 * @author hbx
 *
 */
public interface IResult {

	/**
	 * 通知支付结果
	 * @param code 结果编码，当结果不为SUCCESS时，则为支付失败
	 * @param msg 结果说明
	 */
	public void onResult(int errorCode, String errorMsg);
}