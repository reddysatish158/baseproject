package org.mifosplatform.portfolio.planmapping.service;

import java.util.List;

import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.planmapping.data.PlanMappingData;

public interface PlanMappingReadPlatformService {

	List<PlanMappingData> getPlanMapping();

	List<PlanCodeData> getPlanCode();

	PlanMappingData getPlanMapping(Long planMappingId);
	
	

}
