package org.mifosplatform.billing.servicemapping.data;

public class ServiceCodeData {
	
	
	private String serviceCode;
	private Long id;
	
	
	public ServiceCodeData(final String serviceCode, final Long id){
		this.serviceCode = serviceCode;
		this.id = id;
	}
	
	
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}



	
	
	
	

}
