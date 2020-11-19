package com.metransfert.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.metransfert.network.Packet;
import com.metransfert.network.Utils;
import com.metransfert.network.exceptions.FileTooLargeException;

public class MeTransfertPacketTypes {
	
	public static final byte RESERVED = 0;
	
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
	public static final byte FILE = 1;
	
	public static final byte FILERESULT = 3;
	
	/**
	 * Type ID of a get packet
	 */
	public static final byte REQFILE = 2;
	
	
	public static final byte REQINFO = 4;
	
	public static final byte INFORESULT = 5;
	
	/**
	 * -Reads a file and encapsulate its content in a file packet format (cf. Documentation. File packet payload layout)
	 * -Returns a newly created Packet with the file packet payload
	 * @param file The file to encapsulate in a Packet
	 * @return The newly created packet
	 * @throws FileTooLargeException if the given file's size exceeds the protocol limit (> 2^4 bytes)
	 * @throws IOException if any exception thrown by the file reading process
	 * @throws NullPointerException if the given File argument was null
	 */
	public static Packet createFilePacket(File file) throws FileTooLargeException, IOException { 
		
		if(file == null)
			throw new NullPointerException("File argument cannot be null");
		
		FileInputStream fis = new FileInputStream(file);
		int nameStrLen = Utils.calculateNetworkStringLength(file.getName());
		int fileLen = (int)file.length();
		
		int totalPayloadLen = nameStrLen + fileLen;
		
		if((long)nameStrLen + file.length() > Integer.MAX_VALUE){ 
			fis.close();
			throw new FileTooLargeException("File larger than 4096 Mo not supported yet");
		}
		//allocate payloadBuffer with correct size 
		ByteBuffer payloadBuffer = ByteBuffer.allocate(totalPayloadLen);
		
		Utils.writeNetworkString(payloadBuffer, file.getName());
		
		int i=0;
		while( (i=fis.read()) != -1 ){
			payloadBuffer.put((byte)i);
		}
		fis.close();
		
		return new Packet(MeTransfertPacketTypes.FILE, payloadBuffer.array());
	}
}
