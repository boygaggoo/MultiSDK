package com.mf.network.protocol;

import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 101007)
public class GetMagicReq {
  @ByteField(index = 0)
  private TerminalInfo      terminalInfo;
  @ByteField(index = 1)
  private String            magicData;
  @ByteField(index = 2)
  private String            adid;

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

  public String getAdid() {
    return adid;
  }

  public void setAdid(String adid) {
    this.adid = adid;
  }

  @Override
  public String toString() {
    return "GetMagicReq [terminalInfo=" + terminalInfo + ", magicData=" + magicData + ", adid=" + adid + "]";
  }
  
}
