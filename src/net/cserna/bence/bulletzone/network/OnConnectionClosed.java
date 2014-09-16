package net.cserna.bence.bulletzone.network;

public interface OnConnectionClosed {
	void closed(TcpWorker worker);
}
