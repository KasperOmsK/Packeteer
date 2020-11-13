package com.metransfert.network;

import java.nio.ByteBuffer;

public class Utils {
	
	public static void WriteString(ByteBuffer b, String s) throws Exception{
		if(b == null)
			throw new NullPointerException("given Bytebuffer cannot be null");
		if(s == null)
			throw new NullPointerException("given string cannot be null");
		
		if(b.remaining() < 2*s.length()){
			throw new Exception("ByteBuffer too small...");
		}
		
		b.putInt(2*s.length());
		b.put(s.getBytes());
	}
	
	public static String ReadString(ByteBuffer b){
		if(b == null)
			throw new NullPointerException("given Bytebuffer cannot be null");
		String s = "";
		byte len = b.get();
		for(int i=0; i<len; i++){
			s += b.getChar();
		}
		
		return s;
	}
	
	public static void HexDump(byte[] arr){
		System.out.println("====== HEX DUMP ("+arr.length+" bytes)======");
		for(int i=0; i<arr.length; i++){
			if(i != 0 && i%16 == 0)
				System.out.println();
			System.out.printf("%2x", arr[i]);
		}
		System.out.println("\n=====================\nPrinted " + arr.length + " bytes");
	}
}
