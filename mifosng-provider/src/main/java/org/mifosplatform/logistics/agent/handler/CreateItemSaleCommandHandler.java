package org.mifosplatform.logistics.agent.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.agent.service.AgentWriteplatformService;
import org.mifosplatform.organisation.ippool.service.IpPoolManagementWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateItemSaleCommandHandler implements NewCommandSourceHandler {
	
    
	private final AgentWriteplatformService agentWriteplatformService;
	
	@Autowired
	public CreateItemSaleCommandHandler(final AgentWriteplatformService agentWriteplatformService) {
		this.agentWriteplatformService=agentWriteplatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.agentWriteplatformService.createNewItemSale(command);
	}

}
