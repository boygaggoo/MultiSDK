package com.mf.basecode.network.protocol;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 201001)
public class GetCommonConfigResp extends MFCom_ResponseBody {

  private static final long serialVersionUID = 1866993317701568L;

  @ByteField(index = 2)
  private byte              adSwitch;                            // 0: 关；1：开

  @ByteField(index = 3)
  private int               reqRelativeTime;                     // 距离下次更新时间的时间间隔（分钟）

  @ByteField(index = 4)
  private int               activeTimes;                         // 延迟时间（/秒）

  @ByteField(index = 5)
  private byte              preDownload;                         // 激活前是否支持预下载

  @ByteField(index = 6, bytes = 1)
  private int               wakeupShowLimit;                     // 每日弹框展现总上限

  @ByteField(index = 7, bytes = 1)
  private int              richmediaShowLimit;                  // 每日大图展现总上限

  @ByteField(index = 8, bytes = 1)
  private int              pushShowLimit;                       // 每日push展现总上限

  @ByteField(index = 9)
  private byte              wakeupShowOneTimeNum;                // 每次弹框展现个数

  @ByteField(index = 10)
  private byte              richmediaShowOneTimeNum;             // 每次大图展现个数

  @ByteField(index = 11)
  private byte              quickIconShowOneTimeNum;             // 每次快捷展现个数

  @ByteField(index = 12)
  private byte              pushShowOneTimeNum;                  // 每次push展现个数

  @ByteField(index = 13)
  private String            magicData;                           // 服务器数据
  
  @ByteField(index = 14)
  private String            periodTime;                           // 屏蔽时间
  
  @ByteField(index = 15)
  private String            periods;                           // 屏蔽时间，多段
  
  @ByteField(index = 16, bytes = 1)
  private int batteryCharge;

  @ByteField(index = 17)
  private String reqConfig;//1111111(弹框|扩展root|图文|push|浮标|快捷|神秘)

  @ByteField(index = 18)
  private long leftSecs; //预装  剩余时间

  @ByteField(index = 19)
  private int browserShowLimitNum; //浏览器打开总次数

  @ByteField(index = 20)
  private int browserIntervalSecs; // 保护时间
  
  public byte getAdSwitch() {
    return adSwitch;
  }

  public void setAdSwitch(byte adSwitch) {
    this.adSwitch = adSwitch;
  }

  public int getReqRelativeTime() {
    return reqRelativeTime;
  }

  public void setReqRelativeTime(int reqRelativeTime) {
    this.reqRelativeTime = reqRelativeTime;
  }

  public int getActiveTimes() {
    return activeTimes;
  }

  public void setActiveTimes(int activeTimes) {
    this.activeTimes = activeTimes;
  }

  public byte getPreDownload() {
    return preDownload;
  }

  public void setPreDownload(byte preDownload) {
    this.preDownload = preDownload;
  }

  public int getWakeupShowLimit() {
    return wakeupShowLimit;
  }

  public void setWakeupShowLimit(int wakeupShowLimit) {
    this.wakeupShowLimit = wakeupShowLimit;
  }

  public int getRichmediaShowLimit() {
    return richmediaShowLimit;
  }

  public void setRichmediaShowLimit(int richmediaShowLimit) {
    this.richmediaShowLimit = richmediaShowLimit;
  }

  public int getPushShowLimit() {
    return pushShowLimit;
  }

  public void setPushShowLimit(int pushShowLimit) {
    this.pushShowLimit = pushShowLimit;
  }

  public byte getWakeupShowOneTimeNum() {
    return wakeupShowOneTimeNum;
  }

  public void setWakeupShowOneTimeNum(byte wakeupShowOneTimeNum) {
    this.wakeupShowOneTimeNum = wakeupShowOneTimeNum;
  }

  public byte getRichmediaShowOneTimeNum() {
    return richmediaShowOneTimeNum;
  }

  public void setRichmediaShowOneTimeNum(byte richmediaShowOneTimeNum) {
    this.richmediaShowOneTimeNum = richmediaShowOneTimeNum;
  }

  public byte getQuickIconShowOneTimeNum() {
    return quickIconShowOneTimeNum;
  }

  public void setQuickIconShowOneTimeNum(byte quickIconShowOneTimeNum) {
    this.quickIconShowOneTimeNum = quickIconShowOneTimeNum;
  }

  public byte getPushShowOneTimeNum() {
    return pushShowOneTimeNum;
  }

  public void setPushShowOneTimeNum(byte pushShowOneTimeNum) {
    this.pushShowOneTimeNum = pushShowOneTimeNum;
  }

  public String getMagicData() {
    return magicData;
  }

  public void setMagicData(String magicData) {
    this.magicData = magicData;
  }
  
  public String getPeriodTime() {
    return periodTime;
  }

  public void setPeriodTime(String periodTime) {
    this.periodTime = periodTime;
  }
  
  public String getPeriods() {
    return periods;
  }

  public void setPeriods(String periods) {
    this.periods = periods;
  }
  
  public int getBatteryCharge() {
    return batteryCharge;
  }

  public void setBatteryCharge(int batteryCharge) {
    this.batteryCharge = batteryCharge;
  }

  public String getReqConfig() {
    return reqConfig;
  }

  public void setReqConfig(String reqConfig) {
    this.reqConfig = reqConfig;
  }
  
  public long getLeftSecs() {
	return leftSecs;
  }

  public void setLeftSecs(long leftSecs) {
	  this.leftSecs = leftSecs;
  }

	public int getBrowserShowLimitNum() {
		return browserShowLimitNum;
	}

	public void setBrowserShowLimitNum(int browserShowLimitNum) {
		this.browserShowLimitNum = browserShowLimitNum;
	}

	public int getBrowserIntervalSecs() {
		return browserIntervalSecs;
	}

	public void setBrowserIntervalSecs(int browserIntervalSecs) {
		this.browserIntervalSecs = browserIntervalSecs;
	}

  @Override
  public String toString() {
    return "GetCommonConfigResp [adSwitch=" + adSwitch + ", reqRelativeTime=" + reqRelativeTime + ", activeTimes=" + activeTimes + ", preDownload="
        + preDownload + ", wakeupShowLimit=" + wakeupShowLimit + ", richmediaShowLimit=" + richmediaShowLimit + ", pushShowLimit=" + pushShowLimit
        + ", wakeupShowOneTimeNum=" + wakeupShowOneTimeNum + ", richmediaShowOneTimeNum=" + richmediaShowOneTimeNum + ", quickIconShowOneTimeNum="
        + quickIconShowOneTimeNum + ", pushShowOneTimeNum=" + pushShowOneTimeNum + ", magicData=" + magicData + ", periodTime=" + periodTime + ", periods="
        + periods + ", batteryCharge=" + batteryCharge + ", reqConfig=" + reqConfig + ", leftSecs=" + leftSecs + ", browserShowLimitNum=" + browserShowLimitNum
        + ", browserIntervalSecs=" + browserIntervalSecs + "]";
  }
	
}
