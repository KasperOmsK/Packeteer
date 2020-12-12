package com.packeteer.network;

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
		int read = 0;
		while(read < h.payloadLength){
			data[read] = readByte();
			read++;
		}
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
		
		if(payloadLen < 0)
			throw new RuntimeException("Packet larger than 2Gb aren't supported yet. This is not a normal error that should ever happen during program's usage. This is just to prevent execution to continue because it'll crash anyway.");
		
		byte type = (byte)readByte();
		return new PacketHeader(payloadLen, type);
			
	}
	
	/**
	 * Mark the current position with 5 byte storage, read 5 bytes as a packet header and reset the position to the marked position
	 * <p> NOTE: this method will ALWAYS reset to the marked position, even if an exception occurred
	 * @return The read header
	 * @throws IOException If any I/O error occurred
	 */
	public PacketHeader peekHeader() throws IOException{	
		mark(Packet.HEADER_LEN);
		try{
			PacketHeader h = readHeader();
			return h;
		}catch(Exception e){
			throw e;
		}
		finally{			
			reset();
		}
	}
	
	/**
	 * Reads a packet by block instead of doing it in one go.
	 * Everytime a block is read, the reading process will be interrupted and the handler's "Handle" method will be called
	 * <p>
	 * NOTE : this method will block until a packet could be read
	 * @param handler A BlockHandler interface
	 * @param block_size The size of a block
	 * @throws Exception : throws back any Exception that was thrown by the handler
	 * @throws IOException if an I/O error occured
	 */
	public void readPacket(BlockHandler handler, int block_size) throws Exception{ 
		if(handler == null)
			throw new IllegalArgumentException("handler cannot be null");
		
		PacketHeader h = this.readHeader();
		int remaining = h.payloadLength;
		int segment = 0;
		final int total_segments = (int) Math.ceil( (double)remaining / block_size );
		
		byte[] data_in = new byte[block_size];
		
		while(remaining > 0){
			int currentSize = Integer.min(remaining, block_size);
						
			int nRead = read(data_in);
			
			//Si on a recu moins que blockLen, on resize data_in
			if(nRead < currentSize){
				byte[] tmp = new byte[nRead];
				for(int i=0; i<nRead; i++)
					tmp[i] = data_in[i];
				
				data_in = tmp;
			}
			
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
		if(len < 0)
			return null;
		
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
