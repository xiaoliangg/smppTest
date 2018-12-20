package com.roamingServer.smscgw.server;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 短消息网关启动类 yuliang
 * 
 * @author linda
 * 
 */
public class SmscGW {

	private static Logger logger = Logger.getLogger(SmscGW.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure("conf/log4j.properties");

		logger.info("Start SmscGw ......");

		ServiceManager serviceManager = new ServiceManager();
		serviceManager.init();

		new Thread(serviceManager).start();
	}
}
