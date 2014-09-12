package org.mifosplatform.logistics.ownedhardware.data;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;


@Entity
@Table(name="b_owned_hardware")
public class OwnedHardware extends AbstractAuditableCustom<AppUser, Long>{

	
	@Column(name="client_id")
	private Long clientId;
	
	@Column(name="serial_number")
	private String serialNumber;
	
	@Column(name="provisioning_serial_number")
	private String provisioningSerialNumber;
	
	@Column(name="allocated_date")
	private Date allocatedDate;
	
	@Column(name="status")
	private String status;
	
	@Column(name="item_type")
	private String itemType;
	
	@Column(name="is_deleted")
	private Character isDeleted='N';

	
	public OwnedHardware() {
		
	}

	private OwnedHardware(Long clientId, String serialNumber, String provisioningSerialNumber, Date allocatedDate, String status, String itemType){
		
		this.clientId = clientId;
		this.serialNumber = serialNumber;
		this.provisioningSerialNumber = provisioningSerialNumber;
		this.allocatedDate = allocatedDate;
		this.status = status;
		this.itemType = itemType;
	
	}
	

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getProvisioningSerialNumber() {
		return provisioningSerialNumber;
	}

	public void setProvisioningSerialNumber(String provisioningSerialNumber) {
		this.provisioningSerialNumber = provisioningSerialNumber;
	}

	public Date getAllocatedDate() {
		return allocatedDate;
	}

	public void setAllocatedDate(Date allocatedDate) {
		this.allocatedDate = allocatedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	
	public static OwnedHardware fromJson(JsonCommand command,Long clientId){
		String serialNumber = command.stringValueOfParameterNamed("serialNumber");
		String provisioningSerialNumber = command.stringValueOfParameterNamed("provisioningSerialNumber");
		Date allocatedDate = command.DateValueOfParameterNamed("allocationDate");
		
		String itemType = command.stringValueOfParameterNamed("itemType");
		return new OwnedHardware(clientId, serialNumber, provisioningSerialNumber, allocatedDate, "ACTIVE", itemType); 
	}
	
	public Map<String, Object> update(JsonCommand command) {
		 final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		  final String serialNumberParam = "serialNumber";
	        if (command.isChangeInStringParameterNamed(serialNumberParam, this.serialNumber)) {
	            final String newValue = command.stringValueOfParameterNamed(serialNumberParam);
	            actualChanges.put(serialNumberParam, newValue);
	            this.serialNumber = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String provisioningSerialNumberParamName = "provisioningSerialNumber";
	        if (command.isChangeInStringParameterNamed(provisioningSerialNumberParamName, this.provisioningSerialNumber)) {
	            final String newValue = command.stringValueOfParameterNamed(provisioningSerialNumberParamName);
	            actualChanges.put(provisioningSerialNumberParamName, newValue);
	            this.provisioningSerialNumber = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        
	        final String itemTypeParam = "itemType";
	        if (command.isChangeInStringParameterNamed(itemTypeParam, this.itemType)) {
	            final String newValue = command.stringValueOfParameterNamed(itemTypeParam);
	            actualChanges.put(itemTypeParam, newValue);
	            this.itemType = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String allocationDateParam = "allocationDate";
	        if (command.isChangeInDateParameterNamed(allocationDateParam, this.allocatedDate)) {
	            final Date newValue = command.DateValueOfParameterNamed(allocationDateParam);
	            actualChanges.put(allocationDateParam, newValue);
	            this.allocatedDate = newValue;
	        }
	        
	        final String statusParam = "status";
	        if (command.isChangeInStringParameterNamed(statusParam, this.status)) {
	            final String newValue = command.stringValueOfParameterNamed(statusParam);
	            actualChanges.put(statusParam, newValue);
	            this.status = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        			
	        return actualChanges;

	}
	public void delete() {
		
		if(this.isDeleted == 'N'){
			this.isDeleted='Y';
			this.serialNumber="DEL_"+this.serialNumber;
		}
		
	}

	public void updateSerialNumbers(String provisionNum) {

		this.serialNumber=provisionNum;
		this.provisioningSerialNumber=provisionNum;
		
	}
}
