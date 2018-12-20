package com.roamingServer.smscgw.util;

import java.util.List;

/**
 * 断言类，用以验证参数的合法性
 * 
 * 
 */
public abstract class Assert {

	/**
	 * 断言 expression 表达式是否为 true， 如果 expression 表达式为 false
	 * 时，抛出IllegalArgumentException
	 * 
	 * @param expression
	 *            一个boolean型的表达式
	 * @param message
	 *            断言失败时的错误提示信息
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 断言 object 对象是否不为null， 如果 object 对象为 null 时，抛出 IllegalArgumentException
	 * 
	 * @param object
	 *            被检测的对象
	 * @param message
	 *            断言失败时的错误提示信息
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 断言String类型的 text 对象不能为 null 或者为空， 否则抛出 IllegalArgumentException
	 * 
	 * @param text
	 *            被检测的 String 对象
	 * @param message
	 *            断言失败时的错误提示信息
	 */
	public static void hasLength(String text, String message) {
		if (!StringUtil.hasLength(text)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 断言数组不为空， 否则抛出 IllegalArgumentException
	 * 
	 * @param array
	 *            被检测的数组
	 * @param message
	 *            断言失败时的错误提示信息
	 */
	public static void notEmpty(Object[] array, String message) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 断言集合不为空 否则抛出 IllegalArgumentException
	 * 
	 * @param list
	 *            被检测的集合
	 * @param message
	 *            断言失败时的错误提示信息
	 */
	public static void notEmpty(List<?> list, String message) {
		if (list == null || list.size() <= 0) {
			throw new IllegalArgumentException(message);
		}
	}
}
