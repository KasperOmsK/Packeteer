package com.metransfert.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.metransfert.network.BlockHandler;
import com.metransfert.network.Packet;
import com.metransfert.network.PacketBlockInfo;
import com.metransfert.network.PacketBuilder;
import com.metransfert.network.PacketHeader;
import com.metransfert.network.PacketInputStream;
import com.metransfert.network.PacketOutputStream;
import com.metransfert.network.Utils;

public class ClientThread extends Thread {
	
	private Socket socket;
	private Server server;
	PacketInputStream in;
	PacketOutputStream out;

	
	public ClientThread(Server server, Socket socket) throws IOException {
		this.socket = socket;
		this.server = server;
		this.in = new PacketInputStream(new BufferedInputStream(socket.getInputStream()));
		this.out = new PacketOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	
	@Override
	public void run() {
		try{
			System.out.println("Client connecté !");
	
			in.mark(Integer.MAX_VALUE);
			PacketHeader header = in.readHeader();
			in.reset();
			
			try{
				switch(header.type){
					case MeTransfertPacketTypes.FILE: handleFilePacket(header); break;
					case MeTransfertPacketTypes.REQFILE: handleRequestFile(header); break;
				}
			}
			catch(Exception e){
				throw e;
			}finally{
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Fin thread client");
	}

	
	
	private void handleFilePacket(PacketHeader h) throws IOException{	
		in.mark(Integer.MAX_VALUE);
		in.readHeader(); //on skip le header
		String fileName = in.readString(); //on récupére le nom de fichier
		in.reset();
		
		if(!validateFileName(fileName)){
			System.err.println("filename could be validated");
			//send an error the client
			
			//flush the next packet in the inputStream
			in.readPacket(); //TODO : find another way to flush the packet ?
			
			return;
		}
		
		Store newStore = server.allocateStore();
		File newFile = new File(newStore.path.toFile(), fileName);
		newFile.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(newFile);

		try{
			//read file packet and write it to the new file
			in.readPacket(new BlockHandler() {
				@Override
				public void Handle(PacketBlockInfo blockInfo) {
					int start = 0;
					if(blockInfo.segmentNumber == 0){ //it's the first segment, so the first bytes are actually the filename
						ByteBuffer bf = ByteBuffer.wrap(blockInfo.data);
						start = 4+bf.getInt();
					}
					try {
						fos.write(blockInfo.data, start, blockInfo.size-start);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
	
			//answer
			Packet fileResult = PacketBuilder.newBuilder(MeTransfertPacketTypes.FILERESULT).build();
			
			//out.write(np);
			out.flush();
			
			}catch(Exception e){
				e.printStackTrace();
				System.err.println("File could not be downloaded or saved.");
			}
			finally{
				fos.close();
			}
			
	}
	
	private void handleRequestFile(PacketHeader h){
		try {
			Packet p = in.readPacket();
			ByteBuffer b = p.getPayloadBuffer();
			String reqId = Utils.readNetworkString(b);
			System.out.println("Client requested ID : " + reqId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean validateFileName(String fileName) {
		return true;
	}
	
}
