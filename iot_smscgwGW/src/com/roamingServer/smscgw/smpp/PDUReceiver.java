package com.roamingServer.smscgw.smpp;

import static com.roamingServer.smscgw.smpp.client.SmppStarter.receiveQueue;

import com.logica.smpp.SmppException;
import com.logica.smpp.pdu.DeliverSM;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.util.ByteBuffer;

public class PDUReceiver extends PDUWorker {
	public PDUReceiver() {
	}

	@Override
	public void doCommunication() {
		try {
			PDU pdu = null;
			pdu = SMPPClientPDUEventListener.getPDU(timeout);
			if (pdu != null) {
				sm.giveRsp(pdu);
				// 对pdu进行判读
				if ((pdu instanceof DeliverSM)) {
					lastActiveTime = System.currentTimeMillis();
					// 如果它是msmc主动请求或者对submit的响应，则放入上行消息队列
					DeliverSM requestMessage = (DeliverSM) pdu;
					logger.info("<---------A Mo request from phone:"
							+ requestMessage.getSourceAddr().getAddress()
							+ ",\n DeliverSM request packet(hexString):"
							+ requestMessage.getData().getHexDump());

					Message satMsg = new Message();
					satMsg.setPhoneNum(sm.getFormattedPhoneNum(requestMessage
							.getSourceAddr().getAddress()));
					satMsg.setStlMessage(requestMessage.getShortMessage()
							.getByteMessage());
					receiveQueue.put(satMsg);
					logger.info("in mo,requestMessage from phone:"
							+ satMsg.getPhoneNum()
							+ ",is:"
							+ new ByteBuffer(requestMessage.getShortMessage()
									.getByteMessage()).getHexDump());
				}
				sm.dealRsp(pdu);
			}

			long currentDateTime = System.currentTimeMillis();
			if ((currentDateTime - lastActiveTime) >= checkInterval) {
				lastActiveTime = currentDateTime;
				sm.enquireLink();// 心跳检测
			}

		} catch (SmppException e) {
			String warnMsg = "enquireLink to smsc is broken";
			logger.error(warnMsg, e);
			SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
		} catch (Exception e) {
			String warnMsg = "Pdu receiver failed!!";
			logger.error(warnMsg, e);
			SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
			sm.close();
		}
	}

}
