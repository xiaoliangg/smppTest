package com.roamingServer.smscgw.smpp;

public class SMReceipt implements java.io.Serializable {

	private static final long serialVersionUID = -3857302271901933882L;
	private String phoneNum;

	private String stat;

	/*
	 * Where appropriate this may hold a Network specific error code or an SMSC
	 * error code for the attempted delivery of the message. These errors are
	 * Network or SMSC specific and are not included here.
	 */
	private String errCode;

	// YYMMDDhhmm
	private String submitDate;

	// YYMMDDhhmm
	private String doneDate;

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(String submitDate) {
		this.submitDate = submitDate;
	}

	public String getDoneDate() {
		return doneDate;
	}

	public void setDoneDate(String doneDate) {
		this.doneDate = doneDate;
	}

}
