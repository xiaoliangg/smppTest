package com.roamingServer.smscgw.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.roamingServer.smscgw.exception.AppRuntimeException;

public class BlockingQueueMessageCacheImpl<T> implements IMessageCache<T> {

	private int _cap = -1;
	private BlockingQueue<T> _cache = null;

	private final Logger logger = Logger.getLogger(this.getClass());

	public BlockingQueueMessageCacheImpl(int cap) {
		_cap = cap;
		_cache = new ArrayBlockingQueue<T>(cap + 20);
	}

	@Override
	public void put(T o) {
		try {
			if (_cache.size() >= _cap) {
				logger.error("skip some request.");
				return;
			}
			_cache.put(o);
		} catch (InterruptedException e) {
			logger.error("the cache is full,time out when waiting to put!!", e);
			throw new AppRuntimeException("cache put error!");
		}
	}

	@Override
	public T take() {
		try {
			return _cache.take();
		} catch (InterruptedException e) {
			logger.error("the cache is empty,time out when waiting to take !!",
					e);
			throw new AppRuntimeException("cache take error!");
		}
	}

	@Override
	public int size() {
		return _cache.size();
	}

	@Override
	public void put(T[] o) {
		try {
			for (T t : o) {
				_cache.put(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppRuntimeException("cache take error!");
		}

	}

	@Override
	public void clear() {
		_cache.clear();
	}

}
