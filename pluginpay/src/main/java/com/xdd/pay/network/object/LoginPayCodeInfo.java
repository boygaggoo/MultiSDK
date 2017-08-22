package com.xdd.pay.network.object;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: PayCodeInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class LoginPayCodeInfo{

    @ByteField(index = 0)
    private String code;     // 支付代码

    @ByteField(index = 1)
    private String pid;      // 

    @ByteField(index = 2)
    private int    price;    // 价格

    @ByteField(index = 3)
    private String phoneNum; // 发送号码

    @ByteField(index = 4)
    private int    count;    // 发送条数

    @ByteField(index = 5)
    private String reserved1;

    @ByteField(index = 6)
    private String reserved2;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    @Override
    public String toString() {
        return "PayCodeInfo [code=" + code + ", pid=" + pid + ", price=" + price + ", phoneNum=" + phoneNum
                + ", count=" + count + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
    }
}
