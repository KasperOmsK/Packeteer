package com.metransfert.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Connection {
	
	private OutputStream out;
	private InputStream in;
	private Socket underlyingSocket;
	
	public Connection(Socket sock) throws IOException{
		if(sock == null)
			throw new NullPointerException("Argument 'Socket' cannot be null");
		
		this.underlyingSocket = sock;
		this.out = sock.getOutputStream();
		this.in = sock.getInputStream();
	}
	
	public Packet readPacket() throws IOException{
		byte[] templen = new byte[4];
		in.read(templen, 0, 4);
		ByteBuffer buffLen = ByteBuffer.wrap(templen);
		int packetLength = buffLen.getInt();
		byte type = (byte)in.read();
		byte[] data = new byte[packetLength-1];
		in.read(data, 0, packetLength-1);
		
		return new Packet(type, data);
	}
	
	public void writePacket(Packet p) throws IOException{
		if(p == null)
			throw new NullPointerException("Argument 'Packet' cannot be null");
		
		p.write(out);
	}
}
