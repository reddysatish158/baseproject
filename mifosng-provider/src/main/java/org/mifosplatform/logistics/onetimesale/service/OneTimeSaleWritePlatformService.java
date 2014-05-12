package org.mifosplatform.logistics.onetimesale.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.api.JsonQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.item.data.ItemData;

public interface OneTimeSaleWritePlatformService {

	CommandProcessingResult createOneTimeSale(JsonCommand command,Long clientId);

	ItemData calculatePrice(Long itemId, JsonQuery query);

	CommandProcessingResult deleteOneTimeSale(JsonCommand command, Long entityId);

}
