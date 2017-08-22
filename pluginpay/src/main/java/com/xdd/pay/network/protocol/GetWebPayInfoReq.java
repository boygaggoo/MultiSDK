package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetWebPayInfoReq.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-8-28 17:19:43<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 101006)
public class GetWebPayInfoReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private int price;

	@ByteField(index = 4)
	private String appId;

	@ByteField(index = 5)
	private String reqType;

	@ByteField(index = 6)
	private String verifyCode;

	@ByteField(index = 7)
	private String jsonStr;

	@ByteField(index = 8)
	private String reserved1;

	@ByteField(index = 9)
	private String reserved2;

	public TerminalInfo getTerminalInfo() {
		return terminalInfo;
	}

	public void setTerminalInfo(TerminalInfo terminalInfo) {
		this.terminalInfo = terminalInfo;
	}

	public CpInfo getCpInfo() {
		return cpInfo;
	}

	public void setCpInfo(CpInfo cpInfo) {
		this.cpInfo = cpInfo;
	}

	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(LocationInfo locationInfo) {
		this.locationInfo = locationInfo;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	@Override
	public String toString() {
		return "GetWebPayInfoReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo="
				+ locationInfo + ", price=" + price + ", appId=" + appId + ", reqType=" + reqType + ", verifyCode="
				+ verifyCode + ", jsonStr=" + jsonStr + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
	}
}
