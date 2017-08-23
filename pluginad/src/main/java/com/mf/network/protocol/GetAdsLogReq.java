/**
 * 
 */
package com.mf.network.protocol;

import java.util.ArrayList;

import com.mf.basecode.network.object.AdLogInfo;
import com.mf.basecode.network.object.TerminalInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.SignalCode;

/**
 * @author jinlingmin
 * 
 */
@SignalCode(messageCode = 103001, encrypt = true)
public class GetAdsLogReq {

  @ByteField(index = 0)
  private TerminalInfo         termInfo      = new TerminalInfo();

  @ByteField(index = 2)
  private ArrayList<AdLogInfo> adLogInfoList = new ArrayList<AdLogInfo>();

  public TerminalInfo getTermInfo() {
    return termInfo;
  }

  public void setTermInfo(TerminalInfo termInfo) {
    this.termInfo = termInfo;
  }

  public ArrayList<AdLogInfo> getAdLogInfoList() {
    return adLogInfoList;
  }

  public void setAdLogInfoList(ArrayList<AdLogInfo> adLogInfoList) {
    this.adLogInfoList = adLogInfoList;
  }

  @Override
  public String toString() {
    return "GetAdsLogReq [termInfo=" + termInfo + ", adLogInfoList=" + adLogInfoList + "]";
  }
}
