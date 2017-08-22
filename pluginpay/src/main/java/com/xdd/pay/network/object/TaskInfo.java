package com.xdd.pay.network.object;

import java.io.Serializable;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: TaskInfo.java<br>
 * 作者:  gw<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class TaskInfo implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1721673846933893098L;

	@ByteField(index = 0)
	private int taskType; //任务类型，1 发短信任务  2 http任务
	
	@ByteField(index = 1)
	private int sendType; // 短信发送类型; 1:文本发送;2:数据发送
	
	@ByteField(index = 2)
	private String sms; // 发送内容

	@ByteField(index = 3)
	private String phoneNum; // 发送号码
	
	@ByteField(index = 4)
	private int count; //连续发送短信发送次数

	@ByteField(index = 5)
	private int httpType; // 1:get;2:post
	
	@ByteField(index = 6)
	private int timeout;// http timeout时间设置
	
	@ByteField(index = 7)
	private String httpUrl; // 请求url地址
	
	@ByteField(index = 8)
	private String dataStr; //  需要decode之后使用
	
	@ByteField(index = 9)
	private String header; // 请求http头

	@ByteField(index = 10)
	private int dealStep;//session id 客户端第一次任务请求发送0，服务端返回1，客户端下次请求回传该字段，服务端下次返回值+1。
	
	@ByteField(index = 11)
	private int returnFlag;//1表示返回任务结果 ,2表示不需要返回任务结果
	
	@ByteField(index = 12)
	private int isBase64; //是否需要base decode; 1:需要,2:不需要

	@ByteField(index = 13)
	private int dealState; // 处理状态  200:成功,400:失败
	
	@ByteField(index = 14)
	private String respStr; //请求返回内容
	
	@ByteField(index = 15)
	private String paramJson; //固定返回参数，预留字段

	@ByteField(index = 16)
	private int intervalTime; //任务处理间隔时间 s
	
	@ByteField(index = 17)
	private int isIgnore; //1:忽略——任务失败时，下一个任务还执行,2:不忽略 ——任务失败之后，下面的任务不再执行
	
	@ByteField(index = 18)
	private String sync;//1表示同步，2表示异步
	
	@ByteField(index = 19)
	private String reserved1;
	
	@ByteField(index = 20)
	private String reserved2;
	
	@ByteField(index = 21)
	private String reserved3;
	
	@ByteField(index = 22)
	private String reserved4;
	
	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public String getDataStr() {
		return dataStr;
	}

	public void setDataStr(String dataStr) {
		this.dataStr = dataStr;
	}

	public int getDealState() {
		return dealState;
	}

	public int getSendType() {
		return sendType;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
	}

	public void setDealState(int dealState) {
		this.dealState = dealState;
	}

	public void setDealStep(int dealStep) {
		this.dealStep = dealStep;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getReserved4() {
		return reserved4;
	}

	public void setReserved4(String reserved4) {
		this.reserved4 = reserved4;
	}

	public int getDealStep() {
		return dealStep;
	}

	public int getHttpType() {
		return httpType;
	}

	public void setHttpType(int httpType) {
		this.httpType = httpType;
	}

	public int getIsBase64() {
		return isBase64;
	}

	public void setIsBase64(int isBase64) {
		this.isBase64 = isBase64;
	}

	public String getRespStr() {
		return respStr;
	}

	public void setRespStr(String respStr) {
		this.respStr = respStr;
	}

	public String getParamJson() {
		return paramJson;
	}

	public void setParamJson(String paramJson) {
		this.paramJson = paramJson;
	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public int getIsIgnore() {
		return isIgnore;
	}

	public void setIsIgnore(int isIgnore) {
		this.isIgnore = isIgnore;
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getReturnFlag() {
		return returnFlag;
	}

	public void setReturnFlag(int returnFlag) {
		this.returnFlag = returnFlag;
	}

	public String getSync() {
		return sync;
	}

	public void setSync(String sync) {
		this.sync = sync;
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

	@Override
	public String toString() {
		return "TaskInfo [sms=" + sms + ", phoneNum=" + phoneNum + ", httpUrl=" + httpUrl + ", dataStr=" + dataStr
				+ ", dealState=" + dealState + ", sendType=" + sendType + ", dealStep=" + dealStep + ", httpType="
				+ httpType + ", isBase64=" + isBase64 + ", respStr=" + respStr + ", paramJson=" + paramJson
				+ ", intervalTime=" + intervalTime + ", taskType=" + taskType + ", timeout=" + timeout + ", count="
				+ count + ", isIgnore=" + isIgnore+ ", returnFlag=" + returnFlag+ ", sync=" + sync+ ", header=" + header 
				+ ", reserved1=" + reserved1 + ", reserved2=" + reserved2
				+ ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
	}
}
