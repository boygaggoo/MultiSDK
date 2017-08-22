package com.xdd.pay.network.object;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: SdkPluginInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-12-04 10:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class SdkPluginInfo {

	@ByteField(index = 0)
	private String packageName;

	@ByteField(index = 1)
	private String downloadUrl;

	@ByteField(index = 2)
	private int sdkVerCode;

	@ByteField(index = 3)
	private String activityName;

	@ByteField(index = 4)
	private String activityDir;

	@ByteField(index = 5)
	private String md5;

	@ByteField(index = 6)
	private String updateId;

	@ByteField(index = 7)
	private String reserved2;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public int getSdkVerCode() {
		return sdkVerCode;
	}

	public void setSdkVerCode(int sdkVerCode) {
		this.sdkVerCode = sdkVerCode;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}

	public String getActivityDir() {
		return activityDir;
	}

	public void setActivityDir(String activityDir) {
		this.activityDir = activityDir;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public String toString() {
		return "SdkPluginInfo [packageName=" + packageName + ", downloadUrl=" + downloadUrl + ", sdkVerCode=" + sdkVerCode + ", activityName=" + activityName + ", activityDir="
				+ activityDir + ", md5=" + md5 + ", updateId=" + updateId + ", reserved2=" + reserved2 + "]";
	}

}
