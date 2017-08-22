package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetInitReq.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 17:28:43<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 101002)
public class GetInitReq {
    @ByteField(index = 0)
    private TerminalInfo terminalInfo;

    @ByteField(index = 1)
    private CpInfo       cpInfo;

    @ByteField(index = 2)
    private LocationInfo locationInfo;

    @ByteField(index = 3)
    private String       userBehavior;

    @ByteField(index = 4)
    private String       jsonStr;

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

    public String getjsonStr() {
        return jsonStr;
    }

    public void setjsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

	public String getUserBehavior() {
		return userBehavior;
	}

	public void setUserBehavior(String userBehavior) {
		this.userBehavior = userBehavior;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	@Override
	public String toString() {
		return "GetInitReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo=" + locationInfo
				+ ", userBehavior=" + userBehavior + ", jsonStr=" + jsonStr + "]";
	}

}
