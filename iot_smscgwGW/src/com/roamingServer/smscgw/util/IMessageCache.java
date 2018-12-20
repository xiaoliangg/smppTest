package com.roamingServer.smscgw.util;

public interface IMessageCache<T> {

	public void put(T o);

	public void put(T[] o);

	public T take();

	public int size();

	public void clear();
}
