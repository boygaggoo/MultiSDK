package com.multisdk.library.network.serializer;

import android.util.SparseArray;

public class MessageRecognizer {

  private static SparseArray<Class> mMessageClasses = new SparseArray<>();

  static {
    //mMessageClasses.put(100001, GetZoneServerReq.class);
    //mMessageClasses.put(200001, GetZoneServerResp.class);
    //mMessageClasses.put(101001, GetCommonConfigReq.class);
    //mMessageClasses.put(201001, GetCommonConfigResp.class);
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
