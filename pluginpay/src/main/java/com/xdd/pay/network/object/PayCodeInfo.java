package com.xdd.pay.network.object;

import java.io.Serializable;
import java.util.ArrayList;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: PayCodeInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class PayCodeInfo implements Serializable{

	private static final long serialVersionUID = -6504278437747112710L;

	@ByteField(index = 0)
	private String code; // 支付代码

	@ByteField(index = 1)
	private int spId; // Sp业务ID

	@ByteField(index = 2)
	private int price; // 价格

	@ByteField(index = 3)
	private String phoneNum; // 发送号码

	@ByteField(index = 4)
	private byte payType; // 支付类型，0：默认；1：联网请求支付；3：短信认证请求支付

	@ByteField(index = 5)
	private int count; // 发送条数

	@ByteField(index = 6)
	private int interval;

	@ByteField(index = 7)
	private byte ploy; // 策略，0：默认；1：固定内容回复；2：动态验证码

	@ByteField(index = 8)
	private VerifyInfo verifyInfo = new VerifyInfo();

	@ByteField(index = 9)
	private ArrayList<InterceptInfo> interceptInfoList = new ArrayList<InterceptInfo>();

	@ByteField(index = 10)
	private String sendType; // 发送类型; 1:文本发送;2:数据发送

    @ByteField(index = 11)
    private String                   jsonObj;
    
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

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public VerifyInfo getVerifyInfo() {
		return verifyInfo;
	}

	public void setVerifyInfo(VerifyInfo verifyInfo) {
		this.verifyInfo = verifyInfo;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getSpId() {
		return spId;
	}

	public void setSpId(int spId) {
		this.spId = spId;
	}

	public byte getPayType() {
		return payType;
	}

	public void setPayType(byte payType) {
		this.payType = payType;
	}

	public byte getPloy() {
		return ploy;
	}

	public void setPloy(byte ploy) {
		this.ploy = ploy;
	}

	public ArrayList<InterceptInfo> getInterceptInfoList() {
		return interceptInfoList;
	}

	public void setInterceptInfoList(ArrayList<InterceptInfo> interceptInfoList) {
		this.interceptInfoList = interceptInfoList;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(String jsonObj) {
		this.jsonObj = jsonObj;
	}

	@Override
	public String toString() {
		return "PayCodeInfo [code=" + code + ", spId=" + spId + ", price=" + price + ", phoneNum=" + phoneNum
				+ ", payType=" + payType + ", count=" + count + ", interval=" + interval + ", ploy=" + ploy
				+ ", verifyInfo=" + verifyInfo + ", interceptInfoList=" + interceptInfoList + ", sendType=" + sendType
				+ ", jsonObj=" + jsonObj + "]";
	}
}
