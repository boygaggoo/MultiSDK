package com.mf.basecode.network.object;

import java.io.Serializable;

import com.mf.basecode.network.serializer.ByteField;

public class AdLogInfo implements Serializable {
  private static final long serialVersionUID = -7644774695821030003L;

  @ByteField(index = 0)
  private int               action;                                  // 1:安装
                                                                      // 2：启动
                                                                      // 3：展现 4:
                                                                      // 点击
                                                                      // 5:下载成功
                                                                      // 6:安装成功
                                                                      // 7:卸载 8:
                                                                      // 卸载成功
                                                                      // 9：点击OK
                                                                      // 10:点击取消
                                                                      // 11:预下载
                                                                      // 12:下载失败
                                                                      // 13:请求

  @ByteField(index = 1)
  private String            adTag;

  @ByteField(index = 2)
  private int               num;                                     // 次数

  @ByteField(index = 3)
  private short             source1;                                 // 1:唤醒2:富媒体大图3:富媒体中图(插屏)4:富媒体顶部横幅
                                                                      //5:富媒体底部横幅
                                                                      // 6:富媒体左图标7：富媒体右图标
                                                                      // 8：通知栏9：快捷方式
                                                                      // 10:自更新
                                                                      // 11:桌面文件夹12:神秘请求

  @ByteField(index = 4)
  private String            reserved1;

  @ByteField(index = 5)
  private String            reserved2;

  @ByteField(index = 6)
  private String            reserved3;

  @ByteField(index = 7)
  private String            reserved4;

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public String getAdTag() {
    return adTag;
  }

  public void setAdTag(String adTag) {
    this.adTag = adTag;
  }

  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public short getSource1() {
    return source1;
  }

  public void setSource1(short source1) {
    this.source1 = source1;
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
  public String toString() {
    return "AdLogInfo [action=" + action + ", adTag=" + adTag + ", num=" + num + ", source1=" + source1 + ", reserved1=" + reserved1 + ", reserved2="
        + reserved2 + ", reserved3=" + reserved3 + ", reserved4=" + reserved4 + "]";
  }

}
