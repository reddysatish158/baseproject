package org.mifosplatform.logistics.item.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_item_audit")
public class ItemMasterAudit extends AbstractPersistable<Long>{

	
	@Column(name = "itemmaster_id")
	private Long itemMasterId;
	
	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "changed_date")
	private Date changedDate;
	
	public ItemMasterAudit(){}
	
	
	public ItemMasterAudit(Long itemId,int existingUnitPrice, JsonCommand command) {
		
		final String itemCode = command.stringValueOfParameterNamed("itemCode");
		final BigDecimal unitPrice = new BigDecimal(existingUnitPrice);
		//final LocalDate changedDate = command.localDateValueOfParameterNamed("changedDate");
		final Date changedDate = new Date();
		this.itemMasterId = itemId;
		this.itemCode = itemCode;
		this.unitPrice = unitPrice;
		this.changedDate = changedDate;
		
	}


	public String getItemCode() {
		return itemCode;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public Long getItemMasterId() {
		return itemMasterId;
	}

}
