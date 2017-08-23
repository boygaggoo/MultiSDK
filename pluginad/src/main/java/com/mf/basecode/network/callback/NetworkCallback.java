package com.mf.basecode.network.callback;

import com.mf.basecode.network.serializer.MFCom_Message;

public interface NetworkCallback {

  public void onResponse(Boolean result, MFCom_Message respMessage);

}
