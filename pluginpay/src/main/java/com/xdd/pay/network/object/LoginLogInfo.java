package com.xdd.pay.network.object;

import com.xdd.pay.network.serializer.ByteField;

public class LoginLogInfo {

    @ByteField(index = 0)
    private byte   result;

    @ByteField(index = 1)
    private String payCode;

    @ByteField(index = 2)
    private String payId;    // payType=0时为空

    @ByteField(index = 3)
    private String dest;

    @ByteField(index = 4)
    private String localTime;

    @ByteField(index = 5)
    private String reserved1;

    @ByteField(index = 6)
    private String reserved2;

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    @Override
    public String toString() {
        return "LoginLogInfo [result=" + result + ", payCode=" + payCode + ", payId=" + payId + ", dest=" + dest
                + ", localTime=" + localTime + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
    }

}
