package com.mf.basecode.network.object;

import com.mf.basecode.network.serializer.ByteField;

public class TerminalInfo implements Cloneable {

  @ByteField(index = 0)
  private String hsman;

  @ByteField(index = 1)
  private String hstype;

  @ByteField(index = 2)
  private String osVer;

  @ByteField(index = 3)
  private short  screenWidth;

  @ByteField(index = 4)
  private short  screenHeight;

  @ByteField(index = 5)
  private String  cpu;
  
  @ByteField(index = 6)
  private short  ramSize;

  @ByteField(index = 7)
  private String imsi;

  @ByteField(index = 8)
  private String imei;

  @ByteField(index = 9)
  private int cellId;

  @ByteField(index = 10)
  private int  lac;
  /**
   * 1:2G, 2:3G, 3:wifi
   */
  @ByteField(index = 11, description = "1:2G, 2:3G, 3:wifi")
  private byte   networkType;

  @ByteField(index = 12)
  private String mac;
  
  @ByteField(index = 13, description = "通道标识符")
  private String channelid;

  @ByteField(index = 14, description = "应用程序id")
  private String appid;
  
  @ByteField(index = 15)
  private byte   rootEnable;

  @ByteField(index = 16)
  private String packageName;
  
  @ByteField(index = 17, description = "版本号")
  private int verCode;

  @ByteField(index = 18, description = "版本号")
  private String verName;
  
  @ByteField(index = 19, description = "框架版本号")
  private String frameVerName;
  
  @ByteField(index = 20)
  private String reserved1;
  
  @ByteField(index = 21)
  private String reserved2;
  
  @ByteField(index = 22)
  private String reserved3;
  
  @ByteField(index = 23)
  private String reserved4;

  public String getHsman() {
    return hsman;
  }

  public void setHsman(String hsman) {
    this.hsman = hsman;
  }

  public String getHstype() {
    return hstype;
  }

  public void setHstype(String hstype) {
    this.hstype = hstype;
  }

  public String getOsVer() {
    return osVer;
  }

  public void setOsVer(String osVer) {
    this.osVer = osVer;
  }

  public short getScreenWidth() {
    return screenWidth;
  }

  public void setScreenWidth(short screenWidth) {
    this.screenWidth = screenWidth;
  }

  public short getScreenHeight() {
    return screenHeight;
  }

  public void setScreenHeight(short screenHeight) {
    this.screenHeight = screenHeight;
  }

  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  public short getRamSize() {
    return ramSize;
  }

  public void setRamSize(short ramSize) {
    this.ramSize = ramSize;
  }

  public String getImsi() {
    return imsi;
  }

  public void setImsi(String imsi) {
    this.imsi = imsi;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public int getCellId() {
    return cellId;
  }

  public void setCellId(int cellId) {
    this.cellId = cellId;
  }

  public int getLac() {
    return lac;
  }

  public void setLac(int lac) {
    this.lac = lac;
  }

  public byte getNetworkType() {
    return networkType;
  }

  public void setNetworkType(byte networkType) {
    this.networkType = networkType;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public String getChannelid() {
    return channelid;
  }

  public void setChannelid(String channelid) {
    this.channelid = channelid;
  }

  public String getAppid() {
    return appid;
  }

  public void setAppid(String appid) {
    this.appid = appid;
  }

  public byte getRootEnable() {
    return rootEnable;
  }

  public void setRootEnable(byte rootEnable) {
    this.rootEnable = rootEnable;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public int getVerCode() {
    return verCode;
  }

  public void setVerCode(int verCode) {
    this.verCode = verCode;
  }

  public String getVerName() {
    return verName;
  }

  public void setVerName(String verName) {
    this.verName = verName;
  }

  public String getFrameVerName() {
    return frameVerName;
  }

  public void setFrameVerName(String frameVerName) {
    this.frameVerName = frameVerName;
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

  public String getReserved3() {
    return reserved3;
  }

  public void setReserved3(String reserved3) {
    this.reserved3 = reserved3;
  }

  public String getReserved4() {
    return reserved4;
  }

  public void setReserved4(String reserved4) {
    this.reserved4 = reserved4;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public String toString() {
    return "TerminalInfo [hsman=" + hsman + ", hstype=" + hstype + ", osVer=" + osVer + ", screenWidth=" + screenWidth + ", screenHeight=" + screenHeight
        + ", cpu=" + cpu + ", ramSize=" + ramSize + ", imsi=" + imsi + ", imei=" + imei + ", cellId=" + cellId + ", lac=" + lac + ", networkType="
        + networkType + ", mac=" + mac + ", channelid=" + channelid + ", appid=" + appid + ", rootEnable=" + rootEnable + ", packageName=" + packageName
        + ", verCode=" + verCode + ", verName=" + verName + ", frameVerName=" + frameVerName + ", reserved1=" + reserved1 + ", reserved2=" + reserved2
        + ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
  }
  
}
