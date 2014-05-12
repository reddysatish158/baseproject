package org.mifosplatform.finance.payments.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
import javax.persistence.UniqueConstraint;
@SuppressWarnings("serial")
@Entity
@Table(name = "b_payments",uniqueConstraints = {@UniqueConstraint(name = "receipt_no", columnNames = { "receipt_no" })})
public class Payment extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "client_id", nullable = false)
	private Long clientId;

	@Column(name = "amount_paid", scale = 6, precision = 19, nullable = false)
	private BigDecimal amountPaid;

	@Column(name = "bill_id", nullable = false)
	private Long statementId;

	@Column(name = "is_deleted", nullable = false)
	private boolean deleted = false;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "payment_date")
	private Date paymentDate;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "paymode_id")
	private int paymodeId;
	
	@Column(name = "transaction_id")
	private String transactionId;
	
	@Column(name = "cancel_remark")
	private String cancelRemark;
	
	@Column(name = "receipt_no")
	private String receiptNo;
	
	@Column(name = "invoice_id", nullable = false)
	private Long invoiceId;

	public Payment() {
	}

	public Payment(final Long clientId, final Long paymentId,final Long externalId, final BigDecimal amountPaid,final Long statmentId,
			final LocalDate paymentDate,final String remark, final Long paymodeCode, String transId,String receiptNo, Long invoiceId) {


		this.clientId = clientId;
		this.statementId = statmentId;
		this.amountPaid = amountPaid;
		this.paymentDate = paymentDate.toDate();
		this.remarks = remark;
		this.paymodeId = paymodeCode.intValue();
		this.transactionId=transId;
		this.receiptNo=receiptNo;
		this.invoiceId=invoiceId;

	}

	public static Payment fromJson(JsonCommand command, Long clientid) {
		final LocalDate paymentDate = command
				.localDateValueOfParameterNamed("paymentDate");
		final Long paymentCode = command.longValueOfParameterNamed("paymentCode");
				
		final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");
		final String remarks = command.stringValueOfParameterNamed("remarks");
		final String txtid=command.stringValueOfParameterNamed("txn_id");
		final String receiptNo=command.stringValueOfParameterNamed("receiptNo");
		final Long invoiceId=command.longValueOfParameterNamed("invoiceId");

		return new Payment(clientid, null, null, amountPaid, null, paymentDate,remarks, paymentCode,txtid,receiptNo,invoiceId);


	}

	public Long getClientId() {
		return clientId;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public Long getStatementId() {
		return statementId;
	}

	public boolean isDeleted() {
		return deleted;
	}
	
	

	public Long getInvoiceId() {
		return invoiceId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public int getPaymodeCode() {
		return paymodeId;
	}

		public void updateBillId(Long billId) {
		this.statementId=billId;

	}

		public void cancelPayment(JsonCommand command) {
			
			final String cancelRemarks=command.stringValueOfParameterNamed("cancelRemark");
			this.cancelRemark=cancelRemarks;
			this.deleted=true;
			
		}

		public String getReceiptNo() {
			return receiptNo;
		}

		public int getPaymodeId() {
			return paymodeId;
		}

		public String getTransactionId() {
			return transactionId;
		}

		public String getCancelRemark() {
			return cancelRemark;
		}
		
	public void setInvoiceId(Long invoiceId){
		this.invoiceId=invoiceId;
	}

}

