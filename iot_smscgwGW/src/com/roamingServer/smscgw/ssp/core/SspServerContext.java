package com.roamingServer.smscgw.ssp.core;

import com.roamingServer.smscgw.jms.JmsMessageSender;
import com.roamingServer.smscgw.smpp.Message;
import com.roamingServer.smscgw.util.IMessageCache;
import com.roamingServer.smscgw.util.UniqueWorkID;

public class SspServerContext {

	protected static UniqueWorkID _browerID = UniqueWorkID.getInstance();

	protected IMessageCache<Message> _sendQueue = null;

	protected IMessageCache<Message> _receiveQueue = null;

	public SspServerContext() {
	}

	public static void putJmsMessage(Object cmdlog) {
		try {
			JmsMessageSender.jsmSendQueue.put(cmdlog);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setMessageCache(IMessageCache<Message> send,
			IMessageCache<Message> receive) {
		_sendQueue = send;
		_receiveQueue = receive;
	}

}
