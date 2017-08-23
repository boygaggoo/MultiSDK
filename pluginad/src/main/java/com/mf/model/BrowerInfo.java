package com.mf.model;

import com.mf.basecode.network.serializer.ByteField;

public class BrowerInfo {

	@ByteField(index = 0)
	private String pkgName;
	
	@ByteField(index = 1)
	private String acticityName;
	
	@ByteField(index = 2)
	private int blackOrWhite;

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getActicityName() {
		return acticityName;
	}

	public void setActicityName(String acticityName) {
		this.acticityName = acticityName;
	}

	public int getBlackOrWhite() {
		return blackOrWhite;
	}

	public void setBlackOrWhite(int blackOrWhite) {
		this.blackOrWhite = blackOrWhite;
	}
	
}
