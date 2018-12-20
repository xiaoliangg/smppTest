package com.roamingServer.smscgw.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class UniqueWorkID {

	private static UniqueWorkID _uniqueID = new UniqueWorkID();

	private final Logger logger = Logger.getLogger(UniqueWorkID.class);

	public static final int seed = 1;

	public static final int smscType = 0;

	private static final int smscIdBegin = 30000000;

	private static final int browserIdMax = 100000000;

	public static UniqueWorkID getInstance() {
		return _uniqueID;
	}

	private String _startTime = null;

	private AtomicInteger _smscInt = new AtomicInteger(smscIdBegin);

	String formDate = "yyyyMMHHmm";

	private SimpleDateFormat bartDateFormat = new SimpleDateFormat(formDate);

	private UniqueWorkID() {
		_startTime = bartDateFormat.format(new Date());
	}

	public String getUniqueID(int id) {

		return _startTime + id;
	}

	public int getSeqID(int type) {
		int seq = 0;

		if (type == smscType) {
			if (seq > browserIdMax) {// MAX_VALUE
				_smscInt.set(smscIdBegin);
				_startTime = bartDateFormat.format(new Date());
			}
			seq = _smscInt.addAndGet(seed);
		}
		return seq;
	}

	public int getSeqIdFromUniqueID(String uniqueId) {
		if (uniqueId == null || uniqueId.length() < formDate.length()) {
			logger.error("in getSeqIdFromUniqueID,the parameter 'uniqueId' is not long enough!");
			return -1;
		}
		String stringId = uniqueId.substring(formDate.length());
		return Integer.parseInt(stringId);
	}

	public static String getLogid(int type) {
		return UniqueWorkID.getInstance().getUniqueID(
				UniqueWorkID.getInstance().getSeqID(type));
	}

}
