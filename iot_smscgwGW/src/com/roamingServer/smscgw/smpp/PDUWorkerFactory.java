package com.roamingServer.smscgw.smpp;

public class PDUWorkerFactory {

	private static PDUWorkerFactory factory;

	private PDUWorkerFactory() {

	}

	public synchronized static PDUWorkerFactory getInstance() {
		if (factory == null) {
			factory = new PDUWorkerFactory();
		}
		return factory;
	}

	public PDUWorker getPDUWorker(String bindMode) {
		PDUWorker pDUWorker;
		if (bindMode.equals("r")) {// receive
			pDUWorker = new PDUReceiver();
		} else if (bindMode.equals("t")) {// send
			pDUWorker = new PDUSender();
		} else {
			pDUWorker = new PDUTranceiver();
		}
		return pDUWorker;
	}
}
