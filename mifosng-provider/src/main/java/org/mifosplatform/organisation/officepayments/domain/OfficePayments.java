package org.mifosplatform.organisation.officepayments.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@SuppressWarnings("serial")
@Entity
@Table(name = "m_payments",uniqueConstraints = {@UniqueConstraint(name = "receipt_no", columnNames = { "receipt_no" })})
public class OfficePayments extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "office_id", nullable = false, length = 20)
	private Long officeId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@Column(name = "paymode_id", nullable = false, length = 20)
	private int paymodeId;

	@Column(name = "amount_paid", scale = 6, precision = 19, nullable = true)
	private BigDecimal amountPaid;
	
	@Column(name = "receipt_no")
	private String receiptNo;

	@Column(name = "Remarks")
	private String remarks;
	
	@Column(name = "is_deleted")
	private char isDeleted='N';
	
	public OfficePayments(){
		
	}

	public OfficePayments(final Long officeId, final BigDecimal amountPaid,final LocalDate paymentDate,
							final String remarks, final Long paymodeId, String receiptNo) {


		this.officeId = officeId;
		this.amountPaid = amountPaid;
		this.paymentDate = paymentDate.toDate();
		this.remarks = remarks;
		this.paymodeId = paymodeId.intValue();
		this.receiptNo=receiptNo;

	}
	
	public static OfficePayments fromJson(JsonCommand command){
		
		final LocalDate paymentDate = command
				.localDateValueOfParameterNamed("paymentDate");
		final Long paymodeId = command.longValueOfParameterNamed("paymentCode");
				
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");
		final String remarks = command.stringValueOfParameterNamed("remarks");
		final String receiptNo=command.stringValueOfParameterNamed("receiptNo");
		
		return new OfficePayments(command.entityId(), amountPaid, paymentDate, remarks, paymodeId, receiptNo);
	}
	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public int getPaymodeId() {
		return paymodeId;
	}

	public void setPaymodeId(int paymodeId) {
		this.paymodeId = paymodeId;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
