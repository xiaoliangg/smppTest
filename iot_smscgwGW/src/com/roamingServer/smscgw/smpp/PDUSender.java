package com.roamingServer.smscgw.smpp;

import static com.roamingServer.smscgw.smpp.client.SmppStarter.alarmProperties;
import static com.roamingServer.smscgw.smpp.client.SmppStarter.sendQueue;

import java.util.Calendar;

import com.logica.smpp.Data;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.pdu.SubmitSM;
import com.logica.smpp.pdu.SubmitSMResp;
import com.logica.smpp.pdu.ValueNotSetException;
import com.logica.smpp.util.ByteBuffer;
import com.roamingServer.alarm.constant.AlarmConstant.WARN_LEVEL;
import com.roamingServer.smscgw.alarm.AlarmClient;

public class PDUSender extends PDUWorker {

	public PDUSender() {

	}

	@Override
	public void doCommunication() {

		if (sendedList != null && sendedList.size() < windowSize) {
			if (sendQueue != null && sendQueue.size() > 0) {
				submitToSmsc();
				lastActiveTime = System.currentTimeMillis();
			}
		}
		checkTimeOut(comTimeOut);

		receiveSubmitRsp();

		if (sendQueue == null || sendQueue.size() == 0) {
			long currentDateTime = System.currentTimeMillis();
			if ((currentDateTime - lastActiveTime) >= checkInterval) {
				lastActiveTime = currentDateTime;
				sm.enquireLink();
			}
		}

	}

	private void submitToSmsc() {
		Message currentMsg = sendQueue.take();
		if (currentMsg != null) {
			SubmitSM request = sm.submit(currentMsg);
			try {
				logger.info("=======> send to PhoneNum:"
						+ request.getDestAddr().getAddress() + " send SeqId = "
						+ request.getSequenceNumber()
						+ " ,submitReq hexString content:"
						+ request.getData().getHexDump());
				currentMsg.setSendTime(Calendar.getInstance());
				sendedList.put(request.getSequenceNumber(), currentMsg);
				session.submit(request);
			} catch (Exception e) {
				String warnMsg = "in smpp,pdusender send submitPacket failed";
				logger.error(warnMsg, e);
				SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
				sm.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkTimeOut(long timeOut) {
		try {
			for (int i = 0; i < sendedList.size(); i++) {
				int key = sendedList.keys().nextElement();
				if (sendedList.containsKey(key)) {
					Message msg = sendedList.get(key);

					if (msg == null) {
						continue;
					}
					long currTime = Calendar.getInstance().getTimeInMillis();
					long expireTime = msg.getSendTime().getTimeInMillis()
							+ timeOut;

					if (currTime > expireTime) {
						AlarmClient.sendCommAlarmNotify(WARN_LEVEL.BAD_LEVEL,
								alarmProperties
										.getAlarm_content_send_sms_timeout());
						if (msg.getSendCount() <= sendCount) {
							logger.warn("Resend time out message, smsc didn't deal the message timely!  "
									+ "the dest phoneNum ="
									+ msg.getPhoneNum()
									+ ",msg content HexString ="
									+ (new ByteBuffer(msg.getStlMessage()))
											.getHexDump());
							sendedList.remove(key);
							SubmitSM request = sm.submit(msg);

							logger.info("=======> Resend to PhoneNum:"
									+ request.getDestAddr().getAddress()
									+ " send SeqId = "
									+ request.getSequenceNumber()
									+ " ,submitReq hexString content:"
									+ request.getData().getHexDump());
							msg.setSendTime(Calendar.getInstance());
							msg.setSendCount(msg.getSendCount() + 1);
							sendedList.put(request.getSequenceNumber(), msg);
							session.submit(request);

						} else {
							logger.warn("time out message, smsc didn't deal the message timely!  "
									+ "the dest phoneNum = "
									+ msg.getPhoneNum()
									+ ",msg content HexString = "
									+ (new ByteBuffer(msg.getStlMessage()))
											.getHexDump());

							sendedList.remove(key);
							SmppAckLog.ackLog(msg);

							timeoutFailNum++;
							logger.warn("time out failed number ="
									+ timeoutFailNum);
						}
					}
				}
			}// end-while
		} catch (Exception e) {
			String warnMsg = "checkTimeOut execute failed";
			logger.error(warnMsg, e);
			SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
		}
	}

	private void receiveSubmitRsp() {
		try {
			PDU pdu = null;
			pdu = SMPPClientPDUEventListener.getPDU(timeout);
			sm.giveRsp(pdu);
			sm.dealRsp(pdu);
			if (pdu instanceof SubmitSMResp) {
				Message satMsg = sendedList.get(pdu.getSequenceNumber());
				if (satMsg != null && pdu.getCommandStatus() == Data.ESME_ROK) {
					try {
						logger.info("<-------Ack,SubmitSMResp,PhoneNum = "
								+ satMsg.getPhoneNum() + " SeqId = "
								+ pdu.getSequenceNumber()
								+ ", hexString data ="
								+ pdu.getData().getHexDump());
					} catch (ValueNotSetException e) {
						e.printStackTrace();
					}
					logger.debug("remove from sendlist,seq="
							+ pdu.getSequenceNumber());
					sendedList.remove(pdu.getSequenceNumber());
				}
				if (pdu.getCommandStatus() != Data.ESME_ROK) {
					logger.error("in smpp receiver,the response pdu from smsc :seq="
							+ pdu.getSequenceNumber()
							+ " ack failed, write log.");
					SmppAckLog.ackLog(satMsg);
					AlarmClient.sendCommAlarmNotify(
							WARN_LEVEL.WARN_LEVEL.WORST,
							alarmProperties.getAlarm_content_ack_fail());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
