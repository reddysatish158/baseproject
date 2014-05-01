package org.mifosplatform.logistics.agent.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;


@Entity
@Table(name="m_invoice")
public class ItemSaleInvoice extends AbstractAuditableCustom<AppUser,Long>{
	
	
	@OneToOne
	@JoinColumn(name="sale_id")
	private ItemSale itemsale;
	
	@Column(name="invoice_date")
	private Date invoiceDate;
	
	@Column(name="charge_amount")
	private BigDecimal chargeAmount;
	
	@Column(name="tax_amount")
	private BigDecimal taxAmount;
	
	@Column(name="tax_percantage")
	private BigDecimal taxPercantage;
	
	@Column(name="invoice_amount")
	private BigDecimal invoiceAmount;
	
	
	public ItemSaleInvoice(Date date, BigDecimal chargeAmount, BigDecimal taxPercantage){
		
		
		this.chargeAmount=chargeAmount;
		this.taxPercantage=taxPercantage;
		this.invoiceDate=date;
	}

	public ItemSaleInvoice() {
		// TODO Auto-generated constructor stub
	}

	public static ItemSaleInvoice fromJson(JsonCommand command) {
		
		final BigDecimal chargeAmount=command.bigDecimalValueOfParameterNamed("chargeAmount");
		final BigDecimal taxPercantage=command.bigDecimalValueOfParameterNamed("taxPercantage");
		
		return new ItemSaleInvoice(new Date(),chargeAmount,taxPercantage);
		


	}



	public ItemSale getSale() {
		return itemsale;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public BigDecimal getTaxPercantage() {
		return taxPercantage;
	}

	public void updateAmounts(BigDecimal taxAmount) {
		
		this.taxAmount=taxAmount;
		this.invoiceAmount=this.chargeAmount.add(taxAmount);
		
	}

	public void update(ItemSale itemSale) {

		this.itemsale=itemSale;
	}
	
	
	
	

}
