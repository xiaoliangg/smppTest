package com.roamingServer.smscgw.jms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.activemq.ActiveMQConnection;
import org.apache.log4j.Logger;

/**
 * JMS配置文件处理类
 * 
 */
public class JmsConfig {

	private static JmsConfig instance = null;

	private String user = null;
	private String password = null;
	private String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	static final String propsFilePath = "./conf/jmsConfig.properties";// 配置文件名称

	// RFM业务相关队列
	private String softsimMoQueue = "softsimMoQueue";// 处理SoftSim业务的上行短信队列名称
	private String softsimMtQueue = "softsimMtQueue";// 处理SoftSim业务的下行短信队列名称

	static final Properties properties = new Properties();
	private final Logger logger = Logger.getLogger(JmsConfig.class);

	private JmsConfig() {
		readConfig();
	}

	public static synchronized JmsConfig getInstance() {
		if (instance == null) {
			instance = new JmsConfig();
		}
		return instance;
	}

	private void readConfig() {
		FileInputStream propsFile;
		try {
			propsFile = new FileInputStream(propsFilePath);
			properties.load(propsFile);
			propsFile.close();
		} catch (FileNotFoundException e) {
			logger.error("./conf/jmsConfig.properties not found", e);
		} catch (IOException e) {
			logger.error("load ./conf/jmsConfig.properties failed!", e);
		}

		url = properties.getProperty("serverURL");
		user = properties.getProperty("userName");
		password = properties.getProperty("password");

		softsimMoQueue = properties.getProperty("softsimMoQueue");
		softsimMtQueue = properties.getProperty("softsimMtQueue");
	}

	// @return the user
	public String getUser() {
		return user;
	}

	// @param user the user to set
	public void setUser(String user) {
		this.user = user;
	}

	// @return the password
	public String getPassword() {
		return password;
	}

	// @param password the password to set
	public void setPassword(String password) {
		this.password = password;
	}

	// @return the url
	public String getUrl() {
		return url;
	}

	// @param url the url to set
	public void setUrl(String url) {
		this.url = url;
	}

	// @return the propsFilePath
	public static String getPropsFilePath() {
		return propsFilePath;
	}

	// return the properties
	public static Properties getProperties() {
		return properties;
	}

	// @param instance the instance to set
	public static void setInstance(JmsConfig instance) {
		JmsConfig.instance = instance;
	}

	public String getSoftsimMtQueue() {
		return softsimMtQueue;
	}

	public void setSoftsimMtQueue(String softsimMtQueue) {
		this.softsimMtQueue = softsimMtQueue;
	}

	public String getSoftsimMoQueue() {
		return softsimMoQueue;
	}

	public void setSoftsimMoQueue(String softsimMoQueue) {
		this.softsimMoQueue = softsimMoQueue;
	}

}
