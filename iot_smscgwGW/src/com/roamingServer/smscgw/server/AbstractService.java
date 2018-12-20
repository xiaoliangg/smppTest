package com.roamingServer.smscgw.server;

import org.apache.log4j.Logger;

public abstract class AbstractService implements IService {

	protected Logger _logger = Logger.getLogger(this.getClass());

	int _times = 0;

	@Override
	public boolean init() {
		return false;
	}

	@Override
	public boolean destory() {
		return false;
	}

	@Override
	public boolean stop() {
		return false;
	}

	@Override
	public boolean update() {
		return false;
	}
}
