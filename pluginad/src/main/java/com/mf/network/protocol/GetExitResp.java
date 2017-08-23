package com.mf.network.protocol;

import java.util.ArrayList;

import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;
import com.mf.network.object.AdInfo;

@SignalCode(messageCode = 201013)
public class GetExitResp extends MFCom_ResponseBody {

	@ByteField(index = 2)
	private ArrayList<AdInfo> adInfoList = new ArrayList<AdInfo>();

	@ByteField(index = 3)
	private String magicData;

	@ByteField(index = 4)
	private String packageList;

	@ByteField(index = 5)
	private int showTimes;

	@ByteField(index = 6)
	private int protectTimesSeconds;
	
	@ByteField(index = 7)
  private String sspid;
  
  

	public ArrayList<AdInfo> getAdInfoList() {
		return adInfoList;
	}

	public void setAdInfoList(ArrayList<AdInfo> adInfoList) {
		this.adInfoList = adInfoList;
	}

	public String getMagicData() {
		return magicData;
	}

	public void setMagicData(String magicData) {
		this.magicData = magicData;
	}

	public String getPackageList() {
		return packageList;
	}

	public void setPackageList(String packageList) {
		this.packageList = packageList;
	}

	public int getShowTimes() {
		return showTimes;
	}

	public void setShowTimes(int showTimes) {
		this.showTimes = showTimes;
	}

	public int getProtectTimesSeconds() {
		return protectTimesSeconds;
	}

	public void setProtectTimesSeconds(int protectTimesSeconds) {
		this.protectTimesSeconds = protectTimesSeconds;
	}

	public String getSspid() {
    return sspid;
  }

  public void setSspid(String sspid) {
    this.sspid = sspid;
  }

  @Override
  public String toString() {
    return "GetExitResp [adInfoList=" + adInfoList + ", magicData=" + magicData + ", packageList=" + packageList + ", showTimes=" + showTimes
        + ", protectTimesSeconds=" + protectTimesSeconds + ", sspid=" + sspid + "]";
  }
  
}
