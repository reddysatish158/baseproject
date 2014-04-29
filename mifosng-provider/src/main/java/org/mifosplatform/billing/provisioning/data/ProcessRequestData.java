package org.mifosplatform.billing.provisioning.data;

public class ProcessRequestData {
	
	private Long id;
	private Long clientId;
	private Long orderId;
	private String requestType;
	private String hardwareId;
	private String receiveMessage;
	private String sentMessage;
	private String isProcessed;
	

	public ProcessRequestData(Long id, Long clientId, Long orderId,String requestType, 
			String hardwareId, String receiveMessage, String sentMessage, String isProcessed) {
		this.id=id;
		this.clientId=clientId;
		this.orderId=orderId;
		this.requestType=requestType;
		this.hardwareId=hardwareId;
		this.sentMessage=sentMessage;
		this.receiveMessage=receiveMessage;
		this.isProcessed=isProcessed;
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
	
	

}
