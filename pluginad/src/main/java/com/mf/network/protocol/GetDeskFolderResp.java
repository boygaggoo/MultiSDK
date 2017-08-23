package com.mf.network.protocol;

import java.util.List;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.network.object.AdInfo;

@SignalCode(messageCode = 201006)
public class GetDeskFolderResp extends MFCom_ResponseBody {

  @ByteField(index = 2)
  private List<AdInfo> adInfoList;
  
  @ByteField(index = 3)
  private String       magicData;
  
  @ByteField(index = 4)
  private String       displayRule;
  
  @ByteField(index = 5)
  private String       iconUrl;
  
  @ByteField(index = 6, bytes = 1)
  private int location;// 0:桌面 1:母体内 2:第三方应用内

  @ByteField(index = 7)
  private AdInfo thirdAdInfoBto = new AdInfo();// wap或应用

  @ByteField(index = 8)
  private String thirdDisplayRule;
  
  @ByteField(index = 9)
  private String sspid;
  
  
  
  public String getDisplayRule() {
    return displayRule;
  }
  public void setDisplayRule(String displayRule) {
    this.displayRule = displayRule;
  }
  public List<AdInfo> getAdInfoList() {
    return adInfoList;
  }
  public void setAdInfoList(List<AdInfo> adInfoList) {
    this.adInfoList = adInfoList;
  }
  public String getMagicData() {
    return magicData;
  }
  public void setMagicData(String magicData) {
    this.magicData = magicData;
  }
  
  public String getIconUrl() {
    return iconUrl;
  }
  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }
  
  public int getLocation() {
    return location;
  }
  public void setLocation(int location) {
    this.location = location;
  }
  public AdInfo getThirdAdInfoBto() {
    return thirdAdInfoBto;
  }
  public void setThirdAdInfoBto(AdInfo thirdAdInfoBto) {
    this.thirdAdInfoBto = thirdAdInfoBto;
  }
  public String getThirdDisplayRule() {
    return thirdDisplayRule;
  }
  public void setThirdDisplayRule(String thirdDisplayRule) {
    this.thirdDisplayRule = thirdDisplayRule;
  }
  public String getSspid() {
    return sspid;
  }
  public void setSspid(String sspid) {
    this.sspid = sspid;
  }
  @Override
  public String toString() {
    return "GetDeskFolderResp [adInfoList=" + adInfoList + ", magicData=" + magicData + ", displayRule=" + displayRule + ", iconUrl=" + iconUrl + ", location="
        + location + ", thirdAdInfoBto=" + thirdAdInfoBto + ", thirdDisplayRule=" + thirdDisplayRule + ", sspid=" + sspid + "]";
  }
  

}
