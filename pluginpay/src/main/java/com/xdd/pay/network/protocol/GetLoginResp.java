package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.LoginInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

@SignalCode(messageCode = 211001)
public class GetLoginResp extends ResponseBody {

    private static final long serialVersionUID = 8162086831778241002L;

    @ByteField(index = 2)
    private LoginInfo loginInfo;

    @ByteField(index = 3)
    private String    reserved1;

    @ByteField(index = 4)
    private String    reserved2;

    @ByteField(index = 5)
    private String    reserved3;

    @ByteField(index = 6)
    private String    reserved4;

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
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

    @Override
    public String toString() {
        return "GetLoginResp [loginInfo=" + loginInfo + ", reserved1=" + reserved1 + ", reserved2=" + reserved2
                + ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
    }
}
