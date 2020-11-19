package com.metransfert.server;

import java.nio.file.Path;

public class Store {
	public final String ID;
	public final Path path;
	
	public Store(String iD, Path path) {
		super();
		ID = iD;
		this.path = path;
	}
}
