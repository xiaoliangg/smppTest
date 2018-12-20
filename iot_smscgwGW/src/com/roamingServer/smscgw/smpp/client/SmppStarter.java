package com.roamingServer.smscgw.smpp.client;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.PropertyConfigurator;

import com.logica.smpp.SmppObject;
import com.logica.smpp.debug.Debug;
import com.logica.smpp.debug.DefaultDebug;
import com.roamingServer.smscgw.alarm.AlarmProperties;
import com.roamingServer.smscgw.smpp.Message;
import com.roamingServer.smscgw.smpp.MsgTypeConst;
import com.roamingServer.smscgw.smpp.PDUWorker;
import com.roamingServer.smscgw.smpp.PDUWorkerFactory;
import com.roamingServer.smscgw.smpp.SmppProperties;
import com.roamingServer.smscgw.util.BlockingQueueMessageCacheImpl;
import com.roamingServer.smscgw.util.IMessageCache;
import com.watchdata.commons.lang.WDByteUtil;

public class SmppStarter implements Runnable {

	public static SmppProperties gp = null;
	public static IMessageCache<Message> sendQueue;
	public static IMessageCache<Message> receiveQueue;
	public static AlarmProperties alarmProperties = null;

	private Debug debug = null;
	private int threadNum = 1;
	private String bindMode = "tr";

	public SmppStarter(IMessageCache<Message> sendQueue,
			IMessageCache<Message> receiveQueue) {
		this.sendQueue = sendQueue;
		this.receiveQueue = receiveQueue;
		this.gp = SmppProperties.getInstance();
		this.threadNum = gp.getThreadNum();
		this.bindMode = gp.getBindMode();
		this.alarmProperties = AlarmProperties.getInstance();
	}

	public void initDebug() {
		debug = new DefaultDebug();
		debug.activate();
		SmppObject.setDebug(debug);
	}

	@Override
	public void run() {
		for (int i = 0; i < threadNum; i++) {
			PDUWorker pDUWorker = PDUWorkerFactory.getInstance().getPDUWorker(
					bindMode);
			Thread pDUWorkerThread = new Thread(pDUWorker);
			pDUWorkerThread.start();
		}

	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		PropertyConfigurator.configure("conf/log4j.properties");
		IMessageCache<Message> lq = new BlockingQueueMessageCacheImpl<Message>(
				1000000);
		IMessageCache<Message> lq2 = new BlockingQueueMessageCacheImpl<Message>(
				1000000);
		SmppStarter st = new SmppStarter(lq, lq2);

		// String phoneNum = "+85262179260";
		String phoneNum = "+85262179263";
		String sigPhoneNum = "+6582399241";
		String chinaPhoneNum = "8613601113344";
		String enPhoneNum = "+447872250180";
        String mayueVPhoneNum = "19406030486";
                                 
		//测试信息
//		 Message sm = new Message();
//		 sm.setSeqNo(600);
//		 sm.setPhoneNum(mayueVPhoneNum);
//    	 sm.setPhoneNum("+447700029118");
//		 sm.setPhoneNum("+447700061687");

		 
//		 sm.setMsgFmt(MsgTypeConst.msgNormal);
//		 String ss="Happy New Year";
//		 byte[] bb=ss.getBytes("GBK");
	 // sm.setStlMessage(new byte[] { 0x31, 0x31, 0x31 });
//		 sm.setStlMessage(bb);
//		 SmppStarter.sendQueue.put(sm);
		

		// 鍙戦�鏁版嵁鐭俊
		Message dataSm = new Message();
		dataSm.setSeqNo(700);
		dataSm.setPhoneNum(mayueVPhoneNum);
//		dataSm.setMsgFmt(MsgTypeConst.msgData);
		dataSm.setMsgFmt(MsgTypeConst.msgDataUnformat);

		String dataSms = "010255EBAFADE154D897EC09F6AED333AEE88A73CF73929126238AA47D94D6C57002D4D2247C0DA816B8720E7FFC42D1785D0BAF0AB33477F55BDC8F217EC7A423BFBE44438C67B58ACBCBDBD044EAC0661B0D6844CF618C6F56AA2200E9919F5A7031195C07FE3529AEBD4D96288A592AB57F21092E6B788700AEF9EB4665BC47AB876689CB";

		dataSm.setStlMessage(WDByteUtil.HEX2Bytes(dataSms));

		SmppStarter.sendQueue.put(dataSm);

		// 鍚姩鎵ц绾跨▼
		st.run();
	}

}
