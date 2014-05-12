package org.mifosplatform.portfolio.association.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.association.service.HardwareAssociationWriteplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateAssociationCommandhandler implements NewCommandSourceHandler {

	private final HardwareAssociationWriteplatformService writePlatformService;
	  
	  @Autowired
	    public UpdateAssociationCommandhandler(HardwareAssociationWriteplatformService writePlatformService) {
	        this.writePlatformService = writePlatformService;
	       
	    }

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		// TODO Auto-generated method stub
		return this.writePlatformService.updateAssociation(command);
	}
	
}
