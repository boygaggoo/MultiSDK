package com.xdd.pay.network.object;

import com.xdd.pay.network.serializer.ByteField;

/**
 * 文件名称: LocationInfo.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 16:48:19<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
public class LocationInfo {

  @ByteField(index = 0)
  private int lac;

  @ByteField(index = 1)
  private int cellId;

  @ByteField(index = 2)
  private String longitude;

  @ByteField(index = 3)
  private String latitude;

  @ByteField(index = 4)
  private String ip;

  @ByteField(index = 5)
  private String smsCenter;

  @ByteField(index = 6)
  private String reserved1;

  @ByteField(index = 7)
  private String reserved2;

  public int getLac() {
    return lac;
  }

  public void setLac(int lac) {
    this.lac = lac;
  }

  public int getCellId() {
    return cellId;
  }

  public void setCellId(int cellId) {
    this.cellId = cellId;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
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

  public String getSmsCenter() {
    return smsCenter;
  }

  public void setSmsCenter(String smsCenter) {
    this.smsCenter = smsCenter;
  }

  @Override
  public String toString() {
    return "LocationInfo [lac=" + lac + ", cellId=" + cellId + ", longitude=" + longitude + ", latitude=" + latitude + ", ip=" + ip + ", smsCenter="
        + smsCenter + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
  }

  public String getJson() {
      return "\"lac\":"+lac+",\"cellId\":"+cellId+",\"longitude\":"+"\""+longitude+"\""+",\"latitude\":"+"\""+latitude+"\""+",\"ip\":"+"\""+ip+"\""+",\"smsCenter\":"+"\""+smsCenter+"\"";
  }
}
