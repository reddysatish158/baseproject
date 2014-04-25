package org.mifosplatform.provisioning.provisioning.data;

public class ProvisioningCommandParameterData {

	private Long id;
	private String commandParam;
	private String paramType;
	
	public ProvisioningCommandParameterData(Long id, String commandParam,String paramType){
		this.id=id;
		this.commandParam=commandParam;
		this.paramType=paramType;
		
	}

	public String getCommandParam() {
		return commandParam;
	}

	public String getParamType() {
		return paramType;
	}

	public Long getId() {
		return id;
	}
	
	
	
	
}
