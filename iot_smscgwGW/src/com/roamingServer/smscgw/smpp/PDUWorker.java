package com.roamingServer.smscgw.smpp;

import static com.roamingServer.smscgw.smpp.client.SmppStarter.gp;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.logica.smpp.Session;

public class PDUWorker implements Runnable {

	protected String bindMode;
	protected final Logger logger = Logger.getLogger(PDUWorker.class);

	protected long timeout = 0;
	protected long checkInterval = 0;
	protected long lastActiveTime = 0;

	// 已发送报文队列，控制滑动窗口，key：submit报文的seqId；value：SatMessage
	protected static ConcurrentHashMap<Integer, Message> sendedList = new ConcurrentHashMap<Integer, Message>();

	protected int windowSize = 10;// 滑动窗口数，从配置文件读取
	protected long comTimeOut = 10000;// 通信超时 miliseconds
	protected volatile int timeoutFailNum = 0;// smsc 超时处理的数目
	protected static final int sendCount = gp.getRepeatTime();
	protected SessionManager sm;
	protected Session session;

	public PDUWorker() {

	}

	private void init() {
		checkInterval = gp.getEnquirelinkInterval();
		timeout = gp.getReceiveTimeout();
		this.bindMode = gp.getBindMode();
		if (gp.isSyncMode()) {
			windowSize = 1;
		} else {
			windowSize = gp.getSlideWindowSize();
		}
	}

	@Override
	public void run() {
		init();
		sm = new SessionManager();
		session = sm.bind(bindMode);
		if (bindMode.equalsIgnoreCase("tr")) {
			doCommunication();
		} else {
			while (true) {
				try {
					// 重连检查
					while (session != null && !session.isBound()) {
						session = sm.rebind(bindMode);
					}

					// 业务通信
					doCommunication();

				} catch (Exception e) {
					String warnMsg = "Pduworker thread failed!!";
					logger.error(warnMsg, e);
					SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
					sm.close();
				}
			}
		}
	}

	protected void doCommunication() {

	}

}
