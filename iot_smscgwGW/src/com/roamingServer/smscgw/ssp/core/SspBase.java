package com.roamingServer.smscgw.ssp.core;

import java.util.concurrent.ExecutorService;

import com.roamingServer.smscgw.conf.BasicServerConfig;
import com.roamingServer.smscgw.conf.SmscGWConfig;
import com.roamingServer.smscgw.smpp.Message;
import com.roamingServer.smscgw.util.IMessageCache;

public class SspBase {

	protected ExecutorService _executor = null;
	protected IMessageCache<Message> _sendQueue = null;
	protected IMessageCache<Message> _receiveQueue = null;
	protected SmscGWConfig satGWConfig = null;
	protected boolean _stop = false;
	protected int coreSize;
	protected int maxSize;
	protected int sleepTime;

	public SspBase() {
		initThreadPool();
	}

	// 初始化队列
	public void setMessageCache(IMessageCache<Message> send,
			IMessageCache<Message> receive) {
		_sendQueue = send;
		_receiveQueue = receive;
	}

	// 设置缓冲区大小参数
	private void initThreadPool() {
		satGWConfig = BasicServerConfig.getSatGWConfig();
		coreSize = satGWConfig.getCoreSize();
		maxSize = satGWConfig.getMaxSize();
		sleepTime = satGWConfig.getActiveTime();
	}

}
