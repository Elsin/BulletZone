package net.cserna.bence.bulletzone.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.cserna.bence.bulletzone.core.BinaryConnector;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.inject.Inject;

public class TcpWorker extends Thread implements BinarySender {

	// we won't allocate more than 1MB of memory
	private static final int SANITY_CHECK = 1048576;

	public static final String TAG = "TcpWorker";

	private final BinaryConnector mConnector;
	private final Socket mSocket;
	private final DataInputStream mInput;
	private final DataOutputStream mOutput;
	private final BlockingQueue<Optional<byte[]>> mOutputQueue = new LinkedBlockingQueue<Optional<byte[]>>();
	private final Sender mSender = new Sender();
	private final OnConnectionClosed mConnectionClosed;

	public TcpWorker(BinaryConnector connector, Socket socket,
			OnConnectionClosed connectionClosed) throws IOException {
		super("TcpWorker(" + socket.getInetAddress().toString() + ")");

		this.mConnector = connector;
		mConnectionClosed = connectionClosed;
		mSocket = socket;
		mSocket.setSoTimeout(10000);
		mInput = new DataInputStream(socket.getInputStream());
		mOutput = new DataOutputStream(mSocket.getOutputStream());

		// Message receiver callback setup
		connector.setBinarySender(this);
	}

	@Override
	public void run() {
		mSender.start();
		process();
	}

	private void process() {
		try {
			int length;
			byte[] body;
			while (!mSocket.isClosed()) {
				try {
					length = mInput.readInt();

					if (length > SANITY_CHECK) {
						Log.e(TAG, "Message size violation. Size: " + length);
						continue;
					}

					// Read the message body
					body = new byte[length];
					mInput.readFully(body);

					mConnector.processMessage(body);
				} catch (SocketTimeoutException e) {
					// Log.info("Read timeout on socket.", e);
				}
			}
		} catch (EOFException e) {

		} catch (SocketException e) {
			// Ignore
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			close();
			try {
				mSender.join();
			} catch (InterruptedException e) {
			}

			if (mConnectionClosed != null) {
				mConnectionClosed.closed(this);
			}
		}
	}

	public void sendMessage(byte[] message) {
		try {
			mOutputQueue.put(Optional.of(message));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			mInput.close();
		} catch (IOException e) {

		}
		try {
			mOutput.close();
		} catch (IOException e) {

		}
		try {
			mSocket.close();
		} catch (IOException e) {

		}

		mOutputQueue.clear();
		mOutputQueue.add(Optional.<byte[]> absent());
	}

	@Override
	public int hashCode() {
		return mSocket.getInetAddress().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != TcpWorker.class) {
			return false;
		}

		TcpWorker otherWorker = (TcpWorker) obj;
		try {
			return otherWorker.mSocket.getInetAddress().equals(
					this.mSocket.getInetAddress());
		} catch (Exception e) {

		}

		return false;
	}

	private class Sender extends Thread {
		public Sender() {
			super(TcpWorker.this.getName() + " - Sender");
		}

		@Override
		public void run() {
			try {
				byte[] outgoingMessage;
				Optional<byte[]> optMsg;
				while ((optMsg = mOutputQueue.take()).isPresent()) {
					outgoingMessage = optMsg.get();
					try {
						mOutput.writeInt(outgoingMessage.length);
						mOutput.write(outgoingMessage);
					} catch (IOException e) {
						break;
					}
				}
			} catch (InterruptedException e) {
				Log.e(TAG, "Tcp work sender thread interrupted.", e);
			}
		}
	}
}
