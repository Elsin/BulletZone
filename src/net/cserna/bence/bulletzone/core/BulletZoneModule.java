package net.cserna.bence.bulletzone.core;

import com.google.inject.AbstractModule;

public class BulletZoneModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ActionDispatcher.class).to(ThreadBasedActionDispatcher.class).asEagerSingleton();
		bind(FieldManagerCore.class);
		bind(AsyncFieldManager.class);
		bind(BulletZoneServer.class).to(BulletZoneLocalProxy.class);
		
	}
}
