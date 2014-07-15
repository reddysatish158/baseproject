package org.mifosplatform.provisioning.provisioning.data;

public class ServiceParameterData {
	
	private final Long id;
	private final String paramName;
	private final String paramValue;
	private final String type;
	
	public ServiceParameterData(Long id, String paramName, String paramValue, String type) {
		
		this.id=id;
		this.paramName=paramName;
		this.paramValue=paramValue;
		this.type=type;
	}

	public Long getId() {
		return id;
	}

	public String getParamName() {
		return paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	
	
}
