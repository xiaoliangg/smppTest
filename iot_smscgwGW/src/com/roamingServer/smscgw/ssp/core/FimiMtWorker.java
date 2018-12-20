package com.roamingServer.smscgw.ssp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.iot.bean.cache.SmsMsgBean;
import com.logica.smpp.util.ByteBuffer;
import com.roamingServer.smscgw.exception.ClientRuntimeException;
import com.roamingServer.smscgw.smpp.Message;
import com.roamingServer.smscgw.smpp.MsgTypeConst;
import com.roamingServer.smscgw.util.MsgCommonTool;
import com.watchdata.commons.lang.WDByteUtil;

public class FimiMtWorker implements Callable<Message[]> {

	private SmsMsgBean mtSms;// 下发的下行短信

	private Message[] satMessageArray;

	private final Logger logger = Logger.getLogger(this.getClass());

	public FimiMtWorker(SmsMsgBean mtSms) {
		this.mtSms = mtSms;
	}

	@Override
	public Message[] call() {

		try {

			List<byte[]> packetsList = null;

			packetsList = packMsg();

			return getMessageArray(packetsList);

		} catch (Exception e) {
			logger.error("FimiMtWorker execute failed", e);
			throw new ClientRuntimeException(e.getMessage());
		}

	}

	@SuppressWarnings("unused")
	private Message[] getMessageArray(List<byte[]> packetsList) {
		satMessageArray = new Message[packetsList.size()];
		for (int i = 0; i < packetsList.size(); i++) {
			byte[] currentPacket = packetsList.get(i);

			Message satMessage = new Message();
			// satMessage.setSeqNo(Long.parseLong(mtSms.getLogId()));
			satMessage.setSeqNo(1l);
			satMessage.setStlMessage(currentPacket);
			satMessage.setPhoneNum(mtSms.getMsisdn());
			logger.error(">>>>>>>>>>>>>>>mtMsg:" + mtSms.getMsg());

			if ("0".equals(mtSms.getSmsType())) {
				logger.error(">>>>>>>>>>>>>>>Normal Message!");
				satMessage.setMsgType(MsgTypeConst.msgNormal);
				satMessage.setMsgFmt(MsgTypeConst.msgNormal);
			} else {
				logger.error(">>>>>>>>>>>>>>>Data Message!");
				satMessage.setMsgType(MsgTypeConst.msgData);
				// 将basemt中的短信类型直接赋值到Message中的msgFmt
				satMessage.setMsgFmt(Integer.parseInt(mtSms.getSmsType()));
			}

			logger.error(">>>>>>>>>>>>>>>MeGType:" + satMessage.getMsgType());
			satMessageArray[i] = satMessage;
		}
		return satMessageArray;
	}

	public List<byte[]> packMsg() throws NumberFormatException, Exception {

		ByteBuffer bf = new ByteBuffer();

		// 源代码
		// byte[] msgContent = WDByteUtil.HEX2Bytes(mtSms.getMsg());
		// bf.appendBytes(msgContent);

		if (mtSms.getSmsType().equalsIgnoreCase("0")) {// 普通短信
			bf.appendBytes(mtSms.getMsg().getBytes("gbk"));// substring(1).getBytes());
		} else {// 数据短信
			bf.appendBytes(WDByteUtil.HEX2Bytes(mtSms.getMsg()));
		}

		List<byte[]> packets = new ArrayList<byte[]>();
		packets = MsgCommonTool.messageList(bf.getBuffer(), 0, 0);
		return packets;
	}

	public SmsMsgBean getMtSms() {
		return mtSms;
	}

	public void setMtSms(SmsMsgBean mtSms) {
		this.mtSms = mtSms;
	}

}
