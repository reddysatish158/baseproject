package org.mifosplatform.logistics.agent.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;


@Entity
@Table(name="b_itemsale")
public class ItemSale extends AbstractAuditableCustom<AppUser, Long>{
	
	
	@Column(name="item_id")
	private Long itemId;
	
	@Column(name="purchase_from")
	private Long purchasefrom;
	
	@Column(name="purchase_by")
	private Long purchaseBy;
	
	@Column(name="received_quantity")
	private Long receivedQuantity=0L;
	
	@Column(name="charge_code")
	private String chargeCode;

	@Column(name="status")
	private String status="New";


	@Column(name = "purchase_date")
	private Date purchaseDate;
	
	@Column(name="order_quantity")
	private Long orderQuantity;
	
	

	public void setOrderQuantity(Long orderQuantity) {
		this.orderQuantity = orderQuantity;
	}


	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "itemsale", orphanRemoval = true)
	private ItemSaleInvoice itemSaleInvoice = new ItemSaleInvoice();
	
	public ItemSale(){
		
	}


	public ItemSale(Long itemId, Long purchaseFrom,Long purchaseBy, Date purchaseDate,Long orderQuantity) {
		
		this.itemId=itemId;
		this.purchasefrom=purchaseFrom;
		this.purchaseBy=purchaseBy;
		this.purchaseDate=purchaseDate;
		this.orderQuantity=orderQuantity;
		
	}


	public static ItemSale fromJson(JsonCommand command) {
		
		final Long itemId=command.longValueOfParameterNamed("itemId");
		final Long purchaseFrom=command.longValueOfParameterNamed("purchaseFrom");
		final Long purchaseBy=command.longValueOfParameterNamed("purchaseBy");
		final Date purchaseDate =command.localDateValueOfParameterNamed("purchaseDate").toDate();
		final Long orderQuantity=command.longValueOfParameterNamed("orderQuantity");

		return new ItemSale(itemId,purchaseFrom,purchaseBy,purchaseDate,orderQuantity);
				
		
	}


	public void setItemSaleInvoice(ItemSaleInvoice invoice) {
		invoice.update(this);
		this.itemSaleInvoice=invoice;
		
	}


	public Long getItemId() {
		return itemId;
	}


	public Long getPurchaseFrom() {
		return purchasefrom;
	}


	public Long getReceivedQuantity() {
		return receivedQuantity;
	}


	public void setReceivedQuantity(Long receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}


	public String getChargeCode() {
		return chargeCode;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}


	public Long getOrderQuantity() {
		return orderQuantity;
	}


	public void setPurchaseFrom(Long purchasefrom) {
        this.purchasefrom=purchasefrom;
	}

	public Long getPurchaseBy() {
		return purchaseBy;
	}


	public ItemSaleInvoice getItemSaleInvoice() {
		return itemSaleInvoice;
	}
	
	
	

}
