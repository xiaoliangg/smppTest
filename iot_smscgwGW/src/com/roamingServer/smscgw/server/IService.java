package com.roamingServer.smscgw.server;

public interface IService extends Runnable {

	/**
	 * init the service config
	 * 
	 * @return
	 */
	public boolean init();

	public boolean stop();

	public boolean update();

	public boolean destory();
}
