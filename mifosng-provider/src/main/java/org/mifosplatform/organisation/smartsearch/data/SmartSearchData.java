package org.mifosplatform.organisation.smartsearch.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class SmartSearchData {
	
  private final Long id;
  private final Long clientId;
  private final String clientName;
  private final String  paymenttype;
  private final String receiptNo;
  private final LocalDate paymentDate;
  private final BigDecimal amount;
	

public SmartSearchData(Long id, Long clientId, String clientName,LocalDate paymentDate, String paymodeType, 
			String receiptNo,BigDecimal amount) {
	
	   this.id=id;
	   this.clientId=clientId;
	   this.clientName=clientName;
	   this.paymentDate=paymentDate;
	   this.paymenttype=paymodeType;
	   this.receiptNo=receiptNo;
	   this.amount=amount;
	   

	}

public Long getId() {
	return id;
}


public Long getClientId() {
	return clientId;
}


public String getClientName() {
	return clientName;
}


public String getPaymenttype() {
	return paymenttype;
}


public String getReceiptNo() {
	return receiptNo;
}


public LocalDate getPaymentDate() {
	return paymentDate;
}


public BigDecimal getAmount() {
	return amount;
}


}
