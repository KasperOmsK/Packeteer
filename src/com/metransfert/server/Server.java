package com.metransfert.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import com.metransfert.server.exceptions.ConfigurationParseException;

public class Server {
		
	public final static int PORT = 7999;
	public final static String IP = ""; //if non empty, specifies the IP of the interface that should listen
	
	private static final String CONFIG_DIRECTORY = ".";

	
	public static void main(String[] args) {

		try {
			System.out.println("Server start");
			ServerConfiguration config = new ServerConfiguration(".", "store", 60, 10, PORT);
			Server s = new Server(config);
			s.run();
		} catch (IOException e) {
			System.out.println("Could not create server...");
			e.printStackTrace();
		}
	}
	
	//=-=-=-=-=-=-=-=-=-= SERVER CLASS =-=-=-=-=-=-=-=-=-=
	
	private int port;
	private ServerSocket serverSocket;
	private ServerConfiguration currentConfig;

	
	//=-=-=-=-=-=-=-=-=-= CONSTRUCTORS =-=-=-=-=-=-=-=-=-=
	
	public Server(ServerConfiguration config) throws IOException{
		if(config == null)
			throw new RuntimeException("Argument 'config' cannot be null");
		currentConfig = config;
		
		//check if config is valid
		if(validateConfig() == false){
			System.err.println("Could not start server : configuration error");
		}
		
		//apply config
		this.port = currentConfig.serverPort;
		
		//start server
		serverSocket = new ServerSocket(port);		
	}

	//=-=-=-=-=-=-=-=-=-=-= PUBLIC METHODS =-=-=-=-=-=-=-=-=-=
	
	public void run(){
		while(true){ //TODO: no infinite loop
			try {
				final Socket s = serverSocket.accept();
				ClientThread ct = new ClientThread(this, s); //TODO : store threads in a list
				ct.start();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Could not open connection with client");
			}
		}
	}
	
	public ServerConfiguration getCurrentConfig(){
		return this.currentConfig;
	}
	
	
	/**
	 * Generates a new unused ID for a new store. The ID will be generated according to the server's {@linkplain #currentConfig}
	 * @return A generated string ID
	 */
	public String generateID(){
		int len = currentConfig.IDlength;
		Random r = new Random();
		String s = null;
		do{
			s = "";
			for(int i=0; i<len; i++){
				s += (char)('a' + r.nextInt(26));
			}
		}while(storeExists(s)); //keep generating an ID if it's already used
		return s;
	}


	/**
	 * Automatically generate an unused ID and create the directory.
	 * @return The path to the new directory
	 */
	public Store allocateStore(){
		try {
			String ID = generateID();
			Path p = Paths.get(currentConfig.rootDirectory + currentConfig.storeDirectory + ID);
			return new Store(ID, Files.createDirectories(p));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error creating the store", e);
		}
	}
	
	public File requestFile(String id){
		if(storeExists(id) == false)
			return null;
		File store = new File(currentConfig.rootDirectory, currentConfig.storeDirectory + id);
		String[] list = store.list();
		if(list.length == 0)
			return null;
		
		return new File(list[0]);
	}
	
	//=-=-=-=-=-=-=-=-=-=-= PRIVATE METHODS =-=-=-=-=-=-=-=-=-=

	public boolean storeExists(String id) {
		return Files.exists(Paths.get(currentConfig.rootDirectory + currentConfig.storeDirectory + id));
	}
	
	/**
	 * Will fail if :
	 * <p>
	 * - root directory does not exist
	 * @return
	 * @throws ConfigurationParseException
	 */
	private boolean validateConfig() throws ConfigurationParseException{
		//check root directory config
		File rootDir = new File(currentConfig.rootDirectory);
		if(rootDir.exists() == false || rootDir.isDirectory() == false)
			throw new ConfigurationParseException("The specified root directory '" + currentConfig.rootDirectory + "' does not exist"); //TODO find a more appropriate name for ConfigurationParseException 
		
		return true;
	}
	
}
