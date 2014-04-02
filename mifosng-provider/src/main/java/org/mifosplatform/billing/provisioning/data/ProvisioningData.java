package org.mifosplatform.billing.provisioning.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.mcodevalues.data.MCodeData;
import org.mifosplatform.billing.order.data.OrderLineData;
import org.mifosplatform.billing.paymode.data.McodeData;

public class ProvisioningData {

	private String provisioningSystem;
	private String commandName;
	private Long id;
	private String status;
	private List<McodeData> commands;
	private List<McodeData> provisioning;
	private List<ProvisioningCommandParameterData> commandParameters;
	private Collection<MCodeData> vlanDatas;
	
	private List<OrderLineData> services;

	public ProvisioningData(final Long id,final String ProvisioningSystem,
			final String CommandName,final String Status ){
		this.id=id;
		this.provisioningSystem=ProvisioningSystem;
		this.commandName=CommandName;
		this.status=Status;
	}
	

	public ProvisioningData(List<McodeData> provisioning,List<McodeData> commands) {
		
		this.commands=commands;
		this.provisioning=provisioning;		
	}


	public ProvisioningData(Collection<MCodeData> vlanDatas,
			List<OrderLineData> services) {
		
		this.vlanDatas=vlanDatas;
		this.services=services;
	}


	public String getProvisioningSystem() {
		return provisioningSystem;
	}

	public String getCommandName() {
		return commandName;
	}


	public Long getId() {
		return id;
	}


	public String getStatus() {
		return status;
	}


	public List<McodeData> getCommands() {
		return commands;
	}


	public List<McodeData> getProvisioning() {
		return provisioning;
	}


	public void setCommands(List<McodeData> commands) {
		this.commands = commands;
	}


	public void setProvisioning(List<McodeData> provisioning) {
		this.provisioning = provisioning;
	}
	
	public List<ProvisioningCommandParameterData> getCommandParameters() {
		return commandParameters;
	}


	public void setCommandParameters(
			List<ProvisioningCommandParameterData> commandParameters) {
		this.commandParameters = commandParameters;
	}

	
	
	
}
