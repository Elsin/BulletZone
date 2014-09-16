package net.cserna.bence.bulletzone.network;

import java.io.IOException;
import java.net.Socket;

public interface TcpWorkerFactory {
	public TcpWorker create(Socket socket, OnConnectionClosed connectionClosed) throws IOException;
}
