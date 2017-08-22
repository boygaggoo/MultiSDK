package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.ExceptionLogInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

@SignalCode(messageCode = 101014, encrypt = true)
public class GetExceptionLogInfoReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private ExceptionLogInfo exceptionLogInfo;
	
	@ByteField(index = 4)
	private String reserved1;

	@ByteField(index = 5)
	private String reserved2;
	
	@ByteField(index = 6)
	private String reserved3;

	@ByteField(index = 7)
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


	public ExceptionLogInfo getExceptionLogInfo() {
		return exceptionLogInfo;
	}

	public void setExceptionLogInfo(ExceptionLogInfo exceptionLogInfo) {
		this.exceptionLogInfo = exceptionLogInfo;
	}
}

