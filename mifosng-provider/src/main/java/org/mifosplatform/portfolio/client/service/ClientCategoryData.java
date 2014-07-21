package org.mifosplatform.portfolio.client.service;

public class ClientCategoryData {
	
	private final Long id;
	private final String categoryType;
	private final String billMode;

	public ClientCategoryData(Long id, String categoryType,String billMode) {
           this.id=id;
           this.categoryType=categoryType;
           this.billMode = billMode;
	
	}

	public Long getId() {
		return id;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public String getBillMode() {
		return billMode;
	}

	
}
