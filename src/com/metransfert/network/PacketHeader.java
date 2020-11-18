package com.metransfert.network;

public class PacketHeader {

	public final byte type;
	public final int payloadLength;
	
	public PacketHeader(int payloadLen, byte type) {
		this.payloadLength = payloadLen;
		this.type = type;
	}

}
