package net.cserna.bence.bulletzone.network.protobuf;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import net.cserna.bence.bulletzone.action.Action;
import net.cserna.bence.bulletzone.action.ActionCreateTank;
import net.cserna.bence.bulletzone.action.ActionFire;
import net.cserna.bence.bulletzone.action.ActionMove;
import net.cserna.bence.bulletzone.action.ActionTurn;
import net.cserna.bence.bulletzone.action.ActionReply;
import net.cserna.bence.bulletzone.entity.Direction;
import net.cserna.bence.bulletzone.protobuf.Bulletzone;
import net.cserna.bence.bulletzone.protobuf.Bulletzone.PDU;
import net.cserna.bence.bulletzone.protobuf.Bulletzone.Registration;

public abstract class MessageConverter {

	private static final String TAG = "MessageConverter";

	public static byte[] actionToByte(Action action) {
		return null; // TODO
	}

	public static Action byteToAction(byte[] data) {
		Action action = null;

		// Action variables;
		net.cserna.bence.bulletzone.protobuf.Bulletzone.Action actionPdu = null;
		int userId;
		int actionData;
		long messageId;
		Direction direction;

		try {
			PDU pdu = Bulletzone.PDU.parseFrom(data);

			switch (pdu.getType()) {
			case EXIT:
				break;
			case FIRE:
				// Extract data
				actionPdu = pdu.getAction();

				userId = actionPdu.getUserId();
				messageId = actionPdu.getMessageId();

				// Create new move action
				action = new ActionFire(userId);
				break;
			case JOIN:
				// Extract data
				Registration registration = pdu.getRegistration();
				String name = registration.getName();

				// Create new tank creator action
				action = new ActionCreateTank(name);
				break;
			case MOVE:
				// Extract data
				actionPdu = pdu.getAction();

				userId = actionPdu.getUserId();
				actionData = actionPdu.getData();
				messageId = actionPdu.getMessageId();

				// Parse direction
				direction = Direction.fromByte((byte) actionData);

				// Create new move action
				action = new ActionMove(userId, direction);
				break;
			case TURN:
				// Extract data
				actionPdu = pdu.getAction();

				userId = actionPdu.getUserId();
				actionData = actionPdu.getData();
				messageId = actionPdu.getMessageId();

				// Parse direction
				direction = Direction.fromByte((byte) actionData);

				// Create new turn action
				action = new ActionTurn(userId, direction);
				break;

			case UPDATE:
				Log.e(TAG, "Not implemented message");
				break;
			default:
				Log.e(TAG, "Unknown PDU message type");
				break;
			}

		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return null;
	}

}
