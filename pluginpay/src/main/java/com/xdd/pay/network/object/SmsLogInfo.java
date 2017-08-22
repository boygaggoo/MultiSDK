package com.xdd.pay.network.object;

import java.io.Serializable;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: LogInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2015-7-24 14:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class SmsLogInfo implements Serializable {

	private static final long serialVersionUID = 782355085707181598L;

	@ByteField(index = 0)
	private byte result; // 结果,0：成功；其他失败

	@ByteField(index = 1)
	private byte type; // 1广播接收； 2信箱读取

	@ByteField(index = 2)
	private String sms;

	@ByteField(index = 3)
	private String phoneNum;

	@ByteField(index = 4)
	private String localTime;

	@ByteField(index = 5)
	private String reserved1;

	@ByteField(index = 6)
	private String reserved2;

	@ByteField(index = 7)
	private String reserved3;

	@ByteField(index = 8)
	private String reserved4;

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}

	public String getReserved3() {
		return reserved3;
	}

	public void setReserved3(String reserved3) {
		this.reserved3 = reserved3;
	}

	public String getReserved4() {
		return reserved4;
	}

	public void setReserved4(String reserved4) {
		this.reserved4 = reserved4;
	}

	@Override
	public String toString() {
		return "SmsLogInfo [result=" + result + ", type=" + type + ", sms=" + sms + ", phoneNum=" + phoneNum
				+ ", localTime=" + localTime + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + ", reserved3="
				+ reserved3 + ", reserved4=" + reserved4 + "]";
	}
}
