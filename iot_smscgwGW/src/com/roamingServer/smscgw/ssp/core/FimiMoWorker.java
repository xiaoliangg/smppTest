package com.roamingServer.smscgw.ssp.core;

import org.apache.log4j.Logger;

import com.roamingServer.smscgw.jms.BaseMoSms;
import com.roamingServer.smscgw.smpp.Message;
import com.roamingServer.smscgw.smpp.SmppProperties;
import com.watchdata.commons.lang.WDByteUtil;

public class FimiMoWorker implements Runnable {

	private SspServerContext sspContext = null;

	protected WorkerContext workerContext = null;

	protected Logger logger = Logger.getLogger(this.getClass());

	public FimiMoWorker(SspServerContext context, Message satMessage) {
		sspContext = context;
		workerContext = new WorkerContext();
		workerContext.phone = satMessage.getPhoneNum();
		workerContext.workID = Long.valueOf(sspContext._browerID.getLogid(0));
		workerContext.reqMessage = satMessage.getStlMessage();
	}

	@Override
	public void run() {
		logger.info("a new srequest process, the terminate is "
				+ workerContext.phone);

		// 转换成上行业务短信对象
		BaseMoSms moSms = new BaseMoSms();
		moSms.setMobile(workerContext.phone);
		moSms.setMsg(WDByteUtil.bytes2HEX(workerContext.reqMessage));
		moSms.setOperatorCode(SmppProperties.getInstance().getOperatorCode());
		SspServerContext.putJmsMessage(moSms);

	}

	protected static class WorkerContext {
		byte[] reqMessage = null;
		byte[] respMessage = null;
		String phone = null;
		int workerErrorCode = 0;
		long workID = 0;
	}

	public String getPhone() {
		return workerContext.phone;
	}

	public long getWorkID() {
		return workerContext.workID;
	}

}
