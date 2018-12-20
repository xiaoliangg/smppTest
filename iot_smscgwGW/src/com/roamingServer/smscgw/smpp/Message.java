package com.roamingServer.smscgw.smpp;

import java.util.Calendar;

public class Message {

	private String _phoneNum;
	private long _seqNo = 0;
	private byte[] _sspMessage;

	/**
	 * smpp网关发送此消息的时间
	 */
	private Calendar sendTime;

	/**
	 * 信息格式：数据短信/普通短信,MsgTypeConst中定义
	 */
	private int msgFmt = MsgTypeConst.msgData;

	/**
	 * 标识Message承载的信息类型,具体值见MsgTypeConst
	 */
	private int msgType;

	private int sendCount = 1;// 此消息被发送的次数，默认为1

	/**
	 * @return the sendCount
	 */
	public int getSendCount() {
		return sendCount;
	}

	/**
	 * @param sendCount
	 *            the sendCount to set
	 */
	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	/**
	 * @return the sendTime
	 */
	public Calendar getSendTime() {
		return sendTime;
	}

	/**
	 * @param sendTime
	 *            the sendTime to set
	 */
	public void setSendTime(Calendar sendTime) {
		this.sendTime = sendTime;
	}

	public String getPhoneNum() {
		return _phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		_phoneNum = phoneNum;
	}

	public void setSeqNo(long id) {
		_seqNo = id;
	}

	public long getSeqNo() {
		return _seqNo;
	}

	public byte[] getStlMessage() {
		return _sspMessage;
	}

	public void setStlMessage(byte[] sspMessage) {
		_sspMessage = sspMessage;
	}

	/**
	 * @return the msgFmt
	 */
	public int getMsgFmt() {
		return msgFmt;
	}

	/**
	 * @param msgFmt
	 *            the msgFmt to set
	 */
	public void setMsgFmt(int msgFmt) {
		this.msgFmt = msgFmt;
	}

	/**
	 * @return the msgType
	 */
	public int getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType
	 *            the msgType to set
	 */
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

}
