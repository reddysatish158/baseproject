package org.mifosplatform.logistics.agent.data;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.logistics.item.data.ChargesData;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.office.data.OfficeData;

public class AgentItemSaleData {

	private final List<OfficeData> officeDatas;
	private final List<ItemData> itemDatas;
	private final List<ChargesData> chargeDatas;
	private final Long id;
	private final Long itemId;
	private final Long agentId;
	private final String agentName;
	private final String itemName;
	private final Long orderQunatity;
	private final BigDecimal chargeAmount;
	private final BigDecimal invoiceAmount;
	private final BigDecimal tax;
	
	
	public static AgentItemSaleData instance(AgentItemSaleData itemSaleData, List<OfficeData> officeDatas,List<ItemData> itemDatas,List<ChargesData> chargeDatas) {

		return new AgentItemSaleData(itemSaleData.id,itemSaleData.itemId,itemSaleData.agentId,itemSaleData.itemName,itemSaleData.agentName,
				itemSaleData.orderQunatity,itemSaleData.chargeAmount,itemSaleData.tax,itemSaleData.invoiceAmount,officeDatas,itemDatas,chargeDatas);
	}

	public static AgentItemSaleData withTemplateData(List<OfficeData> officeDatas, List<ItemData> itemDatas,List<ChargesData> chargeDatas) {

		return new AgentItemSaleData(null,null,null,null,null,null,null,null,null,officeDatas,itemDatas,chargeDatas);
	}

	public AgentItemSaleData(Long id, Long itemId, Long agentId,String itemName, String agentName,Long orderQunatity,
			BigDecimal chargeAmount, BigDecimal tax, BigDecimal invoiceAmount,List<OfficeData> officeDatas, List<ItemData> itemDatas,List<ChargesData> chargeDatas) {
		
		this.id=id;
		this.itemId=itemId;
		this.itemName=itemName;
		this.agentId=agentId;
		this.agentName=agentName;
		this.orderQunatity=orderQunatity;
		this.chargeAmount=chargeAmount;
		this.invoiceAmount=invoiceAmount;
		this.tax=tax;
		this.officeDatas=officeDatas;
		this.itemDatas=itemDatas;
		this.chargeDatas=chargeDatas;
	
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
