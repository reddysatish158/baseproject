package org.mifosplatform.scheduledjobs.importfile.data;

import java.util.Date;

import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;

public class MRNErrorData{
	Long rowNumber;
	String serialNumber;
	Long mrnId;
	Date movedDate;
	private String errorMessage;
	
	
	public MRNErrorData(final Long rowNumber, final String serialNumber,final Long mrnId, final Date movedDate) {
		this.rowNumber = rowNumber;
		this.serialNumber = serialNumber;
		this.mrnId = mrnId;
		this.movedDate = movedDate;
	}
	
	public MRNErrorData(final Long rowNumber, final Object object){
		this.rowNumber = rowNumber;
		if(object instanceof String)
			this.errorMessage = object.toString();
		else if(object instanceof Long)
			this.errorMessage = ""+object.toString();
		else{
			errorMessage = object.toString();
		}
			
	}
	
	public MRNErrorData(final Long rowNumber, final PlatformApiDataValidationException object){
		this.rowNumber = rowNumber;
		errorMessage = object.getErrors().get(0).getParameterName()+" is invalid";
	}
			
	
	
	
	public Long getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(Long rowNumber) {
		this.rowNumber = rowNumber;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public Long getMrnId() {
		return mrnId;
	}
	public void setMrnId(Long mrnId) {
		this.mrnId = mrnId;
	}
	public Date getMovedDate() {
		return movedDate;
	}
	public void setMovedDate(Date movedDate) {
		this.movedDate = movedDate;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}

