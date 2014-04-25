package org.mifosplatform.provisioning.provisioning.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvisioningCommandHandler implements NewCommandSourceHandler {

	 private final ProvisioningWritePlatformService writePlatformService;

	    @Autowired
	    public ProvisioningCommandHandler(final ProvisioningWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	    }
	    
	    @Transactional
		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
	    	return this.writePlatformService.createProvisioning(command);
		}
}
