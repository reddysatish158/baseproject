package org.mifosplatform.logistics.mrn.data;

import java.text.ParseException;
import java.util.Date;

import org.mifosplatform.infrastructure.core.api.JsonCommand;

public class MRNMoveDetailsData{

	private String 	serialNumber;
	private Long id;
	private Long mrnId;
	private Date movedDate;
	private Long fromOffice;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Long toOffice;
	
	
	public MRNMoveDetailsData(final String serialNumber, final Long id, final Date movedDate) {
		this.serialNumber = serialNumber;
		this.id = id;
		this.movedDate = movedDate;
	}
	
	public MRNMoveDetailsData(final String serialNumber, final Long mrnId, final Date movedDate, final Long fromOffice, final Long toOffice) {
		this.serialNumber = serialNumber;
		this.mrnId = mrnId;
		this.movedDate = movedDate;
		this.fromOffice = fromOffice;
		this.toOffice = toOffice;
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

	public Long getFromOffice() {
		return fromOffice;
	}

	public void setFromOffice(Long fromOffice) {
		this.fromOffice = fromOffice;
	}

	public Long getToOffice() {
		return toOffice;
	}

	public void setToOffice(Long toOffice) {
		this.toOffice = toOffice;
	}

	public static MRNMoveDetailsData fromJson(JsonCommand command,Long id) throws ParseException {
		final Long idValue=id;
		final String serialNumber = command.stringValueOfParameterNamed("serialNumber");
		
		
		return new MRNMoveDetailsData(serialNumber, idValue, new Date());
	}
	
	
	
	
}
