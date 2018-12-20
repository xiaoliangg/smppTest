package com.roamingServer.smscgw.alarm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AlarmProperties {

	private static AlarmProperties instance = null;
	private String alarm_content_ack_fail;
	private String alarm_content_bind_fail;
	private String alarm_content_enquireLink_check_fail;
	private String alarm_content_send_sms_timeout;

	private final String propsFilePath = "./conf/alarm.properties";
	private final Properties properties = new Properties();

	private final Logger logger = Logger.getLogger(AlarmProperties.class);

	private AlarmProperties() {
		loadProperties();
	}

	public static synchronized AlarmProperties getInstance() {
		if (instance == null) {
			instance = new AlarmProperties();
		}
		return instance;
	}

	private void loadProperties() {
		FileInputStream propsFile;
		try {
			propsFile = new FileInputStream(propsFilePath);
			properties.load(propsFile);
			propsFile.close();
		} catch (FileNotFoundException e) {
			logger.error("./conf/alarm.properties not found", e);
		} catch (IOException e) {
			logger.error("load ./conf/alarm.properties failed!", e);
		}

		alarm_content_ack_fail = properties
				.getProperty("alarm_content_ack_fail");
		alarm_content_bind_fail = properties
				.getProperty("alarm_content_bind_fail");
		alarm_content_enquireLink_check_fail = properties
				.getProperty("alarm_content_enquireLink_check_fail");
		alarm_content_send_sms_timeout = properties
				.getProperty("alarm_content_send_sms_timeout");
	}

	public String getAlarm_content_ack_fail() {
		return alarm_content_ack_fail;
	}

	public void setAlarm_content_ack_fail(String alarm_content_ack_fail) {
		this.alarm_content_ack_fail = alarm_content_ack_fail;
	}

	public String getAlarm_content_bind_fail() {
		return alarm_content_bind_fail;
	}

	public void setAlarm_content_bind_fail(String alarm_content_bind_fail) {
		this.alarm_content_bind_fail = alarm_content_bind_fail;
	}

	public String getAlarm_content_enquireLink_check_fail() {
		return alarm_content_enquireLink_check_fail;
	}

	public void setAlarm_content_enquireLink_check_fail(
			String alarm_content_enquireLink_check_fail) {
		this.alarm_content_enquireLink_check_fail = alarm_content_enquireLink_check_fail;
	}

	public String getAlarm_content_send_sms_timeout() {
		return alarm_content_send_sms_timeout;
	}

	public void setAlarm_content_send_sms_timeout(
			String alarm_content_send_sms_timeout) {
		this.alarm_content_send_sms_timeout = alarm_content_send_sms_timeout;
	}

}
