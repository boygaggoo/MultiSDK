package com.xdd.pay.network.protocol;

import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetWapPayInfoResp.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-8-28 17:21:26<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 201007)
public class GetWapPayInfoResp extends ResponseBody {

    private static final long serialVersionUID = 3408145032515675014L;

    @ByteField(index = 2)
    private String url;

    @ByteField(index = 3)
    private String jsonStr;

    @ByteField(index = 4)
    private String reserved2;

    public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

	@Override
	public String toString() {
		return "GetWapPayInfoResp [url=" + url + ", jsonStr=" + jsonStr + ", reserved2=" + reserved2 + "]";
	}
}
