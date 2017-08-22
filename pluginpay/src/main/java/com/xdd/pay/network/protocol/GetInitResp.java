package com.xdd.pay.network.protocol;

import java.util.ArrayList;

import com.xdd.pay.network.object.PayInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetInitResp.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-5-21 17:30:26<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 201002)
public class GetInitResp extends ResponseBody {

    private static final long  serialVersionUID = 862846061134979574L;

    @ByteField(index = 2)
    private ArrayList<PayInfo> payInfoList;

    @ByteField(index = 3)
    private byte               ploy;                                  // 策略，0：默认；1：二次确认对话框

    @ByteField(index = 4)
    private String             payCount;                              // 支付次数，客户端根据这个次数重新初始化代码

    @ByteField(index = 5)
    private String             qxtStr;

    public String getPayCount() {
        return payCount;
    }

    public void setPayCount(String payCount) {
        this.payCount = payCount;
    }

    public ArrayList<PayInfo> getPayInfoList() {
        return payInfoList;
    }

    public void setPayInfoList(ArrayList<PayInfo> payInfoList) {
        this.payInfoList = payInfoList;
    }

    public byte getPloy() {
        return ploy;
    }

    public void setPloy(byte ploy) {
        this.ploy = ploy;
    }

    public String getQxtStr() {
        return qxtStr;
    }

    public void setQxtStr(String qxtStr) {
        this.qxtStr = qxtStr;
    }

    @Override
    public String toString() {
        return "GetInitResp [payInfoList=" + payInfoList + ", ploy=" + ploy + ", payCount=" + payCount + ", qxtStr="
                + qxtStr + "]";
    }

}
