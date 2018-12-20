package com.roamingServer.smscgw.jms;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.iot.bean.cache.SmsMsgBean;

/**
 * <p>
 * <code>softsim业务接收器</code>
 * 
 * @version
 * @date Apr 13, 2009
 */
public class SoftsimReceiver implements MessageListener, Runnable {

	public static LinkedBlockingQueue<SmsMsgBean> jsmReceiveQueue = new LinkedBlockingQueue<SmsMsgBean>();
	protected final Logger logger = Logger.getLogger(SoftsimReceiver.class);

	// 从文件读取配置
	private String user = null;
	private String password = null;
	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private String softsimMtQueue = "softsimMtQueue";

	public SoftsimReceiver() {
		JmsConfig instance = JmsConfig.getInstance();
		url = instance.getUrl();
		user = instance.getUser();
		password = instance.getPassword();
		softsimMtQueue = instance.getSoftsimMtQueue();
	}

	@Override
	public void run() {

		logger.info("Jms SoftsimReceiver begin to receive mtsms...");
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				user, password, url);
		Connection connection = null;
		MessageConsumer ramConsumer = null;
		Session session = null;
		Destination ramDest;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// for soft sim
			ramDest = session.createQueue(softsimMtQueue);
			ramConsumer = session.createConsumer(ramDest);
			ramConsumer.setMessageListener(this);
		} catch (JMSException e) {
			logger.error(
					"in jms SoftsimReceiver,received command form web failed"
							+ e.getMessage(), e);
		}
	}

	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		try {
			if (objectMessage != null
					&& objectMessage.getObject().getClass() == SmsMsgBean.class) {
				SmsMsgBean msg = (SmsMsgBean) objectMessage.getObject();
				jsmReceiveQueue.put(msg);
				logger.info("<<<<<<<receive softsim mt sms, \n " + "phoneNum="
						+ msg.getMsisdn() + ",content=" + msg.getMsg());
				logger.debug("SoftSim jsmReceiveQueue.size()== "
						+ SoftsimReceiver.jsmReceiveQueue.size());
			}
		} catch (InterruptedException e) {
			logger.error("jsmReceiveQueue.put(cmdMsg) interrupted,the jsmReceiveQueue is too big,no occupation "
					+ e);
		} catch (JMSException e) {
			logger.error("Receive softsim mtSms encounters JMSException " + e);
		}
	}

}
