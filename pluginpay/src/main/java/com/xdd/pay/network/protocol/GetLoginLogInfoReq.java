package com.xdd.pay.network.protocol;

import java.util.ArrayList;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.LoginLogInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

@SignalCode(messageCode = 111003, encrypt = true)
public class GetLoginLogInfoReq {

    @ByteField(index = 0)
    private TerminalInfo            terminalInfo;

    @ByteField(index = 1)
    private CpInfo                  cpInfo;

    @ByteField(index = 2)
    private LocationInfo            locationInfo;

    @ByteField(index = 3)
    private ArrayList<LoginLogInfo> logInfoList = new ArrayList<LoginLogInfo>();

    @ByteField(index = 4)
    private String timestamp;

    @ByteField(index = 5)
    private String                  reserved2;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public ArrayList<LoginLogInfo> getLogInfoList() {
        return logInfoList;
    }

    public void setLogInfoList(ArrayList<LoginLogInfo> logInfoList) {
        this.logInfoList = logInfoList;
    }

    @Override
    public String toString() {
        return "GetLoginLogInfoReq [terminalInfo=" + terminalInfo + ", cpInfo="
                + cpInfo + ", locationInfo=" + locationInfo + ", logInfoList="
                + logInfoList + ", timestamp=" + timestamp + ", reserved2="
                + reserved2 + "]";
    }

}
