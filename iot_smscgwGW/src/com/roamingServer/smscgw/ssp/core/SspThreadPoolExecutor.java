package com.roamingServer.smscgw.ssp.core;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.roamingServer.smscgw.packet.ISspConstant;
import com.roamingServer.smscgw.util.UniqueWorkID;
import com.watchdata.model.logrecord.ErrorLog;

public class SspThreadPoolExecutor extends ThreadPoolExecutor {

	private Logger _logger = Logger.getLogger(this.getClass());

	public SspThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	/**
	 * save the super.method call
	 */
	@Override
	public void beforeExecute(Thread t, Runnable r) {
		// do not delete the super sentence
		super.beforeExecute(t, r);
		_logger.debug("beforeExecute:" + r.hashCode());
	}

	@Override
	public void afterExecute(Runnable r, Throwable t) {
		// do not delete the super sentence
		super.afterExecute(r, t);

		if (t != null) {
			String logid = "";
			ErrorLog errorLog = null;

			if (r.getClass() == FimiMoWorker.class) {
				FimiMoWorker worker = (FimiMoWorker) r;

				logid = UniqueWorkID.getLogid(0);
				errorLog = new ErrorLog(Long.parseLong(logid),
						ISspConstant.ERROR_BLOWER_UNKOWN, t.getMessage(),
						worker.getPhone(), new Date());
				_logger.error("browese worker is error, no packet response to client!"
						+ worker.getPhone() + " userId: " + worker.getPhone());
			}

			SspServerContext.putJmsMessage(errorLog);
		}
	}

	@Override
	protected void terminated() {
		super.terminated();
	}
}
