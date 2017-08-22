package com.xdd.pay.network.object;

import com.xdd.pay.network.serializer.ByteField;

/**
 * @author wander
 * @data 2015-4-15
 */
public class SoPluginInfo {

	@ByteField(index = 0)
	private String url;

	@ByteField(index = 1)
	private String md5;

	@ByteField(index = 2)
	private String saveDir;

	@ByteField(index = 3)
	private String saveName;

	@ByteField(index = 4)
	private String updateId;

	@ByteField(index = 5)
	private String reserved2;

	public SoPluginInfo() {

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
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

	@Override
	public String toString() {
		return "SoPluginInfo [url=" + url + ", md5=" + md5 + ", saveDir=" + saveDir + ", saveName=" + saveName + ", updateId=" + updateId + ", reserved2=" + reserved2 + "]";
	}

}