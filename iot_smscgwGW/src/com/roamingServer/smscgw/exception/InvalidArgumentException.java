package com.roamingServer.smscgw.exception;

@SuppressWarnings("serial")
public class InvalidArgumentException extends RuntimeException {

	public InvalidArgumentException(String msg) {
		super(msg);
	}
}
