package net.cserna.bence.bulletzone.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.inject.Inject;

public class TcpServer extends Thread implements OnConnectionClosed {
	private static final String TAG = "TcpServer";

	private final List<TcpWorker> mWorkers = new ArrayList<TcpWorker>();
	private final int mPort;
	private final OnConnectionClosed mConnectionClosed;
	private volatile boolean mRunning;
	private final TcpWorkerFactory workerFactory;

	private ServerSocket serverSocket;

	@Inject
	private TcpServer(TcpWorkerFactory workerFactory,
			OnConnectionClosed connectionClosed, @Port int port) {
		
		super(TAG);
		this.workerFactory = workerFactory;

		mPort = port;
		mConnectionClosed = connectionClosed;
	}

	@Override
	public void run() {
		try {
			startServer();
		} catch (IOException e) {
			Log.e(TAG, "Error while starting tcp server.", e);
		}
	}

	private void startServer() throws IOException {
		serverSocket = new ServerSocket(mPort);

		Log.i(TAG, "TcpServer started port:[" + mPort + "]");

		mRunning = true;
		for (;;) {
			try {
				Socket socket = serverSocket.accept();
				TcpWorker worker = workerFactory.create(socket, this);
				synchronized (mWorkers) {
					mWorkers.add(worker);
				}
				worker.start();

			} catch (SocketException e) {
				Log.i(TAG, "TCP server stopped");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		mRunning = false;
	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		synchronized (mWorkers) {
			for (TcpWorker worker : mWorkers) {
				worker.close();
			}
			
			for (TcpWorker worker : mWorkers) {
				try {
					worker.join();
				} catch (InterruptedException e) {
					Log.e(TAG, "", e);
				}
			}
			
			mWorkers.clear();
		}
	}

	public boolean isRunning() {
		return mRunning;
	}

	@Override
	public void closed(TcpWorker worker) {
		synchronized (mWorkers) {
			if (mWorkers.contains(worker)) {
				mWorkers.remove(worker);
			}
		}

		if (mConnectionClosed != null) {
			mConnectionClosed.closed(worker);
		}
	}
}
