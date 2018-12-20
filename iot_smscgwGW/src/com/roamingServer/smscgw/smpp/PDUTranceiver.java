package com.roamingServer.smscgw.smpp;

import static com.roamingServer.smscgw.smpp.client.SmppStarter.alarmProperties;
import static com.roamingServer.smscgw.smpp.client.SmppStarter.receiveQueue;
import static com.roamingServer.smscgw.smpp.client.SmppStarter.sendQueue;

import java.util.Calendar;

import com.logica.smpp.Data;
import com.logica.smpp.pdu.Address;
import com.logica.smpp.pdu.DeliverSM;
import com.logica.smpp.pdu.EnquireLinkResp;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.pdu.SubmitSM;
import com.logica.smpp.pdu.SubmitSMResp;
import com.logica.smpp.util.ByteBuffer;
import com.roamingServer.alarm.constant.AlarmConstant.WARN_LEVEL;
import com.roamingServer.smscgw.alarm.AlarmClient;
import com.roamingServer.smscgw.jms.JmsMessageSender;

public class PDUTranceiver extends PDUWorker {
	public PDUTranceiver() {
	}

	@Override
	public void doCommunication() {
		PDUTranceiver_send sender = new PDUTranceiver_send(sm, session,
				comTimeOut, windowSize, bindMode);
		Thread s = new Thread(sender);
		s.start();

		PDUTranceiver_recv receiver = new PDUTranceiver_recv(sm, session,
				timeout, bindMode, checkInterval);
		Thread r = new Thread(receiver);
		r.start();

		Thread.yield();
	}

