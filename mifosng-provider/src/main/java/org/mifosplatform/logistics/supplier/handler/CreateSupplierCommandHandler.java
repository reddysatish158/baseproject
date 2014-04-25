package org.mifosplatform.logistics.supplier.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.supplier.service.SupplierReadPlatformService;
import org.mifosplatform.logistics.supplier.service.SupplierWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateSupplierCommandHandler implements NewCommandSourceHandler{

	final private SupplierWritePlatformService supplierWritePlatformService;
	
	@Autowired
	public CreateSupplierCommandHandler(final SupplierWritePlatformService supplierWritePlatformService) {
		this.supplierWritePlatformService = supplierWritePlatformService;
	}
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return supplierWritePlatformService.createSupplier(command);
	}
	
	
}
