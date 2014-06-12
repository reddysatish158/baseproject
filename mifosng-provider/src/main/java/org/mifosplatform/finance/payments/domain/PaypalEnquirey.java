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
	
	@Column(name = "payment_method")
	private String paymentMethod;
	
	@Column(name = "total_amount", scale = 6, precision = 19, nullable = false)
	private BigDecimal totalAmount;

	@Column(name = "currency")
	private String currency;
	
	@Column(name = "payer_email_id")
	private String payerEmailId;

	@Column(name = "payer_id")
	private String payerId;
	
	@Column(name = "card_number")
	private String cardNumber;

	@Column(name = "card_type")
	private String cardType;
	
	@Column(name = "card_expiry_date")
	private String cardExpiryDate;
	
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
			String currency, String description,String paymentState, String paymentMethod) {
		
		this.payerEmailId=emailId;
		this.payerId=payerId;
		this.totalAmount=totalAmount;
		this.currency=currency;
		this.description=description;
		this.state=paymentState;
		this.paymentMethod=paymentMethod;
			
	}
	
	public void fromPaypalEnquireyTransaction(String cardNumber,String cardType, String cardExpiryDate, BigDecimal totalAmount,
			String currency, String description, String paymentState,String paymentMethod) {
		
		this.cardNumber=cardNumber;
		this.cardType=cardType;
		this.cardExpiryDate=cardExpiryDate;
		this.totalAmount=totalAmount;
		this.currency=currency;
		this.description=description;
		this.state=paymentState;
		this.paymentMethod=paymentMethod;
		
		
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

	public Long getClientId() {
		return clientId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public String getState() {
		return state;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getPayerEmailId() {
		return payerEmailId;
	}

	public String getPayerId() {
		return payerId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public String getCardType() {
		return cardType;
	}

	public String getCardExpiryDate() {
		return cardExpiryDate;
	}
	
	

}
