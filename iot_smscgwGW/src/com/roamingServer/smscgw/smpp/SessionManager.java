package com.roamingServer.smscgw.smpp;

import static com.roamingServer.smscgw.smpp.client.SmppStarter.alarmProperties;
import static com.roamingServer.smscgw.smpp.client.SmppStarter.gp;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.logica.smpp.Data;
import com.logica.smpp.Session;
import com.logica.smpp.TCPIPConnection;
import com.logica.smpp.TimeoutException;
import com.logica.smpp.WrongSessionStateException;
import com.logica.smpp.pdu.BindReceiver;
import com.logica.smpp.pdu.BindRequest;
import com.logica.smpp.pdu.BindResponse;
import com.logica.smpp.pdu.BindTransciever;
import com.logica.smpp.pdu.BindTransmitter;
import com.logica.smpp.pdu.EnquireLink;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.pdu.PDUException;
import com.logica.smpp.pdu.Request;
import com.logica.smpp.pdu.Response;
import com.logica.smpp.pdu.SubmitSM;
import com.logica.smpp.pdu.UnbindResp;
import com.logica.smpp.pdu.ValueNotSetException;
import com.roamingServer.alarm.constant.AlarmConstant.WARN_LEVEL;
import com.roamingServer.smscgw.alarm.AlarmClient;
import com.roamingServer.smscgw.util.Tool;
import com.watchdata.commons.lang.WDStringUtil;

public class SessionManager {
	private static int seqId = 1;
	private Session session;
	private boolean bound = false;
	private final Logger logger = Logger.getLogger(SessionManager.class);
	private static Lock lock = new ReentrantLock();

	public SessionManager() {

	}

	// TODO 方法加锁
	public Session bind(String bindMode) {
		lock.lock();
		logger.warn("start bind,lock fisrt");
		try {
			if (bound) {
				logger.warn("Already bound, unbind first.");
				return session;
			}

			BindRequest request = null;
			BindResponse response = null;
			if (bindMode.equals("r")) {// receive
				request = new BindReceiver();
				logger.info("Bind mode:R mode");
			} else if (bindMode.equals("t")) {// send
				request = new BindTransmitter();
				logger.info("Bind mode:T mode");
			} else {
				request = new BindTransciever();
				logger.info("Bind mode:TR mode");
			}

			TCPIPConnection connection = new TCPIPConnection(gp.getIpAddress(),
					gp.getPort());
			connection.setCommsTimeout(gp.getConnTimeout());
			connection.setReceiveTimeout(gp.getReceiveTimeout());
			session = new Session(connection);

			// set values
			request.setSystemId(gp.getSystemId());
			request.setPassword(gp.getPassword());
			request.setSystemType(gp.getSystemType());
			request.setInterfaceVersion((byte) 0x34);
			request.setAddressRange(gp.getAddressRange());

			String hexBindReq = request.getData().getHexDump();
			logger.debug("bind data hexString =" + hexBindReq);
			// TODO 设置bound=false
			bound = false;
			response = session.bind(request, new SMPPClientPDUEventListener());
			if (response != null
					&& response.getCommandStatus() == Data.ESME_ROK) {
				bound = true;
				session.setBound(true);
				logger.info("bind to smsc " + gp.getSystemId()
						+ " ,Bind to the SMPP server on address :"
						+ gp.getIpAddress() + ":" + gp.getPort());

				logger.debug("bind response seqid = "
						+ response.getSequenceNumber());

			} else {
				logger.warn("Cannot bind to smsc " + gp.getSystemId()
						+ " Bind to the SMPP server : " + gp.getIpAddress()
						+ ":" + gp.getPort() + " failed!!!");
			}

		} catch (Exception e) {
			logger.error("Bind operation failed. ", e);
			AlarmClient.sendCommAlarmNotify(WARN_LEVEL.WORST,
					alarmProperties.getAlarm_content_bind_fail());
		} finally {
			logger.warn("finish bind,unlock");
			lock.unlock();
		}
		return session;
	}

	public Session rebind(String bindMode) {
		String errorMsg = "Connect with smsc failed,cannot communicate any more";
		logger.error(errorMsg);
		SmppAckLog.ackErrorLog(errorMsg);

		try {
			Thread.sleep(5000);
			logger.debug("Rebind now...");
			session = bind(bindMode);

		} catch (Exception e) {
			logger.error("#############################################\n"
					+ " Cannot connect with smsc, " + e.getMessage()
					+ "\n ############################################# ");
		}
		return session;
	}

	public void unbind() {
		try {
			if (!bound) {
				logger.error("Not bound, cannot unbind.");
				return;
			}

			// send the request
			logger.info("Going to unbind.");
			if (session.getReceiver().isReceiver()) {
				logger.info("It could take a while to stop the receiver.");
			}
			UnbindResp response = session.unbind();
			logger.info("Unbind response " + response.debugString());
			bound = false;

		} catch (Exception e) {
			logger.error("Unbind operation failed. ", e);
		}
	}

