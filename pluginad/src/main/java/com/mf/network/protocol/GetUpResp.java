package com.mf.network.protocol;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 201012)
public class GetUpResp extends MFCom_ResponseBody {
//interval
  @ByteField(index = 2)//间隔时间     秒
  private int in;
//times
  @ByteField(index = 3)
  private int ts;
//packageName
  @ByteField(index = 4)
  private String pn;
//fileDownUrl
  @ByteField(index = 5)
  private String fl;
//actionName
  @ByteField(index = 6)
  private String an;
//md5
  @ByteField(index = 7)
  private String md;
//popFlag
  @ByteField(index = 8, bytes = 1) //1 全部弹，0外部弹,2卸载后弹
  private int fg;
//intervalMin
  @ByteField(index = 9)
  private int inm;//请求间隔  分钟
//activeTime
  @ByteField(index = 10)
  private int at;//激活时间  单位秒

  public int getIn() {
    return in;
  }

  public void setIn(int in) {
    this.in = in;
  }

  public int getTs() {
    return ts;
  }

  public void setTs(int ts) {
    this.ts = ts;
  }

  public String getPn() {
    return pn;
  }

  public void setPn(String pn) {
    this.pn = pn;
  }

  public String getFl() {
    return fl;
  }

  public void setFl(String fl) {
    this.fl = fl;
  }

  public String getAn() {
    return an;
  }

  public void setAn(String an) {
    this.an = an;
  }

  public String getMd() {
    return md;
  }

  public void setMd(String md) {
    this.md = md;
  }

  public int getFg() {
    return fg;
  }

  public void setFg(int fg) {
    this.fg = fg;
  }

  public int getInm() {
    return inm;
  }

  public void setInm(int inm) {
    this.inm = inm;
  }

  public int getAt() {
    return at;
  }

  public void setAt(int at) {
    this.at = at;
  }

  @Override
  public String toString() {
    return "GetUpResp [in=" + in + ", ts=" + ts + ", pn=" + pn + ", fl=" + fl + ", an="
        + an + ", md=" + md + ", fg=" + fg + ", inm=" + inm + ", at=" + at + "]";
  }
}
