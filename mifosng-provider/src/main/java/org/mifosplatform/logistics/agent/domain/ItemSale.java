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
	
	@Column(name="agent_id")
	private Long agentId;
	
	@Column(name = "purchase_date")
	private Date purchaseDate;
	
	@Column(name="order_quantity")
	private Long orderQuantity;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "itemsale", orphanRemoval = true)
	private ItemSaleInvoice itemSaleInvoice = new ItemSaleInvoice();
	
	public ItemSale(){
		
	}


	public ItemSale(Long itemId, Long agentId, Date purchaseDate,Long orderQuantity) {
		
		this.itemId=itemId;
		this.agentId=agentId;
		this.purchaseDate=purchaseDate;
		this.orderQuantity=orderQuantity;
		
	}


	public static ItemSale fromJson(JsonCommand command) {
	
		final Long itemId=command.longValueOfParameterNamed("itemId");
		final Long agentId=command.longValueOfParameterNamed("agentId");
		final Date purchaseDate =command.localDateValueOfParameterNamed("purchaseDate").toDate();
		final Long orderQuantity=command.longValueOfParameterNamed("orderQuantity");
		
		return new ItemSale(itemId,agentId,purchaseDate,orderQuantity);
				
		
	}


	public void setItemSaleInvoice(ItemSaleInvoice invoice) {
		invoice.update(this);
		this.itemSaleInvoice=invoice;
		
	}


	public Long getItemId() {
		return itemId;
	}


	public Long getAgentId() {
		return agentId;
	}


	public Date getPurchaseDate() {
		return purchaseDate;
	}


	public Long getOrderQuantity() {
		return orderQuantity;
	}


	public ItemSaleInvoice getItemSaleInvoice() {
		return itemSaleInvoice;
	}
	
	
	

}
