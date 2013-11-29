package org.mifosplatform.billing.entitlements.data;

public class EntitlementsData {

	private final Long id;

	private final Long prepareReqId;
	private String requestType;
	private String hardwareId;
	private String provisioingSystem;
	private String product;
	private Long serviceId;

	public EntitlementsData(Long id,Long prepareReqId, String requestType, String hardwareId, String provisioingSystem, String product, Long serviceId) {
		
          this.id=id;
          this.prepareReqId=prepareReqId;
          this.product=product;
          this.requestType=requestType;
          this.hardwareId=hardwareId;
          this.provisioingSystem=provisioingSystem;
          this.serviceId=serviceId;
	}

	public Long getId() {
		return id;
	}


	public Long getPrepareReqId() {
		return prepareReqId;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public String getProvisioingSystem() {
		return provisioingSystem;
	}

	public String getProduct() {
		return product;
	}

	public Long getServiceId() {
		return serviceId;
	}
	
	

	
}
