package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.PayCodeInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetInitResp.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 17:30:26<br>
 * 模块名称: <br>
 * 功能说明: MM,网游等支付方式实时获取
 */
@SignalCode(messageCode = 201008)
public class GetNetGameResp extends ResponseBody {

    private static final long serialVersionUID = 7932625311004472562L;

    @ByteField(index = 2)
    private PayCodeInfo       payCodeInfo;

    @ByteField(index = 3)
    private String            state; //0:已经登入，无需登入;1:需要登入
    
    @ByteField(index = 4)
    private String       contentSid;

    @ByteField(index = 5)
    private String            reserved1;
    
    @ByteField(index = 6)
    private String            reserved2;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public PayCodeInfo getPayCodeInfo() {
        return payCodeInfo;
    }

    public void setPayCodeInfo(PayCodeInfo payCodeInfo) {
        this.payCodeInfo = payCodeInfo;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getContentSid() {
        return contentSid;
    }

    public void setContentSid(String contentSid) {
        this.contentSid = contentSid;
    }

    @Override
    public String toString() {
        return "GetNetGameResp [payCodeInfo=" + payCodeInfo + ", state=" + state + ", contentSid=" + contentSid
                + ", reserved1=" + reserved1 + ", reserved2=" + reserved2 + "]";
    }

}
