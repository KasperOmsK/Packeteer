package com.metransfert.network;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A Stream to write Packet data (Packet, headers, network formatted strings...)
 * @author Alexandre
 *
 */
public class PacketOutputStream extends DataOutputStream{

	/**
	 * Constructs a PacketOutputStream encapsulating a given BufferedInputStream
	 * @param in
	 */
	public PacketOutputStream(BufferedOutputStream out) {
		super(out);
	}

	/**
	 * Writes a packet to the stream
	 * @param p
	 * @throws IOException
	 */
	public void write(Packet p) throws IOException{
		writeHeader(p);
		this.write(p.getPayload());
	}
	
	/**
	 * Writes a packet header to the stream
	 * @param p
	 * @throws IOException
	 */
	public void writeHeader(Packet p) throws IOException{
		if(p == null)
			throw new NullPointerException("Packet argument cannot be null");
		
		this.writeInt(p.getPayloadLength());
		this.writeByte(p.getType());
	}
	
	/**
	 * Writes a network formated string to the stream
	 * @param string
	 * @throws IOException
	 */
	public void writeString(String string) throws IOException{
		if (string == null)
			throw new NullPointerException("String argument cannot be null");
				
		byte[] bytes = string.getBytes();
		writeInt(bytes.length);
		write(bytes);
	}

}
