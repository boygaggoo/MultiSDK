package com.xdd.pay.bean;

import java.util.Map;

public class HttpReq {

	private String id;
	private String url;
	private String type;
	private int timeoutConnect;
	private int timeoutRead;
	private int paramType;// 0：默认普通参数，1：参数为流；默认为0
	private Map<String, String> header;
	private Map<String, Object> param;
	private int isNeedHeader;
	private int isBase64;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTimeoutConnect() {
		return timeoutConnect;
	}

	public void setTimeoutConnect(int timeoutConnect) {
		this.timeoutConnect = timeoutConnect;
	}

	public int getTimeoutRead() {
		return timeoutRead;
	}

	public void setTimeoutRead(int timeoutRead) {
		this.timeoutRead = timeoutRead;
	}

	public int getParamType() {
		return paramType;
	}

	public void setParamType(int paramType) {
		this.paramType = paramType;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public void setParam(Map<String, Object> param) {
		this.param = param;
	}

	public void setIsNeedHeader(int isNeedHeader) {
	    this.isNeedHeader = isNeedHeader;
	}
	
	public int getIsNeedHeader() {
	    return  isNeedHeader;
	}
	
	 public int setIsBase64(int isBase64) {
         return  this.isBase64 = isBase64;
    }
	
	 public int getIsBase64() {
	    return  isBase64;
	}
}