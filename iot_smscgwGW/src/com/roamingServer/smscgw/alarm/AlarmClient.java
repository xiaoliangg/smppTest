package com.roamingServer.smscgw.alarm;

import com.roamingServer.alarm.constant.AlarmConstant.WARN_LEVEL;
import com.roamingServer.alarm.constant.AlarmConstant.WARN_SOURCE;
import com.roamingServer.alarm.constant.AlarmConstant.WARN_TYPE;
import com.roamingServer.alarm.jms.AlarmSender;
import com.roamingServer.alarm.pojo.AlarmMessage;

public class AlarmClient {
	public static void sendCommAlarmNotify(WARN_LEVEL warnlevel,
			String alarmContent) {
		AlarmSender.send(new AlarmMessage(WARN_SOURCE.GW_C9, warnlevel,
				alarmContent, WARN_TYPE.COMMUNICATE_ALARM));
	}

	public static void sendBusiAlarmNotify(WARN_LEVEL warnlevel,
			String alarmContent) {
		AlarmSender.send(new AlarmMessage(WARN_SOURCE.GW_C9, warnlevel,
				alarmContent, WARN_TYPE.BUSINESS_ALARM));
	}
}
