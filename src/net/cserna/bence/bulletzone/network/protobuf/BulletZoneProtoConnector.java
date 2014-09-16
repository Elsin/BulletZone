package net.cserna.bence.bulletzone.network.protobuf;

import static com.google.common.base.Preconditions.checkNotNull;
import net.cserna.bence.bulletzone.action.Action;
import net.cserna.bence.bulletzone.action.ActionReply;
import net.cserna.bence.bulletzone.core.ActionDispatcher;
import net.cserna.bence.bulletzone.core.BinaryConnector;
import net.cserna.bence.bulletzone.core.BulletZoneConnection;
import net.cserna.bence.bulletzone.network.BinarySender;

import com.google.inject.Inject;

public class BulletZoneProtoConnector implements BinaryConnector, BulletZoneConnection {
	
	private final ActionDispatcher dispatcher;
	private boolean isRegistered = false;
	private BinarySender sender;
	
	@Inject
	public BulletZoneProtoConnector(ActionDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	@Override
	public void processMessage(byte[] data) {
		checkNotNull(data);
		
		if (!isRegistered) {
			// Called by the receiver thread
			dispatcher.registerConnection(this);
			isRegistered = true;
		}
		
		// Send it to dispatcher
		Action action = MessageConverter.byteToAction(data);
		if (action != null) {
			dispatcher.handleAction(action);
		} else {
			// TODO 
		}
		
	}
	
	@Override
	public void handle(ActionReply reply) {
		checkNotNull(reply);
		
		byte[] data = MessageConverter.actionToByte(reply);
		
		if (sender != null && data != null) {
			sender.sendMessage(data);
		} else {
			// TODO 
		}
	}

	@Override
	public void setBinarySender(BinarySender sender) {
		checkNotNull(sender);
		this.sender = sender;
	}

}
