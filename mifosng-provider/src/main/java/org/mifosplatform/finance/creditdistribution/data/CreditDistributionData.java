package org.mifosplatform.finance.creditdistribution.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.invoice.data.InvoiceData;
import org.mifosplatform.finance.payments.data.PaymentData;

public class CreditDistributionData {
	
	private  List<InvoiceData> invoiceDatas;
	private  List<PaymentData> paymentDatas;
	private  Long id;
	private LocalDate distributionDate;
	private Long paymentId;
	private Long invoiceId;
	private BigDecimal amount;

	public CreditDistributionData(List<InvoiceData> invoiceDatas,List<PaymentData> paymentDatas) {
		
		this.invoiceDatas=invoiceDatas;
		this.paymentDatas=paymentDatas;
	}

	public CreditDistributionData(Long id, LocalDate distributionDate,Long paymentId, Long invoiceId, BigDecimal amount) {
         
		this.id=id;
		this.distributionDate=distributionDate;
		this.paymentId=paymentId;
		this.invoiceId=invoiceId;
		this.amount=amount;
	
	}

	public List<InvoiceData> getInvoiceDatas() {
		return invoiceDatas;
	}

	public List<PaymentData> getPaymentDatas() {
		return paymentDatas;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getDistributionDate() {
		return distributionDate;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	
	
}
