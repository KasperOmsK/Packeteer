package com.metransfert.network;

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
	
	public PacketBuilder write(int i){
		fields.add(i);
		return this;
	}
	
	public PacketBuilder writeByte(byte b){
		fields.add(b);
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
				payLen += Utils.calculateNetworkStringLength((String)b);
			else if(b instanceof Integer)
				payLen += 4;
			else if(b instanceof Byte)
				payLen += 1;
			else if(b instanceof Byte[])
				payLen += ((Byte[])b).length;
			else
				throw new RuntimeException("Invalid field type at position " + i + " (" + b.toString() + ")" );
			i++;
		}
		
		ByteBuffer newBuffer = ByteBuffer.allocate(payLen);
		for(Object b : fields){
			if(b instanceof String)
				Utils.writeNetworkString(newBuffer, (String)b);
			else if(b instanceof Integer)
				newBuffer.putInt((Integer)b);
			else if(b instanceof Byte)
				newBuffer.put((Byte)b);
			else if(b instanceof Byte[]){
				Byte[] array = (Byte[])b;
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
