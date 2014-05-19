package org.mifosplatform.organisation.redemption.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.redemption.service.RedemptionWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateRedemptionCommandHandler implements NewCommandSourceHandler {

	private final RedemptionWritePlatformService writePlatformService;
	
	@Autowired
	public CreateRedemptionCommandHandler(RedemptionWritePlatformService writePlatformService){
		this.writePlatformService = writePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.writePlatformService.createRedemption(command);
	}

}
