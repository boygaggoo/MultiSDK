package com.xdd.pay.bean;

import java.util.List;

public class HttpProtocol {

	private int flowType;// flowType==0表示不中断，flowtype==1表示中断
	private int resultCode;// resultCode != 0 表示继续请求
	private int needDetailResultCode;// needDetailResultCode == 0 表示不需要详细结果，1表示需要详细信息
	private int busType;// busType==0(留存) 1、强联网第一步；2、强联网第二步；3、强联网第三步；4、强联网第四步；
	private List<HttpReq> list;

	public int getFlowType() {
		return flowType;
	}

	public void setFlowType(int flowType) {
		this.flowType = flowType;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getNeedDetailResultCode() {
		return needDetailResultCode;
	}

	public void setNeedDetailResultCode(int needDetailResultCode) {
		this.needDetailResultCode = needDetailResultCode;
	}

	public int getBusType() {
		return busType;
	}

	public void setBusType(int busType) {
		this.busType = busType;
	}

	public List<HttpReq> getList() {
		return list;
	}

	public void setList(List<HttpReq> list) {
		this.list = list;
	}

}