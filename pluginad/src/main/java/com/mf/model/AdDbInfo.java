package com.mf.model;


public class AdDbInfo {
  private int    keyid;
  private String adId;
  private String adName;
  private String adPicUrl;
  private int    adType;
  private String adLanguage;
  private String adDownUrl;
  private String packageName;
  private int    versionCode;
  private String fileMd5;
  private int    adDisplayType;
  private int    preDown;
  private int    showTimes;
  private int    position;
  private long   fileSize;
  private int    remainTimes;
  private int    downloadTimes;
  private int    showmark;
  private int    hasShowTimes;
  private long   createTime;
  private int    promType;
  private String promTypeName;
  private String picName;
  private int    installed;
  private String Reserved1;
  private String Reserved2;
  private String Reserved3;
  private String sspid;
  private int ssptype;

  public int getKeyid() {
    return keyid;
  }
  public void setKeyid(int keyid) {
    this.keyid = keyid;
  }
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
  public int getAdType() {
    return adType;
  }
  public void setAdType(int adType) {
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
  public int getAdDisplayType() {
    return adDisplayType;
  }
  public void setAdDisplayType(int adDisplayType) {
    this.adDisplayType = adDisplayType;
  }
  public int getPreDown() {
    return preDown;
  }
  public void setPreDown(int preDown) {
    this.preDown = preDown;
  }
  public int getShowTimes() {
    return showTimes;
  }
  public void setShowTimes(int showTimes) {
    this.showTimes = showTimes;
  }
  public int getPosition() {
    return position;
  }
  public void setPosition(int position) {
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
  
  public int getShowmark() {
    return showmark;
  }
  public void setShowmark(int showmark) {
    this.showmark = showmark;
  }
  public int getHasShowTimes() {
    return hasShowTimes;
  }
  public void setHasShowTimes(int hasShowTimes) {
    this.hasShowTimes = hasShowTimes;
  }
  public long getCreateTime() {
    return createTime;
  }
  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }
  public int getPromType() {
    return promType;
  }
  public void setPromType(int promType) {
    this.promType = promType;
  }
  public String getPromTypeName() {
    return promTypeName;
  }
  public void setPromTypeName(String promTypeName) {
    this.promTypeName = promTypeName;
  }
  public String getPicName() {
    return picName;
  }
  public void setPicName(String picName) {
    this.picName = picName;
  }
  
  public int getInstalled() {
    return installed;
  }
  public void setInstalled(int installed) {
    this.installed = installed;
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
  public boolean equals(Object o) {
    if (o instanceof AdDbInfo) {
      AdDbInfo info = (AdDbInfo) o;
      return info.getPackageName().equals(packageName) && info.getVersionCode() == versionCode;
    }
    return false;
  }
  @Override
  public String toString() {
    return "AdDbInfo [keyid=" + keyid + ", adId=" + adId + ", adName=" + adName + ", adPicUrl=" + adPicUrl + ", adType=" + adType + ", adLanguage="
        + adLanguage + ", adDownUrl=" + adDownUrl + ", packageName=" + packageName + ", versionCode=" + versionCode + ", fileMd5=" + fileMd5
        + ", adDisplayType=" + adDisplayType + ", preDown=" + preDown + ", showTimes=" + showTimes + ", position=" + position + ", fileSize=" + fileSize
        + ", remainTimes=" + remainTimes + ", downloadTimes=" + downloadTimes + ", showmark=" + showmark + ", hasShowTimes=" + hasShowTimes + ", createTime="
        + createTime + ", promType=" + promType + ", promTypeName=" + promTypeName + ", picName=" + picName + ", installed=" + installed + ", Reserved1="
        + Reserved1 + ", Reserved2=" + Reserved2 + ", Reserved3=" + Reserved3 + ", sspid=" + sspid + ", ssptype=" + ssptype + "]";
  }
  
}
