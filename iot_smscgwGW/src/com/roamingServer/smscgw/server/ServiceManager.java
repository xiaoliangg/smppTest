package com.roamingServer.smscgw.server;

import org.apache.log4j.Logger;

import com.roamingServer.smscgw.conf.BasicServerConfig;
import com.roamingServer.smscgw.conf.SmscGWConfig;
import com.roamingServer.smscgw.exception.AppRuntimeException;
import com.roamingServer.smscgw.jms.JmsManager;
import com.roamingServer.smscgw.smpp.Message;
import com.roamingServer.smscgw.smpp.client.SmppStarter;
import com.roamingServer.smscgw.util.BlockingQueueMTCacheImpl;
import com.roamingServer.smscgw.util.BlockingQueueMessageCacheImpl;
import com.roamingServer.smscgw.util.IMessageCache;

public class ServiceManager implements IService {

	private Logger _logger = Logger.getLogger(ServiceManager.class);
	private SmppStarter _smppService;
	private IService _sspService;
	private IMessageCache<Message> _receiveQueue;
	private IMessageCache<Message> _sendQueue;// PDU

	@Override
	public boolean destory() {
		return true;
	}

	@Override
	public boolean init() throws AppRuntimeException {
		_logger.info("start read config from conf/*.*");
		_logger.info("the config contains server, smpp, etc.");
		_logger.info("......");

		SmscGWConfig satGWConfig = BasicServerConfig.getSatGWConfig();
		int sendQueCa = satGWConfig.getSendQueueSize();
		_sendQueue = new BlockingQueueMTCacheImpl<Message>(sendQueCa);

		int receiveQueCa = satGWConfig.getReceiveQueueSize();
		_receiveQueue = new BlockingQueueMessageCacheImpl<Message>(receiveQueCa);

		return true;
	}

	@Override
	public void run() {
		init();
		_smppService = new SmppStarter(_sendQueue, _receiveQueue);
		new Thread(_smppService).start();

		_sspService = new SspServiceImpl(_sendQueue, _receiveQueue);
		new Thread(_sspService).start();

		JmsManager jmsService = new JmsManager();
		new Thread(jmsService).start();

	}

	@Override
	public boolean stop() {
		return false;
	}

	/**
	 * reload config and restart services
	 */
	@Override
	public boolean update() {
		return false;
	}
}