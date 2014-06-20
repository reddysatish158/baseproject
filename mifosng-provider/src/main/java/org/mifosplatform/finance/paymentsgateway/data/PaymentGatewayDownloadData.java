/**
 * 
 */
package org.mifosplatform.finance.paymentsgateway.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

/**
 * @author rakesh
 *
 */
public class PaymentGatewayDownloadData {

	
	private String SerialNo;
	private LocalDate PaymendDate;
	private BigDecimal AmountPaid;
	private String PhoneMSISDN;
	private String Remarks;
	private String Status;
	private String ReceiptNo;
	private String paymentId;

	public PaymentGatewayDownloadData(String SerialNumber, LocalDate PaymentDate,
			BigDecimal AmountPaid, String PhoneMSISDN, String Remarks,
			String Status, String ReceiptNo,String paymentId) {
		this.SerialNo = SerialNumber;
		this.PaymendDate = PaymentDate;
		this.AmountPaid = AmountPaid;
		this.PhoneMSISDN = PhoneMSISDN;
		this.Remarks = Remarks;
		this.Status = Status;
		this.ReceiptNo = ReceiptNo;
		this.paymentId = paymentId;
	}
	

	public String getSerialNo() {
		return SerialNo;
	}

	public LocalDate getPaymendDate() {
		return PaymendDate;
	}

	public BigDecimal getAmountPaid() {
		return AmountPaid;
	}

	public String getPhoneMSISDN() {
		return PhoneMSISDN;
	}

	public String getRemarks() {
		return Remarks;
	}

	public String getStatus() {
		return Status;
	}

	public String getReceiptNo() {
		return ReceiptNo;
	}

	public void setSerialNo(String serialNo) {
		SerialNo = serialNo;
	}

	public void setPaymendDate(LocalDate paymendDate) {
		PaymendDate = paymendDate;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		AmountPaid = amountPaid;
	}

	public void setPhoneMSISDN(String phoneMSISDN) {
		PhoneMSISDN = phoneMSISDN;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public void setReceiptNo(String receiptNo) {
		ReceiptNo = receiptNo;
	}


	public String getPaymentId() {
		return paymentId;
	}


	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	
	
}
