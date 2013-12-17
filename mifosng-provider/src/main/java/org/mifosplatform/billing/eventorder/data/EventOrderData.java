package org.mifosplatform.billing.eventorder.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.eventmaster.data.EventMasterData;
import org.mifosplatform.billing.mcodevalues.data.MCodeData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class EventOrderData {
	
	private Long id;
	private String eventName;
	private LocalDate bookedDate;
	private BigDecimal eventPrice;
	private String chargeCode;
	private String status;
	private List<EventOrderDeviceData> devices;
	private List<EventMasterData> events;
	private List<EnumOptionData> optType;
	private Collection<MCodeData> codes;

	public EventOrderData(Long orderid, LocalDate bookedDate, String eventName,
			BigDecimal bookedPrice, String chargeCode, String status) {
		
		this.id=orderid;
		this.eventName=eventName;
		this.bookedDate=bookedDate;
		this.eventPrice=bookedPrice;
		this.chargeCode=chargeCode;
		this.status=status;
	}

	public EventOrderData(List<EventOrderDeviceData> devices, List<EventMasterData> events, List<EnumOptionData> optType, Collection<MCodeData> codes) {
		this.devices = devices;
		this.events = events;
		this.optType = optType;
		this.codes = codes;
	}

	public Long getId() {
		return id;
	}

	public String getEventName() {
		return eventName;
	}

	public LocalDate getBookedDate() {
		return bookedDate;
	}

	public BigDecimal getEventPrice() {
		return eventPrice;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getStatus() {
		return status;
	}

}
