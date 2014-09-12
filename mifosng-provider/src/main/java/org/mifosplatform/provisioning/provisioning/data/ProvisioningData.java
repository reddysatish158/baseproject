package org.mifosplatform.provisioning.provisioning.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.organisation.ippool.data.IpPoolData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.client.service.GroupData;
import org.mifosplatform.portfolio.order.data.OrderLineData;

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
	private List<IpPoolData> ipPoolDatas;
	private List<ServiceParameterData> parameterDatas,serviceDatas;
	private Collection<GroupData> groupDatas;
	

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


	public ProvisioningData(Collection<MCodeData> vlanDatas,List<IpPoolData> ipPoolDatas, 
			List<OrderLineData> services,final List<ServiceParameterData> serviceDatas, 
			List<ServiceParameterData> parameterDatas, Collection<GroupData> groupDatas) {
		
		this.vlanDatas=vlanDatas;
		this.services=services;
		this.ipPoolDatas=ipPoolDatas;
		this.parameterDatas=parameterDatas;
		this.serviceDatas=serviceDatas;
		this.groupDatas=groupDatas;
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
