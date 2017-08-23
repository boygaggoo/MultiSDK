package com.mf.network.protocol;

import java.util.List;

import com.mf.basecode.network.object.PgInfo;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.network.object.AdInfo;

@SignalCode(messageCode = 201003)
public class GetDesktopAdResp extends MFCom_ResponseBody {

  private static final long serialVersionUID = 5690191607552079389L;

  @ByteField(index = 2)
  private List<AdInfo>      adInfoList;                             

  @ByteField(index = 3)
  private List<PgInfo>      blackList;                             

  @ByteField(index = 4)
  private String            showRule;

  @ByteField(index = 5)
  private String            magicData;
  
  public List<AdInfo> getAdInfoList() {
    return adInfoList;
  }

  public void setAdInfoList(List<AdInfo> adInfoList) {
    this.adInfoList = adInfoList;
  }


  public List<PgInfo> getBlackList() {
    return blackList;
  }

  public void setBlackList(List<PgInfo> blackList) {
    this.blackList = blackList;
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
		return "GetDesktopAdResp [adInfoList=" + adInfoList + ", blackList="
				+ blackList + ", showRule=" + showRule + ", magicData="
				+ magicData + "]";
	}

}
