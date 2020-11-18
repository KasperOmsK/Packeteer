package com.metransfert.network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * A Stream to read Packet data (Packet, headers, network formatted strings...)
 * @author Alexandre
 *
 */
public class PacketInputStream extends DataInputStream {

	public static final int BLOCK_SIZE = 4096;
	
	/**
	 * Constructs a PacketInputStream encapsulating a given BufferedInputStream
	 * @param in
	 */
	public PacketInputStream(BufferedInputStream in) {
		super(in);
	}
	
	/**
	 * Reads an entire packet
	 * <p>
	 * NOTE : this method will block until a packet header could be read
	 * @return the read packet
	 * @throws IOException in any I/O error occured
	 */
	public Packet readPacket() throws IOException{
		PacketHeader h = readHeader();
		byte[] data = new byte[h.payloadLength];
		read(data, 0, h.payloadLength);
		return new Packet(h.type, data);
	}
	
	/**
	 * Reads a packet header (packet size(4 bytes) + type(1 byte))
	 * <p>
	 * NOTE : this method will block until a packet header could be read
	 * @return the read header
	 * @throws IOException in any I/O error occured
	 */
	public PacketHeader readHeader() throws IOException{
		int payloadLen = readInt();
		byte type = (byte)readByte();
		return new PacketHeader(payloadLen, type);
	}
	
	
	/**
	 * Reads a packet by block instead of in one go.
	 * Everytime a block is read, the reading process will be interrupted and the handler's "Handle" method will be called
	 * <p>
	 * NOTE : this method will block until a packet could be read
	 * @param handler A BlockHandler interface
	 * @throws Exception : throws back any Exception that was thrown by the handler
	 * @throws IOException if an I/O error occured
	 */
	public void readPacket(BlockHandler handler) throws Exception{ //TODO: passer le BLOCK_SIZE en paramètres ?
		if(handler == null)
			throw new NullPointerException("handler cannot be null");
		
		PacketHeader h = this.readHeader();
		int remaining = h.payloadLength;
		int block_size = BLOCK_SIZE;
		int segment = 0;
		final int total_segments = (int) Math.ceil( (double)remaining / block_size );
		
		while(remaining > 0){
			int blockLen = Integer.min(remaining, block_size);
			byte[] data_in = new byte[blockLen];
			int nRead = read(data_in);
			PacketBlockInfo bd = new PacketBlockInfo(h, block_size, segment, total_segments, data_in, remaining-nRead);
			handler.Handle(bd);
			segment++;
			
			remaining -= nRead;
		}
	}
	
	/**
	 * Reads a string from the stream
	 * @return the read string
	 * @throws IOException if any I/O error occured
	 * @throws EOFException in the end of stream was reached before a string could be read
	 */
	public String readString() throws IOException{
		int len = readInt();
		String s = "";
		int i;
		while(s.length() < len){
			i = read();
			if(i == -1)
				throw new EOFException("Input stream end reached unexpectedly before a string could be read");
			
			s += (char)i;
		}
		return s;
	}
	
}
