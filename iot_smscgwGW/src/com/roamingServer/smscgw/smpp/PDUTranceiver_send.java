package com.roamingServer.smscgw.smpp;

import com.logica.smpp.Session;

public class PDUTranceiver_send extends PDUTranceiver {

	public PDUTranceiver_send(SessionManager sm, Session session,
			long comTimeOut, int windowSize, String bindMode) {
		this.sm = sm;
		this.session = session;
		this.comTimeOut = comTimeOut;
		this.windowSize = windowSize;
		this.bindMode = bindMode;
		logger.info("begin,send=" + session + ",sm=" + sm);
	}

	@Override
	public void run() {

		while (true) {
			try {
				while (session != null && !session.isBound()) {
					session = sm.rebind(bindMode);
					logger.info("sm=" + sm + ",send=" + session + ",conn="
							+ session.getConnection());
				}

				checkTimeOut(comTimeOut);
				sendPdu();

			} catch (Exception e) {
				String warnMsg = "PDUTranceiver_send thread failed!!";
				logger.error(warnMsg, e);
				SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
				sm.close();
			}
		}
	}
}
