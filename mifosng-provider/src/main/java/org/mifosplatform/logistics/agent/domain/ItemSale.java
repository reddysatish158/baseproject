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


	public ItemSale(Long itemId, Long agentId, Date purchaseDate,Long orderQuantity, String chargeCode) {
		
		this.itemId=itemId;
		this.agentId=agentId;
		this.purchaseDate=purchaseDate;
		this.orderQuantity=orderQuantity;
		this.chargeCode=chargeCode;
		
	}


	public static ItemSale fromJson(JsonCommand command) {
	
		final Long itemId=command.longValueOfParameterNamed("itemId");
		final Long agentId=command.longValueOfParameterNamed("agentId");
		final Date purchaseDate =command.localDateValueOfParameterNamed("purchaseDate").toDate();
		final Long orderQuantity=command.longValueOfParameterNamed("orderQuantity");
		final String chargeCode=command.stringValueOfParameterNamed("chargeCode");
		
		return new ItemSale(itemId,agentId,purchaseDate,orderQuantity,chargeCode);
				
		
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

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}



	public ItemSaleInvoice getItemSaleInvoice() {
		return itemSaleInvoice;
	}
	
	
	

}
