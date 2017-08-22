package com.xdd.pay.network.object;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: CpInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class CpInfo {

  @ByteField(index = 0)
  private String channelId;

  @ByteField(index = 1)
  private String appId;

  @ByteField(index = 2)
  private int    sdkVerCode;

  @ByteField(index = 3)
  private String sdkVerName;

  @ByteField(index = 4)
  private int    clientVerCode;

  @ByteField(index = 5)
  private String clientVerName;

  @ByteField(index = 6)
  private String packageName;

  @ByteField(index = 7)
  private String reserved1;

  @ByteField(index = 8)
  private String reserved2;

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public int getClientVerCode() {
    return clientVerCode;
  }

  public void setClientVerCode(int clientVerCode) {
    this.clientVerCode = clientVerCode;
  }

  public String getClientVerName() {
    return clientVerName;
  }

  public void setClientVerName(String clientVerName) {
    this.clientVerName = clientVerName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getReserved1() {
    return reserved1;
  }

  public void setReserved1(String reserved1) {
    this.reserved1 = reserved1;
  }

  public String getReserved2() {
    return reserved2;
  }

  public void setReserved2(String reserved2) {
    this.reserved2 = reserved2;
  }

  public int getSdkVerCode() {
    return sdkVerCode;
  }

  public void setSdkVerCode(int sdkVerCode) {
    this.sdkVerCode = sdkVerCode;
  }

  public String getSdkVerName() {
    return sdkVerName;
  }

  public void setSdkVerName(String sdkVerName) {
    this.sdkVerName = sdkVerName;
  }

  @Override
  public String toString() {
    return "CpInfo [channelId=" + channelId + ", appId=" + appId + ", sdkVerCode=" + sdkVerCode + ", sdkVerName=" + sdkVerName + ", clientVerCode="
        + clientVerCode + ", clientVerName=" + clientVerName + ", packageName=" + packageName + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
  }
  
  public String getJson() {
      return "\"channelId\":"+"\""+channelId+"\""+",\"appId\":"+"\""+appId+"\""+",\"sdkVerCode\":"+"\""+sdkVerCode+"\""+",\"sdkVerName\":"+"\""+sdkVerName+"\""+",\"clientVerCode\":"+"\""+clientVerCode+"\""+",\"clientVerName\":"+"\""+clientVerName+"\""+",\"packageName\":"+"\""+packageName+"\"";
  }
}
