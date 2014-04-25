package org.mifosplatform.finance.billingorder.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_invoice")
public class Invoice extends AbstractAuditableCustom<AppUser,Long>{


	@Column(name="client_id")
	private Long clientId;

	@Column(name="invoice_date")
	private Date invoiceDate;

	@Column(name="invoice_amount")
	private BigDecimal invoiceAmount;

	@Column(name="netcharge_amount")
	private BigDecimal netChargeAmount;

	@Column(name="tax_amount")
	private BigDecimal taxAmount;
	
	@Column(name="due_amount")
	private BigDecimal dueAmount;

	@Column(name="invoice_status")
	private String invoiceStatus;
	
	@Column(name="bill_id")
	private Long billId;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice", orphanRemoval = true)
	private List<BillingOrder> charges = new ArrayList<BillingOrder>();
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice", orphanRemoval = true)
	private List<InvoiceTax> chargeTaxs = new ArrayList<InvoiceTax>();


	
   public Invoice(){
	
    }
	public  Invoice(final Long clientId, final Date invoiceDate, final BigDecimal invoiceAmount, final BigDecimal netChargeAmount, 
			final BigDecimal taxAmount, final String  invoiceStatus){
		
		this.clientId = clientId;
		this.invoiceDate = invoiceDate;
		this.invoiceAmount = invoiceAmount;
		this.netChargeAmount = netChargeAmount;
		this.taxAmount = taxAmount;
		this.invoiceStatus = invoiceStatus;
		this.dueAmount= invoiceAmount;
      
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
		this.dueAmount=invoiceAmount;
	}

	public BigDecimal getNetChargeAmount() {
		return netChargeAmount;
	}

	public void setNetChargeAmount(BigDecimal netChargeAmount) {
		this.netChargeAmount = netChargeAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public void updateBillId(Long billId) {
		this.billId=billId;
		
	}

	public void addCharges(BillingOrder charge) {
		charge.update(this);
		this.charges.add(charge);

	}

	public List<BillingOrder> getCharges(){
		return this.charges;
	}
	
	public void updateAmount(BigDecimal amountPaid) {
		this.dueAmount=this.dueAmount.subtract(amountPaid);
		
	}
	}


