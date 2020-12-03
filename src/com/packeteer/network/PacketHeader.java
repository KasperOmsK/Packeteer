package com.packeteer.network;

public class PacketHeader {

	public final byte type; 
	public final int payloadLength; //TODO: Use VarInt ?
	
	public PacketHeader(int payloadLen, byte type) {
		this.payloadLength = payloadLen;
		this.type = type;
	}

}
