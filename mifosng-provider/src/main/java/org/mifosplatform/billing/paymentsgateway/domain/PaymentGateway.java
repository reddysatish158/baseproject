package org.mifosplatform.billing.paymentsgateway.domain;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;


@SuppressWarnings("serial")
@Entity
@Table(name = "b_paymentgateway")
public class PaymentGateway extends AbstractPersistable<Long> {
	
	@Column(name = "key_id")
	private String deviceId;

	@Column(name="party_id")
	private String partyId;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@Column(name = "amount_paid", scale = 6, precision = 19, nullable = false)
	private BigDecimal amountPaid;
	
	@Column(name = "receipt_no")
	private String receiptNo;
	
	@Column(name = "source")
	private String source;

	@Column(name="t_details")
	private String details;
	
	@Column(name = "payment_id")
	private String paymentId;
	
	@Column(name = "obs_id")
	private Long obsId;
	
	@Column(name = "status")
	private String status;
	
	public PaymentGateway(){
		
	}
	
	public PaymentGateway(String deviceId, String partyId,Date paymentDate, BigDecimal amountPaid, 
			      String receiptNo,String source, String paymentId, String details) {
		
		this.deviceId=deviceId;
		this.partyId=partyId;
		this.paymentDate=paymentDate;
		this.amountPaid=amountPaid;
		this.receiptNo=receiptNo;
		this.source=source;
		this.paymentId=paymentId;
		this.details=details;
	}


	/*public static PaymentGateway fromJson(JsonCommand command) {
		
		final String deviceId=command.stringValueOfParameterNamed("KEY_ID");
		final String partyId=command.stringValueOfParameterNamed("PARTY_ID");
		final String paymentDate = command.stringValueOfParameterNamed("PAYMENT_DATE");
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("AMOUNT_PAID");
		final String receiptNo = command.stringValueOfParameterNamed("RECEIPT_NO");
		final String source = command.stringValueOfParameterNamed("SOURCE");
		final String paymentId=command.stringValueOfParameterNamed("PAYMENT_ID");
		final String details=command.stringValueOfParameterNamed("DETIALS");
		DateFormat readFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		  Date date = null;
		    try {
		       date = readFormat.parse(paymentDate);
		    } catch ( ParseException e ) {
		        e.printStackTrace();
		    }
		return new PaymentGateway(deviceId, partyId, date, amountPaid, receiptNo, source,
				paymentId, details);

	}*/

	public String getDeviceId() {
		return deviceId;
	}

	public String getPartyId() {
		return partyId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public String getSource() {
		return source;
	}

	public String getDetails() {
		return details;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public Long getObsId() {
		return obsId;
	}

	public String getStatus() {
		return status;
	}

	public void setObsId(Long value) {
		this.obsId = value;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	

}
