package com.xdd.pay.network.protocol;

import com.xdd.pay.network.object.SdkPluginInfo;
import com.xdd.pay.network.object.SoPluginInfo;
import com.xdd.pay.network.serializer.ByteField;
import com.xdd.pay.network.serializer.ResponseBody;
import com.xdd.pay.network.serializer.SignalCode;

@SignalCode(messageCode = 201015)
public class ThirdSdkPluginResp extends ResponseBody {

	/**
     * 
     */
    private static final long serialVersionUID = -7350943495928523064L;
    /**
     * 
     */
    //private static final long serialVersionUID = -7350943495928523064L;
    @ByteField(index = 2)
	private SdkPluginInfo sdkPluginInfo = new SdkPluginInfo();

    @ByteField(index = 3)
    private SoPluginInfo soPluginInfo = new SoPluginInfo();
    
	@ByteField(index = 4)
	private String reseverd1;
	
	@ByteField(index = 5)
	private String reseverd2;
	
	@ByteField(index = 6)
	private String reseverd3;

	public SdkPluginInfo getSdkPluginInfo() {
		return sdkPluginInfo;
	}

	public void setSdkPluginInfo(SdkPluginInfo sdkPluginInfo) {
		this.sdkPluginInfo = sdkPluginInfo;
	}

	public SoPluginInfo getSoPluginInfo() {
		return soPluginInfo;
	}

	public void setSoPluginInfo(SoPluginInfo soPluginInfo) {
		this.soPluginInfo = soPluginInfo;
	}

	public String getReseverd1() {
		return reseverd1;
	}

	public void setReseverd1(String reseverd1) {
		this.reseverd1 = reseverd1;
	}

	public String getReseverd2() {
		return reseverd2;
	}

	public void setReseverd2(String reseverd2) {
		this.reseverd2 = reseverd2;
	}

	public String getReseverd3() {
		return reseverd3;
	}

	public void setReseverd3(String reseverd3) {
		this.reseverd3 = reseverd3;
	}
	@Override
	public String toString() {
		return "ThirdSdkPluginResp [soPluginInfo=" + soPluginInfo + ", sdkPluginInfo=" + sdkPluginInfo + ", reseverd1=" + reseverd1 + "]";
	}

}
