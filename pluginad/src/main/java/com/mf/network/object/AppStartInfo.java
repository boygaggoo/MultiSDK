package com.mf.network.object;

import com.mf.basecode.network.serializer.ByteField;

public class AppStartInfo {

  @ByteField(index = 0)
  private String adid;
  
	@ByteField(index = 1)
	private String appPackagename;

	@ByteField(index = 2)
	private String md5;

	@ByteField(index = 3)
	private String downUrl;

	@ByteField(index = 4)
	private int versoionCode;

	@ByteField(index = 5)
	private short startTimes;

	@ByteField(index = 6)
	private String reserved1;
	
	@ByteField(index = 7)
	private String reserved2;

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

  public short getStartTimes() {
    return startTimes;
  }

  public void setStartTimes(short startTimes) {
    this.startTimes = startTimes;
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
    return "AppStartInfo [adid=" + adid + ", appPackagename=" + appPackagename + ", md5=" + md5 + ", downUrl=" + downUrl + ", versoionCode=" + versoionCode
        + ", startTimes=" + startTimes + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
  }
  
}
