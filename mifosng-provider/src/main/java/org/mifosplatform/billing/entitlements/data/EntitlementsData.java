package org.mifosplatform.billing.entitlements.data;


public class EntitlementsData {

	private Long id;
	private Long prepareReqId;
	private String requestType;
	private String hardwareId;
	private String provisioingSystem;
	private String product;
	private Long serviceId;
	private Long clientId;
	private String status;
	private StakerData results;
	private String error;
	
	

	public EntitlementsData(Long id,Long prepareReqId, String requestType, String hardwareId, String provisioingSystem, String product, Long serviceId, Long clientId) {
		
          this.id=id;
          this.prepareReqId=prepareReqId;
          this.product=product;
          this.requestType=requestType;
          this.hardwareId=hardwareId;
          this.provisioingSystem=provisioingSystem;
          this.serviceId=serviceId;
          this.clientId=clientId;
	}
	
	public EntitlementsData(){
		
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

	public Long getClientId() {
		return clientId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public StakerData getResults() {
		return results;
	}

	public void setResults(StakerData results) {
		this.results = results;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	
}