	public void receivePdu() {
		try {
			PDU pdu = null;
			pdu = SMPPClientPDUEventListener.getPDU(timeout);
			if (pdu != null) {
				sm.giveRsp(pdu);
				lastActiveTime = System.currentTimeMillis();

				if ((pdu instanceof DeliverSM)) {

					DeliverSM requestMessage = (DeliverSM) pdu;
					logger.info("<---------A Mo request from phone:"
							+ requestMessage.getSourceAddr().getAddress()
							+ ",\n DeliverSM request packet(hexString):"
							+ requestMessage.getData().getHexDump());

					if (checkReceipt(requestMessage.getEsmClass())) {
						logger.info("pase receipt");
						try {
							SMReceipt smReceipt = parseReceipt(requestMessage);
							JmsMessageSender.jsmSendQueue.put(smReceipt);
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					} else {
						Message satMsg = new Message();
						satMsg.setPhoneNum(sm
								.getFormattedPhoneNum(requestMessage
										.getSourceAddr().getAddress()));
						satMsg.setStlMessage(requestMessage.getShortMessage()
								.getByteMessage());
						receiveQueue.put(satMsg);
						logger.info("this deliver sm is not receipt.");
					}

					logger.info("in mo,requestMessage from phone:"
							+ requestMessage.getSourceAddr().getAddress()
							+ ",is:"
							+ new ByteBuffer(requestMessage.getShortMessage()
									.getByteMessage()).getHexDump());
				} else if (pdu instanceof SubmitSMResp) {

					if (pdu.getCommandStatus() == Data.ESME_ROK) {
						Message satMsg = sendedList.remove(pdu
								.getSequenceNumber());
						if (satMsg != null) {
							logger.info("<-------Ack,SubmitSMResp,PhoneNum = "
									+ satMsg.getPhoneNum() + " SeqId = "
									+ pdu.getSequenceNumber()
									+ ", hexString data ="
									+ pdu.getData().getHexDump());
							logger.debug("remove from sendlist,seq="
									+ pdu.getSequenceNumber());
						}
					}

					if (pdu.getCommandStatus() != Data.ESME_ROK) {
						
						logger.error("smsc commandStatus = " +pdu.getCommandStatus());
					
						logger.error("in smpp receiver,the response pdu from smsc :seq="
								+ pdu.getSequenceNumber()
								+ " ack failed, write log.");
						Message satMsg = sendedList
								.get(pdu.getSequenceNumber());
						AlarmClient.sendCommAlarmNotify(WARN_LEVEL.WORST,
								alarmProperties.getAlarm_content_ack_fail());
						if (satMsg != null) {
							SmppAckLog.ackLog(satMsg);
						}
					}

				} else if (pdu instanceof EnquireLinkResp) {
					EnquireLinkResp response = (EnquireLinkResp) pdu;
					if (response.getCommandStatus() != Data.ESME_ROK) {
						logger.error("enquireLinkResponse from smsc failed");
						try {
							session.unbind();
						} catch (Exception e) {
							String warnMsg = "enquireLink is broken,unbind failed";
							logger.error(warnMsg, e);
							SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
						}
					} else {
						logger.debug("<-- Receive EnquireLinkResponse from smsc ok");
					}
				}

			}
		} catch (Exception e) {
			String warnMsg = "enquireLink to smsc is broken";
			logger.error(warnMsg, e);
			SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
		}
	}

	public void sendPdu() {
		try {

			if (sendedList != null && sendedList.size() < windowSize) {
				if (sendQueue != null) {
					Message currentMsg = sendQueue.take();
					if (currentMsg != null) {

						SubmitSM request = sm.submit(currentMsg);

						logger.info("=======> send to PhoneNum:"
								+ request.getDestAddr().getAddress()
								+ " send SeqId = "
								+ request.getSequenceNumber()
								+ " ,submitReq hexString content:"
								+ request.getData().getHexDump());
						currentMsg.setSendTime(Calendar.getInstance());
						sendedList.put(request.getSequenceNumber(), currentMsg);
						session.submit(request);
						lastActiveTime = System.currentTimeMillis();
					}
				}
			}
		} catch (Exception e) {
			String warnMsg = "sendPdu failed";
			logger.error(warnMsg, e);
			SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
			AlarmClient.sendCommAlarmNotify(WARN_LEVEL.WORST,
					alarmProperties.getAlarm_content_ack_fail());
		}
	}

	@SuppressWarnings("unchecked")
	public void checkTimeOut(long timeOut) {
		try {
			if (sendedList.size() == windowSize) {
				for (int i = 0; i < sendedList.size(); i++) {
					if (sendedList.keys().hasMoreElements()) {
						int key = sendedList.keys().nextElement();
						if (sendedList.containsKey(key)) {
							Message msg = sendedList.get(key);

							if (msg == null) {
								continue;
							}
							long currTime = Calendar.getInstance()
									.getTimeInMillis();
							long expireTime = msg.getSendTime()
									.getTimeInMillis() + timeOut;

							if (currTime > expireTime) {
								AlarmClient
										.sendCommAlarmNotify(
												WARN_LEVEL.WORSE_LEVEL,
												alarmProperties
														.getAlarm_content_send_sms_timeout());
								if (msg.getSendCount() <= sendCount) {
									logger.warn("Resend time out message, smsc didn't deal the message timely!  "
											+ "the dest phoneNum ="
											+ msg.getPhoneNum()
											+ ",msg content HexString ="
											+ (new ByteBuffer(msg
													.getStlMessage()))
													.getHexDump());
									sendedList.remove(key);
									SubmitSM request = sm.submit(msg);

									logger.info("=======> Resend to PhoneNum:"
											+ request.getDestAddr()
													.getAddress()
											+ " send SeqId = "
											+ request.getSequenceNumber()
											+ " ,submitReq hexString content:"
											+ request.getData().getHexDump());

									msg.setSendTime(Calendar.getInstance());
									msg.setSendCount(msg.getSendCount() + 1);
									sendedList.put(request.getSequenceNumber(),
											msg);
									session.submit(request);

								} else {
									logger.warn("time out message, smsc didn't deal the message timely!  "
											+ "the dest phoneNum ="
											+ msg.getPhoneNum()
											+ ",msg content HexString ="
											+ (new ByteBuffer(msg
													.getStlMessage()))
													.getHexDump());

									sendedList.remove(key);
									SmppAckLog.ackLog(msg);

									timeoutFailNum++;
									logger.warn("time out failed number ="
											+ timeoutFailNum);
								}
							}
						}
					}
				}// end-while
			}
		} catch (Exception e) {
			String warnMsg = "checkTimeOut execute failed";
			logger.error(warnMsg, e);
			SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
		}
	}

	private SMReceipt parseReceipt(DeliverSM deliverSM) throws Exception {

		try {
			SMReceipt receipt = new SMReceipt();
			Address address = deliverSM.getSourceAddr();
			String phoneNum = address.getAddress();
			byte[] rep = deliverSM.getShortMessage().getByteMessage();
			String repString = new String(rep);
			String submitDate = "submit date:";
			String doneDate = "done date:";
			String stat = "stat:";
			String err = "err:";
			receipt.setPhoneNum(phoneNum);
			receipt.setDoneDate(repString.substring(
					repString.indexOf(doneDate) + 10,
					repString.indexOf(doneDate) + 20));
			receipt.setSubmitDate(repString.substring(
					repString.indexOf(submitDate) + 12,
					repString.indexOf(submitDate) + 22));
			receipt.setStat(repString.substring(repString.indexOf(stat) + 5,
					repString.indexOf(stat) + 12));
			receipt.setErrCode(repString.substring(repString.indexOf(err) + 4,
					repString.indexOf(err) + 7));
			return receipt;
		} catch (Exception e) {
			throw new Exception("parse receipt is error");
		}
	}

	private boolean checkReceipt(byte esmClass) {
		boolean result = false;
		byte match = (byte) 60;
		if (4 == (match & esmClass)) {
			result = true;
		}
		return result;
	}

}
