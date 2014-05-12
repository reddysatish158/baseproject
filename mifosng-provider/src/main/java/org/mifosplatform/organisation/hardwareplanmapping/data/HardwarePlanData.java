
package org.mifosplatform.organisation.hardwareplanmapping.data;

import java.util.List;

import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;


public class HardwarePlanData {

	private  Long id;
	private  String planCode;
	private  String itemCode;
	
	
	private List<ItemData> itemDatas;
	private List<PlanCodeData> planDatas;

	public HardwarePlanData(List<ItemData> itemsdata, List<PlanCodeData> plansData) {

		this.itemDatas = itemsdata;
		this.planDatas = plansData;

	}

	public HardwarePlanData(Long id, String planCode,String itemCode) {

		this.id = id;
		this.planCode = planCode;
		this.itemCode = itemCode;
		
	}

	
	public Long getId() {
		return id;
	}

	public String getplanCode() {
		return planCode;
	}

	
	public String getPlanCode() {
		return planCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public List<ItemData> getItemDatas() {
		return itemDatas;
	}

	public List<PlanCodeData> getPlanDatas() {
		return planDatas;
	}

	public void addData(List<ItemData> data) {
		this.itemDatas=data;
		
	}

	public void addPlan(List<PlanCodeData> planData) {

		this.planDatas=planData;
	}

	

	

}
