package com.roamingServer.smscgw.server;

public interface ServiceHookType {
	// --types of hooks supported
	public final static int PRE_STARTUP = 100;
	public final static int POST_STARTUP = 101;
	public final static int PRE_SHUTDOWN = 201;
	public final static int POST_SHUTDOWN = 202;

}
