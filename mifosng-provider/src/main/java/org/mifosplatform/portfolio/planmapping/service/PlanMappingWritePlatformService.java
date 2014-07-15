package org.mifosplatform.portfolio.planmapping.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface PlanMappingWritePlatformService {

	CommandProcessingResult createProvisioningPlanMapping(JsonCommand command);

	CommandProcessingResult updateProvisioningPlanMapping(Long planMapId, JsonCommand command);
	
}
