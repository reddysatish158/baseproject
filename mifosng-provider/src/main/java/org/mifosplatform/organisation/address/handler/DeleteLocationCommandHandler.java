package org.mifosplatform.organisation.address.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.address.service.AddressWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteLocationCommandHandler implements NewCommandSourceHandler{
	private final AddressWritePlatformService writePlatformService;
	
	@Autowired
	public DeleteLocationCommandHandler(final AddressWritePlatformService writePlatformService){
        this.writePlatformService = writePlatformService;
    }
 @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
		 return this.writePlatformService.deleteLocation(command,command.getSupportedEntityType(),command.entityId());
 	}

}
