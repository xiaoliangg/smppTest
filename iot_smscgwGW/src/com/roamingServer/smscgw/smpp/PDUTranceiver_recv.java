package com.roamingServer.smscgw.smpp;

import com.logica.smpp.Session;

public class PDUTranceiver_recv extends PDUTranceiver {

	public PDUTranceiver_recv(SessionManager sm, Session session, long timeout,
			String bindMode, long checkInterval) {
		this.sm = sm;
		this.session = session;
		this.timeout = timeout;
		this.bindMode = bindMode;
		this.checkInterval = checkInterval;
		logger.info("begin,re=" + session + ",sm=" + sm);
	}

	@Override
	public void run() {
		while (true) {
			try {
				while (session != null && !session.isBound()) {
					session = sm.rebind(bindMode);
					logger.info("sm=" + sm + ",re=" + session + ",conn="
							+ session.getConnection());
				}

				receivePdu();

				long currentDateTime = System.currentTimeMillis();
				if ((currentDateTime - lastActiveTime) >= checkInterval) {
					lastActiveTime = currentDateTime;
					sm.enquireLink();
				}

			} catch (Exception e) {
				String warnMsg = "PDUTranceiver_recv thread failed!!";
				logger.error(warnMsg, e);
				SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
				sm.close();
			}
		}

	}

}
