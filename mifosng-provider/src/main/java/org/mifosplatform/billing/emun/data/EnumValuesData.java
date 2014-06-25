package org.mifosplatform.billing.emun.data;

public class EnumValuesData {

	private final Long id;
	private final String value;
	private final String type;
	
	
	public EnumValuesData(Long id, String value, String type) {
        
		this.id=id;
		this.value=value;
		this.type=type;
	}


	public Long getId() {
		return id;
	}


	public String getValue() {
		return value;
	}


	public String getType() {
		return type;
	}

	
}
