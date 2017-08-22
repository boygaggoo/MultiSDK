package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * @author wander
 * @data 2015-3-25
 */
@SignalCode(messageCode = 101010, encrypt = true)
public class GetPluginUpdateLogInfoReq {
	@ByteField(index = 0)
	private TerminalInfo terminalInfo;

	@ByteField(index = 1)
	private CpInfo cpInfo;

	@ByteField(index = 2)
	private LocationInfo locationInfo;

	@ByteField(index = 3)
	private String verOld;

	@ByteField(index = 4)
	private String verNew;

	@ByteField(index = 5)
	private int result;

	@ByteField(index = 6)
	private String updateType;

    @ByteField(index = 7)
    private String updateId;
    
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

	public String getVerOld() {
		return verOld;
	}

	public void setVerOld(String verOld) {
		this.verOld = verOld;
	}

	public String getVerNew() {
		return verNew;
	}

	public void setVerNew(String verNew) {
		this.verNew = verNew;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	@Override
	public String toString() {
		return "GetPluginUpdateLogInfoReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo="
				+ locationInfo + ", verOld=" + verOld + ", verNew=" + verNew + ", result=" + result + ", updateType="
				+ updateType + ", updateId=" + updateId + "]";
	}
}