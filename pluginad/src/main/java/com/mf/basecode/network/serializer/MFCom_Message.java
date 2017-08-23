package com.mf.basecode.network.serializer;


public class MFCom_Message {
	// 1 byte
	@ByteField(index = 1)
	public MFCom_MessageHead head;

	@ByteField(index = 2)
	public Object message;

	public int retryCount = 0;

}
