package com.mf.model;

import com.mf.basecode.network.serializer.ByteField;

public class UrlInfoBto {
	
	private int id;
	
	@ByteField(index = 0)
	private int resId;

	@ByteField(index = 1)
	private String packageName;

	@ByteField(index = 2)
	private String url;

	@ByteField(index = 3)
	private int times;
	
	private int showTimes;
	
	private int showFlag;

	public int getResId() {
		return resId;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
	
	public int getShowTimes() {
		return showTimes;
	}

	public void setShowTimes(int showTimes) {
		this.showTimes = showTimes;
	}

	public int getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(int showFlag) {
		this.showFlag = showFlag;
	}

	@Override
	public String toString() {
		return "UrlInfoBto [id=" + id + ", resId=" + resId + ", packageName="
				+ packageName + ", url=" + url + ", times=" + times
				+ ", showTimes=" + showTimes + ", showFlag=" + showFlag + "]";
	}
	
}
