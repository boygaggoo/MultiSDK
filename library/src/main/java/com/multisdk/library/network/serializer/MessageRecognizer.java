package com.multisdk.library.network.serializer;

import android.util.SparseArray;
import com.multisdk.library.network.protocol.GetCommConfigReq;
import com.multisdk.library.network.protocol.GetCommConfigResp;

public class MessageRecognizer {

  private static SparseArray<Class> mMessageClasses = new SparseArray<>();

  static {
    mMessageClasses.put(1, GetCommConfigReq.class);
    mMessageClasses.put(2, GetCommConfigResp.class);
  }

  public static Class getClassByCode(int code) {
    if (mMessageClasses.valueAt(code) != null) {
      return mMessageClasses.get(code);
    }
    return null;
  }

  public static boolean addClass(Class cls) {
    SignalCode sc = AttributeUtil.getMessageAttribute(cls);
    if (sc != null) {
      if (mMessageClasses.valueAt(sc.messageCode()) == null) {
        mMessageClasses.put(sc.messageCode(), cls);
        return true;
      }
    }
    return false;
  }
}
