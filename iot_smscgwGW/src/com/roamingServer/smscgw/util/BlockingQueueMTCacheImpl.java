package com.roamingServer.smscgw.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.roamingServer.smscgw.exception.AppRuntimeException;

public class BlockingQueueMTCacheImpl<T> implements IMessageCache<T> {

	private BlockingQueue<T> _cache = null;
	private final Logger logger = Logger.getLogger(this.getClass());

	public BlockingQueueMTCacheImpl(int cap) {
		_cache = new ArrayBlockingQueue<T>(cap + 20);
	}

	@Override
	public void put(T o) {
		try {
			_cache.put(o);
		} catch (InterruptedException e) {
			logger.error(
					"the send cache is full,time out when waiting to put!!", e);
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
