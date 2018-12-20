package com.roamingServer.smscgw.ssp.core;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.roamingServer.smscgw.smpp.Message;

public class SspServer extends SspBase implements Runnable {

	private Logger log = Logger.getLogger(SspServer.class);
	private SspServerContext context = null;
	private boolean _stop = false;

	public SspServer() {
		_executor = new SspThreadPoolExecutor(coreSize, maxSize, sleepTime,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

		context = new SspServerContext();
	}

	@Override
	public void run() {
		context.setMessageCache(_sendQueue, _receiveQueue);

		while (true) {
			try {
				if (!_executor.isShutdown()) {
					Message message = _receiveQueue.take();
					_executor.execute(new FimiMoWorker(context, message));
				} else if (_stop) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void shutdown() {
		_executor.shutdown();
	}

	public void release() {

	}
}
