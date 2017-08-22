package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.SmsLogInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetSmsReceiverLogInfoReq.java<br>
 * 作者: hbx <br>
 * 创建时间：2015-7-25 14:28:43<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 101013, encrypt = true)
public class GetSmsReceiverLogInfoReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private SmsLogInfo smsLogInfo;

	@ByteField(index = 4)
	private String clientLinkId;
	
	@ByteField(index = 5)
	private String reserved1;

	@ByteField(index = 6)
	private String reserved2;
	
	@ByteField(index = 7)
	private String reserved3;

	@ByteField(index = 8)
	private String reserved4;

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

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
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

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}


	public SmsLogInfo getSmsLogInfo() {
		return smsLogInfo;
	}

	public void setSmsLogInfo(SmsLogInfo smsLogInfo) {
		this.smsLogInfo = smsLogInfo;
	}

	public String getClientLinkId() {
		return clientLinkId;
	}

	public void setClientLinkId(String clientLinkId) {
		this.clientLinkId = clientLinkId;
	}
	
}
