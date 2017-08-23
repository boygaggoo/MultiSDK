package com.mf.basecode.network.protocol;

import java.util.ArrayList;

import com.mf.basecode.network.object.GameServerBto;
import com.mf.basecode.network.serializer.ByteField;
import com.mf.basecode.network.serializer.MFCom_ResponseBody;
import com.mf.basecode.network.serializer.SignalCode;

@SignalCode(messageCode = 200001, encrypt = true)
public class GetZoneServerResp extends MFCom_ResponseBody {

  /**
   * 
   */
  private static final long        serialVersionUID = 2225714706119399540L;
  @ByteField(index = 2)
  private ArrayList<GameServerBto> zoneServerList   = new ArrayList<GameServerBto>();

  public ArrayList<GameServerBto> getZoneServerList() {
    return zoneServerList;
  }



  public void setZoneServerList(ArrayList<GameServerBto> zoneServerList) {
    this.zoneServerList = zoneServerList;
  }



  @Override
  public String toString() {
    return "GetZoneServerResp [zoneServerList=" + zoneServerList + "]";
  }
}
