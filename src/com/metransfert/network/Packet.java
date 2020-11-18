package com.metransfert.network;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * This class encapsulates an array of binary data to send over the network.
 * It allows for easy serlialisation in order to be sent over the network. (i.e automatic header creation)
 * 
 * <p>
 * General Packet layout :
 * <pre>
 * Packet layout : this describes the general layout of every packet.
 *<=========== HEADER ===========><==================PAYLOAD/DATA=======================>  
 *<================================ N bytes ============================================>  N = payload_len + header_len
 *+-----------------+------------+------------------------------------------------------+
 *| Int payload_len | Byte type  |                 Byte[] packet payload                |
 *+-----------------+------------+------------------------------------------------------+
 *<=====4 bytes=====><==1 byte==><=============== payload_len bytes ====================>
 * </pre>
 * 
 * @author Alexandre
 */
public class Packet {
	
	/**
	 * the size of all packets header (4 bytes for the len + 1 byte for the type)
	 */
	public static final int HEADER_LEN = 5; 
	
	public static final byte PACKETTYPE_RESERVED = 0;
	
	/**
	 * Type ID of a file packet
	 * <pre>
	 * File packet:
	 * 	ID : 1
	 * 	Direction : Client <---> Server
	 *	Description : This packet will encapsulate file transfered between client and server (For uploads and downloads)
	 * 
	 * 'File' packet data layout : 
	 *<============ payload_len bytes ==============> (N < 2^4)
	 *+----------------+----------------------------+
	 *| Str fileName   |        Byte[] data         |
	 *+----------------+----------------------------+
	 *<====n bytes=====><======= N-n bytes =========>
	 * </pre>
	 */
	public static final byte PACKETTYPE_FILE = 1;
	public static final byte PACKETTYPE_GET = 2;
	public static final byte PACKETTYPE_UPLOAD_RESULT = 3;
	public static final byte PACKETTYPE_INFO = 4;
	
	private ByteBuffer payloadBuffer = null;
	private byte packetType = 0;
	private int payloadLength;
	
	/**
	 * Create a new packet of specified type with given payload.
	 * <p>
	 * NOTE : The Packet does not copy the given data, it stores a reference. Modifying the array will cause the Packet to be modified and vice-versa
	 * @param type The type ID of the Packet
	 * @param payload The useful data of the Packet (data minus header)
	 * @throws NullPointerException if the given payload is null
	 */
	public Packet(byte type, byte[] payload){
		if(payload == null)
			throw new NullPointerException("Argument 'byte[] payload' cannot be null");
		
		try{
			Math.addExact(payload.length, HEADER_LEN);
		}catch(ArithmeticException e){
			
		}
		
		this.packetType = type;
		this.payloadBuffer = ByteBuffer.wrap(payload);
				
		this.payloadLength = payload.length;
	}
	
	/**
	 * Serializes and writes the Packet to the given OutputStream
	 * @param stream
	 * @throws IOException if the writing process threw an IOException
	 * @throws NullPointerException if the given OutputStream is null
	 */
	@Deprecated
	public void writeTo(OutputStream stream) throws IOException{
		if(stream == null)
			throw new NullPointerException("Argument 'OutputStream stream' cannot be null");
		
		ByteBuffer payloadLenBuffer = ByteBuffer.allocate(4);
		//TODO : Gérer le boutisme pour l'envoie sur réseau cf classe DataOutputStream 
		
		payloadLenBuffer.putInt(payloadBuffer.limit()); 	//On encapsule la taille de la payload dans un ByteBuffer pour qu'il gère le boutisme automatiquement 
		
		// ===== envoie du header
		stream.write(payloadLenBuffer.array()); 	//on envoie la taille de la payload
		stream.write(this.packetType);				//on envoie le type de packet
	
		// ====== envoie de la payload
		stream.write(this.payloadBuffer.array());	//on envoie les données encapsulés
	}
	
	/**
	 * Returns the type ID of the packet (see Documentation for existing packet types)
	 * @return This packet's type ID
	 */
	public byte getType(){
		return this.packetType;
	}
	
	/**
	 * 
	 * @return the length (in bytes) of the encapsulated data (payload), that is the total length of the packet minus the header's length
	 */
	public int getPayloadLength(){
		return payloadLength;
	}
	
	/**
	 * Returns a reference to the underlying data array (payload). Modifying this array will modify the packet's data and vice-versa
	 * @return A reference to the packet's payload data
	 */
	public byte[] getPayload(){
		return payloadBuffer.array();
	}

	/**
	 * Returns a read-only copy of the ByteBuffer wrapping the packet's payload. with index 0
	 * The copied ByteBuffer index is 0.
	 * NOTE : The data itself is not copied. Only the ByteBuffer wrapper object is
	 * @return The read-only copy of the ByteBuffer
	 */
	public ByteBuffer getPayloadBuffer(){
		ByteBuffer readOnlyBuffer = this.payloadBuffer.asReadOnlyBuffer();
		readOnlyBuffer.rewind();
		return readOnlyBuffer;
	}

	/**
	 * Checks whether the given data can be encapsulated into a Packet (i.e can data fit into a Packet (4 GB max with header)
	 * @param data the data to test
	 * @return true if the data can be encapsulated into a Packet, false otherwise
	 * @throws Exception 
	 */
	public static boolean canEncapsulate(byte[] data) throws Exception{
		throw new Exception("Not yet implemented");
		//return true;
	}
}
