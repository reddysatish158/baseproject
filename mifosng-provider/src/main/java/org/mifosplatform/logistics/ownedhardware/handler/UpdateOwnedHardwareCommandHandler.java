package org.mifosplatform.logistics.ownedhardware.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.ownedhardware.service.OwnedHardwareWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateOwnedHardwareCommandHandler implements NewCommandSourceHandler {

	
	OwnedHardwareWritePlatformService ownedHardwareWritePlatformService;
	
	@Autowired
	public UpdateOwnedHardwareCommandHandler(final OwnedHardwareWritePlatformService ownedHardwareWritePlatformService) {
		this.ownedHardwareWritePlatformService = ownedHardwareWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return ownedHardwareWritePlatformService.updateOwnedHardware(command, command.entityId());
	}

}
