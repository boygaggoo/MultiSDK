package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.PayInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetWebPayInfoResp.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-8-28 17:21:26<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 201005)
public class GetPayInfoResp extends ResponseBody {

    private static final long serialVersionUID = -4346270903850613171L;

    @ByteField(index = 2)
    private PayInfo           payInfo;

    @ByteField(index = 3)
    private String            reserved1;

    @ByteField(index = 4)
    private String            qxtStr;

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public PayInfo getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(PayInfo payInfo) {
        this.payInfo = payInfo;
    }

    public String getQxtStr() {
        return qxtStr;
    }

    public void setQxtStr(String qxtStr) {
        this.qxtStr = qxtStr;
    }

    @Override
    public String toString() {
        return "GetPayInfoResp [payInfo=" + payInfo + ", reserved1=" + reserved1 + ", qxtStr=" + qxtStr + "]";
    }

}
