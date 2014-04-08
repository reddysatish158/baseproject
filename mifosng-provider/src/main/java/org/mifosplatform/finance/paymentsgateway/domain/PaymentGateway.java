package org.mifosplatform.finance.paymentsgateway.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
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
	
	@Temporal(TemporalType.TIMESTAMP)
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
	
	@Column(name = "Remarks")
	private String remarks;
	
	@Column(name = "t_status")
	private String tStatus;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "is_auto" ,nullable = false)
	private boolean isAuto=true;
	
	public PaymentGateway(){
		
	}
	
	public PaymentGateway(String deviceId, String partyId,Date paymentDate, BigDecimal amountPaid, 
			      String receiptNo,String source, String details) {
		
		this.deviceId=deviceId;
		this.partyId=partyId;
		this.paymentDate=paymentDate;
		this.amountPaid=amountPaid;
		this.receiptNo=receiptNo;
		this.source=source;
		this.details=details;
	}

	public PaymentGateway(String deviceId, String transactionId,BigDecimal amountPaid, String phoneNo, 
			String type,String tStatus, String details, Date date, String source) {
		this.deviceId=deviceId;
		this.partyId=phoneNo;
		this.paymentDate=date;
		this.amountPaid=amountPaid;
		this.receiptNo=transactionId;
		this.source=source;
		this.details=details;
		this.type=type;
		this.tStatus=tStatus;
	}

	public Map<String, Object> fromJson(JsonCommand command) {
		 
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		 final String remarks = "remarks";
		 if (command.isChangeInStringParameterNamed(remarks,this.remarks)) {
				final String newValue = command
						.stringValueOfParameterNamed("remarks");
				actualChanges.put(remarks, newValue);
				this.remarks = StringUtils.defaultIfEmpty(newValue, null);
		 }
		 final String status = "status";
			if (command.isChangeInStringParameterNamed(status,
					this.status)) {
				final String newValue = command
						.stringValueOfParameterNamed("status");
				actualChanges.put(status, newValue);
				this.status = StringUtils.defaultIfEmpty(newValue, null);
			}
			return actualChanges;
		 
		 
		 
	}

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

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public boolean isAuto() {
		return isAuto;
	}

	
	
	
	
	

}
