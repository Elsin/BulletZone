package net.cserna.bence.bulletzone.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.eventbus.EventBus;

public abstract class FieldEntity {

	protected static final EventBus eventBus = new EventBus();
	protected FieldHolder parent;

	/**
	 * Serializes the current {@link FieldEntity} instance.
	 * 
	 * @return Integer representation of the current {@link FieldEntity}
	 */
	public abstract int getIntValue();

	public FieldHolder getParent() {
		return parent;
	}

	public void setParent(FieldHolder parent) {
		this.parent = parent;
	}

	public abstract FieldEntity copy();

	public void hit(int damage) {
	}

	public static final void registerEventBusListener(Object listener) {
		checkNotNull(listener);
		eventBus.register(listener);
	}

	public static final void unregisterEventBusListener(Object listener) {
		checkNotNull(listener);
		eventBus.unregister(listener);
	}

}
