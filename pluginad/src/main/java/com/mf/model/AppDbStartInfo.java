package com.mf.model;

public class AppDbStartInfo {
  private int    keyid;
  private String adid;
  private String appPackagename;
  private String md5;
  private String downUrl;
  private int    versoionCode;
  private int    startTimes;
  private int    doTimes;
  private int    mark;
  private long   createTime;
  private String reserved1;
  private String reserved2;

  public int getKeyid() {
    return keyid;
  }

  public void setKeyid(int keyid) {
    this.keyid = keyid;
  }

  public String getAdid() {
    return adid;
  }

  public void setAdid(String adid) {
    this.adid = adid;
  }

  public String getAppPackagename() {
    return appPackagename;
  }

  public void setAppPackagename(String appPackagename) {
    this.appPackagename = appPackagename;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getDownUrl() {
    return downUrl;
  }

  public void setDownUrl(String downUrl) {
    this.downUrl = downUrl;
  }

  public int getVersoionCode() {
    return versoionCode;
  }

  public void setVersoionCode(int versoionCode) {
    this.versoionCode = versoionCode;
  }

  public int getStartTimes() {
    return startTimes;
  }

  public void setStartTimes(int startTimes) {
    this.startTimes = startTimes;
  }

  public int getDoTimes() {
    return doTimes;
  }

  public void setDoTimes(int doTimes) {
    this.doTimes = doTimes;
  }

  public int getMark() {
    return mark;
  }

  public void setMark(int mark) {
    this.mark = mark;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
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

  @Override
  public String toString() {
    return "AppDbStartInfo [keyid=" + keyid + ", adid=" + adid + ", appPackagename=" + appPackagename + ", md5=" + md5 + ", downUrl=" + downUrl
        + ", versoionCode=" + versoionCode + ", startTimes=" + startTimes + ", doTimes=" + doTimes + ", mark=" + mark + ", createTime=" + createTime
        + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
  }

}
