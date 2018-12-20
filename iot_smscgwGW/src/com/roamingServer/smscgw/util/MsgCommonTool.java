package com.roamingServer.smscgw.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MsgCommonTool {

	public final static int MsgLenStandard = 140;
	private static final Logger logger = Logger.getLogger(MsgCommonTool.class);

	public static List<byte[]> messageList(byte[] msg, int n, int m) {
		if (msg == null) {
			logger.error("msg cannot be null,return null!");
			return null;
		}
		List<byte[]> resultMsgList = new ArrayList<byte[]>();

		int i = 0;
		byte[] currentMsg = null;

		int count = msgTotalNumber(msg, n, m);

		if (count == 1) {
			resultMsgList.add(msg);
		} else {
			while (i < count) {
				if (i == 0) {
					currentMsg = new byte[MsgLenStandard - n];
					System.arraycopy(msg, 0, currentMsg, 0,
							(MsgLenStandard - n));
				} else {
					int tempPos = ((MsgLenStandard - n) + (i - 1)
							* (MsgLenStandard - m));
					if ((tempPos + (MsgLenStandard - m)) <= msg.length) {
						System.out.println("i=" + i);
						currentMsg = new byte[MsgLenStandard - m];
						System.arraycopy(msg, tempPos, currentMsg, 0,
								(MsgLenStandard - m));
					} else {

						currentMsg = new byte[msg.length - tempPos];
						System.arraycopy(msg, tempPos, currentMsg, 0,
								msg.length - tempPos);
					}
				}
				resultMsgList.add(currentMsg);
				currentMsg = null;
				i++;
			}
		}
		return resultMsgList;
	}

	public static int msgTotalNumber(byte[] msg, int n, int m) {
		if (msg == null) {
			logger.error("msg cannot be null,return null!");
			return 0;
		}

		int count = 1;

		int msgLen = msg.length;
		if (msgLen > (MsgLenStandard - n)) {
			byte[] msgLeft = new byte[msgLen - (MsgLenStandard - n)];
			System.arraycopy(msg, (MsgLenStandard - n), msgLeft, 0, msgLen
					- (MsgLenStandard - n));

			count += msgLeft.length / (MsgLenStandard - m);
			if (msgLeft.length % (MsgLenStandard - m) != 0) {
				count++;
			}
		}
		return count;
	}
}
