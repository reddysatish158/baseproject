package org.mifosplatform.portfolio.planservice.service;

import java.util.Collection;

import org.mifosplatform.portfolio.planservice.data.PlanServiceData;

public interface PlanServiceReadPlatformService {

	Collection<PlanServiceData> retrieveClientPlanService(Long clientId,
			String serviceType);

}
