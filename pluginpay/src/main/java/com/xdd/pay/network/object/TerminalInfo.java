package com.xdd.pay.network.object;

import com.xdd.pay.network.serializer.ByteField;

public class TerminalInfo {

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
  private String cpu;

  @ByteField(index = 6)
  private int    ramSize;

  @ByteField(index = 7)
  private int    romSize;

  @ByteField(index = 8)
  private int    extraSize;

  @ByteField(index = 9)
  private String imsi;

  @ByteField(index = 10)
  private String imei;

  @ByteField(index = 11)
  private byte   networkType;

  @ByteField(index = 12)
  private String phoneNum;

  @ByteField(index = 13)
  private String mac;

  @ByteField(index = 14)
  private byte   root;

  @ByteField(index = 15)
  private byte   networkSystem;

  @ByteField(index = 16)
  private String ICCID;

  @ByteField(index = 17)
  private String jsonObj;

  @ByteField(index = 18)
  private String reserved3;

  @ByteField(index = 19)
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

  public int getRamSize() {
    return ramSize;
  }

  public void setRamSize(int ramSize) {
    this.ramSize = ramSize;
  }

  public int getRomSize() {
    return romSize;
  }

  public void setRomSize(int romSize) {
    this.romSize = romSize;
  }

  public int getExtraSize() {
    return extraSize;
  }

  public void setExtraSize(int extraSize) {
    this.extraSize = extraSize;
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

  public byte getNetworkType() {
    return networkType;
  }

  public void setNetworkType(byte networkType) {
    this.networkType = networkType;
  }

  public String getPhoneNum() {
    return phoneNum;
  }

  public void setPhoneNum(String phoneNum) {
    this.phoneNum = phoneNum;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public byte getRoot() {
    return root;
  }

  public void setRoot(byte root) {
    this.root = root;
  }

  public byte getNetworkSystem() {
    return networkSystem;
  }

  public void setNetworkSystem(byte networkSystem) {
    this.networkSystem = networkSystem;
  }

  public String getICCID() {
    return ICCID;
  }

  public void setICCID(String iCCID) {
    ICCID = iCCID;
  }

  public String getJsonObj() {
    return jsonObj;
  }

  public void setJsonObj(String jsonObj) {
    this.jsonObj = jsonObj;
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
  public String toString() {
    return "TerminalInfo [hsman=" + hsman + ", hstype=" + hstype + ", osVer=" + osVer + ", screenWidth=" + screenWidth + ", screenHeight=" + screenHeight
        + ", cpu=" + cpu + ", ramSize=" + ramSize + ", romSize=" + romSize + ", extraSize=" + extraSize + ", imsi=" + imsi + ", imei=" + imei
        + ", networkType=" + networkType + ", phoneNum=" + phoneNum + ", mac=" + mac + ", root=" + root + ", networkSystem=" + networkSystem + ", ICCID="
        + ICCID + ", jsonObj=" + jsonObj + ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
  }

  public String getJson() {
    return "\"hsman\":" + "\"" + hsman + "\"" + ",\"hstype\":" + "\"" + hstype + "\"" + ",\"osVer\":" + "\"" + osVer + "\"" + ",\"screenWidth\":" + screenWidth
        + ",\"screenHeight\":" + screenHeight + ",\"cpu\":" + "\"" + cpu + "\"" + ",\"ramSize\":" + ramSize + ",\"romSize\":" + romSize + ",\"extraSize\":"
        + extraSize + ",\"imsi\":" + "\"" + imsi + "\"" + ",\"imei\":" + "\"" + imei + "\"" + ",\"networkType\":" + networkType + ",\"phoneNum\":" + "\""
        + phoneNum + "\"" + ",\"mac\":" + "\"" + mac + "\"" + ",\"root\":" + root + ",\"networkSystem\":" + networkSystem + ",\"ICCID\":" + "\"" + ICCID + "\""
        + ",\"buildId\":" + android.os.Build.ID;
  }

}
