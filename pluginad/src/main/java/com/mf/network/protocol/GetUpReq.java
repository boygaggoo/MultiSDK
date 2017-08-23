/**
 * 
 */
package com.mf.network.protocol;

import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 101012)
public class GetUpReq {

  @ByteField(index = 0)
  private TerminalInfo      terminalInfo;
  
  @ByteField(index = 1)
  private String            magicData;
  
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
  
  @Override
  public String toString() {
    return "GetUpReq [terminalInfo=" + terminalInfo + ", magicData=" + magicData + "]";
  }
}
