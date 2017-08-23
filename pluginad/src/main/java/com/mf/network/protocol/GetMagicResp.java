package com.mf.network.protocol;

import java.util.Arrays;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 201007)
public class GetMagicResp extends MFCom_ResponseBody {
  
  @ByteField(index = 2)
  private byte execMethod;
  
  @ByteField(index = 3)
  private String tagetUrl;
  
  @ByteField(index = 4)
  private byte[] content;
  
  @ByteField(index = 5)
  private int reqTimes;
  
  @ByteField(index = 6)
  private int reqIntervalTime;
  
  @ByteField(index = 7)
  private String magicData;
  
  @ByteField(index = 8)
  private long startTime;
  
  @ByteField(index = 9)
  private String adid;

  public byte getExecMethod() {
    return execMethod;
  }

  public void setExecMethod(byte execMethod) {
    this.execMethod = execMethod;
  }

  public String getTagetUrl() {
    return tagetUrl;
  }

  public void setTagetUrl(String tagetUrl) {
    this.tagetUrl = tagetUrl;
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  public int getReqTimes() {
    return reqTimes;
  }

  public void setReqTimes(int reqTimes) {
    this.reqTimes = reqTimes;
  }

  public int getReqIntervalTime() {
    return reqIntervalTime;
  }

  public void setReqIntervalTime(int reqIntervalTime) {
    this.reqIntervalTime = reqIntervalTime;
  }

  public String getMagicData() {
    return magicData;
  }

  public void setMagicData(String magicData) {
    this.magicData = magicData;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public String getAdid() {
    return adid;
  }

  public void setAdid(String adid) {
    this.adid = adid;
  }

  @Override
  public String toString() {
    return "GetMagicResp [execMethod=" + execMethod + ", tagetUrl=" + tagetUrl + ", content=" + Arrays.toString(content) + ", reqTimes=" + reqTimes
        + ", reqIntervalTime=" + reqIntervalTime + ", magicData=" + magicData + ", startTime=" + startTime + ", adid=" + adid + "]";
  }
  
}
