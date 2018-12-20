package com.roamingServer.smscgw.smpp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.logica.smpp.Data;
import com.logica.smpp.pdu.Address;
import com.logica.smpp.pdu.AddressRange;
import com.logica.smpp.pdu.WrongLengthOfStringException;
import com.roamingServer.smscgw.util.CommonTool;

public class SmppProperties {

	private static SmppProperties instance = null;
	private String ipAddress = null;
	private int port = 0;
	private String systemId = null;
	private String password = null;
	private String bindMode = "r";
	private String serviceType = "";
	private AddressRange addressRange = new AddressRange();
	private Address sourceAddress = new Address();
	private Address destAddress = new Address();
	private int slideWindowSize = 10;
	private long receiveTimeout = Data.RECEIVE_BLOCKING;
	private int connTimeout = 3000;
	private long enquirelinkInterval = 1000;
	private boolean syncMode = true;// true:同步 false 异步

	private String operatorCode;

	private String nationCode = "86";
	private int repeatTime = 3;
	private byte esmClass = 0;
	private byte protocolId = 0;
	private String scheduleDeliveryTime = "";
	private byte priorityFlag = 0;
	private byte registeredDelivery = 0;
	private byte replaceIfPresentFlag = 0;
	private byte dataCoding = (byte) 0xF6;
	private String validityPeriod = "";
	private String systemType = "OTA";
	private byte normalMsgDataCoding = (byte) 0x01;
	private byte normalChineseMsgDataCoding = (byte) 0x01;
	private int threadNum = 1;

	private byte unformatEsmClass = 0;
	private String propsFilePath = "./conf/smppConfig.properties";
	private final Properties properties = new Properties();

	private final Logger logger = Logger.getLogger(SmppProperties.class);

	private String isRegisteredDelivery = "0";

	private SmppProperties() {
		loadProperties();
	}

	public static synchronized SmppProperties getInstance() {
		if (instance == null) {
			instance = new SmppProperties();
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
			logger.error("./conf/smppConfig.properties not found", e);
		} catch (IOException e) {
			logger.error("load ./conf/smppConfig.properties failed!", e);
		}
		byte ton;
		byte npi;
		String addr;

		String smsc = properties.getProperty("smsc").trim();
		if (smsc == null || smsc.length() == 0) {
			throw new RuntimeException("smsc config is error!");
		}
		int index = smsc.indexOf(":");
		if (index > 0) {
			ipAddress = smsc.substring(0, index);
			port = Integer.parseInt(smsc.substring(index + 1).trim());
		}

		systemId = properties.getProperty("systemId").trim();
		password = properties.getProperty("password").trim();

		bindMode = properties.getProperty("bindMode").trim();
		serviceType = properties.getProperty("serviceType").trim();
		systemType = properties.getProperty("systemType").trim();

		// set sourceAddress
		ton = CommonTool.intToByte(Integer.parseInt(properties.getProperty(
				"sourceTon").trim()))[0];
		npi = CommonTool.intToByte(Integer.parseInt(properties.getProperty(
				"sourceNpi").trim()))[0];
		addr = properties.getProperty("sourceAddr");
		setAddressParameter("source-address", sourceAddress, ton, npi, addr);

		// set destinationAddress
		ton = CommonTool.intToByte(Integer.parseInt(properties.getProperty(
				"destTon").trim()))[0];
		npi = CommonTool.intToByte(Integer.parseInt(properties.getProperty(
				"destNpi").trim()))[0];
		addr = properties.getProperty("destAddr").trim();
		setAddressParameter("destination-address", destAddress, ton, npi, addr);

		// set addressRange
		ton = CommonTool.intToByte(Integer.parseInt(properties.getProperty(
				"addrTon").trim()))[0];
		npi = CommonTool.intToByte(Integer.parseInt(properties.getProperty(
				"addrNpi").trim()))[0];
		addr = properties.getProperty("addrRange").trim();
		addressRange.setTon(ton);
		addressRange.setNpi(npi);
		try {
			addressRange.setAddressRange(addr);
		} catch (WrongLengthOfStringException e) {
			e.printStackTrace();
		}

		slideWindowSize = Integer.parseInt(properties.getProperty(
				"slideWindowSize").trim());
		receiveTimeout = Long.parseLong(properties
				.getProperty("receiveTimeout").trim());
		connTimeout = Integer.parseInt(properties.getProperty("connTimeout")
				.trim());
		enquirelinkInterval = Long.parseLong(properties.getProperty(
				"enquirelinkInterval").trim());
		syncMode = properties.getProperty("syncMode", "false").trim()
				.equals("true") ? true : false;
		nationCode = properties.getProperty("nationCode", "").trim();
		repeatTime = Integer.parseInt(properties.getProperty("repeatTime", "3")
				.trim());

		esmClass = CommonTool.intToByte(Integer.parseInt(properties
				.getProperty("esmClass").trim()))[0];
		unformatEsmClass = CommonTool.intToByte(Integer.parseInt(properties
				.getProperty("unformatEsmClass").trim()))[0];
		protocolId = CommonTool.intToByte(Integer.parseInt(properties
				.getProperty("protocolId").trim()))[0];
		priorityFlag = CommonTool.intToByte(Integer.parseInt(properties
				.getProperty("priorityFlag").trim()))[0];
		isRegisteredDelivery = properties.getProperty("registeredDelivery")
				.trim();
		registeredDelivery = CommonTool.intToByte(Integer.parseInt(properties
				.getProperty("registeredDelivery").trim()))[0];
		String dataCodeString = properties.getProperty("dataCoding").trim();
		dataCoding = CommonTool.intToByte(Integer.parseInt(dataCodeString))[0];
		String normalMsgDataCodingStr = properties.getProperty(
				"normalMsgDataCoding").trim();
		normalMsgDataCoding = CommonTool.intToByte(Integer
				.parseInt(normalMsgDataCodingStr))[0];

		String normalChineseMsgDataCodingStr = properties.getProperty(
				"normalChineseMsgDataCoding").trim();
		normalChineseMsgDataCoding = CommonTool.intToByte(Integer
				.parseInt(normalChineseMsgDataCodingStr))[0];
		threadNum = Integer.parseInt(properties.getProperty("threadNum", "1")
				.trim());
		operatorCode = properties.getProperty("operatorCode");
	}

