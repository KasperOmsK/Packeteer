package com.metransfert.network.exceptions;

public class FileTooLargeException extends Exception{

	private static final long serialVersionUID = -2040495009642104611L;
	
	public FileTooLargeException(String s){
		super(s);
	}
}
