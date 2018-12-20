package com.roamingServer.smscgw.ssp.core;

import static com.roamingServer.smscgw.jms.SoftsimReceiver.jsmReceiveQueue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.iot.bean.cache.SmsMsgBean;
import com.roamingServer.smscgw.smpp.Message;

public class SspClient extends SspBase implements Runnable {

	private static final Logger logger = Logger.getLogger(SspClient.class);

	public SspClient() {
		super();
		_executor = new SspThreadPoolExecutor(coreSize, maxSize, sleepTime,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	public void run() {
		while (true) {
			try {
				List<SmsMsgBean> list = queryMOCommand();
				if (null == list || list.size() < 1) {
					Thread.sleep(2000);
				} else {
					Iterator<SmsMsgBean> it = list.iterator();
					while (it.hasNext()) {
						SmsMsgBean cmd = it.next();

						if (cmd != null) {
							doSatBussiness(cmd);
						}
					}
				}
			} catch (Exception e) {
				logger.error("executor deal the cmd failed", e);
			}
		}
	}

	public List<SmsMsgBean> queryMOCommand() {
		synchronized (jsmReceiveQueue) {
			List<SmsMsgBean> receiveList = null;
			if (jsmReceiveQueue.size() > 0) {
				receiveList = new ArrayList<SmsMsgBean>();
				int len = jsmReceiveQueue.size() > 200 ? 200 : jsmReceiveQueue
						.size();
				for (int i = 0; i < len; i++) {
					try {
						receiveList.add(jsmReceiveQueue.take());
					} catch (InterruptedException e) {
						logger.error("", e);
					}
				}
			}
			return receiveList;
		}
	}

	/**
	 * 进行业务处理
	 * 
	 * @param cmd
	 * @throws InterruptedException
	 * @throws ExecutionException
	 *             void
	 */
	private void doSatBussiness(SmsMsgBean cmd) throws InterruptedException,
			ExecutionException {
		FimiMtWorker fimiMtWorker = new FimiMtWorker(cmd);
		Future<Message[]> future = _executor.submit(fimiMtWorker);

		if (future != null) {
			Message[] satMsgArray;
			satMsgArray = future.get();

			for (int i = 0; i < satMsgArray.length; i++) {
				_sendQueue.put(satMsgArray[i]);
				logger.debug("put mtMessage into sendQueue,smpp sender will send it");
				logger.debug("**************message MsgType:"
						+ satMsgArray[i].getMsgType());
			}
		}
	}

	public void shutdown() {
		_executor.shutdown();
	}

}
