package org.mifosplatform.billing.eventorder.data;

public class EventOrderDeviceData {

	
	private Long allocationId;
	private Long clientId;
	private Long itemMasterId;
	private String serialNumber;
	private String itemCode;
	private String itemDescription;
	
	
	public EventOrderDeviceData(final Long allocationId, final Long clientId, final Long itemMasterId, final String serialNumber, final String itemCode, final String itemDescription) {
		this.allocationId = allocationId;
		this.clientId = clientId;
		this.itemMasterId = itemMasterId;
		this.serialNumber = serialNumber;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		
	}
	
	
	
	public Long getAllocationId() {
		return allocationId;
	}
	public void setAllocationId(Long allocationId) {
		this.allocationId = allocationId;
	}
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public Long getItemMasterId() {
		return itemMasterId;
	}
	public void setItemMasterId(Long itemMasterId) {
		this.itemMasterId = itemMasterId;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	
	
	
	
	
}
