package net.cserna.bence.bulletzone.core;

import net.cserna.bence.bulletzone.network.BinarySender;

public interface BinaryConnector {

	public void processMessage(byte[] data);
	public void setBinarySender(BinarySender sender);
	
}
