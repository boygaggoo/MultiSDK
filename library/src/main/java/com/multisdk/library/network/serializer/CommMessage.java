package com.multisdk.library.network.serializer;

public class CommMessage {

  // 1 byte
  @ByteField(index = 1) public CommMessageHead head;

  @ByteField(index = 2) public Object message;

  public int retryCount = 0;
}
