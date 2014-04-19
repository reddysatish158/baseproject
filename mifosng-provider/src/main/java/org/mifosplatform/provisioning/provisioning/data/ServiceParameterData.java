package org.mifosplatform.provisioning.provisioning.data;

public class ServiceParameterData {
	
	private final Long id;
	private final String paramName;
	private final String paramValue;
	
	public ServiceParameterData(Long id, String paramName, String paramValue) {
		
		this.id=id;
		this.paramName=paramName;
		this.paramValue=paramValue;
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
