package com.xdd.pay.network.serializer;


public class MessageC {
	// 1 byte
	@ByteField(index = 1)
	public MessageHead head;

	@ByteField(index = 2)
	public Object message;

	public int retryCount = 0;

}
