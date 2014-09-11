package org.mifosplatform.provisioning.provisioning.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningServiceParamsWriteplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateProvisioningServiceParamsCommandHandler implements NewCommandSourceHandler {

	 private final ProvisioningServiceParamsWriteplatformService writePlatformService;

	    @Autowired
	    public UpdateProvisioningServiceParamsCommandHandler(final ProvisioningServiceParamsWriteplatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	    }

		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
	       return this.writePlatformService.updateServiceParams(command,command.entityId());
		}
}