	private void setAddressParameter(String descr, Address address, byte ton,
			byte npi, String addr) {
		address.setTon(ton);
		address.setNpi(npi);
		try {
			address.setAddress(addr);
		} catch (WrongLengthOfStringException e) {
			logger.error("setAddressParameter failed", e);
		}
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the systemId
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * @param systemId
	 *            the systemId to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType
	 *            the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the addressRange
	 */
	public AddressRange getAddressRange() {
		return addressRange;
	}

	/**
	 * @param addressRange
	 *            the addressRange to set
	 */
	public void setAddressRange(AddressRange addressRange) {
		this.addressRange = addressRange;
	}

	/**
	 * @return the sourceAddress
	 */
	public Address getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * @param sourceAddress
	 *            the sourceAddress to set
	 */
	public void setSourceAddress(Address sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	/**
	 * @return the destAddress
	 */
	public Address getDestAddress() {
		return destAddress;
	}

	/**
	 * @param destAddress
	 *            the destAddress to set
	 */
	public void setDestAddress(Address destAddress) {
		this.destAddress = destAddress;
	}

	/**
	 * @return the slideWindowSize
	 */
	public int getSlideWindowSize() {
		return slideWindowSize;
	}

	/**
	 * @param slideWindowSize
	 *            the slideWindowSize to set
	 */
	public void setSlideWindowSize(int slideWindowSize) {
		this.slideWindowSize = slideWindowSize;
	}

	/**
	 * @return the receiveTimeout
	 */
	public long getReceiveTimeout() {
		return receiveTimeout;
	}

	/**
	 * @param receiveTimeout
	 *            the receiveTimeout to set
	 */
	public void setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

	/**
	 * @return the enquirelinkInterval
	 */
	public long getEnquirelinkInterval() {
		return enquirelinkInterval;
	}

	/**
	 * @param enquirelinkInterval
	 *            the enquirelinkInterval to set
	 */
	public void setEnquirelinkInterval(long enquirelinkInterval) {
		this.enquirelinkInterval = enquirelinkInterval;
	}

	/**
	 * @return the syncMode
	 */
	public boolean isSyncMode() {
		return syncMode;
	}

	/**
	 * @param syncMode
	 *            the syncMode to set
	 */
	public void setSyncMode(boolean syncMode) {
		this.syncMode = syncMode;
	}

	/**
	 * @return the esmClass
	 */
	public byte getEsmClass() {
		return esmClass;
	}

	/**
	 * @param esmClass
	 *            the esmClass to set
	 */
	public void setEsmClass(byte esmClass) {
		this.esmClass = esmClass;
	}

	/**
	 * @return the protocolId
	 */
	public byte getProtocolId() {
		return protocolId;
	}

	/**
	 * @param protocolId
	 *            the protocolId to set
	 */
	public void setProtocolId(byte protocolId) {
		this.protocolId = protocolId;
	}

	/**
	 * @return the scheduleDeliveryTime
	 */
	public String getScheduleDeliveryTime() {
		return scheduleDeliveryTime;
	}

	/**
	 * @param scheduleDeliveryTime
	 *            the scheduleDeliveryTime to set
	 */
	public void setScheduleDeliveryTime(String scheduleDeliveryTime) {
		this.scheduleDeliveryTime = scheduleDeliveryTime;
	}

	/**
	 * @return the priorityFlag
	 */
	public byte getPriorityFlag() {
		return priorityFlag;
	}

	/**
	 * @param priorityFlag
	 *            the priorityFlag to set
	 */
	public void setPriorityFlag(byte priorityFlag) {
		this.priorityFlag = priorityFlag;
	}

	/**
	 * @return the registeredDelivery
	 */
	public byte getRegisteredDelivery() {
		return registeredDelivery;
	}

	/**
	 * @param registeredDelivery
	 *            the registeredDelivery to set
	 */
	public void setRegisteredDelivery(byte registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}

	/**
	 * @return the replaceIfPresentFlag
	 */
	public byte getReplaceIfPresentFlag() {
		return replaceIfPresentFlag;
	}

	/**
	 * @param replaceIfPresentFlag
	 *            the replaceIfPresentFlag to set
	 */
	public void setReplaceIfPresentFlag(byte replaceIfPresentFlag) {
		this.replaceIfPresentFlag = replaceIfPresentFlag;
	}

	/**
	 * @return the dataCoding
	 */
	public byte getDataCoding() {
		return dataCoding;
	}

	/**
	 * @param dataCoding
	 *            the dataCoding to set
	 */
	public void setDataCoding(byte dataCoding) {
		this.dataCoding = dataCoding;
	}

	/**
	 * @return the validityPeriod
	 */
	public String getValidityPeriod() {
		return validityPeriod;
	}

	/**
	 * @param validityPeriod
	 *            the validityPeriod to set
	 */
	public void setValidityPeriod(String validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	/**
	 * @return the propsFilePath
	 */
	public String getPropsFilePath() {
		return propsFilePath;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @return the systemType
	 */
	public String getSystemType() {
		return systemType;
	}

	/**
	 * @param systemType
	 *            the systemType to set
	 */
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	/**
	 * @param propsFilePath
	 *            the propsFilePath to set
	 */
	public void setPropsFilePath(String propsFilePath) {
		this.propsFilePath = propsFilePath;
	}

	/**
	 * @return the normalMsgDataCoding
	 */
	public byte getNormalMsgDataCoding() {
		return normalMsgDataCoding;
	}

	/**
	 * @param normalMsgDataCoding
	 *            the normalMsgDataCoding to set
	 */
	public void setNormalMsgDataCoding(byte normalMsgDataCoding) {
		this.normalMsgDataCoding = normalMsgDataCoding;
	}

	/**
	 * @return the threadNum
	 */
	public int getThreadNum() {
		return threadNum;
	}

	/**
	 * @param threadNum
	 *            the threadNum to set
	 */
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	/**
	 * @return the bindMode
	 */
	public String getBindMode() {
		return bindMode;
	}

	/**
	 * @param bindMode
	 *            the bindMode to set
	 */
	public void setBindMode(String bindMode) {
		this.bindMode = bindMode;
	}

	/**
	 * @return the nationCode
	 */
	public String getNationCode() {
		return nationCode;
	}

	/**
	 * @param nationCode
	 *            the nationCode to set
	 */
	public void setNationCode(String nationCode) {
		this.nationCode = nationCode;
	}

	/**
	 * @return the repeatTime
	 */
	public int getRepeatTime() {
		return repeatTime;
	}

	/**
	 * @param repeatTime
	 *            the repeatTime to set
	 */
	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public static void main(String[] args) {
		String ipAddress = null;
		int port;
		String smsc = "10.0.61.2:8980";
		if (smsc == null || smsc.length() == 0) {
			throw new RuntimeException("smsc is error!");
		}
		int index = smsc.indexOf(":");
		if (index > 0) {
			ipAddress = smsc.substring(0, index);
			port = Integer.parseInt(smsc.substring(index + 1).trim());
		}
		/*
		 * SmppProperties properties = SmppProperties.getInstance();
		 * System.out.println("passwd = "+properties.getPassword());
		 */
	}

	public String getIsRegisteredDelivery() {
		return isRegisteredDelivery;
	}

	public void setIsRegisteredDelivery(String isRegisteredDelivery) {
		this.isRegisteredDelivery = isRegisteredDelivery;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public int getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}

	public byte getUnformatEsmClass() {
		return unformatEsmClass;
	}

	public void setUnformatEsmClass(byte unformatEsmClass) {
		this.unformatEsmClass = unformatEsmClass;
	}

	public byte getNormalChineseMsgDataCoding() {
		return normalChineseMsgDataCoding;
	}

	public void setNormalChineseMsgDataCoding(byte normalChineseMsgDataCoding) {
		this.normalChineseMsgDataCoding = normalChineseMsgDataCoding;
	}

}
