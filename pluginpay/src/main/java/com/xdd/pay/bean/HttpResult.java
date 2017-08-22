package com.xdd.pay.bean;

import java.util.List;

public class HttpResult {

	private int type; // 1：表示请求，2：表示结果
	private String id;
	private String resultCode;
	private Integer busType;// busType==0(留存) 1、强联网第一步；2、强联网第二步；3、强联网第三步；4、强联网第四步；
	private List<HttpResultDetail> resultList;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Integer getBusType() {
		return busType;
	}

	public void setBusType(Integer busType) {
		this.busType = busType;
	}

	public List<HttpResultDetail> getResultList() {
		return resultList;
	}

	public void setResultList(List<HttpResultDetail> resultList) {
		this.resultList = resultList;
	}

}