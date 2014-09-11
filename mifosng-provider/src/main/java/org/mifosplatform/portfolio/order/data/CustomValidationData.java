package org.mifosplatform.portfolio.order.data;

public class CustomValidationData {

	private final Long errorCode;
	private final String errorMessage;
	
	public CustomValidationData(Long errCode, String errMsg) {
        
		this.errorCode=errCode;
		this.errorMessage=errMsg;
	
	}

	public Long getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	
}
