package com.xdd.pay.network.object;

import java.io.Serializable;
import java.util.ArrayList;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: PayInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class PayInfo implements Serializable{

	private static final long serialVersionUID = 6120651578371682886L;

	@ByteField(index = 0)
    private String                 pointNum;       // 计费点

    @ByteField(index = 1)
    private int                    price;          // 计费点价格

    @ByteField(index = 2)
    private String                 tip;

    @ByteField(index = 3)
    private byte                   type;           // 类型，0：默认；1：暗扣

    @ByteField(index = 4)
    private byte                   sdkType;        // 选择SDK类型，0：自有SDK；1：乐易付；2：斯凯

    @ByteField(index = 5)
    private ArrayList<PayCodeInfo> payCodeInfoList;

    @ByteField(index = 6)
    private String                 isNetworkType;

    @ByteField(index = 7)
    private String                 reserved2;

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getSdkType() {
        return sdkType;
    }

    public void setSdkType(byte sdkType) {
        this.sdkType = sdkType;
    }

    public ArrayList<PayCodeInfo> getPayCodeInfoList() {
        return payCodeInfoList;
    }

    public void setPayCodeInfoList(ArrayList<PayCodeInfo> payCodeInfoList) {
        this.payCodeInfoList = payCodeInfoList;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getIsNetworkType() {
        return isNetworkType;
    }

    public void setIsNetworkType(String isNetworkType) {
        this.isNetworkType = isNetworkType;
    }

    @Override
    public String toString() {
        return "PayInfo [pointNum=" + pointNum + ", price=" + price + ", tip=" + tip + ", type=" + type + ", sdkType="
                + sdkType + ", payCodeInfoList=" + payCodeInfoList + ", isNetworkType=" + isNetworkType
                + ", reserved2=" + reserved2 + "]";
    }

}
