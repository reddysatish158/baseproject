package org.mifosplatform.logistics.onetimesale.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.xmlbeans.impl.tool.XSTCTester.Harness;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_onetime_sale")
public class OneTimeSale extends AbstractAuditableCustom<AppUser, Long> {



	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "units", length = 65536)
	private String units;

	@Column(name = "charge_code")
	private String chargeCode;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "quantity")
	private String quantity;

	@Column(name = "total_price")
	private BigDecimal totalPrice;

	@Column(name = "sale_date")
	private Date saleDate;

	@Column(name = "item_id")
	private Long itemId;
	
	@Column(name = "bill_id")
	private Long billId;

	@Column(name = "is_invoiced", nullable = false)
	private char deleted = 'n';
	
	@Column(name="hardware_allocated",nullable=false)
	private String hardwareAllocated = "UNALLOCATED";

	@Column(name = "discount_id")
	private Long discountId;
	
	@Column(name = "is_deleted", nullable = false)
	private char isDeleted = 'N';
	
	@Column(name = "office_id")
	private Long officeId;
	
	public OneTimeSale(){}
	
	public OneTimeSale(Long clientId, Long itemId,String units,String quantity,
			 String chargeCode, BigDecimal unitPrice,
			BigDecimal totalPrice, LocalDate saleDate, Long discountId, Long officeId,String saleType) {

	this.clientId=clientId;
	this.itemId=itemId;
	this.units=units;
	this.chargeCode=chargeCode;
	this.unitPrice=unitPrice;
	this.totalPrice=totalPrice;
	this.quantity=quantity;
	this.saleDate=saleDate.toDate();
	this.discountId=discountId;
	this.officeId=officeId;
		if(saleType.equalsIgnoreCase("SecondSale")){
			this.deleted='y';
		}
	}

	public Long getClientId() {
		return clientId;
	}

	public String getUnits() {
		return units;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public String getQuantity() {
		return quantity;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public Long getItemId() {
		return itemId;
	}

	public char getDeleted() {
		return deleted;
	}

	public void setDeleted(char deleted) {
		this.deleted = deleted;
	}
	
	public void updateBillId(Long billId) {
        this.billId=billId;
       
    }
	
	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public static OneTimeSale fromJson(Long clientId, JsonCommand command, ItemMaster item) {
			final String saleType = command.stringValueOfParameterNamed("saleType");
		    final Long itemId=command.longValueOfParameterNamed("itemId");
		    final String units = item.getUnits();
		    final String chargeCode = command.stringValueOfParameterNamed("chargeCode");
		    final String quantity = command.stringValueOfParameterNamed("quantity");
		    final BigDecimal unitPrice=command.bigDecimalValueOfParameterNamed("unitPrice");
		    final BigDecimal totalPrice=command.bigDecimalValueOfParameterNamed("totalPrice");
		    final LocalDate saleDate=command.localDateValueOfParameterNamed("saleDate");
		    final Long discountId=command.longValueOfParameterNamed("discountId");
		    final Long officeId=command.longValueOfParameterNamed("officeId");
          return new OneTimeSale(clientId, itemId, units, quantity, chargeCode, unitPrice, totalPrice, saleDate,discountId,officeId,saleType);
		 
	}
	public String getHardwareAllocated() {
		return hardwareAllocated;
	}
	
	public void setHardwareAllocated(String hardwareAllocated) {
		this.hardwareAllocated = hardwareAllocated;
	}

	public void setStatus() {
		this.hardwareAllocated = "UNALLOCATED";
		
	}


}
