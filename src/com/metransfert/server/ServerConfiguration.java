package com.metransfert.server;

import java.io.File;

public class ServerConfiguration {
	
	// [SERVICE]
	
	/**
	 * A string representing the path the root directory (i.e where everything will be stored/read from
	 * <p>
	 * NOTE : The string should end with a forward slash '/'. If the given string does not end properly, a '/' will be appended to the end upon creation
	 */
	public final String rootDirectory;
	public final String storeDirectory;
	public final int defaultLeaseTime; //in seconds
	public final int IDlength;
	
	//[NETWORK]
	
	public final int serverPort;
	
	
	public ServerConfiguration(String rootDirectory, String storeDir, int defaultLeaseTime, int iDlength, int port) {
		rootDirectory = directorify(rootDirectory);
		storeDir = directorify(storeDir);
		this.rootDirectory = rootDirectory;
		this.storeDirectory = storeDir;
		this.defaultLeaseTime = defaultLeaseTime;
		IDlength = iDlength;
		
		
		this.serverPort = port;
	}
	
	public static ServerConfiguration loadFromFile(File configFile){
		throw new RuntimeException("Not implemented yet");
	}
	
	private String directorify(String dirPath){
		if(dirPath.endsWith("/") == false)
			dirPath += "/";
		return dirPath;
	}
}
