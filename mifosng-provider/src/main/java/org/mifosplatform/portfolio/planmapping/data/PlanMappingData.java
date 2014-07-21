package org.mifosplatform.portfolio.planmapping.data;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public class PlanMappingData {
	
	private String planStatus;
	private Long id;
	private Long planId;
	private String planCode;
	private String planIdentification;
	private List<EnumOptionData> status;
	private List<PlanCodeData> planCodeData;

	public PlanMappingData(Long id, String planCode, String planIdentification,String planStatus, Long planId) {

		this.id = id;
		this.planCode = planCode;
		this.planIdentification = planIdentification;
		this.planStatus = planStatus;
		this.planId = planId;
	}

	public PlanMappingData(List<PlanCodeData> planCodeData,List<EnumOptionData> status) {
		
		this.planCodeData = planCodeData;
		this.status = status;	
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public Long getId() {
		return id;
	}

	public String getPlanCode() {
		return planCode;
	}

	public String getPlanIdentification() {
		return planIdentification;
	}

	public List<EnumOptionData> getStatus() {
		return status;
	}

	public void setStatus(List<EnumOptionData> status) {
		this.status = status;
	}

	public List<PlanCodeData> getPlanCodeData() {
		return planCodeData;
	}

	public void setPlanCodeData(List<PlanCodeData> planCodeData) {
		this.planCodeData = planCodeData;
	}

	public Long getPlanId() {
		return planId;
	}
	
	
	
	
	

}
