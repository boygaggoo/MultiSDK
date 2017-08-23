package com.mf.basecode.network.protocol;

import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 100001, encrypt = true)
public class GetZoneServerReq {

  @ByteField(index = 0)
  private TerminalInfo terminalInfo;
  
  @ByteField(index = 1)
  private int       source;

  public TerminalInfo getTerminalInfo() {
    return terminalInfo;
  }

  public void setTerminalInfo(TerminalInfo terminalInfo) {
    this.terminalInfo = terminalInfo;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  @Override
  public String toString() {
    return "GetZoneServerReq [terminalInfo=" + terminalInfo + ", source=" + source + "]";
  }

  
}
