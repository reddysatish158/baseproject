package org.mifosplatform.provisioning.provisioning.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Entity
@Table(name="b_service_parameters")
public class ServiceParameters extends AbstractAuditableCustom<AppUser,Long>{
	
	
	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "plan_name")
	private String planName;

	@Column(name = "parameter_name")
	private String parameterName;

	@Column(name = "parameter_value")
	private String parameterValue;
	
	public ServiceParameters(){
		
	}


	public ServiceParameters(Long clientId, Long orderId, String planName,String paramName,String paramValue) {
	
		       this.clientId=clientId;
		       this.orderId=orderId;
		       this.planName=planName;
		       this.parameterName=paramName;
		       this.parameterValue=paramValue;
	}


	public static ServiceParameters fromJson(JsonElement j,FromJsonHelper fromJsonHelper, Long clientId, Long orderId, String planName) {

		 final String group = fromJsonHelper.extractStringNamed("paramName",j);
		 final String service = fromJsonHelper.extractStringNamed("paramValue",j);
		 return new ServiceParameters(clientId,orderId,planName,group,service);
	
	}


	public Long getClientId() {
		return clientId;
	}


	public Long getOrderId() {
		return orderId;
	}


	public String getPlanName() {
		return planName;
	}


	public String getParameterName() {
		return parameterName;
	}


	public String getParameterValue() {
		return parameterValue;
	}


	public Map<String, Object> updateServiceParam(JsonArray serviceParameters,FromJsonHelper fromApiJsonHelper) {
		
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		for(JsonElement element:serviceParameters){
			
			if(this.parameterName.equalsIgnoreCase(fromApiJsonHelper.extractStringNamed("paramName",element))){
				
				String newValue=fromApiJsonHelper.extractStringNamed("paramValue", element);;
				this.parameterValue=newValue;
				actualChanges.put(parameterName, newValue);
				return actualChanges;
				
			}
			
		}
		
		return actualChanges;
		
		 /* final String firstnameParamName = "planCode";
	        if (command.isChangeInStringParameterNamed(firstnameParamName, this.planCode)) {
	            final String newValue = command.stringValueOfParameterNamed(firstnameParamName);
	            actualChanges.put(firstnameParamName, newValue);
	            this.planCode = StringUtils.defaultIfEmpty(newValue, null);
	        }*/
	        
	      
	        
		
	}
	
	
}
