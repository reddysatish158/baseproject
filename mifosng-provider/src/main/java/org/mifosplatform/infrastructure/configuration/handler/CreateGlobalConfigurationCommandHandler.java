package org.mifosplatform.infrastructure.configuration.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.configuration.service.GlobalConfigurationWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateGlobalConfigurationCommandHandler  implements NewCommandSourceHandler {

	private final GlobalConfigurationWritePlatformService writePlatformService;
	  
	  @Autowired
	    public CreateGlobalConfigurationCommandHandler(GlobalConfigurationWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	       
	    }

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return writePlatformService.create(command);
	}
}
