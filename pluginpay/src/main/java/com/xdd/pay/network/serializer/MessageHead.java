package com.xdd.pay.network.serializer;

public class MessageHead {
	// 1 byte
	@ByteField(index = 1)
	public byte version;

	// 4 byte
	@ByteField(index = 2)
	public int length;

	// type,reserved,transaction字段交换位置，之前文档都错了
	// 1 byte
	@ByteField(index = 3)
	public byte type;

	// 2 byte reserved
	@ByteField(index = 4)
	public short reserved;

	// 16 byte ,use 4 byte as transaction
	@ByteField(index = 5)
	public long firstTransaction;

	@ByteField(index = 6)
	public long secondTransaction;

	// 4 byte code
	@ByteField(index = 7)
	public int code;
}
