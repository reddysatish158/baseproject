package org.mifosplatform.portfolio.servicemapping.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.dataqueries.data.ReportParameterData;

public class ServiceMappingData {

	
	private Long id;
	private Long serviceId;
	private String serviceCode;
	private String serviceIdentification;
	private String status;
	private String image;
	private String category;
	private String subCategory;
	private List<ServiceMappingData> serviceMappingData;
	private List<ServiceCodeData> serviceCodeData;
	private List<EnumOptionData> statusData;
	private Collection<ReportParameterData> serviceParameters;
	private Collection<McodeData> categories;
	
	public Collection<McodeData> getCategories() {
		return categories;
	}

	public void setCategories(Collection<McodeData> categories) {
		this.categories = categories;
	}

	public Collection<McodeData> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(Collection<McodeData> subCategories) {
		this.subCategories = subCategories;
	}



	private Collection<McodeData> subCategories;
	

	public ServiceMappingData(Long id, String serviceCode, String serviceIndentification,String status,
				String image,String category,String subCategory) {
		
		this.id=id;
		this.serviceCode=serviceCode;
		this.serviceIdentification=serviceIndentification;
		this.status=status;
		this.image=image;
		this.category=category;
		this.subCategory=subCategory;
	}
	
	public ServiceMappingData( List<ServiceMappingData> serviceMappingData,	List<ServiceCodeData> serviceCodeData, List<EnumOptionData> status, 
			Collection<ReportParameterData> serviceParameters, Collection<McodeData> categories, Collection<McodeData> subCategories) {

		this.serviceMappingData=serviceMappingData;
		this.serviceCodeData=serviceCodeData;
		this.statusData=status;
		this.serviceParameters=serviceParameters;
		this.categories=categories;
		this.subCategories=subCategories;
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
