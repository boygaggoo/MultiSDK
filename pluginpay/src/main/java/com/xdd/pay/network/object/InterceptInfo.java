package com.xdd.pay.network.object;

import java.io.Serializable;

import com.xdd.pay.network.serializer.ByteField;

public class InterceptInfo implements Serializable {
	private static final long serialVersionUID = -1379909623479173758L;

	@ByteField(index = 0)
	private String message;

	@ByteField(index = 1)
	private String phoneNum;

	@ByteField(index = 2)
	private String resvered1;

	@ByteField(index = 3)
	private String resvered2;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getResvered1() {
		return resvered1;
	}

	public void setResvered1(String resvered1) {
		this.resvered1 = resvered1;
	}

	public String getResvered2() {
		return resvered2;
	}

	public void setResvered2(String resvered2) {
		this.resvered2 = resvered2;
	}

	@Override
	public String toString() {
		return "InterceptInfo [message=" + message + ", phoneNum=" + phoneNum + ", resvered1=" + resvered1
				+ ", resvered2=" + resvered2 + "]";
	}

}
