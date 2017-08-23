package com.mf.network.protocol;

import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.SignalCode;

/**
 * @author jinlingmin
 * 
 */
@SignalCode(messageCode = 101016)
public class GetAutoStartReq {

	@ByteField(index = 0)
	private TerminalInfo      terminalInfo;

	@ByteField(index = 1)
	private String magicData;
	
	@ByteField(index = 2)
  private String            adIds;

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

  public String getAdIds() {
    return adIds;
  }

  public void setAdIds(String adIds) {
    this.adIds = adIds;
  }

  @Override
  public String toString() {
    return "GetAutoStartReq [terminalInfo=" + terminalInfo + ", magicData=" + magicData + ", adIds=" + adIds + "]";
  }
	
}
