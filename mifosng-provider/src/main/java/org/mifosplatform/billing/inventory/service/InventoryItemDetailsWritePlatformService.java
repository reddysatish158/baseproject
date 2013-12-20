package org.mifosplatform.billing.inventory.service;

import org.mifosplatform.billing.inventory.domain.InventoryItemDetailsAllocation;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface InventoryItemDetailsWritePlatformService {


	CommandProcessingResult addItem(JsonCommand json,Long orderId);
	CommandProcessingResult allocateHardware(JsonCommand command);
	InventoryItemDetailsAllocation deAllocateHardware(String serialNo,Long clientId);
}
