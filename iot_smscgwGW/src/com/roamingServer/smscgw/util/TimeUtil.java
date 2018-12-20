package com.roamingServer.smscgw.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间操作工具类
 * 
 * 
 */
public abstract class TimeUtil {

	/**
	 * 获取当前系统时间
	 * 
	 * @return java.util.Date 对象
	 */
	public static Date getSystemTime() {
		return new Date();
	}

	/**
	 * 获取当前系统时间的字符串格式，以yyyyMMddHHmmss的格式表示
	 * 
	 * @return
	 */
	public static String getStringToday() {
		Date currentTime = new Date();
		return format(currentTime, "yyyyMMddHHmmss");
	}

	/**
	 * 将传入的 java.util.Date 对象，按 'yyyy-MM-dd' 的格式进行格式化
	 * 
	 * @param date
	 *            需要格式化的 java.util.Date 对象
	 * @return 已格式化的时间字符串
	 */
	public static String getFormatDate(Date date) {
		return format(date, "yyyy-MM-dd");
	}

	/**
	 * 将传入的 java.util.Date 对象，按所指定的格式进行格式化
	 * 
	 * @param date
	 *            时间对象
	 * @param formatStr
	 *            指定格式
	 * @return
	 */
	public static String getFormat(Date date, String formatStr) {
		return format(date, formatStr);
	}

	/**
	 * 将传入的 java.util.Date 对象，按 'hh:mm:ss' 的格式进行格式化
	 * 
	 * @param date
	 *            需要格式化的 java.util.Date 对象
	 * @return 已格式化的时间字符串
	 */
	public static String getFormatTime(Date date) {
		return format(date, "hh:mm:ss");
	}

	/**
	 * 将传入的 java.util.Date 对象，按'yyyy-MM-dd hh:mm:ss'的格式进行格式化
	 * 
	 * @param date
	 *            需要格式化的 java.util.Date 对象
	 * @return 已格式化的时间字符串
	 */
	public static String getFormatDateTime(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String format(Date date, String formatStr) {
		Assert.notNull(date, "[Assertion failed] - this date can't be null");
		Assert.notNull(formatStr,
				"[Assertion failed] - this formate string can't be null");

		SimpleDateFormat formater = new SimpleDateFormat(formatStr);
		return formater.format(date);
	}

	public static Date format(String pattern, String str) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			return new Date();
		}
	}

}
