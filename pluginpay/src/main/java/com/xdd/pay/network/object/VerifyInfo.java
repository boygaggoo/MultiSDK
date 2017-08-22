package com.xdd.pay.network.object;

import java.io.Serializable;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: VerifyInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class VerifyInfo implements Serializable {

	private static final long serialVersionUID = 3476123044142941398L;

	@ByteField(index = 0)
	private String code; // 验证码，如果策略为1，则为固定信息，为2则为动态验证码

	@ByteField(index = 1)
	private int length; // 验证码长度

	@ByteField(index = 2)
	private String match; // 动态验证码前几个匹配字符串

	@ByteField(index = 3)
	private String phoneNum; // 二次确认号码

	@ByteField(index = 4)
	private String reserved1;
	@ByteField(index = 5)
	private String reserved2;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
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

	@Override
	public String toString() {
		return "VerifyInfo [code=" + code + ", length=" + length + ", match=" + match + ", phoneNum=" + phoneNum
				+ ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
	}

}
