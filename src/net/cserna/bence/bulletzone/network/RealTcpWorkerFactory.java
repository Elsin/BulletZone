package net.cserna.bence.bulletzone.network;

import java.io.IOException;
import java.net.Socket;

import net.cserna.bence.bulletzone.core.ActionDispatcher;
import net.cserna.bence.bulletzone.core.BinaryConnector;
import net.cserna.bence.bulletzone.network.protobuf.BulletZoneProtoConnector;

import com.google.inject.Inject;

public class RealTcpWorkerFactory implements TcpWorkerFactory {

	private final ActionDispatcher dispatcher;

	@Override
	public TcpWorker create(Socket socket, OnConnectionClosed connectionClosed)
			throws IOException {
		BulletZoneProtoConnector bulletZoneProtoConnector = new BulletZoneProtoConnector(
				dispatcher);

		return new TcpWorker(bulletZoneProtoConnector, socket, connectionClosed);
	}

	@Inject
	public RealTcpWorkerFactory(ActionDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

}
