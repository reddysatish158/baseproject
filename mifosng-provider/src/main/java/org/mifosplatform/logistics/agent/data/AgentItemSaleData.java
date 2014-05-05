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
	
	
	public static AgentItemSaleData templateData(AgentItemSaleData itemSaleData, List<OfficeData> officeDatas,List<ItemData> itemDatas) {

		return null;
	}

	public AgentItemSaleData(AgentItemSaleData itemSaleData, List<OfficeData> officeDatas, List<ItemData> itemDatas) {
		
		if(itemSaleData !=null){
			this.id=itemSaleData.id;
			this.itemId=itemSaleData.itemId;
			this.agentId=itemSaleData.agentId;
			this.agentName=itemSaleData.agentName;
			this.itemName=itemSaleData.itemName;
			this.orderQunatity=itemSaleData.orderQunatity;
			this.chargeAmount=itemSaleData.chargeAmount;
			this.invoiceAmount=itemSaleData.invoiceAmount;
			this.tax=itemSaleData.tax;
			
		}

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
