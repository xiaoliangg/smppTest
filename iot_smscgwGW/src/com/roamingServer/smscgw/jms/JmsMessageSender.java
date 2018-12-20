package com.roamingServer.smscgw.jms;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.iot.bean.cache.SmsMsgBean;

/**
 * JMS消息发送器
 */
public class JmsMessageSender implements Runnable {

	public static LinkedBlockingQueue<Object> jsmSendQueue = new LinkedBlockingQueue<Object>();
	protected final Logger logger = Logger.getLogger(JmsMessageSender.class);
	private String user = null;
	private String password = null;
	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private String softsimMoQueue = "softsimMoQueue";// 接受Soft sim上行业务请求的队列名称

	private Connection connection = null;
	private Session session = null;
	private MessageProducer softsimMoProducer = null;
	private ObjectMessage message = null;

	public JmsMessageSender() {
		JmsConfig instance = JmsConfig.getInstance();
		url = instance.getUrl();
		user = instance.getUser();
		password = instance.getPassword();
		softsimMoQueue = instance.getSoftsimMoQueue();
	}

	public boolean init() {
		try {
			logger.info("init WebSender...");
			// Create the connection.
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					user, password, url);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			softsimMoProducer = getProducer(session, softsimMoQueue);
			message = session.createObjectMessage();
		} catch (JMSException e) {
			logger.error("WebSender init failed!" + e.getMessage());
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e2) {
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * @param session
	 * @param queue
	 * @return
	 * @throws JMSException
	 *             MessageProducer
	 */
	protected MessageProducer getProducer(Session session, String queue)
			throws JMSException {
		MessageProducer producer = null;
		if (queue != null) {
			Destination dest = session.createQueue(queue);
			producer = session.createProducer(dest);
		}
		return producer;
	}

	@Override
	public void run() {
		boolean initStatus = init();

		if (initStatus) {
			logger.info("Jms WebSender begin to send the reqdata to the corresponding service processor...");
			while (true) {
				try {
					Object msg = jsmSendQueue.take();

					if (null != msg && (msg instanceof SmsMsgBean)) {// softSim业务上行短信
						message.setObject((SmsMsgBean) msg);
						softsimMoProducer.send(message);
						logger.debug("send a softsimt mo  to jms softsimMoQueue");
					}
				} catch (InterruptedException e) {
					logger.error("WebSender is interrupted send to web failed,"
							+ e.getMessage());
					e.printStackTrace();
				} catch (JMSException e) {
					logger.error("WebSender send to web failed,"
							+ e.getMessage());
					if (connection != null) {
						try {
							connection.close();
						} catch (JMSException e1) {
						}
					}
					boolean recon = init();
					while (!recon) {
						recon = init();
					}
				}
			} // end while
		}
	}

	public static void main(String[] args) {
		JmsMessageSender WebSender = new JmsMessageSender();
		WebSender.run();
	}

}
