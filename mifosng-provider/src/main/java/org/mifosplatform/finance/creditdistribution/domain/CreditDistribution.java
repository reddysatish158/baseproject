package org.mifosplatform.finance.creditdistribution.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.useradministration.domain.AppUser;

import com.google.gson.JsonElement;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_credit_distribution")
public class CreditDistribution extends AbstractAuditableCustom<AppUser, Long>{

	
	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "payment_id")
	private Long paymentId;

	@Column(name = "invoice_id")
	private Long invoiceId;
	
	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "distribution_date")
	private Date distributionDate;

	
	public CreditDistribution() {
		// TODO Auto-generated constructor stub
	}


	public CreditDistribution(Long paymentId, Long clientId, Long invoiceId,
			BigDecimal amount, Date distributionDate) {
		
		this.paymentId=paymentId;
		this.clientId=clientId;
		this.invoiceId=invoiceId;
		this.distributionDate=distributionDate;
		this.amount=amount;
		
	}


	public static CreditDistribution fromJson(JsonElement j,FromJsonHelper fromJsonHelper) {
	
	final Long paymentId = fromJsonHelper.extractLongNamed("paymentId",j);
	final Long clientId = fromJsonHelper.extractLongNamed("clientId", j);
	final Long invoiceId = fromJsonHelper.extractLongNamed("invoiceId", j);
	final BigDecimal amount = fromJsonHelper.extractBigDecimalWithLocaleNamed("amount", j);
	final Date distributionDate = new Date();
	return new CreditDistribution(paymentId,clientId,invoiceId,amount,distributionDate);
	
	}


	public Long getClientId() {
		return clientId;
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


	public Date getDistributionDate() {
		return distributionDate;
	}

	
}