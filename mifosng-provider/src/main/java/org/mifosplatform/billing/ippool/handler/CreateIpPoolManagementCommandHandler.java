package org.mifosplatform.billing.ippool.handler;

import org.mifosplatform.billing.ippool.service.IpPoolManagementWritePlatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateIpPoolManagementCommandHandler implements NewCommandSourceHandler {
	
    
	private final IpPoolManagementWritePlatformService ipPoolManagementWritePlatformService;
	
	@Autowired
	public CreateIpPoolManagementCommandHandler(final IpPoolManagementWritePlatformService ipPoolManagementWritePlatformService) {
		this.ipPoolManagementWritePlatformService = ipPoolManagementWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.ipPoolManagementWritePlatformService.createIpPoolManagement(command);
	}

}
