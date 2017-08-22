package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

@SignalCode(messageCode = 101015)
public class ThirdSdkPluginReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private String soInfo;
	
	@ByteField(index = 4)
    private String type;

	@ByteField(index = 5)
	private String reserved1;

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

	public String getSoInfo() {
		return soInfo;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
        return type;
    }

    public void setSoInfo(String soInfo) {
        this.soInfo = soInfo;
    }
	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	@Override
	public String toString() {
		return "ThirdSdkPluginReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo=" + locationInfo + ", soInfo=" + soInfo + ", reserved1=" + reserved1 + "]";
	}

}