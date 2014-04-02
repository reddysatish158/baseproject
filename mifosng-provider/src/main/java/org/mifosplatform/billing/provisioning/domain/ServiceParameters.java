package org.mifosplatform.billing.provisioning.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;


@Entity
@Table(name="b_service_parameters")
public class ServiceParameters extends AbstractAuditableCustom<AppUser,Long >{
	
	
	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "plan_name")
	private String planName;

	@Column(name = "group")
	private String groupName;

	@Column(name = "service")
	private String service;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "mac_id")
	private String macId;

	@Column(name = "vlan_id")
	private String vlanId;
	
	
	public ServiceParameters(){
		
	}


	public ServiceParameters(Long clientId, Long orderId, String planName,String group, String service, 
			String ipAddress, String macId,String vlanId) {
	
		       this.clientId=clientId;
		       this.orderId=orderId;
		       this.planName=planName;
		       this.groupName=group;
		       this.service=service;
		       this.ipAddress=ipAddress;
		       this.macId=macId;
		       this.vlanId=vlanId;

	}


	public static ServiceParameters fromJson(JsonCommand command) {

		

		 final Long clientId = command.longValueOfParameterNamed("clientId");
		 final Long orderId= command.longValueOfParameterNamed("orderId");
		 final String planName = command.stringValueOfParameterNamed("planName");
		 final String group = command.stringValueOfParameterNamed("group");
		 final String service = command.stringValueOfParameterNamed("service");
		 final String ipAddress = command.stringValueOfParameterNamed("ipAddress");
		 final String macId = command.stringValueOfParameterNamed("macId");
		 final String vlanId = command.stringValueOfParameterNamed("vlanId");
		 return new ServiceParameters(clientId,orderId,planName,group,service,ipAddress,macId,vlanId);
	
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


	public String getGroupName() {
		return groupName;
	}


	public String getService() {
		return service;
	}


	public String getIpAddress() {
		return ipAddress;
	}


	public String getMacId() {
		return macId;
	}


	public String getVlanId() {
		return vlanId;
	}
	

	
	
}
