package org.mifosplatform.finance.payments.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_paypal_payment_details")
public class PaypalEnquirey extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "client_id", nullable = false)
	private Long clientId;

	@Column(name = "payment_id", nullable = false)
	private String paymentId;

	@Column(name = "state")
	private String state;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@Column(name = "payer_email_id")
	private String payerEmailId;

	@Column(name = "payer_id")
	private String payerId;
	
	@Column(name = "total_amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal totalAmount;

	@Column(name = "currency")
	private String currency;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "obs_id")
	private Long obsPaymentId;

	
	public PaypalEnquirey(){
		
	}

	public PaypalEnquirey(Long clientId, String state, String paymentid, Date date) {
		this.clientId=clientId;
		this.paymentId=paymentid;
		this.state=state;
		this.paymentDate=date;
	}

	public void fromPaypalEnquireyTransaction(String emailId, String payerId,BigDecimal totalAmount, 
			String currency, String description,String paymentState) {
		
		this.payerEmailId=emailId;
		this.payerId=payerId;
		this.totalAmount=totalAmount;
		this.currency=currency;
		this.description=description;
		this.state=paymentState;
		
		
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getObsPaymentId() {
		return obsPaymentId;
	}

	public void setObsPaymentId(Long obsPaymentId) {
		this.obsPaymentId = obsPaymentId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}
