package com.multisdk.library.network.obj;

import com.multisdk.library.network.serializer.ByteField;
import java.io.Serializable;

public class InitInfo implements Serializable {

  @ByteField(index = 0)
  private byte type; // 1 广告，2 支付

  @ByteField(index = 1)
  private byte isSwitch; // 0关闭，1 开启

  @ByteField(index = 2)
  private int reqRelativeTime; // 距离下次更新时间的时间间隔（分钟）

  @ByteField(index = 3)
  private int activeTimes; // 延迟时间（/秒）

  @ByteField(index = 4)
  private byte isUpdate; // 0 不需要，1 需要

  @ByteField(index = 5)
  private int version;

  @ByteField(index = 6)
  private String downloadUrl;

  @ByteField(index = 7)
  private String md5;

  public byte getType() {
    return type;
  }

  public void setType(byte type) {
    this.type = type;
  }

  public byte getIsSwitch() {
    return isSwitch;
  }

  public void setIsSwitch(byte isSwitch) {
    this.isSwitch = isSwitch;
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

  public byte getIsUpdate() {
    return isUpdate;
  }

  public void setIsUpdate(byte isUpdate) {
    this.isUpdate = isUpdate;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }
}
