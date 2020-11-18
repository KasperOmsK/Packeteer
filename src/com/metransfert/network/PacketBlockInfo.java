package com.metransfert.network;

public class PacketBlockInfo {
	
	/**
	 * The header of the parent packet
	 */
	public final PacketHeader associatedPacketHeader;
	
	/**
	 * The maximum size this block can have.
	 */
	public final int blockSize;
	
	/*
	 * The segment number of this block (starting at 0 and ending at 'segmentCount-1'
	 */
	public final int segmentNumber;
	
	/*
	 * The number of segment the parent packet packet was sliced in
	 */
	public final int segmentCount;
	
	/*
	 * The underlying data
	 */
	public final byte[] data;
	
	/* 
	 * The size of this block. Same as data.length
	 */
	public final int size;
	
	/*
	 * How many bytes are left after this block
	 */
	public final int left;
	
	public PacketBlockInfo(PacketHeader header, int blockSize, int seq, int total, byte[] data, int leftAfterThis){
		this.associatedPacketHeader = header;
		this.blockSize = blockSize;
		this.segmentNumber = seq;
		this.segmentCount = total;
		this.data = data;
		this.size = data.length;
		this.left = leftAfterThis;
	}
}
