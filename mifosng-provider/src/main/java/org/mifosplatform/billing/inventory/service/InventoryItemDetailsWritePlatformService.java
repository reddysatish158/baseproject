package org.mifosplatform.billing.inventory.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface InventoryItemDetailsWritePlatformService {


	CommandProcessingResult addItem(JsonCommand json,Long orderId);
	
	CommandProcessingResult allocateHardware(JsonCommand command);
}
