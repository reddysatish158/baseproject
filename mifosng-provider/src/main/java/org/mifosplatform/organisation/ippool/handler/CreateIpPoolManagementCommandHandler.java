package org.mifosplatform.organisation.ippool.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.ippool.service.IpPoolManagementWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateIpPoolManagementCommandHandler implements NewCommandSourceHandler {
	
    
	private final IpPoolManagementWritePlatformService ipPoolManagementWritePlatformService;
	
	@Autowired
	public CreateIpPoolManagementCommandHandler(final IpPoolManagementWritePlatformService ipPoolManagementWritePlatformService) {
		this.ipPoolManagementWritePlatformService = ipPoolManagementWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.ipPoolManagementWritePlatformService.createIpPoolManagement(command);
	}

}
