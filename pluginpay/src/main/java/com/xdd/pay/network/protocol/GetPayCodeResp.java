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
@SignalCode(messageCode = 201004)
public class GetPayCodeResp extends ResponseBody {

    private static final long serialVersionUID = 7932625311004472562L;

    @ByteField(index = 2)
    private PayCodeInfo       payCodeInfo;

    @ByteField(index = 3)
    private String            state; //0:已经登入，无需登入;1:需要登入

    @ByteField(index = 4)
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

    @Override
    public String toString() {
        return "GetPayCodeResp [payCodeInfo=" + payCodeInfo + ", state=" + state + ", reserved2=" + reserved2 + "]";
    }

}
