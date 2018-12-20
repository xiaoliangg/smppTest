package com.roamingServer.smscgw.server;

public interface IServiceHook extends ServiceHookType {

	/**
	 * Information about the server hook.
	 */
	public String info();

	/**
	 * Method called to perform any initialisation
	 */
	public void initHook(IService quickserver);

	/**
	 * Invoked pre/post server event. If the hook is doing some action for the
	 * even passed it should return true indicating the same.
	 */
	public boolean handleEvent(int event);
}
