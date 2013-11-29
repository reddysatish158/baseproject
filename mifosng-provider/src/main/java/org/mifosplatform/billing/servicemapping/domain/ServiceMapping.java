package org.mifosplatform.billing.servicemapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_prov_service_details")
public class ServiceMapping extends AbstractPersistable<Long>{
	

	@Column(name = "service_id")
	private Long serviceId;

	@Column(name = "service_identification", nullable = false, length = 100)
	private String serviceIdentification;

	@Column(name = "status", nullable = false, length = 100)
	private String status;
	
	@Column(name = "image", nullable = false, length = 100)
	private String image;

	@Column(name = "is_deleted")
	private String isDeleted="n";
	
	


public ServiceMapping()
{}


public ServiceMapping(Long serviceId, String serviceUrl, String status,
		String image) {
	this.serviceId=serviceId;
	this.serviceIdentification=serviceUrl;
	this.status=status;
	this.image=image;
}


public static ServiceMapping fromJson(JsonCommand command){
	final Long serviceId = command.longValueOfParameterNamed("serviceId");
	final String serviceIdentification = command.stringValueOfParameterNamed("serviceIdentification");
	final String status = command.stringValueOfParameterNamed("status");
	final String image = command.stringValueOfParameterNamed("image");

	
	
	return new ServiceMapping(serviceId,serviceIdentification,status,image);
}


public Long getServiceId() {
	return serviceId;
}


public String getServiceIdentification() {
	return serviceIdentification;
}


public String getStatus() {
	return status;
}


public String getImage() {
	return image;
}


public String getIsDeleted() {
	return isDeleted;
}


public Map<String, Object> update(JsonCommand command) {
	

	 final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
	 final String serviceParamName = "serviceId";
		if (command.isChangeInLongParameterNamed(serviceParamName,this.serviceId)) {
			final Long newValue = command
					.longValueOfParameterNamed(serviceParamName);
			actualChanges.put(serviceParamName, newValue);
			this.serviceId=newValue;
		}
		
	  final String serviceIdentificationParamName = "serviceIdentification";
       if (command.isChangeInStringParameterNamed(serviceIdentificationParamName, this.serviceIdentification)) {
           final String newValue = command.stringValueOfParameterNamed(serviceIdentificationParamName);
           actualChanges.put(serviceIdentificationParamName, newValue);
           this.serviceIdentification = StringUtils.defaultIfEmpty(newValue, null);
       }
       
       final String statusParamName = "status";
       if (command.isChangeInStringParameterNamed(statusParamName, this.status)) {
           final String newValue = command.stringValueOfParameterNamed(statusParamName);
           actualChanges.put(statusParamName, newValue);
           this.status = StringUtils.defaultIfEmpty(newValue, null);
       }
       
       final String imageParamName = "image";
       if (command.isChangeInStringParameterNamed(imageParamName, this.image)) {
           final String newValue = command.stringValueOfParameterNamed(imageParamName);
           actualChanges.put(imageParamName, newValue);
           this.image = StringUtils.defaultIfEmpty(newValue, null);
       }
					
       return actualChanges;


	
}



}
