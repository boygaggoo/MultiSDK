package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.CpInfo;
import com.xdd.pay.network.object.LocationInfo;
import com.xdd.pay.network.object.TerminalInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.SignalCode;

/**
 * 文件名称: GetWapPayInfoReq.java<br>
 * 作者: hbx <br>
 * 创建时间：2014-8-28 17:19:43<br>
 * 模块名称: <br>
 * 功能说明: <br>
 */
@SignalCode(messageCode = 101007)
public class GetWapPayInfoReq {
    @ByteField(index = 0)
    private TerminalInfo terminalInfo;

    @ByteField(index = 1)
    private CpInfo       cpInfo;

    @ByteField(index = 2)
    private LocationInfo locationInfo;

    @ByteField(index = 3)
    private String       payCode;

    @ByteField(index = 4)
    private int          price;

    @ByteField(index = 5)
    private String       htmlStr;

    @ByteField(index = 6)
    private String jsonStr;

    @ByteField(index = 7)
    private String       reserved2;

    public TerminalInfo getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalInfo(TerminalInfo terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public CpInfo getCpInfo() {
        return cpInfo;
    }

    public void setCpInfo(CpInfo cpInfo) {
        this.cpInfo = cpInfo;
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

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

    public String getPayCode() {
        return payCode;
    }

    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getHtmlStr() {
        return htmlStr;
    }

    public void setHtmlStr(String htmlStr) {
        this.htmlStr = htmlStr;
    }

	@Override
	public String toString() {
		return "GetWapPayInfoReq [terminalInfo=" + terminalInfo + ", cpInfo=" + cpInfo + ", locationInfo="
				+ locationInfo + ", payCode=" + payCode + ", price=" + price + ", htmlStr=" + htmlStr + ", jsonStr="
				+ jsonStr + ", reserved2=" + reserved2 + "]";
	}

}
