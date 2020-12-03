package com.packeteer.network;

/**
 * An interface for PacketBlock handlers
 * @author Alexandre
 *
 */
public interface BlockHandler{

	public void Handle(PacketBlockInfo bd) throws Exception;
}
