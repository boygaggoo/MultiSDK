package com.mf.network.object;

import java.io.Serializable;

import com.mf.basecode.network.serializer.ByteField;

public class AdInfo implements Serializable {

  private static final long serialVersionUID = 3400845986346562220L;

  @ByteField(index = 0)
  private String            adId;

  @ByteField(index = 1)
  private String            adName;

  @ByteField(index = 2)
  private String            adPicUrl;

  @ByteField(index = 3)
  private byte              adType;

  @ByteField(index = 4)
  private String            adLanguage;

  @ByteField(index = 5)
  private String            adDownUrl;                              // 显示通知时间，格式：15：15

  @ByteField(index = 6)
  private String            packageName;                            // 广告展示时间，为0时不消失

  @ByteField(index = 7)
  private int               versionCode;                            // 1:
  // 图；2：图文

  @ByteField(index = 8)
  private String            fileMd5;                                // 点击触发类型
                                                                     // 1：apk下载,
                                                                     // 2:wap
  @ByteField(index = 9)
  private byte              adDisplayType;                          // actionType:1，则是apk下载url;
                                                                     // actionType:2
                                                                     // 则是wap地址
  @ByteField(index = 10)
  private byte              preDown;                                // type为1时用到

  @ByteField(index = 11)
  private byte              showTimes;

  @ByteField(index = 12)
  private byte              position;

  @ByteField(index = 13)
  private long              fileSize;
  
  @ByteField(index = 14)
  private int              remainTimes;
  
  @ByteField(index = 15)
  private int downloadTimes;

  @ByteField(index = 16)
  private String            Reserved1;                              // MD5

  @ByteField(index = 17)
  private String            Reserved2;

  @ByteField(index = 18)
  private String            Reserved3;

  @ByteField(index = 19)
  private String            sspid;
  
  @ByteField(index = 20)
  private int            ssptype;   //1banner  2插屏   3全屏
  

  public String getAdId() {
    return adId;
  }

  public void setAdId(String adId) {
    this.adId = adId;
  }

  public String getAdName() {
    return adName;
  }

  public void setAdName(String adName) {
    this.adName = adName;
  }

  public String getAdPicUrl() {
    return adPicUrl;
  }

  public void setAdPicUrl(String adPicUrl) {
    this.adPicUrl = adPicUrl;
  }

  public byte getAdType() {
    return adType;
  }

  public void setAdType(byte adType) {
    this.adType = adType;
  }

  public String getAdLanguage() {
    return adLanguage;
  }

  public void setAdLanguage(String adLanguage) {
    this.adLanguage = adLanguage;
  }

  public String getAdDownUrl() {
    return adDownUrl;
  }

  public void setAdDownUrl(String adDownUrl) {
    this.adDownUrl = adDownUrl;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getFileMd5() {
    return fileMd5;
  }

  public void setFileMd5(String fileMd5) {
    this.fileMd5 = fileMd5;
  }

  public byte getAdDisplayType() {
    return adDisplayType;
  }

  public void setAdDisplayType(byte adDisplayType) {
    this.adDisplayType = adDisplayType;
  }

  public byte getPreDown() {
    return preDown;
  }

  public void setPreDown(byte preDown) {
    this.preDown = preDown;
  }

  public byte getShowTimes() {
    return showTimes;
  }

  public void setShowTimes(byte showTimes) {
    this.showTimes = showTimes;
  }

  public byte getPosition() {
    return position;
  }

  public void setPosition(byte position) {
    this.position = position;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public int getRemainTimes() {
    return remainTimes;
  }

  public void setRemainTimes(int remainTimes) {
    this.remainTimes = remainTimes;
  }
  
  public int getDownloadTimes() {
    return downloadTimes;
  }

  public void setDownloadTimes(int downloadTimes) {
    this.downloadTimes = downloadTimes;
  }

  public String getReserved1() {
    return Reserved1;
  }

  public void setReserved1(String reserved1) {
    Reserved1 = reserved1;
  }

  public String getReserved2() {
    return Reserved2;
  }

  public void setReserved2(String reserved2) {
    Reserved2 = reserved2;
  }

  public String getReserved3() {
    return Reserved3;
  }

  public void setReserved3(String reserved3) {
    Reserved3 = reserved3;
  }

  public String getSspid() {
    return sspid;
  }

  public void setSspid(String sspid) {
    this.sspid = sspid;
  }


  public int getSsptype() {
    return ssptype;
  }

  public void setSsptype(int ssptype) {
    this.ssptype = ssptype;
  }

  @Override
  public String toString() {
    return "AdInfo [adId=" + adId + ", adName=" + adName + ", adPicUrl=" + adPicUrl + ", adType=" + adType + ", adLanguage=" + adLanguage + ", adDownUrl="
        + adDownUrl + ", packageName=" + packageName + ", versionCode=" + versionCode + ", fileMd5=" + fileMd5 + ", adDisplayType=" + adDisplayType
        + ", preDown=" + preDown + ", showTimes=" + showTimes + ", position=" + position + ", fileSize=" + fileSize + ", remainTimes=" + remainTimes
        + ", downloadTimes=" + downloadTimes + ", Reserved1=" + Reserved1 + ", Reserved2=" + Reserved2 + ", Reserved3=" + Reserved3 + ", sspid=" + sspid
        + ", ssptype=" + ssptype + "]";
  }
}
