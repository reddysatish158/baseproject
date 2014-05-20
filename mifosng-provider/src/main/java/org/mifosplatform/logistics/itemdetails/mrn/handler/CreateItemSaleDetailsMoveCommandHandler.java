package org.mifosplatform.logistics.itemdetails.mrn.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.mrn.service.MRNDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateItemSaleDetailsMoveCommandHandler implements NewCommandSourceHandler{

	final private MRNDetailsWritePlatformService mrnDetailsWritePlatformService;
	
	@Autowired
	public CreateItemSaleDetailsMoveCommandHandler(final MRNDetailsWritePlatformService mrnDetailsMoveCommandHandler) {
		this.mrnDetailsWritePlatformService = mrnDetailsMoveCommandHandler;
	}
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return mrnDetailsWritePlatformService.moveItemSale(command);
	}
}
