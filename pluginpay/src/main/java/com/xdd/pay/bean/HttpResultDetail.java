package com.xdd.pay.bean;

public class HttpResultDetail {

	private String id;
	private String resultCode;
	private String resultDetail;
	private String cookie;
	private String resultHttpHeader;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDetail() {
		return resultDetail;
	}

	public void setResultDetail(String resultDetail) {
		this.resultDetail = resultDetail;
	}
	
	public void setCookie(String cookie) {
	    this.cookie = cookie;
	}
	
	public String getCookie() {
	    return cookie;
	}
	
	public void setResultHttpHeader(String resultHttpHeader) {
        this.resultHttpHeader = resultHttpHeader;
    }

    public String getResultHttpHeader() {
        return resultHttpHeader;
    }
}