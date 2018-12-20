package com.roamingServer.smscgw.smpp;

import static com.roamingServer.smscgw.jms.JmsMessageSender.jsmSendQueue;

import java.util.Date;

import org.apache.log4j.Logger;

import com.roamingServer.smscgw.jms.JmsMessageSender;
import com.roamingServer.smscgw.util.UniqueWorkID;
import com.watchdata.model.logrecord.ErrorLog;

public class SmppAckLog {

	private static Logger logger = Logger.getLogger(SmppAckLog.class);

	public static void ackLog(Message smscMsg) {
		String timeoutDesp = "smsc didnot give response in receive-timeout";
		try {
			String logId = String.valueOf(smscMsg.getSeqNo());
			ErrorLog errorLog = new ErrorLog(Long.parseLong(logId),// logid
					"4",// errorType
					timeoutDesp, smscMsg.getPhoneNum(), new Date());
			jsmSendQueue.put(errorLog);
		} catch (Exception e) {

			logger.error("SmppAckLog failed," + e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public static void ackErrorLog(String errorMsg) {
		String logid = UniqueWorkID.getLogid(0);
		ErrorLog errorLog = new ErrorLog(Long.parseLong(logid),// logid
				"3",// errorType
				errorMsg, "0", new Date());
		try {
			JmsMessageSender.jsmSendQueue.put(errorLog);
		} catch (InterruptedException e) {
			logger.error("ackErrorLog failed, Interrupted when putting errorlog into jsmSendQueue");
			e.printStackTrace();
		}
	}
}
