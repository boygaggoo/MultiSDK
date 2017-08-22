package com.xdd.pay.network.object;

import java.io.Serializable;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: LogInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class LogInfo implements Serializable{

	private static final long serialVersionUID = -36369039878026246L;

	@ByteField(index = 0)
    private byte   result;   // 结果,0：成功；其他失败

    @ByteField(index = 1)
    private byte   type;     // 1请求MO； 2成功MO

    @ByteField(index = 2)
    private byte   sdkType;  // 选择SDK类型，0：自有SDK；1：乐易付；2：斯凯

    @ByteField(index = 3)
    private byte   payType;  // 支付类型，0：默认；1：联网请求支付；2：短信认证请求支付

    @ByteField(index = 4)
    private String payCode;

    @ByteField(index = 5)
    private String payId;    // payType=0时为空 联网计费,短信认证时事获取的计费代码

    @ByteField(index = 6)
    private int    price;

    @ByteField(index = 7)
    private String localTime;

    @ByteField(index = 8)
    private String pointNum;

    @ByteField(index = 9)
    private String jsonObj;
    
    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
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

    public byte getPayType() {
        return payType;
    }

    public void setPayType(byte payType) {
        this.payType = payType;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
    }

	public String getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(String jsonObj) {
		this.jsonObj = jsonObj;
	}

	@Override
	public String toString() {
		return "LogInfo [result=" + result + ", type=" + type + ", sdkType=" + sdkType + ", payType=" + payType
				+ ", payCode=" + payCode + ", payId=" + payId + ", price=" + price + ", localTime=" + localTime
				+ ", pointNum=" + pointNum + ", jsonObj=" + jsonObj + "]";
	}

}
