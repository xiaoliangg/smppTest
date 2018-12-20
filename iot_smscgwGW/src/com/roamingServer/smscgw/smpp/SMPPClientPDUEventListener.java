package com.roamingServer.smscgw.smpp;

import org.apache.log4j.Logger;

import com.logica.smpp.ServerPDUEvent;
import com.logica.smpp.ServerPDUEventListener;
import com.logica.smpp.SmppObject;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.util.Queue;

class SMPPClientPDUEventListener extends SmppObject implements
		ServerPDUEventListener {
	static Queue pduQueue = new Queue();

	protected static Logger logger = Logger
			.getLogger(SMPPClientPDUEventListener.class);

	public SMPPClientPDUEventListener() {
	}

	@Override
	public void handleEvent(ServerPDUEvent event) {
		PDU pdu = event.getPDU();
		if (pdu != null) {
			synchronized (pduQueue) {
				pduQueue.enqueue(pdu);
				pduQueue.notifyAll();
			}
		}
	}

	public static PDU getPDU(long timeout) {
		PDU pdu = null;

		synchronized (pduQueue) {
			if (pduQueue.isEmpty()) {
				try {
					pduQueue.wait(timeout);
				} catch (InterruptedException e) {
					logger.error("InterruptedException happened.", e);
				}
			}
			if (!pduQueue.isEmpty()) {
				pdu = (PDU) pduQueue.dequeue();
			}
		}

		return pdu;
	}

}
