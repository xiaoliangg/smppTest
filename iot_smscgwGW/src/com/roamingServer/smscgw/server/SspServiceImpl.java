package com.roamingServer.smscgw.server;

import com.roamingServer.smscgw.conf.SmscGWConfig;
import com.roamingServer.smscgw.smpp.Message;
import com.roamingServer.smscgw.ssp.core.SspClient;
import com.roamingServer.smscgw.ssp.core.SspServer;
import com.roamingServer.smscgw.util.IMessageCache;

public class SspServiceImpl extends AbstractService {

	private SspServer _sspServer = null;

	private SspClient sspClient = null;
	private IMessageCache<Message> _receiveQueue = null;

	private IMessageCache<Message> _sendQueue = null;

	public SspServiceImpl(IMessageCache<Message> send,
			IMessageCache<Message> receive) {
		_sendQueue = send;
		_receiveQueue = receive;
	}

	@Override
	public void run() {

		init();
		_logger.info("run the ssp service...");

		new Thread(_sspServer).start();

		int mtTreadNum = SmscGWConfig.getInstance().getMtThreadNum();
		for (int i = 0; i < mtTreadNum; i++) {
			new Thread(sspClient).start();
		}
	}

	@Override
	public boolean init() {
		_sspServer = new SspServer();
		_sspServer.setMessageCache(_sendQueue, _receiveQueue);

		sspClient = new SspClient();
		sspClient.setMessageCache(_sendQueue, _receiveQueue);
		return false;
	}
}
