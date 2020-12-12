package com.packeteer.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
	public PacketOutputStream(OutputStream out) {
		super(out);
	}

	/**
	 * Writes a packet to the stream
	 * @param p
	 * @throws IOException
	 */
	@Deprecated
	public void write(Packet p) throws IOException{
		write(p.getHeader());
		this.write(p.getPayload());
	}
	
	public void writeAndFlush(Packet p) throws IOException{
		write(p);
		flush();
	}
	
	/**
	 * Writes a packet header to the stream
	 * @param h
	 * @throws IOException
	 */
	@Deprecated
	public void write(PacketHeader h) throws IOException{
		if(h == null)
			throw new IllegalArgumentException("PacketHeader argument cannot be null");
		
		this.writeInt(h.payloadLength);
		this.writeByte(h.type);
	}
	
	public void writeAndFlush(PacketHeader h) throws IOException{
		write(h);
		flush();
	}
	
	/**
	 * Writes a network formated string to the stream
	 * @param string
	 * @throws IOException
	 */
	@Deprecated
	public void write(String string) throws IOException{
		if (string == null)
			throw new IllegalArgumentException("String argument cannot be null");
				
		byte[] bytes = string.getBytes();
		writeInt(bytes.length);
		write(bytes);
	}
	
	public void writeAndFlush(String string) throws IOException{
		write(string);
		flush();
	}
}
