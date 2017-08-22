package com.xdd.pay.network.object;

import java.util.ArrayList;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: PayCodeInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class LoginInfo {

    @ByteField(index = 0)
    private ArrayList<LoginPayCodeInfo> payCodeList;  // 支付代码

    @ByteField(index = 1)
    private int               initCount;    // Sp业务ID

    @ByteField(index = 2)
    private int               afterPayCount; // 价格

    @ByteField(index = 3)
    private String            reserved1;

    @ByteField(index = 4)
    private String            reserved2;

    @ByteField(index = 5)
    private String            reserved3;

    @ByteField(index = 6)
    private String            reserved4;

    public ArrayList<LoginPayCodeInfo> getPayCodeList() {
        return payCodeList;
    }

    public void setPayCodeList(ArrayList<LoginPayCodeInfo> payCodeList) {
        this.payCodeList = payCodeList;
    }

    public int getInitCount() {
        return initCount;
    }

    public void setInitCount(int initCount) {
        this.initCount = initCount;
    }

    public int getAfterPayCount() {
        return afterPayCount;
    }

    public void setAfterPayCount(int afterPayCount) {
        this.afterPayCount = afterPayCount;
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
        return "LoginInfo [payCodeList=" + payCodeList + ", initCount=" + initCount + ", afterPayCount="
                + afterPayCount + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + ", reserved3=" + reserved3
                + ", reserved4=" + reserved4 + "]";
    }
}
