package org.mifosplatform.billing.emun.data;

public class EnumValuesData {

	private final Long id;
	private final String value;
	
	
	public EnumValuesData(Long id, String value) {
        
		this.id=id;
		this.value=value;
	}


	public Long getId() {
		return id;
	}


	public String getValue() {
		return value;
	}

	
}
