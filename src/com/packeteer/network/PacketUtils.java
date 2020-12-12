package com.packeteer.network;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * A class that holds several utilities methods
 * @author Alexandre
 *
 */
public class PacketUtils {
	
	//============ BUFFER HELPER METHODS =============
	
	//TODO: All these helper class for reading/writing custom types on bytebuffers (e.g writeNetworkString, readInt) should not be in a static class like this one. Rather we should have our own CustomByteBuffer class deriving from java.nio.ByteBuffer which would implement theses methods
	
	/**
	 * Reads an integer in the buffer at its current index and then decrement the index by 4
	 * <p>
	 * This method will block until an integer could be read or an exception was thrown
	 * @param buffer
	 * @return the peeked integer
	 * @throws BufferOverflowException If there are fewer than 4 bytes remaining in this buffer
	 */
	public static int peekInt(ByteBuffer buffer){
		try{
			buffer.mark();
			int r = buffer.getInt();
			return r;			
		}
		catch(BufferOverflowException e){
			throw e;
		}
		finally{
			buffer.reset();			
		}
	}
	
	/**
	 * Read a byte in the buffer at its current index and then decrement the index by 1
	 * <p>
	 * This method will block until an integer could be read or an exception was thrown
	 * @param buffer
	 * @return the peeked byte
	 * @throws BufferUnderflowException If there are fewer than 1 byte remaining in this buffer
	 */
	public static byte peekByte(ByteBuffer buffer){
		try{
			buffer.mark();
			byte b = buffer.get();
			return b;			
		}
		catch(BufferUnderflowException e){
			throw e;
		}
		finally{
			buffer.reset();			
		}
	}
	

	// ============= NETWORK HELPER METHODS ====================
	
	/**
	 * Attempts to write a network formatted string to the given ByteBuffer
	 * @param buffer The bufer to write to
	 * @param string The string to write
	 * @throws NullPointerException if any of the given input is null
	 */
	public static void writeNetworkString(ByteBuffer buffer, String string) {
		if (buffer == null)
			throw new IllegalArgumentException("Bytebuffer argument cannot be null");
		if (string == null)
			throw new IllegalArgumentException("String argument cannot be null");
				
		//NOTE : Pas besoin de vérifier si 'b' est assez grand avant d'écrire car b.putInt ci dessous va renvoyer une runtime exception si jamais ce nest pas le cas
		byte[] bytes = string.getBytes();
		buffer.putInt(bytes.length); //TODO : faire attention à l'encodage ici
		buffer.put(string.getBytes());
	}

	/**
	 * Calculates how many bytes a string will take when sent over the network.
	 * <p>
	 * Note : This takes into account the 4 bytes that encode the string length
	 * @param string The string
	 * @return The number of bytes
	 * @throws NullPointerException if the given string input is null
	 */
	public static int calculateNetworkStringLength(String string) {
		if (string == null)
			throw new IllegalArgumentException("String argument cannot be null");

		return 4+string.getBytes().length; // 4 bytes for len (int) + number of bytes
										// char
	}

	/**
	 * Attempts to read a network formated string from a ByteBuffer (cf. Documentation for network string formatting)
	 * <p>
	 * This method will blocks until a string could be read, or an exception is thrown
	 * @param buffer An existing ByteBuffer
	 * @return The read string
	 * @throws BufferUnderflowException if the buffer end is reached before a string is read
	 */
	public static String readNetworkString(ByteBuffer buffer) {
		String s = ""; //TODO: this should not throw a BufferUnderflowException but a EOFException (because BufferUnderflow is a runtime exception, but we expect callers to handle error when the buffer is too small to read a string
		int len = buffer.getInt();
		for (int i = 0; i < len; i++) {
			s += (char)buffer.get(); //TODO : faire attention à l'encodage ici, si la chaine contient un character plus grand que 1 octet, ça va afficher nimorte quoi
		}
		return s;
	}
	
	/**
	 * Attempts to read a network formated string from an InputStream (cf. Documentation for network string formatting)
	 * <p>
	 * This method will blocks until a string could be read, or an exception is thrown
	 * @param is an InputStream
	 * @return The read string
	 * @throws IOException if the reading process threw an exception
	 * @throws EOFException if the end of the stream was reached unexpectedly
	 */
	public static String readNetworkString(InputStream is) throws IOException, EOFException{
		
		int len = PacketUtils.readInt(is);
		String s = "";
		int i;
		while(s.length() < len){
			i = is.read();
			if(i == -1)
				throw new EOFException("Input stream end reached unexpectedly before a string could be read");
			
			s += (char)i;
		}
		return s;
	}
	
	/**
	 * Attempts to read a network formated string from an InputStream at the current index and decrement the index by the number of byte read
	 * <p>
	 * This method will blocks until a string could be read, or an exception is thrown
	 * @param buffer a ByteBuffer
	 * @return The peeked string
	 * @throws BufferUnderflowException if the buffer end is reached before a string could be read read
	 */
	public static String peekNetworkString(ByteBuffer buffer){
		try{
			buffer.mark();
			String s = readNetworkString(buffer);
			return s;			
		}
		catch(BufferUnderflowException e){
			throw e;
		}
		finally{
			buffer.reset();			
		}
	}
	
	
	// ============ INPUT STREAM HELPER METHODS ======================

	/**
	 * Attempts to read an integer from the inputStream
	 * <p>
	 * This method will blocks until an integer could be read, or an exception is thrown
	 * @param is
	 * @return The read integer
	 * @throws IOException if any I/O error occured
	 * @throws EOFException if fewer than 4 bytes could be read
	 */
	public static int readInt(InputStream is) throws EOFException, IOException{	
		DataInputStream dis = new DataInputStream(is);
		return dis.readInt();
		
	}
	
	// ===================== MISC HELPER METHODS ======================
	
	/**
	 * Prints a byte array in an hexdump fashion. Fancy !
	 * @param array an array of bytes
	 */
	public static void hexDump(byte[] array) {
		System.out.println("================= HEX DUMP (" + array.length + " bytes) =================");

		boolean nl = true;
		int splitCounter = 0;

		String interp = "";

		boolean printDecoding = false;

		for (int i = 0; i < array.length; i++) {
			if (i != 0 && i % 16 == 0) { // end of line

				if (printDecoding)
					System.out.println("   " + interp);
				else
					System.out.println();

				interp = "";

				nl = true;
				splitCounter = 0;
			}

			if (splitCounter != 0 && (splitCounter % 8 == 0)) { // split
				System.out.print("  ");
			}

			if (nl)
				splitCounter++;

			System.out.printf("%1$02X ", array[i]);
			interp += (char) array[i];

		}
		System.out.println("\n=================================================\nPrinted " + array.length + " bytes");
	}


}
