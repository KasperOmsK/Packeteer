package com.metransfert.network;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Packet {
	
	public static final byte PACKETTYPE_TEST = 0;
	public static final byte PACKETTYPE_GET = 1;
	
	private int dataLength; //len(data) + len(type) = len(data)+1
	private ByteBuffer buffer = null;
	private byte packetType = 0;
	
	public Packet(byte type, byte[] data){
		if(data == null)
			throw new NullPointerException("Argument 'byte[] data' cannot be null");
		this.packetType = type;
		this.buffer = ByteBuffer.wrap(data);
	}
	
	public void write(OutputStream stream) throws IOException{
		if(stream == null)
			throw new NullPointerException("Argument 'OutputStream stream' cannot be null");
		
		ByteBuffer lenBuffer = ByteBuffer.allocate(4);
		lenBuffer.putInt(buffer.limit() + 1); //On encapsule la taille dans un ByteBuffer pour qu'il gère le boutisme lui même
		stream.write(lenBuffer.array()); 	//on envoie la taille totale
		stream.write(this.packetType);		//on envoie le type de packet
		stream.write(this.buffer.array());	//on envoie les données encapsulés
	}
	
	public byte getType(){
		return this.packetType;
	}
	
	public byte[] getUnderlyingData(){
		return buffer.array();
	}
}
