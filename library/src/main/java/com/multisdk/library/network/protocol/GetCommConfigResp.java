package com.multisdk.library.network.protocol;

import com.multisdk.library.network.obj.InitInfo;
import com.multisdk.library.network.serializer.ByteField;
import com.multisdk.library.network.serializer.CommRespBody;
import com.multisdk.library.network.serializer.SignalCode;
import java.util.ArrayList;

@SignalCode(messageCode = 2)
public class GetCommConfigResp extends CommRespBody{

  @ByteField(index = 2)
  private ArrayList<InitInfo> infoArrayList;

  public ArrayList<InitInfo> getInfoArrayList() {
    return infoArrayList;
  }

  public void setInfoArrayList(ArrayList<InitInfo> infoArrayList) {
    this.infoArrayList = infoArrayList;
  }
}
