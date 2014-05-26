package org.mifosplatform.portfolio.client.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.client.service.ClientCardDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteClientCardDetailsCommandHandler implements NewCommandSourceHandler {

	
	 private final ClientCardDetailsWritePlatformService clientCardDetailsWritePlatformService;

	    @Autowired
	    public DeleteClientCardDetailsCommandHandler(ClientCardDetailsWritePlatformService clientCardDetailsWritePlatformService) {
	        this.clientCardDetailsWritePlatformService = clientCardDetailsWritePlatformService;
	    }

	    @Transactional
		@Override
		public CommandProcessingResult processCommand(JsonCommand command) {
	    	return this.clientCardDetailsWritePlatformService.deleteClientCardDetails(command);
		}
	    
}