	public void enquireLink() {
		EnquireLink request = new EnquireLink();
		logger.debug("-->Send enquireLink to smc");
		request.assignSequenceNumber();
		try {
			session.enquireLink(request);
		} catch (Exception e) {
			String warnMsg = "enquireLink check failed!!";
			logger.error(warnMsg, e);
			AlarmClient.sendCommAlarmNotify(WARN_LEVEL.WORST,
					alarmProperties.getAlarm_content_enquireLink_check_fail());
			SmppAckLog.ackErrorLog(warnMsg + e.getMessage());
			close();
		}
	}

	public void giveRsp(PDU pdu) {
		if (pdu != null && pdu.isRequest()) {
			Response response = ((Request) pdu).getResponse();
			logger.info("<---receive a "
					+ pdu.getClass().getName().substring(20)
					+ "\n -->give response to smc" + response.debugString());
			try {
				session.respond(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void dealRsp(PDU pdu) throws ValueNotSetException, TimeoutException,
			PDUException, WrongSessionStateException, IOException {
		if (pdu != null && pdu.isResponse()) {
			Response response = ((Response) pdu);
			if (response.getCommandStatus() == Data.ESME_ROK) {
				logger.debug("<-- Receive "
						+ response.getClass().getName().substring(20)
						+ " from smsc ��ok");
			} else {
				logger.error("<-- Receive "
						+ response.getClass().getName().substring(20)
						+ " from smsc ��status error,unbind now");
				session.unbind();
			}
		}
	}

	public void close() {
		session.setBound(false);
		setBound(false);
		try {
			if (session != null) {
				session.close();
			}
		} catch (Exception e1) {
			logger.error("session.close() failed");
		}
	}

	public SubmitSM submit(Message message) {
		SubmitSM request = new SubmitSM();
		try {
			request.setSequenceNumber(createId());
			request.setServiceType(gp.getServiceType());
			request.setSourceAddr(gp.getSourceAddress());
			request.setDestAddr(gp.getDestAddress().getTon(), gp
					.getDestAddress().getNpi(), message.getPhoneNum());
			request.setEsmClass(gp.getEsmClass());
			request.setProtocolId(gp.getProtocolId());
			request.setPriorityFlag(gp.getPriorityFlag());

			if ("1".equalsIgnoreCase(gp.getIsRegisteredDelivery())) {
				// if (message.getMsgFmt() == MsgTypeConst.msgData) {
				request.setRegisteredDelivery(gp.getRegisteredDelivery());
				// } else {
				// request.setRegisteredDelivery((byte) 0);
				// }
			} else {
				request.setRegisteredDelivery((byte) 0);
			}

			request.setShortMessage(new String(message.getStlMessage(),
					"iso8859-1"), "iso8859-1");

			if (message.getMsgFmt() == MsgTypeConst.msgData
					|| message.getMsgFmt() == MsgTypeConst.msgDataUnformat) {
				request.setDataCoding(gp.getDataCoding());
				if (message.getMsgFmt() == MsgTypeConst.msgDataUnformat) {
					// 如果是非格式化数据短信，将esmclass设置为0
					request.setEsmClass(gp.getUnformatEsmClass());
				}
			} else {
				byte emclass = 0;
				request.setEsmClass(emclass);

				String shortMsg = new String(message.getStlMessage(), "GBK");

				request.setDataCoding(gp.getNormalMsgDataCoding());

				logger.debug("shortMessage information, message=" + shortMsg);

				String hexStr = WDStringUtil.asc2hex(shortMsg);
				if (Tool.isChinese(shortMsg)) {
					request.setDataCoding(gp.getNormalChineseMsgDataCoding());
					hexStr = Tool.encodeUnicode(shortMsg);
				}
				byte[] msgContent = Tool.hexStringToByte(hexStr);
				request.setShortMessage(new String(msgContent, "iso8859-1"),
						"iso8859-1");
			}
		} catch (Exception e) {
			logger.error("Submit operation failed. " + e);
			try {
				session.unbind();
			} catch (Exception e1) {
				logger.error("Session Unbind Failed  " + e1);
			}
		}
		return request;
	}

	private synchronized int createId() {
		if (seqId > 1000000000) {
			seqId = 0;
		}
		return seqId++;
	}

	public String getFormattedPhoneNum(String phoneNum) {
		String nationCode = gp.getNationCode();
		if (phoneNum != null && phoneNum.startsWith(nationCode)) {
			phoneNum = phoneNum.substring(nationCode.length());
		}
		return phoneNum;
	}

	/**
	 * @return the bound
	 */
	public boolean isBound() {
		return bound;
	}

	/**
	 * @param bound
	 *            the bound to set
	 */
	public void setBound(boolean bound) {
		this.bound = bound;
	}
}
