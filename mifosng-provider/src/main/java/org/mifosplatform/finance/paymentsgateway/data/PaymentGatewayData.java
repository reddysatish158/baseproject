package org.mifosplatform.finance.paymentsgateway.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;

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
	private List<MediaEnumoptionData> statusData;
	private String remarks;
	private LocalDate paymendDate;
	private Object phoneMSISDN;

	public PaymentGatewayData(Long id, String serialNo, String phoneNo,
			LocalDate paymentDate, BigDecimal amountPaid, String receiptNo,
			String clientName, String status, Long paymentId, String remarks) {
		this.id=id;
		this.paymentId=paymentId;
		this.serialNo=serialNo;
		this.paymentDate=paymentDate;
		this.receiptNo=receiptNo;
		this.amountPaid=amountPaid;
		this.status=status;
		this.phoneNo=phoneNo;
		this.clientName=clientName;
		this.remarks=remarks;
		
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

	public List<MediaEnumoptionData> getStatusData() {
		return statusData;
	}

	public void setStatusData(List<MediaEnumoptionData> statusData) {
		this.statusData = statusData;
	}

	public String getRemarks() {
		return remarks;
	}
	
	
	
	
}
