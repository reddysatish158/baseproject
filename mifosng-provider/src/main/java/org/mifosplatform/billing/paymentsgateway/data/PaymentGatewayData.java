package org.mifosplatform.billing.paymentsgateway.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class PaymentGatewayData {

	private Long id;
	private Long paymentId;
	private String serialNo;
	private LocalDate paymentDate;
	private String receiptNo;
	private BigDecimal amountPaid;
	private String status;
	private String phoneNo;
	private String clientName;

	public PaymentGatewayData(Long id, String serialNo, String phoneNo,
			LocalDate paymentDate, BigDecimal amountPaid, String receiptNo,
			String clientName, String status, Long paymentId) {
		this.id=id;
		this.paymentId=paymentId;
		this.serialNo=serialNo;
		this.paymentDate=paymentDate;
		this.receiptNo=receiptNo;
		this.amountPaid=amountPaid;
		this.status=status;
		this.phoneNo=phoneNo;
		this.clientName=clientName;
	}

	public Long getId() {
		return id;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public String getStatus() {
		return status;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public String getClientName() {
		return clientName;
	}
	
	
	
	
}
