package com.multisdk.library.network.protocol;

import com.multisdk.library.network.obj.TerminalInfo;
import com.multisdk.library.network.serializer.ByteField;
import com.multisdk.library.network.serializer.SignalCode;

@SignalCode(messageCode = 1)
public class GetCommConfigReq {

  @ByteField(index = 0)
  private TerminalInfo terminalInfo = new TerminalInfo();

  @ByteField(index = 1)
  private byte isMajor;//1:主2:从

  public TerminalInfo getTerminalInfo() {
    return terminalInfo;
  }

  public void setTerminalInfo(TerminalInfo terminalInfo) {
    this.terminalInfo = terminalInfo;
  }

  public byte getIsMajor() {
    return isMajor;
  }

  public void setIsMajor(byte isMajor) {
    this.isMajor = isMajor;
  }
}
