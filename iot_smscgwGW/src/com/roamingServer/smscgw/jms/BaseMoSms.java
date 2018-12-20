package com.roamingServer.smscgw.jms;

import java.io.Serializable;

/**
 * Mo 短信基类
 * 
 * @author Administrator
 * 
 */
public class BaseMoSms implements Serializable {

	private static final long serialVersionUID = -2133842161314633563L;

	private String cmdType;// 报文类型

	private String mobile; // 手机号

	private String ussdPre; // ussd上发信息的前缀信息

	private String ussdSuffix;// ussd上发信息的后缀

	private String msg;// 整条报文

	private String channel;// 短信对象来源： 0-来源于卡片；1-来源于码号平台

	private String operatorCode;

	private String imsi;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {

		this.mobile = mobile;
	}

	public String getUssdPre() {
		return ussdPre;
	}

	public void setUssdPre(String ussdPre) {
		this.ussdPre = ussdPre;
	}

	public String getUssdSuffix() {
		return ussdSuffix;
	}

	public void setUssdSuffix(String ussdSuffix) {
		this.ussdSuffix = ussdSuffix;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCmdType() {
		return cmdType;
	}

	public void setCmdType(String cmdType) {
		this.cmdType = cmdType;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

}
