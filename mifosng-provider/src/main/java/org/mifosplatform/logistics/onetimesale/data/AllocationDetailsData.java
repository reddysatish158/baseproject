package org.mifosplatform.logistics.onetimesale.data;

import org.joda.time.LocalDate;

public class AllocationDetailsData {
	
	private final Long id;
	private final String itemDescription;
	private final String serialNo;
	private final LocalDate allocationDate;
	private final Long itemDetailId;
	public AllocationDetailsData(Long id, String itemDescription,String serialNo, LocalDate allocationDate,Long itemDetailId) {
		this.id=id;
		this.itemDescription=itemDescription;
		this.serialNo=serialNo;
		this.allocationDate=allocationDate;
		this.itemDetailId=itemDetailId;
	}
	public AllocationDetailsData(Long id, Long orderId, String serialNum,
			Long clientId) {
		
		this.id=id;
		this.serialNo=serialNum;
		this.itemDescription=null;
		this.allocationDate=null;
		this.itemDetailId=null;
		
	}
	public Long getId() {
		return id;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public LocalDate getAllocationDate() {
		return allocationDate;
	}
	public Long getItemDetailId() {
		return itemDetailId;
	}

	
}
