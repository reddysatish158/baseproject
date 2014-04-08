package org.mifosplatform.finance.billingorder.data;

import java.math.BigDecimal;
import java.util.Date;

public class GenerateInvoiceData {
	
	private final Long clientId;
	private final Date nextBillableDay;
	private BigDecimal invoiceAmount;
	
	public GenerateInvoiceData( final Long clientId, final Date nextBillableDay,BigDecimal invoiceAmount ) {
		this.clientId = clientId;
		this.nextBillableDay = nextBillableDay;
		this.invoiceAmount=invoiceAmount;
	}

	public Long getClientId() {
		return clientId;
	}

	public Date getNextBillableDay() {
		return nextBillableDay;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

}
