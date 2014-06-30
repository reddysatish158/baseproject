package org.mifosplatform.portfolio.association.domain;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_association")
public class HardwareAssociation extends AbstractAuditableCustom<AppUser, Long> {

	

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "plan_id")
	private Long planId;
	


	@Column(name = "hw_serial_no")
	private String serialNo;
	
	@Column(name = "order_id")
	private Long orderId;
	
	@Column(name = "is_deleted")
	private String isDeleted="N";

	
	 public HardwareAssociation() {
		// TODO Auto-generated constructor stub
			
	}


	public HardwareAssociation(Long clientId, Long planId, String serialNo,Long orderId) {
            this.clientId=clientId;
            this.planId=planId;
            this.serialNo=serialNo;
            this.orderId=orderId;
	
	}
	
	public Map<String, Object> updateAssociationDetails(JsonCommand command) {
		// TODO Auto-generated method stub
		final LinkedHashMap<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String serialNo="provisionNum";
		if(command.isChangeInStringParameterNamed(serialNo, this.serialNo)){
			final String newValue=command.stringValueOfParameterNamed("provisionNum");
			actualChanges.put(serialNo, newValue);
			this.serialNo=newValue;
		}
		
		final String planId="planId";
		if(command.isChangeInLongParameterNamed(planId, this.planId)){
			final Long newValue=command.longValueOfParameterNamed("planId");
			actualChanges.put(planId, newValue);
			this.planId=newValue;
		}
		
		final String orderId="orderId";
		if(command.isChangeInLongParameterNamed(orderId, this.orderId)){
			final Long newValue=command.longValueOfParameterNamed("orderId");
			actualChanges.put(orderId, newValue);
			this.orderId=newValue;
		}
		
		return actualChanges;
	}


	public void delete() {
		this.isDeleted="Y";
		
	}


	public Long getClientId() {
		return clientId;
	}


	public Long getPlanId() {
		return planId;
	}


	public String getSerialNo() {
		return serialNo;
	}


	public Long getOrderId() {
		return orderId;
	}


	public String getIsDeleted() {
		return isDeleted;
	}


	public void updateserailNum(String serialNumber) {
		this.serialNo=serialNumber;
		
	}
}
	