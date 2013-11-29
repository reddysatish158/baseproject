package org.mifosplatform.billing.servicemapping.data;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class ServiceMappingData {

	
	private Long id;
	private Long serviceId;
	private String serviceCode;
	private String serviceIdentification;
	private String status;
	private String image;
	private List<ServiceMappingData> serviceMappingData;
	private List<ServiceCodeData> serviceCodeData;
	private List<EnumOptionData> statusData;

	public ServiceMappingData(Long id, String serviceCode, String serviceIndentification,String status,String image) {
		
		this.id=id;
		this.serviceCode=serviceCode;
		this.serviceIdentification=serviceIndentification;
		this.status=status;
		this.image=image;
	}
	
	public ServiceMappingData( List<ServiceMappingData> serviceMappingData,	List<ServiceCodeData> serviceCodeData, List<EnumOptionData> status) {

		this.serviceMappingData=serviceMappingData;
		this.serviceCodeData=serviceCodeData;
		this.statusData=status;
	}
	
	public List<ServiceCodeData> getServiceCodeData() {
		return serviceCodeData;
	}

	public void setServiceCodeData(List<ServiceCodeData> serviceCodeData) {
		this.serviceCodeData = serviceCodeData;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public List<ServiceMappingData> getServiceMappingData() {
		return serviceMappingData;
	}


	public void setServiceMappingData(List<ServiceMappingData> serviceMappingData) {
		this.serviceMappingData = serviceMappingData;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceIndentification() {
		return serviceIdentification;
	}

	public void setServiceIndentification(String serviceIndentification) {
		this.serviceIdentification = serviceIndentification;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}



	public void setStatusData(List<EnumOptionData> status) {
		this.statusData=status;
		
	}
	
	
	
	

	
	
}
