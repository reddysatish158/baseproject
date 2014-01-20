package org.mifosplatform.billing.order.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface OrderWritePlatformService {
	
	CommandProcessingResult createOrder(Long entityId, JsonCommand command);
	CommandProcessingResult updateOrderPrice(Long orderId, JsonCommand command);
	CommandProcessingResult deleteOrder(Long orderId,JsonCommand command);
	CommandProcessingResult renewalClientOrder(JsonCommand command,Long orderId);
	CommandProcessingResult reconnectOrder(Long entityId);
	CommandProcessingResult disconnectOrder(JsonCommand command, Long orderId);
	CommandProcessingResult retrackOsdMessage(JsonCommand command);
	CommandProcessingResult changePlan(JsonCommand command, Long entityId);
	CommandProcessingResult applyPromo(JsonCommand command);
}
