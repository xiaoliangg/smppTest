package com.roamingServer.smscgw.util;

import org.apache.log4j.Logger;

public class Tool {
	static final int UNICODE_LENGTH = 4;
	private static final Logger logger = Logger.getLogger(Tool.class);
	private static int CMPP_FILL_ZERO = 0;

	/**
	 * turn int value to HexString e.g. :para = 5,then return 05;para =18 then
	 * return 12;
	 * 
	 * @param para
	 * @return String
	 */
	public static String intToHexString(int para) {
		String hexPara = Integer.toHexString(para);
		if (hexPara.length() % 2 != 0) {
			hexPara = "0" + hexPara;
		}
		return hexPara;
	}

	/**
	 * 
	 * @param data
	 *            source byte[]
	 * @param start
	 *            start index
	 * @param len
	 *            length to get
	 * @return byte[]
	 */
	public static byte[] copyBytes(byte[] data, int start, int len) {

		byte[] returnData = null;
		if (data == null) {
			logger.warn("\n data is null");
			return returnData;
		}
		if (data.length < start + len) {
			logger.warn("\n data is not long enough! datalength=" + data.length
					+ " start=" + start + " len" + len);
			return returnData;
		}
		if (len > 0) {
			returnData = new byte[len];
			for (int i = 0; i < len; i++) {
				returnData[i] = data[i + start];
			}
		}
		return returnData;
	}

	/**
	 * ��srcBytes���鿽�������ַ�destBytes����
	 * 
	 * @param srcBytes
	 *            Դbyte����
	 * @param destBytes
	 *            Ŀ��byte����
	 * @param srcInx
	 *            Դ������ʼλ��
	 * @param len
	 *            ��������
	 * @param destInx
	 *            Ŀ��byte��ʼλ��
	 */
	public static void bytesCopyTo(byte[] srcBytes, byte[] destBytes,
			int srcInx, int len, int destInx) {
		int n = srcInx + len - srcBytes.length; // ��Ҫ����λ��
		if (n < 0) {
			n = 0;
		}// �ַ����ʱ��,���в������:0-����,1-�Ҳ���
		if (CMPP_FILL_ZERO == 0) { // ����
			for (int i = 0; i < n; i++) {
				destBytes[destInx + i] = '0';
			}
			for (int i = n, j = srcInx; i < len; i++, j++) {
				destBytes[destInx + i] = srcBytes[j];
			}
		} else { // �Ҳ���
			for (int i = 0, j = srcInx; i < len - n; i++, j++) {
				destBytes[destInx + i] = srcBytes[j];
			}
			for (int i = len - n; i < n; i++) {
				destBytes[destInx + i] = 0x00;
			}
		}
	}

	public static byte[] intToByte(int i) {
		byte abyte[] = new byte[1];
		abyte[0] = (byte) (0xff & i);
		return abyte;
	}

	public static byte[] intToBytes2(int i) {
		byte abyte[] = new byte[2];
		abyte[1] = (byte) (0xff & i);
		abyte[0] = (byte) ((0xff00 & i) >> 8);
		return abyte;
	}

	public static byte[] intToBytes3(int i) {
		byte abyte[] = new byte[3];
		abyte[2] = (byte) (0xff & i);
		abyte[1] = (byte) ((0xff00 & i) >> 8);
		abyte[0] = (byte) ((0xff0000 & i) >> 16);
		return abyte;
	}

	public static byte[] intToBytes4(int i) {
		byte abyte[] = new byte[4];
		abyte[3] = (byte) (0xff & i);
		abyte[2] = (byte) ((0xff00 & i) >> 8);
		abyte[1] = (byte) ((0xff0000 & i) >> 16);
		abyte[0] = (byte) ((0xff000000 & i) >> 24);
		return abyte;
	}

	public static int hexStringToInt(String hex) {
		if (hex == null) {
			logger.error("the input param hex is null");
			return -1;
		}
		hex = hex.toUpperCase();
		int max = hex.length();
		int result = 0;
		for (int i = max; i > 0; i--) {
			char c = hex.charAt(i - 1);
			int algorism = 0;
			if (c >= '0' && c <= '9') {
				algorism = c - '0';
			} else {
				algorism = c - 55;
			}
			result += Math.pow(16, max - i) * algorism;
		}
		return result;
	}

	/**
	 * Convert HEX string consisted of [0-F] into byte array
	 */
	public static byte[] hexStringToByte(String s) {
		byte[] bs = new byte[s.length()];
		byte[] bytes = new byte[s.length() / 2];

		for (int i = 0; i < s.length(); i++) {
			if ((s.charAt(i) >= 'A') && (s.charAt(i) <= 'F')) {
				bs[i] = (byte) (s.charAt(i) - 'A' + 10);
			} else if ((s.charAt(i) >= 'a') && (s.charAt(i) <= 'f')) {
				bs[i] = (byte) (s.charAt(i) - 'a' + 10);
			} else {
				bs[i] = (byte) (s.charAt(i) - '0');
			}
		}

		for (int i = 0; i < bs.length / 2; i++) {
			bytes[i] = (byte) ((bs[i * 2] << 4) ^ bs[i * 2 + 1]);
		}

		return bytes;
	}

	public static String hexStringToString(String hexString, int encodeType) {
		String result = "";
		int max = hexString.length() / encodeType;
		for (int i = 0; i < max; i++) {
			char c = (char) hexStringToAlgorism(hexString.substring(i
					* encodeType, (i + 1) * encodeType));
			result += c;
		}
		return result;
	}

	public static int hexStringToAlgorism(String hex) {
		hex = hex.toUpperCase();
		int max = hex.length();
		int result = 0;
		for (int i = max; i > 0; i--) {
			char c = hex.charAt(i - 1);
			int algorism = 0;
			if (c >= '0' && c <= '9') {
				algorism = c - '0';
			} else {
				algorism = c - 55;
			}
			result += Math.pow(16, max - i) * algorism;
		}
		return result;
	}

	public static boolean isChinese(String tmp) {
		boolean tag = false;
		for (int i = 0; i < tmp.getBytes().length; i++) {
			if ((tmp.getBytes()[i] < 0)) {
				tag = true;
			}
		}
		return tag;
	}

	public static String encodeUnicode(String msg) throws Exception {
		StringBuffer buffer = new StringBuffer();
		char[] msgBuffer = msg.toCharArray();
		for (int i = 0; i < msg.length(); i++) {
			String hex = new Formatter("%04X").form((short) msgBuffer[i]);
			buffer.append(hex.substring(hex.length() - UNICODE_LENGTH));
		}
		return (buffer.toString());
	}

	public static String getHexDump(byte[] data) {
		String dump = "";
		try {
			int dataLen = data.length;
			byte[] buffer = data;
			for (int i = 0; i < dataLen; i++) {
				dump += Character.forDigit((buffer[i] >> 4) & 0x0f, 16);
				dump += Character.forDigit(buffer[i] & 0x0f, 16);
			}
		} catch (Throwable t) {
			// catch everything as this is for debug
			dump = "Throwable caught when dumping = " + t;
		}
		return dump;
	}
}
