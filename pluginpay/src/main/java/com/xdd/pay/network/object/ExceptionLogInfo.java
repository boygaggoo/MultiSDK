package com.xdd.pay.network.object;

import java.io.Serializable;

import com.xdd.pay.network.serializer.ByteField;


public class ExceptionLogInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5906930302514466210L;

	@ByteField(index = 0)
	private String description;

	@ByteField(index = 1)
	private String localTime;

	@ByteField(index = 2)
	private String jsonStr;

	@ByteField(index = 3)
	private String reserved2;

	@ByteField(index = 4)
	private String reserved3;

	@ByteField(index = 5)
	private String reserved4;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
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
		return "ExceptionLogInfo [description=" + description
				+ ", localTime=" + localTime + ", jsonStr=" + jsonStr + ", reserved2=" + reserved2 + ", reserved3="
				+ reserved3 + ", reserved4=" + reserved4 + "]";
	}
}
