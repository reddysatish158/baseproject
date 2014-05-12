package org.mifosplatform.portfolio.client.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.clientprospect.service.ClientProspectWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.client.service.ClientWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditClientProspectCommandHandler implements NewCommandSourceHandler{
	 private final ClientProspectWritePlatformService clientProspectWritePlatformService;

	    @Autowired
	    public EditClientProspectCommandHandler(final ClientProspectWritePlatformService clientProspectWritePlatformService) {
	        this.clientProspectWritePlatformService = clientProspectWritePlatformService;
	    }

	    @Transactional
	    @Override
	    public CommandProcessingResult processCommand(final JsonCommand command) {

	        return this.clientProspectWritePlatformService.editProspectDetails(command);
	    }
}
