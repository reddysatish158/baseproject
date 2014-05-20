package org.mifosplatform.portfolio.association.data;

public class HardwareAssociationData {
	
	private  Long id;
	private  String serialNo;
	private  Long planId;
	private  Long orderId;
	private String itemCode;
	private String provSerialNum;
	

	public HardwareAssociationData(Long id, String serialNo, Long planId,Long orderId, String itemCode) {
                
		this.id=id;
		this.planId=planId;
		this.serialNo=serialNo;
		this.orderId=orderId;
		this.itemCode=itemCode;
	
	}

	public HardwareAssociationData(Long id, String serialNo,String provSerialNum) {
		
		this.id=id;
		this.serialNo=serialNo;
		this.provSerialNum=provSerialNum;

	
	}

	public Long getId() {
		return id;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public Long getPlanId() {
		return planId;
	}

	public Long getorderId() {
		return orderId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getProvSerialNum() {
		return provSerialNum;
	}

	
}
