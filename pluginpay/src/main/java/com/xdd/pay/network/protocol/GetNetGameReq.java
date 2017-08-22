package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetPayInfoReq.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 17:28:43<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 101008)
public class GetNetGameReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private String payCode;

	@ByteField(index = 4)
	private String contentSid;

	@ByteField(index = 5)
	private String jsonStr;

	@ByteField(index = 6)
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

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getContentSid() {
		return contentSid;
	}

	public void setContentSid(String contentSid) {
		this.contentSid = contentSid;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	@Override
	public String toString() {
		return "GetNetGameReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo=" + locationInfo
				+ ", payCode=" + payCode + ", contentSid=" + contentSid + ", jsonStr=" + jsonStr + ", reserved2="
				+ reserved2 + "]";
	}

}
