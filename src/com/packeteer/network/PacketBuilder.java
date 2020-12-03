package com.packeteer.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PacketBuilder {
	private final byte type;
	ArrayList<Object> fields = new ArrayList<>();
	
	public PacketBuilder(byte type){
		this.type = type;
	}
	
	public PacketBuilder write(String s){
		fields.add(s);
		return this;
	}
	
	public PacketBuilder writeInt(int i){
		fields.add(i);
		return this;
	}
	
	public PacketBuilder writeByte(int b){
		fields.add((byte)b);
		return this;
	}
	
	public PacketBuilder write(byte[] d){
		fields.add(d);
		return this;
	}
	
	public Packet build(){
		int payLen = 0;
		int i= 0;
		for(Object b : fields){
			if(b instanceof String)
				payLen += PacketUtils.calculateNetworkStringLength((String)b);
			else if(b instanceof Integer)
				payLen += 4;
			else if(b instanceof Byte)
				payLen += 1;
			else if(b instanceof byte[])
				payLen += ((byte[])b).length;
			else
				throw new RuntimeException("Invalid field type at position " + i + " (" + b.toString() + ")" );
			i++;
		}
		
		ByteBuffer newBuffer = ByteBuffer.allocate(payLen);
		for(Object b : fields){
			if(b instanceof String)
				PacketUtils.writeNetworkString(newBuffer, (String)b);
			else if(b instanceof Integer)
				newBuffer.putInt((Integer)b);
			else if(b instanceof Byte)
				newBuffer.put((Byte)b);
			else if(b instanceof byte[]){
				byte[] array = (byte[])b;
				for(int c=0; c<array.length; c++){
					newBuffer.put(array[c]);
				}
			}
		}
		
		return new Packet(type, newBuffer.array());
	}

	public static PacketBuilder newBuilder(byte type){
		return new PacketBuilder(type);
	}
}
