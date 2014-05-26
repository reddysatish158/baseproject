package org.mifosplatform.portfolio.servicemapping.data;

public class ServiceCodeData {
	
	
	
	private Long id;
	private String serviceCode;
	private String serviceType;
	
	public ServiceCodeData(final String serviceCode, final Long id){
	
	}
	
	
	public ServiceCodeData(Long id, String serviceCode, String serviceType) {
		this.id = id;
		this.serviceCode = serviceCode;
		this.serviceType = serviceType;
	
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
