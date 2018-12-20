package com.roamingServer.smscgw.jms;

import com.roamingServer.smscgw.conf.SmscGWConfig;

/**
 * <p>
 * <code>JmsManager队列管理器
 * </p>
 * 
 * @version
 */
public class JmsManager implements Runnable {

	@Override
	public void run() {
		// 消息接收器，主要负责从各业务处理器接收下行短信内容
		SoftsimReceiver softSimReceiver = new SoftsimReceiver();
		JmsMessageSender jmsSender = new JmsMessageSender();

		// 获取线程数
		int jmsReceiverNum = SmscGWConfig.getInstance().getJmsReceiverNum();
		int jmsSenderNum = SmscGWConfig.getInstance().getJmsSenderNum();

		// jms消息接收器
		if (jmsReceiverNum > 0) {
			Thread[] softsimRevThreads = new Thread[jmsReceiverNum];
			for (int i = 0; i < jmsReceiverNum; i++) {
				softsimRevThreads[i] = new Thread(softSimReceiver);
				softsimRevThreads[i].start();
			}
		}

		// jms消息发送器
		if (jmsSenderNum > 0) {
			Thread[] jmsSenderThread = new Thread[jmsSenderNum];
			for (int i = 0; i < jmsSenderNum; i++) {
				jmsSenderThread[i] = new Thread(jmsSender);
				jmsSenderThread[i].start();
			}
		}

	}

	public static void main(String[] args) {
		JmsManager jm = new JmsManager();
		jm.run();
	}
}
