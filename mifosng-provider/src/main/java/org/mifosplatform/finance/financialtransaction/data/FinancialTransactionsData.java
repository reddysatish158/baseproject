package org.mifosplatform.finance.financialtransaction.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

public class FinancialTransactionsData {

	private Long id;
	private Long transactionId;
	private Date transactionDate;
	private String transactionType;
	private BigDecimal amount;
	private LocalDate transDate;
	private LocalDate transactionalDate;
	private LocalDate billDate;
	private LocalDate dueDate;
	private String transaction;
	private String chargeType;
	private String chargeDescription;
	private BigDecimal chargeAmount;
	private BigDecimal taxAmount;
	private BigDecimal discountAmount;
	private BigDecimal netChargeAmount;
	private LocalDate chargeStartDate;
	private LocalDate chargeEndDate;
	private List<FinancialTransactionsData> transactionsDatas;
	private BigDecimal creditAmount;
	private BigDecimal debitAmount;
	private Long chargeId;
	private Long orderId;
	private String userName;
	private String transactionCategory;
	private boolean flag;

	public FinancialTransactionsData(final Long transactionId,final Date transactionDate,String transactionType,BigDecimal amount) {
		this.transactionId = transactionId;
		this.transactionDate = transactionDate;
		this.transactionType = transactionType;
		this.amount = amount;
	}

	public FinancialTransactionsData(Long transactionId, LocalDate transDate,String transactionType, BigDecimal amount,
			BigDecimal creditAmount, BigDecimal debitAmount,String userName, String transactionCategory, boolean flag) {

		this.transactionId = transactionId;
		this.transDate = transDate;
		this.transactionType = transactionType;
		this.amount = amount;
		this.transaction="INVOICE";
		this.creditAmount=creditAmount;
		this.debitAmount=debitAmount;
		this.userName=userName;
		this.transactionCategory=transactionCategory;
		this.flag=flag;

	}



	public FinancialTransactionsData(Long transctionId,
			String transactionType, LocalDate transactionDate, BigDecimal amount) {
		this.transactionId = transctionId;
		this.transactionalDate = transactionDate;
		this.transactionType = transactionType;
		this.amount = amount;
	}

	public FinancialTransactionsData(Long id, LocalDate billDate,
			LocalDate dueDate, BigDecimal amount) {
		this.id=id;
		this.billDate=billDate;
		this.dueDate=dueDate;
		this.amount=amount;
	}

	public FinancialTransactionsData(Long chargeId, String chargeType,
			String chargeDescription, BigDecimal chargeAmount,
			BigDecimal taxAmount,BigDecimal discountAmount, BigDecimal netChargeAmount,LocalDate chargeStartDate, LocalDate chargeEndDate, Long orderId) {
		this.chargeId=chargeId;
		this.chargeType=chargeType;
		this.chargeDescription=chargeDescription;
		this.chargeAmount=chargeAmount;
		this.taxAmount=taxAmount;
		this.discountAmount=discountAmount;
		this.netChargeAmount=netChargeAmount;
		this.chargeStartDate=chargeStartDate;
		this.chargeEndDate=chargeEndDate;
		this.orderId=orderId;
	}

	public FinancialTransactionsData(
			List<FinancialTransactionsData> transactionData) {
		this.transactionsDatas=transactionData;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getTransDate() {
		return transDate;
	}

	public LocalDate getTransactionalDate() {
		return transactionalDate;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getBillDate() {
		return billDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	/**
	 * @return the transaction
	 */
	public String getTransaction() {
		return transaction;
	}

	/**
	 * @return the chargeType
	 */
	public String getChargeType() {
		return chargeType;
	}

	/**
	 * @return the chargeDescription
	 */
	public String getChargeDescription() {
		return chargeDescription;
	}

	/**
	 * @return the chargeAmount
	 */
	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	/**
	 * @return the taxAmount
	 */
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @return the discountAmount
	 */
	public BigDecimal getdiscountAmount() {
		return discountAmount;
	}
	/**
	 * @return the netChargeAmount
	 */
	public BigDecimal getNetChargeAmount() {
		return netChargeAmount;
	}

	
	
	
	/**
	 * @return the chargeStartDate
	 */
	public LocalDate getChargeStartDate() {
		return chargeStartDate;
	}

	/**
	 * @return the chargeEndDate
	 */
	public LocalDate getChargeEndDate() {
		return chargeEndDate;
	}

	/**
	 * @return the transactionsDatas
	 */
	public List<FinancialTransactionsData> getTransactionsDatas() {
		return transactionsDatas;
	}

	/**
	 * @return the creditAmount
	 */
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	/**
	 * @return the debitAmount
	 */
	public BigDecimal getDebitAmount() {
		return debitAmount;
	}


}
