package com.roamingServer.smscgw.conf;

public class BasicServerConfig {

	private BasicServerConfig() {
		// do action
	}

	// sat gateWay
	public static SmscGWConfig getSatGWConfig() {
		return SmscGWConfig.getInstance();
	}

}
