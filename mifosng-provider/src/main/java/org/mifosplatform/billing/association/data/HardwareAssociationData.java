package org.mifosplatform.billing.association.data;

public class HardwareAssociationData {
	
	private final Long id;
	private final String serialNo;
	private final Long planId;
	private final Long orderId;
	private String itemCode;

	public HardwareAssociationData(Long id, String serialNo, Long planId,Long orderId, String itemCode) {
                
		this.id=id;
		this.planId=planId;
		this.serialNo=serialNo;
		this.orderId=orderId;
		this.itemCode=itemCode;
	
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

	
}
