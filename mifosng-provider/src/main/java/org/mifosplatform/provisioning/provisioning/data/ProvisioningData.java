package org.mifosplatform.provisioning.provisioning.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.order.data.OrderLineData;

public class ProvisioningData {

	private String provisioningSystem;
	private String commandName;
	private Long id;
	private String status;
	private List<McodeData> commands;
	private List<McodeData> provisioning;
	private List<ProvisioningCommandParameterData> commandParameters;
	

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
