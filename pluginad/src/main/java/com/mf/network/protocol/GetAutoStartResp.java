package com.mf.network.protocol;

import java.util.ArrayList;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.network.object.AppStartInfo;

@SignalCode(messageCode = 201016)
public class GetAutoStartResp extends MFCom_ResponseBody{

	@ByteField(index = 2)
	private ArrayList<AppStartInfo> appList = new ArrayList<AppStartInfo>();

	@ByteField(index = 3)
	private String rule;//11(前一个，解屏，，后一个：安装)
	
	@ByteField(index = 4)
  private int seconds;

	@ByteField(index = 4)
	private String magicData;

	public String getMagicData() {
		return magicData;
	}

	public void setMagicData(String magicData) {
		this.magicData = magicData;
	}

	public ArrayList<AppStartInfo> getAppList() {
		return appList;
	}

	public void setAppList(ArrayList<AppStartInfo> appList) {
		this.appList = appList;
	}

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public int getSeconds() {
    return seconds;
  }

  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }

  @Override
  public String toString() {
    return "GetAutoStartResp [appList=" + appList + ", rule=" + rule + ", seconds=" + seconds + ", magicData=" + magicData + "]";
  }
  
}
