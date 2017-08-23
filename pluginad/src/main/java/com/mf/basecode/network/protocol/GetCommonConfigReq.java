package com.mf.basecode.network.protocol;

import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 101001)
public class GetCommonConfigReq  {
  
	@ByteField(index = 0)
	private TerminalInfo terminalInfo = new TerminalInfo();

	@ByteField(index = 1)
	private String magicData; // 回传服务器数据（每次请求服务器会下发）
	
	@ByteField(index = 2)
	private byte isMajor;//1:主2:从

  public TerminalInfo getTerminalInfo() {
    return terminalInfo;
  }

  public void setTerminalInfo(TerminalInfo terminalInfo) {
    this.terminalInfo = terminalInfo;
  }

  public String getMagicData() {
    return magicData;
  }

  public void setMagicData(String magicData) {
    this.magicData = magicData;
  }

  public byte getIsMajor() {
    return isMajor;
  }

  public void setIsMajor(byte isMajor) {  
    this.isMajor = isMajor;
  }

  @Override
  public String toString() {
    return "GetCommonConfigReq [terminalInfo=" + terminalInfo + ", magicData=" + magicData + ", isMajor=" + isMajor + "]";
  }

}
