package net.cserna.bence.bulletzone.network;

import net.cserna.bence.bulletzone.core.BinaryConnector;
import net.cserna.bence.bulletzone.network.protobuf.BulletZoneProtoConnector;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class NetworkModule extends AbstractModule implements Module {

	@Override
	protected void configure() {

		// PORT BINDING
		
		// Server port
		bind(Integer.class).annotatedWith(Port.class).toInstance(3359);
		// BinaryConnector implementation
		bind(BinaryConnector.class).to(BulletZoneProtoConnector.class);
		// TcpWorker factory
		bind(TcpWorkerFactory.class).to(RealTcpWorkerFactory.class);
	}

}
