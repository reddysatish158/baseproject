package org.mifosplatform.logistics.agent.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ItemSaleWriteplatformService {

	CommandProcessingResult createNewItemSale(JsonCommand command);

}
