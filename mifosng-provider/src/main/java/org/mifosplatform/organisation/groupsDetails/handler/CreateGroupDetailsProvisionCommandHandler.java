package org.mifosplatform.organisation.groupsDetails.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.groupsDetails.service.GroupsDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGroupDetailsProvisionCommandHandler implements NewCommandSourceHandler{

private final GroupsDetailsWritePlatformService groupsDetailsWritePlatformService;
	
	@Autowired
	public CreateGroupDetailsProvisionCommandHandler(final GroupsDetailsWritePlatformService groupsDetailsWritePlatformService){
		this.groupsDetailsWritePlatformService = groupsDetailsWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.groupsDetailsWritePlatformService.addProvision(command);
	}

}
