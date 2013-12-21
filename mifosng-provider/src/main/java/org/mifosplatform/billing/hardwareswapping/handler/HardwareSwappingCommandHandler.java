package org.mifosplatform.billing.hardwareswapping.handler;

import org.mifosplatform.billing.association.service.HardwareAssociationWriteplatformService;
import org.mifosplatform.billing.hardwareswapping.service.HardwareSwappingWriteplatformService;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HardwareSwappingCommandHandler  implements NewCommandSourceHandler {

	private final HardwareSwappingWriteplatformService writePlatformService;
	  
	  @Autowired
	    public HardwareSwappingCommandHandler(HardwareSwappingWriteplatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	       
	    }
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return writePlatformService.dohardWareSwapping(command.entityId(),command);
	}

}
