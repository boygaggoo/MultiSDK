package com.mf.network.protocol;

import java.util.ArrayList;

import com.mf.basecode.network.object.PgInfo;
import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 101002)
public class GetWakeupReq {
  @ByteField(index = 0)
  private TerminalInfo      terminalInfo;

  @ByteField(index = 1)
  private ArrayList<PgInfo> installList   = new ArrayList<PgInfo>();

  @ByteField(index = 2)
  private ArrayList<PgInfo> uninstallList = new ArrayList<PgInfo>();

  @ByteField(index = 3)
  private String            magicData;

  @ByteField(index = 4)
  private String            adIds;

  public TerminalInfo getTerminalInfo() {
    return terminalInfo;
  }

  public void setTerminalInfo(TerminalInfo terminalInfo) {
    this.terminalInfo = terminalInfo;
  }

  public ArrayList<PgInfo> getInstallList() {
    return installList;
  }

  public void setInstallList(ArrayList<PgInfo> installList) {
    this.installList = installList;
  }

  public ArrayList<PgInfo> getUninstallList() {
    return uninstallList;
  }

  public void setUninstallList(ArrayList<PgInfo> uninstallList) {
    this.uninstallList = uninstallList;
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
    return "GetWakeupReq [terminalInfo=" + terminalInfo + ", installList=" + installList + ", uninstallList=" + uninstallList + ", magicData=" + magicData
        + ", adIds=" + adIds + "]";
  }
  
  

}
