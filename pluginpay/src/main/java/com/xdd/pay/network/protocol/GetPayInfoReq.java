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
@SignalCode(messageCode = 101005)
public class GetPayInfoReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private String pointNum;

	@ByteField(index = 4)
	private int price;

	@ByteField(index = 5)
	private String jsonStr;

	@ByteField(index = 6)
	private String isSkyPay;

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

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public String getReserved2() {
		return isSkyPay;
	}

	public void setReserved2(String reserved2) {
		this.isSkyPay = reserved2;
	}

	public String getPointNum() {
		return pointNum;
	}

	public void setPointNum(String pointNum) {
		this.pointNum = pointNum;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "GetPayInfoReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo=" + locationInfo
				+ ", pointNum=" + pointNum + ", price=" + price + ", jsonStr=" + jsonStr + ", isSkyPay=" + isSkyPay
				+ "]";
	}
}
