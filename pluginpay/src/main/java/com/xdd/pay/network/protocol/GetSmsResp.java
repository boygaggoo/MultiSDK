package com.xdd.pay.network.protocol;

import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

@SignalCode(messageCode = 201012)
public class GetSmsResp extends ResponseBody {

	private static final long serialVersionUID = -2574850698290165626L;

	@ByteField(index = 2)
	private int ploy;

	@ByteField(index = 3)
	private String msg;

	@ByteField(index = 4)
	private String dest;

	@ByteField(index = 5)
	private String url;

	@ByteField(index = 6)
	private String parameter;

	@ByteField(index = 7)
	private String jsonStr;

	@ByteField(index = 8)
	private String reserved2;

	@ByteField(index = 9)
	private String reserved3;

	@ByteField(index = 10)
	private String reserved4;

	public int getPloy() {
		return ploy;
	}

	public void setPloy(int ploy) {
		this.ploy = ploy;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
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
		return "GetSmsResp [ploy=" + ploy + ", msg=" + msg + ", dest=" + dest + ", url=" + url + ", parameter="
				+ parameter + ", jsonStr=" + jsonStr + ", reserved2=" + reserved2 + ", reserved3=" + reserved3
				+ ", reserved4=" + reserved4 + "]";
	}
}
