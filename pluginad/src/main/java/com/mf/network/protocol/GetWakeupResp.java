package com.mf.network.protocol;

import java.util.List;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.network.object.AdInfo;

@SignalCode(messageCode = 201002)
public class GetWakeupResp extends MFCom_ResponseBody {

  /**
   * 
   */
  private static final long serialVersionUID = -5959953814022983885L;

  @ByteField(index = 3)
  private List<AdInfo>      wakeupList;                              // 唤醒信息列表

  @ByteField(index = 4)
  private String            showRule;

  @ByteField(index = 5)
  private String            magicData;

  public List<AdInfo> getWakeupList() {
    return wakeupList;
  }

  public void setWakeupList(List<AdInfo> wakeupList) {
    this.wakeupList = wakeupList;
  }

  public String getShowRule() {
    return showRule;
  }

  public void setShowRule(String showRule) {
    this.showRule = showRule;
  }

  public String getMagicData() {
    return magicData;
  }

  public void setMagicData(String magicData) {
    this.magicData = magicData;
  }

  @Override
  public String toString() {
    return "GetWakeupResp [wakeupList=" + wakeupList + ", showRule=" + showRule + ", magicData=" + magicData + "]";
  }
  
}
