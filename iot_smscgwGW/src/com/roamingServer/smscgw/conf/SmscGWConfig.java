package com.roamingServer.smscgw.conf;

import java.io.File;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class SmscGWConfig {
	private static SmscGWConfig instance;
	private static Logger logger = Logger.getLogger(SmscGWConfig.class);

	// ssp connect packet param
	private String serverAddr = null;
	private String applicationAddr = null;

	// gateway cache size
	private int sendQueueSize = 100000000;
	private int receiveQueueSize = 100000000;

	// executor param,thread num
	private int coreSize = 50;
	private int maxSize = 1000;
	private int activeTime = 1000;

	// session param
	private int sessionSize = 10000;
	private int connectTimeout = 30000;
	private int suspendTimeout = 30000;

	// jms 线程数配置
	private int jmsSenderNum = 1;
	private int jmsReceiverNum = 1;

	private boolean invalidateEncodeXML = false;
	private boolean validateSession = false;

	private int mtThreadNum = 1;

	private SmscGWConfig() {
		parseXML();
	}

	public static synchronized SmscGWConfig getInstance() {
		if (instance == null) {
			instance = new SmscGWConfig();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private void parseXML() {

		File satGWFile = new File("./conf/SmscGW.xml");
		SAXReader reader = new SAXReader();
		reader.setIncludeExternalDTDDeclarations(false);
		reader.setIncludeInternalDTDDeclarations(false);
		Document doc;

		try {
			doc = reader.read(satGWFile);
			Element root = doc.getRootElement();

			// BaseConfig
			Element baseConfig = root.element("BaseConfig");
			serverAddr = baseConfig.elementTextTrim("ServerAddr");
			applicationAddr = baseConfig.elementTextTrim("ApplicationAddr");
			mtThreadNum = Integer.parseInt(baseConfig
					.elementTextTrim("mtThreadNum"));

			// ServerConfig
			Element serverConfigNode = root.element("ServerConfig");
			sendQueueSize = serverConfigNode.elementText("SendQueueSize")
					.equals("") ? sendQueueSize : Integer
					.parseInt(serverConfigNode.elementText("SendQueueSize"));
			receiveQueueSize = serverConfigNode.elementText("ReceiveQueueSize")
					.equals("") ? receiveQueueSize : Integer
					.parseInt(serverConfigNode.elementText("ReceiveQueueSize"));

			Element executorNode = serverConfigNode.element("Executor");
			coreSize = executorNode.elementTextTrim("CoreSize").equals("") ? coreSize
					: Integer
							.parseInt(executorNode.elementTextTrim("CoreSize"));
			maxSize = executorNode.elementTextTrim("MaxSize").equals("") ? maxSize
					: Integer.parseInt(executorNode.elementTextTrim("MaxSize"));
			activeTime = executorNode.elementTextTrim("ActiveTime").equals("") ? activeTime
					: Integer.parseInt(executorNode
							.elementTextTrim("ActiveTime"));

			Element sessionNode = serverConfigNode.element("Session");
			sessionSize = sessionNode.elementTextTrim("SessionSize").equals("") ? sessionSize
					: Integer.parseInt(sessionNode
							.elementTextTrim("SessionSize"));
			connectTimeout = sessionNode.elementTextTrim("ConnectTimeout")
					.equals("") ? connectTimeout : Integer.parseInt(sessionNode
					.elementTextTrim("ConnectTimeout"));
			suspendTimeout = sessionNode.elementTextTrim("SuspendTimeout")
					.equals("") ? suspendTimeout : Integer.parseInt(sessionNode
					.elementTextTrim("SuspendTimeout"));

			// JmsThreadNum
			Element jmsThreadNumNode = serverConfigNode.element("JmsThreadNum");

			jmsSenderNum = jmsThreadNumNode.elementTextTrim("jmsSenderNum")
					.equals("") ? jmsSenderNum : Integer
					.parseInt(jmsThreadNumNode.elementTextTrim("jmsSenderNum"));
			jmsReceiverNum = jmsThreadNumNode.elementTextTrim("jmsReceiverNum")
					.equals("") ? jmsReceiverNum : Integer
					.parseInt(jmsThreadNumNode
							.elementTextTrim("jmsReceiverNum"));

			invalidateEncodeXML = Boolean.parseBoolean(serverConfigNode
					.elementTextTrim("InvalidateEncodeXML"));
			validateSession = Boolean.parseBoolean(serverConfigNode
					.elementTextTrim("ValidateSession"));

			if (logger.isDebugEnabled()) {
				debugPrint();
			}
		} catch (DocumentException e) {
			logger.error("parse conf/SmscGW.xml failed.\n" + e.getMessage(),
					e.getCause());
		} catch (Exception e) {
			logger.error("parse conf/SmscGW.xml failed.\n" + e.getMessage(),
					e.getCause());
			e.printStackTrace();
		}

	}

	private void debugPrint() {
		logger.debug("**********in conf/SmscGW.xml :********* \n");
		logger.debug("serverAddr=" + serverAddr + "\n" + "applicationAddr="
				+ applicationAddr + "\n" + "sendQueueSize=" + sendQueueSize
				+ "\n" + "receiveQueueSize=" + receiveQueueSize + "\n"
				+ "coreSize=" + coreSize + "\n" + "maxSize=" + maxSize + "\n"
				+ "activeTime=" + activeTime + "\n" + "sessionSize="
				+ sessionSize + "\n" + "connectTimeout=" + connectTimeout
				+ "\n" + "suspendTimeout=" + suspendTimeout + "\n"
				+ "invalidateEncodeXML=" + invalidateEncodeXML + "\n");
		logger.debug("**************************************");
	}

	public static void main(String[] args) {
		SmscGWConfig.getInstance().getServerAddr();
		System.out.println("ooo");
	}

	/**
	 * @return the serverAddr
	 */
	public String getServerAddr() {
		return serverAddr;
	}

	/**
	 * @param serverAddr
	 *            the serverAddr to set
	 */
	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
	}

	/**
	 * @return the applicationAddr
	 */
	public String getApplicationAddr() {
		return applicationAddr;
	}

	/**
	 * @param applicationAddr
	 *            the applicationAddr to set
	 */
	public void setApplicationAddr(String applicationAddr) {
		this.applicationAddr = applicationAddr;
	}

	/**
	 * @return the sendQueueSize
	 */
	public int getSendQueueSize() {
		return sendQueueSize;
	}

	/**
	 * @param sendQueueSize
	 *            the sendQueueSize to set
	 */
	public void setSendQueueSize(int sendQueueSize) {
		this.sendQueueSize = sendQueueSize;
	}

	/**
	 * @return the receiveQueueSize
	 */
	public int getReceiveQueueSize() {
		return receiveQueueSize;
	}

	/**
	 * @param receiveQueueSize
	 *            the receiveQueueSize to set
	 */
	public void setReceiveQueueSize(int receiveQueueSize) {
		this.receiveQueueSize = receiveQueueSize;
	}

	/**
	 * @return the coreSize
	 */
	public int getCoreSize() {
		return coreSize;
	}

	/**
	 * @param coreSize
	 *            the coreSize to set
	 */
	public void setCoreSize(int coreSize) {
		this.coreSize = coreSize;
	}

	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize
	 *            the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the activeTime
	 */
	public int getActiveTime() {
		return activeTime;
	}

	/**
	 * @param activeTime
	 *            the activeTime to set
	 */
	public void setActiveTime(int activeTime) {
		this.activeTime = activeTime;
	}

	/**
	 * @return the sessionSize
	 */
	public int getSessionSize() {
		return sessionSize;
	}

	/**
	 * @param sessionSize
	 *            the sessionSize to set
	 */
	public void setSessionSize(int sessionSize) {
		this.sessionSize = sessionSize;
	}

	/**
	 * @return the connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * @param connectTimeout
	 *            the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @return the suspendTimeout
	 */
	public int getSuspendTimeout() {
		return suspendTimeout;
	}

	/**
	 * @param suspendTimeout
	 *            the suspendTimeout to set
	 */
	public void setSuspendTimeout(int suspendTimeout) {
		this.suspendTimeout = suspendTimeout;
	}

	/**
	 * @return the invalidateEncodeXML
	 */
	public boolean isInvalidateEncodeXML() {
		return invalidateEncodeXML;
	}

	/**
	 * @param invalidateEncodeXML
	 *            the invalidateEncodeXML to set
	 */
	public void setInvalidateEncodeXML(boolean invalidateEncodeXML) {
		this.invalidateEncodeXML = invalidateEncodeXML;
	}

	/**
	 * @return the invalidateEncodeXML
	 */
	public boolean isValidateSession() {
		return validateSession;
	}

	/**
	 * @param invalidateEncodeXML
	 *            the invalidateEncodeXML to set
	 */
	public void setisValidateSession(boolean validateSession) {
		this.validateSession = validateSession;
	}

	// /**
	// * @return the logger
	// */
	// public Log getLogger() {
	// return logger;
	// }

	public String getSCPCharset() {
		return "utf-8";
	}

	public int getJmsSenderNum() {
		return jmsSenderNum;
	}

	public void setJmsSenderNum(int jmsSenderNum) {
		this.jmsSenderNum = jmsSenderNum;
	}

	public int getJmsReceiverNum() {
		return jmsReceiverNum;
	}

	public void setJmsReceiverNum(int jmsReceiverNum) {
		this.jmsReceiverNum = jmsReceiverNum;
	}

	public int getMtThreadNum() {
		return mtThreadNum;
	}

	public void setMtThreadNum(int mtThreadNum) {
		this.mtThreadNum = mtThreadNum;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		SmscGWConfig.logger = logger;
	}

}
