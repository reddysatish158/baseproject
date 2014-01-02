package org.mifosplatform.billing.provisioning.handler;

import org.mifosplatform.billing.provisioning.service.ProvisioningWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteProvisioningCommandHandler implements NewCommandSourceHandler  {

	 private final ProvisioningWritePlatformService writePlatformService;

	    @Autowired
	    public DeleteProvisioningCommandHandler(final ProvisioningWritePlatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	    }

		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
			return this.writePlatformService.deleteProvisioningSystem(command);
		}
}
