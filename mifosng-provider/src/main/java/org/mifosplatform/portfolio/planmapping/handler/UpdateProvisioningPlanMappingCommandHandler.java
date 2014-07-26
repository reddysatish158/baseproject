package org.mifosplatform.portfolio.planmapping.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.planmapping.service.PlanMappingWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProvisioningPlanMappingCommandHandler implements NewCommandSourceHandler {

private final PlanMappingWritePlatformService writePlatformService;
	
	@Autowired
	public UpdateProvisioningPlanMappingCommandHandler(PlanMappingWritePlatformService writePlatformService){
		this.writePlatformService = writePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.writePlatformService.updateProvisioningPlanMapping(command.entityId(),command);
	}

}
