package com.roamingServer.smscgw.util;

import java.text.MessageFormat;

/**
 * 字符串的辅导工具类
 * 
 */
public abstract class StringUtil {

	/**
	 * 判断字符串的长度是否拥有长度
	 * 
	 * @param str
	 *            需要判断的字符串
	 * @return 拥有返回 true，否则返回 false
	 */
	public static boolean hasLength(String str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * 将null对象替换为空串，即""
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceEmpty(String str) {
		return replaceEmpty(str, "");
	}

	/**
	 * 使用指定的字符串对象替换null对象
	 * 
	 * @param str
	 * @param replaceStr
	 * @return
	 */
	public static String replaceEmpty(String str, String replaceStr) {
		return (!hasLength(str)) ? replaceStr : str;
	}

	/**
	 * 按字节逆序 例如：
	 * 
	 * <pre>
	 * &quot;000001F4&quot;--&gt;&quot;F4010000&quot;
	 * </pre>
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @return 转换后的字符串
	 */
	public static String reverseByByte(String str) {

		String reverseStr = "";
		for (int i = str.length(); i >= 2; i = i - 2) {
			reverseStr = reverseStr + str.substring(i - 2, i);
		}
		return reverseStr;
	}

	/**
	 * 判断字符串的长度是否拥有指定长度
	 * 
	 * @param str
	 *            需要判断的字符串
	 * @param len
	 *            指定的长度
	 * @return boolean的结果
	 */
	public static boolean hasLength(String str, int len) {
		if (str == null) {
			return false;
		}
		if (str.length() < len) {
			return false;
		}
		return true;
	}

	public static String subString(String str, int start, int end) {
		if (!hasLength(str)) {
			return "";
		}
		try {
			return str.substring(start, end);
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 将字符串填充到固定长度
	 * 
	 * @param src
	 * @param fillCode
	 * @param length
	 * @param type
	 *            "1"表示左填充，"2"表示右填充
	 * @return
	 */
	public static String fillCode(String src, String fillCode, int length,
			String type) {
		if (fillCode.length() != 1) {
			return null;
		}
		if (src.length() > length) {
			return src.substring(0, length);
		}
		StringBuffer result = new StringBuffer();
		if ("1".equals(type)) {
			int le = length - src.length();
			for (int i = 0; i < le; i++) {
				result.append(fillCode);
			}
			result.append(src);
			return result.toString();
		} else {
			result.append(src);
			int le = length - src.length();
			for (int i = 0; i < le; i++) {
				result.append(fillCode);
			}
			return result.toString();
		}
	}

	/**
	 * 根据消息模板和参数，组织消息模板内容
	 * 
	 * @param messageFormat
	 * @param params
	 * @return
	 */
	public static String genMessageFormat(String messageFormat, Object[] params) {
		MessageFormat format = new MessageFormat(messageFormat);
		String message = format.format(params);
		return message;
	}

	public static void main(String[] args) {
		// String test = "20141010";
		// System.out.println(test.compareTo("20141009"));
		String ss = genMessageFormat("111111", null);
		System.out.println(ss);

	}

}
