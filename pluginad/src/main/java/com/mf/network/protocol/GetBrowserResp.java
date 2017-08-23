package com.mf.network.protocol;

import java.util.ArrayList;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.model.UrlInfoBto;

@SignalCode(messageCode = 201015,encrypt = false)
public class GetBrowserResp extends MFCom_ResponseBody{

	private static final long serialVersionUID = 1L;

	@ByteField(index = 2)
	private ArrayList<UrlInfoBto> urlList = new ArrayList<UrlInfoBto>();

	@ByteField(index = 3)
	private String magicData;
	
	@ByteField(index = 4)
	private ArrayList<String> whitePackages = new ArrayList<String>();
	
	@ByteField(index = 5)
	private ArrayList<String> blackPackages = new ArrayList<String>();

	public ArrayList<UrlInfoBto> getUrlList() {
		return urlList;
	}

	public void setUrlList(ArrayList<UrlInfoBto> urlList) {
		this.urlList = urlList;
	}

	public String getMagicData() {
		return magicData;
	}

	public void setMagicData(String magicData) {
		this.magicData = magicData;
	}

	public ArrayList<String> getWhitePackages() {
		return whitePackages;
	}

	public void setWhitePackages(ArrayList<String> whitePackages) {
		this.whitePackages = whitePackages;
	}

	public ArrayList<String> getBlackPackages() {
		return blackPackages;
	}

	public void setBlackPackages(ArrayList<String> blackPackages) {
		this.blackPackages = blackPackages;
	}

	@Override
	public String toString() {
		return "GetBrowserResp [urlList=" + urlList + ", magicData="
				+ magicData + ", whitePackages=" + whitePackages
				+ ", blackPackages=" + blackPackages + "]";
	}
	
}
