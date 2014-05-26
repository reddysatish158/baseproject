package org.mifosplatform.provisioning.provisioning.data;

public class ProcessRequestData {
	
	private final Long id;
	private final Long clientId;
	private final Long orderId;
	private final String requestType;
	private final String hardwareId;
	private final String receiveMessage;
	private final String sentMessage;
	private final String isProcessed;
	private final String orderNo;
	

	public ProcessRequestData(Long id, Long clientId, Long orderId,String requestType, 
			String hardwareId, String receiveMessage, String sentMessage, String isProcessed, String orderNo) {
		this.id=id;
		this.clientId=clientId;
		this.orderId=orderId;
		this.requestType=requestType;
		this.hardwareId=hardwareId;
		this.sentMessage=sentMessage;
		this.receiveMessage=receiveMessage;
		this.isProcessed=isProcessed;
		this.orderNo=orderNo;
	}


	public Long getId() {
		return id;
	}


	public Long getClientId() {
		return clientId;
	}


	public Long getOrderId() {
		return orderId;
	}


	public String getRequestType() {
		return requestType;
	}


	public String getHardwareId() {
		return hardwareId;
	}


	public String getReceiveMessage() {
		return receiveMessage;
	}

	public String getSentMessage() {
		return sentMessage;
	}


	public String getIsProcessed() {
		return isProcessed;
	}


	public String getOrderNo() {
		return orderNo;
	}
	
	

}
