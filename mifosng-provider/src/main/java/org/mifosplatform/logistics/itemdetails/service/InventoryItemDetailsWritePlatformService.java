package org.mifosplatform.logistics.itemdetails.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsAllocation;

public interface InventoryItemDetailsWritePlatformService {


	CommandProcessingResult addItem(JsonCommand json,Long orderId);
	
	CommandProcessingResult allocateHardware(JsonCommand command);
	
	InventoryItemDetailsAllocation deAllocateHardware(String serialNo,Long clientId);
	
	CommandProcessingResult updateItem(Long itemId,JsonCommand json);
	
	CommandProcessingResult deAllocateHardware(JsonCommand command);
}
