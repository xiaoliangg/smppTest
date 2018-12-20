package com.roamingServer.smscgw.exception;

@SuppressWarnings("serial")
public class AppRuntimeException extends RuntimeException {

	public AppRuntimeException(String msg) {
		super(msg);
	}

	public AppRuntimeException() {
	}
}
