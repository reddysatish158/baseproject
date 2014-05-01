package org.mifosplatform.logistics.agent.data;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.office.data.OfficeData;

public class AgentItemSaleData {

	private List<OfficeData> officeDatas;
	private List<ItemData> itemDatas;
	private Long id;
	private Long itemId;
	private Long agentId;
	private String agentName;
	private String itemName;
	private Long orderQunatity;
	private BigDecimal chargeAmount;
	private BigDecimal invoiceAmount;
	private BigDecimal tax;
	

	public AgentItemSaleData(List<OfficeData> officeDatas, List<ItemData> itemDatas) {

		this.officeDatas=officeDatas;
		this.itemDatas=itemDatas;
	
	}

	public AgentItemSaleData(Long id, Long itemId, Long agentId,String itemName, String agentName,
			Long orderQunatity,BigDecimal chargeAmount, BigDecimal tax, BigDecimal invoiceAmount) {
		
		this.id=id;
		this.itemId=itemId;
		this.itemName=itemName;
		this.agentId=agentId;
		this.agentName=agentName;
		this.orderQunatity=orderQunatity;
		this.chargeAmount=chargeAmount;
		this.invoiceAmount=invoiceAmount;
		this.tax=tax;

	
	
	}

	public List<OfficeData> getOfficeDatas() {
		return officeDatas;
	}

	public List<ItemData> getItemDatas() {
		return itemDatas;
	}

	public Long getId() {
		return id;
	}

	public Long getItemId() {
		return itemId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getItemName() {
		return itemName;
	}

	public Long getOrderQunatity() {
		return orderQunatity;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public BigDecimal getTax() {
		return tax;
	}

	
	
}
