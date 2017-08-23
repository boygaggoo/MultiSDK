package com.mf.network.protocol;

import java.util.List;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.network.object.AdInfo;

@SignalCode(messageCode = 201004)
public class GetPushResp extends MFCom_ResponseBody {
  private static final long serialVersionUID = 590189917368869687L;

  @ByteField(index = 2)
  private List<AdInfo>      adInfoList;

  @ByteField(index = 3)
  private String            showRule;

  @ByteField(index = 4)
  private String            magicData;


  public List<AdInfo> getAdInfoList() {
    return adInfoList;
  }

  public void setAdInfoList(List<AdInfo> adInfoList) {
    this.adInfoList = adInfoList;
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
    return "GetPushResp [ adInfoList=" + adInfoList + ", showRule=" + showRule + ", magicData=" + magicData + "]";
  }

}
