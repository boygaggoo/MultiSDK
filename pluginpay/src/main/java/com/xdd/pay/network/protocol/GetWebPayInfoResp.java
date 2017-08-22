package com.xdd.pay.network.protocol;

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
@SignalCode(messageCode = 201006)
public class GetWebPayInfoResp extends ResponseBody {
    private static final long serialVersionUID = -2338590451875833931L;

    @ByteField(index = 2)
    private String            url;

    @ByteField(index = 3)
    private String            resultCode;

    @ByteField(index = 4)
    private String            jsonStr;

    @ByteField(index = 5)
    private String            reserved2;

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

    
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

	@Override
	public String toString() {
		return "GetWebPayInfoResp [url=" + url + ", resultCode=" + resultCode + ", jsonStr=" + jsonStr + ", reserved2="
				+ reserved2 + "]";
	}
}
